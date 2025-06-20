import React, { useState, useEffect } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import type { Vocabulary as VocabularyType } from '@/lib/type';

// UI Components
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Badge } from '@/components/ui/badge';

const TOPICS = [
    { value: '', label: 'All Topics' },
    { value: 'Environment', label: 'Environment' },
    { value: 'Education', label: 'Education' },
    { value: 'Technology', label: 'Technology' },
];

const BANDS = [
    { value: '', label: 'All Bands' },
    { value: '5.0', label: '5.0' },
    { value: '6.0', label: '6.0' },
    { value: '6.5', label: '6.5' },
    { value: '7.0', label: '7.0' },
];

// --- Vocabulary Add/Edit Form Modal ---
const VocabularyFormModal: React.FC<{
    open: boolean;
    onClose: () => void;
    onSubmit: (vocabulary: Omit<VocabularyType, 'id'>) => void;
    initialData?: Omit<VocabularyType, 'id'>;
    isEdit?: boolean;
}> = ({ open, onClose, onSubmit, initialData, isEdit }) => {
    const [formData, setFormData] = useState<Omit<VocabularyType, 'id'>>(initialData || {
        word: '',
        translate: '',
        explanation: '',
        topic: '',
        band: '',
        exp: [{ esentence: '', vsentence: '' }]
    });

    useEffect(() => {
        if (open && initialData) setFormData(initialData);
        if (open && !initialData) {
            setFormData({
                word: '',
                translate: '',
                explanation: '',
                topic: '',
                band: '',
                exp: [{ esentence: '', vsentence: '' }]
            });
        }
    }, [open, initialData]);

    const handleChange = (
        e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
    ) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleExpChange = (idx: number, field: 'esentence' | 'vsentence', value: string) => {
        setFormData(prev => ({
            ...prev,
            exp: prev.exp.map((ex, i) =>
                i === idx ? { ...ex, [field]: value } : ex
            )
        }));
    };

    const addExample = () => {
        setFormData(prev => ({
            ...prev,
            exp: [...prev.exp, { esentence: '', vsentence: '' }]
        }));
    };

    const removeExample = (idx: number) => {
        setFormData(prev => ({
            ...prev,
            exp: prev.exp.filter((_, i) => i !== idx)
        }));
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        onSubmit(formData);
        onClose();
    };

    if (!open) return null;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/30">
            <Card className="relative w-full max-w-xl mx-2 p-8">
                <button
                    className="absolute top-3 right-3 text-gray-400 hover:text-red-500 text-xl"
                    onClick={onClose}
                    aria-label="Close"
                >×</button>
                <h2 className="font-bold text-xl mb-6 " >{isEdit ? 'Edit Vocabulary' : 'Add Vocabulary'}</h2>
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <Label>Word</Label>
                        <Input name="word" value={formData.word} onChange={handleChange} required />
                    </div>
                    <div>
                        <Label>Translate</Label>
                        <Input name="translate" value={formData.translate} onChange={handleChange} required />
                    </div>
                    <div>
                        <Label>Explanation</Label>
                        <Textarea name="explanation" value={formData.explanation} onChange={handleChange} required />
                    </div>
                    <div className="flex gap-3">
                        <div className="flex-1">
                            <Label>Topic</Label>
                            <select
                                name="topic"
                                value={formData.topic}
                                onChange={handleChange}
                                required
                                className="w-full px-2 py-2 rounded border border-gray-300"
                            >
                                <option value="">Select Topic</option>
                                {TOPICS.slice(1).map(t => (
                                    <option key={t.value} value={t.value}>{t.label}</option>
                                ))}
                            </select>
                        </div>
                        <div className="flex-1">
                            <Label>Band</Label>
                            <select
                                name="band"
                                value={formData.band}
                                onChange={handleChange}
                                required
                                className="w-full px-2 py-2 rounded border border-gray-300"
                            >
                                <option value="">Select Band</option>
                                {BANDS.slice(1).map(b => (
                                    <option key={b.value} value={b.value}>{b.label}</option>
                                ))}
                            </select>
                        </div>
                    </div>
                    <div>
                        <Label>Example Sentences</Label>
                        <div className="space-y-2">
                            {formData.exp.map((ex, idx) => (
                                <div key={idx} className="flex gap-2 items-center">
                                    <Input
                                        value={ex.esentence}
                                        onChange={e => handleExpChange(idx, 'esentence', e.target.value)}
                                        placeholder="English sentence"
                                        required
                                    />
                                    <Input
                                        value={ex.vsentence}
                                        onChange={e => handleExpChange(idx, 'vsentence', e.target.value)}
                                        placeholder="Vietnamese sentence"
                                        required
                                    />
                                    {formData.exp.length > 1 && (
                                        <Button type="button" variant="ghost" size="sm" onClick={() => removeExample(idx)}>
                                            Remove
                                        </Button>
                                    )}
                                </div>
                            ))}
                        </div>
                        <Button type="button" onClick={addExample} variant="outline" className="mt-2  hover:bg-emerald-600 hover:text-white" size="sm">
                            + Add Example
                        </Button>
                    </div>
                    <Button type="submit" className="w-full mt-2 hover:bg-emerald-600 hover:text-white " variant="outline" >{isEdit ? 'Save Changes' : 'Add Vocabulary'}</Button>
                </form>
            </Card>
        </div>
    );
};

