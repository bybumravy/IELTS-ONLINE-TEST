export const uploadFile = async (file: File, type: 'audio' | 'image'): Promise<string> => {
    const formData = new FormData();
    formData.append('file', file);

    const endpoint = type === 'audio' ? '/api/upload/audio' : '/api/upload/image';

    const response = await fetch(`http://localhost:8080${endpoint}`, {
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