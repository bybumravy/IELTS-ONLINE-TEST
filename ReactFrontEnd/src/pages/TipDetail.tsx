import {useEffect, useMemo, useState} from "react";
import { useParams } from "react-router-dom";
import { Badge } from "@/components/ui/badge"
import {BookOpen, Headphones, PenLine, Mic, CheckCircle, BrainCircuit, Lightbulb } from "lucide-react";
import {Card, CardContent, CardHeader, CardTitle,} from "@/components/ui/card"
import {Button} from "@/components/ui/button.tsx";

export interface Section{
    question: string;
    options?: string[];
    answer: string | string[];
    explanation: string | string[];
}
export interface Exercises {
    paragraph: string;
    instruction: string;
    imageUrl?: string;
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
                                    {detail.exercises.map((exercise, idx) => (
                                        <div key={idx} className="mb-12">
                                            {/* Hiển thị hình ảnh */}
                                            {exercise.imageUrl && (
                                                <div className="mb-4">
                                                    <img
                                                        src={exercise.imageUrl}
                                                        alt="Diagram"
                                                        className="w-full h-auto border rounded-lg"
                                                    />
                                                </div>
                                            )}
                                            {/* Hiển thị đoạn paragraph với html */}
                                            <div className="mb-4 prose max-w-none" dangerouslySetInnerHTML={{ __html: exercise.paragraph }} />

                                            {/* Hiển thị instruction */}
                                            <p className="mb-6 font-semibold italic" dangerouslySetInnerHTML={{ __html: exercise.instruction}}/>

                                            {/* Hiển thị từng câu hỏi */}
                                            {exercise.section.map((q, qIdx) => (
                                                <div key={qIdx} className="mb-6 p-4 border rounded-lg bg-gray-50">
                                                    <div className="flex items-center gap-2">
                                                        {(() => {
                                                            // Multiple Choice (True/False/Not Given)
                                                            if (q.options && q.options.length > 0 && q.options.includes("True")) {
                                                                return (
                                                                    <div className="ml-2 inline-block">
                                                                        <label className="inline-block mb-1 font-semibold" htmlFor={`input-${idx}-${qIdx}`}>
                                                                            {qIdx + 1}.
                                                                        </label>
                                                                        <p className="font-medium inline-block px-2">{q.question}</p>
                                                                        <select
                                                                            id={`input-${idx}-${qIdx}`}
                                                                            className="border rounded px-2 py-1 mt-1 inline-block"
                                                                            defaultValue=""
                                                                        >
                                                                            <option value="" disabled></option>
                                                                            {q.options.map((opt, i) => (
                                                                                <option key={i} value={opt}>
                                                                                    {opt}
                                                                                </option>
                                                                            ))}
                                                                        </select>
                                                                    </div>
                                                                );
                                                            }
                                                            // Sentence Completion (nhiều <h1> trong question)
                                                            else if ((q.question.match(/<h1>/g) || []).length > 1) {
                                                                const parts = q.question.split("<h1>");
                                                                return (
                                                                    <div className="ml-2">
                                                                        {parts.map((part, pIdx) => (
                                                                            <span key={pIdx} className="inline-flex items-center">
                                                                            <p className="font-medium">{part}</p>
                                                                                {pIdx < parts.length - 1 && (
                                                                                    <input
                                                                                        type="text"
                                                                                        className="border rounded px-1 py-1 mx-1"
                                                                                        placeholder=""
                                                                                    />
                                                                                )}
                                                                        </span>
                                                                        ))}
                                                                    </div>
                                                                );
                                                            }
                                                            // Diagram Completion (một <h1> trong question)
                                                            else if ((q.question.match(/<h1>/g) || []).length === 1) {
                                                                const parts = q.question.split("<h1>");
                                                                return (
                                                                    <div className="ml-2 inline-block">
                                                                        <p className="font-medium inline-block">{parts[0]}</p>
                                                                        <input
                                                                            type="text"
                                                                            id={`input-${idx}-${qIdx}`}
                                                                            className="border rounded px-2 py-1 ml-2 inline-block"
                                                                            placeholder=""
                                                                        />
                                                                    </div>
                                                                );
                                                            }
                                                            return null; // Trường hợp không xác định
                                                        })()}
                                                    </div>
                                                    {/* Đáp án đúng */}
                                                    <details className="border-l-4 border-emerald-600 pl-3 mt-2">
                                                        <summary className="cursor-pointer text-emerald-700">
                                                            Answer & Explanation
                                                        </summary>
                                                        <p className="mt-1">
                                                            <strong>Answer:</strong> {Array.isArray(q.answer) ? q.answer : q.answer}

                                                    </p>
                                                        <p className="text-sm text-gray-600">{q.explanation}</p>
                                                    </details>

                                                </div>
                                            ))}
                                        </div>
                                    ))}
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