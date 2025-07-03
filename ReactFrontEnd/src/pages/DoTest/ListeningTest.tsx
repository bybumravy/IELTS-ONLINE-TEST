import React, { useState, useRef, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { DoTestHeader } from "@/components/layout/doTest/DoTestHeader";
import { Play, Pause, RotateCcw, Volume2 } from "lucide-react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import { useAuth } from "@/contexts/AuthContext";
import {customFetch} from "@/components/sections/customFetch";
export type Question = {
    question: string;
    answer: string;
    options: string[];
    explanation: string;
};

export type Section = {
    sectionNumber: number;
    type: string;
    imageUrl: string;
    introduction: string;
    questions: Question[];
};

export type TaskListening = {
    taskNumber: number;
    title: string;
    audioIntroduction: string;
    sections: Section[];
};

export type ListeningTest = {
    testId: string;
    audioUrl: string;
    tasks: TaskListening[];
    username: string;
    skill: string;
};

interface QuestionWithStudentAnswer extends Question {
    studentAnswer?: string | null;
    questionId?: number;
}

export default function ListeningTest() {
    const { testId } = useParams<{ testId: string }>();
    const containerRef = useRef<HTMLDivElement | null>(null);
    const [currentPart, setCurrentPart] = useState(1);
    const [isPlaying, setIsPlaying] = useState(false);
    const [currentTime, setCurrentTime] = useState(0);
    const [answers, setAnswers] = useState<Record<number, string>>({});
    const [volume, setVolume] = useState(75);
    const [listeningTest, setListeningTest] = useState<ListeningTest | null>(null);
    const [duration, setDuration] = useState(0);
    const [progress, setProgress] = useState(0);
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const { user } = useAuth();
    const [isHighlightMode, setIsHighlightMode] = useState(false);
    const audioRef = useRef<HTMLAudioElement | null>(null);
    const mode = searchParams.get("mode");

    // Khởi tạo dark mode từ localStorage
    const [isDarkMode, setIsDarkMode] = useState(() => {
        return localStorage.getItem("darkMode") === "true";
    });
    const toggleHighlightMode = () => {
        setIsHighlightMode((prev) => !prev);
    };
    useEffect(() => {
        localStorage.setItem("darkMode", isDarkMode ? "true" : "false");
    }, [isDarkMode]);

    const toggleDarkMode = () => {
        setIsDarkMode((prev) => !prev);
    };

    const API_URL = import.meta.env.VITE_API_URL;

    // Fetch dữ liệu test listening
    useEffect(() => {
        if (!testId) return;
        customFetch(`${API_URL}/verify/listening/${testId}`, )
            .then((res) => (res.ok ? res.json() : null))
            .then((data) => {
                if (!data) return;
                let questionId = 1;
                const updated = structuredClone(data);
                updated.tasks.forEach((task) => {
                    task.sections.forEach((section) => {
                        section.questions.forEach((q) => {
                            (q as QuestionWithStudentAnswer).questionId = questionId++;
                        });
                    });
                });
                setListeningTest(updated);
            })
            .catch(console.error);
    }, [testId]);

    // Xử lý sự kiện audio
    useEffect(() => {
        const audio = audioRef.current;
        if (!audio) return;

        const update = () => {
            setCurrentTime(audio.currentTime);
            setProgress((audio.currentTime / audio.duration) * 100);
        };
        const loaded = () => setDuration(audio.duration);

        audio.addEventListener("timeupdate", update);
        audio.addEventListener("loadedmetadata", loaded);

        return () => {
            audio.removeEventListener("timeupdate", update);
            audio.removeEventListener("loadedmetadata", loaded);
        };
    }, [listeningTest]);

    const handleFullscreen = () => {
        if (!containerRef.current) return;

        if (!document.fullscreenElement) {
            containerRef.current!.requestFullscreen()
                .catch((err) => {
                    console.error(`Error attempting to enable fullscreen: ${err.message}`);
                });
        } else {
            document.exitFullscreen()
                .catch((err) => {
                    console.error(`Error attempting to exit fullscreen: ${err.message}`);
                });
        }
    };

    const togglePlayPause = () => {
        const audio = audioRef.current;
        if (!audio) return;

        if (isPlaying) {
            audio.pause();
            setIsPlaying(false);
        } else {
            audio.play()
                .then(() => setIsPlaying(true))
                .catch((err) => {
                    console.error("Không thể phát âm thanh:", err);
                    setIsPlaying(false); // giữ trạng thái đúng nếu lỗi
                });
        }
    };
    const resetAudio = () => {
        const audio = audioRef.current;
        if (audio) {
            audio.currentTime = 0;
            setProgress(0);
            setCurrentTime(0);
            setIsPlaying(false);
        }
    };

    const handleSeek = (e: React.ChangeEvent<HTMLInputElement>) => {
        const audio = audioRef.current;
        const value = +e.target.value;
        if (audio && duration) {
            audio.currentTime = (value / 100) * duration;
            setProgress(value);
        }
    };

    const handleVolumeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const audio = audioRef.current;
        const value = +e.target.value;
        setVolume(value);
        if (audio) audio.volume = value / 100;
    };

    const formatTime = (time: number) => {
        const min = Math.floor(time / 60)
            .toString()
            .padStart(2, "0");
        const sec = Math.floor(time % 60)
            .toString()
            .padStart(2, "0");
        return `${min}:${sec}`;
    };

    const handleAnswerChange = (qid: number, ans: string) => {
        setAnswers((prev) => ({ ...prev, [qid]: ans }));
    };

    const handleSubmit = async () => {
        if (!listeningTest) return;

        const dataToSend = structuredClone(listeningTest);
        if (user?.username) dataToSend.username = user.username;
        dataToSend.skill = "listening";
        delete dataToSend.audioUrl;

        dataToSend.tasks.forEach((task) => {
            delete task.title;
            delete task.audioIntroduction;

            task.sections.forEach((section) => {
                delete section.introduction;
                delete section.imageUrl;

                section.questions.forEach((q) => {
                    const question = q as QuestionWithStudentAnswer;
                    const qid = question.questionId!;
                    question.studentAnswer = answers[qid] || null;

                    delete question.explanation;
                    delete question.options;
                });
            });
        });


        try {
            const res = await customFetch(`${API_URL}/verify/listening/submit`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(dataToSend),
            });

            const result = await res.json();
            console.log(result);
            alert("Submit thành công!");
            if (mode === "fulltest") {
                navigate(`/test/reading/${testId}?mode=${mode}`);
            } else {
                navigate(`/listening-result/${result.id}`);
            }
        } catch (error) {
            console.error(error);
            alert("Lỗi khi submit.");
        }
    };

    const currentTask = listeningTest?.tasks[currentPart - 1];
    if (!listeningTest || !currentTask) return null;

    return (
        <div ref={containerRef} className={isDarkMode ? "dark" : ""}>
            <div
                className="listening-test-container flex flex-col min-h-screen
          bg-white text-gray-900
          dark:bg-[#202124] dark:text-gray-100
          transition-colors duration-300"
            >
                <audio ref={audioRef} src={listeningTest.audioUrl} />
                <div className="sticky top-0 z-50 shadow-sm bg-white dark:bg-[#303134] border-b border-gray-200 dark:border-gray-600">
                    <DoTestHeader
                        initialTime={3600}
                        onSubmit={handleSubmit}
                        isDarkMode={isDarkMode}
                        toggleDarkMode={toggleDarkMode}
                        onFullscreenToggle={handleFullscreen} // truyền hàm fullscreen
                        isHighlightMode={isHighlightMode}
                        toggleHighlightMode={toggleHighlightMode}
                    />
                    <div className="flex items-center gap-3 px-4 py-3">
                        <Button variant="outline" size="icon" onClick={resetAudio}>
                            <RotateCcw className="w-5 h-5" />
                        </Button>
                        <Button
                            variant="outline"
                            size="icon"
                            onClick={togglePlayPause}
                            className="bg-teal-500 text-white hover:bg-teal-600"
                        >
                            <>
                                {isPlaying ? <Pause className="w-5 h-5" /> : <Play className="w-5 h-5" />}
                            </>
                        </Button>
                        <div className="flex-1">
                            <input
                                type="range"
                                min="0"
                                max="100"
                                value={progress}
                                onChange={handleSeek}
                                className="w-full"
                            />
                            <div className="text-xs mt-1">
                                {formatTime(currentTime)} / {formatTime(duration)}
                            </div>
                        </div>
                        <Volume2 className="w-5 h-5" />
                        <input
                            type="range"
                            min="0"
                            max="100"
                            value={volume}
                            onChange={handleVolumeChange}
                            className="w-24"
                        />
                    </div>
                </div>

                <main className="flex-1 p-6 w-full">
                    <h1 className="text-2xl font-bold text-blue-900 mb-6 dark:text-blue-300">
                        Part {currentTask.taskNumber}: {currentTask.title}
                    </h1>
                    {currentTask.sections.map((section, sectionIdx) => (
                        <div key={sectionIdx} className="mb-10">
                            <h2 className="text-xl font-semibold text-teal-600 mb-2 dark:text-teal-300">
                                Questions {sectionIdx + 1}
                            </h2>
                            {section.questions.map((q) => {
                                const question = q as QuestionWithStudentAnswer;
                                const qId = question.questionId!;
                                return (
                                    <div key={qId} className="mb-6">
                                        <p className="mb-2">
                                            {qId}. {question.question}
                                        </p>
                                        {question.options?.length ? (
                                            <div className="space-y-2">
                                                {question.options.map((opt, i) => (
                                                    <label key={i} className="flex items-center gap-2">
                                                        <input
                                                            type="radio"
                                                            name={`q-${qId}`}
                                                            value={opt}
                                                            checked={answers[qId] === opt}
                                                            onChange={() => handleAnswerChange(qId, opt)}
                                                        />
                                                        {opt}
                                                    </label>
                                                ))}
                                            </div>
                                        ) : (
                                            <input
                                                type="text"
                                                value={answers[qId] || ""}
                                                onChange={(e) => handleAnswerChange(qId, e.target.value)}
                                                placeholder="Your answer"
                                                className="w-full border p-2 rounded"
                                            />
                                        )}
                                    </div>
                                );
                            })}
                        </div>
                    ))}
                </main>

                <div className="sticky bottom-0 bg-white dark:bg-[#303134] border-t border-gray-200 dark:border-gray-600 p-4">
                    <div className="grid grid-cols-4 gap-4">
                        {listeningTest.tasks.map((task) => {
                            const isActive = currentPart === task.taskNumber;
                            return (
                                <div
                                    key={task.taskNumber}
                                    onClick={() => setCurrentPart(task.taskNumber)}
                                    className={`border rounded p-4 text-center cursor-pointer transition ${
                                        isActive
                                            ? "border-teal-500 bg-teal-50 dark:bg-teal-900 text-teal-300"
                                            : "border-gray-300 dark:border-gray-600 bg-white dark:bg-[#303134] hover:bg-gray-100 dark:hover:bg-[#3c4043]"
                                    }`}
                                >
                                    Part {task.taskNumber}
                                </div>
                            );
                        })}
                    </div>
                </div>
            </div>
        </div>
    );
}
