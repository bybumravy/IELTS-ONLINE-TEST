import { createContext, useContext, type ReactNode } from "react"
import { useAuthState } from "@/hooks/useAuthState"
import type { AuthContextType } from "@/types/apiTypes"

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
    const auth = useAuthState()
    return <AuthContext.Provider value={auth}>{children}</AuthContext.Provider>
}

export function useAuth() {
    const context = useContext(AuthContext)
    if (!context) throw new Error("useAuth phải dùng trong AuthProvider")
    return context
}
