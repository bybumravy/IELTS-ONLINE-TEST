export const uploadFile = async (file: File): Promise<string> => {
    const formData = new FormData();
    formData.append('file', file);

    const response = await fetch('http://localhost:8080/api/upload', {
        method: 'POST',
        body: formData,
        credentials: 'include'
    });

    if (!response.ok) {
        throw new Error('Failed to upload file');
    }

    const data = await response.json();
    return data.url;
}; 