import { useEffect } from "react";
import { useAuth } from "@/contexts/AuthContext";
import { Navigate, useLocation } from "react-router-dom";

export default function ProtectedLayout({ children, allowRoles }: { children: React.ReactNode; allowRoles: string[] }) {
    const { user } = useAuth();
    const location = useLocation();

    useEffect(() => {
        if (!user) {
            window.alert("Bạn phải đăng nhập mới vào được trang này");
        }
    }, [user]);

    if (!user) {
        // Chưa login → bắt login
        return <Navigate to="/login" state={{ from: location }} replace />;
    }

    if (!allowRoles.includes(user.role)) {
        // Đã login nhưng sai role → chặn
        return <Navigate to="/" replace />;
    }

    return <>{children}</>;
}
