import { useState, useEffect } from 'react';
import axios from 'axios';
import type {AggregatedStudent, StudentSkillResult} from '@/types/apiTypes';

export default function DashboardPage() {
    const [top20, setTop20] = useState<AggregatedStudent[]>([]);
    const [top3BySkill, setTop3BySkill] = useState<Record<string, StudentSkillResult[]>>({});

    useEffect(() => {
        const token = localStorage.getItem("token"); // Hoặc từ AuthContext nếu bạn dùng context
        const headers = { Authorization: `Bearer ${token}` };

        axios.get('/api/dashboard/top20', { headers })
            .then(res => {
                if (Array.isArray(res.data)) {
                    setTop20(res.data);
                } else {
                    console.error("Unexpected response for top20:", res.data);
                    setTop20([]);
                }
            })
            .catch(err => console.error("Top20 Error", err.response?.data || err.message));

        axios.get('/api/dashboard/top3-skills', { headers })
            .then(res => setTop3BySkill(res.data))
            .catch(err => console.error("Top3Skills Error", err.response?.data || err.message));
    }, []);


    return (
        <div className="p-6 space-y-8">
            <h2 className="text-2xl font-bold">🎯 Top 20 Students (by avg totalCorrect)</h2>
            <table className="min-w-full table-auto border">
                <thead>
                <tr className="bg-gray-100">
                    <th className="p-2">#</th>
                    <th>Username</th>
                    <th>Total Correct (avg)</th>
                    <th>Band Writing</th>
                    <th>Band Reading</th>
                    <th>Band Listening</th>
                    <th>Band Speaking</th>
                </tr>
                </thead>
                <tbody>
                {top20.map((s, idx) => (
                    <tr key={idx} className="border-t">
                        <td className="p-2 text-center">{idx + 1}</td>
                        <td className="text-center">{s._id}</td>
                        <td className="text-center">{s.avgTotalCorrect}</td>
                        <td className="text-center">{s.bandWriting || '-'}</td>
                        <td className="text-center">{s.bandReading || '-'}</td>
                        <td className="text-center">{s.bandListening || '-'}</td>
                        <td className="text-center">{s.bandSpeaking || '-'}</td>
                    </tr>
                ))}
                </tbody>
            </table>

            <h2 className="text-2xl font-bold mt-10">🏆 Top 3 Students by Skill</h2>
            {["writing", "reading", "listening", "speaking"].map(skill => (
                <div key={skill} className="mb-6">
                    <h3 className="text-xl font-semibold capitalize">{skill}</h3>
                    <ul className="list-disc ml-6">
                        {top3BySkill[skill]?.map((s, i) => (
                            <li key={i}>
                                {s.username} - Band: {s.band}, Correct: {s.totalCorrect}
                            </li>
                        ))}
                    </ul>
                </div>
            ))}
        </div>

    );
}
