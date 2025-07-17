import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import type { Vocabulary as VocabularyType } from '@/types/apiTypes';

const VocabularyGame: React.FC = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const initialWords: VocabularyType[] = location.state?.vocabList || [];

    const [remainingWords, setRemainingWords] = useState<VocabularyType[]>([]);
    const [currentWord, setCurrentWord] = useState<VocabularyType | null>(null);
    const [options, setOptions] = useState<string[]>([]);
    const [message, setMessage] = useState('');
    const [shakeIndex, setShakeIndex] = useState<number | null>(null);
    const [shakeMessage, setShakeMessage] = useState(false);
    const [correctIndex, setCorrectIndex] = useState<number | null>(null);

    // Khi bắt đầu game
    useEffect(() => {
        if (!initialWords || initialWords.length === 0) {
            navigate('/student/vocabulary');
            return;
        }
        setRemainingWords([...initialWords]);
    }, []);

    // Mỗi khi danh sách từ thay đổi → chọn từ tiếp theo
    useEffect(() => {
        if (remainingWords.length > 0) {
            loadNextWord(remainingWords[0]);
        } else if (remainingWords.length === 0 && currentWord !== null) {
            setCurrentWord(null);
            setOptions([]);
            setMessage('🎉 Congratulations! You’ve finished the game.');
        }
    }, [remainingWords]);

    const loadNextWord = (word: VocabularyType) => {
        const incorrectOptions = initialWords
            .filter((v) => v.id !== word.id && !!v.translate)
            .map((v) => v.translate)
            .sort(() => 0.5 - Math.random())
            .slice(0, 3);

        const allOptions = [...incorrectOptions, word.translate].sort(() => 0.5 - Math.random());

        setCurrentWord(word);
        setOptions(allOptions);
        setMessage('');
    };

    const handleAnswer = (answer: string, idx: number) => {
        if (!currentWord) return;

        if (answer === currentWord.translate) {
            setCorrectIndex(idx); // ✅ Đánh dấu nút được chọn là đúng
            setMessage('✅ Correct!');
            setTimeout(() => {
                setCorrectIndex(null); // 🔄 Reset lại sau 800ms
                setShakeIndex(null);
                setShakeMessage(false);
                setRemainingWords(prev => prev.slice(1));
            }, 800);
        } else {
            setMessage('❌ Sai rồi, thử lại!');
            setShakeIndex(idx);
            setShakeMessage(true);
            setTimeout(() => {
                setShakeIndex(null);
                setShakeMessage(false);
            }, 600);
        }
    };

    return (
        <div className="max-w-3xl mx-auto mt-12 text-center">
            <h1 className="text-4xl font-bold mb-8">🧠 Choose the correct meaning</h1>

            {currentWord ? (
                <>
                    <Card className="p-10 mb-10 text-3xl shadow-xl rounded-2xl border-2 border-gray-300">
                        <div className="font-bold mb-2">{currentWord.word}</div>
                        {currentWord.partOfSpeech && (
                            <div className="text-xl text-gray-600 mb-1">({currentWord.partOfSpeech})</div>
                        )}
                        {currentWord.pronunciation && (
                            <div className="text-xl italic text-gray-500">/{currentWord.pronunciation}/</div>
                        )}
                    </Card>

                    <div className="grid grid-cols-2 gap-6 mb-6">
                        {options.map((opt, idx) => (
                            <Button
                                key={idx}
                                variant="outline"
                                className={`py-6 text-2xl font-bold rounded-xl shadow-md transition-transform duration-300 
      ${shakeIndex === idx ? 'shake bg-red-100 border-red-500' : ''}
      ${correctIndex === idx ? 'bg-green-500 text-white border-green-500' : ''}
    `}
                                onClick={() => handleAnswer(opt, idx)}
                            >
                                {opt}
                            </Button>
                        ))}

                    </div>

                    {shakeMessage && message && (
                        <span className="mt-4 inline-block text-2xl font-bold text-red-600 animate-shake">
    {message}
  </span>
                    )}

                </>
            ) : (
                <div className="text-2xl font-bold text-green-600 mt-10">{message}</div>
            )}

            <br /><Button className="mt-10 text-lg px-6 py-3" onClick={() => navigate('/practice/vocabulary')}>
                ← Back to Vocabulary
            </Button>
        </div>
    );
};

export default VocabularyGame;
