import { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import type { Vocabulary as VocabularyType } from '@/types/apiTypes';
import { Card } from '@/components/ui/card';
import { cn } from '@/lib/utils';
import { ArrowLeft } from 'lucide-react';

type CardType = {
    id: string;
    text: string;
    type: 'word' | 'translate';
    matched: boolean;
};

export default function MatchingGamePage() {
    const location = useLocation();
    const navigate = useNavigate();
    const vocabList: VocabularyType[] = location.state?.vocabList || [];

    const batchSize = 6;
    const totalBatches = Math.ceil(vocabList.length / batchSize);

    const [batchIndex, setBatchIndex] = useState(0);
    const [cards, setCards] = useState<CardType[]>([]);
    const [selected, setSelected] = useState<number[]>([]);
    const [shakeIndexes, setShakeIndexes] = useState<number[]>([]);
    const [gameCompleted, setGameCompleted] = useState(false);

    const currentBatch = vocabList.slice(batchIndex * batchSize, (batchIndex + 1) * batchSize);

    useEffect(() => {
        const wordCards: CardType[] = currentBatch.map(v => ({
            id: v.id,
            text: v.word,
            type: 'word',
            matched: false
        }));

        const translateCards: CardType[] = currentBatch.map(v => ({
            id: v.id,
            text: v.translate,
            type: 'translate',
            matched: false
        }));

        const shuffledCards = shuffleArray([...wordCards, ...translateCards]);
        setCards(shuffledCards);
        setSelected([]);
        setShakeIndexes([]);
    }, [batchIndex, vocabList]);

    const handleCardClick = (index: number) => {
        if (cards[index].matched || selected.includes(index)) return;

        const newSelected = [...selected, index];
        setSelected(newSelected);

        if (newSelected.length === 2) {
            const [first, second] = newSelected;
            const firstCard = cards[first];
            const secondCard = cards[second];

            if (firstCard.id === secondCard.id && firstCard.type !== secondCard.type) {
                const updated = [...cards];
                updated[first].matched = true;
                updated[second].matched = true;
                setCards(updated);
            } else {
                setShakeIndexes([first, second]);
                setTimeout(() => setShakeIndexes([]), 600);
            }

            setTimeout(() => setSelected([]), 600);
        }
    };

    const handleNextBatch = () => {
        if (batchIndex < totalBatches - 1) {
            setBatchIndex(batchIndex + 1);
        } else {
            setGameCompleted(true);
        }
    };

    return (
        <div className="max-w-5xl mx-auto text-center mt-10 px-4">
            <h1 className="text-4xl font-bold mb-10">Matching words and meanings</h1>

            <div className="grid grid-cols-4 gap-6">
                {cards.map((card, index) => (
                    <Card
                        key={index}
                        className={cn(
                            'p-6 text-xl font-semibold cursor-pointer transition-all rounded-xl shadow-sm border',
                            selected.includes(index) && 'border-blue-500 ring-2 ring-blue-300',
                            card.matched && 'bg-green-500 text-white pointer-events-none',
                            shakeIndexes.includes(index) && 'shake bg-red-100 border-red-400'
                        )}
                        onClick={() => handleCardClick(index)}
                    >
                        {card.text}
                    </Card>
                ))}
            </div>

            {cards.length > 0 && cards.every(c => c.matched) && !gameCompleted && (
                <div className="mt-10">
                    {batchIndex < totalBatches - 1 ? (
                        <button
                            className="px-8 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition text-lg"
                            onClick={handleNextBatch}
                        >
                            Continue with the next words
                        </button>
                    ) : (
                        <div className="text-2xl font-bold text-green-600 mt-10">
                            Congratulations you have completed all the word pairs! <br />
                            <span className="text-xl text-green-700">Congratulations!</span>
                        </div>
                    )}
                </div>
            )}

            {gameCompleted && (
                <div className="text-2xl font-bold text-green-600 mt-10">
                    Chúc mừng bạn đã hoàn thành tất cả cặp từ! <br />
                    <span className="text-xl text-green-700">Congratulations!</span>
                </div>
            )}

            {/* Quay lại button */}
            <div className="mt-12 text-left">
                <button
                    onClick={() => navigate('/practice/vocabulary')}
                    className="inline-flex items-center gap-2 text-green-600 hover:underline text-base font-medium"
                >
                    <ArrowLeft className="w-5 h-5" />
                    Back to Vocabulary
                </button>
            </div>
        </div>
    );
}

// Shuffle helper
function shuffleArray<T>(array: T[]): T[] {
    return [...array].sort(() => Math.random() - 0.5);
}
