"use client"

import { useEffect, useRef, useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Progress } from "@/components/ui/progress"
import { Separator } from "@radix-ui/react-dropdown-menu"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { Clock, Mic, Play, Square, ChevronRight, CheckCircle, AlertCircle, Volume2, Brain } from "lucide-react"
import {useNavigate, useParams} from "react-router-dom";
import {useAuth} from "@/contexts/AuthContext";

// Mock user context for demo
type Speaking = {
    _id: string
    username: string; // ✅ Thêm username
    skill: string;
    part1: {
        partNumber: numbernpm 
        title: string
        instruction: string
        questions: { question: string }[]
    }
    part2: {
        partNumber: number
        title: string
        instruction: string
        question: string
        cueCards: string[]
    }
    part3: {
        partNumber: number
        title: string
        instruction: string
        questions: { question: string }[]
    }
}

type Part = "part1" | "part2" | "part3"

const SpeakingTest = () => {
    const { testId } = useParams<{ testId: string }>()
    const { user } = useAuth()

    const [speaking, setSpeaking] = useState<Speaking | null>(null)
    const [loading, setLoading] = useState(true)
    const [currentPart, setCurrentPart] = useState<Part>("part1")
    const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0)
    const [audioUrls, setAudioUrls] = useState<{ [key: string]: string }>({})
    const [recordingKey, setRecordingKey] = useState<string | null>(null)
    const [isThinking, setIsThinking] = useState(false)
    const [thinkingTime, setThinkingTime] = useState(0)
    const [partTimeLeft, setPartTimeLeft] = useState(0)
    const [partStarted, setPartStarted] = useState(false)
    const [showTransition, setShowTransition] = useState(false)
    const [showConfirmNextPart, setShowConfirmNextPart] = useState(false)
    const [isSubmitting, setIsSubmitting] = useState(false)
    const navigate = useNavigate();
    const timerRef = useRef<NodeJS.Timeout | null>(null)
    const mediaRecorderRef = useRef<MediaRecorder | null>(null)
    const audioChunksRef = useRef<Blob[]>([])

    // Mock data for demo
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
        if (!speaking) return 0
        const questions =
            currentPart === "part1" ? speaking.part1.questions : currentPart === "part3" ? speaking.part3.questions : []
        if (currentPart === "part2") return 180
        return 300 + (questions.length) * 5
    }

    const startTimer = (seconds: number) => {
        if (timerRef.current) return
        setPartStarted(true)
        setPartTimeLeft(seconds)
        timerRef.current = setInterval(() => {
            setPartTimeLeft((prev) => {
                if (prev <= 1) {
                    clearInterval(timerRef.current!)
                    timerRef.current = null
                    setShowTransition(true)

                    if (currentPart === "part3") {
                        setIsSubmitting(true)
                    } else {
                        setTimeout(goToNextPart, 2000)
                    }

                    return 0
                }
                return prev - 1
            })
        }, 1000)
    }

    const startThinking = (seconds: number, callback: () => void) => {
        setThinkingTime(seconds)
        setIsThinking(true)
        const interval = setInterval(() => {
            setThinkingTime((prev) => {
                if (prev <= 1) {
                    clearInterval(interval)
                    setIsThinking(false)
                    callback()
                    return 0
                }
                return prev - 1
            })
        }, 1000)
    }

    const startRecording = async (key: string) => {
        if (!partStarted) startTimer(getInitialTime())
        try {
            const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
            const mediaRecorder = new MediaRecorder(stream)
            mediaRecorderRef.current = mediaRecorder
            audioChunksRef.current = []
            setRecordingKey(key)

            mediaRecorder.ondataavailable = (e: BlobEvent) => audioChunksRef.current.push(e.data)
            mediaRecorder.onstop = () => {
                const audioBlob = new Blob(audioChunksRef.current, { type: "audio/webm" })
                const url = URL.createObjectURL(audioBlob)
                setAudioUrls((prev) => ({ ...prev, [key]: url }))
                setRecordingKey(null)
            }

            mediaRecorder.start()
        } catch (error) {
            console.error("Error accessing microphone:", error)
        }
    }

    const stopRecording = () => mediaRecorderRef.current?.stop()

    const nextQuestion = () => {
        if (!speaking) return

        const questions = currentPart === "part1" ? speaking.part1.questions : speaking.part3.questions

        if (currentQuestionIndex < questions.length - 1) {
            setCurrentQuestionIndex((prev) => prev + 1)
        } else {
            if (currentPart === "part3") {
                setIsSubmitting(true)
            } else {
                setShowTransition(true)
            }
        }
    }

    const goToNextPart = () => {
        if (timerRef.current) clearInterval(timerRef.current)
        timerRef.current = null

        setShowTransition(false)
        setPartStarted(false)
        setShowConfirmNextPart(false)
        setCurrentQuestionIndex(0)

        if (currentPart === "part1") setCurrentPart("part2")
        else if (currentPart === "part2") setCurrentPart("part3")
    }

    const formatTime = (seconds: number) => {
        const mins = Math.floor(seconds / 60)
        const secs = seconds % 60
        return `${mins}:${secs.toString().padStart(2, "0")}`
    }

    const getProgressPercentage = () => {
        const totalTime = getInitialTime()
        return ((totalTime - partTimeLeft) / totalTime) * 100
    }

    const renderQuestion = (q: string, key: string, isLast: boolean, onNext: () => void) => (
        <Card className="mb-6">
            <CardContent className="p-6">
                <div className="space-y-4">
                    <div className="flex items-start gap-3">
                        <div className="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center flex-shrink-0 mt-1">
                            <span className="text-blue-600 font-semibold text-sm">Q</span>
                        </div>
                        <p className="text-lg leading-relaxed">{q}</p>
                    </div>

                    <div className="flex flex-col sm:flex-row gap-3 items-start">
                        <Button
                            onClick={() => {
                                recordingKey === key
                                    ? stopRecording()
                                    : startThinking(currentPart === "part2" ? 60 : 5, () => startRecording(key))
                            }}
                            disabled={isThinking}
                            variant={recordingKey === key ? "destructive" : "default"}
                            size="lg"
                            className="min-w-[200px]"
                        >
                            {recordingKey === key ? (
                                <>
                                    <Square className="w-4 h-4 mr-2" />
                                    Stop Recording
                                </>
                            ) : isThinking ? (
                                <>
                                    <Brain className="w-4 h-4 mr-2 animate-pulse" />
                                    Thinking... ({thinkingTime}s)
                                </>
                            ) : (
                                <>
                                    <Mic className="w-4 h-4 mr-2" />
                                    Start Recording
                                </>
                            )}
                        </Button>

                        {isThinking && (
                            <div className="flex items-center gap-2 text-orange-600">
                                <Brain className="w-4 h-4 animate-pulse" />
                                <span className="text-sm font-medium">Preparation time: {thinkingTime}s</span>
                            </div>
                        )}
                    </div>

                    {audioUrls[key] && (
                        <div className="space-y-3 p-4 bg-green-50 rounded-lg border border-green-200">
                            <div className="flex items-center gap-2 text-green-700">
                                <CheckCircle className="w-4 h-4" />
                                <span className="font-medium">Recording completed</span>
                            </div>
                            <div className="flex items-center gap-3">
                                <Volume2 className="w-4 h-4 text-gray-500" />
                                <audio controls src={audioUrls[key]} className="flex-1" />
                            </div>
                            {currentPart !== "part2" && (
                                <Button onClick={onNext} variant="outline" className="w-full sm:w-auto">
                                    {isLast ? "Complete Part" : "Next Question"}
                                    <ChevronRight className="w-4 h-4 ml-2" />
                                </Button>
                            )}
                        </div>
                    )}
                </div>
            </CardContent>
        </Card>
    )

    const prepareSubmissionData = () => {
        if (!speaking) return null

        const cloned = JSON.parse(JSON.stringify(speaking))
        if ("username" in user) cloned.username = user.username
        cloned.skill = "speaking";

        cloned.part1.questions = cloned.part1.questions.map((q: any, i: number) => ({
            question: q.question,
            studentAnswer: audioUrls[`part1-${i + 1}`] ? `part1-${i + 1}.webm` : "",
        }))

        cloned.part2.studentAnswer = audioUrls["part2"] ? "part2.webm" : ""

        cloned.part3.questions = cloned.part3.questions.map((q: any, i: number) => ({
            question: q.question,
            studentAnswer: audioUrls[`part3-${i + 1}`] ? `part3-${i + 1}.webm` : "",
        }))

        return cloned
    }

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
        console.log(submissionData)
        try {
            const res = await fetch("http://localhost:8080/verify/speaking/submit", {
                method: "POST",
                body: formData,
                credentials: "include",
            });

            if (!res.ok) throw new Error("Lỗi khi gửi bài!");
            alert("✅ Bài đã được nộp!");
            navigate("/result");
        } catch (err) {
            console.error(err);
            alert("❌ Gửi bài thất bại!");
        }

        setIsSubmitting(false);

    };


    if (loading) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center">
                <div className="text-center">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
                    <p className="text-gray-600">Loading your speaking test...</p>
                </div>
            </div>
        )
    }

    if (!speaking) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center">
                <Alert className="max-w-md">
                    <AlertCircle className="h-4 w-4" />
                    <AlertDescription>No test data found. Please try again.</AlertDescription>
                </Alert>
            </div>
        )
    }

    const partData = speaking[currentPart]
    const timerDisplay = partStarted ? partTimeLeft : getInitialTime()

    return (
        <div className="min-h-screen bg-gray-50">


            <div className="max-w-4xl mx-auto px-4 py-8">
                {/* Progress and Timer */}
                <Card className="mb-6">
                    <CardContent className="p-6">
                        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 mb-4">
                            <div>
                                <h2 className="text-2xl font-bold text-gray-900 mb-1">
                                    Part {partData.partNumber}: {partData.title}
                                </h2>
                                <div className="flex items-center gap-2">
                                    <Badge variant={currentPart === "part1" ? "default" : "secondary"}>Part 1</Badge>
                                    <Badge variant={currentPart === "part2" ? "default" : "secondary"}>Part 2</Badge>
                                    <Badge variant={currentPart === "part3" ? "default" : "secondary"}>Part 3</Badge>
                                </div>
                            </div>
                            <div className="flex items-center gap-3 text-right">
                                <Clock className="w-5 h-5 text-blue-600" />
                                <div>
                                    <div className="text-2xl font-bold text-blue-600">{formatTime(timerDisplay)}</div>
                                    <div className="text-sm text-gray-500">Time remaining</div>
                                </div>
                            </div>
                        </div>

                        {partStarted && (
                            <div className="space-y-2">
                                <div className="flex justify-between text-sm text-gray-600">
                                    <span>Progress</span>
                                    <span>{Math.round(getProgressPercentage())}%</span>
                                </div>
                                <Progress value={getProgressPercentage()} className="h-2" />
                            </div>
                        )}
                    </CardContent>
                </Card>

                {/* Transition Screen */}
                {showTransition ? (
                    <Card>
                        <CardContent className="p-8 text-center">
                            <CheckCircle className="w-16 h-16 text-green-500 mx-auto mb-4" />
                            <h3 className="text-xl font-bold mb-2">Part {partData.partNumber} Completed!</h3>
                            <p className="text-gray-600 mb-6">
                                Great job! You've successfully completed {currentPart.toUpperCase()}.
                            </p>
                            <Button onClick={goToNextPart} size="lg">
                                Continue to Next Part
                                <ChevronRight className="w-4 h-4 ml-2" />
                            </Button>
                        </CardContent>
                    </Card>
                ) : (
                    <>
                        {/* Instructions */}
                        {!partStarted && (
                            <Card className="mb-6">
                                <CardHeader>
                                    <CardTitle className="flex items-center gap-2">
                                        <div className="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center">
                                            <span className="text-blue-600 font-bold text-sm">i</span>
                                        </div>
                                        Instructions
                                    </CardTitle>
                                </CardHeader>
                                <CardContent>
                                    <p className="text-gray-700 leading-relaxed mb-4">{partData.instruction}</p>

                                    {currentPart === "part2" && speaking.part2.cueCards && (
                                        <div className="bg-blue-50 p-4 rounded-lg">
                                            <h4 className="font-semibold mb-2">You should talk about:</h4>
                                            <ul className="space-y-1">
                                                {speaking.part2.cueCards.map((card, index) => (
                                                    <li key={index} className="flex items-center gap-2">
                                                        <div className="w-1.5 h-1.5 bg-blue-500 rounded-full"></div>
                                                        {card}
                                                    </li>
                                                ))}
                                            </ul>
                                        </div>
                                    )}

                                    <Separator className="my-4" />
                                    <Button onClick={() => startTimer(getInitialTime())} size="lg" className="w-full sm:w-auto">
                                        <Play className="w-4 h-4 mr-2" />
                                        Start Part {partData.partNumber}
                                    </Button>
                                </CardContent>
                            </Card>
                        )}

                        {/* Questions */}
                        {partStarted && (
                            <div className="space-y-6">
                                {currentPart === "part1" &&
                                    renderQuestion(
                                        speaking.part1.questions[currentQuestionIndex].question,
                                        `part1-${currentQuestionIndex + 1}`,
                                        currentQuestionIndex === speaking.part1.questions.length - 1,
                                        nextQuestion,
                                    )}

                                {currentPart === "part2" && renderQuestion(speaking.part2.question, "part2", true, () => {})}

                                {currentPart === "part3" &&
                                    renderQuestion(
                                        speaking.part3.questions[currentQuestionIndex].question,
                                        `part3-${currentQuestionIndex + 1}`,
                                        currentQuestionIndex === speaking.part3.questions.length - 1,
                                        nextQuestion,
                                    )}

                                {/* Navigation */}
                                <Card>
                                    <CardContent className="p-4">
                                        <div className="flex flex-col sm:flex-row gap-3 justify-between">
                                            <div className="text-sm text-gray-600">
                                                {currentPart === "part1" &&
                                                    `Question ${currentQuestionIndex + 1} of ${speaking.part1.questions.length}`}
                                                {currentPart === "part2" && "Long turn - Speak for 1-2 minutes"}
                                                {currentPart === "part3" &&
                                                    `Question ${currentQuestionIndex + 1} of ${speaking.part3.questions.length}`}
                                            </div>
                                            <div className="flex gap-2">
                                                {currentPart !== "part3" ? (
                                                    <Button onClick={() => setShowConfirmNextPart(true)} variant="outline">
                                                        Skip to Next Part
                                                        <ChevronRight className="w-4 h-4 ml-2" />
                                                    </Button>
                                                ) : (
                                                    <Button onClick={() => setIsSubmitting(true)} className="bg-green-600 hover:bg-green-700">
                                                        <CheckCircle className="w-4 h-4 mr-2" />
                                                        Submit Test
                                                    </Button>
                                                )}
                                            </div>
                                        </div>
                                    </CardContent>
                                </Card>
                            </div>
                        )}
                    </>
                )}

                {/* Confirmation Dialogs */}
                {showConfirmNextPart && (
                    <Card className="mt-6 border-orange-200 bg-orange-50">
                        <CardContent className="p-6">
                            <div className="flex items-start gap-3">
                                <AlertCircle className="w-5 h-5 text-orange-600 mt-0.5" />
                                <div className="flex-1">
                                    <h3 className="font-semibold text-orange-900 mb-2">Skip to Next Part?</h3>
                                    <p className="text-orange-800 mb-4">
                                        Are you sure you want to move to the next part? You won't be able to return to this section.
                                    </p>
                                    <div className="flex gap-3">
                                        <Button onClick={goToNextPart} variant="default">
                                            Yes, Continue
                                        </Button>
                                        <Button onClick={() => setShowConfirmNextPart(false)} variant="outline">
                                            Cancel
                                        </Button>
                                    </div>
                                </div>
                            </div>
                        </CardContent>
                    </Card>
                )}

                {isSubmitting && (
                    <Card className="mt-6 border-green-200 bg-green-50">
                        <CardContent className="p-6">
                            <div className="flex items-start gap-3">
                                <CheckCircle className="w-5 h-5 text-green-600 mt-0.5" />
                                <div className="flex-1">
                                    <h3 className="font-semibold text-green-900 mb-2">Submit Your Test?</h3>
                                    <p className="text-green-800 mb-4">
                                        Are you ready to submit your speaking test? Once submitted, you cannot make any changes.
                                    </p>
                                    <div className="flex gap-3">
                                        <Button onClick={handleSubmit} className="bg-green-600 hover:bg-green-700">
                                            <CheckCircle className="w-4 h-4 mr-2" />
                                            Yes, Submit
                                        </Button>
                                        <Button onClick={() => setIsSubmitting(false)} variant="outline">
                                            Cancel
                                        </Button>
                                    </div>
                                </div>
                            </div>
                        </CardContent>
                    </Card>
                )}
            </div>
        </div>
    )
}

export default SpeakingTest