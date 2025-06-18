import {useState, useRef, useEffect} from "react"
import { Button } from "@/components/ui/button"
import { DoTestHeader } from "@/components/layout/doTest/DoTestHeader"
import { Play, Pause, RotateCcw, Volume2 } from "lucide-react"
import {useNavigate, useParams} from "react-router-dom";
import {useAuth} from "@/contexts/AuthContext";

type Question = {
    question: string;
    answer: string;
    options: string[];
    explanation: string;
};

type Section = {
    sectionNumber: number;
    type: string;
    imageUrl: string;
    introduction: string;
    questions: Question[];
};

type TaskListening = {
    taskNumber: number;
    title: string;
    audioIntroduction: string;
    sections: Section[];
};

type ListeningTest = {
    testId: string;
    audioUrl: string;
    tasks: TaskListening[];
    username: string; // ✅ Thêm username
    skill: string;    // ✅ Thêm skill
};
const getQuestionNumbers = (taskListening: TaskListening): number[] => {
    const numbers: number[] = [];
    let counter = 1;
    taskListening.sections.forEach((section) => {
        section.questions.forEach(() => {
            numbers.push(counter++);
        });
    });
    return numbers;
};
const getTotalQuestions = (taskListenings: TaskListening[], currentTaskIndex: number): { start: number, end: number } => {
    let totalQuestionsBefore = 0;
    for (let i = 0; i < currentTaskIndex; i++) {
        taskListenings[i].sections.forEach(section => {
            totalQuestionsBefore += section.questions.length;
        });
    }
    const currentTask = taskListenings[currentTaskIndex];
    const totalQuestionsInCurrent = currentTask.sections.reduce((sum, section) => sum + section.questions.length, 0);
    const start = totalQuestionsBefore + 1;
    const end = totalQuestionsBefore + totalQuestionsInCurrent;
    return { start, end };
};
interface QuestionWithStudentAnswer extends Question {
    studentAnswer?: string | null;
    questionId?: number;
}
export default function ListeningTest() {
    const { testId } = useParams<{ testId: string }>();
    const [currentPart, setCurrentPart] = useState(1)
    const [isPlaying, setIsPlaying] = useState(false)
    const [currentTime, setCurrentTime] = useState(0)
    const [answers, setAnswers] = useState<Record<number, string>>({});
    const [volume, setVolume] = useState(75)
    const audioRef = useRef<HTMLAudioElement | null>(null)
    const [listeningTest, setListeningTest] = useState<ListeningTest | null>(null)
    const [sections, setSections] = useState<Section[]>([])
    const [tasks, setTasks] = useState<TaskListening[]>([])
    const [duration, setDuration] = useState(0)
    const [progress, setProgress] = useState(0);
    const navigate = useNavigate();
    const {user} = useAuth()
    useEffect(() => {
        fetch(`http://localhost:8080/verify/listening/${testId}`, {
            credentials: "include",
        })
            .then((res) => {
                if (!res.ok) {
                    if (res.status === 401 || res.status === 403) {
                        return;
                    }
                    throw new Error(`HTTP error! status: ${res.status}`);
                }
                return res.json();
            })
            .then((data) => {
                if (!data) return;

                let questionId = 1;
                const updated = structuredClone(data);

                updated.tasks.forEach((task) => {
                    task.sections.forEach((section) => {
                        section.questions.forEach((question) => {
                            (question as QuestionWithStudentAnswer).questionId = questionId++;
                        });
                    });
                });

                setListeningTest(updated);
                setTasks(updated.tasks);
            })
            .catch((err) => console.error("Failed to load listening test:", err));
    }, []);


    useEffect(() => {
        const audio = audioRef.current;
        if (!audio) return;

        const handleTimeUpdate = () => {
            setCurrentTime(audio.currentTime);
            if (audio.duration) {
                setProgress((audio.currentTime / audio.duration) * 100);
            }
        };

        const handleLoadedMetadata = () => {
            setDuration(audio.duration);
        };

        audio.addEventListener("timeupdate", handleTimeUpdate);
        audio.addEventListener("loadedmetadata", handleLoadedMetadata);

        return () => {
            audio.removeEventListener("timeupdate", handleTimeUpdate);
            audio.removeEventListener("loadedmetadata", handleLoadedMetadata);
        };
    }, [listeningTest]);

    useEffect(() => {
        if (!listeningTest) return;
        const updatedData = structuredClone(listeningTest);

        let questionId = 1;

        updatedData?.tasks.forEach(task => {
            task.sections.forEach(section => {
                section.questions.forEach(question => {
                    const q = question as QuestionWithStudentAnswer;
                    q.studentAnswer = answers[questionId] ?? null;
                    q.questionId = questionId;
                    questionId++;
                });
            });
        });




        setListeningTest(updatedData);
    }, [answers]);

    const currentTaskIndex = currentPart - 1;
    if (!listeningTest) return null;

    const { start, end } = getTotalQuestions(listeningTest.tasks, currentTaskIndex);
    const currentTask = listeningTest.tasks[currentTaskIndex];
    const togglePlayPause = () => {
        const audio = audioRef.current;
        if (!audio) return;

        if (isPlaying) {
            audio.pause();
        } else {
            audio.play();
        }
        setIsPlaying(!isPlaying);
    };


    const resetAudio = () => {
        const audio = audioRef.current;
        if (audio) {
            audio.currentTime = 0;
            setProgress(0);
            setCurrentTime(0);
        }
    };

    const handleSeek = (e) => {
        const audio = audioRef.current;
        const value = e.target.value;
        if (audio && duration) {
            audio.currentTime = (value / 100) * duration;
            setProgress(value);
        }
    };

    const handleVolumeChange = (e) => {
        const audio = audioRef.current;
        const value = e.target.value;
        setVolume(value);
        if (audio) {
            audio.volume = value / 100;
        }
    };

    const formatTime = (time) => {
        const minutes = Math.floor(time / 60)
            .toString()
            .padStart(2, "0");
        const seconds = Math.floor(time % 60)
            .toString()
            .padStart(2, "0");
        return `${minutes}:${seconds}`;
    };
    const handleAnswerChange = (questionId: number, answer: string) => {
        setAnswers(prev => ({
            ...prev,
            [questionId]: answer,
        }));
    };
    const handleSubmit = async () => {
        if (!listeningTest) return;

        const dataToSend = structuredClone(listeningTest);

        // ✅ Thêm username và skill
        if (user?.username) {
            dataToSend.username = user.username;
        }
        dataToSend.skill = "listening";

        // Xóa audioUrl ở cấp test
        delete dataToSend.audioUrl;

        // Xóa các trường không cần trong từng task
        dataToSend.tasks.forEach(task => {
            delete task.title;
            delete task.audioIntroduction;

            task.sections.forEach(section => {
                delete section.introduction;
                delete section.imageUrl;

                section.questions.forEach(q => {
                    delete q.explanation;
                    delete q.options;
                });
            });
        });

        console.log("Dữ liệu sau khi xử lý:", JSON.stringify(dataToSend, null, 2));

        try {
            const res = await fetch("http://localhost:8080/verify/listening/submit", {
                method: "POST",
                credentials: "include",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(dataToSend),
            });

            const result = await res.json();
            console.log("Kết quả nộp bài:", result);
            alert("Submit thành công!");
            navigate("/result");
        } catch (error) {
            console.error("Lỗi khi submit:", error);
            alert("Lỗi khi submit.");
        }
    };
    return (
        <div className="flex flex-col min-h-screen">
            {currentTask && (
                <audio ref={audioRef} src={listeningTest?.audioUrl} />
            )}

            <div className="sticky top-0 z-50 flex flex-col shadow-sm">
                <DoTestHeader initialTime={3600} onSubmit={handleSubmit} />

                <div className="bg-white border-b border-gray-200 px-4 py-3 flex items-center gap-3">
                    <Button
                        variant="outline"
                        size="icon"
                        className="rounded-full w-8 h-8 border-gray-300"
                        onClick={resetAudio}
                    >
                        <RotateCcw className="w-4 h-4" />
                    </Button>

                    <Button
                        variant="outline"
                        size="icon"
                        className="rounded-full w-10 h-10 border-gray-300 bg-teal-500 text-white hover:bg-teal-600"
                        onClick={togglePlayPause}
                    >
                        {isPlaying ? <Pause className="w-5 h-5" /> : <Play className="w-5 h-5" />}
                    </Button>

                    <div className="flex-1">
                        <input
                            type="range"
                            min="0"
                            max="100"
                            value={progress}
                            onChange={handleSeek}
                            className="w-full h-1 bg-gray-200 rounded-lg appearance-none cursor-pointer"
                        />
                        <span className="text-sm text-gray-600">
                             {formatTime(currentTime)} / {formatTime(duration)}
                        </span>
                    </div>

                    <div className="flex items-center gap-2">

                        <Volume2 className="w-5 h-5 text-gray-500" />
                        <input
                            type="range"
                            min="0"
                            max="100"
                            value={volume}
                            onChange={handleVolumeChange}
                            className="w-24 h-1 bg-gray-200 rounded-lg appearance-none cursor-pointer"

                        />

                    </div>
                </div>
            </div>
            {/* Content chính */}
            <div className="flex-1 p-6 max-w-7xl mx-auto w-full">
                {currentTask ? (
                    <>
                        <h1 className="text-2xl font-bold text-blue-900 mb-6">
                            Part {currentTask.taskNumber}: {currentTask.title}
                        </h1>

                        {currentTask.sections.map((section, sectionIdx) => (

                            <div key={sectionIdx} className="mb-10">
                                {(() => {
                                    const { start } = getTotalQuestions(tasks, currentTaskIndex); // dùng tasks từ state
                                    let startNumber = start;

                                    // Tính tổng số câu hỏi từ các section trước section hiện tại
                                    for (let i = 0; i < sectionIdx; i++) {
                                        startNumber += currentTask.sections[i].questions.length;
                                    }

                                    const endNumber = startNumber + section.questions.length - 1;

                                    return (
                                        <h2 className="text-xl font-semibold text-teal-600 mb-2">
                                            Questions {startNumber}–{endNumber}
                                        </h2>
                                    );
                                })()}

                                {section.introduction && (
                                    <p className="text-gray-700 italic mb-4">{section.introduction}</p>
                                )}

                                {section.imageUrl && (
                                    <img
                                        src={section.imageUrl}
                                        alt="Listening illustration"
                                        className="w-full h-auto rounded mb-4"
                                    />
                                )}

                                {Array.isArray(section.questions) &&
                                    section.questions.map((question, qIdx) => {

                                        const q = question as QuestionWithStudentAnswer;
                                        const qId = q.questionId!; // vì bạn đã gán trong useEffect

                                        return (
                                            <div key={qId} className="mb-6">
                                                {section.type === 'dropdown' ? (
                                                    <div className="flex items-center gap-3 mb-3">
                                                        <p className="text-gray-800 font-medium">
                                                            {qId}. {q.question}
                                                        </p>
                                                        <select
                                                            value={answers[qId] || ""}
                                                            onChange={(e) => handleAnswerChange(qId, e.target.value)}
                                                            className="border border-gray-300 rounded p-2 min-w-[150px]"
                                                        >
                                                            <option value="">Select</option>
                                                            {q.options.map((option, optIdx) => (
                                                                <option key={optIdx} value={option}>
                                                                    {option}
                                                                </option>
                                                            ))}
                                                        </select>
                                                    </div>
                                                ) : (
                                                    <>
                                                        <p className="text-gray-800 font-medium mb-3">
                                                            {qId}. {q.question}
                                                        </p>

                                                        {q.options?.length > 0 ? (
                                                            <div className="space-y-2 mb-3">
                                                                {q.options.map((option, optIdx) => (
                                                                    <div key={optIdx} className="flex items-center">
                                                                        <input
                                                                            type="radio"
                                                                            id={`q${qId}-opt${optIdx}`}
                                                                            name={`question-${qId}`}
                                                                            value={option}
                                                                            checked={answers[qId] === option}
                                                                            onChange={() => handleAnswerChange(qId, option)}
                                                                            className="mr-2"
                                                                        />
                                                                        <label htmlFor={`q${qId}-opt${optIdx}`}>{option}</label>
                                                                    </div>
                                                                ))}
                                                            </div>
                                                        ) : (
                                                            <input
                                                                type="text"
                                                                value={answers[qId] || ""}
                                                                onChange={(e) => handleAnswerChange(qId, e.target.value)}
                                                                placeholder="Your answer"
                                                                className="w-full border border-gray-300 rounded p-2"
                                                            />
                                                        )}
                                                    </>
                                                )}
                                            </div>
                                        );
                                    })}

                            </div>
                        ))}
                    </>
                ) : (
                    <p className="text-gray-600">No task available.</p>
                )}
            </div>


            {/* Thanh chuyển hướng */}
            {listeningTest && listeningTest.tasks && (
                <div className="sticky bottom-0 bg-white border-t border-gray-200 p-4">
                    <div className="max-w-7xl mx-auto grid grid-cols-4 gap-4">
                        {listeningTest.tasks.map((task) => {
                            const isActive = currentPart === task.taskNumber
                            return (
                                <div
                                    key={task.taskNumber}
                                    onClick={() => setCurrentPart(task.taskNumber)}
                                    className={`border rounded-lg p-4 cursor-pointer transition duration-200 text-center ${
                                        isActive
                                            ? "border-teal-500 bg-teal-50 text-teal-700"
                                            : "border-gray-200 bg-white hover:bg-gray-50 text-gray-800"
                                    }`}
                                >
                                    <h3 className="font-semibold text-sm">Part {task.taskNumber}</h3>
                                </div>
                            )
                        })}
                    </div>
                </div>
            )}

        </div>
    )
}