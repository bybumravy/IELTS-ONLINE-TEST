"use client"

import { useEffect, useState } from "react"
import { ChevronDown, ChevronUp, Award, FileText, BookOpen, Target, Zap, Mic, Volume2, Play, Pause } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Collapsible, CollapsibleContent, CollapsibleTrigger } from "@/components/ui/collapsible"
import { Badge } from "@/components/ui/badge"
import { useParams } from "react-router-dom"

interface Review {
    scoreEva: string
    reviewEva: string
}

interface ErrorCorrection {
    originalText: string
    correctedText: string
    errorType: string
    explanation: string
    sentenceContext: string
}

interface PronunciationWord {
    word: string
    stress: "primary" | "secondary" | "none"
    phonetic?: string
    isCorrect: boolean
    feedback?: string
}

interface SpeakingEvaluation {
    Grammar: Review & { errorCorrections?: ErrorCorrection[] }
    FluencyCoherence: Review
    LexicalResource: Review & { errorCorrections?: ErrorCorrection[] }
    Pronunciation: Review & {
        script: string
        pronunciationWords: PronunciationWord[]
        overallFeedback: string
    }
}

interface SpeakingPartAnswer {
    id: string
    partNumber: number
    question: string
    audioUrl: string
    transcript: string
    duration: string
    score: string
    evaluation: SpeakingEvaluation
    sampleAnswer: string
}

interface SpeakingAnswer {
    id: string
    username?: string
    testId: string
    part1: SpeakingPartAnswer
    part2: SpeakingPartAnswer
    part3: SpeakingPartAnswer
}

