import { useState } from 'react';
import axios from 'axios';
import { Button, Card, message } from 'antd';

interface CoursePackage {
    id: string;
    name: string;
    price: number;
}

const packages: CoursePackage[] = [
    { id: "writing", name: "IELTS Writing Premium", price: 299000 },
    { id: "speaking", name: "IELTS Speaking Premium", price: 299000 },
    { id: "full", name: "IELTS Full Package", price: 499000 }
];

export default function PremiumPage() {
    const [selected, setSelected] = useState<CoursePackage | null>(null);
    const [loading, setLoading] = useState(false);

    const handlePay = async () => {
        if (!selected) return;

        setLoading(true);
        try {
            const response = await axios.post("http://localhost:8080/api/momo/create", {
                courseId: selected.id,
                courseName: selected.name,
                amount: selected.price
            });

            const payUrl = response.data.payUrl;
            if (payUrl) {
                window.location.href = payUrl;
            } else {
                message.error("Không nhận được đường dẫn thanh toán.");
            }
        } catch (error) {
            console.error("Lỗi tạo thanh toán:", error);
            message.error("Tạo thanh toán thất bại.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="flex flex-col items-center gap-6 p-8">
            <h2 className="text-2xl font-bold">Chọn gói học IELTS</h2>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 w-full max-w-4xl">
                {packages.map(pkg => (
                    <Card
                        key={pkg.id}
                        title={pkg.name}
                        onClick={() => setSelected(pkg)}
                        style={{
                            border: selected?.id === pkg.id ? '2px solid #1677ff' : undefined
                        }}
                    >
                        <p><strong>Giá:</strong> {pkg.price.toLocaleString()} VND</p>
                    </Card>
                ))}
            </div>

            <Button
                type="primary"
                loading={loading}
                disabled={!selected}
                onClick={handlePay}
                className="bg-pink-600 text-white"
            >
                Thanh toán với MoMo
            </Button>
        </div>
    );
}
