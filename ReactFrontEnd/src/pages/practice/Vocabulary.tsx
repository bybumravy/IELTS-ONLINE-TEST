import React, { useState, useEffect } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import type { Vocabulary as VocabularyType } from '@/lib/type';
import '@/Vocabulary/Vocabulary.module.css';

const VocabularyForm: React.FC<{
    onSubmit: (vocabulary: Omit<VocabularyType, 'id'>) => void;
}> = ({ onSubmit }) => {
    const [formData, setFormData] = useState<Omit<VocabularyType, 'id'>>({
        word: '',
        translate: '',
        explanation: '',
        topic: '',
        band: '',
        exp: [{ esentence: '', vsentence: '' }]
    });

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
        setFormData({
            word: '',
            translate: '',
            explanation: '',
            topic: '',
            band: '',
            exp: [{ esentence: '', vsentence: '' }]
        });
    };

    return (
        <form onSubmit={handleSubmit} className={styles.vocabularyForm}>
            <input
                type="text"
                name="word"
                value={formData.word}
                onChange={handleChange}
                placeholder="Word"
                required
                className={styles.inputField}
            />
            <input
                type="text"
                name="translate"
                value={formData.translate}
                onChange={handleChange}
                placeholder="Translate"
                required
                className={styles.inputField}
            />
            <input
                type="text"
                name="explanation"
                value={formData.explanation}
                onChange={handleChange}
                placeholder="Explanation"
                required
                className={styles.inputField}
            />
            <select
                name="topic"
                value={formData.topic}
                onChange={handleChange}
                required
                className={styles.selectField}
            >
                <option value="">Select Topic</option>
                <option value="Environment">Environment</option>
                <option value="Education">Education</option>
                <option value="Technology">Technology</option>
            </select>
            <select
                name="band"
                value={formData.band}
                onChange={handleChange}
                required
                className={styles.selectField}
            >
                <option value="">Select Band</option>
                <option value="5.0">5.0</option>
                <option value="6.0">6.0</option>
                <option value="6.5">6.5</option>
                <option value="7.0">7.0</option>
            </select>
            <div>
                <strong>Example sentences:</strong>
                {formData.exp.map((ex, idx) => (
                    <div key={idx} className={styles.exampleGroup}>
                        <input
                            type="text"
                            value={ex.esentence}
                            onChange={e => handleExpChange(idx, 'esentence', e.target.value)}
                            placeholder="English sentence"
                            required
                            className={styles.inputField}
                        />
                        <input
                            type="text"
                            value={ex.vsentence}
                            onChange={e => handleExpChange(idx, 'vsentence', e.target.value)}
                            placeholder="Vietnamese sentence"
                            required
                            className={styles.inputField}
                        />
                        {formData.exp.length > 1 && (
                            <button type="button" onClick={() => removeExample(idx)} className={styles.removeButton}>Remove</button>
                        )}
                    </div>
                ))}
                <button type="button" onClick={addExample} className={styles.addButton}>+ Add Example</button>
            </div>
            <button type="submit" className={styles.submitButton}>
                Add Vocabulary
            </button>
        </form>
    );
};

