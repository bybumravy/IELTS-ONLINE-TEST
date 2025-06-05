export const login = async (email: string, password: string) => {
    const res = await fetch("http://localhost:8080/api/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ email, password }),
    })

    if (!res.ok) throw new Error("Login failed")
}

export const logout = async () => {
    await fetch("http://localhost:8080/api/logout", {
        method: "POST",
        credentials: "include",
    })
}

// export const getMe = async () => {
//     const res = await fetch("http://localhost:8080/api/me", { credentials: "include" })
//     if (!res.ok) throw new Error("Failed to fetch user")
//     return res.json()
// }

export const register = async (email: string, password: string, role = "student") => {
    const res = await fetch("http://localhost:8080/api/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password, role }),
    })

    if (!res.ok) throw new Error("Register failed")
}
