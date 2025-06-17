// IELTS Premium Page with MoMo Payment Integration
import {useEffect, useState} from "react"
import axios from "axios"
import {
    Check,
    Star,
    Zap,
    MessageSquare,
    FileText,
    Trophy,
    Users,
    Clock,
} from "lucide-react"
import { Button } from "@/components/ui/button"
import {
    Card,
    CardContent,
    CardDescription,
    CardFooter,
    CardHeader,
    CardTitle,
} from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"

type Plan = {
    id: string;
    name: string;
    price: number;
    originalPrice: number;
    duration: string;
    popular: boolean;
    description: string[];
};
export default function IELTSPremiumPage() {
    const [selectedPlan, setSelectedPlan] = useState<Plan | null>(null)
    const [loading, setLoading] = useState(false)
    const [plans, setPlans] = useState<Plan[]>([])

    useEffect(() => {
        const fetchPlans = async () => {
            try {
                const response = await axios.get("http://localhost:8080/api/courses")
                const fetched = response.data.map((c) => ({
                    id: c.id,
                    name: c.name,
                    price: c.price,
                    originalPrice: c.originalPrice,
                    description: c.description.split("|"),
                    duration: `${c.duration} tháng`,
                }))
                setPlans(fetched)
            } catch (error) {
                console.error("Lỗi tải gói học:", error)
            }
        }
        fetchPlans()
    }, [])

    const handlePay = async () => {
        if (!selectedPlan) return
        setLoading(true)
        try {
            const response = await axios.post("http://localhost:8080/api/momo/create", {
                courseId: selectedPlan.id,
                courseName: selectedPlan.name,
                amount: selectedPlan.price,
            })
            const payUrl = response.data.payUrl
            if (payUrl) {
                window.location.href = payUrl
            }
        } catch (error) {
            console.error("Lỗi tạo thanh toán:", error)
            alert("Tạo thanh toán thất bại.")
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="min-h-screen bg-gradient-to-br from-orange-50 via-white to-green-50">
            <header className="container mx-auto px-4 py-8 text-center">
                <Badge className="mb-4 bg-orange-100 text-orange-800 hover:bg-orange-200">
                    🚀 Ra mắt AI Chấm Bài IELTS
                </Badge>
                <h1 className="text-4xl md:text-6xl font-bold bg-gradient-to-r from-orange-600 to-green-600 bg-clip-text text-transparent mb-4">
                    IELTS Premium AI
                </h1>
                <p className="text-xl text-gray-600 max-w-3xl mx-auto mb-8">
                    Nâng cao band điểm IELTS với công nghệ AI tiên tiến. Chấm bài Speaking & Writing chính xác như giám khảo thật, phản hồi tức thì 24/7.
                </p>
            </header>

            <section className="container mx-auto px-4 py-16">
                <h2 className="text-3xl font-bold text-center mb-12">Chọn Gói Học Phù Hợp</h2>
                <div className="grid md:grid-cols-3 gap-8 max-w-6xl mx-auto">
                    {plans.map((plan) => (
                        <Card
                            key={plan.id}
                            className={`relative cursor-pointer 
                            ${selectedPlan?.id === plan.id ? "ring-2 ring-blue-500" : ""}`}
                            onClick={() => setSelectedPlan(plan)}>

                            <CardHeader className="text-center pb-4">
                                <CardTitle className="text-xl font-bold">{plan.name}</CardTitle>
                                <CardDescription className="text-sm text-gray-500">{plan.duration}</CardDescription>
                                <div className="mt-4">
                                    <span className="text-3xl font-bold text-orange-600">{plan.price.toLocaleString()}₫</span>
                                    <div className="text-sm text-gray-400 line-through">{plan.originalPrice.toLocaleString()}₫</div>
                                </div>
                            </CardHeader>
                            <CardContent className="space-y-3">
                                <>
                                    {plan.description.map((feature, i) => (
                                        <div key={i} className="flex items-start gap-3">
                                            <Check className="h-5 w-5 text-green-500 mt-0.5 flex-shrink-0" />
                                            <span className="text-sm">{feature}</span>
                                        </div>
                                    ))}
                                </>
                            </CardContent>
                        </Card>
                    ))}
                </div>

                <div className="text-center mt-12">
                    <Button
                        size="lg"
                        className="bg-pink-600 text-white"
                        onClick={handlePay}
                        disabled={!selectedPlan || loading}
                    >
                        {loading ? "Đang xử lý..." : "Thanh toán với MoMo"}
                    </Button>
                </div>
            </section>
        </div>
    )
}
