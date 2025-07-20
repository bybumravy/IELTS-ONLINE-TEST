import { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import type { Vocabulary as VocabularyType } from '@/types/apiTypes';
import { Card } from '@/components/ui/card';
import { cn } from '@/lib/utils';

type CardType = {
    id: string;
    text: string;
    type: 'word' | 'translate';
    matched: boolean;
};

export default function MatchingGamePage() {
    const location = useLocation();
    const vocabList: VocabularyType[] = location.state?.vocabList || [];

    const [cards, setCards] = useState<CardType[]>([]);
    const [selected, setSelected] = useState<number[]>([]);
    const [shakeIndexes, setShakeIndexes] = useState<number[]>([]);

    useEffect(() => {
        const wordCards: CardType[] = vocabList.map(v => ({
            id: v.id,
            text: v.word,
            type: 'word',
            matched: false
        }));

        const translateCards: CardType[] = vocabList.map(v => ({
            id: v.id,
            text: v.translate,
            type: 'translate',
            matched: false
        }));

        const shuffledCards = shuffleArray([...wordCards, ...translateCards]);
        setCards(shuffledCards);
    }, [vocabList]);

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

    return (
        <div className="max-w-4xl mx-auto text-center mt-10">
            <h1 className="text-3xl font-bold mb-6">🔗 Ghép từ và nghĩa</h1>
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
            {cards.length > 0 && cards.every(c => c.matched) && (
                <div className="text-2xl font-bold text-green-600 mt-10">🎉 Hoàn thành!</div>
            )}
        </div>
    );
}

// Shuffle helper
function shuffleArray<T>(array: T[]): T[] {
    return [...array].sort(() => Math.random() - 0.5);
}
