export interface Section{
    question: string | string[];
    options?: string[];
    answer: string | string[];
    explanation: string | string[];
}
export interface Exercises {
    paragraph?: string;
    audioUrl?: string;
    instruction: string;
    imageUrl?: string;
    section: Section[];
}

export interface TipDetail {
    id: string | number;
    type: string;
    skill: string;
    description: string;
    strategy?: string[];
    tips?: string[];
    exercises: Exercises[];
}
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