export default function SpeakingResult() {
    const [data, setData] = useState<SpeakingAnswer | null>(null)
    const [loading, setLoading] = useState(true)
    const [activePart, setActivePart] = useState<"part1" | "part2" | "part3">("part1")
    const [openSections, setOpenSections] = useState<{
        question: boolean
        review: boolean
        scoring: boolean
        sample: boolean
    }>({
        question: false,
        review: true,
        scoring: false,
        sample: false,
    })
    const [feedbackView, setFeedbackView] = useState<"grammar" | "lexical" | "fluency" | "pronunciation">("grammar")
    const [isPlaying, setIsPlaying] = useState(false)
    const [currentAudio, setCurrentAudio] = useState<HTMLAudioElement | null>(null)

    const { resultId } = useParams<{ resultId: string }>()

    // Mock data for demonstration
    useEffect(() => {
        // Simulate API call
        setTimeout(() => {
            const mockData: SpeakingAnswer = {
                id: "1",
                testId: "test-123",
                username: "john_doe",
                part1: {
                    id: "p1",
                    partNumber: 1,
                    question: "Let's talk about your hometown. Where are you from?",
                    audioUrl: "/audio/part1.mp3",
                    transcript:
                        "I am from Ho Chi Minh City, which is the largest city in Vietnam. It's a very bustling and vibrant place with lots of opportunities for young people like me.",
                    duration: "2:30",
                    score: "6.5",
                    evaluation: {
                        Grammar: {
                            scoreEva: "6.0",
                            reviewEva: "Generally accurate grammar with some minor errors in complex structures.",
                            errorCorrections: [
                                {
                                    originalText: "lots of opportunities",
                                    correctedText: "many opportunities",
                                    errorType: "Word Choice",
                                    explanation: "In formal contexts, 'many' is preferred over 'lots of'",
                                    sentenceContext:
                                        "It's a very bustling and vibrant place with lots of opportunities for young people like me.",
                                },
                            ],
                        },
                        FluencyCoherence: {
                            scoreEva: "7.0",
                            reviewEva: "Speech is generally fluent with natural pauses. Ideas are well-connected and easy to follow.",
                        },
                        LexicalResource: {
                            scoreEva: "6.5",
                            reviewEva: "Good range of vocabulary with some less common words used appropriately.",
                            errorCorrections: [
                                {
                                    originalText: "bustling",
                                    correctedText: "busy",
                                    errorType: "Word Choice",
                                    explanation: "While 'bustling' is correct, 'busy' might be more natural in this context",
                                    sentenceContext:
                                        "It's a very bustling and vibrant place with lots of opportunities for young people like me.",
                                },
                            ],
                        },
                        Pronunciation: {
                            scoreEva: "6.0",
                            reviewEva: "Generally clear pronunciation with some issues in word stress and intonation.",
                            script:
                                "I am from Ho Chi Minh City, which is the largest city in Vietnam. It's a very bustling and vibrant place with lots of opportunities for young people like me.",
                            pronunciationWords: [
                                { word: "I", stress: "none", isCorrect: true },
                                { word: "am", stress: "none", isCorrect: true },
                                { word: "from", stress: "none", isCorrect: true },
                                { word: "Ho", stress: "primary", isCorrect: true },
                                { word: "Chi", stress: "none", isCorrect: true },
                                { word: "Minh", stress: "none", isCorrect: true },
                                { word: "City", stress: "primary", isCorrect: false, feedback: "Stress should be on first syllable" },
                                { word: "which", stress: "none", isCorrect: true },
                                { word: "is", stress: "none", isCorrect: true },
                                { word: "the", stress: "none", isCorrect: true },
                                { word: "largest", stress: "primary", isCorrect: true },
                                { word: "city", stress: "primary", isCorrect: true },
                                { word: "in", stress: "none", isCorrect: true },
                                {
                                    word: "Vietnam",
                                    stress: "secondary",
                                    isCorrect: false,
                                    feedback: "Primary stress should be on 'nam'",
                                },
                            ],
                            overallFeedback: "Work on word stress patterns, especially in proper nouns and compound words.",
                        },
                    },
                    sampleAnswer:
                        "I'm originally from Manchester, which is a major city in the north of England. It's quite an industrial city, known for its football teams and music scene. What I love most about Manchester is its rich cultural diversity and the friendly nature of the people there.",
                },
                part2: {
                    id: "p2",
                    partNumber: 2,
                    question:
                        "Describe a memorable journey you have taken. You should say: where you went, who you went with, what you did there, and explain why it was memorable.",
                    audioUrl: "/audio/part2.mp3",
                    transcript:
                        "I'd like to talk about a trip I took to Japan last year with my family. We visited Tokyo and Kyoto, and it was absolutely amazing experience.",
                    duration: "2:00",
                    score: "7.0",
                    evaluation: {
                        Grammar: {
                            scoreEva: "7.0",
                            reviewEva: "Good control of grammar with occasional minor errors.",
                            errorCorrections: [
                                {
                                    originalText: "amazing experience",
                                    correctedText: "an amazing experience",
                                    errorType: "Article",
                                    explanation: "Missing article 'an' before 'amazing experience'",
                                    sentenceContext: "We visited Tokyo and Kyoto, and it was absolutely amazing experience.",
                                },
                            ],
                        },
                        FluencyCoherence: {
                            scoreEva: "7.5",
                            reviewEva:
                                "Speaks fluently with only occasional hesitation. Ideas are well-organized and coherently presented.",
                        },
                        LexicalResource: {
                            scoreEva: "7.0",
                            reviewEva: "Good range of vocabulary with some sophisticated words used naturally.",
                        },
                        Pronunciation: {
                            scoreEva: "6.5",
                            reviewEva: "Clear pronunciation with good intonation patterns.",
                            script:
                                "I'd like to talk about a trip I took to Japan last year with my family. We visited Tokyo and Kyoto, and it was absolutely amazing experience.",
                            pronunciationWords: [
                                { word: "I'd", stress: "none", isCorrect: true },
                                { word: "like", stress: "none", isCorrect: true },
                                { word: "to", stress: "none", isCorrect: true },
                                { word: "talk", stress: "none", isCorrect: true },
                                { word: "about", stress: "secondary", isCorrect: true },
                                { word: "a", stress: "none", isCorrect: true },
                                { word: "trip", stress: "none", isCorrect: true },
                                { word: "I", stress: "none", isCorrect: true },
                                { word: "took", stress: "none", isCorrect: true },
                                { word: "to", stress: "none", isCorrect: true },
                                { word: "Japan", stress: "secondary", isCorrect: false, feedback: "Primary stress should be on 'pan'" },
                                { word: "last", stress: "none", isCorrect: true },
                                { word: "year", stress: "none", isCorrect: true },
                            ],
                            overallFeedback: "Good overall pronunciation with minor stress issues in some words.",
                        },
                    },
                    sampleAnswer:
                        "I'd like to describe a fascinating journey I took to Iceland last summer with my best friend. We spent two weeks exploring the dramatic landscapes, including the famous Golden Circle route. What made this trip particularly memorable was witnessing the Northern Lights on our final night - it was absolutely breathtaking and something I'll never forget.",
                },
                part3: {
                    id: "p3",
                    partNumber: 3,
                    question: "How do you think travel has changed in recent years?",
                    audioUrl: "/audio/part3.mp3",
                    transcript:
                        "Well, I think travel has become much more accessible nowadays due to budget airlines and online booking platforms. However, there are also new challenges like overtourism in popular destinations.",
                    duration: "1:45",
                    score: "7.5",
                    evaluation: {
                        Grammar: {
                            scoreEva: "7.5",
                            reviewEva: "Wide range of grammatical structures used accurately with only minor errors.",
                        },
                        FluencyCoherence: {
                            scoreEva: "8.0",
                            reviewEva:
                                "Speaks fluently with natural and appropriate linking. Ideas are well-developed and logically organized.",
                        },
                        LexicalResource: {
                            scoreEva: "7.5",
                            reviewEva: "Wide range of vocabulary used flexibly and precisely with some less common items.",
                        },
                        Pronunciation: {
                            scoreEva: "7.0",
                            reviewEva: "Clear and effective pronunciation with good stress and intonation patterns.",
                            script:
                                "Well, I think travel has become much more accessible nowadays due to budget airlines and online booking platforms. However, there are also new challenges like overtourism in popular destinations.",
                            pronunciationWords: [
                                { word: "Well", stress: "none", isCorrect: true },
                                { word: "I", stress: "none", isCorrect: true },
                                { word: "think", stress: "none", isCorrect: true },
                                { word: "travel", stress: "primary", isCorrect: true },
                                { word: "has", stress: "none", isCorrect: true },
                                { word: "become", stress: "secondary", isCorrect: true },
                                { word: "much", stress: "none", isCorrect: true },
                                { word: "more", stress: "none", isCorrect: true },
                                { word: "accessible", stress: "secondary", isCorrect: true },
                                { word: "nowadays", stress: "primary", isCorrect: true },
                            ],
                            overallFeedback: "Excellent pronunciation with natural stress patterns and clear articulation.",
                        },
                    },
                    sampleAnswer:
                        "I believe travel has undergone significant transformation in recent decades. The advent of low-cost carriers has democratized air travel, making it accessible to a broader demographic. Additionally, digital platforms have revolutionized how we plan and book trips, offering unprecedented convenience and choice. However, this accessibility has also led to challenges such as overtourism, which threatens the sustainability of popular destinations and impacts local communities.",
                },
            }
            setData(mockData)
            setLoading(false)
        }, 1000)
    }, [resultId])

    const getScoreColor = (score: string) => {
        const numScore = Number.parseFloat(score)
        if (numScore >= 7.0) return "text-emerald-600 bg-emerald-100"
        if (numScore >= 6.0) return "text-amber-600 bg-amber-100"
        return "text-red-600 bg-red-100"
    }

    const calculateOverallScore = () => {
        if (!data) return "0.0"
        const part1Score = Number.parseFloat(data.part1.score)
        const part2Score = Number.parseFloat(data.part2.score)
        const part3Score = Number.parseFloat(data.part3.score)
        return ((part1Score + part2Score + part3Score) / 3).toFixed(1)
    }

    const playAudio = (audioUrl: string) => {
        if (currentAudio) {
            currentAudio.pause()
        }

        const audio = new Audio(audioUrl)
        audio.onplay = () => setIsPlaying(true)
        audio.onpause = () => setIsPlaying(false)
        audio.onended = () => setIsPlaying(false)

        setCurrentAudio(audio)
        audio.play()
    }

    const renderErrorCorrections = (originalText: string, corrections: ErrorCorrection[]) => {
        if (!corrections || corrections.length === 0) {
            return (
                <div className="bg-emerald-100 p-4 rounded-xl border border-green-200">
                    <p className="text-green-700 text-sm font-medium">✓ No errors found</p>
                </div>
            )
        }

        const sentences = originalText.match(/[^.!?\n]+[.!?\n]+|[^.!?\n]+$/g) || [originalText]
        const sentenceUsed: Record<number, boolean> = {}
        const highlightedSentences = [...sentences]

        corrections.forEach((correction, idx) => {
            const contextIdx = sentences.findIndex(
                (s, i) => !sentenceUsed[i] && s.trim() === correction.sentenceContext.trim(),
            )
            if (contextIdx === -1) return

            sentenceUsed[contextIdx] = true
            const context = sentences[contextIdx]
            const wordIdx = context.indexOf(correction.originalText)
            if (wordIdx === -1) return

            const before = context.slice(0, wordIdx)
            const errorWord = context.slice(wordIdx, wordIdx + correction.originalText.length)
            const after = context.slice(wordIdx + correction.originalText.length)

            highlightedSentences[contextIdx] = (
                <span key={`sentence-${idx}`}>
          {before}
                    <mark className="bg-red-100 text-red-800 font-medium rounded-md px-2 py-1 cursor-help transition-colors hover:bg-red-200 relative">
            {errorWord}
                        <span className="absolute -top-2 -right-2 bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center font-bold">
              {idx + 1}
            </span>
          </mark>
                    {after}
        </span>
            ) as string
        })

        return (
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                <div className="lg:col-span-2">
                    <div className="whitespace-pre-line p-6 bg-white rounded-xl border border-slate-200 shadow-sm">
                        <div className="leading-relaxed">
                            {highlightedSentences.map((s, i) => (
                                <span key={i}>{s}</span>
                            ))}
                        </div>
                    </div>
                </div>
                <div className="lg:col-span-1 space-y-3">
                    <h4 className="font-semibold text-slate-800 mb-3">Error Details</h4>
                    {corrections.map((error, index) => (
                        <div key={index} className="bg-red-50 border border-red-200 rounded-lg p-4 relative">
                            <div className="absolute -top-2 -left-2 bg-red-500 text-white text-xs rounded-full w-6 h-6 flex items-center justify-center font-bold">
                                {index + 1}
                            </div>
                            <div className="space-y-2 ml-2">
                                <Badge variant="outline" className="text-xs border-red-300 text-red-700 mb-2">
                                    {error.errorType}
                                </Badge>
                                <div className="space-y-1">
                                    <p className="text-sm">
                                        <span className="font-medium text-slate-700">Error:</span>{" "}
                                        <span className="text-red-600 font-medium">{error.originalText}</span>
                                    </p>
                                    <p className="text-sm">
                                        <span className="font-medium text-slate-700">Fix:</span>{" "}
                                        <span className="text-green-600 font-medium">{error.correctedText}</span>
                                    </p>
                                </div>
                                <p className="text-xs text-slate-600 bg-white p-2 rounded border">{error.explanation}</p>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        )
    }

    const renderPronunciationScript = (words: PronunciationWord[]) => {
        return (
            <div className="space-y-4">
                <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
                    <div className="flex flex-wrap gap-1 leading-relaxed">
                        {words.map((wordObj, index) => (
                            <span
                                key={index}
                                className={`inline-block px-1 py-0.5 rounded transition-colors cursor-help ${
                                    !wordObj.isCorrect
                                        ? "bg-red-100 text-red-800 border border-red-200"
                                        : wordObj.stress === "primary"
                                            ? "bg-blue-100 text-blue-800 font-bold"
                                            : wordObj.stress === "secondary"
                                                ? "bg-blue-50 text-blue-700 font-medium"
                                                : "text-slate-700"
                                }`}
                                title={wordObj.feedback || `Stress: ${wordObj.stress}`}
                            >
                {wordObj.word}
                                {wordObj.stress === "primary" && <span className="text-blue-600 ml-0.5">ˈ</span>}
                                {wordObj.stress === "secondary" && <span className="text-blue-500 ml-0.5">ˌ</span>}
              </span>
                        ))}
                    </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <div className="bg-blue-50 p-4 rounded-lg border border-blue-200">
                        <div className="flex items-center gap-2 mb-2">
                            <span className="w-3 h-3 bg-blue-600 rounded"></span>
                            <span className="font-medium text-blue-800">Primary Stress (ˈ)</span>
                        </div>
                        <p className="text-sm text-blue-700">Main stressed syllable</p>
                    </div>
                    <div className="bg-blue-50 p-4 rounded-lg border border-blue-200">
                        <div className="flex items-center gap-2 mb-2">
                            <span className="w-3 h-3 bg-blue-500 rounded"></span>
                            <span className="font-medium text-blue-700">Secondary Stress (ˌ)</span>
                        </div>
                        <p className="text-sm text-blue-600">Weaker stressed syllable</p>
                    </div>
                    <div className="bg-red-50 p-4 rounded-lg border border-red-200">
                        <div className="flex items-center gap-2 mb-2">
                            <span className="w-3 h-3 bg-red-600 rounded"></span>
                            <span className="font-medium text-red-800">Needs Improvement</span>
                        </div>
                        <p className="text-sm text-red-700">Incorrect pronunciation</p>
                    </div>
                </div>
            </div>
        )
    }

    const renderFeedback = (part: SpeakingPartAnswer) => (
        <div className="space-y-6">
            {/* Audio Player */}
            <Card className="bg-gradient-to-r from-purple-50 to-pink-50 border-purple-200">
                <CardContent className="p-6">
                    <div className="flex items-center gap-4">
                        <Button onClick={() => playAudio(part.audioUrl)} className="bg-purple-600 hover:bg-purple-700" size="lg">
                            {isPlaying ? <Pause className="h-5 w-5" /> : <Play className="h-5 w-5" />}
                        </Button>
                        <div>
                            <p className="font-medium text-purple-800">Listen to Recording</p>
                            <p className="text-sm text-purple-600">Duration: {part.duration}</p>
                        </div>
                        <Volume2 className="h-5 w-5 text-purple-600 ml-auto" />
                    </div>
                </CardContent>
            </Card>

            {/* Transcript */}
            <Card className="border-slate-200">
                <CardHeader>
                    <CardTitle className="flex items-center gap-2 text-slate-700">
                        <FileText className="h-5 w-5" />
                        Your Response Transcript
                    </CardTitle>
                </CardHeader>
                <CardContent>
                    <div className="bg-slate-50 p-4 rounded-lg">
                        <p className="text-slate-700 leading-relaxed italic">"{part.transcript}"</p>
                    </div>
                </CardContent>
            </Card>

            {/* Feedback Navigation */}
            <div className="grid grid-cols-2 lg:grid-cols-4 gap-2 bg-slate-100 p-1 rounded-lg">
                <button
                    onClick={() => setFeedbackView("grammar")}
                    className={`py-3 px-4 rounded-md font-medium transition-all duration-200 flex items-center justify-center gap-2 ${
                        feedbackView === "grammar" ? "bg-white text-red-600 shadow-sm" : "text-slate-600 hover:text-slate-800"
                    }`}
                >
                    <Target className="h-4 w-4" />
                    <span className="hidden sm:inline">Grammar</span>
                </button>
                <button
                    onClick={() => setFeedbackView("lexical")}
                    className={`py-3 px-4 rounded-md font-medium transition-all duration-200 flex items-center justify-center gap-2 ${
                        feedbackView === "lexical" ? "bg-white text-amber-600 shadow-sm" : "text-slate-600 hover:text-slate-800"
                    }`}
                >
                    <BookOpen className="h-4 w-4" />
                    <span className="hidden sm:inline">Lexical</span>
                </button>
                <button
                    onClick={() => setFeedbackView("fluency")}
                    className={`py-3 px-4 rounded-md font-medium transition-all duration-200 flex items-center justify-center gap-2 ${
                        feedbackView === "fluency" ? "bg-white text-blue-600 shadow-sm" : "text-slate-600 hover:text-slate-800"
                    }`}
                >
                    <Zap className="h-4 w-4" />
                    <span className="hidden sm:inline">Fluency</span>
                </button>
                <button
                    onClick={() => setFeedbackView("pronunciation")}
                    className={`py-3 px-4 rounded-md font-medium transition-all duration-200 flex items-center justify-center gap-2 ${
                        feedbackView === "pronunciation"
                            ? "bg-white text-purple-600 shadow-sm"
                            : "text-slate-600 hover:text-slate-800"
                    }`}
                >
                    <Mic className="h-4 w-4" />
                    <span className="hidden sm:inline">Pronunciation</span>
                </button>
            </div>

            {/* Content based on selected view */}
            {feedbackView === "grammar" && (
                <div className="space-y-4">
                    <h3 className="text-lg font-semibold text-slate-800 flex items-center gap-2">
                        <Target className="h-5 w-5 text-red-600" />
                        Grammar Analysis
                    </h3>
                    {part.evaluation.Grammar.errorCorrections && part.evaluation.Grammar.errorCorrections.length > 0 ? (
                        renderErrorCorrections(part.transcript, part.evaluation.Grammar.errorCorrections)
                    ) : (
                        <div className="bg-green-50 p-6 rounded-xl border border-green-200 text-center">
                            <Target className="h-8 w-8 mx-auto mb-2 text-green-600" />
                            <p className="text-green-700 font-medium">✓ No grammar errors found</p>
                        </div>
                    )}
                </div>
            )}

            {feedbackView === "lexical" && (
                <div className="space-y-4">
                    <h3 className="text-lg font-semibold text-slate-800 flex items-center gap-2">
                        <BookOpen className="h-5 w-5 text-amber-600" />
                        Lexical Resource Analysis
                    </h3>
                    {part.evaluation.LexicalResource.errorCorrections &&
                    part.evaluation.LexicalResource.errorCorrections.length > 0 ? (
                        renderErrorCorrections(part.transcript, part.evaluation.LexicalResource.errorCorrections)
                    ) : (
                        <div className="bg-green-50 p-6 rounded-xl border border-green-200 text-center">
                            <BookOpen className="h-8 w-8 mx-auto mb-2 text-green-600" />
                            <p className="text-green-700 font-medium">✓ Good lexical resource usage</p>
                        </div>
                    )}
                </div>
            )}

            {feedbackView === "fluency" && (
                <div className="space-y-4">
                    <h3 className="text-lg font-semibold text-slate-800 flex items-center gap-2">
                        <Zap className="h-5 w-5 text-blue-600" />
                        Fluency & Coherence
                    </h3>
                    <Card className="border-blue-200 bg-blue-50/50">
                        <CardContent className="p-6">
                            <p className="text-slate-700 leading-relaxed">{part.evaluation.FluencyCoherence.reviewEva}</p>
                        </CardContent>
                    </Card>
                </div>
            )}

            {feedbackView === "pronunciation" && (
                <div className="space-y-4">
                    <h3 className="text-lg font-semibold text-slate-800 flex items-center gap-2">
                        <Mic className="h-5 w-5 text-purple-600" />
                        Pronunciation Analysis
                    </h3>
                    {renderPronunciationScript(part.evaluation.Pronunciation.pronunciationWords)}
                    <Card className="border-purple-200 bg-purple-50/50">
                        <CardContent className="p-6">
                            <p className="text-slate-700 leading-relaxed">{part.evaluation.Pronunciation.overallFeedback}</p>
                        </CardContent>
                    </Card>
                </div>
            )}
        </div>
    )

    const renderPartContent = (part: SpeakingPartAnswer) => (
        <Card className="overflow-hidden shadow-lg border-0">
            {/* Question Section */}
            <Collapsible
                open={openSections.question}
                onOpenChange={(v) => setOpenSections((prev) => ({ ...prev, question: v }))}
            >
                <CollapsibleTrigger asChild>
                    <Button
                        variant="ghost"
                        className="w-full justify-between p-6 h-auto bg-slate-50 hover:bg-slate-100 border-b border-slate-200 rounded-none"
                    >
                        <div className="flex items-center gap-3">
                            <BookOpen className="h-5 w-5 text-slate-600" />
                            <span className="font-semibold text-slate-700">Question</span>
                        </div>
                        {openSections.question ? <ChevronUp className="h-5 w-5" /> : <ChevronDown className="h-5 w-5" />}
                    </Button>
                </CollapsibleTrigger>
                <CollapsibleContent>
                    <div className="p-6 bg-white border-b border-slate-200">
                        <p className="text-slate-700 leading-relaxed font-medium">{part.question}</p>
                    </div>
                </CollapsibleContent>
            </Collapsible>

            {/* Review Section */}
            <Collapsible open={openSections.review} onOpenChange={(v) => setOpenSections((prev) => ({ ...prev, review: v }))}>
                <CollapsibleTrigger asChild>
                    <Button
                        variant="ghost"
                        className="w-full justify-between p-6 h-auto bg-emerald-600 hover:bg-emerald-700 text-white rounded-none"
                    >
                        <div className="flex items-center gap-3">
                            <Mic className="h-5 w-5" />
                            <span className="font-semibold">Detailed Analysis & Feedback</span>
                        </div>
                        {openSections.review ? <ChevronUp className="h-5 w-5" /> : <ChevronDown className="h-5 w-5" />}
                    </Button>
                </CollapsibleTrigger>
                <CollapsibleContent>
                    <div className="p-6 bg-gradient-to-br from-orange-50 to-amber-50">{renderFeedback(part)}</div>
                </CollapsibleContent>
            </Collapsible>

            {/* Scoring Breakdown */}
            <Collapsible
                open={openSections.scoring}
                onOpenChange={(v) => setOpenSections((prev) => ({ ...prev, scoring: v }))}
            >
                <CollapsibleTrigger asChild>
                    <Button
                        variant="ghost"
                        className="w-full justify-between p-6 h-auto bg-slate-50 hover:bg-slate-100 border-b border-slate-200 rounded-none"
                    >
                        <div className="flex items-center gap-3">
                            <Award className="h-5 w-5 text-slate-600" />
                            <span className="font-semibold text-slate-700">Scoring Breakdown</span>
                        </div>
                        {openSections.scoring ? <ChevronUp className="h-5 w-5" /> : <ChevronDown className="h-5 w-5" />}
                    </Button>
                </CollapsibleTrigger>
                <CollapsibleContent>
                    <div className="p-6 bg-white border-b border-slate-200">
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                            <div className="space-y-4">
                                {/* Grammar */}
                                <div className="p-3 bg-slate-50 rounded-lg space-y-2">
                                    <div className="flex justify-between items-center">
                                        <span className="font-medium text-slate-700">Grammar</span>
                                        <Badge className={getScoreColor(part.evaluation.Grammar.scoreEva)}>
                                            {part.evaluation.Grammar.scoreEva}
                                        </Badge>
                                    </div>
                                    <p className="text-sm text-slate-600 bg-white p-2 rounded border">
                                        {part.evaluation.Grammar.reviewEva}
                                    </p>
                                </div>
                                {/* Fluency & Coherence */}
                                <div className="p-3 bg-slate-50 rounded-lg space-y-2">
                                    <div className="flex justify-between items-center">
                                        <span className="font-medium text-slate-700">Fluency & Coherence</span>
                                        <Badge className={getScoreColor(part.evaluation.FluencyCoherence.scoreEva)}>
                                            {part.evaluation.FluencyCoherence.scoreEva}
                                        </Badge>
                                    </div>
                                    <p className="text-sm text-slate-600 bg-white p-2 rounded border">
                                        {part.evaluation.FluencyCoherence.reviewEva}
                                    </p>
                                </div>
                            </div>
                            <div className="space-y-4">
                                {/* Lexical Resource */}
                                <div className="p-3 bg-slate-50 rounded-lg space-y-2">
                                    <div className="flex justify-between items-center">
                                        <span className="font-medium text-slate-700">Lexical Resource</span>
                                        <Badge className={getScoreColor(part.evaluation.LexicalResource.scoreEva)}>
                                            {part.evaluation.LexicalResource.scoreEva}
                                        </Badge>
                                    </div>
                                    <p className="text-sm text-slate-600 bg-white p-2 rounded border">
                                        {part.evaluation.LexicalResource.reviewEva}
                                    </p>
                                </div>
                                {/* Pronunciation */}
                                <div className="p-3 bg-slate-50 rounded-lg space-y-2">
                                    <div className="flex justify-between items-center">
                                        <span className="font-medium text-slate-700">Pronunciation</span>
                                        <Badge className={getScoreColor(part.evaluation.Pronunciation.scoreEva)}>
                                            {part.evaluation.Pronunciation.scoreEva}
                                        </Badge>
                                    </div>
                                    <p className="text-sm text-slate-600 bg-white p-2 rounded border">
                                        {part.evaluation.Pronunciation.reviewEva}
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>
                </CollapsibleContent>
            </Collapsible>

            {/* Sample Answer */}
            <Collapsible open={openSections.sample} onOpenChange={(v) => setOpenSections((prev) => ({ ...prev, sample: v }))}>
                <CollapsibleTrigger asChild>
                    <Button
                        variant="ghost"
                        className="w-full justify-between p-6 h-auto bg-slate-50 hover:bg-slate-100 rounded-none rounded-b-lg"
                    >
                        <div className="flex items-center gap-3">
                            <BookOpen className="h-5 w-5 text-slate-600" />
                            <span className="font-semibold text-slate-700">Sample Answer</span>
                        </div>
                        {openSections.sample ? <ChevronUp className="h-5 w-5" /> : <ChevronDown className="h-5 w-5" />}
                    </Button>
                </CollapsibleTrigger>
                <CollapsibleContent>
                    <div className="p-6 bg-white">
                        <div className="bg-emerald-50 p-6 rounded-xl border border-emerald-200 shadow-sm">
                            <p className="text-slate-700 leading-relaxed italic">"{part.sampleAnswer}"</p>
                        </div>
                    </div>
                </CollapsibleContent>
            </Collapsible>
        </Card>
    )

    if (loading) {
        return (
            <div className="min-h-screen bg-gradient-to-br from-slate-50 to-blue-50 flex items-center justify-center">
                <div className="text-center space-y-4">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
                    <p className="text-slate-600 font-medium">Loading your results...</p>
                </div>
            </div>
        )
    }

    if (!data) {
        return (
            <div className="min-h-screen bg-gradient-to-br from-slate-50 to-blue-50 flex items-center justify-center">
                <Card className="max-w-md mx-auto">
                    <CardContent className="p-8 text-center">
                        <Mic className="h-12 w-12 text-slate-400 mx-auto mb-4" />
                        <p className="text-slate-600 font-medium">No results found</p>
                    </CardContent>
                </Card>
            </div>
        )
    }

    const overallScore = calculateOverallScore()

    return (
        <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
                {/* Score Overview */}
                <Card className="mb-8 overflow-hidden shadow-2xl border-0 bg-gradient-to-r from-emerald-600 to-emerald-700">
                    <div className="bg-gradient-to-r from-emerald-600 to-emerald-700 text-white p-8">
                        <div className="text-center mb-8">
                            <div className="text-sm text-blue-100 uppercase tracking-wide font-medium">Final Score</div>
                            <div className="text-3xl font-bold mt-2">IELTS Speaking Results</div>
                        </div>
                        <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
                            <Card className="bg-lime-50 border-white/20">
                                <CardContent className="p-6 text-center">
                                    <div className="text-sm text-emerald-600 mb-2">Overall Score</div>
                                    <div className="text-5xl font-bold text-emerald-900 mb-2">{overallScore}</div>
                                    <div className="text-xs text-emerald-500">Average Score</div>
                                </CardContent>
                            </Card>
                            <Card className="bg-lime-50 border-white/20">
                                <CardContent className="p-6 text-center">
                                    <div className="text-sm text-emerald-600 mb-2">Part 1</div>
                                    <div className="text-5xl font-bold text-emerald-900 mb-2">{data.part1.score}</div>
                                    <div className="text-xs text-emerald-500">Introduction</div>
                                </CardContent>
                            </Card>
                            <Card className="bg-lime-50 border-white/20">
                                <CardContent className="p-6 text-center">
                                    <div className="text-sm text-emerald-600 mb-2">Part 2</div>
                                    <div className="text-5xl font-bold text-emerald-900 mb-2">{data.part2.score}</div>
                                    <div className="text-xs text-emerald-500">Long Turn</div>
                                </CardContent>
                            </Card>
                            <Card className="bg-lime-50 border-white/20">
                                <CardContent className="p-6 text-center">
                                    <div className="text-sm text-emerald-600 mb-2">Part 3</div>
                                    <div className="text-5xl font-bold text-emerald-900 mb-2">{data.part3.score}</div>
                                    <div className="text-xs text-emerald-500">Discussion</div>
                                </CardContent>
                            </Card>
                        </div>
                    </div>
                </Card>

                {/* Part Tabs */}
                <div className="mb-8">
                    <div className="flex space-x-1 bg-slate-100 p-1 rounded-xl">
                        <button
                            onClick={() => setActivePart("part1")}
                            className={`flex-1 py-4 px-6 rounded-lg font-semibold transition-all duration-200 ${
                                activePart === "part1"
                                    ? "bg-white text-emerald-600 shadow-md"
                                    : "text-emerald-700 hover:text-emerald-900"
                            }`}
                        >
                            <div className="flex items-center justify-center gap-3">
                                <Mic className="h-5 w-5" />
                                <div>
                                    <div className="text-lg">Part 1</div>
                                    <div className="text-sm opacity-75">Introduction</div>
                                </div>
                                <div className={`px-3 py-1 rounded-full text-sm font-bold ${getScoreColor(data.part1.score)}`}>
                                    {data.part1.score}
                                </div>
                            </div>
                        </button>
                        <button
                            onClick={() => setActivePart("part2")}
                            className={`flex-1 py-4 px-6 rounded-lg font-semibold transition-all duration-200 ${
                                activePart === "part2"
                                    ? "bg-white text-emerald-600 shadow-md"
                                    : "text-emerald-700 hover:text-emerald-900"
                            }`}
                        >
                            <div className="flex items-center justify-center gap-3">
                                <Mic className="h-5 w-5" />
                                <div>
                                    <div className="text-lg">Part 2</div>
                                    <div className="text-sm opacity-75">Long Turn</div>
                                </div>
                                <div className={`px-3 py-1 rounded-full text-sm font-bold ${getScoreColor(data.part2.score)}`}>
                                    {data.part2.score}
                                </div>
                            </div>
                        </button>
                        <button
                            onClick={() => setActivePart("part3")}
                            className={`flex-1 py-4 px-6 rounded-lg font-semibold transition-all duration-200 ${
                                activePart === "part3"
                                    ? "bg-white text-emerald-600 shadow-md"
                                    : "text-emerald-700 hover:text-emerald-900"
                            }`}
                        >
                            <div className="flex items-center justify-center gap-3">
                                <Mic className="h-5 w-5" />
                                <div>
                                    <div className="text-lg">Part 3</div>
                                    <div className="text-sm opacity-75">Discussion</div>
                                </div>
                                <div className={`px-3 py-1 rounded-full text-sm font-bold ${getScoreColor(data.part3.score)}`}>
                                    {data.part3.score}
                                </div>
                            </div>
                        </button>
                    </div>
                </div>

                {/* Active Part Content */}
                <div className="space-y-6">
                    {renderPartContent(activePart === "part1" ? data.part1 : activePart === "part2" ? data.part2 : data.part3)}
                </div>
            </div>
        </div>
    )
}
