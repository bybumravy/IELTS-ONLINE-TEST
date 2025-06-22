import { useEffect, useState } from "react";
import { useAuth } from "@/contexts/AuthContext";

export default function ProtectedLayoutRole({ children, allowRoles }: { children: React.ReactNode; allowRoles: string[] }) {
    const { user } = useAuth();
    const [blockReason, setBlockReason] = useState<string | null>(null);
    console.log(user)
    useEffect(() => {

        if (!user) {
            setBlockReason("Bạn phải đăng nhập mới vào được trang này");
        } else if (!allowRoles.includes(user.role)) {
            setBlockReason("Bạn không có quyền truy cập trang này");
        } else {
            setBlockReason(null); // OK, không bị block
        }
    }, [user, allowRoles]);

    useEffect(() => {
        if (blockReason) {
            window.alert(blockReason);
        }
    }, [blockReason]);

    if (blockReason) {
        return null;
    }

    return <>{children}</>;
}
