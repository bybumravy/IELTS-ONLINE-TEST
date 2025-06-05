export interface IELTSTest {
    id: string
    testTitle: string
    tags: string[]
    createdAt: string
}


export interface Tip {
    id: number
    skill: string
    type: string
    description: string
}

export interface FAQ {
    question: string
    answer: string
}

export interface User {
    username: string
    role: 'student' | 'teacher' | 'manager' | 'admin'
}

export interface AuthContextType {
    user: User | null
    login: (email: string, password: string) => Promise<void>
    logout: () => void
    register: (email: string, password: string, role?: string) => Promise<void>
}

