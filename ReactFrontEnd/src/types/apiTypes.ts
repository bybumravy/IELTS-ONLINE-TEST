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

export interface ListeningQuestion {
    id: number;
    questionNumber: number;
    question: string;
    type: 'text' | 'multiple-choice' | 'matching';
    options?: string[];
    correctAnswer: string;
}

export interface ListeningPart {
    id: number;
    partNumber: number;
    title: string;
    instructions: string;
    description: string;
    questions: ListeningQuestion[];
}

export interface ListeningTest {
    id: number;
    title: string;
    description: string;
    audioUrl: string;
    duration: number;
    parts: ListeningPart[];
}
export interface Vocabulary {
    id: string;
    word: string;
    translate: string;
    partOfSpeech?: string;
    pronunciation?: string;
}
