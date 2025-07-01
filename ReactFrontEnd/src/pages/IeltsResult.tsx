
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Progress } from "@/components/ui/progress"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { CheckCircle, XCircle, Clock, Calendar, Target, TrendingUp, BookOpen, Download, Share2 } from "lucide-react"

export default function Component() {
    // Sample data - trong thực tế sẽ được fetch từ API
    const studentResult = {
        student: {
            name: "Nguyễn Văn An",
            email: "nguyenvanan@email.com",
            avatar: "/placeholder.svg?height=40&width=40",
        },
        test: {
            skill: "Reading",
            date: "2024-01-15",
            duration: "60 phút",
            totalQuestions: 40,
            correctAnswers: 32,
        },
        score: {
            band: 7.5,
            percentage: 80,
            level: "Good User",
        },
        sections: [
            {
                name: "Passage 1",
                questions: "1-13",
                correct: 11,
                total: 13,
                percentage: 85,
            },
            {
                name: "Passage 2",
                questions: "14-26",
                correct: 10,
                total: 13,
                percentage: 77,
            },
            {
                name: "Passage 3",
                questions: "27-40",
                correct: 11,
                total: 14,
                percentage: 79,
            },
        ],
        questionTypes: [
            { type: "Multiple Choice", correct: 8, total: 10, percentage: 80 },
            { type: "True/False/Not Given", correct: 12, total: 15, percentage: 80 },
            { type: "Matching Headings", correct: 6, total: 8, percentage: 75 },
            { type: "Gap Fill", correct: 6, total: 7, percentage: 86 },
        ],
        detailedAnswers: [
            {
                question: 1,
                questionText: "What is the main purpose of the passage?",
                options: [
                    "A. To describe a historical event",
                    "B. To explain a scientific process",
                    "C. To argue for a policy change",
                    "D. To compare different theories",
                ],
                userAnswer: "B",
                correctAnswer: "B",
                isCorrect: true,
                explanation: "The passage focuses on explaining how photosynthesis works, making option B the correct answer.",
            },
            {
                question: 2,
                questionText: "According to the text, which factor is most important for plant growth?",
                options: ["A. Temperature", "B. Water availability", "C. Sunlight exposure", "D. Soil nutrients"],
                userAnswer: "A",
                correctAnswer: "C",
                isCorrect: false,
                explanation:
                    "The passage clearly states in paragraph 2 that 'sunlight exposure is the primary factor determining plant growth rates', making C the correct answer.",
            },
            {
                question: 3,
                questionText: "The word 'abundant' in line 15 is closest in meaning to:",
                options: ["A. Scarce", "B. Plentiful", "C. Necessary", "D. Expensive"],
                userAnswer: "C",
                correctAnswer: "C",
                isCorrect: true,
                explanation: "In context, 'abundant' refers to something that is plentiful or available in large quantities.",
            },
        ],
    }

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

    return (
        <div className="min-h-screen bg-gray-50 p-4">
            <div className="max-w-6xl mx-auto space-y-6">
                {/* Header */}
                <div className="bg-white rounded-lg shadow-sm p-6">
                    <div className="flex items-center justify-between">
                        <div className="flex items-center gap-4">
                            <Avatar className="h-12 w-12">
                                <AvatarImage src={studentResult.student.avatar || "/placeholder.svg"} />
                                <AvatarFallback>{studentResult.student.name.charAt(0)}</AvatarFallback>
                            </Avatar>
                            <div>
                                <h1 className="text-2xl font-bold">Kết quả bài thi IELTS {studentResult.test.skill}</h1>
                                <p className="text-gray-600">
                                    {studentResult.student.name} • {studentResult.student.email}
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
                                    <span className="text-sm text-gray-600">Ngày thi:</span>
                                    <span className="text-sm font-medium">{studentResult.test.date}</span>
                                </div>
                                <div className="flex justify-between">
                                    <span className="text-sm text-gray-600">Thời gian:</span>
                                    <span className="text-sm font-medium">{studentResult.test.duration}</span>
                                </div>
                                <div className="flex justify-between">
                                    <span className="text-sm text-gray-600">Tổng câu hỏi:</span>
                                    <span className="text-sm font-medium">{studentResult.test.totalQuestions}</span>
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
                                <div className={`text-4xl font-bold ${getBandColor(studentResult.score.band)}`}>
                                    {studentResult.score.band}
                                </div>
                                <p className="text-sm text-gray-600 mt-1">{getBandDescription(studentResult.score.band)}</p>
                                <Progress value={studentResult.score.percentage} className="mt-3 [&>div]:bg-green-600" />
                                <p className="text-xs text-gray-500 mt-1">{studentResult.score.percentage}% chính xác</p>
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
                                    <span className="text-sm font-medium text-green-600">
                    {studentResult.test.correctAnswers}/{studentResult.test.totalQuestions}
                  </span>
                                </div>
                                <div className="flex justify-between">
                                    <span className="text-sm text-gray-600">Sai:</span>
                                    <span className="text-sm font-medium text-red-600">
                    {studentResult.test.totalQuestions - studentResult.test.correctAnswers}/
                                        {studentResult.test.totalQuestions}
                  </span>
                                </div>
                                <div className="flex justify-between">
                                    <span className="text-sm text-gray-600">Tỷ lệ:</span>
                                    <span className="text-sm font-medium">{studentResult.score.percentage}%</span>
                                </div>
                            </div>
                        </CardContent>
                    </Card>
                </div>

                {/* Detailed Results */}
                <Tabs defaultValue="sections" className="space-y-4">
                    <TabsList className="grid w-full grid-cols-4">
                        <TabsTrigger value="sections">Theo phần</TabsTrigger>
                        <TabsTrigger value="question-types">Loại câu hỏi</TabsTrigger>
                        <TabsTrigger value="detailed">Chi tiết</TabsTrigger>
                        <TabsTrigger value="feedback">Nhận xét</TabsTrigger>
                    </TabsList>

                    <TabsContent value="sections" className="space-y-4">
                        <Card>
                            <CardHeader>
                                <CardTitle>Kết quả theo từng phần</CardTitle>
                                <CardDescription>Phân tích điểm số theo từng passage</CardDescription>
                            </CardHeader>
                            <CardContent>
                                <div className="space-y-4">
                                    {studentResult.sections.map((section, index) => (
                                        <div key={index} className="space-y-2">
                                            <div className="flex justify-between items-center">
                                                <div>
                                                    <h4 className="font-medium">{section.name}</h4>
                                                    <p className="text-sm text-gray-600">Câu {section.questions}</p>
                                                </div>
                                                <div className="text-right">
                                                    <p className="font-medium">
                                                        {section.correct}/{section.total}
                                                    </p>
                                                    <p className="text-sm text-gray-600">{section.percentage}%</p>
                                                </div>
                                            </div>
                                            <Progress value={section.percentage} className="[&>div]:bg-green-600" />
                                        </div>
                                    ))}
                                </div>
                            </CardContent>
                        </Card>
                    </TabsContent>

                    <TabsContent value="question-types" className="space-y-4">
                        <Card>
                            <CardHeader>
                                <CardTitle>Kết quả theo loại câu hỏi</CardTitle>
                                <CardDescription>Phân tích điểm số theo từng dạng bài</CardDescription>
                            </CardHeader>
                            <CardContent>
                                <div className="space-y-4">
                                    {studentResult.questionTypes.map((type, index) => (
                                        <div key={index} className="space-y-2">
                                            <div className="flex justify-between items-center">
                                                <h4 className="font-medium">{type.type}</h4>
                                                <div className="text-right">
                                                    <p className="font-medium">
                                                        {type.correct}/{type.total}
                                                    </p>
                                                    <p className="text-sm text-gray-600">{type.percentage}%</p>
                                                </div>
                                            </div>
                                            <Progress value={type.percentage} className="[&>div]:bg-green-600" />
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
                                <CardDescription>Xem câu hỏi, đáp án của bạn, đáp án đúng và giải thích</CardDescription>
                            </CardHeader>
                            <CardContent>
                                <div className="space-y-6 max-h-96 overflow-y-auto">
                                    {studentResult.detailedAnswers.map((answer, index) => (
                                        <div key={index} className="border rounded-lg p-4 space-y-4">
                                            {/* Question Header */}
                                            <div className="flex items-center gap-3 pb-2 border-b">
                                                {answer.isCorrect ? (
                                                    <CheckCircle className="h-5 w-5 text-green-600" />
                                                ) : (
                                                    <XCircle className="h-5 w-5 text-red-600" />
                                                )}
                                                <span className="font-bold text-lg">Câu {answer.question}</span>
                                                <span
                                                    className={`px-2 py-1 rounded text-xs font-medium ${
                                                        answer.isCorrect ? "bg-green-100 text-green-700" : "bg-red-100 text-red-700"
                                                    }`}
                                                >
                          {answer.isCorrect ? "Đúng" : "Sai"}
                        </span>
                                            </div>

                                            {/* Question Text */}
                                            <div className="space-y-2">
                                                <h4 className="font-medium text-gray-900">Câu hỏi:</h4>
                                                <p className="text-gray-700">{answer.questionText}</p>
                                            </div>

                                            {/* Options */}
                                            {answer.options && (
                                                <div className="space-y-2">
                                                    <h4 className="font-medium text-gray-900">Các lựa chọn:</h4>
                                                    <div className="grid gap-1">
                                                        {answer.options.map((option, optIndex) => {
                                                            const optionLetter = option.charAt(0)
                                                            const isUserChoice = optionLetter === answer.userAnswer
                                                            const isCorrectChoice = optionLetter === answer.correctAnswer

                                                            return (
                                                                <div
                                                                    key={optIndex}
                                                                    className={`p-2 rounded text-sm ${
                                                                        isCorrectChoice
                                                                            ? "bg-green-50 border border-green-200 text-green-700"
                                                                            : isUserChoice && !answer.isCorrect
                                                                                ? "bg-red-50 border border-red-200 text-red-700"
                                                                                : "bg-gray-50"
                                                                    }`}
                                                                >
                                                                    {option}
                                                                    {isUserChoice && <span className="ml-2 text-xs font-medium">(Bạn chọn)</span>}
                                                                    {isCorrectChoice && <span className="ml-2 text-xs font-medium">(Đáp án đúng)</span>}
                                                                </div>
                                                            )
                                                        })}
                                                    </div>
                                                </div>
                                            )}

                                            {/* Answer Summary */}
                                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 p-3 bg-gray-50 rounded">
                                                <div>
                                                    <p className="text-sm text-gray-600">Câu trả lời của bạn:</p>
                                                    <p className={`font-medium ${answer.isCorrect ? "text-green-600" : "text-red-600"}`}>
                                                        {answer.userAnswer}
                                                    </p>
                                                </div>
                                                <div>
                                                    <p className="text-sm text-gray-600">Đáp án đúng:</p>
                                                    <p className="font-medium text-green-600">{answer.correctAnswer}</p>
                                                </div>
                                            </div>

                                            {/* Explanation */}
                                            <div className="space-y-2">
                                                <h4 className="font-medium text-gray-900">Giải thích:</h4>
                                                <div className="p-3 bg-blue-50 rounded-lg">
                                                    <p className="text-sm text-blue-800">{answer.explanation}</p>
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </CardContent>
                        </Card>
                    </TabsContent>

                    <TabsContent value="feedback" className="space-y-4">
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                            <Card>
                                <CardHeader>
                                    <CardTitle className="text-green-600">Điểm mạnh</CardTitle>
                                </CardHeader>
                                <CardContent>
                                    <ul className="space-y-2">
                                        <li className="flex items-start gap-2">
                                            <CheckCircle className="h-4 w-4 text-green-600 mt-0.5" />
                                            <span className="text-sm">Làm tốt các câu hỏi Gap Fill (86%)</span>
                                        </li>
                                        <li className="flex items-start gap-2">
                                            <CheckCircle className="h-4 w-4 text-green-600 mt-0.5" />
                                            <span className="text-sm">Hiểu rõ nội dung Passage 1 (85%)</span>
                                        </li>
                                        <li className="flex items-start gap-2">
                                            <CheckCircle className="h-4 w-4 text-green-600 mt-0.5" />
                                            <span className="text-sm">Kỹ năng đọc hiểu tổng thể tốt</span>
                                        </li>
                                    </ul>
                                </CardContent>
                            </Card>

                            <Card>
                                <CardHeader>
                                    <CardTitle className="text-orange-600">Cần cải thiện</CardTitle>
                                </CardHeader>
                                <CardContent>
                                    <ul className="space-y-2">
                                        <li className="flex items-start gap-2">
                                            <Target className="h-4 w-4 text-orange-600 mt-0.5" />
                                            <span className="text-sm">Luyện thêm dạng Matching Headings</span>
                                        </li>
                                        <li className="flex items-start gap-2">
                                            <Target className="h-4 w-4 text-orange-600 mt-0.5" />
                                            <span className="text-sm">Cải thiện tốc độ đọc cho Passage 2</span>
                                        </li>
                                        <li className="flex items-start gap-2">
                                            <Target className="h-4 w-4 text-orange-600 mt-0.5" />
                                            <span className="text-sm">Chú ý kỹ hơn với từ khóa</span>
                                        </li>
                                    </ul>
                                </CardContent>
                            </Card>
                        </div>

                        <Card>
                            <CardHeader>
                                <CardTitle>Gợi ý học tập</CardTitle>
                            </CardHeader>
                            <CardContent>
                                <div className="space-y-3">
                                    <div className="p-4 bg-green-50 rounded-lg">
                                        <h4 className="font-medium text-green-700 mb-2">Để đạt band 8.0:</h4>
                                        <ul className="text-sm text-green-600 space-y-1">
                                            <li>• Luyện thêm 5-10 bài Matching Headings mỗi tuần</li>
                                            <li>• Đọc các bài báo học thuật để quen với từ vựng khó</li>
                                            <li>• Thực hành skimming và scanning thường xuyên</li>
                                        </ul>
                                    </div>
                                    <div className="p-4 bg-green-100 rounded-lg">
                                        <h4 className="font-medium text-green-700 mb-2">Tài liệu đề xuất:</h4>
                                        <ul className="text-sm text-green-600 space-y-1">
                                            <li>• Cambridge IELTS 15-17 (Reading sections)</li>
                                            <li>• Academic articles từ BBC, The Guardian</li>
                                            <li>• IELTS Reading practice tests online</li>
                                        </ul>
                                    </div>
                                </div>
                            </CardContent>
                        </Card>
                    </TabsContent>
                </Tabs>

                {/* Action Buttons */}
                <div className="flex justify-center gap-4">
                    <Button className="bg-green-600 hover:bg-green-700">
                        <BookOpen className="h-4 w-4 mr-2" />
                        Làm bài tập luyện tập
                    </Button>
                    <Button variant="outline" className="border-green-600 text-green-600 hover:bg-green-50">
                        <Clock className="h-4 w-4 mr-2" />
                        Xem lịch sử bài thi
                    </Button>
                </div>
            </div>
        </div>
    )
}