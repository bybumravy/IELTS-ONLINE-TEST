export interface IELTSTest {
    id: number
    title: string
    type: string
    duration: string
    sections: string[]
    difficulty: string
    participants: number
    rating: number
    isNew: boolean
}

export interface Tip {
    id: number
    skill: string
    title: string
    description: string
    readTime: string
    icon: any
    color: string
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
