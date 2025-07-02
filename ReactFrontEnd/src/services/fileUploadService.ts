export const uploadFile = async (file: File, type: 'audio' | 'image'): Promise<string> => {
    const formData = new FormData();
    formData.append('file', file);

    const API_URL = import.meta.env.VITE_API_URL;
    const endpoint = type === 'audio' ? '/api/upload/audio' : '/api/upload/image';

    const response = await fetch(`${API_URL}${endpoint}`, {
        method: 'POST',
        body: formData,
        credentials: 'include'
    });

    if (!response.ok) {
        throw new Error(`Failed to upload ${type} file`);
    }

    const data = await response.json();
    return data.url;
}; 