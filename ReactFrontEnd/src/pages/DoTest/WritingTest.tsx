import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Textarea } from "@/components/ui/textarea"
import { Card, CardContent } from "@/components/ui/card"
import { Clock, Printer, Maximize2, Menu, ChevronRight } from "lucide-react"

export default function IELTSWritingPractice() {
    const [currentTask, setCurrentTask] = useState(1)
    const [timeRemaining, setTimeRemaining] = useState(10 * 60) // 10 minutes in seconds
    const [essayText, setEssayText] = useState("")
    const [wordCount, setWordCount] = useState(0)

    // Timer countdown
    useEffect(() => {
        const timer = setInterval(() => {
            setTimeRemaining((prev) => {
                if (prev <= 0) {
                    clearInterval(timer)
                    return 0
                }
                return prev - 1
            })
        }, 1000)

        return () => clearInterval(timer)
    }, [])

    // Word count calculation
    useEffect(() => {
        const words = essayText
            .trim()
            .split(/\s+/)
            .filter((word) => word.length > 0)
        setWordCount(words.length)
    }, [essayText])

    const formatTime = (seconds: number) => {
        const minutes = Math.floor(seconds / 60)
        return `${minutes} minute${minutes !== 1 ? "s" : ""} remaining`
    }

    const chartData = {
        men: [
            { country: "Australia", year2000: 47, year1970: 74 },
            { country: "Japan", year2000: 76, year1970: 84 },
            { country: "USA", year2000: 55, year1970: 73 },
            { country: "Belgium", year2000: 19, year1970: 79 },
        ],
        women: [
            { country: "Australia", year2000: 38, year1970: 48 },
            { country: "Japan", year2000: 45, year1970: 43 },
            { country: "USA", year2000: 41, year1970: 53 },
            { country: "Belgium", year2000: 14, year1970: 63 },
        ],
    }

    const BarChart = ({ title, data }: { title: string; data: any[] }) => (
        <div className="mb-8">
            <h3 className="text-center font-medium mb-4 text-gray-600">{title}</h3>
            <div className="space-y-3">
                {data.map((item, index) => (
                    <div key={index} className="flex items-center">
                        <div className="w-16 text-sm text-gray-600 text-right mr-4">{item.country}</div>
                        <div className="flex-1 relative">
                            <div className="flex items-center space-x-1">
                                <div
                                    className="bg-orange-500 h-6 flex items-center justify-end pr-2"
                                    style={{ width: `${(item.year2000 / 90) * 100}%` }}
                                >
                                    <span className="text-white text-xs font-medium">{item.year2000}</span>
                                </div>
                                <div
                                    className="bg-blue-600 h-6 flex items-center justify-end pr-2"
                                    style={{ width: `${(item.year1970 / 90) * 100}%` }}
                                >
                                    <span className="text-white text-xs font-medium">{item.year1970}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                ))}
            </div>
            <div className="flex justify-center mt-4 space-x-4 text-sm">
                <div className="flex items-center space-x-2">
                    <div className="w-4 h-4 bg-orange-500"></div>
                    <span>2000</span>
                </div>
                <div className="flex items-center space-x-2">
                    <div className="w-4 h-4 bg-blue-600"></div>
                    <span>1970</span>
                </div>
            </div>
            <div className="flex justify-between text-xs text-gray-500 mt-2">
                <span>0</span>
                <span>10</span>
                <span>20</span>
                <span>30</span>
                <span>40</span>
                <span>50</span>
                <span>60</span>
                <span>70</span>
                <span>80</span>
                <span>90</span>
            </div>
        </div>
    )

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <header className="bg-white border-b border-gray-200 px-6 py-3">
                <div className="flex items-center justify-between">
                    <div className="flex items-center space-x-4">
                        <div className="flex items-center space-x-2">
                            <div className="w-8 h-8 bg-teal-600 rounded-full flex items-center justify-center">
                                <span className="text-white font-bold text-sm">I</span>
                            </div>
                            <div>
                                <div className="font-bold text-gray-800">IELTS</div>
                                <div className="text-xs text-gray-600">Online Tests</div>
                            </div>
                        </div>
                    </div>

                    <div className="flex items-center space-x-2 text-orange-600">
                        <Clock className="w-5 h-5" />
                        <span className="font-medium">{formatTime(timeRemaining)}</span>
                    </div>

                    <div className="flex items-center space-x-2">
                        <Button variant="ghost" size="sm">
                            <Printer className="w-4 h-4" />
                        </Button>
                        <Button variant="ghost" size="sm">
                            <Maximize2 className="w-4 h-4" />
                        </Button>
                        <Button variant="ghost" size="sm">
                            <Menu className="w-4 h-4" />
                        </Button>
                        <Button className="bg-orange-600 hover:bg-orange-700 text-white px-6">Submit →</Button>
                    </div>
                </div>
            </header>

            {/* Main Content */}
            <div className="flex h-[calc(100vh-80px)]">
                {/* Left Panel - Task Content */}
                <div className="w-1/2 bg-white p-6 overflow-y-auto border-r border-gray-200">
                    {currentTask === 1 ? (
                        <div>
                            <h1 className="text-xl font-bold text-gray-800 mb-2">WRITING TASK 1</h1>
                            <p className="text-sm text-gray-600 mb-4">
                                You should spend about <strong>20 minutes</strong> on this task.
                            </p>

                            <p className="text-sm text-gray-700 mb-4">
                                The charts below show the percentages of men and women aged 60-64 in employment in four countries in
                                1970 and 2000.
                            </p>

                            <p className="text-sm text-gray-700 mb-4">
                                Summarise the information by selecting and reporting the main features, and make comparison where
                                relevant.
                            </p>

                            <p className="text-sm text-gray-700 mb-6">
                                You should write <strong>at least 150 words</strong>.
                            </p>

                            <Card className="mb-6">
                                <CardContent className="p-6">
                                    <BarChart title="% men aged 60-64 in employment" data={chartData.men} />
                                    <BarChart title="% women aged 60-64 in employment" data={chartData.women} />
                                </CardContent>
                            </Card>
                        </div>
                    ) : (
                        <div>
                            <h1 className="text-xl font-bold text-gray-800 mb-2">WRITING TASK 2</h1>
                            <p className="text-sm text-gray-600 mb-4">
                                You should spend about <strong>40 minutes</strong> on this task.
                            </p>

                            <p className="text-sm text-gray-700 mb-4">
                                Some people think that all university students should study whatever they like. Others believe that they
                                should only be allowed to study subjects that will be useful in the future, such as those related to
                                science and technology.
                            </p>

                            <p className="text-sm text-gray-700 mb-4">Discuss both these views and give your own opinion.</p>

                            <p className="text-sm text-gray-700 mb-6">
                                Give reasons for your answer and include any relevant examples from your own knowledge or experience.
                            </p>

                            <p className="text-sm text-gray-700 mb-6">
                                Write <strong>at least 250 words</strong>.
                            </p>
                        </div>
                    )}
                </div>

                {/* Right Panel - Writing Area */}
                <div className="w-1/2 bg-gray-50 p-6 flex flex-col">
                    <Textarea
                        placeholder="Type your essay here..."
                        value={essayText}
                        onChange={(e) => setEssayText(e.target.value)}
                        className="flex-1 resize-none border-gray-300 focus:border-teal-500 focus:ring-teal-500"
                    />

                    <div className="mt-4 flex justify-between items-center">
                        <div className="text-sm text-gray-600">
                            Words Count: <span className="font-medium">{wordCount}</span>
                        </div>
                        <Button variant="ghost" size="sm" className="text-orange-600">
                            <ChevronRight className="w-4 h-4" />
                        </Button>
                    </div>
                </div>
            </div>

            {/* Bottom Navigation */}
            <div className="bg-white border-t border-gray-200 px-6 py-4">
                <div className="flex justify-center space-x-4">
                    <Button
                        variant={currentTask === 1 ? "default" : "outline"}
                        onClick={() => setCurrentTask(1)}
                        className={currentTask === 1 ? "bg-orange-100 text-orange-700 border-orange-300" : ""}
                    >
                        Task 1
                    </Button>
                    <Button
                        variant={currentTask === 2 ? "default" : "outline"}
                        onClick={() => setCurrentTask(2)}
                        className={currentTask === 2 ? "bg-orange-100 text-orange-700 border-orange-300" : ""}
                    >
                        Task 2
                    </Button>
                </div>
            </div>
        </div>
    )
}

