import { useState, useEffect } from "react";
import { Textarea } from "@/components/ui/textarea";
import { DoTestHeader } from "@/components/layout/doTest/DoTestHeader";
import {useAuth} from "@/contexts/AuthContext";

interface WritingTask {
    type: string;
    question: string;
    imageUrl?: string;
}

interface WritingData {
    testId: string;
    task1: WritingTask;
    task2: WritingTask;
}

export default function IELTSWritingPractice() {
    const { user } = useAuth();
    const [currentTask, setCurrentTask] = useState(1);
    const [essayTask1, setEssayTask1] = useState("");
    const [essayTask2, setEssayTask2] = useState("");
    const [wordCountTask1, setWordCountTask1] = useState(0);
    const [wordCountTask2, setWordCountTask2] = useState(0);
    const [writingData, setWritingData] = useState<WritingData | null>(null);
    const [timeRemaining, setTimeRemaining] = useState(60 * 60);
    const [isSubmitting, setIsSubmitting] = useState(false);

    // // Timer
    // useEffect(() => {
    //     const timer = setInterval(() => {
    //         setTimeRemaining((prev) => {
    //             if (prev <= 0) {
    //                 clearInterval(timer);
    //                 return 0;
    //             }
    //             return prev - 1;
    //         });
    //     }, 1000);
    //     return () => clearInterval(timer);
    // }, []);

    // Fetch writing data
    useEffect(() => {
        fetch("http://localhost:8080/api/writing/t01")
            .then((res) => res.json())
            .then((data) => setWritingData(data))
            .catch((err) => console.error("Error fetching writing data:", err));
    }, []);

    // Word count Task 1
    useEffect(() => {
        const words = essayTask1.trim().split(/\s+/).filter((w) => w.length > 0);
        setWordCountTask1(words.length);
    }, [essayTask1]);

    // Word count Task 2
    useEffect(() => {
        const words = essayTask2.trim().split(/\s+/).filter((w) => w.length > 0);
        setWordCountTask2(words.length);
    }, [essayTask2]);

    const handleSubmit = async () => {
        if (!writingData) return;

        const task1Submission = essayTask1.trim()
            ? {
                type: writingData.task1.type,
                question: writingData.task1.question,
                imageUrl: writingData.task1.imageUrl,
                answer: essayTask1.trim(),
                wordCount: wordCountTask1.toString(),
            }
            : null;

        const task2Submission = essayTask2.trim()
            ? {
                type: writingData.task2.type,
                question: writingData.task2.question,
                answer: essayTask2.trim(),
                wordCount: wordCountTask2.toString(),
            }
            : null;

        if (!task1Submission && !task2Submission) {
            alert("You haven't written anything.");
            return;
        }

        const payload = {
            testId: writingData.testId,
            username: user?.username,
            task1: task1Submission,
            task2: task2Submission,
        };

        setIsSubmitting(true);
        try {
            const response = await fetch("http://localhost:8080/api/writing/submit", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload),
            });

            if (!response.ok) throw new Error("Failed to submit writing");

            const result = await response.json();
            console.log("Writing saved:", result);
            alert("Your essay has been submitted successfully!");
        } catch (error) {
            console.error("Error submitting writing:", error);
            alert("Submit failed. Please try again.");
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className="min-h-screen bg-gray-50">
            <DoTestHeader initialTime={60 * 60} onSubmit={handleSubmit} />

            <div className="flex h-[calc(100vh-80px)]">
                {/* Left Panel */}
                <div className="w-1/2 bg-white p-6 overflow-y-auto border-r border-gray-200">
                    {currentTask === 1 ? (
                        <div>
                            <h1 className="text-xl font-bold text-gray-800 mb-2">WRITING TASK 1</h1>
                            <p className="text-sm text-gray-600 mb-4">You should spend about <strong>20 minutes</strong> on this task.</p>
                            <p className="text-sm text-gray-700 mb-4">{writingData?.task1?.question || "Loading..."}</p>
                            <p className="text-sm text-gray-700 mb-6">You should write <strong>at least 150 words</strong>.</p>
                        </div>
                    ) : (
                        <div>
                            <h1 className="text-xl font-bold text-gray-800 mb-2">WRITING TASK 2</h1>
                            <p className="text-sm text-gray-600 mb-4">You should spend about <strong>40 minutes</strong> on this task.</p>
                            <p className="text-sm text-gray-700 mb-4">{writingData?.task2?.question || "Loading..."}</p>
                            <p className="text-sm text-gray-700 mb-6">Write <strong>at least 250 words</strong>.</p>
                        </div>
                    )}
                </div>

                {/* Right Panel */}
                <div className="w-1/2 bg-gray-50 p-6 flex flex-col">
                    {currentTask === 1 ? (
                        <>
                            <Textarea
                                placeholder="Type your essay for Task 1 here..."
                                value={essayTask1}
                                onChange={(e) => setEssayTask1(e.target.value)}
                                className="flex-1 resize-none border-gray-300 focus:border-teal-500 focus:ring-teal-500"
                            />
                            <div className="mt-4 flex justify-between items-center">
                                <div className="text-sm text-gray-600">Words Count: <span className="font-medium">{wordCountTask1}</span></div>
                            </div>
                        </>
                    ) : (
                        <>
                            <Textarea
                                placeholder="Type your essay for Task 2 here..."
                                value={essayTask2}
                                onChange={(e) => setEssayTask2(e.target.value)}
                                className="flex-1 resize-none border-gray-300 focus:border-teal-500 focus:ring-teal-500"
                            />
                            <div className="mt-4 flex justify-between items-center">
                                <div className="text-sm text-gray-600">Words Count: <span className="font-medium">{wordCountTask2}</span></div>
                            </div>
                        </>
                    )}
                </div>
            </div>

            {/* Bottom Navigation */}
            {writingData && writingData.task1 && writingData.task2 && (
                <div className="sticky bottom-0 bg-white border-t border-gray-200 p-4">
                    <div className="max-w-8xl mx-auto grid grid-cols-2 gap-4">
                        {[1, 2].map((taskNumber) => {
                            const isActive = currentTask === taskNumber;
                            return (
                                <div
                                    key={taskNumber}
                                    onClick={() => setCurrentTask(taskNumber)}
                                    className={`border rounded-lg p-4 cursor-pointer transition duration-200 text-center ${
                                        isActive
                                            ? "border-teal-500 bg-teal-50 text-teal-700"
                                            : "border-gray-200 bg-white hover:bg-gray-50 text-gray-800"
                                    }`}
                                >
                                    <h3 className="font-semibold text-sm">Task {taskNumber}</h3>
                                </div>
                            );
                        })}
                    </div>
                </div>
            )}
        </div>
    );
}
