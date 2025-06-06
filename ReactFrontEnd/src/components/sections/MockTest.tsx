import { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { ArrowRight } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

interface ListTest {
    id: string;
    title: string;
    year: number;
}

interface TestsByYear {
    [year: string]: ListTest[];
}

interface MockTestProps {
    selectedSkill: 'Listening' | 'Reading' | 'Writing' | 'Speaking' | 'All Skills';
}

const MockTest: React.FC<MockTestProps> = ({ selectedSkill = 'All Skills' }) => {
    const [testsByYear, setTestsByYear] = useState<TestsByYear>({});
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchTests = async () => {
            try {
                setLoading(true);
                setError(null);
                const endpoint =
                    selectedSkill === 'All Skills'
                        ? 'http://localhost:8080/api/test/all-skill'
                        : `http://localhost:8080/api/test/${selectedSkill.toLowerCase()}`;

                const response = await fetch(endpoint);
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                const data = await response.json();

                // Validate and transform data if needed
                const transformedData: TestsByYear = {};
                if (Array.isArray(data)) {
                    // If data is an array of tests, group by year
                    data.forEach((test: ListTest) => {
                        const year = test.year.toString();
                        if (!transformedData[year]) {
                            transformedData[year] = [];
                        }
                        transformedData[year].push(test);
                    });
                } else if (typeof data === 'object') {
                    // If data is already grouped by year
                    Object.entries(data).forEach(([year, tests]) => {
                        if (Array.isArray(tests)) {
                            transformedData[year] = tests;
                        }
                    });
                }

                setTestsByYear(transformedData);
            } catch (err) {
                console.error('Failed to fetch tests:', err);
                setError(err instanceof Error ? err.message : 'Failed to fetch tests');
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
        const skill = selectedSkill === 'All Skills' ? 'full' : selectedSkill.toLowerCase();
        navigate(`/test/${testId}/${skill}`);
    };

    if (loading) {
        return (
            <div className="w-full max-w-7xl mx-auto p-6">
                <div className="flex items-center justify-center h-64">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-emerald-500"></div>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="w-full max-w-7xl mx-auto p-6">
                <div className="flex items-center justify-center h-64">
                    <div className="text-red-500">Error: {error}</div>
                </div>
            </div>
        );
    }

    return (
        <div className="w-full max-w-7xl mx-auto p-6">
            <div className="space-y-8">
                {Object.entries(testsByYear)
                    .sort(([yearA], [yearB]) => Number(yearB) - Number(yearA))
                    .map(([year, tests]) => (
                        <motion.div
                            key={year}
                            initial={{ opacity: 0, y: 20 }}
                            animate={{ opacity: 1, y: 0 }}
                            className="bg-white rounded-xl shadow-lg p-6"
                        >
                            <h2 className="text-2xl font-bold text-[#374151] mb-6">{getTestTitle(year)}</h2>
                            <div className="grid gap-6">
                                {tests.map((test) => (
                                    <div key={test.id} className="border-b border-gray-200 pb-4 last:border-0 last:pb-0">
                                        <div className="flex flex-col gap-4">
                                            <div className="flex items-center justify-between">
                                                <div className="flex items-center gap-2">
                                                    <h3 className="text-lg font-semibold text-[#374151]">{test.title}</h3>
                                                </div>
                                                <span className="text-sm text-gray-500">{year}</span>
                                            </div>

                                            <div className="flex items-center justify-between">
                                                <div className="flex items-center gap-4 text-sm text-gray-500">
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
                                    </div>
                                ))}
                            </div>
                        </motion.div>
                    ))}
            </div>
        </div>
    );
};

export default MockTest;