import { useState } from "react"
import { ChevronDown, ChevronUp, Home } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Collapsible, CollapsibleContent, CollapsibleTrigger } from "@/components/ui/collapsible"

export default function Component() {
    const [generalFeedbackOpen, setGeneralFeedbackOpen] = useState(true)
    const [questionsOpen, setQuestionsOpen] = useState(false)
    const [answerCorrectionOpen, setAnswerCorrectionOpen] = useState(true)

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Main Content */}
            <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 pb-12 mt-6">
                {/* Score Section */}
                <div className="bg-slate-700 rounded-2xl p-8 mb-6">
                    <div className="text-center mb-6">
                        <div className="text-gray-300 text-sm mb-2">FINAL SCORE FROM</div>
                        <div className="text-white text-2xl font-bold">AI Examiner Evaluation</div>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                        <Card className="bg-white">
                            <CardContent className="p-6 text-center">
                                <div className="text-gray-600 text-sm mb-2">Overall</div>
                                <div className="text-4xl font-bold text-orange-500">6.0</div>
                            </CardContent>
                        </Card>

                        <Card className="bg-white">
                            <CardContent className="p-6 text-center">
                                <div className="text-gray-600 text-sm mb-2">Task 1</div>
                                <div className="text-4xl font-bold text-slate-700">6.0</div>
                            </CardContent>
                        </Card>

                        <Card className="bg-white">
                            <CardContent className="p-6 text-center">
                                <div className="text-gray-600 text-sm mb-2">Task 2</div>
                                <div className="text-4xl font-bold text-slate-700">6.5</div>
                            </CardContent>
                        </Card>
                    </div>
                </div>

                {/* Overall Score */}
                <div className="bg-orange-400 rounded-lg p-4 mb-6 flex items-center justify-between">
                    <div className="text-white text-xl font-semibold">Overall Score</div>
                    <div className="text-white text-2xl font-bold">6.0</div>
                </div>

                {/* General Feedback */}
                <Collapsible open={generalFeedbackOpen} onOpenChange={setGeneralFeedbackOpen}>

                    <CollapsibleContent>
                        <div className="bg-gray-100 p-6 rounded-lg mb-6">
                            <p className="text-gray-700 leading-relaxed">
                                Your essays show a good understanding of the topics. However, attention to grammatical accuracy,
                                vocabulary usage, and the flow of ideas could be improved to enhance overall coherence and clarity of
                                your arguments.
                            </p>
                        </div>
                    </CollapsibleContent>
                </Collapsible>

                {/* Task 1 Section */}
                <div className="mb-8">
                    <div className="bg-slate-700 text-white px-6 py-3 rounded-t-lg">
                        <h2 className="text-lg font-semibold">TASK 1</h2>
                    </div>

                    {/* Questions */}
                    <Collapsible open={questionsOpen} onOpenChange={setQuestionsOpen}>
                        <CollapsibleTrigger asChild>
                            <Button
                                variant="ghost"
                                className="w-full justify-between p-4 h-auto text-left font-semibold text-slate-700 hover:bg-gray-100 border-x border-gray-200"
                            >
                                Questions
                                {questionsOpen ? <ChevronUp className="h-4 w-4" /> : <ChevronDown className="h-4 w-4" />}
                            </Button>
                        </CollapsibleTrigger>
                        <CollapsibleContent>
                            <div className="bg-gray-50 p-6 border-x border-gray-200">
                                <p className="text-gray-700">The Task 1 question content would be displayed here when expanded.</p>
                            </div>
                        </CollapsibleContent>
                    </Collapsible>

                    {/* Answer & Score */}
                    <Collapsible open={answerCorrectionOpen} onOpenChange={setAnswerCorrectionOpen}>
                        <CollapsibleTrigger asChild>
                            <Button
                                variant="ghost"
                                className="w-full justify-between p-4 h-auto text-left font-semibold bg-orange-400 text-white hover:bg-orange-500 border-x border-gray-200"
                            >
                                Answer & Score
                                {answerCorrectionOpen ? <ChevronUp className="h-4 w-4" /> : <ChevronDown className="h-4 w-4" />}
                            </Button>
                        </CollapsibleTrigger>
                        <CollapsibleContent>
                            <div className="bg-orange-50 p-6 border-x border-orange-200">
                                <p className="text-gray-700">Student's answer and scoring details would be displayed here.</p>
                            </div>
                        </CollapsibleContent>
                    </Collapsible>

                    {/* Evaluation */}
                    <Collapsible>
                        <CollapsibleTrigger asChild>
                            <Button
                                variant="ghost"
                                className="w-full justify-between p-4 h-auto text-left font-semibold text-slate-700 hover:bg-gray-100 border-x border-gray-200"
                            >
                                Evaluation
                                <ChevronDown className="h-4 w-4" />
                            </Button>
                        </CollapsibleTrigger>
                        <CollapsibleContent>
                            <div className="bg-gray-50 p-6 border-x border-gray-200">
                                <p className="text-gray-700">Detailed evaluation and feedback for Task 1 would be displayed here.</p>
                            </div>
                        </CollapsibleContent>
                    </Collapsible>

                    {/* Sample Answer */}
                    <Collapsible>
                        <CollapsibleTrigger asChild>
                            <Button
                                variant="ghost"
                                className="w-full justify-between p-4 h-auto text-left font-semibold text-slate-700 hover:bg-gray-100 border border-gray-200 rounded-b-lg"
                            >
                                Sample Answer
                                <ChevronDown className="h-4 w-4" />
                            </Button>
                        </CollapsibleTrigger>
                        <CollapsibleContent>
                            <div className="bg-gray-50 p-6 border-x border-b border-gray-200 rounded-b-lg">
                                <p className="text-gray-700">Sample answer for Task 1 would be displayed here.</p>
                            </div>
                        </CollapsibleContent>
                    </Collapsible>
                </div>

                {/* Task 2 Section */}
                <div className="mb-8">
                    <div className="bg-slate-700 text-white px-6 py-3 rounded-t-lg">
                        <h2 className="text-lg font-semibold">TASK 2</h2>
                    </div>

                    {/* Questions */}
                    <Collapsible>
                        <CollapsibleTrigger asChild>
                            <Button
                                variant="ghost"
                                className="w-full justify-between p-4 h-auto text-left font-semibold text-slate-700 hover:bg-gray-100 border-x border-gray-200"
                            >
                                Questions
                                <ChevronDown className="h-4 w-4" />
                            </Button>
                        </CollapsibleTrigger>
                        <CollapsibleContent>
                            <div className="bg-gray-50 p-6 border-x border-gray-200">
                                <p className="text-gray-700">The Task 2 question content would be displayed here when expanded.</p>
                            </div>
                        </CollapsibleContent>
                    </Collapsible>

                    {/* Answer & Score */}
                    <Collapsible>
                        <CollapsibleTrigger asChild>
                            <Button
                                variant="ghost"
                                className="w-full justify-between p-4 h-auto text-left font-semibold bg-orange-400 text-white hover:bg-orange-500 border-x border-gray-200"
                            >
                                Answer & Score
                                <ChevronDown className="h-4 w-4" />
                            </Button>
                        </CollapsibleTrigger>
                        <CollapsibleContent>
                            <div className="bg-orange-50 p-6 border-x border-orange-200">
                                <p className="text-gray-700">Student's answer and scoring details would be displayed here.</p>
                            </div>
                        </CollapsibleContent>
                    </Collapsible>

                    {/* Evaluation */}
                    <Collapsible>
                        <CollapsibleTrigger asChild>
                            <Button
                                variant="ghost"
                                className="w-full justify-between p-4 h-auto text-left font-semibold text-slate-700 hover:bg-gray-100 border-x border-gray-200"
                            >
                                Evaluation
                                <ChevronDown className="h-4 w-4" />
                            </Button>
                        </CollapsibleTrigger>
                        <CollapsibleContent>
                            <div className="bg-gray-50 p-6 border-x border-gray-200">
                                <p className="text-gray-700">Detailed evaluation and feedback for Task 2 would be displayed here.</p>
                            </div>
                        </CollapsibleContent>
                    </Collapsible>

                    {/* Sample Answer */}
                    <Collapsible>
                        <CollapsibleTrigger asChild>
                            <Button
                                variant="ghost"
                                className="w-full justify-between p-4 h-auto text-left font-semibold text-slate-700 hover:bg-gray-100 border border-gray-200 rounded-b-lg"
                            >
                                Sample Answer
                                <ChevronDown className="h-4 w-4" />
                            </Button>
                        </CollapsibleTrigger>
                        <CollapsibleContent>
                            <div className="bg-gray-50 p-6 border-x border-b border-gray-200 rounded-b-lg">
                                <p className="text-gray-700">Sample answer for Task 2 would be displayed here.</p>
                            </div>
                        </CollapsibleContent>
                    </Collapsible>
                </div>
            </div>
        </div>
    )
}
