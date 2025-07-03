import { useEffect, useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import { CheckCircle, XCircle } from "lucide-react";

export default function VnPayResultPage() {
    const [searchParams] = useSearchParams();
    const [status, setStatus] = useState<"success" | "failed" | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        const responseCode = searchParams.get("vnp_ResponseCode");

        if (responseCode === "00") {
            setStatus("success");

            // Gọi nâng cấp Premium
            fetch("http://localhost:8080/api/user/upgrade-premium", {
                method: "POST",
                credentials: "include",
            })
                .then(res => {
                    if (!res.ok) throw new Error("Failed to upgrade premium");
                    return res.text();
                })
                .then(msg => console.log(msg))
                .catch(err => console.error(err));

            // ✅ Lấy selectedPlan từ localStorage
            const stored = localStorage.getItem("selectedPlan");
            const selectedPlan = stored ? JSON.parse(stored) : null;

            if (selectedPlan) {
                fetch("http://localhost:8080/api/transactions/save", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    credentials: "include",
                    body: JSON.stringify({
                        type: selectedPlan.duration,              // ví dụ: "1 tháng"
                        amount: selectedPlan.price,
                        paymentMethod: "VNPay",
                        status: "SUCCESS",
                        message: "Giao dịch thành công",
                    }),
                });
            }

            // ✅ Dọn sạch sau khi xài xong
            localStorage.removeItem("selectedPlan");

            // Chuyển trang
            setTimeout(() => {
                window.location.href = "/";
            }, 3000);
        } else {
            setStatus("failed");
        }
    }, [searchParams]);



    return (
        <div className="min-h-screen flex flex-col items-center justify-center bg-green-50 text-center px-4">
            {status === "success" ? (
                <>
                    <CheckCircle className="text-green-600 w-20 h-20 mb-4" />
                    <h1 className="text-3xl font-bold text-green-700">Thanh toán thành công!</h1>
                    <p className="text-gray-600 mt-2">Cảm ơn bạn đã đăng ký gói học IELTS Premium.</p>
                </>
            ) : status === "failed" ? (
                <>
                    <XCircle className="text-red-600 w-20 h-20 mb-4" />
                    <h1 className="text-3xl font-bold text-red-700">Thanh toán thất bại</h1>
                    <p className="text-gray-600 mt-2">Vui lòng thử lại hoặc liên hệ hỗ trợ.</p>
                </>
            ) : (
                <p>Đang xử lý kết quả thanh toán...</p>
            )}
        </div>
    );
}
