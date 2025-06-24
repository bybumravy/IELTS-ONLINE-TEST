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
    const { user, isLoading } = useAuth(); // Lấy thêm isLoading
    const hasChecked = useRef(false);

    useEffect(() => {
        if (hasChecked.current || isLoading) return; // Chờ đến khi loading xong
        hasChecked.current = true;

        if (!user) {
            navigate("/loginadmin", { state: { from: location.pathname } });
            return;
        }

        if (!user.role || !allowRoles.includes(user.role)) {
            navigate("/error", { state: { code: 403 } });
        }
    }, [user, isLoading, allowRoles, navigate]); // Thêm isLoading vào dependencies

    // Hiển thị loading nếu chưa xác định xong
    if (isLoading) {
        return <div>Loading...</div>; // Hoặc spinner
    }

    if (!user || !user.role || !allowRoles.includes(user.role)) {
        return null;
    }

    return <>{children}</>;
}