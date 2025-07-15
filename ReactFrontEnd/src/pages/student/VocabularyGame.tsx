import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import type { Vocabulary as VocabularyType } from '@/lib/type';

const VocabularyGame: React.FC = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const initialWords: VocabularyType[] = location.state?.vocabList || [];

    const [remainingWords, setRemainingWords] = useState<VocabularyType[]>([]);
    const [currentWord, setCurrentWord] = useState<VocabularyType | null>(null);
    const [options, setOptions] = useState<string[]>([]);
    const [message, setMessage] = useState('');

    useEffect(() => {
        if (!initialWords.length) {
            navigate('/student/vocabulary');
            return;
        }
        setRemainingWords([...initialWords]);
    }, []);

    useEffect(() => {
        if (remainingWords.length === 0) {
            setMessage("🎉 Congratulations! You’ve finished the game.");
            setCurrentWord(null);
            return;
        }

        const word = remainingWords[0];
        const answers = [
            word.translate,
            ...generateRandomTranslations(word.translate, 3)
        ];
        const shuffled = shuffleArray(answers);
        setCurrentWord(word);
        setOptions(shuffled);
    }, [remainingWords]);

    const handleAnswer = (answer: string) => {
        if (!currentWord) return;
        if (answer === currentWord.translate) {
            setMessage('✅ Correct!');
            setTimeout(() => {
                setMessage('');
                setRemainingWords(prev => prev.slice(1));
            }, 800);
        } else {
            setMessage('❌ Try again');
        }
    };

    return (
        <div className="max-w-xl mx-auto mt-12 text-center">
            <h1 className="text-2xl font-bold mb-6">🎮 Vocabulary Game</h1>
            {currentWord ? (
                <>
                    <Card className="p-6 mb-6">
                        <div className="text-xl font-semibold mb-2">{currentWord.word}</div>
                        {currentWord.partOfSpeech && (
                            <div className="text-sm text-gray-600 mb-2">({currentWord.partOfSpeech})</div>
                        )}
                        {currentWord.pronunciation && (
                            <div className="text-sm italic text-gray-500">/{currentWord.pronunciation}/</div>
                        )}
                    </Card>
                    <div className="grid grid-cols-2 gap-4 mb-4">
                        {options.map((opt, idx) => (
                            <Button
                                key={idx}
                                variant="outline"
                                className="py-2"
                                onClick={() => handleAnswer(opt)}
                            >
                                {opt}
                            </Button>
                        ))}
                    </div>
                    <div className="text-lg font-medium mt-4">{message}</div>
                </>
            ) : (
                <div className="text-xl text-green-600 font-bold">{message}</div>
            )}
            <Button className="mt-6" onClick={() => navigate('/student/vocabulary')}>← Back to Vocabulary</Button>
        </div>
    );
};

export default VocabularyGame;

// Helper
const generateRandomTranslations = (correct: string, count: number): string[] => {
    const dummy = ['apple', 'book', 'love', 'school', 'run', 'idea', 'strong', 'happy', 'money', 'sleep'];
    return shuffleArray(dummy.filter(d => d !== correct)).slice(0, count);
};

const shuffleArray = <T,>(array: T[]): T[] => {
    return [...array].sort(() => Math.random() - 0.5);
};
