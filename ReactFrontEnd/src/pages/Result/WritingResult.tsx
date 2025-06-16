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

interface TaskWritingAnswer {
    type: string;
    question: string;
    imageUrl?: string;
    answer: string;
    wordCount: string;
    feedback: string;
    evaluation?: Evaluation;
    sampleAnswer: string;
}

interface WritingAnswer {
    id: string;
    username?: string;
    testId: string;
    overallScore?: string;
    task1: TaskWritingAnswer;
    task2: TaskWritingAnswer;
}


export default function WritingResult() {
    const [data, setData] = useState<WritingAnswer | null>(null);
    const [loading, setLoading] = useState(true);
    const [openSection, setOpenSection] = useState({
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
    }, []);

    if (loading) return <div className="p-8 text-center">Loading...</div>;
    if (!data) return <div className="p-8 text-center">No data found</div>;

    const renderTask = (task: TaskWritingAnswer, taskTitle: string, openKeyQ: string, openKeyA: string) => (
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
            <Collapsible>
                <CollapsibleTrigger asChild>
                    <Button variant="ghost" className="bg-stone-100 w-full justify-between p-4 font-semibold text-slate-700 border-x border-gray-200">
                        Evaluation <ChevronDown />
                    </Button>
                </CollapsibleTrigger>
                <CollapsibleContent>
                    <div className="bg-gray-50 p-6 border-x border-gray-200 space-y-2">
                        <p><strong>Task Achievement:</strong> {task.evaluation?.TaskAchievement}</p>
                        <p><strong>Coherence & Cohesion:</strong> {task.evaluation?.CoherenceCohesion}</p>
                        <p><strong>Lexical Resource:</strong> {task.evaluation?.LexicalResource}</p>
                        <p><strong>Grammar:</strong> {task.evaluation?.Grammar}</p>
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
                        <Card><CardContent className="p-6"><div className="text-sm text-gray-600">Overall</div><div className="text-4xl font-bold text-orange-500">{data.overallScore}</div></CardContent></Card>
                        <Card><CardContent className="p-6"><div className="text-sm text-gray-600">Task 1</div><div className="text-4xl font-bold text-slate-700">{data.task1?.evaluation ? "6.0" : "-"}</div></CardContent></Card>
                        <Card><CardContent className="p-6"><div className="text-sm text-gray-600">Task 2</div><div className="text-4xl font-bold text-slate-700">{data.task2?.evaluation ? "6.5" : "-"}</div></CardContent></Card>
                    </div>
                </div>

                {/* Overall Score Box */}
                <div className="bg-orange-500 rounded-lg p-4 mb-6 flex items-center justify-between text-white">
                    <div className="text-xl font-semibold">Overall Score</div>
                    <div className="text-2xl font-bold">{data.overallScore}</div>
                </div>


                {/* Task 1 */}
                {renderTask(data.task1, "Task 1", "questions1", "answer1")}

                {/* Task 2 */}
                {renderTask(data.task2, "Task 2", "questions2", "answer2")}
            </div>
        </div>
    );
}
