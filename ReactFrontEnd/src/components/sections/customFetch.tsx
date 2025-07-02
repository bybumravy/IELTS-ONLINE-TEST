const API_URL = import.meta.env.VITE_API_URL;

export const customFetch = (url: string, options: RequestInit = {}) => {
    const excludeUrls = [

    ];

    const shouldIncludeCredentials = !excludeUrls.some(excludeUrl => url.startsWith(excludeUrl));

    return fetch(url.startsWith("http") ? url : `${API_URL}${url}`, {
        ...options,
        ...(shouldIncludeCredentials ? { credentials: "include" } : {}),
    });
};