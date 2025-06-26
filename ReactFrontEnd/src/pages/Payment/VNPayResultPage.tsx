import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { CheckCircle, XCircle } from "lucide-react";

export default function VnPayResultPage() {
    const [searchParams] = useSearchParams();
    const [status, setStatus] = useState<"success" | "failed" | null>(null);

    useEffect(() => {
        const responseCode = searchParams.get("vnp_ResponseCode");
        if (responseCode === "00") {
            setStatus("success");
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