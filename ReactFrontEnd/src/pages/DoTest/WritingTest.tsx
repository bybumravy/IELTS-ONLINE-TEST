import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Textarea } from "@/components/ui/textarea"
import { ChevronRight } from "lucide-react"
import {DoTestHeader} from "@/components/layout/doTest/DoTestHeader";

export default function IELTSWritingPractice() {
    const [currentTask, setCurrentTask] = useState(1)
    const [essayText, setEssayText] = useState("")
    const [wordCount, setWordCount] = useState(0)
    const [writingData, setWritingData] = useState(null)
    const [timeRemaining, setTimeRemaining] = useState(10 * 60) // 10 minutes in seconds


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

    useEffect(() => {
        fetch("http://localhost:8080/api/writing/1")
            .then((response) => response.json())
            .then((data) => setWritingData(data))
            .catch((error) => console.error("Error fetching writing data:", error))
    }, [])


    useEffect(() => {
        const words = essayText
            .trim()
            .split(/\s+/)
            .filter((word) => word.length > 0)
        setWordCount(words.length)
    }, [essayText])



    return (
        <div className="min-h-screen bg-gray-50">
            <DoTestHeader/>
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
                                {writingData?.task1?.prompt || "Loading..."}
                            </p>

                            <p className="text-sm text-gray-700 mb-6">
                                You should write <strong>at least 150 words</strong>.
                            </p>
                        </div>
                    ) : (
                        <div>
                            <h1 className="text-xl font-bold text-gray-800 mb-2">WRITING TASK 2</h1>
                            <p className="text-sm text-gray-600 mb-4">
                                You should spend about <strong>40 minutes</strong> on this task.
                            </p>

                            <p className="text-sm text-gray-700 mb-4">
                                {writingData?.task2?.prompt || "Loading..."}
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
            <div className="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 px-6 py-4">
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

