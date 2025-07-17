"use client"

import {useEffect, useState} from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Textarea } from "@/components/ui/textarea"
import { Badge } from "@/components/ui/badge"
import { Separator } from "@/components/ui/separator"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Input } from "@/components/ui/input"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import {
    ChevronLeft,
    ChevronRight,
    Save,
    Send,
    Clock,
    User,
    FileText,
    Edit3,
    AlertCircle,
    CheckCircle,
    X,
    Type,
    RefreshCw,
} from "lucide-react"
interface WritingAnswer {
    _id: string;
    username: string;
    testId: string;
    task1: WritingTask;
    task2: WritingTask;
    _class: string; // lớp Java backend map
}
interface WritingTask {
    type: "Task 1" | "Task 2";
    question: string;
    imageUrl?: string;
    answer: string;
    wordCount: string;
    score: string;
    sampleAnswer: string;
    feedback: WritingFeedback;
}
interface WritingFeedback {
    errorCorrections: ErrorCorrection[];
    sentenceImprovements: SentenceImprovement[];
    overallComment: string;
    evaluation: WritingEvaluation;
}
interface ErrorCorrection {
    originalText: string;
    correctedText: string;
    errorType: string; // e.g., "word choice"
    explanation: string;
    sentenceContext: string;
}

interface SentenceImprovement {
    originalSentence: string;
    improvedSentence: string;
    techniquesUsed: string[]; // e.g., ["enhanced vocabulary", "complex sentence structure"]

}

interface WritingEvaluation {
    TaskAchievement: ScoreWithReview;
    CoherenceCohesion: ScoreWithReview;
    LexicalResource: ScoreWithReview;
    Grammar: ScoreWithReview;
}

interface ScoreWithReview {
    scoreEva: string;     // e.g., "8"
    reviewEva: string;    // e.g., "The essay uses a sufficient range of vocabulary..."
}
interface ErrorAnnotation {
    id: string
    wordIndex: number
    type: "grammar" | "vocabulary" | "spelling" | "punctuation" | "style"
    original: string
    correction: string
    comment: string
    sentenceContext : string
}

interface SentenceCorrection {
    id: string
    sentenceIndex: number
    type: "structure" | "clarity" | "coherence" | "conciseness" | "academic_tone"
    original: string
    correction: string
    comment: string

}

const API_URL = import.meta.env.VITE_API_URL;

