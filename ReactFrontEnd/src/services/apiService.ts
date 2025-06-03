import type { IELTSTest, Tip, FAQ } from "@/types/apiTypes"


const API_BASE_URL = "http://localhost:8080/api"

export class ApiService {
    private static async request<T>(endpoint: string, options?: RequestInit): Promise<T> {
        const url = `${API_BASE_URL}${endpoint}`
        const defaultHeaders: Record<string, string> = {
            "Content-Type": "application/json",
        }


        let extraHeaders: Record<string, string> = {}
        if (options?.headers instanceof Headers) {
            extraHeaders = Object.fromEntries(options.headers.entries())
        } else if (options?.headers) {
            extraHeaders = options.headers as Record<string, string>
        }


        const mergedHeaders = {
            ...defaultHeaders,
            ...extraHeaders,
        }


        const response = await fetch(url, {
            ...options,
            headers: mergedHeaders,
        })

        if (!response.ok) {
            throw new Error(`API request failed: ${response.statusText}`)
        }

        return response.json()
    }


    static async getLatestTests(): Promise<IELTSTest[]> {
        return this.request<IELTSTest[]>("/tests/latest")
    }

    static async getTestById(id: number): Promise<IELTSTest> {
        return this.request<IELTSTest>(`/tests/${id}`)
    }

    static async getTestsBySkill(skill: string): Promise<IELTSTest[]> {
        return this.request<IELTSTest[]>(`/tests/skill/${skill}`)
    }


    static async getTips(): Promise<Tip[]> {
        return this.request<Tip[]>("/tips")
    }

    static async getTipsBySkill(skill: string): Promise<Tip[]> {
        return this.request<Tip[]>(`/tips/skill/${skill}`)
    }


    static async getFAQs(): Promise<FAQ[]> {
        return this.request<FAQ[]>("/faqs")
    }

    static async login(email: string, password: string): Promise<{ token: string; user: any }> {
        return this.request<{ token: string; user: any }>("/auth/login", {
            method: "POST",
            body: JSON.stringify({ email, password }),
        })
    }

    static async register(userData: { name: string; email: string; password: string }): Promise<{
        token: string
        user: any
    }> {
        return this.request<{ token: string; user: any }>("/auth/register", {
            method: "POST",
            body: JSON.stringify(userData),
        })
    }

    static async getUserProfile(token: string): Promise<any> {
        return this.request<any>("/user/profile", {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        })
    }

    static async getUserTestHistory(token: string): Promise<any[]> {
        return this.request<any[]>("/user/test-history", {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        })
    }
}
