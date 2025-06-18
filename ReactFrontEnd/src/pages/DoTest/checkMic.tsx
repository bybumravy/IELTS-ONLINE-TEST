"use client"

import { useState, useEffect, useRef } from "react"
import { useNavigate, useParams } from "react-router-dom"
import { Button } from "@/components/ui/button"
import { Mic, Clock, Menu, ArrowRight, Info } from "lucide-react"

export default function VoiceRecorder() {
    const { testId } = useParams<{ testId: string }>()
    const [recording, setRecording] = useState(false)
    const [audioUrl, setAudioUrl] = useState<string | null>(null)
    const mediaRecorderRef = useRef<MediaRecorder | null>(null)
    const audioChunksRef = useRef<Blob[]>([])
    const [timeLeft, setTimeLeft] = useState(20)
    const navigate = useNavigate()

    // Countdown Timer
    useEffect(() => {
        if (recording && timeLeft > 0) {
            const timer = setTimeout(() => setTimeLeft(timeLeft - 1), 1000)
            return () => clearTimeout(timer)
        } else if (recording && timeLeft === 0) {
            stopRecording()
        }
    }, [recording, timeLeft])

    const formatTime = (seconds: number) => {
        const mins = Math.floor(seconds / 60)
        const secs = seconds % 60
        return `${mins.toString().padStart(2, "0")}:${secs.toString().padStart(2, "0")}`
    }

    const startRecording = async () => {
        try {
            const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
            const mediaRecorder = new MediaRecorder(stream)
            mediaRecorderRef.current = mediaRecorder
            audioChunksRef.current = []

            mediaRecorder.ondataavailable = (event: BlobEvent) => {
                audioChunksRef.current.push(event.data)
            }

            mediaRecorder.onstop = () => {
                const audioBlob = new Blob(audioChunksRef.current, { type: "audio/webm" })
                const url = URL.createObjectURL(audioBlob)
                setAudioUrl(url)
            }

            mediaRecorder.start()
            setRecording(true)
            setTimeLeft(20) // Reset timer
        } catch (error) {
            console.error("Error starting recording:", error)
        }
    }

    const stopRecording = () => {
        if (mediaRecorderRef.current && recording) {
            mediaRecorderRef.current.stop()
            setRecording(false)
        }
    }

    const handleTestMicrophone = () => {
        if (recording) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    const handleSkip = () => {
        console.log("Skipped microphone test")
        // Navigate to next test step
        navigate(`/checkMic/${testId}`)
    }

    return (
        <div className="min-h-screen bg-white">
            {/* Header */}
            <header className="flex items-center justify-between p-4 bg-white">
                <div className="flex items-center">
                    <div className="w-8 h-8 bg-gradient-to-r from-blue-500 via-purple-500 to-pink-500 rounded-lg flex items-center justify-center">
                        <span className="text-white font-bold text-sm">ok</span>
                    </div>
                </div>

                <div className="flex items-center gap-2 text-gray-600">
                    <Clock className="w-5 h-5" />
                    <span className="text-xl font-medium text-pink-400">{formatTime(timeLeft)}</span>
                </div>

                <Button variant="ghost" size="icon">
                    <Menu className="w-6 h-6" />
                </Button>
            </header>

            {/* Main Content */}
            <div className="flex items-center justify-center min-h-[calc(100vh-80px)] p-4">
                <div className="w-full max-w-2xl mx-auto">
                    <div className="bg-gray-50 rounded-3xl p-12 text-center">
                        <h1 className="text-3xl md:text-4xl font-bold text-slate-700 mb-12">TEST YOUR MICROPHONE</h1>

                        <div className="mb-8">
                            <Button
                                variant="ghost"
                                size="icon"
                                className={`w-20 h-20 rounded-full ${recording ? "bg-pink-400" : "bg-white shadow-lg hover:shadow-xl"} transition-shadow`}
                                onClick={handleTestMicrophone}
                            >
                                <Mic className={`w-8 h-8 ${recording ? "text-white" : "text-pink-400"}`} />
                            </Button>
                        </div>

                        <p className="text-gray-600 mb-8 text-lg">
                            {recording ? `Recording... ${timeLeft} seconds left` : "Press the button to start recording"}
                        </p>

                        <div className="flex items-center justify-center gap-2 text-gray-600 mb-12">
                            <span>To complete this activity, you must allow access to your system's microphone. Click</span>
                            <div className="w-6 h-6 rounded-full bg-pink-400 flex items-center justify-center">
                                <Info className="w-3 h-3 text-white" />
                            </div>
                            <span>the button below to Start.</span>
                        </div>

                        <div className="flex gap-4 justify-center">
                            <Button
                                onClick={handleTestMicrophone}
                                className="bg-pink-400 hover:bg-pink-500 text-white px-8 py-3 rounded-lg font-medium"
                            >
                                {recording ? "Stop Recording" : "Test Microphone"}
                                <div className="w-4 h-4 rounded-full bg-white/20 ml-2 flex items-center justify-center">
                                    <Info className="w-2 h-2 text-white" />
                                </div>
                            </Button>

                            <Button
                                onClick={handleSkip}
                                variant="outline"
                                className="bg-slate-600 hover:bg-slate-700 text-white border-slate-600 px-8 py-3 rounded-lg font-medium"
                            >
                                Skip
                                <ArrowRight className="w-4 h-4 ml-2" />
                            </Button>
                        </div>

                        {audioUrl && (
                            <div className="mt-8">
                                <h3 className="text-lg font-medium text-slate-700 mb-2">Preview:</h3>
                                <audio controls src={audioUrl} className="w-full rounded-lg"></audio>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    )
}
