import { useEffect, useRef, useState } from "react";
import { useAuth } from "@/contexts/AuthContext";
import {useParams} from "react-router-dom";

type Speaking = {
    _id: string;
    part1: {
        partNumber: number;
        title: string;
        instruction: string;
        questions: { question: string }[];
    };
    part2: {
        partNumber: number;
        title: string;
        instruction: string;
        question: string;
        cueCards: string[];
    };
    part3: {
        partNumber: number;
        title: string;
        instruction: string;
        questions: { question: string }[];
    };
};

type Part = "part1" | "part2" | "part3";

const SpeakingTest = () => {
    const { testId } = useParams<{ testId: string }>();
    const [speaking, setSpeaking] = useState<Speaking | null>(null);
    const [loading, setLoading] = useState(true);
    const [currentPart, setCurrentPart] = useState<Part>("part1");
    const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
    const [showTransitionMessage, setShowTransitionMessage] = useState(false);
    const [showConfirmNextPart, setShowConfirmNextPart] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [recordingKey, setRecordingKey] = useState<string | null>(null);
    const [audioUrls, setAudioUrls] = useState<{ [key: string]: string }>({});
    const [isThinking, setIsThinking] = useState(false);
    const [thinkingTime, setThinkingTime] = useState(0);
    const [partTimeLeft, setPartTimeLeft] = useState(0);
    const [partStarted, setPartStarted] = useState(false);

    const timerRef = useRef<NodeJS.Timeout | null>(null);
    const mediaRecorderRef = useRef<MediaRecorder | null>(null);
    const audioChunksRef = useRef<Blob[]>([]);
    const { user } = useAuth();

    useEffect(() => {
        const fetchData = async () => {
            try {
                const res = await fetch(`http://localhost:8080/verify/speaking/${testId}`, {
                    method: "GET",
                    credentials: "include",
                });
                if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);
                const data = await res.json();

                setSpeaking(data);
            } catch (err) {
                console.error("Failed to fetch speaking test:", err);
            } finally {
                setLoading(false);
            }
        };
        fetchData();

        return () => {
            if (timerRef.current) clearInterval(timerRef.current);
        };
    }, [testId]);

    if (loading) return <p>Loading...</p>;
    if (!speaking) return <p>No data found</p>;

    const getInitialTimeForPart = () => {
        switch (currentPart) {
            case "part1":
                return 300 + (speaking.part1.questions.length - 1) * 5;
            case "part2":
                return 150;
            case "part3":
                return 300 + (speaking.part3.questions.length - 1) * 5;
            default:
                return 0;
        }
    };

    const startPartTimer = (seconds: number) => {
        if (timerRef.current !== null) return;
        setPartStarted(true);
        setPartTimeLeft(seconds);
        timerRef.current = setInterval(() => {
            setPartTimeLeft((prev) => {
                if (prev <= 1) {
                    clearInterval(timerRef.current!);
                    timerRef.current = null;
                    setShowTransitionMessage(true);
                    setTimeout(() => goToNextPart(), 2000);
                    return 0;
                }
                return prev - 1;
            });
        }, 1000);
    };

    const startThinkingTime = (seconds: number, callback: () => void) => {
        setThinkingTime(seconds);
        setIsThinking(true);
        const interval = setInterval(() => {
            setThinkingTime((prev) => {
                if (prev <= 1) {
                    clearInterval(interval);
                    setIsThinking(false);
                    callback();
                    return 0;
                }
                return prev - 1;
            });
        }, 1000);
    };

    const startRecording = async (key: string) => {
        if (!partStarted) startPartTimer(getInitialTimeForPart());
        const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
        const mediaRecorder = new MediaRecorder(stream);
        mediaRecorderRef.current = mediaRecorder;
        audioChunksRef.current = [];
        setRecordingKey(key);

        mediaRecorder.ondataavailable = (event: BlobEvent) => audioChunksRef.current.push(event.data);

        mediaRecorder.onstop = () => {
            const audioBlob = new Blob(audioChunksRef.current, { type: "audio/webm" });
            const url = URL.createObjectURL(audioBlob);
            setAudioUrls((prev) => ({ ...prev, [key]: url }));
            setRecordingKey(null);
        };

        mediaRecorder.start();
    };

    const stopRecording = () => mediaRecorderRef.current?.stop();

    const nextQuestion = () => {
        if (!speaking) return;
        const questions = currentPart === "part1" ? speaking.part1.questions : speaking.part3.questions;
        if (currentQuestionIndex < questions.length - 1) setCurrentQuestionIndex(currentQuestionIndex + 1);
        else setShowTransitionMessage(true);
    };

    const goToNextPart = () => {
        if (timerRef.current) clearInterval(timerRef.current);
        timerRef.current = null;
        setShowTransitionMessage(false);
        setPartStarted(false);
        setShowConfirmNextPart(false);

        if (currentPart === "part1") setCurrentPart("part2");
        else if (currentPart === "part2") setCurrentPart("part3");

        setCurrentQuestionIndex(0);
    };

    const renderQuestionPart = (
        questions: { question: string }[],
        partKey: "part1" | "part3"
    ) => {
        const q = questions[currentQuestionIndex];
        const key = `${partKey}-${currentQuestionIndex + 1}`;

        const handleRecord = () => {
            if (recordingKey === key) stopRecording();
            else startThinkingTime(5, () => startRecording(key));
        };

        return (
            <div>
                <p>
                    <strong>{`Câu hỏi ${currentQuestionIndex + 1}:`}</strong> {q.question}
                </p>
                <button onClick={handleRecord} disabled={isThinking}>
                    {recordingKey === key
                        ? "Stop Recording"
                        : isThinking
                            ? `Thinking... (${thinkingTime}s)`
                            : "Start Recording"}
                </button>
                {audioUrls[key] && (
                    <>
                        <audio controls src={audioUrls[key]} style={{ display: "block", marginTop: "0.5rem" }} />
                        <button onClick={nextQuestion}>
                            {currentQuestionIndex < questions.length - 1 ? "Next" : "Kết thúc Part"}
                        </button>
                    </>
                )}
            </div>
        );
    };

    const renderPart2 = () => {
        const key = "part2";

        const handleRecord = () => {
            if (recordingKey === key) stopRecording();
            else startThinkingTime(60, () => startRecording(key));
        };

        return (
            <div>
                <p>
                    <strong>{speaking.part2.question}</strong>
                </p>
                <ul>
                    {speaking.part2.cueCards.map((cue, index) => (
                        <li key={index}>- {cue}</li>
                    ))}
                </ul>
                <button onClick={handleRecord} disabled={isThinking}>
                    {recordingKey === key
                        ? "Stop Recording"
                        : isThinking
                            ? `Thinking... (${thinkingTime}s)`
                            : "Start Recording"}
                </button>
                {audioUrls[key] && (
                    <audio controls src={audioUrls[key]} style={{ display: "block", marginTop: "0.5rem" }} />
                )}
            </div>
        );
    };

    const instruction = speaking[currentPart].instruction;

    const prepareSubmissionData = () => {
        if (!speaking) return null;

        const cloned = JSON.parse(JSON.stringify(speaking));

        if ("username" in user) {
            cloned.username = user.username;
        }

        // 🔹 Part 1
        if (cloned.part1?.questions?.length) {
            cloned.part1.questions = cloned.part1.questions.map((q: any, i: number) => {
                const key = `part1-${i + 1}`;
                return {
                    question: q.question,
                    studentAnswer: audioUrls[key] ? `${key}.webm` : "", // chỉ gán nếu có
                };
            });
        }

        // 🔹 Part 2
        cloned.part2.studentAnswer = audioUrls["part2"] ? "part2.webm" : "";

        // 🔹 Part 3
        if (cloned.part3?.questions?.length) {
            cloned.part3.questions = cloned.part3.questions.map((q: any, i: number) => {
                const key = `part3-${i + 1}`;
                return {
                    question: q.question,
                    studentAnswer: audioUrls[key] ? `${key}.webm` : "",
                };
            });
        }

        return cloned;
    };
    return (
        <div>
            <h1>Speaking Test ID: {speaking._id}</h1>
            <h2>
                Part {speaking[currentPart].partNumber}: {speaking[currentPart].title}
            </h2>

            {instruction && (
                <p style={{ fontStyle: "italic", marginBottom: "1rem" }}>📘 {instruction}</p>
            )}

            <div style={{ marginBottom: "1rem", fontWeight: "bold", color: "darkred" }}>
                ⏱️ Thời gian còn lại:{" "}
                {Math.floor((partStarted ? partTimeLeft : getInitialTimeForPart()) / 60)}:
                {String((partStarted ? partTimeLeft : getInitialTimeForPart()) % 60).padStart(2, "0")}
            </div>

            {showTransitionMessage ? (
                <div>
                    <p>
                        <strong>Bạn đã hoàn thành {currentPart.toUpperCase()}</strong>
                    </p>
                    <button onClick={goToNextPart}>Tiếp tục phần tiếp theo</button>
                </div>
            ) : (
                <section style={{ marginTop: "1rem" }}>
                    {currentPart === "part1" && renderQuestionPart(speaking.part1.questions, "part1")}
                    {currentPart === "part2" && renderPart2()}
                    {currentPart === "part3" && renderQuestionPart(speaking.part3.questions, "part3")}
                </section>
            )}

            {!showTransitionMessage && (
                <div style={{ marginTop: "2rem" }}>
                    {currentPart !== "part3" ? (
                        <button onClick={() => setShowConfirmNextPart(true)}>➡️ Next Part</button>
                    ) : (
                        <button onClick={() => setIsSubmitting(true)}>✅ Submit Test</button>
                    )}
                </div>
            )}

            {showConfirmNextPart && (
                <div
                    style={{
                        background: "#fffae6",
                        padding: "1rem",
                        marginTop: "1rem",
                        border: "1px solid #ccc",
                    }}
                >
                    <p>
                        <strong>Bạn có chắc muốn chuyển sang phần tiếp theo không?</strong>
                    </p>
                    <button onClick={() => goToNextPart()} style={{ marginRight: "1rem" }}>
                        ✅ Có
                    </button>
                    <button onClick={() => setShowConfirmNextPart(false)}>❌ Không</button>
                </div>
            )}

            {isSubmitting && (
                <div
                    style={{
                        background: "#ffe0e0",
                        padding: "1rem",
                        marginTop: "1rem",
                        border: "1px solid #ccc",
                    }}
                >
                    <p>
                        <strong>Bạn có chắc muốn nộp bài không?</strong>
                    </p>
                    <button
                        onClick={async () => {
                            const submissionData = prepareSubmissionData();
                            if (!submissionData) return;

                            const formData = new FormData();

                            // 🔹 Gửi JSON metadata
                            formData.append(
                                "metadata",
                                new Blob([JSON.stringify(submissionData)], { type: "application/json" }),
                                "metadata.json"
                            );
                            // 🔹 Gửi từng file audio (key là filename như: part1-1.webm)
                            await Promise.all(
                                Object.entries(audioUrls).map(async ([key, url]) => {
                                    const blob = await fetch(url).then(res => res.blob());
                                    formData.append("files", blob, `${key}.webm`); // giữ nguyên tên để backend map lại được
                                })
                            );

                            try {
                                const res = await fetch("http://localhost:8080/verify/speaking/submit", {
                                    method: "POST",
                                    body: formData,
                                    credentials: "include",
                                });

                                if (!res.ok) throw new Error("Lỗi khi gửi bài!");
                                alert("✅ Bài đã được nộp!");
                            } catch (err) {
                                console.error(err);
                                alert("❌ Gửi bài thất bại!");
                            }

                            setIsSubmitting(false);
                        }}
                    >
                        ✅ Có
                    </button>
                    <button onClick={() => setIsSubmitting(false)}>❌ Không</button>
                </div>
            )}
        </div>
    );
};

export default SpeakingTest;