import React, { useState, useEffect } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import type { Vocabulary as VocabularyType } from '@/lib/type';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { VocabularyItemStudent } from '@/components/ui/vocabulary/VocabularyItemStudent.tsx';
import { useNavigate } from 'react-router-dom';


const VocabularyList: React.FC = () => {
    const { user } = useAuth();
    const [vocabularies, setVocabularies] = useState<VocabularyType[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [filters, setFilters] = useState({ topic: '', band: '' });
    const [appliedFilters, setAppliedFilters] = useState({ topic: '', band: '' });

    // Search & Pagination states
    const [search, setSearch] = useState('');
    const [searchInput, setSearchInput] = useState('');
    const [page, setPage] = useState(0);
    const pageSize = 10;
    const [totalPages, setTotalPages] = useState(1);
    const [totalElements, setTotalElements] = useState(0);

    // Detail modal state
    const [selectedVocab, setSelectedVocab] = useState<VocabularyType | null>(null);

    const API_BASE = "http://localhost:8080/api/practice";
    const navigate = useNavigate();

    const goToGame = () => {
        navigate('/student/vocabulary-game', {
            state: {
                vocabList: vocabularies
            }
        });
    };


    // --- NEW: State for topics/bands fetched from backend ---
    const [topics, setTopics] = useState<{ value: string, label: string }[]>([{ value: '', label: 'All Topics' }]);
    const [bands, setBands] = useState<{ value: string, label: string }[]>([{ value: '', label: 'All Bands' }]);

    // --- Fetch topics/bands from backend ---
    useEffect(() => {
        fetch(`${API_BASE}/vocabulary/topics`, { credentials: "include" })
            .then(res => res.json())
            .then(data => setTopics([{ value: '', label: 'All Topics' }, ...data.map((t: string) => ({ value: t, label: t }))]))
            .catch(() => setTopics([{ value: '', label: 'All Topics' }]));

        fetch(`${API_BASE}/vocabulary/bands`, { credentials: "include" })
            .then(res => res.json())
            .then(data => setBands([{ value: '', label: 'All Bands' }, ...data.map((b: string) => ({ value: b, label: b }))]))
            .catch(() => setBands([{ value: '', label: 'All Bands' }]));
    }, []);

    const fetchVocabularies = async () => {
        try {
            setLoading(true);
            const { topic, band } = appliedFilters;
            const params = new URLSearchParams();
            if (search) params.append('keyword', search);
            if (topic) params.append('topic', topic);
            if (band) params.append('band', band);
            params.append('page', page.toString());
            params.append('size', pageSize.toString());

            let url = `${API_BASE}/vocabulary/filter?${params.toString()}`;
            const response = await fetch(url, {
                headers: { 'Content-Type': 'application/json' },
                credentials: "include"
            });
            if (!response.ok) throw new Error('Failed to fetch vocabularies');
            const data = await response.json();
            setVocabularies(data.content || []);
            setTotalPages(data.totalPages || 1);
            setTotalElements(data.totalElements || 0);
            setError('');
        } catch (err) {
            setError(err instanceof Error ? err.message : 'An error occurred');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (user) fetchVocabularies();
        // eslint-disable-next-line
    }, [user, page, pageSize, appliedFilters.topic, appliedFilters.band, search]);

    const handleFilterChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const { name, value } = e.target;
        setFilters(prev => ({ ...prev, [name]: value }));
        setPage(0);
    };

    const applyFilters = () => {
        setAppliedFilters({ ...filters });
        setPage(0);
    };

    const handleSearchInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setSearchInput(e.target.value);
    };

    const handleSearchKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key === 'Enter') {
            setSearch(searchInput);
            setPage(0);
        }
    };

    const resetFilters = () => {
        setFilters({ topic: '', band: '' });
        setAppliedFilters({ topic: '', band: '' });
        setSearch('');
        setSearchInput('');
        setPage(0);
        setTimeout(fetchVocabularies, 0);
    };

    if (!user) {
        return <div className="text-center mt-12 text-lg text-gray-500">Please login to access vocabulary</div>;
    }
    if (loading) {
        return <div className="text-center mt-12 text-lg text-gray-500">Loading...</div>;
    }
    if (error) {
        return <div className="text-center mt-12 text-lg text-red-500">Error: {error}</div>;
    }

    return (
        <div className="max-w-4xl mx-auto py-8 px-2 min-h-[80vh]">
            <h1 className="text-2xl font-bold mb-6 text-center">Vocabulary</h1>
            <Card className="mb-6 p-6">
                <div className="flex flex-col md:flex-row gap-4 items-center">
                    <div className="flex-1 flex gap-2">
                        <Input
                            type="text"
                            placeholder="Search vocabulary..."
                            value={searchInput}
                            onChange={handleSearchInputChange}
                            onKeyDown={handleSearchKeyDown}
                            className="w-full px-2 py-2 rounded border border-gray-300"
                        />
                        <select
                            name="topic"
                            value={filters.topic}
                            onChange={handleFilterChange}
                            className="w-full px-2 py-2 rounded border border-gray-300"
                        >
                            {topics.map(t => (
                                <option key={t.value} value={t.value}>{t.label}</option>
                            ))}
                        </select>
                        <select
                            name="band"
                            value={filters.band}
                            onChange={handleFilterChange}
                            className="w-full px-2 py-2 rounded border border-gray-300"
                        >
                            {bands.map(b => (
                                <option key={b.value} value={b.value}>{b.label}</option>
                            ))}
                        </select>
                    </div>
                    <div className="flex gap-2">
                        <Button onClick={applyFilters} variant="outline"
                                className="hover:bg-emerald-600 hover:text-white">Apply Filters</Button>
                        <Button onClick={resetFilters} variant="outline"
                                className="hover:bg-emerald-600 hover:text-white">Reset</Button>
                        <Button onClick={goToGame} variant="outline" className="hover:bg-blue-600 hover:text-white">🎮 Play Game</Button>
                    </div>
                </div>
            </Card>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6 justify-center">
                {vocabularies.length === 0 ? (
                    <Card className="p-4 text-center text-gray-500 col-span-2">No vocabulary found.</Card>
                ) : (
                    vocabularies.map((vocab) => (
                        <VocabularyItemStudent
                            key={vocab.id}
                            vocabulary={vocab}
                            onDetailClick={(v) => setSelectedVocab(v)}
                        />
                    ))
                )}
            </div>
            {/* Modal detail: bổ sung phiên âm và loại từ */}
            {selectedVocab && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/30">
                    <Card className="relative w-full max-w-xl mx-2 p-8">
                        <button
                            className="absolute top-3 right-3 text-gray-400 hover:text-red-500 text-xl"
                            onClick={() => setSelectedVocab(null)}
                            aria-label="Close"
                        >×
                        </button>
                        <h2 className="font-bold text-xl mb-3 flex items-center gap-2">
                            {selectedVocab.word}
                            {selectedVocab.partOfSpeech && (
                                <span className="bg-emerald-600 text-white px-2 py-0.5 rounded text-xs ml-2">
                                    {selectedVocab.partOfSpeech}
                                </span>
                            )}
                        </h2>
                        {selectedVocab.pronunciation && (
                            <div className="mb-2 text-gray-700">
                                <b>Transcription:</b> <span className="italic text-gray-500 text-base">{selectedVocab.pronunciation}</span>
                            </div>
                        )}
                        <div className="mb-2 text-gray-700">
                            <b>Translate:</b> {selectedVocab.translate}
                        </div>
                        <div className="mb-2 text-gray-700">
                            <b>Explanation:</b> {selectedVocab.explanation}
                        </div>
                        {selectedVocab.exp?.length > 0 && (
                            <div className="mt-2">
                                <div className="font-semibold text-gray-700 mb-1">Examples:</div>
                                <ul className="list-disc list-inside">
                                    {selectedVocab.exp.map((ex, i) => (
                                        <React.Fragment key={i}>
                                            <li>
                                                <span className="text-gray-800"> {ex.esentence}</span>
                                            </li>
                                            <li className="list-none pl-6"> {/* Lùi vào và loại bỏ dấu chấm */}
                                                <span className="text-gray-800"> {ex.vsentence}</span>
                                            </li>
                                        </React.Fragment>
                                    ))}
                                </ul>
                            </div>
                        )}
                    </Card>
                </div>
            )}
            <div className="flex justify-between items-center mt-6">
                <div>
                    <span className="text-gray-600">
                        Showing page {page + 1} of {totalPages} ({totalElements} items)
                    </span>
                </div>
                <div className="flex gap-2">
                    <Button
                        variant="outline"
                        disabled={page === 0}
                        onClick={() => setPage(page - 1)}
                    >
                        Previous
                    </Button>
                    <Button
                        variant="outline"
                        disabled={page + 1 >= totalPages}
                        onClick={() => setPage(page + 1)}
                    >
                        Next
                    </Button>
                </div>
            </div>
        </div>
    );
};

export default VocabularyList;