import { useEffect, useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import { useAuth } from "@/contexts/AuthContext";

export default function VerifyEmail() {
    const { fetchUser } = useAuth(); // <-- dùng context đúng
    const [searchParams] = useSearchParams();
    const [message, setMessage] = useState("Đang xác thực...");
    const [isSuccess, setIsSuccess] = useState<boolean | null>(null);
    const token = searchParams.get("token");
    const navigate = useNavigate();
    const API_URL = import.meta.env.VITE_API_URL;

    useEffect(() => {
        const verify = async () => {
            try {
                const res = await fetch(`${API_URL}/api/verify-email?token=${token}`, {
                    method: "GET",
                    credentials: "include"
                });

                if (!res.ok) throw new Error(await res.text());

                await fetchUser(); // <-- cập nhật lại user từ backend

                setMessage("✅ Xác thực thành công! Bạn có thể trở về trang chủ.");
                setIsSuccess(true);

            } catch (err: any) {
                setMessage("❌ Xác thực thất bại: " + err.message);
                setIsSuccess(false);
            }
        };

        if (token) {
            verify();
        } else {
            setMessage("⚠️ Thiếu token xác thực.");
            setIsSuccess(false);
        }
    }, [token, fetchUser]);

    const handleGoHome = () => {
        navigate("/");
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100">
            <div className="bg-white p-8 rounded shadow-md text-center max-w-md w-full">
                <h1 className={`text-2xl font-bold mb-4 ${isSuccess === true ? "text-green-600" : isSuccess === false ? "text-red-600" : "text-gray-600"}`}>
                    {isSuccess === null ? "Đang xử lý..." : isSuccess ? "Thành công" : "Lỗi"}
                </h1>
                <p className="text-gray-700">{message}</p>

                {isSuccess && (
                    <button
                        onClick={handleGoHome}
                        className="mt-6 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition"
                    >
                        OK – Quay về trang chủ
                    </button>
                )}
            </div>
        </div>
    );
}