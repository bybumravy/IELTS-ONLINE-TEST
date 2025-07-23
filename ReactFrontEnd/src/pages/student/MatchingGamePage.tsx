import { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import type { Vocabulary as VocabularyType } from '@/types/apiTypes';
import { Card } from '@/components/ui/card';
import { cn } from '@/lib/utils';
import { ArrowLeft } from 'lucide-react'; // dùng icon mũi tên

type CardType = {
    id: string;
    text: string;
    type: 'word' | 'translate';
    matched: boolean;
};

export default function MatchingGamePage() {
    const location = useLocation();
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
        <div className="max-w-4xl mx-auto text-center mt-10 px-4">
            <div className="mb-4 text-left">
                <a
                    href="http://localhost:5173/practice/vocabulary"
                    className="inline-flex items-center gap-2 text-blue-600 hover:underline text-base font-medium"
                >
                    <ArrowLeft className="w-5 h-5" />
                    {gameCompleted && <span>Quay lại luyện tập từ vựng</span>}
                </a>
            </div>

            <h1 className="text-3xl font-bold mb-6">Ghép từ và nghĩa</h1>

            <div className="grid grid-cols-4 gap-4">
                {cards.map((card, index) => (
                    <Card
                        key={index}
                        className={cn(
                            'p-4 text-lg font-semibold cursor-pointer transition-all',
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
                <div className="mt-8">
                    {batchIndex < totalBatches - 1 ? (
                        <button
                            className="px-6 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition"
                            onClick={handleNextBatch}
                        >
                            Tiếp tục các từ tiếp theo
                        </button>
                    ) : (
                        <div className="text-2xl font-bold text-green-600 mt-10">
                            Chúc mừng bạn đã hoàn thành tất cả cặp từ! <br />
                            <span className="text-xl text-green-700">Chúc mừng bạn nhé!</span>
                        </div>
                    )}
                </div>
            )}

            {gameCompleted && (
                <div className="text-2xl font-bold text-green-600 mt-10">
                    Chúc mừng bạn đã hoàn thành tất cả cặp từ! <br />
                    <span className="text-xl text-green-700">Chúc mừng bạn nhé!</span>
                </div>
            )}
        </div>
    );
}

// Shuffle helper
function shuffleArray<T>(array: T[]): T[] {
    return [...array].sort(() => Math.random() - 0.5);
}
