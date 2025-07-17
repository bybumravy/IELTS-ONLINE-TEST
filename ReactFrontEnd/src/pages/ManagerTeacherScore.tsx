import { useEffect, useState } from "react"
import { Link } from "react-router-dom";
interface WritingAnswer {
    id: string;
    username: string;
    testId: string;
    band: number;
    gradingMethod: string;
}

function ManagerTeacherScoreList() {
    const [writingAnswers, setWritingAnswers] = useState<WritingAnswer[]>([]);

    useEffect(() => {
        fetch("http://localhost:8080/verify/listwriting", {
            credentials: "include",
        })
            .then((res) => res.json())
            .then((data) => setWritingAnswers(data))
            .catch((err) => console.error("Lỗi khi lấy dữ liệu:", err));
    }, []);

    return (
        <div>
            <h2>Danh sách bài viết đã được giáo viên chấm</h2>
            <table className="table-auto border-collapse border border-gray-300 w-full">
                <thead>
                <tr className="bg-gray-100">
                    <th className="border px-4 py-2">ID</th>
                    <th className="border px-4 py-2">Username</th>
                    <th className="border px-4 py-2">Test ID</th>
                    <th className="border px-4 py-2">Band</th>
                    <th className="border px-4 py-2">Method</th>
                </tr>
                </thead>
                <tbody>
                {writingAnswers.map((answer) => (
                    <tr key={answer.id}>
                        <td className="border px-4 py-2">
                            <Link to={`/teacher-scoring/${answer.id}`} className="text-blue-600 hover:underline">
                                {answer.id}
                            </Link>
                        </td>
                        <td className="border px-4 py-2">{answer.username}</td>
                        <td className="border px-4 py-2">{answer.testId}</td>
                        <td className="border px-4 py-2">{answer.band}</td>
                        <td className="border px-4 py-2">{answer.gradingMethod}</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}

export default ManagerTeacherScoreList;
