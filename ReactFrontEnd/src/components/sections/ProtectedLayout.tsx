import { useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "@/contexts/AuthContext";

export default function ProtectedLayoutRole({
                                                children,
                                                allowRoles
                                            }: {
    children: React.ReactNode;
    allowRoles: string[];
}) {
    const navigate = useNavigate();
    const location = useLocation();
    const { user, isLoading } = useAuth();

    useEffect(() => {
        if (isLoading) return;

        if (!user) {
            // Sử dụng replace: true để ngăn history stack
            navigate("/login", {
                replace: true,
                state: { from: location.pathname }
            });
            return;
        }

        if (!user.role || !allowRoles.includes(user.role)) {
            navigate("/error", {
                replace: true,
                state: { code: 403 }
            });
        }
    }, [user, isLoading, allowRoles, navigate, location.pathname]);

    // Nếu đang loading hoặc user không hợp lệ, không render gì
    if (isLoading || !user || !user.role || !allowRoles.includes(user.role)) {
        return null;
    }

    return <>{children}</>;
}