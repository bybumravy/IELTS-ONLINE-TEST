import { useState, useEffect } from "react";
import { DoTestHeader } from "@/components/layout/doTest/DoTestHeader";
import {useParams} from "react-router-dom";

export interface Question {
    question: string | null;
    answer: string | null;
    options: string[] | null;
    explanation: string | null;
}

export interface Section {
    sectionNumber: number;
    type: string;
    introduction: string;
    questions: Question[];
}

export interface Task {
    taskNumber: number;
    title: string;
    paragraph: string;
    sections: Section[];
}

export interface ReadingTest {
    idReading: string;
    testId: string;
    tasks: Task[];
}
interface QuestionWithStudentAnswer extends Question {
    studentAnswer: string | null;
    questionId: number;
}


export default function ReadingTest() {
    const { testId } = useParams<{ testId: string }>();
    const [currentPart, setCurrentPart] = useState(1);
    const [readingTest, setReadingTest] = useState<ReadingTest | null>(null);
    const [tasks, setTasks] = useState<Task[]>([]);
    const [isSubmitted, setIsSubmitted] = useState(false);
    const currentTask = tasks.find((task) => Number(task.taskNumber) === currentPart) || null;
    const [answers, setAnswers] = useState<Record<number, string>>({});
    useEffect(() => {
        const fetchData = async () => {
            try {
                const res = await fetch(`http://localhost:8080/verify/reading/${testId}`, {
                    credentials: "include",
                });
                const data: ReadingTest = await res.json();

                let questionId = 1;
                const updatedData = {
                    ...data,
                    tasks: data.tasks?.map(task => ({
                        ...task,
                        sections: task.sections?.map(section => ({
                            ...section,
                            questions: section.questions?.map(question => ({
                                ...question,
                                questionId: questionId++,
                                studentAnswer: null
                            })) || []
                        })) || []
                    })) || []
                };

                setReadingTest(updatedData);
                setTasks(updatedData.tasks);
            } catch (err) {
                console.error("Failed to load reading test:", err);
            }
        };

        fetchData();
    }, []);


    // const updatedData = structuredClone(readingData);
    useEffect(() => {
        if (!readingTest || !readingTest.tasks) return;

        const updatedData = structuredClone(readingTest);

        let questionId = 1;
        updatedData?.tasks?.forEach(task => {
            task?.sections?.forEach(section => {
                section?.questions?.forEach(question => {
                    const q = question as QuestionWithStudentAnswer;
                    q.studentAnswer = answers[`q${questionId}`] ?? null;
                    q.questionId = questionId;
                    questionId++;
                });
            });
        });

        setReadingTest(updatedData);
    }, [answers]);


    const handleSubmit = async () => {
        if (!readingTest) return;

        setIsSubmitted(true);
        try {
            const response = await fetch("http://localhost:8080/verify/reading/submit", {
                method: "POST",
                credentials: "include",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    ...readingTest,
                    tasks: readingTest.tasks.map(task => ({
                        ...task,
                        sections: task.sections.map(section => ({
                            ...section,
                            questions: section.questions.map(question => ({
                                ...question,
                                studentAnswer: answers[(question as QuestionWithStudentAnswer).questionId] || null
                            }))
                        }))
                    }))
                }),
            });

            if (!response.ok) throw new Error("Submit failed");
            const result = await response.json();
            console.log("Saved:", result);
            setIsSubmitted(true);
        } catch (error) {
            console.error("Error submitting:", error);
        } finally {
            setIsSubmitted(false);
        }
    };

    const getSectionQuestionRange = (task: Task | null, sectionIndex: number): { start: number; end: number } => {
        if (!task) return { start: 0, end: 0 };

        const section = task.sections[sectionIndex];
        const questionIds = section.questions.map(q => (q as QuestionWithStudentAnswer).questionId).filter(Boolean);

        const start = Math.min(...questionIds);
        const end = Math.max(...questionIds);

        return { start, end };
    };


    const getTotalQuestions = (taskReading: Task[], currentTaskIndex: number): { start: number, end: number } => {
        let totalQuestionsBefore = 0;
        for (let i = 0; i < currentTaskIndex; i++) {
            taskReading[i].sections.forEach(section => {
                totalQuestionsBefore += section.questions.length;
            });
        }
        const currentTask = taskReading[currentTaskIndex];
        const totalQuestionsInCurrent = currentTask.sections.reduce((sum, section) => sum + section.questions.length, 0);
        const start = totalQuestionsBefore + 1;
        const end = totalQuestionsBefore + totalQuestionsInCurrent;
        return { start, end };
    };

    const handleAnswerChange = (questionId: number, answer: string) => {
        setAnswers((prev) => ({
            ...prev,
            [questionId]: answer,
        }));
    };



    return (
        <div className="flex flex-col min-h-screen">
            <div className="sticky top-0 z-50 flex flex-col shadow-sm">
                <DoTestHeader initialTime={3600} onSubmit={handleSubmit} />
            </div>

            <div className="flex flex-1 max-w-7xl mx-auto w-full overflow-hidden">
                {/* LEFT: Paragraph */}
                <div className="w-1/2 p-6 border-r overflow-y-auto h-[calc(100vh-148px)] bg-gray-50">
                    {currentTask && (
                        <>
                            <h1 className="text-2xl font-bold text-blue-900 mb-4">
                                Part {currentTask.taskNumber}: {currentTask.title}
                            </h1>
                            <div className="whitespace-pre-line text-gray-800 leading-relaxed">
                                {currentTask.paragraph}
                            </div>
                        </>
                    )}
                </div>

                {/* RIGHT: Questions */}
                <div className="w-1/2 p-6 overflow-y-auto h-[calc(100vh-148px)]">
                    {currentTask ? (
                        currentTask.sections.map((section, sectionIdx) => (
                            <div key={sectionIdx} className="mb-10">
                                {(() => {
                                    const range = getSectionQuestionRange(currentTask, sectionIdx);
                                    return (
                                        <h2 className="text-xl font-semibold text-teal-600 mb-2">
                                            Question {range.start} - {range.end}
                                        </h2>
                                    );
                                })()}


                                {section.introduction && (
                                    <p className="text-gray-700 italic mb-4">{section.introduction}</p>
                                )}

                                {section.questions.map((question, qIdx) => {
                                    if (!question.question) return null;
                                    const q = question as QuestionWithStudentAnswer;
                                    const questionId = q.questionId!;
                                    const currentAnswer = answers[questionId] || "";

                                    return (
                                        <div key={questionId} className="mb-6">
                                            <p className="text-gray-800 font-medium mb-3">
                                                {qIdx + 1}. {q.question}
                                            </p>

                                            {section.type === "True/False/Not Given" ||
                                            section.type === "Yes/No/Not Given" ? (
                                                <select
                                                    value={currentAnswer}
                                                    onChange={(e) =>
                                                        handleAnswerChange(questionId, e.target.value)
                                                    }
                                                    className="border border-gray-300 rounded p-2 min-w-[150px]"
                                                >
                                                    <option value="">Select</option>
                                                    {q.options?.map((option, optIdx) => (
                                                        <option key={`q${questionId}-opt${optIdx}`} value={option}>
                                                            {option}
                                                        </option>
                                                    ))}
                                                </select>
                                            ) : (
                                                <>
                                                    {q.options && q.options.length > 0 ? (
                                                        <div className="space-y-2 mb-3">
                                                            {q.options.map((option, optIdx) => (
                                                                <div key={optIdx} className="flex items-center">
                                                                    <input
                                                                        type="radio"
                                                                        id={`q${questionId}-opt${optIdx}`}
                                                                        name={`q${questionId}`}
                                                                        value={option}
                                                                        checked={currentAnswer === option}
                                                                        onChange={(e) =>
                                                                            handleAnswerChange(questionId, e.target.value)
                                                                        }
                                                                        className="mr-2"
                                                                    />
                                                                    <label htmlFor={`q${questionId}-opt${optIdx}`}>
                                                                        {option}
                                                                    </label>
                                                                </div>
                                                            ))}
                                                        </div>
                                                    ) : (
                                                        <input
                                                            type="text"
                                                            placeholder="Your answer"
                                                            value={currentAnswer}
                                                            onChange={(e) =>
                                                                handleAnswerChange(questionId, e.target.value)
                                                            }
                                                            className="w-full border border-gray-300 rounded p-2"
                                                        />
                                                    )}
                                                </>
                                            )}
                                        </div>
                                    );
                                })}

                            </div>
                        ))
                    ) : (
                        <p className="text-gray-600">No task available.</p>
                    )}
                </div>
            </div>

            {/* Navigation */}
            {readingTest && readingTest.tasks && (
                <div className="sticky bottom-0 bg-white border-t border-gray-200 p-4">
                    <div className="max-w-7xl mx-auto grid grid-cols-3 gap-4">
                        {readingTest.tasks.map((task) => {
                            const isActive = Number(task.taskNumber) === currentPart;
                            return (
                                <div
                                    key={task.taskNumber}
                                    onClick={() => setCurrentPart(Number(task.taskNumber))}
                                    className={`border rounded-lg p-4 cursor-pointer transition duration-200 text-center ${
                                        isActive
                                            ? "border-teal-500 bg-teal-50 text-teal-700"
                                            : "border-gray-200 bg-white hover:bg-gray-50 text-gray-800"
                                    }`}
                                >
                                    <h3 className="font-semibold text-sm">Part {task.taskNumber}</h3>
                                </div>
                            );
                        })}
                    </div>
                </div>
            )}
        </div>
    );
}
