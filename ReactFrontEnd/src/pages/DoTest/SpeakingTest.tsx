import { useEffect, useRef, useState } from "react";
import { useAuth } from "@/contexts/AuthContext";
import { useParams } from "react-router-dom";

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
    const { user } = useAuth();

    const [speaking, setSpeaking] = useState<Speaking | null>(null);
    const [loading, setLoading] = useState(true);
    const [currentPart, setCurrentPart] = useState<Part>("part1");
    const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
    const [audioUrls, setAudioUrls] = useState<{ [key: string]: string }>({});
    const [recordingKey, setRecordingKey] = useState<string | null>(null);
    const [isThinking, setIsThinking] = useState(false);
    const [thinkingTime, setThinkingTime] = useState(0);
    const [partTimeLeft, setPartTimeLeft] = useState(0);
    const [partStarted, setPartStarted] = useState(false);
    const [showTransition, setShowTransition] = useState(false);
    const [showConfirmNextPart, setShowConfirmNextPart] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const timerRef = useRef<NodeJS.Timeout | null>(null);
    const mediaRecorderRef = useRef<MediaRecorder | null>(null);
    const audioChunksRef = useRef<Blob[]>([]);

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

    const getInitialTime = () => {
        if (!speaking) return 0;
        const questions = currentPart === "part1" ? speaking.part1.questions : currentPart === "part3" ? speaking.part3.questions : [];
        if (currentPart === "part2") return 150;
        return 300 + (questions.length - 1) * 5;
    };

    const startTimer = (seconds: number) => {
        if (timerRef.current) return;
        setPartStarted(true);
        setPartTimeLeft(seconds);
        timerRef.current = setInterval(() => {
            setPartTimeLeft(prev => {
                if (prev <= 1) {
                    clearInterval(timerRef.current!);
                    timerRef.current = null;
                    setShowTransition(true);

                    if (currentPart === "part3") {
                        setIsSubmitting(true);  // Nếu là part3 thì mở Submit
                    } else {
                        setTimeout(goToNextPart, 2000);  // part1, part2 bình thường
                    }

                    return 0;
                }
                return prev - 1;
            });
        }, 1000);
    };

    const startThinking = (seconds: number, callback: () => void) => {
        setThinkingTime(seconds);
        setIsThinking(true);
        const interval = setInterval(() => {
            setThinkingTime(prev => {
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
        if (!partStarted) startTimer(getInitialTime());
        const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
        const mediaRecorder = new MediaRecorder(stream);
        mediaRecorderRef.current = mediaRecorder;
        audioChunksRef.current = [];
        setRecordingKey(key);

        mediaRecorder.ondataavailable = (e: BlobEvent) => audioChunksRef.current.push(e.data);
        mediaRecorder.onstop = () => {
            const audioBlob = new Blob(audioChunksRef.current, { type: "audio/webm" });
            const url = URL.createObjectURL(audioBlob);
            setAudioUrls(prev => ({ ...prev, [key]: url }));
            setRecordingKey(null);
        };

        mediaRecorder.start();
    };

    const stopRecording = () => mediaRecorderRef.current?.stop();

    const nextQuestion = () => {
        if (!speaking) return;

        const questions = currentPart === "part1" ? speaking.part1.questions : speaking.part3.questions;

        if (currentQuestionIndex < questions.length - 1) {
            setCurrentQuestionIndex(prev => prev + 1);
        } else {
            // Nếu là part3, sau câu hỏi cuối cùng → mở confirm Submit
            if (currentPart === "part3") {
                setIsSubmitting(true);
            } else {
                setShowTransition(true);
            }
        }
    };

    const goToNextPart = () => {
        if (timerRef.current) clearInterval(timerRef.current);
        timerRef.current = null;

        setShowTransition(false);
        setPartStarted(false);
        setShowConfirmNextPart(false);
        setCurrentQuestionIndex(0);

        if (currentPart === "part1") setCurrentPart("part2");
        else if (currentPart === "part2") setCurrentPart("part3");
    };

    const renderQuestion = (q: string, key: string, isLast: boolean, onNext: () => void) => (
        <div>
            <p><strong>Câu hỏi:</strong> {q}</p>
            <button onClick={() => {
                recordingKey === key
                    ? stopRecording()
                    : startThinking(currentPart === "part2" ? 60 : 5, () => startRecording(key));
            }} disabled={isThinking}>
                {recordingKey === key ? "Stop Recording" : isThinking ? `Thinking... (${thinkingTime}s)` : "Start Recording"}
            </button>
            {audioUrls[key] && (
                <>
                    <audio controls src={audioUrls[key]} style={{ display: "block", marginTop: "0.5rem" }} />
                    {currentPart !== "part2" && (
                        <button onClick={onNext}>{isLast ? "Kết thúc Part" : "Next"}</button>
                    )}
                </>
            )}
        </div>
    );

    const prepareSubmissionData = () => {
        if (!speaking) return null;

        const cloned = JSON.parse(JSON.stringify(speaking));
        if ("username" in user) cloned.username = user.username;

        cloned.part1.questions = cloned.part1.questions.map((q: any, i: number) => ({
            question: q.question,
            studentAnswer: audioUrls[`part1-${i + 1}`] ? `part1-${i + 1}.webm` : "",
        }));

        cloned.part2.studentAnswer = audioUrls["part2"] ? "part2.webm" : "";

        cloned.part3.questions = cloned.part3.questions.map((q: any, i: number) => ({
            question: q.question,
            studentAnswer: audioUrls[`part3-${i + 1}`] ? `part3-${i + 1}.webm` : "",
        }));

        return cloned;
    };

    const handleSubmit = async () => {
        const submissionData = prepareSubmissionData();
        if (!submissionData) return;

        const formData = new FormData();
        formData.append("metadata", new Blob([JSON.stringify(submissionData)], { type: "application/json" }), "metadata.json");

        await Promise.all(
            Object.entries(audioUrls).map(async ([key, url]) => {
                const blob = await fetch(url).then(res => res.blob());
                formData.append("files", blob, `${key}.webm`);
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
    };

    if (loading) return <p>Loading...</p>;
    if (!speaking) return <p>No data found</p>;

    const partData = speaking[currentPart];
    const timerDisplay = partStarted ? partTimeLeft : getInitialTime();

    return (
        <div>
            <h1>Speaking Test ID: {speaking._id}</h1>
            <h2>Part {partData.partNumber}: {partData.title}</h2>

            <div style={{ marginBottom: "1rem", fontWeight: "bold", color: "darkred" }}>
                ⏱️ Thời gian còn lại: {Math.floor(timerDisplay / 60)}:{String(timerDisplay % 60).padStart(2, "0")}
            </div>

            {showTransition ? (
                <div>
                    <p><strong>Bạn đã hoàn thành {currentPart.toUpperCase()}</strong></p>
                    <button onClick={goToNextPart}>Tiếp tục phần tiếp theo</button>
                </div>
            ) : (
                <section style={{ marginTop: "1rem" }}>
                    {!partStarted ? (
                        <>
                            {partData.instruction && (
                                <p style={{ fontStyle: "italic", marginBottom: "1rem" }}>📘 {partData.instruction}</p>
                            )}
                            <button onClick={() => startTimer(getInitialTime())}>▶️ Start</button>
                        </>
                    ) : (
                        <>
                            {currentPart === "part1" &&
                                renderQuestion(
                                    speaking.part1.questions[currentQuestionIndex].question,
                                    `part1-${currentQuestionIndex + 1}`,
                                    currentQuestionIndex === speaking.part1.questions.length - 1,
                                    nextQuestion
                                )
                            }

                            {currentPart === "part2" &&
                                renderQuestion(speaking.part2.question, "part2", true, () => { })
                            }

                            {currentPart === "part3" &&
                                renderQuestion(
                                    speaking.part3.questions[currentQuestionIndex].question,
                                    `part3-${currentQuestionIndex + 1}`,
                                    currentQuestionIndex === speaking.part3.questions.length - 1,
                                    nextQuestion
                                )
                            }
                        </>
                    )}
                </section>
            )}

            {!showTransition && partStarted && (
                <div style={{ marginTop: "2rem" }}>
                    {currentPart !== "part3" ? (
                        <button onClick={() => setShowConfirmNextPart(true)}>➡️ Next Part</button>
                    ) : (
                        <button onClick={() => setIsSubmitting(true)}>✅ Submit Test</button>
                    )}
                </div>
            )}

            {showConfirmNextPart && (
                <div style={{ background: "#fffae6", padding: "1rem", marginTop: "1rem", border: "1px solid #ccc" }}>
                    <p><strong>Bạn có chắc muốn chuyển sang phần tiếp theo không?</strong></p>
                    <button onClick={goToNextPart} style={{ marginRight: "1rem" }}>✅ Có</button>
                    <button onClick={() => setShowConfirmNextPart(false)}>❌ Không</button>
                </div>
            )}

            {isSubmitting && (
                <div style={{ background: "#ffe0e0", padding: "1rem", marginTop: "1rem", border: "1px solid #ccc" }}>
                    <p><strong>Bạn có chắc muốn nộp bài không?</strong></p>
                    <button onClick={handleSubmit}>✅ Có</button>
                    <button onClick={() => setIsSubmitting(false)}>❌ Không</button>
                </div>
            )}
        </div>
    );
};

export default SpeakingTest;
