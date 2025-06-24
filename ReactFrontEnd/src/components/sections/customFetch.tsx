export const customFetch = (url: string, options: RequestInit = {}) => {
    // Các URL ngoại lệ — không cần credentials
    const excludeUrls = [

    ];

    const shouldIncludeCredentials = !excludeUrls.some(excludeUrl => url.startsWith(excludeUrl));

    return fetch(url, {
        ...options,
        ...(shouldIncludeCredentials ? { credentials: "include" } : {}),  // nếu false → không thêm gì
    });
};