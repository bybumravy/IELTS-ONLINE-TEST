import { useState, useEffect, useRef } from "react";
import { DoTestHeader } from "@/components/layout/doTest/DoTestHeader";
import {useParams, useNavigate} from "react-router-dom";
import {useAuth} from "@/contexts/AuthContext";
import { PenLine, Eraser } from "lucide-react";

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
    username: string; // ✅ Thêm username
    skill: string;
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
    const {user} = useAuth()
    const [isHighlightMode, setIsHighlightMode] = useState(false);
    const [highlightedParagraph, setHighlightedParagraph] = useState<string | null>(null);
    const paragraphRef = useRef<HTMLDivElement>(null);
    const [isEraserMode, setIsEraserMode] = useState(false);
    const initialTime = 3600;
    const navigate = useNavigate();

    // Lấy thời gian còn lại từ localStorage hoặc tính toán lại
    function getInitialTimeRemaining() {
        if (!testId) return initialTime;
        const key = `reading-startTime-${testId}`;
        let startTime = localStorage.getItem(key);
        if (!startTime) {
            startTime = Date.now().toString();
            localStorage.setItem(key, startTime);
        }
        const elapsed = Math.floor((Date.now() - Number(startTime)) / 1000);
        return Math.max(initialTime - elapsed, 0);
    }
    const [timeRemaining, setTimeRemaining] = useState(getInitialTimeRemaining());

    const pastelColors = [
        { name: "Vàng", value: "#FFF9B1" },
        { name: "Xanh Mint", value: "#B1FFF6" },
        { name: "Hồng Nhạt", value: "#FFD1E3" },
        { name: "Tím Nhạt", value: "#E1D1FF" },
        { name: "Xanh Nhạt", value: "#D1F0FF" },
    ];
    const [highlightColor, setHighlightColor] = useState(pastelColors[0].value);

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

    // Load autosave khi vào trang
    useEffect(() => {
        if (!testId) return;
        const saved = localStorage.getItem(`reading-autosave-${testId}`);
        if (saved) {
            try {
                const parsed = JSON.parse(saved);
                if (parsed.answers) setAnswers(parsed.answers);
                if (parsed.highlightedParagraph) setHighlightedParagraph(parsed.highlightedParagraph);
            } catch {}
        }
    }, [testId]);

    // Autosave khi answers hoặc highlight thay đổi
    useEffect(() => {
        if (!testId) return;
        const data = JSON.stringify({ answers, highlightedParagraph });
        localStorage.setItem(`reading-autosave-${testId}`, data);
    }, [answers, highlightedParagraph, testId]);

    // Khi vào trang hoặc testId đổi, cập nhật lại timeRemaining
    useEffect(() => {
        setTimeRemaining(getInitialTimeRemaining());
        if (getInitialTimeRemaining() === 0) handleSubmit();
    }, [testId]);

    // Đếm ngược thời gian
    useEffect(() => {
        if (timeRemaining <= 0) return;
        const timer = setInterval(() => {
            setTimeRemaining((prev) => {
                if (prev <= 1) {
                    clearInterval(timer);
                    handleSubmit();
                    return 0;
                }
                return prev - 1;
            });
        }, 1000);
        return () => clearInterval(timer);
    }, [timeRemaining]);

    const handleSubmit = async () => {
        if (!readingTest) return;

        setIsSubmitted(true);

        try {
            const dataToSend = {
                ...readingTest,
                username: user?.username || null,
                skill: "reading",
                tasks: readingTest.tasks.map(task => {
                    const { title, ...restTask } = task;
                    return {
                        ...restTask,
                        sections: task.sections.map(section => {
                            const { paragraph, ...restSection } = section;
                            return {
                                ...restSection,
                                questions: section.questions.map(question => ({
                                    ...question,
                                    studentAnswer: answers[(question as QuestionWithStudentAnswer).questionId] || null
                                }))
                            };
                        })
                    };
                })
            };

            const response = await fetch("http://localhost:8080/verify/reading/submit", {
                method: "POST",
                credentials: "include",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(dataToSend),
            });

            if (!response.ok) throw new Error("Gửi bài thất bại");
            localStorage.removeItem(`reading-autosave-${testId}`);
            localStorage.removeItem(`reading-startTime-${testId}`);
            const result = await response.json();
            console.log("Đã lưu:", result);
            navigate("/result");
            alert("🎉 Nộp bài thành công!");
        } catch (error) {
            console.error("Lỗi khi nộp bài:", error);
            alert("❌ Có lỗi xảy ra khi nộp bài.");
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

    // Highlight handler mới: chèn <mark> vào selection HTML với màu đã chọn
    const handleHighlight = () => {
        if (!isHighlightMode || isEraserMode || !paragraphRef.current) return;
        const selection = window.getSelection();
        if (!selection || selection.rangeCount === 0) return;
        const range = selection.getRangeAt(0);
        if (!paragraphRef.current.contains(range.commonAncestorContainer)) return;
        if (range.collapsed) return;

        // Tạo thẻ <mark> bọc quanh selection, dùng màu đã chọn
        const mark = document.createElement("mark");
        mark.style.background = highlightColor;
        mark.appendChild(range.extractContents());
        range.insertNode(mark);
        selection.removeAllRanges();

        // Lưu lại HTML mới
        setHighlightedParagraph(paragraphRef.current.innerHTML);
    };

    // Eraser handler: click vào <mark> sẽ xóa highlight đó
    useEffect(() => {
        if (!isEraserMode || !paragraphRef.current) return;
        const handler = (e: MouseEvent) => {
            const target = e.target as HTMLElement;
            if (target.tagName === "MARK") {
                const parent = target.parentNode;
                if (!parent) return;
                // Thay thế <mark> bằng text con
                while (target.firstChild) {
                    parent.insertBefore(target.firstChild, target);
                }
                parent.removeChild(target);
                setHighlightedParagraph(paragraphRef.current!.innerHTML);
            }
        };
        paragraphRef.current.addEventListener("click", handler);
        return () => paragraphRef.current?.removeEventListener("click", handler);
    }, [isEraserMode]);

    useEffect(() => {
        if (!isHighlightMode || isEraserMode) return;
        const handler = () => handleHighlight();
        document.addEventListener("mouseup", handler);
        return () => document.removeEventListener("mouseup", handler);
    }, [isHighlightMode, isEraserMode, highlightColor]);

    return (
        <div className="flex flex-col min-h-screen">
            <div className="sticky top-0 z-50 flex flex-col shadow-sm">
                <DoTestHeader
                    initialTime={timeRemaining}
                    onSubmit={handleSubmit}
                    extraActions={
                        <div className="flex items-center gap-2 ml-4">
                            {/* Nút Highlight */}
                            <button
                                className={`relative flex items-center justify-center w-10 h-10 rounded-full border transition shadow-sm group
                                    ${isHighlightMode ? "bg-yellow-100 border-yellow-400" : "bg-white border-gray-300 hover:bg-yellow-50"}`}
                                onClick={() => { setIsHighlightMode((prev) => !prev); setIsEraserMode(false); }}
                                title="Highlight"
                            >
                                <PenLine className={`w-5 h-5 ${isHighlightMode ? "text-yellow-600" : "text-gray-500 group-hover:text-yellow-600"}`} />
                            </button>
                            {/* Nút Eraser */}
                            <button
                                className={`relative flex items-center justify-center w-10 h-10 rounded-full border transition shadow-sm group
                                    ${isEraserMode ? "bg-pink-100 border-pink-400" : "bg-white border-gray-300 hover:bg-pink-50"}`}
                                onClick={() => { setIsEraserMode((prev) => !prev); setIsHighlightMode(false); }}
                                title="Eraser"
                            >
                                <Eraser className={`w-5 h-5 ${isEraserMode ? "text-pink-600" : "text-gray-500 group-hover:text-pink-600"}`} />
                            </button>
                            {/* Chọn màu pastel - chỉ hiện khi bật highlight */}
                            {isHighlightMode && (
                                <div className="flex items-center gap-1 ml-2 animate-fade-in">
                                    {pastelColors.map((color) => (
                                        <button
                                            key={color.value}
                                            className={`w-7 h-7 rounded-full border-2 transition shadow-sm
                                                ${highlightColor === color.value ? "border-black scale-110" : "border-gray-300"}`}
                                            style={{ background: color.value }}
                                            onClick={() => setHighlightColor(color.value)}
                                            aria-label={color.name}
                                            title={color.name}
                                        />
                                    ))}
                                </div>
                            )}
                        </div>
                    }
                />
            </div>

            <div className="flex flex-1 max-w-7xl mx-auto w-full overflow-hidden">
                {/* LEFT: Paragraph */}
                <div className="w-1/2 p-6 border-r overflow-y-auto h-[calc(100vh-148px)] bg-gray-50">
                    {currentTask && (
                        <>
                            <h1 className="text-2xl font-bold text-blue-900 mb-4">
                                Part {currentTask.taskNumber}: {currentTask.title}
                            </h1>
                            <div
                                ref={paragraphRef}
                                className="whitespace-pre-line text-gray-800 leading-relaxed"
                                style={{ cursor: isHighlightMode ? "text" : "auto" }}
                                dangerouslySetInnerHTML={{ __html: highlightedParagraph ?? currentTask.paragraph }}
                            />
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
                    <div className="max-w-7xl mx-auto flex flex-row items-center justify-between gap-4">
                        {/* Navigation buttons */}
                        <div className="grid grid-cols-3 gap-4 flex-1">
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
                </div>
            )}
        </div>
    );
}