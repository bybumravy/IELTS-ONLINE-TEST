export const customFetch = async (url: string, options: RequestInit = {}) => {
    const excludeUrls = ["/api/refresh-token"];

    const shouldIncludeCredentials = !excludeUrls.some(excludeUrl =>
        url.includes(excludeUrl)
    );

    let response = await fetch(url, {
        ...options,
        ...(shouldIncludeCredentials ? { credentials: "include" } : {}),
    });

    // Nếu AccessToken hết hạn và không gọi chính /refresh-token
    if (response.status === 401 && !url.includes("/refresh-token")) {
        const API_URL = import.meta.env.VITE_API_URL;
        const refreshRes = await fetch(`${API_URL}/api/refreshtoken`, {
            method: "POST",
            credentials: "include",
        });

        if (refreshRes.ok) {
            // Gọi lại request ban đầu sau khi refresh token thành công
            response = await fetch(url, {
                ...options,
                ...(shouldIncludeCredentials ? { credentials: "include" } : {}),
            });
        } else {
            // Nếu refresh thất bại → chuyển về login
            window.location.href = "/login";
            throw new Error("Phiên đăng nhập đã hết hạn.");
        }
    }

    return response;
};
