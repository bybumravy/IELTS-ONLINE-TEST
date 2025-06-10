
export const login = async (email: string, password: string) => {
    const res = await fetch("http://localhost:8080/api/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ email, password }),
    })
   console.log(res);
    if (!res.ok) throw new Error("Login failed")
}

export const logout = async () => {
    await fetch("http://localhost:8080/api/logout", {
        method: "POST",
        credentials: "include",
    })
}

export const getMe = async () => {
    const res = await fetch("http://localhost:8080/api/user-info", { credentials: "include" })
    if (!res.ok) throw new Error("Failed to fetch user")
    return res.json()
}

export const register = async (email: string, password: string, role = "student") => {
    const res = await fetch("http://localhost:8080/api/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password, role }),
    })

    if (!res.ok) throw new Error("Register failed")
}

export const listeningTestService = {
    async getAllTests(): Promise<ListeningTest[]> {
        const response = await fetch(`http://localhost:8080/api/tests`);
        if (!response.ok) {
            throw new Error('Failed to fetch tests');
        }
        const data = await response.json();
        return data;
    },

    async getTestById(id: number): Promise<ListeningTest> {
        const response = await fetch(`http://localhost:8080/api/${id}`);
        if (!response.ok) {
            throw new Error(`Failed to fetch test with id ${id}`);
        }
        const data = await response.json();
        return data;
    }
};