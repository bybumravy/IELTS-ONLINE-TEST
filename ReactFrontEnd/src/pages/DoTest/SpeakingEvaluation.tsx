import { useState } from "react";

const audioUrl =
    "https://swpieltsbucket.s3.ap-southeast-1.amazonaws.com/audio/user/phamhoangviet05052005@gmail.com/T002_685fe2cd72a6917d6ced8905/part2.mp3";

export default function SpeakingEvaluation() {
    const [result, setResult] = useState<any>(null);
    const [loading, setLoading] = useState(false);

    const handleEvaluate = async () => {
        setLoading(true);
        try {
            const res = await fetch("http://localhost:8080/api/speaking/evaluate", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ audioUrl }),
            });

            if (!res.ok) throw new Error("Chấm điểm thất bại");

            const data = await res.json();
            setResult(data);
        } catch (error) {
            console.error("Lỗi:", error);
            alert("Có lỗi xảy ra khi chấm điểm!");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="p-4 max-w-xl mx-auto">
            <h2 className="text-xl font-bold mb-4">Chấm điểm Speaking</h2>

            <audio controls src={audioUrl} className="mb-4 w-full" />

            <button
                onClick={handleEvaluate}
                disabled={loading}
                className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:opacity-50"
            >
                {loading ? "Đang chấm..." : "Chấm điểm"}
            </button>

            {result && (
                <div className="mt-6 bg-gray-50 p-4 rounded shadow space-y-2">
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
            )}
        </div>
    );
}
