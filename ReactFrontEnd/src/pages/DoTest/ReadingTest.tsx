import { useState, useEffect, useRef } from "react";
import { DoTestHeader } from "@/components/layout/doTest/DoTestHeader";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import { useAuth } from "@/contexts/AuthContext";
import {customFetch} from "@/components/sections/customFetch";
export interface Question {
    question: string | null;
    answer: string | null;
    options: string[] | null;
    explanation: string | null;
}

export interface Section {
    imageUrl?: string; // sửa lại từ String -> string
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
    submittedAt: any;
    idReading: string;
    testId: string;
    tasks: Task[];
    username: string;
    skill: string;

}

interface QuestionWithStudentAnswer extends Question {
    studentAnswer?: string | null;
    questionId?: number;
}

export default function ReadingTest() {
    const { testId } = useParams<{ testId: string }>();
    const [searchParams] = useSearchParams();
    const testAnswerId = searchParams.get("testAnswerId");
    const mode = searchParams.get("mode");
    const [currentPart, setCurrentPart] = useState(1);
    const [readingTest, setReadingTest] = useState<ReadingTest | null>(null);
    const [tasks, setTasks] = useState<Task[]>([]);
    const [answers, setAnswers] = useState<Record<number, string>>({});
    const [_isSubmitted, setIsSubmitted] = useState(false);
    const [popupPosition, setPopupPosition] = useState<{ x: number; y: number } | null>(null);
    const { user } = useAuth();
    const navigate = useNavigate();

    const containerRef = useRef<HTMLDivElement>(null);
    const paragraphRef = useRef<HTMLDivElement>(null);

    const [isDarkMode, setIsDarkMode] = useState(() => localStorage.getItem("darkMode") === "true");
    

    const currentTask = tasks.find((task) => Number(task.taskNumber) === currentPart) || null;

    const toggleDarkMode = () => setIsDarkMode((prev) => !prev);


    useEffect(() => {
        localStorage.setItem("darkMode", isDarkMode ? "true" : "false");
    }, [isDarkMode]);

    const API_URL = import.meta.env.VITE_API_URL;

    useEffect(() => {
        const fetchData = async () => {
            try {
                const res = await customFetch(`${API_URL}/verify/reading/${testId}`);
                const data: ReadingTest = await res.json();

                let questionId = 1;
                const updatedData = {
                    ...data,
                    tasks: data.tasks.map((task) => ({
                        ...task,
                        sections: task.sections.map((section) => ({
                            ...section,
                            questions: section.questions.map((question) => ({
                                ...question,
                                questionId: questionId++,
                                studentAnswer: null,
                            })),
                        })),
                    })),
                };

                setReadingTest(updatedData);

                setTasks(updatedData.tasks);

            } catch (err) {
                console.error("Failed to load reading test:", err);
            }
        };

        fetchData();
    }, [testId]);

    useEffect(() => {
        if (!readingTest) return;
        const updatedData = structuredClone(readingTest);

        updatedData.tasks.forEach((task) => {
            task.sections.forEach((section) => {
                section.questions.forEach((question) => {
                    const q = question as QuestionWithStudentAnswer;
                    if (typeof q.questionId === "number") {
                        q.studentAnswer = answers[q.questionId] ?? null;
                    } else {
                        q.studentAnswer = null;
                    }
                });
            });
        });

        setReadingTest(updatedData);
    }, [answers]);

    const handleFullscreen = () => {
        if (!containerRef.current) return;
        if (!document.fullscreenElement) {
            containerRef.current?.requestFullscreen().catch((err) => console.error(err));
        } else {
            document.exitFullscreen();
        }
    };

    const handleSubmit = async () => {
        if (!readingTest) return;

        const dataToSend = structuredClone(readingTest); // clone gốc để không thay đổi state
        if (user?.username) dataToSend.username = user.username;
        dataToSend.skill = "reading";
        dataToSend.submittedAt = new Date().toISOString();
        dataToSend.tasks.forEach((task) => {
            delete (task as any).title;

            task.sections.forEach((section) => {
                delete (section as any).introduction;
                delete (section as any).imageUrl;

                section.questions.forEach((q) => {
                    const question = q as QuestionWithStudentAnswer;
                    question.studentAnswer = question.studentAnswer || null;
                    const qid = question.questionId;
                    if (typeof qid === "number") {
                        question.studentAnswer = answers[qid] || null;
                    } else {
                        question.studentAnswer = null;
                    }
                    delete (question as any).explanation;
                    delete (question as any).options;
                });
            });
        });


        // ✅ In ra dữ liệu JSON để kiểm tra trước khi submit
        console.log("✅ Data to be submitted:");
        console.log(JSON.stringify(dataToSend, null, 2));

        setIsSubmitted(true);

        try {
            const dataToSendFinal = {
                ...readingTest,
                username: user?.username || readingTest.username || "",
                skill: "reading",
        
                tasks: readingTest.tasks.map((task) => ({
                    ...task,
                    sections: task.sections.map((section) => ({
                        ...section,
                        questions: section.questions.map((question) => ({
                            ...question,
                            studentAnswer: (question as QuestionWithStudentAnswer).studentAnswer || null,
                        })),
                    })),
                })),
            };
            let response;
            if(testAnswerId != null) {
                response = await fetch(`${API_URL}/verify/reading/submit?testAnswerId=${testAnswerId}`, {
                    method: "POST",
                    credentials: "include",
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify(dataToSendFinal),
                });
            }
            else {
                response = await fetch(`${API_URL}/verify/reading/submit`, {
                    method: "POST",
                    credentials: "include",
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify(dataToSendFinal),
                });
            }
            if (!response.ok) throw new Error("Submit failed");
            const result = await response.json();
            console.log("Saved:", result);
            alert("Submitted successfully!");
            if (mode === "fulltest") {
                navigate(`/test/writing/${testId}?testAnswerId=${testAnswerId}&mode=fulltest`);
            } else {
                navigate(`/reading-result/${result.id}`);
            }
        } catch (error) {
            console.error(error);
            alert("Error submitting");
        } finally {
            setIsSubmitted(false);
        }
    };



    const handleAnswerChange = (questionId: number, answer: string) => {
        setAnswers((prev) => ({ ...prev, [questionId]: answer }));
    };

    const getSectionQuestionRange = (task: Task | null, sectionIndex: number) => {
        if (!task) return { start: 0, end: 0 };
        const questionIds = task.sections[sectionIndex].questions
            .map((q) => (q as QuestionWithStudentAnswer).questionId)
            .filter((id): id is number => typeof id === "number");
        if (questionIds.length === 0) return { start: 0, end: 0 };
        return { start: Math.min(...questionIds), end: Math.max(...questionIds) };
    };

    // Removed handleTextSelection, normalizeRange, and applyHighlight functions


    return (
        <div ref={containerRef} className={isDarkMode ? "dark" : ""}>
            <div className="flex flex-col min-h-screen bg-white text-gray-900 dark:bg-[#202124] dark:text-gray-100 transition-colors">
                <div className="sticky top-0 z-50 flex flex-col shadow-sm">
                    <DoTestHeader
                        initialTime={3600}
                        onSubmit={handleSubmit}
                        isDarkMode={isDarkMode}
                        toggleDarkMode={toggleDarkMode}
                        onFullscreenToggle={handleFullscreen}
                        // Removed highlight mode props
                        // isHighlightMode={isHighlightMode}
                        // toggleHighlightMode={toggleHighlightMode}
                    />
                </div>

                <div className="flex flex-1 max-w-7xl mx-auto w-full overflow-hidden">
                    {/* LEFT: Paragraph */}
                    <div
                        ref={paragraphRef}
                        className="w-1/2 p-6 border-r overflow-y-auto h-[calc(100vh-148px)] bg-gray-50 dark:bg-[#303134]"
                        // Removed onMouseUp handler for highlight
                        // onMouseUp={handleTextSelection}
                    >
                        {currentTask && (
                            <>
                                <h1 className="text-2xl font-bold text-blue-900 mb-4 dark:text-blue-300">
                                    Part {currentTask.taskNumber}: {currentTask.title}
                                </h1>
                                <div
                                    className={`whitespace-pre-line text-gray-800 leading-relaxed dark:text-gray-300`}
                                >
                                    {currentTask.paragraph}
                                </div>
                                {/* Removed color picker popup */}
                            </>
                        )}
                    </div>

                    {/* RIGHT: Questions */}
                    <div className="w-1/2 p-6 overflow-y-auto h-[calc(100vh-148px)] bg-white dark:bg-[#202124]">
                        {currentTask ? (
                            currentTask.sections.map((section, sectionIdx) => (
                                <div key={sectionIdx} className="mb-10">
                                    {(() => {
                                        const range = getSectionQuestionRange(currentTask, sectionIdx);
                                        return (
                                            <h2 className="text-xl font-semibold text-teal-600 mb-2 dark:text-teal-300">
                                                Question {range.start} - {range.end}
                                            </h2>
                                        );
                                    })()}

                                    {section.introduction && (
                                        <p className="text-gray-700 italic mb-4 dark:text-gray-400">{section.introduction}</p>
                                    )}
                                    {section.imageUrl && (
                                        <img
                                            src={section.imageUrl}
                                            alt="Section related"
                                            className="my-2 rounded-md max-w-full md:max-w-md"
                                        />
                                    )}

                                    {section.questions.map((question, _qIdx) => {
                                        if (!question.question) return null;
                                        const q = question as QuestionWithStudentAnswer;
                                        const questionId = q.questionId!;
                                        const currentAnswer = answers[questionId] || "";

                                        // Tính số thứ tự câu hỏi đúng tổng thể
                                        let questionNumber = questionId;

                                        return (
                                            <div key={questionId} className="mb-6">
                                                <p className="text-gray-800 font-medium mb-3 dark:text-gray-200">
                                                    {questionNumber}. {q.question}
                                                </p>
                                                {/* Đã loại bỏ hiển thị ảnh ở từng câu hỏi */}
                                                {section.type === "True/False/Not Given" || section.type === "Yes/No/Not Given"  || section.type === "map-labeling" || section.type === "dropdown" || section.type === "matching-heading" ? (
                                                    <select
                                                        value={currentAnswer}
                                                        onChange={(e) => handleAnswerChange(questionId, e.target.value)}
                                                        className="border border-gray-300 rounded p-2 min-w-[150px] dark:bg-[#202124] dark:border-gray-600"
                                                    >
                                                        <option value="">Select</option>
                                                        {q.options?.map((option, optIdx) => {
                                                            const answerKey = option.split(".")[0].trim();
                                                            return (
                                                                <option key={optIdx} value={answerKey}>
                                                                    {option}
                                                                </option>
                                                            );
                                                        })}
                                                    </select>
                                                ) : q.options?.length ? (
                                                    <div className="space-y-2 mb-3">
                                                        {q.options && q.options.map((option, optIdx) => {
                                                            const answerKey = option.split(".")[0].trim();
                                                            return (
                                                                <div key={optIdx} className="flex items-center">
                                                                    <input
                                                                        type="radio"
                                                                        id={`q${questionId}-opt${optIdx}`}
                                                                        name={`q${questionId}`}
                                                                        value={answerKey}
                                                                        checked={currentAnswer === answerKey}
                                                                        onChange={(e) => handleAnswerChange(questionId, e.target.value)}
                                                                        className="mr-2"
                                                                    />
                                                                    <label htmlFor={`q${questionId}-opt${optIdx}`}>{option}</label>
                                                                </div>
                                                            );
                                                        })}
                                                    </div>
                                                ) : (
                                                    <input
                                                        type="text"
                                                        placeholder="Your answer"
                                                        value={currentAnswer}
                                                        onChange={(e) => handleAnswerChange(questionId, e.target.value)}
                                                        className="w-full border border-gray-300 rounded p-2 dark:bg-[#202124] dark:border-gray-600"
                                                    />
                                                )}
                                            </div>
                                        );
                                    })}
                                </div>
                            ))
                        ) : (
                            <p className="text-gray-600 dark:text-gray-400">No task available.</p>
                        )}
                    </div>
                </div>

                {/* Navigation */}
                {readingTest && readingTest.tasks && (
                    <div className="sticky bottom-0 bg-white dark:bg-[#303134] border-t border-gray-200 dark:border-gray-600 p-4">
                        <div className="max-w-7xl mx-auto grid grid-cols-3 gap-4">
                            {readingTest.tasks.map((task) => {
                                const isActive = Number(task.taskNumber) === currentPart;
                                return (
                                    <div
                                        key={task.taskNumber}
                                        onClick={() => setCurrentPart(Number(task.taskNumber))}
                                        className={`border rounded-lg p-4 cursor-pointer transition ${
                                            isActive
                                                ? "border-teal-500 bg-teal-50 text-teal-700 dark:bg-teal-900"
                                                : "border-gray-200 bg-white hover:bg-gray-50 dark:bg-[#202124] dark:hover:bg-[#3c4043]"
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
        </div>
    );
}