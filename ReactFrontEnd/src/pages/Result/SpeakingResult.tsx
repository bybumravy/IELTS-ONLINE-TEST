"use client"

import { useEffect, useState } from "react"
import {
    Award,
    FileText,
    BookOpen,
    Target,
    Zap,
    Mic,
    Volume2,
    Play,
    Pause,
    Clock,
    CheckCircle,
    AlertCircle,
} from "lucide-react"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import {useParams} from "react-router-dom";

interface StressMismatch {
    word: string
    expectedStress: string
    actualStress: string
    start: number
    end: number
    feedback?: string
}

interface PronunciationEvaluation {
    word: string
    feedback?: string
    isCorrect?: boolean
    stress?: string
}

interface GrammarAnswer {
    score?: number
    errorText: string
    correctText: string
    errorType: string
    explanation: string
}

interface FleCohAnswer {
    score?: number
    meanIntensity: string
    pauseCount: string
    speechRate: string
    comment: string
}

interface PronunciationAnswer {
    score: number
    StressTranscript: string
    stressMismatchesDetailed: StressMismatch[]
    pronunciationEvaluation: PronunciationEvaluation[]
}

interface SpeakingAnswerQuestion {
    question: string
    transcript: string
    audioAnswer: string
    score: number
    grammarAnswer: GrammarAnswer
    lexicalAnswer: GrammarAnswer
    pronunciationAnswer: PronunciationAnswer
    fluencyCohAnswer: FleCohAnswer
}

interface SpeakingAnswerPart13 {
    partNumber: number
    title: string
    instruction: string
    questions?: SpeakingAnswerQuestion[]
    averageScore: number
}

interface SpeakingAnswerPart2 {
    partNumber: number
    title: string
    question: string
    studentAnswer: string
    audioAnswer: string
    score: number
    grammarAnswer: GrammarAnswer
    lexicalAnswer: GrammarAnswer
    pronunciationAnswer: PronunciationAnswer
    fluencyCohAnswer: FleCohAnswer
    cueCards: string[]
}

interface SpeakingAnswer {
    id: string
    testId: string
    username?: string
    skill: string
    part1: SpeakingAnswerPart13
    part2: SpeakingAnswerPart2
    part3: SpeakingAnswerPart13
    band?: number
    submittedAt?: string
}

