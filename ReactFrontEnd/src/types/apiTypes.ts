export interface IELTSTest {
    id: string          // vì Java dùng String cho id
    testTitle: string   // trùng với testTitle bên Java
    tags: string[]      // danh sách tag, kiểu List<String> bên Java
    createdAt: string   // ngày giờ trả về dạng chuỗi ISO (backend trả Date, frontend nhận string)
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

