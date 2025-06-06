import { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { ArrowRight, Star } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { Badge } from '@/components/ui/badge';

interface ListTest {
    id: string;
    title: string;
    year: number;
}

interface TestsByYear {
    [year: string]: ListTest[];
}

interface MockTestProps {
    selectedSkill?: 'Listening' | 'Reading' | 'Writing' | 'Speaking' | 'All Skills';
}

const MockTest: React.FC<MockTestProps> = ({ selectedSkill = 'All Skills' }) => {
    const [testsByYear, setTestsByYear] = useState<TestsByYear>({});
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchTests = async () => {
            try {
                setLoading(true);
                const endpoint =
                    selectedSkill === 'All Skills'
                        ? 'http://localhost:8080/api/test/all-skill'
                        : `http://localhost:8080/api/test/${selectedSkill?.toLowerCase}`;

                const response = await fetch(endpoint);
                const data: TestsByYear = await response.json();

                // Backend trả về dữ liệu dạng { "2024": [{id,title,year}, ...], ... }
                // Chuyển key sang đúng format (nếu cần)
                setTestsByYear(data);
            } catch (err) {
                console.error('Failed to fetch tests:', err);
                setTestsByYear({});
            } finally {
                setLoading(false);
            }
        };

        fetchTests();
    }, [selectedSkill]);

    const getTestTitle = (year: string): string => {
        if (selectedSkill === 'All Skills') {
            return `IELTS Mock Tests ${year}`;
        }
        return `IELTS ${selectedSkill} Practice Tests ${year}`;
    };

    const handleStartTest = (testId: string): void => {
        const skill = selectedSkill === 'All Skills' ? 'full' : selectedSkill?.toLowerCase;
        navigate(`/test/${testId}/${skill}`);
    };

    // Format ngày không có vì backend chỉ trả year, title, id

    if (loading) {
        return (
            <div className="w-full max-w-7xl mx-auto p-6">
                <div className="flex items-center justify-center h-64">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-emerald-500"></div>
                </div>
            </div>
        );
    }

    return (
        <div className="w-full max-w-7xl mx-auto p-6">
            <div className="space-y-8">
                {Object.entries(testsByYear)
                    .sort((a, b) => Number(b[0]) - Number(a[0]))
                    .map(([year, tests]) => (
                        <section key={year} className="bg-white rounded-xl p-6 shadow-lg">
                            <h2 className="text-2xl font-bold text-[#374151] border-b-2 border-[#34D399] pb-2 mb-6">
                                {getTestTitle(year)}
                            </h2>
                            <div className="grid gap-4">
                                {tests.map((test, index) => (
                                    <motion.div
                                        key={test.id}
                                        initial={{ opacity: 0, y: 20 }}
                                        animate={{ opacity: 1, y: 0 }}
                                        transition={{ delay: index * 0.1 }}
                                        className="bg-white p-4 rounded-lg shadow-md hover:shadow-lg transition-all duration-300"
                                    >
                                        <div className="flex flex-col gap-4">
                                            <div className="flex items-center justify-between">
                                                <div className="flex items-center gap-2">
                                                    {/* Vì backend không trả isNew, tags, difficulty nên tạm ẩn Badge */}
                                                    <h3 className="text-lg font-semibold text-[#374151]">{test.title}</h3>
                                                </div>
                                                <span className="text-sm text-gray-500">{year}</span>
                                            </div>

                                            <div className="flex items-center justify-between">
                                                <div className="flex items-center gap-4 text-sm text-gray-500">
                                                    {/* Có thể bổ sung nếu có dữ liệu thêm */}
                                                </div>

                                                <motion.button
                                                    whileHover={{ scale: 1.05 }}
                                                    whileTap={{ scale: 0.95 }}
                                                    onClick={() => handleStartTest(test.id)}
                                                    className="flex items-center gap-2 bg-[#34D399] text-white px-4 py-2 rounded-lg shadow-sm hover:shadow-md hover:bg-[#FAFAF9] hover:text-[#374151] transition-all duration-300"
                                                >
                                                    <span>Start</span>
                                                    <ArrowRight size={16} />
                                                </motion.button>
                                            </div>
                                        </div>
                                    </motion.div>
                                ))}
                            </div>
                        </section>
                    ))}
            </div>
        </div>
    );
};

export default MockTest;
