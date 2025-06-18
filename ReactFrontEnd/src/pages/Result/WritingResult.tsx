import { useEffect, useState } from "react";
import { ChevronDown, ChevronUp } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Collapsible, CollapsibleContent, CollapsibleTrigger } from "@/components/ui/collapsible";
import {useParams} from "react-router-dom";


interface Evaluation {
    TaskAchievement: string;
    CoherenceCohesion: string;
    LexicalResource: string;
    Grammar: string;
}

interface WritingAnswer {
    id: string;
    username?: string;
    testId: string;
    task1: TaskWritingAnswer;
    task2: TaskWritingAnswer;
}
interface ErrorCorrection {
    originalText: string;
    correctedText: string;
    errorType: string;
    explanation: string;
    startIndex: number;
    endIndex: number;
}


interface SentenceImprovement {
    originalSentence: string;
    improvedSentence: string;
    techniquesUsed: string[];
    bandBoost: string;
}

interface Feedback {
    errorCorrections: ErrorCorrection[];
    sentenceImprovements: SentenceImprovement[];
    overallComment: string;
}

interface TaskWritingAnswer {
    score: string;
    type: string;
    question: string;
    imageUrl?: string;
    answer: string;
    wordCount: string;
    feedback: Feedback;
    evaluation?: Evaluation;
    sampleAnswer: string;
}


