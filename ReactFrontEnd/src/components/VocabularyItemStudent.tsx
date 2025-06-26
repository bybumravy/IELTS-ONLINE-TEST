import React, { useState } from 'react';
import type { Vocabulary as VocabularyType } from '@/lib/type';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';

export interface VocabularyItemStudentProps {
    vocabulary: VocabularyType;
    onDetailClick?: (vocab: VocabularyType) => void;
}

export const VocabularyItemStudent: React.FC<VocabularyItemStudentProps> = ({
                                                                                vocabulary,
                                                                                onDetailClick,
                                                                            }) => {
    const [flipped, setFlipped] = useState(false);

    return (
        <div className="relative w-full flex justify-center">
            <div
                className="flashcard-container cursor-pointer w-full"
                onClick={() => setFlipped(f => !f)}
            >
                <div className={`flashcard ${flipped ? 'flipped' : ''}`}>
                    {/* Front: English word, topic, band */}
                    <Card className="flashcard-face flashcard-front p-8 flex flex-col items-center justify-center min-h-[140px] bg-white text-black rounded-2xl">
                        <span className="text-2xl font-semibold">{vocabulary.word}</span>
                        <div className="flex gap-2 mt-4">
                            <span className="px-3 py-1 bg-[#059669] text-white rounded text-xs font-semibold">{vocabulary.topic}</span>
                            <span className="px-3 py-1 bg-[#059669] text-white rounded text-xs font-semibold">Band {vocabulary.band}</span>
                        </div>
                    </Card>
                    {/* Back: Vietnamese translate, topic, band */}
                    <Card className="flashcard-face flashcard-back p-8 flex flex-col items-center justify-center min-h-[140px] bg-white text-black rounded-2xl">
                        <span className="text-2xl font-semibold">{vocabulary.translate}</span>
                        <div className="flex gap-2 mt-4">
                            <span className="px-3 py-1 bg-[#059669] text-white rounded text-xs font-semibold">{vocabulary.topic}</span>
                            <span className="px-3 py-1 bg-[#059669] text-white rounded text-xs font-semibold">Band {vocabulary.band}</span>
                        </div>
                    </Card>
                </div>
            </div>
            <div className="absolute top-4 right-4 z-10">
                <Button
                    size="sm"
                    variant="outline"
                    className="detail-btn"
                    onClick={e => {
                        e.stopPropagation();
                        onDetailClick?.(vocabulary);
                    }}
                >
                    Detail
                </Button>
            </div>
        </div>
    );
};