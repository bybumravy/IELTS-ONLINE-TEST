import React, {useEffect, useMemo, useState} from "react";
import { useParams } from "react-router-dom";
import { Badge } from "@/components/ui/badge"
import {BookOpen, Headphones, PenLine, Mic, CheckCircle, BrainCircuit, Lightbulb } from "lucide-react";
import {Card, CardContent, CardHeader, CardTitle,} from "@/components/ui/card"
import {Button} from "@/components/ui/button.tsx";

export interface Exercises {
    question: string;
    options?: string[];
    answer: string | string[];
    explanation: string;
}

export interface TipDetail {
    id: string | number;
    type: string;
    skill: string;
    description: string;
    strategy?: string[];
    tip?: string[];
    exercises: Exercises[];
}
function TipDetail() {

    // const sampleData: TipDetail[] = [
    //     {
    //         id: 3,
    //         type: "Form Completion",
    //         skill: "Reading",
    //         description: "Choose vocabulary from the reading to complete the sentence. Remember that these are words from the reading, do not create anything based on your own understanding or knowledge.",
    //         strategy: [
    //             "Read and analyze the question carefully, determine the type of word to fill in the blank.",
    //             "Locate the information you need to read in the article.",
    //             "Analyze the sentence structure in the passage to understand the meaning of the sentence.",
    //             "Compare the sentences in the passage and the question and choose the word to fill in the blank."
    //         ],
    //         tip: [
    //             "Read the instructions carefully to find out : How many words should you write for your answer and whether you should use the exact words from the text or synonyms?",
    //             "Read the sentences before you read the text.",
    //             "The answers appear in the order in the text that corresponds to the order of the list of questions.",
    //             "Completed sentences must be grammatically correct.",
    //             "Try to figure out what type of word is missing: noun, verb, adjective or adverb.",
    //             "Skim to find the location of the answer, then read the details to find the answer."
    //         ],
    //         exercises: [
    //             {
    //                 question: "The company was founded in ____.",
    //                 options: ["1995", "2001", "1987", "2010"],
    //                 answer: "1995",
    //                 explanation: "The reading passage states: 'The company was established in 1995 in London.'"
    //             },
    //             {
    //                 question: "Its first international branch opened in ____.",
    //                 options: ["Paris", "Berlin", "Tokyo", "New York"],
    //                 answer: "New York",
    //                 explanation: "The passage mentions that 'In 2000, it opened its first international office in New York.'"
    //             }
    //         ]
    //     },
    //     {
    //         id: 14,
    //         type: "Task 1 - Map/Diagram",
    //         skill: "Writing",
    //         description: "Candidates will be provided with a chart, image or diagram with missing labels.",
    //         strategy: [
    //             "Read the question and the related diagram or chart carefully.",
    //             "Identify the type of word or information that needs to go into each blank.",
    //             "Look for key words, synonyms, antonyms, or clues in the text.",
    //             "Put the appropriate word or phrase in the blank.",
    //             "Reread the entire passage to check for logic.",
    //             "Check your answers for grammar, spelling, or typing errors."
    //         ],
    //         tip: [
    //             "Identify the correct word type to fill in each blank.",
    //             "Choose the questions you know to do first.",
    //             "Read the question carefully to see how many words you are given.",
    //             "Answers are not always in the order of the passage."
    //         ],
    //         exercises: [
    //             {
    //                 question: "Label the diagram: The substance is first heated in the ____.",
    //                 options: ["burner", "beaker", "furnace", "boiler"],
    //                 answer: "boiler",
    //                 explanation: "The passage says: 'Initially, the mixture is heated in a large boiler until it reaches 100°C.'"
    //             },
    //             {
    //                 question: "The vapour then travels through the ____.",
    //                 options: ["filter", "pipe", "chamber", "valve"],
    //                 answer: "pipe",
    //                 explanation: "According to the text: 'Once heated, the vapour moves through a long pipe that leads to the cooling chamber.'"
    //             }
    //         ]
    //     },
    //     {
    //         id: 7,
    //         type: "Multiple Choices",
    //         skill: "Reading",
    //         description: "Choose vocabulary from the reading to complete the sentence. Remember that these are words from the reading, do not create anything based on your own understanding or knowledge.",
    //         strategy: [
    //             "Read the instructions and write down the number of words to fill in.",
    //             "Read the incomplete sentence and guess the correct type and meaning of the word.",
    //             "Predict the answer.",
    //             "Search for synonyms and phrases.",
    //             "Scan to find keywords and determine the answer.",
    //             "Correct spelling errors.",
    //             "Continue with another question."
    //         ],
    //         tip: [],
    //         exercises: [
    //             {
    //                 question: "What was the main cause of the accident?",
    //                 options: ["Driver fatigue", "Mechanical failure", "Poor weather", "High speed"],
    //                 answer: "Mechanical failure",
    //                 explanation: "The text clearly states: 'Investigators concluded the accident was due to a failure in the braking system.'"
    //             },
    //             {
    //                 question: "Why did the government delay the reform?",
    //                 options: ["Lack of funding", "Public opposition", "Insufficient data", "Change in leadership"],
    //                 answer: "Change in leadership",
    //                 explanation: "The article mentions: 'After the resignation of the prime minister, the reform process was paused.'"
    //             }
    //         ]
    //     },
    //     {
    //         id: 19,
    //         type: "Part 1 - Introduction",
    //         skill: "Speaking",
    //         description: "Choose vocabulary from the reading to complete the sentence. Remember that these are words from the reading, do not create anything based on your own understanding or knowledge.",
    //         strategy: [
    //             "Read the instructions and write down the number of words to fill in.",
    //             "Read the incomplete sentence and guess the correct type and meaning of the word.",
    //             "Predict the answer.",
    //             "Search for synonyms and phrases.",
    //             "Scan to find keywords and determine the answer.",
    //             "Correct spelling errors.",
    //             "Continue with another question."
    //         ],
    //         tip: [
    //             "Search for synonyms and phrases.",
    //             "Scan to find keywords and determine the answer.",
    //             "Correct spelling errors."
    //         ],
    //         exercises: [
    //             {
    //                 question: "What type of animal was first domesticated by humans?",
    //                 answer: "dog",
    //                 explanation: "The passage notes: 'The dog is believed to be the first animal ever domesticated by humans.'"
    //             },
    //             {
    //                 question: "Which planet is known for having the largest volcano?",
    //                 answer: "Mars",
    //                 explanation: "The text states: 'Olympus Mons, the largest volcano in the solar system, is located on Mars.'"
    //             }
    //         ]
    //     },
    //     {
    //         id: 11,
    //         type: "Summary Completion",
    //         skill: "Reading",
    //         description: "Choose vocabulary from the reading to complete the sentence. Remember that these are words from the reading, do not create anything based on your own understanding or knowledge.",
    //         strategy: [
    //             "Read the instructions and write down the number of words to fill in.",
    //             "Read the incomplete sentence and guess the correct type and meaning of the word.",
    //             "Predict the answer.",
    //             "Search for synonyms and phrases.",
    //             "Scan to find keywords and determine the answer.",
    //             "Correct spelling errors.",
    //             "Continue with another question."
    //         ],
    //         tip: [],
    //         exercises: [
    //             {
    //                 question: "Complete the summary using words from the text: The study revealed that regular ____ can reduce stress and improve concentration. Participants reported feeling ____ and more focused after engaging in the activity.",
    //                 options: ["exercise", "walking", "meditation", "yoga", "calmer", "energized", "sleepy"],
    //                 answer: ["meditation", "calmer"],
    //                 explanation: "The summary is based on a section stating: 'Regular meditation was found to reduce stress, and participants said they felt calmer and more focused afterwards.'"
    //             }
    //         ]
    //     }
    // ];

    const [detail, setDetail] = useState<TipDetail | null>(null);

    const { id } = useParams<{ id: string }>();
    useEffect(() => {
        if (!id) return;

        fetch(`http://localhost:8080/api/read/${id}`)
            .then((res) => res.json())
            .then((data: TipDetail) => {
                setDetail(data);
            })
            .catch((error) => {
                console.error("Lỗi khi gọi API chi tiết tip:", error);
                setDetail(null);
            });
    }, [id]);



    // const detail = useMemo(
    //     () => sampleData.find((d) => d.id.toString() === id),
    //     [id]
    // );
    if (!detail) {
        return (
            <div className="min-h-screen flex items-center justify-center text-red-500">
                Không tìm thấy dữ liệu.
            </div>
        );
    }

    while (
        !detail ||
        !detail.tip ||
        !detail.strategy ||
        !detail.exercises
        ) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                Đang tải...
            </div>
        );
    }

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
                                    {detail.strategy.map((s, idx) => (
                                        <div key={idx} className="flex items-start gap-4">
                                            <div className="bg-emerald-100 p-2 rounded-full">
                                                <span className="font-bold text-emerald-700">{idx + 1}</span>
                                            </div>
                                            <div>
                                                <h3 className="font-semibold text-lg text-left">Skim First, Read Later</h3>
                                                {/*<span className="font-bold text-emerald-700">*/}
                                                {/*     {idx + 1}*/}
                                                {/*</span>*/}
                                                <p className="text-gray-600 text-left">{s}</p>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </CardContent>
                        </Card>
                        {/* Tips Section */}
                        {detail.tip.length > 0 && (
                            <Card className="border shadow-sm mb-10">
                                <CardHeader className="bg-gray-50 border-b">
                                    <div className="flex items-center gap-2">
                                        <Lightbulb className="h-5 w-5 text-emerald-600" />
                                        <CardTitle>Essential Tips</CardTitle>
                                    </div>
                                </CardHeader>
                                <CardContent className="p-6">
                                    <div className="grid gap-4">
                                        {detail.tip.map((t, idx) => (
                                            <div key={idx} className="flex items-start gap-3">
                                                <CheckCircle className="h-5 w-5 text-emerald-500 mt-1" />
                                                <div>
                                                    <p className="font-medium text-left">{t}</p>
                                                    {/*<p className="text-sm text-gray-600 text-left">*/}
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                </CardContent>
                            </Card>
                        )}
                        {/* Practice Exercise */}
                        <Card className="border shadow-sm mb-10">
                            <CardHeader className="bg-gray-50 border-b text-left">
                                <CardTitle>Practice Exercise</CardTitle>
                            </CardHeader>
                            <CardContent className="p-6 text-left">
                                {detail.exercises.map((ex, idx) => (
                                    <div className="prose max-w-none">
                                        <div key={idx} className="bg-gray-50 p-4 rounded-lg my-4 border">
                                            <p className="font-medium">
                                                {idx + 1}. {ex.question}
                                            </p>
                                        </div>

                                        <h4>Questions:</h4>
                                        {/* Multiple-choice nếu có options */}
                                        {ex.options && (
                                            <ul className="list-disc ml-6 space-y-1">
                                                {ex.options.map((opt) => (
                                                    <li key={opt}>{opt}</li>
                                                ))}
                                            </ul>
                                        )}
                                        {/* Ẩn/hiện đáp án */}
                                        <details className="border-l-4 border-emerald-600 pl-3 mt-2">
                                            <summary className="cursor-pointer text-emerald-700">
                                                Answer & Explanation
                                            </summary>
                                            <p className="mt-1">
                                                <strong>Answer:</strong>{" "}
                                                {Array.isArray(ex.answer)
                                                    ? ex.answer.join(", ")
                                                    : ex.answer}
                                            </p>
                                            <p className="text-sm text-gray-600">{ex.explanation}</p>
                                        </details>
                                    </div>
                                ))}
                            </CardContent>
                            {/*<div className="px-6 pb-6">*/}
                            {/*    <Button className="w-full bg-emerald-600 hover:bg-emerald-700">Check Answers</Button>*/}
                            {/*</div>*/}
                        </Card>
                        {/* Optional back button */}
                        <Button variant="outline" onClick={() => history.back()}>
                            ← Back
                        </Button>
                    </div>
                </div>
            </section>
        </div>
    )
};

export default TipDetail;