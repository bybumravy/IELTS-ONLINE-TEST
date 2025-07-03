import { useEffect, useState } from "react"
import { useAuth } from "@/contexts/AuthContext"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Progress } from "@/components/ui/progress"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { CheckCircle, XCircle, Clock, Calendar, Target, TrendingUp, BookOpen, Download, Share2 } from "lucide-react"
import { useNavigate, useParams, useSearchParams } from "react-router-dom"

export default function ListeningResult() {
    const { user } = useAuth()
    // const [searchParams] = useSearchParams()
    const [result, setResult] = useState<any>(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)

    const { resultId } = useParams<{ resultId: string }>()
    const navigate = useNavigate()

    useEffect(() => {
        if (!resultId) return
        setLoading(true)
        fetch(`http://localhost:8080/api/result/listening/by-id?answerId=${resultId}`)
            .then(res => {
                if (!res.ok) throw new Error("Không tìm thấy kết quả")
                return res.json()
            })
            .then(data => {
                setResult(data)
                setLoading(false)
            })
            .catch(e => {
                setError(e.message)
                setLoading(false)
            })
    }, [resultId])

    // Helper: tổng số câu hỏi, số đúng, số sai, %
    const calcStats = (result: any) => {
        return {
            percentage: result.totalQuestions ? Math.round((result.totalCorrect / result.totalQuestions) * 100) : 0
        }
    }

    // Tính toán sau khi đảm bảo result tồn tại
    const stats = result ? calcStats(result) : { totalQuestions: 0, totalCorrect: 0, percentage: 0 }

    const getBandColor = (band: number) => {
        if (band >= 8) return "text-green-700"
        if (band >= 6.5) return "text-green-600"
        if (band >= 5.5) return "text-yellow-600"
        return "text-red-600"
    }

    const getBandDescription = (band: number) => {
        if (band >= 8) return "Very Good User"
        if (band >= 7) return "Good User"
        if (band >= 6) return "Competent User"
        if (band >= 5) return "Modest User"
        return "Limited User"
    }

    if (loading) return <div className="p-8 text-center">Đang tải kết quả...</div>
    if (error) return <div className="p-8 text-center text-red-600">{error}</div>
    if (!result) return null

    return (
        <div className="min-h-screen bg-gray-50 p-4">
            <div className="max-w-6xl mx-auto space-y-6">
                {/* Header */}
                <div className="bg-white rounded-lg shadow-sm p-6">
                    <div className="flex items-center justify-between">
                        <div className="flex items-center gap-4">
                            <Avatar className="h-12 w-12">
                                <AvatarImage src={"/placeholder.svg"} />
                                <AvatarFallback>{user?.username?.charAt(0) || "U"}</AvatarFallback>
                            </Avatar>
                            <div>
                                <h1 className="text-2xl font-bold">Kết quả bài thi IELTS Listening</h1>
                                <p className="text-gray-600">
                                    {user?.username}
                                </p>
                            </div>
                        </div>
                        <div className="flex gap-2">
                            <Button variant="outline" size="sm">
                                <Share2 className="h-4 w-4 mr-2" />
                                Chia sẻ
                            </Button>
                            <Button variant="outline" size="sm">
                                <Download className="h-4 w-4 mr-2" />
                                Tải xuống
                            </Button>
                        </div>
                    </div>
                </div>

                {/* Test Info & Overall Score */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                    <Card>
                        <CardHeader className="flex flex-row items-center space-y-0 pb-2">
                            <Calendar className="h-4 w-4 text-muted-foreground" />
                            <CardTitle className="text-sm font-medium ml-2">Thông tin bài thi</CardTitle>
                        </CardHeader>
                        <CardContent>
                            <div className="space-y-2">
                                <div className="flex justify-between">
                                    <span className="text-sm text-gray-600">Test ID:</span>
                                    <span className="text-sm font-medium">{result.testId}</span>
                                </div>
                                <div className="flex justify-between">
                                    <span className="text-sm text-gray-600">Thời gian:</span>
                                    <span className="text-sm font-medium">30 phút</span>
                                </div>
                                <div className="flex justify-between">
                                    <span className="text-sm text-gray-600">Tổng câu hỏi:</span>
                                    <span className="text-sm font-medium">{result.totalQuestions}</span>
                                </div>
                            </div>
                        </CardContent>
                    </Card>

                    <Card>
                        <CardHeader className="flex flex-row items-center space-y-0 pb-2">
                            <Target className="h-4 w-4 text-muted-foreground" />
                            <CardTitle className="text-sm font-medium ml-2">Điểm tổng thể</CardTitle>
                        </CardHeader>
                        <CardContent>
                            <div className="text-center">
                                <div className={`text-4xl font-bold ${getBandColor(result.band)}`}>{result.band}</div>
                                <p className="text-sm text-gray-600 mt-1">{getBandDescription(result.band)}</p>
                                <Progress value={stats.percentage} className="mt-3 [&>div]:bg-green-600" />
                                <p className="text-xs text-gray-500 mt-1">{stats.percentage}% chính xác</p>
                            </div>
                        </CardContent>
                    </Card>

                    <Card>
                        <CardHeader className="flex flex-row items-center space-y-0 pb-2">
                            <TrendingUp className="h-4 w-4 text-muted-foreground" />
                            <CardTitle className="text-sm font-medium ml-2">Kết quả</CardTitle>
                        </CardHeader>
                        <CardContent>
                            <div className="space-y-2">
                                <div className="flex justify-between">
                                    <span className="text-sm text-gray-600">Đúng:</span>
                                    <span className="text-sm font-medium text-green-600">{result.totalCorrect}/{result.totalQuestions}</span>
                                </div>
                                <div className="flex justify-between">
                                    <span className="text-sm text-gray-600">Sai:</span>
                                    <span className="text-sm font-medium text-red-600">{result.totalQuestions - result.totalCorrect}/{result.totalQuestions}</span>
                                </div>
                                <div className="flex justify-between">
                                    <span className="text-sm text-gray-600">Tỷ lệ:</span>
                                    <span className="text-sm font-medium">{stats.percentage}%</span>
                                </div>
                            </div>
                        </CardContent>
                    </Card>
                </div>

                {/* Detailed Results */}
                <Tabs defaultValue="sections" className="space-y-4">
                    <TabsList className="grid w-full grid-cols-3">
                        <TabsTrigger value="sections">Theo phần</TabsTrigger>
                        <TabsTrigger value="detailed">Chi tiết</TabsTrigger>
                        <TabsTrigger value="raw">Dữ liệu gốc</TabsTrigger>
                    </TabsList>

                    <TabsContent value="sections" className="space-y-4">
                        <Card>
                            <CardHeader>
                                <CardTitle>Kết quả theo từng task</CardTitle>
                                <CardDescription>Phân tích điểm số theo từng section</CardDescription>
                            </CardHeader>
                            <CardContent>
                                <div className="space-y-4">
                                    {result.tasks?.map((task: any, tIdx: number) => (
                                        <div key={tIdx} className="mb-4">
                                            <div className="font-bold mb-2">Task {task.taskNumber}</div>
                                            {task.sections?.map((section: any, sIdx: number) => {
                                                const total = section.questions?.length || 0
                                                const correct = section.questions?.filter((q: any) => q.answer === q.studentAnswer).length || 0
                                                const percent = total ? Math.round((correct / total) * 100) : 0
                                                return (
                                                    <div key={sIdx} className="space-y-2 mb-2">
                                                        <div className="flex justify-between items-center">
                                                            <div>
                                                                <h4 className="font-medium">Section {section.sectionNumber} ({section.type})</h4>
                                                                <p className="text-sm text-gray-600">Câu hỏi: {total}</p>
                                                            </div>
                                                            <div className="text-right">
                                                                <p className="font-medium">{correct}/{total}</p>
                                                                <p className="text-sm text-gray-600">{percent}%</p>
                                                            </div>
                                                        </div>
                                                        <Progress value={percent} className="[&>div]:bg-green-600" />
                                                    </div>
                                                )
                                            })}
                                        </div>
                                    ))}
                                </div>
                            </CardContent>
                        </Card>
                    </TabsContent>

                    <TabsContent value="detailed" className="space-y-4">
                        <Card>
                            <CardHeader>
                                <CardTitle>Chi tiết từng câu hỏi</CardTitle>
                                <CardDescription>Xem câu hỏi, đáp án của bạn, đáp án đúng</CardDescription>
                            </CardHeader>
                            <CardContent>
                                <div className="space-y-6 max-h-96 overflow-y-auto">
                                    {result.tasks?.flatMap((task: any) =>
                                        task.sections?.flatMap((section: any) =>
                                            section.questions?.map((q: any, idx: number) => (
                                                <div key={idx} className="border rounded-lg p-4 space-y-4">
                                                    <div className="flex items-center gap-3 pb-2 border-b">
                                                        {q.answer === q.studentAnswer ? (
                                                            <CheckCircle className="h-5 w-5 text-green-600" />
                                                        ) : (
                                                            <XCircle className="h-5 w-5 text-red-600" />
                                                        )}
                                                        <span className="font-bold text-lg">Câu hỏi</span>
                                                        <span className={`px-2 py-1 rounded text-xs font-medium ${q.answer === q.studentAnswer ? "bg-green-100 text-green-700" : "bg-red-100 text-red-700"}`}>{q.answer === q.studentAnswer ? "Đúng" : "Sai"}</span>
                                                    </div>
                                                    <div className="space-y-2">
                                                        <h4 className="font-medium text-gray-900">Câu hỏi:</h4>
                                                        <p className="text-gray-700">{q.question}</p>
                                                    </div>
                                                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 p-3 bg-gray-50 rounded">
                                                        <div>
                                                            <p className="text-sm text-gray-600">Câu trả lời của bạn:</p>
                                                            <p className={`font-medium ${q.answer === q.studentAnswer ? "text-green-600" : "text-red-600"}`}>{q.studentAnswer || <span className="italic text-gray-400">(Chưa trả lời)</span>}</p>
                                                        </div>
                                                        <div>
                                                            <p className="text-sm text-gray-600">Đáp án đúng:</p>
                                                            <p className="font-medium text-green-600">{q.answer}</p>
                                                        </div>
                                                        {/* ✅ Thêm phần này để hiển thị explanation có highlight */}
                                                        {q.explanation && (
                                                            <div className="col-span-2">
                                                                <p className="text-sm text-gray-600 mt-3">Giải thích:</p>
                                                                <div
                                                                    className="text-sm text-gray-800 p-2 bg-gray-100 rounded"
                                                                    dangerouslySetInnerHTML={{ __html: q.explanation }}
                                                                />
                                                            </div>
                                                        )}
                                                    </div>
                                                </div>
                                            ))
                                        )
                                    )}
                                </div>
                            </CardContent>
                        </Card>
                    </TabsContent>

                    <TabsContent value="raw" className="space-y-4">
                        <pre className="bg-gray-100 p-4 rounded text-xs overflow-x-auto max-h-96">{JSON.stringify(result, null, 2)}</pre>
                    </TabsContent>
                </Tabs>

                {/* Action Buttons */}
                <div className="flex justify-center gap-4">
                    <Button className="bg-green-600 hover:bg-green-700" onClick={() => navigate(`/tips/Listening`)}>
                        <BookOpen className="h-4 w-4 mr-2" />
                        Làm bài tập luyện tập
                    </Button>
                    <Button variant="outline" className="border-green-600 text-green-600 hover:bg-green-50" onClick={() => navigate("/test-history")}>
                        <Clock className="h-4 w-4 mr-2" />
                        Xem lịch sử bài thi
                    </Button>
                </div>
            </div>
        </div>
    )
}