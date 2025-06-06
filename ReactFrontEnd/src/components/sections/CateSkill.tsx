import { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Search, SlidersHorizontal } from 'lucide-react';

type SkillName = 'Listening' | 'Reading' | 'Writing' | 'Speaking' | 'All Skills';

interface Skill {
    name: SkillName;
    icon: string;
}

interface CateSkillProps {
    onSkillChange: (skill: SkillName) => void;
    initialSkill?: SkillName;
    onSortChange?: (sortOption: string) => void;
}

const CateSkill: React.FC<CateSkillProps> = ({ onSkillChange, initialSkill = 'All Skills', onSortChange }) => {
    const [selectedSkill, setSelectedSkill] = useState<SkillName>(initialSkill);
    const [showSort, setShowSort] = useState<boolean>(false);
    const [sortBy, setSortBy] = useState<string>('Newest');

    useEffect(() => {
        setSelectedSkill(initialSkill);
    }, [initialSkill]);

    const skills: Skill[] = [
        { name: 'All Skills', icon: '🎯' },
        { name: 'Listening', icon: '🎧' },
        { name: 'Reading', icon: '📚' },
        { name: 'Writing', icon: '✍️' },
        { name: 'Speaking', icon: '🎤' },
    ];

    const handleSkillChange = (skillName: SkillName) => {
        setSelectedSkill(skillName);
        onSkillChange(skillName);
    };

    const handleSortChange = (option: string) => {
        setSortBy(option);
        setShowSort(false);
        if (onSortChange) onSortChange(option);
    };

    return (
        <div className="w-full max-w-7xl mx-auto p-6">
            <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                className="bg-white shadow-lg rounded-2xl p-6 space-y-6"
            >
                <div className="text-center mb-8">
                    <h2 className="text-3xl font-bold text-[#374151] mb-2">IELTS Practice Tests</h2>
                    <p className="text-gray-600">Choose your skill and start practicing</p>
                </div>

                <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-5 gap-4">
                    {skills.map((skill) => (
                        <motion.button
                            key={skill.name}
                            whileHover={{ scale: 1.05 }}
                            whileTap={{ scale: 0.95 }}
                            onClick={() => handleSkillChange(skill.name)}
                            className={`flex flex-col items-center justify-center gap-2 py-3 rounded-lg cursor-pointer select-none
                                ${
                                selectedSkill === skill.name
                                    ? 'bg-[#34D399] text-white shadow-md'
                                    : 'bg-gray-100 text-gray-600 hover:bg-gray-200 hover:text-[#34D399]'
                            } transition-all duration-300`}
                        >
                            <span className="text-2xl">{skill.icon}</span>
                            <span className="text-sm font-semibold">{skill.name}</span>
                        </motion.button>
                    ))}
                </div>

                {onSortChange && (
                    <div className="relative flex justify-end">
                        <motion.button
                            whileHover={{ scale: 1.05 }}
                            whileTap={{ scale: 0.95 }}
                            onClick={() => setShowSort(!showSort)}
                            className="flex items-center gap-2 px-4 py-2 text-gray-600 hover:text-[#34D399]"
                        >
                            <SlidersHorizontal size={20} />
                            <span>Sort by: {sortBy}</span>
                        </motion.button>

                        {showSort && (
                            <motion.div
                                initial={{ opacity: 0, y: 10 }}
                                animate={{ opacity: 1, y: 0 }}
                                className="absolute top-full right-0 mt-2 bg-white shadow-lg rounded-lg py-2 z-10"
                            >
                                {['Newest', 'Oldest', 'Most Popular'].map((option) => (
                                    <button
                                        key={option}
                                        onClick={() => handleSortChange(option)}
                                        className="w-full px-4 py-2 text-left hover:bg-gray-100"
                                    >
                                        {option}
                                    </button>
                                ))}
                            </motion.div>
                        )}
                    </div>
                )}
            </motion.div>
        </div>
    );
};

export default CateSkill;
