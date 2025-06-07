import {useEffect, useMemo, useState} from "react";
import { useParams } from "react-router-dom";
import { Badge } from "@/components/ui/badge"
import {BookOpen, Headphones, PenLine, Mic, CheckCircle, BrainCircuit, Lightbulb } from "lucide-react";
import {Card, CardContent, CardHeader, CardTitle,} from "@/components/ui/card"
import {Button} from "@/components/ui/button.tsx";

export interface Section{
    question: string;
    option: string[];
    correctAnswer: string | string[];
    explanation: string;
}
export interface Exercises {
    passage: string;
    instruction?: string;
    image?: string;
    section: Section[];
}

export interface TipDetail {
    id: string | number;
    type: string;
    skill: string;
    description: string;
    strategy?: string[];
    tips?: string[];
    exercises: Exercises[];
}
function TipDetail() {
    const passage = "Safaricom is the <h1>mobile phone company in Kenya. An M-Pesa account needs to be credited by <h1>. <h1> companies are particularly interested in using M-Pesa.";

    const [answers, setAnswers] = useState<{ [key: number]: string }>({});

    const [detail, setDetail] = useState<TipDetail | null>(null);

    const { skill, id } = useParams<{ skill: string; id: string }>();
    useEffect(() => {
        if (!id) return;

        fetch(`http://localhost:8080/api/${skill}/${id}`)
            .then((res) => res.json())
            .then((data: TipDetail) => {
                setDetail(data);
            })
            .catch((error) => {
                console.error("Lỗi khi gọi API chi tiết tip:", error);
                setDetail(null);
            });
    }, [id, skill]);

    if (!detail) {
        return (
            <div className="min-h-screen flex items-center justify-center text-red-500">
                Không tìm thấy dữ liệu.
            </div>
        );
    }

  //   const replaced = passage.split("<h1>").map((part, idx) => (
  //       <span key={idx}>
  //   {part}
  //           {idx !== passage.split("<h1>").length - 1 && (
  //               <input
  //                   type="text"
  //                   className="mx-1 border rounded px-2 py-1"
  //                   value={answers[idx] || ""}
  //                   onChange={(e) =>
  //                       setAnswers({ ...answers, [idx]: e.target.value })
  //                   }
  //               />
  //           )}
  // </span>
  //   ));
    return(

        <div className="min-h-screen bg-white">
            <section className="py-16 bg-gray-50">
                <div className="container mx-auto px-4">
                    {/* Reading Skill Content */}
                    <div className="max-w-4xl mx-auto">
                        <div className="mb-12">
                            <Badge className="bg-gray-100 text-emerald-800 font-normal mb-3 mr-300">
                                <BookOpen className="h-3 w-3 mr-1" />
                                {detail.skill}
                            </Badge>
                            <h2 className="text-3xl font-bold mb-3 text-left">{detail.type}</h2>
                        </div>
                        {/* Skill Definition */}
                        <Card className="border shadow-sm mb-10">
                            <CardHeader className="bg-gray-50 border-b">
                                <div className="flex items-center gap-2">
                                    <BookOpen className="h-5 w-5 text-emerald-600" />
                                    <CardTitle>Understanding IELTS {detail.skill}</CardTitle>
                                </div>
                            </CardHeader>
                            <CardContent className="p-6">
                                <div className="prose max-w-none text-left">
                                    {detail.description}
                                </div>
                            </CardContent>
                        </Card>
                        {/* Example Illustration*/}
                        {/*<div className="mb-10">*/}
                        {/*    <div className="flex items-center gap-2 mb-4">*/}
                        {/*        <PenLine className="h-5 w-5 text-emerald-600" />*/}
                        {/*        <h3 className="text-xl font-bold">Example Illustration</h3>*/}
                        {/*    </div>*/}
                        {/*    <p className="text-gray-600 mb-4">Sample reading passage with question types</p>*/}

                        {/*    <div className="relative w-full h-[400px] mb-4 border rounded-lg overflow-hidden">*/}
                        {/*        <img*/}
                        {/*            src="/images/12345.png" height={1000} width={800}*/}
                        {/*            alt="IELTS Reading Example"*/}
                        {/*            className="object-contain"*/}
                        {/*        />*/}
                        {/*    </div>*/}
                        {/*</div>*/}
                        {/* Strategy Section */}
                        <Card className="border shadow-sm mb-10">
                            <CardHeader className="bg-gray-50 border-b">
                                <div className="flex items-center gap-2">
                                    <BrainCircuit className="h-5 w-5 text-emerald-600" />
                                    <CardTitle>{detail.skill} Strategies</CardTitle>
                                </div>
                            </CardHeader>
                            <CardContent className="p-6">
                                <div className="grid gap-6">
                                    {detail.strategy?.map((s, idx) => (
                                        <div key={idx} className="flex items-start gap-4">
                                            <div className="bg-emerald-100 p-2 rounded-full">
                                                <span className="font-bold text-emerald-700">{idx + 1}</span>
                                            </div>
                                            <div>
                                                <h3 className="font-semibold text-lg text-left">{s}</h3>
                                                {/*<p className="text-gray-600 text-left">{s}</p>*/}
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </CardContent>
                        </Card>
                        {/* Tips Section */}
                            <Card className="border shadow-sm mb-10">
                                <CardHeader className="bg-gray-50 border-b">
                                    <div className="flex items-center gap-2">
                                        <Lightbulb className="h-5 w-5 text-emerald-600" />
                                        <CardTitle>Essential Tips</CardTitle>
                                    </div>
                                </CardHeader>
                                <CardContent className="p-6">
                                    <div className="grid gap-4">
                                        {detail.tips?.map((t, idx) => (
                                            <div key={idx} className="flex items-start gap-3">
                                                <CheckCircle className="h-5 w-5 text-emerald-500 mt-1" />
                                                <div>
                                                    <h3 className="font-medium text-left">{t}</h3>
                                                    {/*<p className="text-sm text-gray-600 text-left">*/}
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                </CardContent>
                            </Card>
                        {/* Practice Exercise */}
                        <Card className="border shadow-sm mb-10">
                            <CardHeader className="bg-gray-50 border-b text-left">
                                <CardTitle>Practice Exercise</CardTitle>
                            </CardHeader>
                            <CardContent className="p-6 text-left">
                                <>
                                    {/* Passage */}
                                    {/*<div className="flex items-start gap-3 mb-4">*/}
                                    {/*    <h3 className="font-medium text-center">{detail.passage[0]}</h3>*/}
                                    {/*</div>*/}

                                    {detail.exercises.map((p, idx) => (
                                        <div key={idx} className="flex items-start gap-3">
                                            <div>
                                                <p className="text-left">{p.passage}</p>
                                            </div>
                                        </div>
                                    ))}
                                     {/*Question*/}
                                    {/*{detail.exercises?.map((ex, idx) => (*/}
                                    {/*    <p key={idx} className="text-left">*/}
                                    {/*        <div className="bg-gray-50 p-4 rounded-lg my-4 border">*/}
                                                {/*<p className="font-medium">*/}
                                                    {/*{ex.question}*/}
                                                    {/*<p className="text-left text-lg leading-7">*/}
                                                    {/*    {replaced}*/}
                                                    {/*</p>*/}
                                    {/*            </p>*/}
                                    {/*        </div>*/}
                                    {/*    </p>*/}
                                    {/*))}*/}
                                    {/*{detail.exercises?.map((ex, idx) => (*/}
                                    {/*    <div key={idx} className="prose max-w-none">*/}
                                    {/*        /!*<h4>Questions {idx + 1}:</h4>*!/*/}
                                    {/*        {ex.options && (*/}
                                    {/*            <ul className="list-disc ml-6 space-y-1">*/}
                                    {/*                {ex.options.map((opt) => (*/}
                                    {/*                    <li key={opt}>{opt}</li>*/}
                                    {/*                ))}*/}
                                    {/*            </ul>*/}
                                    {/*        )}*/}
                                             {/*Ẩn/hiện đáp án*/}
                                            {/*<details className="border-l-4 border-emerald-600 pl-3 mt-2">*/}
                                            {/*    <summary className="cursor-pointer text-emerald-700">*/}
                                            {/*        Answer & Explanation*/}
                                            {/*    </summary>*/}
                                            {/*    <p className="mt-1">*/}
                                            {/*        <strong>Answer:</strong>{" "}*/}
                                            {/*        {Array.isArray(ex.correctAnswer)*/}
                                            {/*            ? ex.correctAnswer.join(", ")*/}
                                            {/*            : ex.correctAnswer}*/}
                                            {/*    </p>*/}
                                            {/*    <p className="text-sm text-gray-600">{ex.explanation}</p>*/}
                                            {/*</details>*/}
                                        {/*</div>*/}
                                    {/*))}*/}
                                </>
                            </CardContent>
                        </Card>
                        {/* Optional back button */}
                        <div className="px-6 pb-6">
                            <Button className="w-full bg-emerald-600 hover:bg-emerald-700"
                                    onClick={() => history.back()}>← Back</Button>
                        </div>
                    </div>
                </div>
            </section>
        </div>
    )
};

export default TipDetail;