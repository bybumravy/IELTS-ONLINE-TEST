import { useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "@/contexts/AuthContext";

export default function ProtectedLayoutRole({
                                                children,
                                                allowRoles
                                            }: {
    children: React.ReactNode;
    allowRoles: string[]
}) {
    const navigate = useNavigate();
    const { user } = useAuth();
    const hasChecked = useRef(false);

    useEffect(() => {
        if (hasChecked.current) return;
        hasChecked.current = true;

        if (!user) {
            navigate("/loginadmin", { state: { from: location.pathname } });
            return;
        }

        if (!user.role || !allowRoles.includes(user.role)) {

            navigate("/error", { state: { code: 403 } });
        }
    }, [user, allowRoles, navigate]);

    if (!user || !user.role || !allowRoles.includes(user.role)) {
        return null; // Hoặc return loading spinner
    }

    return <>{children}</>;
}