// --- Vocabulary Item Card ---
const VocabularyItem: React.FC<{
    vocabulary: VocabularyType;
    onEdit: (vocab: VocabularyType) => void;
    onDelete: (id: string) => void;
}> = ({ vocabulary, onEdit, onDelete }) => (
    <Card className="p-5 flex flex-col gap-2">
        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-2">
            <div>
                <h2 className="text-lg font-bold">{vocabulary.word}</h2>
                <div className="flex flex-wrap gap-2 my-1">
                    <Badge className="bg-emerald-600 text-white">{vocabulary.topic}</Badge>
                    <Badge className="bg-emerald-600 text-white">Band {vocabulary.band}</Badge>
                </div>
            </div>
            <div>
                <Button size="sm" variant="outline" className="mr-2 hover:bg-emerald-600 hover:text-white" onClick={() => onEdit(vocabulary)}>
                    Edit
                </Button>
                <Button size="sm" variant="outline" className="mr-2 hover:bg-emerald-600 hover:text-white" onClick={() => onDelete(vocabulary.id)}>
                    Delete
                </Button>
            </div>
        </div>
        <div className="pl-1">
            <div className="text-gray-700"><b>Translate:</b> {vocabulary.translate}</div>
            <div className="text-gray-700"><b>Explanation:</b> {vocabulary.explanation}</div>
            {vocabulary.exp?.length > 0 && (
                <div className="mt-2">
                    <div className="font-semibold text-gray-700 mb-1">Examples:</div>
                    <ul className="list-disc list-inside">
                        {vocabulary.exp.map((ex, i) => (
                            <li key={i}>
                                <span className="text-gray-800">English: {ex.esentence}</span><br/>
                                <span className="text-gray-800">Vietnamese: {ex.vsentence}</span>
                            </li>
                        ))}
                    </ul>
                </div>
            )}
        </div>
    </Card>
);