export default function Component() {

    const [taskData, setTaskData] = useState(null)
    const [selectedTask, setSelectedTask] = useState<"task1" | "task2">("task1")

    useEffect(() => {
        fetch(`http://localhost:8080/verify/writingbyteacher/685f84953e1264c819a3630f`, {
            credentials: "include",
        })
            .then((res) => res.json())
            .then((data) => {
                console.log("✅ WritingAnswer by ID:");
                console.log(JSON.stringify(data, null, 2));
                setTaskData(data)
            })
            .catch((err) => console.error("❌ Error fetching writing data:", err));
    }, []);
    const [allScores, setAllScores] = useState<{
        task1: {
            taskResponse: string;
            coherenceCohesion: string;
            lexicalResource: string;
            grammaticalRange: string;
        };
        task2: {
            taskResponse: string;
            coherenceCohesion: string;
            lexicalResource: string;
            grammaticalRange: string;
        };
    }>({
        task1: {
            taskResponse: "",
            coherenceCohesion: "",
            lexicalResource: "",
            grammaticalRange: "",
        },
        task2: {
            taskResponse: "",
            coherenceCohesion: "",
            lexicalResource: "",
            grammaticalRange: "",
        },
    });
    const scores = allScores[selectedTask];
    console.log(scores)
    const [allComments, setAllComments] = useState<{
        task1: CommentSet,
        task2: CommentSet
    }>({
        task1: {
            taskResponse: "",
            coherenceCohesion: "",
            lexicalResource: "",
            grammaticalRange: "",
            overall: "",
        },
        task2: {
            taskResponse: "",
            coherenceCohesion: "",
            lexicalResource: "",
            grammaticalRange: "",
            overall: "",
        }
    })
    type CommentSet = {
        taskResponse: string
        coherenceCohesion: string
        lexicalResource: string
        grammaticalRange: string
        overall: string
    }
    const comments = allComments[selectedTask]
    const [allErrors, setAllErrors] = useState<{
        task1: ErrorAnnotation[],
        task2: ErrorAnnotation[]
    }>({
        task1: [],
        task2: [],
    })
    const convertAnnotationToCorrection = (annotation: ErrorAnnotation): ErrorCorrection => ({
        originalText: annotation.original,
        correctedText: annotation.correction,
        errorType: annotation.type,
        explanation: annotation.comment,
        sentenceContext: annotation.sentenceContext,
    });
    const convertSentenceCorrection = (correction: SentenceCorrection): SentenceImprovement => ({
        originalSentence: correction.original,
        improvedSentence: correction.correction,
        techniquesUsed: [correction.type]// hoặc phân tích thêm từ comment
      // bạn có thể cho người dùng nhập hoặc tính tự động
    });
    const errors = allErrors[selectedTask]



    const [allSentenceCorrections, setAllSentenceCorrections] = useState<{
        task1: SentenceCorrection[],
        task2: SentenceCorrection[]
    }>({
        task1: [],
        task2: [],
    })
    const sentenceCorrections = allSentenceCorrections[selectedTask]


    const [isEditMode, setIsEditMode] = useState(false)
    const [isSentenceMode, setIsSentenceMode] = useState(false)
    const [newError, setNewError] = useState({
        type: "grammar" as const,
        original: "",
        correction: "",
        comment: "",
        sentenceContext: ""
    })

    const [newSentenceCorrection, setNewSentenceCorrection] = useState({
        type: "structure" as const,
        original: "",
        correction: "",
        comment: "",
    })

    const calculateOverallScore = () => {
        const scoreValues = Object.values(scores)
            .filter((score) => score !== "")
            .map(Number)
        if (scoreValues.length === 4) {
            return (scoreValues.reduce((a, b) => a + b, 0) / 4).toFixed(1)
        }
        return "N/A"
    }

    const sampleEssay = taskData?.[selectedTask]?.answer ?? ""

    const sentences = sampleEssay.split(/(?<=[.!?])\s+/).filter((s) => s.trim().length > 0)
    const words = sampleEssay.split(/(\s+)/)

    const getErrorTypeColor = (type: string) => {
        switch (type) {
            case "grammar":
                return "bg-red-100 border-red-300 text-red-800"
            case "vocabulary":
                return "bg-blue-100 border-blue-300 text-blue-800"
            case "spelling":
                return "bg-yellow-100 border-yellow-300 text-yellow-800"
            case "punctuation":
                return "bg-purple-100 border-purple-300 text-purple-800"
            case "style":
                return "bg-green-100 border-green-300 text-green-800"
            default:
                return "bg-gray-100 border-gray-300 text-gray-800"
        }
    }

    const getSentenceTypeColor = (type: string) => {
        switch (type) {
            case "structure":
                return "bg-orange-100 border-orange-300 text-orange-800"
            case "clarity":
                return "bg-cyan-100 border-cyan-300 text-cyan-800"
            case "coherence":
                return "bg-indigo-100 border-indigo-300 text-indigo-800"
            case "conciseness":
                return "bg-pink-100 border-pink-300 text-pink-800"
            case "academic_tone":
                return "bg-emerald-100 border-emerald-300 text-emerald-800"
            default:
                return "bg-gray-100 border-gray-300 text-gray-800"
        }
    }

    const getErrorTypeIcon = (type: string) => {
        switch (type) {
            case "grammar":
                return "📝"
            case "vocabulary":
                return "📚"
            case "spelling":
                return "✏️"
            case "punctuation":
                return "❗"
            case "style":
                return "🎨"
            default:
                return "❓"
        }
    }

    const getSentenceTypeIcon = (type: string) => {
        switch (type) {
            case "structure":
                return "🏗️"
            case "clarity":
                return "💡"
            case "coherence":
                return "🔗"
            case "conciseness":
                return "✂️"
            case "academic_tone":
                return "🎓"
            default:
                return "📄"
        }
    }
    const getSentenceContext = (wordIndex: number, words: string[]): string => {
        const originalText = words.join(" ");
        const sentenceRegex = /[^.!?]+[.!?]/g;
        const sentences = originalText.match(sentenceRegex) || [];

        let runningIndex = 0;

        for (let i = 0; i < sentences.length; i++) {
            const sentence = sentences[i];
            const sentenceWords = sentence.trim().split(/\s+/);
            const sentenceStart = runningIndex;
            const sentenceEnd = runningIndex + sentenceWords.length - 1;

            console.log(`📚 Sentence ${i + 1}: "${sentence.trim()}"`);
            console.log(`➡️ Word range: ${sentenceStart} - ${sentenceEnd} | Looking for: ${wordIndex}`);

            if (wordIndex >= sentenceStart && wordIndex <= sentenceEnd) {
                return sentence.trim();
            }

            runningIndex += sentenceWords.length;
        }

        console.warn("⚠️ Không tìm thấy câu chứa từ index", wordIndex);
        return "";
    };
    const addError = (wordIndex: number, word: string) => {
        const sentenceContext = getSentenceContext(wordIndex, words);

        const error: ErrorAnnotation = {
            id: Date.now().toString(),
            wordIndex,
            type: newError.type,
            original: word,
            correction: newError.correction,
            comment: newError.comment,
            sentenceContext // ✅ GÁN vào đây
        };

        const currentErrors = allErrors[selectedTask] || [];

        setAllErrors({
            ...allErrors,
            [selectedTask]: [...currentErrors, error]
        });

        setNewError({
            type: "grammar",
            original: "",
            correction: "",
            comment: "",
            sentenceContext: ""
        });
    };


    const addSentenceCorrection = (sentenceIndex: number, sentence: string) => {
        const correction: SentenceCorrection = {
            id: Date.now().toString(),
            sentenceIndex,
            type: newSentenceCorrection.type,
            original: sentence,
            correction: newSentenceCorrection.correction,
            comment: newSentenceCorrection.comment,
        }

        setAllSentenceCorrections(prev => ({
            ...prev,
            [selectedTask]: [...prev[selectedTask], correction]
        }))

        setNewSentenceCorrection({
            type: "structure",
            original: "",
            correction: "",
            comment: "",
        })
    }
    const removeError = (errorId: string) => {
        setAllErrors(prev => ({
            ...prev,
            [selectedTask]: prev[selectedTask].filter(e => e.id !== errorId)
        }))
    }

    const removeSentenceCorrection = (correctionId: string) => {
        setAllSentenceCorrections(prev => {
            const updated = {
                ...prev,
                [selectedTask]: prev[selectedTask].filter((c) => c.id !== correctionId)
            }
            console.log("❌ Removed sentence correction:", correctionId, "from", selectedTask)
            return updated
        })
    }

    const getErrorStats = () => {
        const stats = errors.reduce(
            (acc, error) => {
                acc[error.type] = (acc[error.type] || 0) + 1
                return acc
            },
            {} as Record<string, number>,
        )
        return stats
    }

    const getSentenceStats = () => {
        const stats = sentenceCorrections.reduce(
            (acc, correction) => {
                acc[correction.type] = (acc[correction.type] || 0) + 1
                return acc
            },
            {} as Record<string, number>,
        )
        return stats
    }
    const updateComment = (field: keyof CommentSet, value: string) => {
        setAllComments(prev => ({
            ...prev,
            [selectedTask]: {
                ...prev[selectedTask],
                [field]: value
            }
        }))
    }
    return (

        <div className="min-h-screen bg-gray-50 p-4">
            {taskData?.[selectedTask]?.imageUrl && (
                <div className="mb-4">
                    <img
                        src={taskData[selectedTask].imageUrl}
                        alt={`Đề bài ${selectedTask.toUpperCase()}`}
                        className="w-[400px] mx-auto border rounded shadow"
                    />
                </div>
            )}
            <div className="mx-auto max-w-7xl">
                {/* Header */}
                <div className="mb-6 flex items-center justify-between">
                    <div>
                        <h1 className="text-2xl font-bold text-gray-900">Chấm bài Writing IELTS</h1>
                        <p className="text-gray-600">
                            {selectedTask === "task1" ? "Task 1 - Academic Writing" : "Task 2 - Essay Writing"}
                        </p>
                    </div>
                    <div className="flex items-center gap-4">
                        <Badge variant="outline" className="flex items-center gap-2">
                            <User className="h-4 w-4" />
                            {taskData?.username}
                        </Badge>

                    </div>
                </div>

                <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                    {/* Left Column - Essay Content */}
                    <div className="space-y-6">
                        <Card>
                            <CardHeader>
                                <CardTitle className="flex items-center gap-2">
                                    <FileText className="h-5 w-5" />
                                    Task Prompt
                                </CardTitle>
                            </CardHeader>
                            <CardContent>
                                <div className="rounded-lg bg-blue-50 p-4">
                                    {taskData?.[selectedTask]?.question && (
                                        <p className="text-sm text-gray-700 whitespace-pre-line">
                                            <strong>{selectedTask === "task1" ? "Task 1:" : "Task 2:"}</strong> {taskData[selectedTask].question}
                                        </p>
                                    )}
                                </div>
                            </CardContent>
                        </Card>

                        <Card>
                            <CardHeader>
                                <div className="flex items-center justify-between">
                                    <CardTitle>Student's Writing</CardTitle>
                                    <div className="flex gap-2">
                                        <Button
                                            variant={isEditMode ? "default" : "outline"}
                                            size="sm"
                                            onClick={() => {
                                                setIsEditMode(!isEditMode)
                                                setIsSentenceMode(false)
                                            }}
                                        >
                                            <Edit3 className="mr-2 h-4 w-4" />
                                            {isEditMode ? "Xong" : "Sửa từ"}
                                        </Button>
                                        <Button
                                            variant={isSentenceMode ? "default" : "outline"}
                                            size="sm"
                                            onClick={() => {
                                                setIsSentenceMode(!isSentenceMode)
                                                setIsEditMode(false)
                                            }}
                                        >
                                            <Type className="mr-2 h-4 w-4" />
                                            {isSentenceMode ? "Xong" : "Sửa câu"}
                                        </Button>
                                    </div>
                                </div>
                                <div className="flex items-center gap-2 text-sm text-gray-600">
                                    <span>  Số từ: {taskData?.[selectedTask]?.wordCount || 0}</span>
                                    <Separator orientation="vertical" className="h-4" />

                                    <span className="text-red-600">Lỗi từ: {errors.length}</span>
                                    <Separator orientation="vertical" className="h-4" />
                                    <span className="text-orange-600">Sửa câu: {sentenceCorrections.length}</span>
                                </div>
                            </CardHeader>
                            <CardContent>
                                <Tabs defaultValue="essay" className="w-full">
                                    <TabsList className="grid w-full grid-cols-3">
                                        <TabsTrigger value="essay">Essay</TabsTrigger>
                                        <TabsTrigger value="errors">Error word ({errors.length})</TabsTrigger>
                                        <TabsTrigger value="sentences">Fix sentence ({sentenceCorrections.length})</TabsTrigger>
                                    </TabsList>

                                    <TabsContent value="essay" className="mt-4">
                                        <div className="rounded-lg border bg-white p-4">
                                            {isSentenceMode ? (
                                                // Sentence editing mode
                                                <div className="space-y-4">
                                                    {sentences.map((sentence, index) => {
                                                        const correction = sentenceCorrections.find((c) => c.sentenceIndex === index)

                                                        return (
                                                            <div key={index} className="border-l-4 border-gray-200 pl-4">
                                                                <Dialog>
                                                                    <DialogTrigger asChild>
                                                                        <div
                                                                            className={`cursor-pointer p-3 rounded-lg transition-colors ${
                                                                                correction
                                                                                    ? `${getSentenceTypeColor(correction.type)} border-2`
                                                                                    : "hover:bg-gray-50 border border-dashed border-gray-300"
                                                                            }`}
                                                                        >
                                                                            <div className="text-sm leading-relaxed">
                                                                                {correction ? (
                                                                                    <div className="space-y-2">
                                                                                        <div className="flex items-center gap-2 text-xs font-medium">
                                                                                            <span>{getSentenceTypeIcon(correction.type)}</span>
                                                                                            <span className="capitalize">{correction.type}</span>
                                                                                        </div>
                                                                                        <div className="line-through text-gray-500">{sentence}</div>
                                                                                        <div className="text-green-700 font-medium">{correction.correction}</div>
                                                                                    </div>
                                                                                ) : (
                                                                                    sentence
                                                                                )}
                                                                            </div>
                                                                        </div>
                                                                    </DialogTrigger>
                                                                    <DialogContent className="max-w-2xl">
                                                                        <DialogHeader>
                                                                            <DialogTitle>{correction ? "Chỉnh sửa câu" : "Thêm sửa câu"}</DialogTitle>
                                                                        </DialogHeader>
                                                                        <div className="space-y-4">
                                                                            <div>
                                                                                <label className="text-sm font-medium">Câu gốc:</label>
                                                                                <div className="mt-1 p-3 bg-gray-50 rounded text-sm">{sentence}</div>
                                                                            </div>

                                                                            {correction ? (
                                                                                <div className="space-y-4">
                                                                                    <div>
                                                                                        <label className="text-sm font-medium">Loại sửa:</label>
                                                                                        <p className="text-sm capitalize flex items-center gap-2">
                                                                                            <span>{getSentenceTypeIcon(correction.type)}</span>
                                                                                            {correction.type}
                                                                                        </p>
                                                                                    </div>
                                                                                    <div>
                                                                                        <label className="text-sm font-medium">Câu đã sửa:</label>
                                                                                        <div className="mt-1 p-3 bg-green-50 rounded text-sm">
                                                                                            {correction.correction}
                                                                                        </div>
                                                                                    </div>
                                                                                    <div>
                                                                                        <label className="text-sm font-medium">Giải thích:</label>
                                                                                        <p className="text-sm text-gray-700">{correction.comment}</p>
                                                                                    </div>
                                                                                    <Button
                                                                                        variant="destructive"
                                                                                        size="sm"
                                                                                        onClick={() => removeSentenceCorrection(correction!.id)}
                                                                                    >
                                                                                        <X className="mr-2 h-4 w-4" />
                                                                                        Xóa sửa câu
                                                                                    </Button>
                                                                                </div>
                                                                            ) : (
                                                                                <div className="space-y-4">
                                                                                    <div>
                                                                                        <label className="text-sm font-medium">Loại sửa:</label>
                                                                                        <Select
                                                                                            value={newSentenceCorrection.type}
                                                                                            onValueChange={(value: any) =>
                                                                                                setNewSentenceCorrection({ ...newSentenceCorrection, type: value })
                                                                                            }
                                                                                        >
                                                                                            <SelectTrigger>
                                                                                                <SelectValue />
                                                                                            </SelectTrigger>
                                                                                            <SelectContent>
                                                                                                <SelectItem value="structure">🏗️ Structure (Cấu trúc)</SelectItem>
                                                                                                <SelectItem value="clarity">💡 Clarity (Rõ ràng)</SelectItem>
                                                                                                <SelectItem value="coherence">🔗 Coherence (Mạch lạc)</SelectItem>
                                                                                                <SelectItem value="conciseness">✂️ Conciseness (Súc tích)</SelectItem>
                                                                                                <SelectItem value="academic_tone">
                                                                                                    🎓 Academic Tone (Giọng điệu học thuật)
                                                                                                </SelectItem>
                                                                                            </SelectContent>
                                                                                        </Select>
                                                                                    </div>
                                                                                    <div>
                                                                                        <label className="text-sm font-medium">Câu sửa:</label>
                                                                                        <Textarea
                                                                                            value={newSentenceCorrection.correction}
                                                                                            onChange={(e) =>
                                                                                                setNewSentenceCorrection({
                                                                                                    ...newSentenceCorrection,
                                                                                                    correction: e.target.value,
                                                                                                })
                                                                                            }
                                                                                            placeholder="Viết lại câu đã sửa..."
                                                                                            className="min-h-[80px]"
                                                                                        />
                                                                                    </div>
                                                                                    <div>
                                                                                        <label className="text-sm font-medium">Giải thích:</label>
                                                                                        <Textarea
                                                                                            value={newSentenceCorrection.comment}
                                                                                            onChange={(e) =>
                                                                                                setNewSentenceCorrection({
                                                                                                    ...newSentenceCorrection,
                                                                                                    comment: e.target.value,
                                                                                                })
                                                                                            }
                                                                                            placeholder="Giải thích lý do sửa và cách cải thiện..."
                                                                                            className="min-h-[60px]"
                                                                                        />
                                                                                    </div>
                                                                                    <Button
                                                                                        onClick={() => addSentenceCorrection(index, sentence)}
                                                                                        disabled={
                                                                                            !newSentenceCorrection.correction || !newSentenceCorrection.comment
                                                                                        }
                                                                                    >
                                                                                        <RefreshCw className="mr-2 h-4 w-4" />
                                                                                        Thêm sửa câu
                                                                                    </Button>
                                                                                </div>
                                                                            )}
                                                                        </div>
                                                                    </DialogContent>
                                                                </Dialog>
                                                            </div>
                                                        )
                                                    })}
                                                </div>
                                            ) : (
                                                // Word editing mode (existing code)
                                                <div className="text-sm leading-relaxed">
                                                    {words.map((word, index) => {
                                                        const error = errors.find((e) => e.wordIndex === index)
                                                        const isWhitespace = /^\s+$/.test(word)

                                                        if (isWhitespace) {
                                                            return <span key={index}>{word}</span>
                                                        }
                                                        if (error) {
                                                            return (
                                                                <Dialog key={index}>
                                                                    <DialogTrigger asChild>
                                    <span
                                        className={`cursor-pointer border-b-2 border-dashed px-1 ${getErrorTypeColor(error.type)}`}
                                        title={`${error.type}: ${error.comment}`}
                                    >
                                      {word}
                                    </span>
                                                                    </DialogTrigger>
                                                                    <DialogContent>
                                                                        <DialogHeader>
                                                                            <DialogTitle className="flex items-center gap-2">
                                                                                <span>{getErrorTypeIcon(error.type)}</span>
                                                                                Chi tiết lỗi - {error.type}
                                                                            </DialogTitle>
                                                                        </DialogHeader>
                                                                        <div className="space-y-4">
                                                                            <div>
                                                                                <label className="text-sm font-medium">Từ gốc:</label>
                                                                                <p className="text-red-600 font-mono">{error.original}</p>
                                                                            </div>
                                                                            <div>
                                                                                <label className="text-sm font-medium">Sửa thành:</label>
                                                                                <p className="text-green-600 font-mono">{error.correction}</p>
                                                                            </div>
                                                                            <div>
                                                                                <label className="text-sm font-medium">Giải thích:</label>
                                                                                <p className="text-gray-700">{error.comment}</p>
                                                                            </div>
                                                                            <Button variant="destructive" size="sm" onClick={() => removeError(error!.id)}>
                                                                                <X className="mr-2 h-4 w-4" />
                                                                                Xóa lỗi
                                                                            </Button>
                                                                        </div>
                                                                    </DialogContent>
                                                                </Dialog>
                                                            )
                                                        }

                                                        if (isEditMode) {
                                                            return (
                                                                <Dialog key={index}>
                                                                    <DialogTrigger asChild>
                                                                        <span className="cursor-pointer hover:bg-yellow-100 px-1 rounded">{word}</span>
                                                                    </DialogTrigger>
                                                                    <DialogContent>
                                                                        <DialogHeader>
                                                                            <DialogTitle>Thêm lỗi cho từ: "{word}"</DialogTitle>
                                                                        </DialogHeader>
                                                                        <div className="space-y-4">
                                                                            <div>
                                                                                <label className="text-sm font-medium">Loại lỗi:</label>
                                                                                <Select
                                                                                    value={newError.type}
                                                                                    onValueChange={(value: any) => setNewError({ ...newError, type: value })}
                                                                                >
                                                                                    <SelectTrigger>
                                                                                        <SelectValue />
                                                                                    </SelectTrigger>
                                                                                    <SelectContent>
                                                                                        <SelectItem value="grammar">📝 Grammar</SelectItem>
                                                                                        <SelectItem value="vocabulary">📚 Vocabulary</SelectItem>
                                                                                        <SelectItem value="spelling">✏️ Spelling</SelectItem>
                                                                                        <SelectItem value="punctuation">❗ Punctuation</SelectItem>
                                                                                        <SelectItem value="style">🎨 Style</SelectItem>
                                                                                    </SelectContent>
                                                                                </Select>
                                                                            </div>
                                                                            <div>
                                                                                <label className="text-sm font-medium">Sửa thành:</label>
                                                                                <Input
                                                                                    value={newError.correction}
                                                                                    onChange={(e) => setNewError({ ...newError, correction: e.target.value })}
                                                                                    placeholder="Từ/cụm từ đúng"
                                                                                />
                                                                            </div>
                                                                            <div>
                                                                                <label className="text-sm font-medium">Giải thích:</label>
                                                                                <Textarea
                                                                                    value={newError.comment}
                                                                                    onChange={(e) => setNewError({ ...newError, comment: e.target.value })}
                                                                                    placeholder="Giải thích lỗi và cách sửa"
                                                                                />
                                                                            </div>
                                                                            <Button
                                                                                onClick={() => addError(index, word)}
                                                                                disabled={!newError.correction || !newError.comment}
                                                                            >
                                                                                <AlertCircle className="mr-2 h-4 w-4" />
                                                                                Thêm lỗi
                                                                            </Button>
                                                                        </div>
                                                                    </DialogContent>
                                                                </Dialog>
                                                            )
                                                        }

                                                        return <span key={index}>{word}</span>
                                                    })}
                                                </div>
                                            )}
                                        </div>
                                    </TabsContent>

                                    <TabsContent value="errors" className="mt-4">
                                        <div className="space-y-4">
                                            {/* Error Statistics */}
                                            <div className="grid grid-cols-5 gap-2">
                                                {Object.entries(getErrorStats()).map(([type, count]) => (
                                                    <Badge key={type} variant="outline" className={`justify-center ${getErrorTypeColor(type)}`}>
                                                        {getErrorTypeIcon(type)} {count}
                                                    </Badge>
                                                ))}
                                            </div>

                                            {/* Error List */}
                                            <div className="space-y-2 max-h-60 overflow-y-auto">
                                                {errors.map((error) => (
                                                    <div key={error.id} className={`p-3 rounded-lg border ${getErrorTypeColor(error.type)}`}>
                                                        <div className="flex items-start justify-between">
                                                            <div className="flex-1">
                                                                <div className="flex items-center gap-2 mb-1">
                                                                    <span>{getErrorTypeIcon(error.type)}</span>
                                                                    <span className="font-medium capitalize">{error.type}</span>
                                                                </div>
                                                                <div className="text-sm">
                                                                    <span className="line-through text-red-600">{error.original}</span>
                                                                    <span className="mx-2">→</span>
                                                                    <span className="text-green-600 font-medium">{error.correction}</span>
                                                                </div>
                                                                <p className="text-xs mt-1 opacity-80">{error.comment}</p>
                                                            </div>
                                                            <Button variant="ghost" size="sm" onClick={() => removeError(error.id)}>
                                                                <X className="h-4 w-4" />
                                                            </Button>
                                                        </div>
                                                    </div>
                                                ))}
                                                {errors.length === 0 && (
                                                    <div className="text-center py-8 text-gray-500">
                                                        <CheckCircle className="h-12 w-12 mx-auto mb-2 opacity-50" />
                                                        <p>Chưa có lỗi từ nào được đánh dấu</p>
                                                    </div>
                                                )}
                                            </div>
                                        </div>
                                    </TabsContent>

                                    <TabsContent value="sentences" className="mt-4">
                                        <div className="space-y-4">
                                            {/* Sentence Statistics */}
                                            <div className="grid grid-cols-5 gap-2">
                                                {Object.entries(getSentenceStats()).map(([type, count]) => (
                                                    <Badge
                                                        key={type}
                                                        variant="outline"
                                                        className={`justify-center ${getSentenceTypeColor(type)}`}
                                                    >
                                                        {getSentenceTypeIcon(type)} {count}
                                                    </Badge>
                                                ))}
                                            </div>

                                            {/* Sentence Corrections List */}
                                            <div className="space-y-3 max-h-60 overflow-y-auto">
                                                {sentenceCorrections.map((correction) => (
                                                    <div
                                                        key={correction.id}
                                                        className={`p-4 rounded-lg border ${getSentenceTypeColor(correction.type)}`}
                                                    >
                                                        <div className="flex items-start justify-between">
                                                            <div className="flex-1">
                                                                <div className="flex items-center gap-2 mb-2">
                                                                    <span>{getSentenceTypeIcon(correction.type)}</span>
                                                                    <span className="font-medium capitalize">{correction.type}</span>
                                                                </div>
                                                                <div className="space-y-2 text-sm">
                                                                    <div>
                                                                        <span className="text-xs text-gray-500 uppercase tracking-wide">Gốc:</span>
                                                                        <p className="line-through text-red-600">{correction.original}</p>
                                                                    </div>
                                                                    <div>
                                                                        <span className="text-xs text-gray-500 uppercase tracking-wide">Sửa:</span>
                                                                        <p className="text-green-600 font-medium">{correction.correction}</p>
                                                                    </div>
                                                                </div>
                                                                <p className="text-xs mt-2 opacity-80">{correction.comment}</p>
                                                            </div>
                                                            <Button variant="ghost" size="sm" onClick={() => removeSentenceCorrection(correction.id)}>
                                                                <X className="h-4 w-4" />
                                                            </Button>
                                                        </div>
                                                    </div>
                                                ))}
                                                {sentenceCorrections.length === 0 && (
                                                    <div className="text-center py-8 text-gray-500">
                                                        <CheckCircle className="h-12 w-12 mx-auto mb-2 opacity-50" />
                                                        <p>Chưa có câu nào được sửa</p>
                                                    </div>
                                                )}
                                            </div>
                                        </div>
                                    </TabsContent>
                                </Tabs>
                            </CardContent>
                        </Card>
                    </div>

                    {/* Right Column - Grading */}
                    <div className="space-y-6">
                        {/* Scoring Section */}
                        <Card>
                            <CardHeader>
                                <CardTitle>Chấm điểm theo tiêu chí</CardTitle>
                            </CardHeader>
                            <CardContent className="space-y-6">
                                {/* Task Achievement */}
                                <div className="space-y-3">
                                    <div className="flex items-center justify-between">
                                        <label className="font-medium">Task Achievement</label>
                                        <Select
                                            value={scores.taskResponse}
                                            onValueChange={(value) => {
                                                setAllScores((prev) => ({
                                                    ...prev,
                                                    [selectedTask]: {
                                                        ...prev[selectedTask],
                                                        taskResponse: value,
                                                    },
                                                }));
                                            }}
                                        >
                                            <SelectTrigger className="w-20">
                                                <SelectValue placeholder="0" />
                                            </SelectTrigger>
                                            <SelectContent>
                                                <>
                                                    {[...Array(17)].map((_, index) => {
                                                        const score = (index + 2) * 0.5; // từ 1.0 đến 9.0
                                                        return (
                                                            <SelectItem key={score} value={score.toString()}>
                                                                {score}
                                                            </SelectItem>
                                                        );
                                                    })}
                                                </>
                                            </SelectContent>
                                        </Select>
                                    </div>
                                    <Textarea
                                        placeholder="Nhận xét về Task Response..."
                                        value={comments.taskResponse}
                                        onChange={(e) => updateComment("taskResponse", e.target.value)}
                                        className="min-h-[80px]"
                                    />
                                </div>

                                <Separator />

                                {/* Coherence and Cohesion */}
                                <div className="space-y-3">
                                    <div className="flex items-center justify-between">
                                        <label className="font-medium">Coherence and Cohesion</label>
                                        <Select
                                            value={scores.coherenceCohesion}
                                            onValueChange={(value) => {
                                                setAllScores((prev) => ({
                                                    ...prev,
                                                    [selectedTask]: {
                                                        ...prev[selectedTask],
                                                        coherenceCohesion: value,
                                                    },
                                                }));
                                            }}
                                        >
                                            <SelectTrigger className="w-20">
                                                <SelectValue placeholder="0" />
                                            </SelectTrigger>
                                            <SelectContent>
                                                <>
                                                    {[...Array(17)].map((_, index) => {
                                                        const score = (index + 2) * 0.5; // từ 1.0 đến 9.0
                                                        return (
                                                            <SelectItem key={score} value={score.toString()}>
                                                                {score}
                                                            </SelectItem>
                                                        );
                                                    })}
                                                </>
                                            </SelectContent>
                                        </Select>
                                    </div>
                                    <Textarea
                                        placeholder="Nhận xét về Coherence & Cohesion..."
                                        value={comments.coherenceCohesion}
                                        onChange={(e) => updateComment("coherenceCohesion", e.target.value)}
                                        className="min-h-[80px]"
                                    />
                                </div>

                                <Separator />

                                {/* Lexical Resource */}
                                <div className="space-y-3">
                                    <div className="flex items-center justify-between">
                                        <label className="font-medium">Lexical Resource</label>
                                        <Select
                                            value={scores.lexicalResource}
                                            onValueChange={(value) => {
                                                setAllScores((prev) => ({
                                                    ...prev,
                                                    [selectedTask]: {
                                                        ...prev[selectedTask],
                                                        lexicalResource: value,
                                                    },
                                                }));
                                            }}
                                        >
                                            <SelectTrigger className="w-20">
                                                <SelectValue placeholder="0" />
                                            </SelectTrigger>
                                            <SelectContent>
                                                <>
                                                    {[...Array(17)].map((_, index) => {
                                                        const score = (index + 2) * 0.5; // từ 1.0 đến 9.0
                                                        return (
                                                            <SelectItem key={score} value={score.toString()}>
                                                                {score}
                                                            </SelectItem>
                                                        );
                                                    })}
                                                </>
                                            </SelectContent>
                                        </Select>
                                    </div>
                                    <Textarea
                                        placeholder="Nhận xét về Lexical Resource..."
                                        value={comments.lexicalResource}
                                        onChange={(e) => updateComment("lexicalResource", e.target.value)}
                                        className="min-h-[80px]"
                                    />
                                </div>

                                <Separator />

                                {/* Grammatical Range and Accuracy */}
                                <div className="space-y-3">
                                    <div className="flex items-center justify-between">
                                        <label className="font-medium">Grammatical Range and Accuracy</label>
                                        <Select
                                            value={scores.grammaticalRange}
                                            onValueChange={(value) => {
                                                setAllScores((prev) => ({
                                                    ...prev,
                                                    [selectedTask]: {
                                                        ...prev[selectedTask],
                                                        grammaticalRange: value,
                                                    },
                                                }));
                                            }}
                                        >
                                            <SelectTrigger className="w-20">
                                                <SelectValue placeholder="0" />
                                            </SelectTrigger>
                                            <SelectContent>
                                                <>
                                                    {[...Array(17)].map((_, index) => {
                                                        const score = (index + 2) * 0.5; // từ 1.0 đến 9.0
                                                        return (
                                                            <SelectItem key={score} value={score.toString()}>
                                                                {score}
                                                            </SelectItem>
                                                        );
                                                    })}
                                                </>
                                            </SelectContent>
                                        </Select>
                                    </div>
                                    <Textarea
                                        placeholder="Nhận xét về Grammatical Range and Accuracy..."
                                        value={comments.grammaticalRange}
                                        onChange={(e) =>
                                            setAllComments(prev => ({
                                                ...prev,
                                                [selectedTask]: {
                                                    ...prev[selectedTask],
                                                    grammaticalRange: e.target.value,
                                                },
                                            }))
                                        }
                                    />
                                </div>
                            </CardContent>
                        </Card>

                        {/* Overall Score and Comments */}
                        <Card>
                            <CardHeader>
                                <CardTitle>Tổng kết</CardTitle>
                            </CardHeader>
                            <CardContent className="space-y-4">
                                <div className="flex items-center justify-between rounded-lg bg-blue-50 p-4">
                                    <span className="font-medium">Điểm tổng kết:</span>
                                    <span className="text-2xl font-bold text-blue-600">{calculateOverallScore()}</span>
                                </div>

                                <div className="space-y-2">
                                    <label className="font-medium">Nhận xét tổng quan:</label>
                                    <Textarea
                                        placeholder="Nhận xét tổng quan về bài làm..."
                                        value={comments.overall}
                                        onChange={(e) =>
                                            setAllComments(prev => ({
                                                ...prev,
                                                [selectedTask]: {
                                                    ...prev[selectedTask],
                                                    overall: e.target.value,
                                                }
                                            }))
                                        }
                                        className="min-h-[100px]"
                                    />
                                </div>
                            </CardContent>
                        </Card>

                        {/* Action Buttons */}
                        <div className="flex gap-3">
                            <Button className="flex-1">
                                <Send className="mr-2 h-4 w-4" />
                                Hoàn thành chấm
                            </Button>
                        </div>

                        {/* Navigation */}
                        <div className="flex items-center justify-between">
                            <Button
                                variant="outline"
                                size="sm"
                                onClick={() => setSelectedTask('task1')}
                            >
                                Bài truoc
                                <ChevronLeft className="ml-2 h-4 w-4" />
                            </Button>
                            <span className="text-sm text-gray-600">Bài 1 / 25</span>
                            <Button
                                variant="outline"
                                size="sm"
                                onClick={() => setSelectedTask('task2')}
                            >
                                Bài tiếp
                                <ChevronRight className="ml-2 h-4 w-4" />
                            </Button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}