export default function SpeakingResult() {
    const API_URL = import.meta.env.VITE_API_URL;

    const [data, setData] = useState<SpeakingAnswer | null>(null)
    const [loading, setLoading] = useState(true)
    const [activePart, setActivePart] = useState<"part1" | "part2" | "part3">("part1")
    const [isPlaying, setIsPlaying] = useState(false)
    const [currentAudio, setCurrentAudio] = useState<HTMLAudioElement | null>(null)

    const { resultId } = useParams  <{ resultId: string }>();

    const [feedbackView, setFeedbackView] = useState<"errors" | "improvements">("errors")
    useEffect(() => {
        fetch(`${API_URL}/api/result/speaking/${resultId}`)
            .then(res => {
                if (!res.ok) throw new Error("Failed to fetch data");
                return res.json();
            })
            .then(json => setData(json))
            .catch(err => console.error("Fetch error:", err))
            .finally(() => setLoading(false));
    }, [resultId]);


    const calculateOverallScore = () => {
        if (!data) return 0
        const scores = [data.part1?.averageScore ?? 0, data.part2?.score ?? 0, data.part3?.averageScore ?? 0]
        const validScores = scores.filter((s) => typeof s === "number" && !isNaN(s))
        if (validScores.length === 0) return 0
        const avg = validScores.reduce((a, b) => a + b, 0) / validScores.length
        return Math.round(avg * 10) / 10
    }

    const playAudio = (audioUrl: string) => {
        try {
            if (currentAudio) {
                currentAudio.pause()
            }
            if (!audioUrl) return
            const audio = new Audio(audioUrl)
            audio.onplay = () => setIsPlaying(true)
            audio.onpause = () => setIsPlaying(false)
            audio.onended = () => setIsPlaying(false)
            audio.onerror = () => setIsPlaying(false)
            setCurrentAudio(audio)
            audio.play().catch(() => setIsPlaying(false))
        } catch {
            setIsPlaying(false)
        }
    }

    const renderErrorCorrections = (originalText: string, grammarAnswer: GrammarAnswer, lexicalAnswer: GrammarAnswer) => {
        const errors = []
        if (grammarAnswer?.errorText) errors.push({ ...grammarAnswer, type: "Grammar" })
        if (lexicalAnswer?.errorText) errors.push({ ...lexicalAnswer, type: "Lexical" })

        if (errors.length === 0) {
            return (
                <div className="bg-green-50 border border-green-200 rounded-2xl p-8 text-center">
                    <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
                        <CheckCircle className="h-8 w-8 text-green-600" />
                    </div>
                    <h4 className="text-lg font-semibold text-green-800 mb-2">Excellent Work!</h4>
                    <p className="text-green-700">No errors detected in this section. Keep up the great work!</p>
                </div>
            )
        }

        return (
            <div className="space-y-6">
                <div className="bg-gray-50 border border-gray-200 rounded-2xl p-6">
                    <h4 className="font-semibold text-gray-800 mb-4 flex items-center gap-2">
                        <FileText className="h-5 w-5 text-green-600" />
                        Your Response
                    </h4>
                    <div className="bg-white border border-gray-200 rounded-xl p-4">
                        <p className="text-gray-700 leading-relaxed italic">"{originalText}"</p>
                    </div>
                </div>

                <div className="space-y-4">
                    <h4 className="font-semibold text-gray-800 flex items-center gap-2 text-lg">
                        <Target className="h-6 w-6 text-red-600" />
                        Areas for Improvement
                    </h4>
                    {errors.map((error, index) => (
                        <div key={index} className="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm">
                            <div className="flex items-start justify-between mb-4">
                                <Badge className="bg-red-100 text-red-700 border-red-200">{error.type} Error</Badge>
                                <Badge variant="outline" className="text-gray-600">
                                    {error.errorType}
                                </Badge>
                            </div>

                            <div className="grid md:grid-cols-2 gap-6 mb-4">
                                <div className="space-y-2">
                                    <div className="flex items-center gap-2">
                                        <AlertCircle className="h-4 w-4 text-red-500" />
                                        <span className="text-sm font-medium text-gray-700">Original</span>
                                    </div>
                                    <div className="bg-red-50 border border-red-200 rounded-lg p-3">
                                        <p className="text-red-700 font-medium">{error.errorText}</p>
                                    </div>
                                </div>
                                <div className="space-y-2">
                                    <div className="flex items-center gap-2">
                                        <CheckCircle className="h-4 w-4 text-green-500" />
                                        <span className="text-sm font-medium text-gray-700">Correction</span>
                                    </div>
                                    <div className="bg-green-50 border border-green-200 rounded-lg p-3">
                                        <p className="text-green-700 font-medium">{error.correctText}</p>
                                    </div>
                                </div>
                            </div>

                            <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                                <h5 className="font-medium text-blue-800 mb-2">Explanation</h5>
                                <p className="text-blue-700 text-sm leading-relaxed">{error.explanation}</p>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        )
    }

    const renderPronunciationScript = (words: PronunciationEvaluation[]) => {
        const correctWords = words.filter((w) => w.isCorrect).length
        const totalWords = words.length
        const accuracy = Math.round((correctWords / totalWords) * 100)

        return (
            <div className="space-y-6">
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <div className="bg-green-50 border border-green-200 rounded-2xl p-6 text-center">
                        <div className="text-3xl font-bold text-green-700 mb-2">{accuracy}%</div>
                        <p className="text-green-600 font-medium">Accuracy</p>
                    </div>
                    <div className="bg-blue-50 border border-blue-200 rounded-2xl p-6 text-center">
                        <div className="text-3xl font-bold text-blue-700 mb-2">{correctWords}</div>
                        <p className="text-blue-600 font-medium">Correct Words</p>
                    </div>
                    <div className="bg-amber-50 border border-amber-200 rounded-2xl p-6 text-center">
                        <div className="text-3xl font-bold text-amber-700 mb-2">{totalWords - correctWords}</div>
                        <p className="text-amber-600 font-medium">Need Practice</p>
                    </div>
                </div>

                <div className="bg-white border border-gray-200 rounded-2xl p-6">
                    <h4 className="font-semibold text-gray-800 mb-4 flex items-center gap-2">
                        <Mic className="h-5 w-5 text-green-600" />
                        Word-by-Word Analysis
                    </h4>
                    <div className="flex flex-wrap gap-3">
                        {words.map((wordObj, index) => (
                            <div
                                key={index}
                                className={`inline-flex items-center px-4 py-2 rounded-xl border transition-all cursor-help ${
                                    !wordObj.isCorrect
                                        ? "bg-red-50 text-red-800 border-red-200"
                                        : wordObj.stress === "primary"
                                            ? "bg-blue-50 text-blue-800 border-blue-200 font-bold"
                                            : wordObj.stress === "secondary"
                                                ? "bg-blue-25 text-blue-700 border-blue-100 font-medium"
                                                : "bg-gray-50 text-gray-700 border-gray-200"
                                }`}
                                title={wordObj.feedback || `Stress: ${wordObj.stress}`}
                            >
                                <span>{wordObj.word}</span>
                                {wordObj.stress === "primary" && <span className="text-blue-600 ml-1 text-lg">ˈ</span>}
                                {wordObj.stress === "secondary" && <span className="text-blue-500 ml-1">ˌ</span>}
                                {!wordObj.isCorrect && <AlertCircle className="h-4 w-4 ml-2 text-red-600" />}
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        )
    }

    const renderPartContent = (part: SpeakingAnswerPart13 | SpeakingAnswerPart2, isPart2 = false) => {
        if (isPart2) {
            // part2: render 1 question
            const currentQuestion = (part as SpeakingAnswerPart2)
            return (
                <div className="space-y-6">
                    {/* Question Section */}
                    <div className="bg-white border border-gray-200 rounded-2xl p-8 shadow-sm">
                        <div className="flex items-center gap-3 mb-6">
                            <div className="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center">
                                <BookOpen className="h-6 w-6 text-green-600" />
                            </div>
                            <div>
                                <h3 className="text-xl font-bold text-gray-800">Question</h3>
                                <p className="text-gray-600">Individual Long Turn</p>
                            </div>
                        </div>

                        <div className="bg-green-50 border-l-4 border-l-green-500 rounded-r-xl p-6 mb-6">
                            <p className="text-gray-800 leading-relaxed font-medium text-lg">{currentQuestion.question}</p>
                        </div>

                        {currentQuestion.cueCards && (
                            <div className="bg-amber-50 border border-amber-200 rounded-2xl p-6">
                                <h4 className="font-semibold text-amber-800 mb-4 flex items-center gap-2">
                                    <FileText className="h-5 w-5" />
                                    Cue Card Points
                                </h4>
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                                    {currentQuestion.cueCards.map((cue, index) => (
                                        <div key={index} className="flex items-center gap-3 bg-white rounded-lg p-3 border border-amber-200">
                                            <div className="w-6 h-6 bg-amber-500 text-white rounded-full flex items-center justify-center text-sm font-bold">
                                                {index + 1}
                                            </div>
                                            <span className="text-amber-800 font-medium">{cue}</span>
                                        </div>
                                    ))}
                                </div>
                            </div>
                        )}
                    </div>

                    {/* Audio Player */}
                    <div className="bg-white border border-gray-200 rounded-2xl p-8 shadow-sm">
                        <div className="flex items-center justify-between">
                            <div className="flex items-center gap-6">
                                <Button
                                    onClick={() => playAudio(currentQuestion.audioAnswer)}
                                    className="bg-green-600 hover:bg-green-700 w-16 h-16 rounded-full shadow-lg"
                                    size="lg"
                                >
                                    {isPlaying ? <Pause className="h-6 w-6" /> : <Play className="h-6 w-6" />}
                                </Button>
                                <div>
                                    <h4 className="text-xl font-bold text-gray-800 mb-1">Your Recording</h4>
                                    <div className="flex items-center gap-4 text-gray-600">
                                        <div className="flex items-center gap-1">
                                            <Clock className="h-4 w-4" />
                                            <span className="text-sm font-medium">Duration: 2:00</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <Volume2 className="h-8 w-8 text-gray-400" />
                        </div>
                    </div>

                    {/* Transcript */}
                    <div className="bg-white border border-gray-200 rounded-2xl p-8 shadow-sm">
                        <div className="flex items-center gap-3 mb-6">
                            <div className="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center">
                                <FileText className="h-6 w-6 text-green-600" />
                            </div>
                            <div>
                                <h3 className="text-xl font-bold text-gray-800">Your Response</h3>
                                <p className="text-gray-600">Transcript</p>
                            </div>
                        </div>
                        <div className="bg-gray-50 border border-gray-200 rounded-2xl p-6">
                            <p className="text-gray-700 leading-relaxed text-lg italic">
                                "{currentQuestion.studentAnswer}"
                            </p>
                        </div>
                    </div>

                    {/* Analysis Tabs */}
                    <div className="bg-white border border-gray-200 rounded-2xl shadow-sm overflow-hidden">
                        <div className="bg-green-600 text-white p-6">
                            <div className="flex items-center gap-3">
                                <div className="w-12 h-12 bg-white/20 rounded-full flex items-center justify-center">
                                    <Award className="h-6 w-6" />
                                </div>
                                <div>
                                    <h3 className="text-2xl font-bold">Detailed Analysis & Feedback</h3>
                                    <p className="text-green-100">Comprehensive evaluation across all criteria</p>
                                </div>
                            </div>
                        </div>

                        <Tabs defaultValue="grammar" className="w-full">
                            <TabsList className="grid w-full grid-cols-4 bg-gray-50 rounded-none border-b">
                                <TabsTrigger
                                    value="grammar"
                                    className="flex items-center gap-2 data-[state=active]:bg-white data-[state=active]:text-red-600"
                                >
                                    <Target className="h-4 w-4" />
                                    <span className="hidden sm:inline">Grammar</span>
                                </TabsTrigger>
                                <TabsTrigger
                                    value="lexical"
                                    className="flex items-center gap-2 data-[state=active]:bg-white data-[state=active]:text-amber-600"
                                >
                                    <BookOpen className="h-4 w-4" />
                                    <span className="hidden sm:inline">Vocabulary</span>
                                </TabsTrigger>
                                <TabsTrigger
                                    value="fluency"
                                    className="flex items-center gap-2 data-[state=active]:bg-white data-[state=active]:text-blue-600"
                                >
                                    <Zap className="h-4 w-4" />
                                    <span className="hidden sm:inline">Fluency</span>
                                </TabsTrigger>
                                <TabsTrigger
                                    value="pronunciation"
                                    className="flex items-center gap-2 data-[state=active]:bg-white data-[state=active]:text-purple-600"
                                >
                                    <Mic className="h-4 w-4" />
                                    <span className="hidden sm:inline">Pronunciation</span>
                                </TabsTrigger>
                            </TabsList>

                            <TabsContent value="grammar" className="p-8 space-y-6">
                                <div className="flex items-center justify-between mb-6">
                                    <h3 className="text-2xl font-bold text-gray-800 flex items-center gap-3">
                                        <Target className="h-6 w-6 text-red-600" />
                                        Grammar & Accuracy
                                    </h3>
                                    <div className="text-3xl font-bold text-red-600">{currentQuestion.grammarAnswer?.score ?? "-"}</div>
                                </div>
                                {renderErrorCorrections(
                                    currentQuestion.studentAnswer,
                                    currentQuestion.grammarAnswer,
                                    { ...currentQuestion.lexicalAnswer, errorText: "" },
                                )}
                            </TabsContent>

                            <TabsContent value="lexical" className="p-8 space-y-6">
                                <div className="flex items-center justify-between mb-6">
                                    <h3 className="text-2xl font-bold text-gray-800 flex items-center gap-3">
                                        <BookOpen className="h-6 w-6 text-amber-600" />
                                        Lexical Resource
                                    </h3>
                                    <div className="text-3xl font-bold text-amber-600">{currentQuestion.lexicalAnswer?.score ?? "-"}</div>
                                </div>
                                {renderErrorCorrections(
                                    currentQuestion.studentAnswer,
                                    { ...currentQuestion.grammarAnswer, errorText: "" },
                                    currentQuestion.lexicalAnswer,
                                )}
                            </TabsContent>

                            <TabsContent value="fluency" className="p-8 space-y-6">
                                <div className="flex items-center justify-between mb-6">
                                    <h3 className="text-2xl font-bold text-gray-800 flex items-center gap-3">
                                        <Zap className="h-6 w-6 text-blue-600" />
                                        Fluency & Coherence
                                    </h3>
                                    <div className="text-3xl font-bold text-blue-600">{currentQuestion.fluencyCohAnswer?.score ?? "-"}</div>
                                </div>

                                <div className="bg-blue-50 border border-blue-200 rounded-2xl p-8">
                                    <div className="bg-white rounded-xl p-6 mb-6 border border-blue-200">
                                        <h4 className="font-semibold text-blue-800 mb-3">Examiner Feedback</h4>
                                        <p className="text-gray-700 leading-relaxed text-lg">{currentQuestion.fluencyCohAnswer?.comment ?? ""}</p>
                                    </div>

                                    <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                                        <div className="bg-white rounded-xl p-6 border border-blue-200 text-center">
                                            <div className="text-3xl font-bold text-blue-600 mb-2">
                                                {currentQuestion.fluencyCohAnswer?.speechRate ?? "-"}
                                            </div>
                                            <p className="font-medium text-blue-800">Speech Rate</p>
                                        </div>
                                        <div className="bg-white rounded-xl p-6 border border-blue-200 text-center">
                                            <div className="text-3xl font-bold text-blue-600 mb-2">
                                                {currentQuestion.fluencyCohAnswer?.pauseCount ?? "-"}
                                            </div>
                                            <p className="font-medium text-blue-800">Pauses</p>
                                        </div>
                                        <div className="bg-white rounded-xl p-6 border border-blue-200 text-center">
                                            <div className="text-3xl font-bold text-blue-600 mb-2">
                                                {currentQuestion.fluencyCohAnswer?.meanIntensity ?? "-"}
                                            </div>
                                            <p className="font-medium text-blue-800">Volume</p>
                                        </div>
                                    </div>
                                </div>
                            </TabsContent>

                            <TabsContent value="pronunciation" className="p-8 space-y-6">
                                <div className="flex items-center justify-between mb-6">
                                    <h3 className="text-2xl font-bold text-gray-800 flex items-center gap-3">
                                        <Mic className="h-6 w-6 text-purple-600" />
                                        Pronunciation Assessment
                                    </h3>
                                    <div className="text-3xl font-bold text-purple-600">{currentQuestion.pronunciationAnswer?.score ?? "-"}</div>
                                </div>
                                {renderPronunciationScript(currentQuestion.pronunciationAnswer?.pronunciationEvaluation ?? [])}
                            </TabsContent>
                        </Tabs>
                    </div>
                </div>
            )
        } else {
            // part1 or part3: render all questions
            return (
                <div className="space-y-12">
                    {((part as SpeakingAnswerPart13).questions ?? []).map((question, idx) => (
                        <div key={idx} className="space-y-6">
                            {/* Question Section */}
                            <div className="bg-white border border-gray-200 rounded-2xl p-8 shadow-sm">
                                <div className="flex items-center gap-3 mb-6">
                                    <div className="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center">
                                        <BookOpen className="h-6 w-6 text-green-600" />
                                    </div>
                                    <div>
                                        <h3 className="text-xl font-bold text-gray-800">Question {idx + 1}</h3>
                                        <p className="text-gray-600">Interview Question</p>
                                    </div>
                                </div>
                                <div className="bg-green-50 border-l-4 border-l-green-500 rounded-r-xl p-6 mb-6">
                                    <p className="text-gray-800 leading-relaxed font-medium text-lg">{question.question}</p>
                                </div>
                            </div>
                            {/* Audio Player */}
                            <div className="bg-white border border-gray-200 rounded-2xl p-8 shadow-sm">
                                <div className="flex items-center justify-between">
                                    <div className="flex items-center gap-6">
                                        <Button
                                            onClick={() => playAudio(question.audioAnswer)}
                                            className="bg-green-600 hover:bg-green-700 w-16 h-16 rounded-full shadow-lg"
                                            size="lg"
                                        >
                                            {isPlaying ? <Pause className="h-6 w-6" /> : <Play className="h-6 w-6" />}
                                        </Button>
                                        <div>
                                            <h4 className="text-xl font-bold text-gray-800 mb-1">Your Recording</h4>
                                            <div className="flex items-center gap-4 text-gray-600">
                                                <div className="flex items-center gap-1">
                                                    <Clock className="h-4 w-4" />
                                                    <span className="text-sm font-medium">Duration: 1:45</span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <Volume2 className="h-8 w-8 text-gray-400" />
                                </div>
                            </div>
                            {/* Transcript */}
                            <div className="bg-white border border-gray-200 rounded-2xl p-8 shadow-sm">
                                <div className="flex items-center gap-3 mb-6">
                                    <div className="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center">
                                        <FileText className="h-6 w-6 text-green-600" />
                                    </div>
                                    <div>
                                        <h3 className="text-xl font-bold text-gray-800">Your Response</h3>
                                        <p className="text-gray-600">Transcript</p>
                                    </div>
                                </div>
                                <div className="bg-gray-50 border border-gray-200 rounded-2xl p-6">
                                    <p className="text-gray-700 leading-relaxed text-lg italic">
                                        "{question.transcript}"
                                    </p>
                                </div>
                            </div>
                            {/* Analysis Tabs */}
                            <div className="bg-white border border-gray-200 rounded-2xl shadow-sm overflow-hidden">
                                <div className="bg-green-600 text-white p-6">
                                    <div className="flex items-center gap-3">
                                        <div className="w-12 h-12 bg-white/20 rounded-full flex items-center justify-center">
                                            <Award className="h-6 w-6" />
                                        </div>
                                        <div>
                                            <h3 className="text-2xl font-bold">Detailed Analysis & Feedback</h3>
                                            <p className="text-green-100">Comprehensive evaluation across all criteria</p>
                                        </div>
                                    </div>
                                </div>

                                <Tabs defaultValue="grammar" className="w-full">
                                    <TabsList className="grid w-full grid-cols-4 bg-gray-50 rounded-none border-b">
                                        <TabsTrigger
                                            value="grammar"
                                            className="flex items-center gap-2 data-[state=active]:bg-white data-[state=active]:text-red-600"
                                        >
                                            <Target className="h-4 w-4" />
                                            <span className="hidden sm:inline">Grammar</span>
                                        </TabsTrigger>
                                        <TabsTrigger
                                            value="lexical"
                                            className="flex items-center gap-2 data-[state=active]:bg-white data-[state=active]:text-amber-600"
                                        >
                                            <BookOpen className="h-4 w-4" />
                                            <span className="hidden sm:inline">Vocabulary</span>
                                        </TabsTrigger>
                                        <TabsTrigger
                                            value="fluency"
                                            className="flex items-center gap-2 data-[state=active]:bg-white data-[state=active]:text-blue-600"
                                        >
                                            <Zap className="h-4 w-4" />
                                            <span className="hidden sm:inline">Fluency</span>
                                        </TabsTrigger>
                                        <TabsTrigger
                                            value="pronunciation"
                                            className="flex items-center gap-2 data-[state=active]:bg-white data-[state=active]:text-purple-600"
                                        >
                                            <Mic className="h-4 w-4" />
                                            <span className="hidden sm:inline">Pronunciation</span>
                                        </TabsTrigger>
                                    </TabsList>

                                    <TabsContent value="grammar" className="p-8 space-y-6">
                                        <div className="flex items-center justify-between mb-6">
                                            <h3 className="text-2xl font-bold text-gray-800 flex items-center gap-3">
                                                <Target className="h-6 w-6 text-red-600" />
                                                Grammar & Accuracy
                                            </h3>
                                            <div className="text-3xl font-bold text-red-600">{question.grammarAnswer?.score ?? "-"}</div>
                                        </div>
                                        {renderErrorCorrections(
                                            question.transcript,
                                            question.grammarAnswer,
                                            { ...question.lexicalAnswer, errorText: "" },
                                        )}
                                    </TabsContent>

                                    <TabsContent value="lexical" className="p-8 space-y-6">
                                        <div className="flex items-center justify-between mb-6">
                                            <h3 className="text-2xl font-bold text-gray-800 flex items-center gap-3">
                                                <BookOpen className="h-6 w-6 text-amber-600" />
                                                Lexical Resource
                                            </h3>
                                            <div className="text-3xl font-bold text-amber-600">{question.lexicalAnswer?.score ?? "-"}</div>
                                        </div>
                                        {renderErrorCorrections(
                                            question.transcript,
                                            { ...question.grammarAnswer, errorText: "" },
                                            question.lexicalAnswer,
                                        )}
                                    </TabsContent>

                                    <TabsContent value="fluency" className="p-8 space-y-6">
                                        <div className="flex items-center justify-between mb-6">
                                            <h3 className="text-2xl font-bold text-gray-800 flex items-center gap-3">
                                                <Zap className="h-6 w-6 text-blue-600" />
                                                Fluency & Coherence
                                            </h3>
                                            <div className="text-3xl font-bold text-blue-600">{question.fluencyCohAnswer?.score ?? "-"}</div>
                                        </div>

                                        <div className="bg-blue-50 border border-blue-200 rounded-2xl p-8">
                                            <div className="bg-white rounded-xl p-6 mb-6 border border-blue-200">
                                                <h4 className="font-semibold text-blue-800 mb-3">Examiner Feedback</h4>
                                                <p className="text-gray-700 leading-relaxed text-lg">{question.fluencyCohAnswer?.comment ?? ""}</p>
                                            </div>

                                            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                                                <div className="bg-white rounded-xl p-6 border border-blue-200 text-center">
                                                    <div className="text-3xl font-bold text-blue-600 mb-2">
                                                        {question.fluencyCohAnswer?.speechRate ?? "-"}
                                                    </div>
                                                    <p className="font-medium text-blue-800">Speech Rate</p>
                                                </div>
                                                <div className="bg-white rounded-xl p-6 border border-blue-200 text-center">
                                                    <div className="text-3xl font-bold text-blue-600 mb-2">
                                                        {question.fluencyCohAnswer?.pauseCount ?? "-"}
                                                    </div>
                                                    <p className="font-medium text-blue-800">Pauses</p>
                                                </div>
                                                <div className="bg-white rounded-xl p-6 border border-blue-200 text-center">
                                                    <div className="text-3xl font-bold text-blue-600 mb-2">
                                                        {question.fluencyCohAnswer?.meanIntensity ?? "-"}
                                                    </div>
                                                    <p className="font-medium text-blue-800">Volume</p>
                                                </div>
                                            </div>
                                        </div>
                                    </TabsContent>

                                    <TabsContent value="pronunciation" className="p-8 space-y-6">
                                        <div className="flex items-center justify-between mb-6">
                                            <h3 className="text-2xl font-bold text-gray-800 flex items-center gap-3">
                                                <Mic className="h-6 w-6 text-purple-600" />
                                                Pronunciation Assessment
                                            </h3>
                                            <div className="text-3xl font-bold text-purple-600">{question.pronunciationAnswer?.score ?? "-"}</div>
                                        </div>
                                        {renderPronunciationScript(question.pronunciationAnswer?.pronunciationEvaluation ?? [])}
                                    </TabsContent>
                                </Tabs>
                            </div>
                        </div>
                    ))}
                </div>
            )
        }
    }

    if (loading) {
        return (
            <div className="min-h-screen bg-gray-100 flex items-center justify-center">
                <div className="text-center space-y-6">
                    <div className="animate-spin rounded-full h-20 w-20 border-4 border-green-200 border-t-green-600 mx-auto"></div>
                    <div>
                        <h2 className="text-2xl font-bold text-gray-800 mb-2">Loading Your Results</h2>
                        <p className="text-gray-600">Please wait while we prepare your detailed analysis...</p>
                    </div>
                </div>
            </div>
        )
    }

    if (!data) {
        return (
            <div className="min-h-screen bg-gray-100 flex items-center justify-center">
                <div className="max-w-md mx-auto bg-white rounded-2xl shadow-lg p-12 text-center">
                    <div className="w-20 h-20 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-6">
                        <Mic className="h-10 w-10 text-gray-400" />
                    </div>
                    <h2 className="text-2xl font-bold text-gray-800 mb-2">No Results Found</h2>
                    <p className="text-gray-600">Please check your result ID and try again</p>
                </div>
            </div>
        )
    }

    const overallScore = calculateOverallScore()

    return (
        <div className="min-h-screen bg-gray-100">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
                {/* Header Section - Matching the design */}
                <div className="bg-green-600 rounded-3xl p-12 mb-8 text-white">
                    <div className="text-center mb-8">
                        <p className="text-green-100 text-sm font-medium mb-2 uppercase tracking-wide">FINAL SCORE</p>
                        <h1 className="text-4xl font-bold mb-8">AI Examiner Evaluation</h1>
                    </div>

                    <div className="grid grid-cols-1 gap-6">
                        {/* Overall Score Card */}
                        <div className="bg-green-50 rounded-3xl p-8 text-center">
                            <p className="text-green-600 text-sm font-medium mb-2">Overall Score</p>
                            <div className="text-6xl font-bold text-green-800 mb-2">{overallScore}</div>
                            <p className="text-green-600 text-sm">Weighted Average</p>
                        </div>
                    </div>
                </div>

                {/* Part Navigation - Matching the design */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
                    <button
                        onClick={() => setActivePart("part1")}
                        className={`bg-white rounded-2xl p-6 text-left border-2 transition-all ${
                            activePart === "part1" ? "border-green-500 shadow-lg" : "border-gray-200 hover:border-gray-300"
                        }`}
                    >
                        <div className="flex items-center gap-3 mb-2">
                            <div className="w-8 h-8 bg-green-100 rounded-full flex items-center justify-center">
                                <FileText className="h-4 w-4 text-green-600" />
                            </div>
                            <div>
                                <h3 className="font-bold text-green-600">Part 1</h3>
                                <p className="text-gray-600 text-sm">Introduction & Interview</p>
                            </div>
                        </div>
                        <div className="text-right">
                            <span className="text-2xl font-bold text-green-600">{data.part1.averageScore}</span>
                        </div>
                    </button>

                    <button
                        onClick={() => setActivePart("part2")}
                        className={`bg-white rounded-2xl p-6 text-left border-2 transition-all ${
                            activePart === "part2" ? "border-green-500 shadow-lg" : "border-gray-200 hover:border-gray-300"
                        }`}
                    >
                        <div className="flex items-center gap-3 mb-2">
                            <div className="w-8 h-8 bg-green-100 rounded-full flex items-center justify-center">
                                <FileText className="h-4 w-4 text-green-600" />
                            </div>
                            <div>
                                <h3 className="font-bold text-green-600">Part 2</h3>
                                <p className="text-gray-600 text-sm">Long Turn</p>
                            </div>
                        </div>
                        <div className="text-right">
                            <span className="text-2xl font-bold text-green-600">{data.part2.score}</span>
                        </div>
                    </button>

                    <button
                        onClick={() => setActivePart("part3")}
                        className={`bg-white rounded-2xl p-6 text-left border-2 transition-all ${
                            activePart === "part3" ? "border-green-500 shadow-lg" : "border-gray-200 hover:border-gray-300"
                        }`}
                    >
                        <div className="flex items-center gap-3 mb-2">
                            <div className="w-8 h-8 bg-green-100 rounded-full flex items-center justify-center">
                                <FileText className="h-4 w-4 text-green-600" />
                            </div>
                            <div>
                                <h3 className="font-bold text-green-600">Part 3</h3>
                                <p className="text-gray-600 text-sm">Two-way Discussion</p>
                            </div>
                        </div>
                        <div className="text-right">
                            <span className="text-2xl font-bold text-green-600">{data.part3.averageScore}</span>
                        </div>
                    </button>
                </div>

                {/* Content */}
                <div>
                    {activePart === "part1" && renderPartContent(data.part1, false)}
                    {activePart === "part2" && renderPartContent(data.part2, true)}
                    {activePart === "part3" && renderPartContent(data.part3, false)}
                </div>
            </div>
        </div>
    )
}