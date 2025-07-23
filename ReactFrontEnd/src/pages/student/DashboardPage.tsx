import axios from "axios";
import { useEffect, useState } from "react";

interface StudentResult {
    averageBand: number;
    username: string;
    skill: string;
    band: number;
    totalCorrect: number;
    submittedAt: string;
}

const DashboardPage = () => {
    const [top10, setTop10] = useState<StudentResult[]>([]);
    const [top3BySkill, setTop3BySkill] = useState<Record<string, StudentResult[]>>({});

    useEffect(() => {
        const token = localStorage.getItem("token");
        const headers = { Authorization: `Bearer ${token}` };

        axios.get("http://localhost:8080/api/dashboard/top10", { headers })
            .then((res) => setTop10(res.data));

        axios.get("http://localhost:8080/api/dashboard/top3-skills", { headers })
            .then((res) => setTop3BySkill(res.data));
    }, []);

    return (
        <div className="p-8 bg-gray-50 min-h-screen">
            {/* 🎯 Top 3 by Skill */}
            <h2 className="text-2xl font-bold mb-6 text-green-700">🎯 Top 3 Students by Skill</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-12">
                {["reading", "listening", "speaking", "writing"].map((skill) => (
                    <div key={skill} className="bg-white border rounded-xl shadow-sm p-5">
                        <h3 className="text-lg font-semibold text-gray-800 mb-3 capitalize border-b pb-2">{skill}</h3>
                        {top3BySkill[skill]?.length ? (
                            <ul className="space-y-2">
                                {top3BySkill[skill].map((student, index) => (
                                    <li key={index} className="text-gray-700">
                                        <span className="font-medium">{index + 1}. {student.username}</span> –
                                        <span className="text-blue-600 font-bold"> Band {student.band}</span>
                                    </li>
                                ))}
                            </ul>
                        ) : (
                            <p className="text-gray-400 italic">No data available</p>
                        )}
                    </div>
                ))}
            </div>

            {/* 🏆 Top 10 Students by Total Correct */}
            <h2 className="text-2xl font-bold mb-4 text-blue-700">🏆 Top 10 Students by Total Correct</h2>
            <div className="overflow-x-auto shadow-md rounded-xl bg-white border">
                <table className="min-w-full divide-y divide-gray-200">
                    <thead className="bg-blue-50">
                    <tr>
                        <th className="px-6 py-3 text-left text-sm font-semibold text-gray-700">#</th>
                        <th className="px-6 py-3 text-left text-sm font-semibold text-gray-700">Username</th>
                        <th className="px-6 py-3 text-left text-sm font-semibold text-gray-700">Total Correct</th>
                    </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-100">
                    {top10.map((student, index) => (
                        <tr key={index} className="hover:bg-gray-50">
                            <td className="px-6 py-4">{index + 1}</td>
                            <td className="px-6 py-4">{student.username}</td>
                            <td className="px-6 py-4">  {student.averageBand != null ? student.averageBand.toFixed(2) : 'N/A'}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default DashboardPage;
