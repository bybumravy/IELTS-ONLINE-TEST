import { useState } from "react";

const questionsAndAudios = [
    {
        question: "Where are you from?",
        audioUrl:
            "https://swpieltsbucket.s3.ap-southeast-1.amazonaws.com/audio/user/.../part1-1.mp3",
    },
    {
        question: "Do you like your hometown?",
        audioUrl:
            "https://swpieltsbucket.s3.ap-southeast-1.amazonaws.com/audio/user/.../part1-2.mp3",
    },
    {
        question: "What’s your favorite part of your hometown?",
        audioUrl:
            "https://swpieltsbucket.s3.ap-southeast-1.amazonaws.com/audio/user/.../part1-3.mp3",
    },
];
const API_URL = import.meta.env.VITE_API_URL || 'VITE_API_URL=http://api.languages.io.vn:8080';

export default function SpeakingEvaluation() {
    const [results, setResults] = useState<any[]>([]);
    const [loading, setLoading] = useState(false);

    const handleEvaluateAll = async () => {
        setLoading(true);
        try {
            const responses = await Promise.all(
                questionsAndAudios.map((item) =>
                    fetch(`${API_URL}/api/speaking/evaluate`, {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({
                            question: item.question,
                            audioUrl: item.audioUrl,
                        }),
                    }).then((res) => res.json())
                )
            );
            setResults(responses);
        } catch (error) {
            console.error("Lỗi:", error);
            alert("Có lỗi xảy ra khi chấm điểm!");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="p-4 max-w-2xl mx-auto">
            <h2 className="text-xl font-bold mb-4">Chấm điểm Speaking Part 1</h2>

            <button
                onClick={handleEvaluateAll}
                disabled={loading}
                className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:opacity-50"
            >
                {loading ? "Đang chấm..." : "Chấm toàn bộ câu"}
            </button>

            {results.map((result, idx) => (
                <div
                    key={idx}
                    className="mt-6 bg-gray-50 p-4 rounded shadow space-y-2"
                >
                    <p>
                        <strong>Câu hỏi:</strong> {questionsAndAudios[idx].question}
                    </p>
                    <audio
                        controls
                        src={questionsAndAudios[idx].audioUrl}
                        className="w-full"
                    />
                    <p><strong>Transcript:</strong> {result.transcript}</p>
                    <p><strong>Band tổng:</strong> {result.overallBand}</p>
                    <ul className="list-disc ml-5">
                        <li><strong>Fluency:</strong> {result.score.fluency}</li>
                        <li><strong>Grammar:</strong> {result.score.grammar}</li>
                        <li><strong>Vocabulary:</strong> {result.score.vocabulary}</li>
                        <li><strong>Pronunciation:</strong> {result.score.pronunciation}</li>
                    </ul>
                    <p><strong>Nhận xét:</strong> {result.feedback}</p>
                </div>
            ))}
        </div>
    );
}