const VocabularyItem: React.FC<{
    vocabulary: VocabularyType;
    onUpdate: (id: string, updatedVocabulary: VocabularyType) => void;
    onDelete: (id: string) => void;
}> = ({ vocabulary, onUpdate, onDelete }) => {
    const [isEditing, setIsEditing] = useState(false);
    const [editData, setEditData] = useState<VocabularyType>(vocabulary);

    const handleEditChange = (
        e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
    ) => {
        const { name, value } = e.target;
        setEditData(prev => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleExpChange = (idx: number, field: 'esentence' | 'vsentence', value: string) => {
        setEditData(prev => ({
            ...prev,
            exp: prev.exp.map((ex, i) =>
                i === idx ? { ...ex, [field]: value } : ex
            )
        }));
    };

    const addExample = () => {
        setEditData(prev => ({
            ...prev,
            exp: [...prev.exp, { esentence: '', vsentence: '' }]
        }));
    };

    const removeExample = (idx: number) => {
        setEditData(prev => ({
            ...prev,
            exp: prev.exp.filter((_, i) => i !== idx)
        }));
    };

    const handleSave = () => {
        onUpdate(vocabulary.id, editData);
        setIsEditing(false);
    };

    return (
        <div className={styles.vocabularyItem}>
            {isEditing ? (
                <div className={styles.editForm}>
                    <input
                        type="text"
                        name="word"
                        value={editData.word}
                        onChange={handleEditChange}
                        className={styles.inputField}
                    />
                    <input
                        type="text"
                        name="translate"
                        value={editData.translate}
                        onChange={handleEditChange}
                        className={styles.inputField}
                    />
                    <input
                        type="text"
                        name="explanation"
                        value={editData.explanation}
                        onChange={handleEditChange}
                        className={styles.inputField}
                    />
                    <select
                        name="topic"
                        value={editData.topic}
                        onChange={handleEditChange}
                        className={styles.selectField}
                    >
                        <option value="Environment">Environment</option>
                        <option value="Education">Education</option>
                        <option value="Technology">Technology</option>
                    </select>
                    <select
                        name="band"
                        value={editData.band}
                        onChange={handleEditChange}
                        className={styles.selectField}
                    >
                        <option value="5.0">5.0</option>
                        <option value="6.0">6.0</option>
                        <option value="6.5">6.5</option>
                        <option value="7.0">7.0</option>
                    </select>
                    <div>
                        <strong>Example sentences:</strong>
                        {editData.exp.map((ex, idx) => (
                            <div key={idx} className={styles.exampleGroup}>
                                <input
                                    type="text"
                                    value={ex.esentence}
                                    onChange={e => handleExpChange(idx, 'esentence', e.target.value)}
                                    placeholder="English sentence"
                                    required
                                    className={styles.inputField}
                                />
                                <input
                                    type="text"
                                    value={ex.vsentence}
                                    onChange={e => handleExpChange(idx, 'vsentence', e.target.value)}
                                    placeholder="Vietnamese sentence"
                                    required
                                    className={styles.inputField}
                                />
                                {editData.exp.length > 1 && (
                                    <button type="button" onClick={() => removeExample(idx)} className={styles.removeButton}>Remove</button>
                                )}
                            </div>
                        ))}
                        <button type="button" onClick={addExample} className={styles.addButton}>+ Add Example</button>
                    </div>
                    <div className={styles.editActions}>
                        <button onClick={handleSave} className={styles.saveButton}>
                            Save
                        </button>
                        <button
                            onClick={() => setIsEditing(false)}
                            className={styles.cancelButton}
                        >
                            Cancel
                        </button>
                    </div>
                </div>
            ) : (
                <div className={styles.vocabularyContent}>
                    <h3>{vocabulary.word}</h3>
                    <p><strong>Translate:</strong> {vocabulary.translate}</p>
                    <p><strong>Explanation:</strong> {vocabulary.explanation}</p>
                    <p><strong>Topic:</strong> {vocabulary.topic}</p>
                    <p><strong>Band:</strong> {vocabulary.band}</p>
                    <div>
                        <strong>Examples:</strong>
                        <ul>
                            {vocabulary.exp && vocabulary.exp.map((ex, i) => (
                                <li key={i}>
                                    <b>EN:</b> {ex.esentence}<br />
                                    <b>VI:</b> {ex.vsentence}
                                </li>
                            ))}
                        </ul>
                    </div>
                    <div className={styles.actions}>
                        <button
                            onClick={() => setIsEditing(true)}
                            className={styles.editButton}
                        >
                            Edit
                        </button>
                        <button
                            onClick={() => onDelete(vocabulary.id)}
                            className={styles.deleteButton}
                        >
                            Delete
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

const Vocabulary: React.FC = () => {
    const { user } = useAuth();
    const [vocabularies, setVocabularies] = useState<VocabularyType[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [filters, setFilters] = useState({
        topic: '',
        band: '',
    });

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
                headers: {
                    'Content-Type': 'application/json',
                },
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
                headers: {
                    'Content-Type': 'application/json',
                },
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

    const handleUpdateVocabulary = async (id: string, updatedVocabulary: VocabularyType) => {
        try {
            const response = await fetch(`${API_BASE}/vocabulary/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: "include",
                body: JSON.stringify(updatedVocabulary),
            });
            if (!response.ok) throw new Error('Failed to update vocabulary');
            const updatedVocab = await response.json();
            setVocabularies((prev) =>
                prev.map((v) => (v.id === id ? updatedVocab : v))
            );
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

    const handleFilterChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const { name, value } = e.target;
        setFilters((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const applyFilters = () => {
        fetchVocabularies();
    };

    const resetFilters = () => {
        setFilters({ topic: '', band: '' });
        fetchVocabularies();
    };

    useEffect(() => {
        if (user) {
            fetchVocabularies();
        }
    }, [user]);

    if (!user) {
        return <div className={styles.message}>Please login to access vocabulary</div>;
    }
    if (loading) {
        return <div className={styles.message}>Loading...</div>;
    }
    if (error) {
        return <div className={styles.errorMessage}>Error: {error}</div>;
    }

    return (
        <div className={styles.vocabulary}>
            <h1>Vocabulary Management</h1>
            <div className={styles.filters}>
                <select
                    name="topic"
                    value={filters.topic}
                    onChange={handleFilterChange}
                    className={styles.filterSelect}
                >
                    <option value="">All Topics</option>
                    <option value="Environment">Environment</option>
                    <option value="Education">Education</option>
                    <option value="Technology">Technology</option>
                </select>
                <select
                    name="band"
                    value={filters.band}
                    onChange={handleFilterChange}
                    className={styles.filterSelect}
                >
                    <option value="">All Bands</option>
                    <option value="5.0">5.0</option>
                    <option value="6.0">6.0</option>
                    <option value="6.5">6.5</option>
                    <option value="7.0">7.0</option>
                </select>
                <button onClick={applyFilters} className={styles.filterButton}>
                    Apply Filters
                </button>
                <button onClick={resetFilters} className={styles.resetButton}>
                    Reset
                </button>
            </div>
            <VocabularyForm onSubmit={handleAddVocabulary} />
            <div className={styles.vocabularyList}>
                {vocabularies.length === 0 ? (
                    <p className={styles.message}>Không tìm thấy dữ liệu.</p>
                ) : (
                    vocabularies.map((vocab) => (
                        <VocabularyItem
                            key={vocab.id}
                            vocabulary={vocab}
                            onUpdate={handleUpdateVocabulary}
                            onDelete={handleDeleteVocabulary}
                        />
                    ))
                )}
            </div>
        </div>
    );
};

export default Vocabulary;