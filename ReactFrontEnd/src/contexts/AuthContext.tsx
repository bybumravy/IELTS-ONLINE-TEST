import { createContext, useContext, type ReactNode, useState, useEffect } from "react";
import type { AuthContextType, User } from "@/types/apiTypes";
import * as authService from "@/services/authService";

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<User | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    const fetchUser = async () => {
        setIsLoading(true);
        try {
            const data = await authService.getMe();
            if (data) {
                setUser({ username: data.username, role: data.role });
            } else {
                setUser(null);
            }
        } catch {
            setUser(null);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        fetchUser();
    }, []);

    const login = async (email: string, password: string) => {
        await authService.login(email, password);
        await fetchUser();
    };

    const logout = async () => {
        await authService.logout();
        setUser(null);
    };

    const register = async (email: string, password: string, role = "student") => {
        await authService.register(email, password, role);
        await fetchUser();
    };

    return (
        <AuthContext.Provider value={{
            user,
            isLoading, // Cung cấp trạng thái loading
            login,
            logout,
            register,
            fetchUser
        }}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    const context = useContext(AuthContext);
    if (!context) throw new Error("useAuth phải dùng trong AuthProvider");
    return context;
}