// --- Main Vocabulary Page ---
const Vocabulary: React.FC = () => {
    const { user } = useAuth();
    const [vocabularies, setVocabularies] = useState<VocabularyType[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [filters, setFilters] = useState({ topic: '', band: '' });

    // Modal state
    const [showAdd, setShowAdd] = useState(false);
    const [editData, setEditData] = useState<VocabularyType | null>(null);

    const API_BASE = "http://localhost:8080/api/practice";

    const fetchVocabularies = async () => {
        try {
            setLoading(true);
            const { topic, band } = filters;
            let url = `${API_BASE}/vocabulary`;
            if (topic || band) {
                const params = new URLSearchParams();
                if (topic) params.append('topic', topic);
                if (band) params.append('band', band);
                url += `/filter?${params.toString()}`;
            }
            const response = await fetch(url, {
                headers: { 'Content-Type': 'application/json' },
                credentials: "include"
            });
            if (!response.ok) throw new Error('Failed to fetch vocabularies');
            const data = await response.json();
            setVocabularies(data);
            setError('');
        } catch (err) {
            setError(err instanceof Error ? err.message : 'An error occurred');
        } finally {
            setLoading(false);
        }
    };

    const handleAddVocabulary = async (vocabulary: Omit<VocabularyType, 'id'>) => {
        try {
            const response = await fetch(`${API_BASE}/vocabulary/add`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: "include",
                body: JSON.stringify(vocabulary),
            });
            if (!response.ok) throw new Error('Failed to add vocabulary');
            const newVocabulary = await response.json();
            setVocabularies((prev) => [...prev, newVocabulary]);
            setError('');
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Failed to add vocabulary');
        }
    };

    const handleEditVocabulary = async (id: string, updated: Omit<VocabularyType, 'id'>) => {
        try {
            const response = await fetch(`${API_BASE}/vocabulary/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                credentials: "include",
                body: JSON.stringify(updated),
            });
            if (!response.ok) throw new Error('Failed to update vocabulary');
            const updatedVocab = await response.json();
            setVocabularies((prev) => prev.map((v) => (v.id === id ? updatedVocab : v)));
            setError('');
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Failed to update vocabulary');
        }
    };

    const handleDeleteVocabulary = async (id: string) => {
        try {
            const response = await fetch(`${API_BASE}/vocabulary/${id}`, {
                method: 'DELETE',
                credentials: "include"
            });
            if (!response.ok) throw new Error('Failed to delete vocabulary');
            setVocabularies((prev) => prev.filter((v) => v.id !== id));
            setError('');
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Failed to delete vocabulary');
        }
    };

    useEffect(() => {
        if (user) fetchVocabularies();
    }, [user]);

    // Filter handler
    const handleFilterChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const { name, value } = e.target;
        setFilters((prev) => ({ ...prev, [name]: value }));
    };
    const applyFilters = () => fetchVocabularies();
    const resetFilters = () => {
        setFilters({ topic: '', band: '' });
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
            <h1 className="text-2xl font-bold mb-6 text-center">Vocabulary Management</h1>
            <Card className="mb-6 p-6">
                <div className="flex flex-col md:flex-row gap-4 items-center">
                    <div className="flex-1 flex gap-2">
                        <select
                            name="topic"
                            value={filters.topic}
                            onChange={handleFilterChange}
                            className="w-full px-2 py-2 rounded border border-gray-300"
                        >
                            {TOPICS.map(t => (
                                <option key={t.value} value={t.value}>{t.label}</option>
                            ))}
                        </select>
                        <select
                            name="band"
                            value={filters.band}
                            onChange={handleFilterChange}
                            className="w-full px-2 py-2 rounded border border-gray-300"
                        >
                            {BANDS.map(b => (
                                <option key={b.value} value={b.value}>{b.label}</option>
                            ))}
                        </select>
                    </div>
                    <div className="flex gap-2">
                        <Button onClick={applyFilters} variant="outline"  className="  hover:bg-emerald-600 hover:text-white" >Apply Filters</Button>
                        <Button onClick={resetFilters} variant="outline"  className="  hover:bg-emerald-600 hover:text-white">Reset</Button>
                        <Button onClick={() => setShowAdd(true)} variant="outline"  className="  hover:bg-emerald-600 hover:text-white">+ Add Vocabulary</Button>
                    </div>
                </div>
            </Card>

            {/* Add Modal */}
            <VocabularyFormModal
                open={showAdd}
                onClose={() => setShowAdd(false)}
                onSubmit={handleAddVocabulary}
            />

            {/* Edit Modal */}
            <VocabularyFormModal
                open={!!editData}
                onClose={() => setEditData(null)}
                onSubmit={updated => {
                    if (editData) handleEditVocabulary(editData.id, updated);
                    setEditData(null);
                }}
                initialData={editData ? { ...editData, id: undefined } as any : undefined}
                isEdit
            />

            {/* Vocabulary List */}
            <div className="space-y-5">
                {vocabularies.length === 0 ? (
                    <Card className="p-4 text-center text-gray-500">No vocabulary found.</Card>
                ) : (
                    vocabularies.map((vocab) => (
                        <VocabularyItem
                            key={vocab.id}
                            vocabulary={vocab}
                            onEdit={v => setEditData(v)}
                            onDelete={handleDeleteVocabulary}
                        />
                    ))
                )}
            </div>
        </div>
    );
};

export default Vocabulary;