export default function WritingResult() {
    const [data, setData] = useState<WritingAnswer | null>(null);
    const [loading, setLoading] = useState(true);
    const [openSection, setOpenSection] = useState<{
        questions1: boolean;
        answer1: boolean;
        questions2: boolean;
        answer2: boolean;
    }>({
        questions1: false,
        answer1: true,
        questions2: false,
        answer2: false,
    });
    const { resultId } = useParams<{ resultId: string }>();

    useEffect(() => {
        fetch(`http://localhost:8080/api/result/${resultId}`)
            .then(res => {
                if (!res.ok) throw new Error("Failed to fetch data");
                return res.json();
            })
            .then(json => setData(json))
            .catch(err => console.error("Fetch error:", err))
            .finally(() => setLoading(false));
    }, [resultId]);

    if (loading) return <div className="p-8 text-center">Loading...</div>;
    if (!data) return <div className="p-8 text-center">No data found</div>;
    // Highlight errors by matching originalText only (no position logic)
    const renderTextWithCorrectionsByPosition = (answer: string, corrections: ErrorCorrection[]) => {
        if (!corrections || corrections.length === 0) {
            return (
                <div className="whitespace-pre-line p-4 bg-gray-50 rounded border border-gray-200">
                    {answer}
                </div>
            );
        }
        return renderTextBasedHighlighting(answer, corrections);
    };

    // Simple text-based highlighting (no position, no debug)
    const renderTextBasedHighlighting = (answer: string, corrections: ErrorCorrection[]) => {
        let highlightedText = answer;
        // Sort by length to avoid partial matches
        const sortedCorrections = [...corrections].sort((a, b) => b.originalText.length - a.originalText.length);
        sortedCorrections.forEach(correction => {
            if (correction.originalText && correction.originalText.length > 0) {
                const regex = new RegExp(escapeRegExp(correction.originalText), 'g');
                const replacement = `<mark class=\"bg-red-100 text-red-700 font-semibold rounded-sm px-1\" title=\"${correction.explanation}\">${correction.originalText}</mark>`;
                highlightedText = highlightedText.replace(regex, replacement);
            }
        });
        return (
            <div
                className="whitespace-pre-line p-4 bg-gray-50 rounded border border-gray-200"
                dangerouslySetInnerHTML={{ __html: highlightedText }}
            />
        );
    };

    // Utility for regex escaping
    const escapeRegExp = (string: string) => {
        return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    };

    const renderFeedback = (originalText: string, feedback: Feedback) => (
        <div className="space-y-8">
            {/*/!* Debug information *!/*/}
            {/*{process.env.NODE_ENV === 'development' && (*/}
            {/*    <div className="bg-blue-50 p-4 rounded border border-blue-200">*/}
            {/*        <h4 className="font-medium mb-2 text-blue-800">Debug Information</h4>*/}
            {/*        <div className="text-sm text-blue-700 space-y-1">*/}
            {/*            <p>Total corrections: {feedback.errorCorrections.length}</p>*/}
            {/*            <p>Text length: {originalText.length}</p>*/}
            {/*            {feedback.errorCorrections.map((correction, index) => (*/}
            {/*                <div key={index} className="ml-4">*/}
            {/*                    <p>Correction {index + 1}:</p>*/}
            {/*                    <p className="ml-4">Text: "{correction.originalText}"</p>*/}
            {/*                    <p className="ml-4">Position: {correction.startIndex}-{correction.endIndex}</p>*/}
            {/*                </div>*/}
            {/*            ))}*/}
            {/*        </div>*/}
            {/*    </div>*/}
            {/*)}*/}
            {/* Highlight lỗi theo vị trí */}
            <div>
                <h3 className="text-lg font-semibold mb-3 text-emerald-700">Text with Errors Highlighted</h3>
                {renderTextWithCorrectionsByPosition(originalText, feedback.errorCorrections)}
                <div className="mt-4 grid grid-cols-1 md:grid-cols-2 gap-4">
                    {feedback.errorCorrections.map((error, index) => (
                        <div key={index} className="bg-white p-3 rounded border border-gray-200">
                            <p className="text-sm">
                                <strong>Error:</strong> <span className="text-red-500">{error.originalText}</span>
                            </p>
                            <p className="text-sm">
                                <strong>Correction:</strong> <span className="text-green-600">{error.correctedText}</span>
                            </p>
                            <p className="text-xs text-gray-500 mt-1">{error.errorType} • {error.explanation}</p>
                            {process.env.NODE_ENV === 'development' && (
                                <p className="text-xs text-blue-500 mt-1">Position: {error.startIndex}-{error.endIndex}</p>
                            )}
                        </div>
                    ))}
                </div>
            </div>
            {/* Overall comment */}
            <div className="bg-white p-4 rounded-lg shadow">
                <h3 className="text-lg font-semibold mb-2 text-emerald-700">Overall Feedback</h3>
                <p className="text-gray-700 whitespace-pre-line">{feedback.overallComment}</p>
            </div>
        </div>
    );

    const renderTask = (task: TaskWritingAnswer, taskTitle: string, openKeyQ: keyof typeof openSection, openKeyA: keyof typeof openSection) => (
        <div className="mb-8">
            <div className="bg-emerald-600 text-white px-6 py-3 rounded-t-lg">
                <h2 className="text-lg font-semibold">{taskTitle.toUpperCase()}</h2>
            </div>

            {/* Question */}
            <Collapsible open={openSection[openKeyQ]} onOpenChange={(v) => setOpenSection(prev => ({ ...prev, [openKeyQ]: v }))}>
                <CollapsibleTrigger asChild>
                    <Button variant="ghost" className="bg-stone-100 w-full justify-between p-4 font-semibold text-slate-700 border-x border-gray-200">
                        Question {openSection[openKeyQ] ? <ChevronUp /> : <ChevronDown />}
                    </Button>
                </CollapsibleTrigger>
                <CollapsibleContent>
                    <div className="bg-gray-50 p-6 border-x border-gray-200">
                        <p className="text-gray-700">{task.question}</p>
                    </div>
                </CollapsibleContent>
            </Collapsible>

            {/* Answer */}
            <Collapsible open={openSection[openKeyA]} onOpenChange={(v) => setOpenSection(prev => ({ ...prev, [openKeyA]: v }))}>
                <CollapsibleTrigger asChild>
                    <Button variant="ghost" className="w-full justify-between p-4 bg-orange-500 text-white hover:bg-orange-500 border-x border-gray-200">
                        Answer & Score {openSection[openKeyA] ? <ChevronUp /> : <ChevronDown />}
                    </Button>
                </CollapsibleTrigger>
                <CollapsibleContent>
                    <div className="bg-orange-50 p-6 border-x border-orange-200">
                        <p className="mb-2 text-gray-800 whitespace-pre-line">{task.answer}</p>
                        <p className="text-sm text-gray-600">Word Count: {task.wordCount}</p>
                    </div>
                </CollapsibleContent>
            </Collapsible>

            {/* Feedback */}
            {/* Feedback */}
            <Collapsible>
                <CollapsibleTrigger asChild>
                    <Button variant="ghost" className="bg-stone-100 w-full justify-between p-4 font-semibold text-slate-700 border-x border-gray-200">
                        Detailed Feedback <ChevronDown />
                    </Button>
                </CollapsibleTrigger>
                <CollapsibleContent>
                    <div className="bg-gray-50 p-6 border-x border-gray-200">
                        {task.feedback && renderFeedback(task.answer, task.feedback)}

                        {/* Phần evaluation */}
                        <div className="mt-6 pt-6 border-t border-gray-200">
                            <h4 className="font-medium mb-2">Scoring Breakdown</h4>
                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <p><strong>Task Achievement:</strong> {task.evaluation?.TaskAchievement}</p>
                                    <p><strong>Coherence & Cohesion:</strong> {task.evaluation?.CoherenceCohesion}</p>
                                </div>
                                <div>
                                    <p><strong>Lexical Resource:</strong> {task.evaluation?.LexicalResource}</p>
                                    <p><strong>Grammar:</strong> {task.evaluation?.Grammar}</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </CollapsibleContent>
            </Collapsible>


            {/* Sample Answer */}
            <Collapsible>
                <CollapsibleTrigger asChild>
                    <Button variant="ghost" className="bg-stone-100 w-full justify-between p-4 font-semibold text-slate-700 border border-gray-200 rounded-b-lg">
                        Sample Answer <ChevronDown />
                    </Button>
                </CollapsibleTrigger>
                <CollapsibleContent>
                    <div className="bg-gray-50 p-6 border-x border-b border-gray-200 rounded-b-lg whitespace-pre-line">
                        {task.sampleAnswer}
                    </div>
                </CollapsibleContent>
            </Collapsible>
        </div>
    );

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
                {/* Scores */}
                <div className="bg-emerald-600 rounded-2xl p-8 mb-6 text-white">
                    <div className="text-center mb-6">
                        <div className="text-sm text-gray-200">FINAL SCORE FROM</div>
                        <div className="text-2xl font-bold">AI Examiner Evaluation</div>
                    </div>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-center">
                        <Card><CardContent className="p-6"><div className="text-sm text-gray-600">Overall</div><div className="text-4xl font-bold text-orange-500">{}</div></CardContent></Card>
                        <Card><CardContent className="p-6"><div className="text-sm text-gray-600">Task 1</div><div className="text-4xl font-bold text-slate-700">{data.task1.score}</div></CardContent></Card>
                        <Card><CardContent className="p-6"><div className="text-sm text-gray-600">Task 2</div><div className="text-4xl font-bold text-slate-700">{data.task2.score}</div></CardContent></Card>
                    </div>
                </div>

                {/* Overall Score Box */}
                <div className="bg-orange-500 rounded-lg p-4 mb-6 flex items-center justify-between text-white">
                    <div className="text-xl font-semibold">Overall Score</div>
                    <div className="text-2xl font-bold">{}</div>
                </div>


                {/* Task 1 */}
                {renderTask(data.task1, "Task 1", "questions1", "answer1")}

                {/* Task 2 */}
                {renderTask(data.task2, "Task 2", "questions2", "answer2")}
            </div>
        </div>
    );
}
