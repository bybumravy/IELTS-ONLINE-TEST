import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { BookOpen, Mail } from "lucide-react";

const ForgetPasswordPage = () => {
    const [email, setEmail] = useState("");
    const [emailError, setEmailError] = useState("");
    const [message, setMessage] = useState("");

    const navigate = useNavigate();

    const getEmailErrorMessage = (value: string): string => {
        if (!value) return "Email is required";
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;
        if (!emailRegex.test(value)) {
            return "Please enter a valid email address (e.g. you@example.com)";
        }
        return "";
    };

    const handleEmailChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        setEmail(value);
        setEmailError(getEmailErrorMessage(value));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        const error = getEmailErrorMessage(email);
        setEmailError(error);
        if (error) return;

        try {
            const res = await fetch("http://localhost:8080/api/forgotpassword", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email }),
            });

            if (!res.ok) {
                const text = await res.text(); // Đọc lỗi text nếu có
                throw new Error(text || "Có lỗi xảy ra");
            }

            // ✅ THÀNH CÔNG → Thông báo cứng
            alert("Hãy kiểm tra Gmail để reset mật khẩu.");
            setEmail(""); // Clear input
        } catch (err: any) {
            setEmailError(err.message || "Đã xảy ra lỗi");
        }
    };


    return (
        <div className="min-h-screen flex flex-col items-center justify-center bg-gray-50 px-4">
            <div className="flex items-center space-x-2 mb-8">
                <div className="w-10 h-10 bg-emerald-600 rounded-lg flex items-center justify-center">
                    <BookOpen className="w-6 h-6 text-white" />
                </div>
                <span className="text-2xl font-bold text-gray-900">LANGUAGES</span>
            </div>

            <Card className="w-full max-w-md">
                <CardHeader className="space-y-1">
                    <CardTitle className="text-2xl font-bold text-center">Forgot your password?</CardTitle>
                    <CardDescription className="text-center">
                        Enter your email address and we’ll send you a reset link
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <form onSubmit={handleSubmit} className="space-y-4">
                        {/* Email Field */}
                        <div className="space-y-2">
                            <Label htmlFor="email">Email</Label>
                            <div className="relative">
                                <Mail className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                                <Input
                                    id="email"
                                    type="email"
                                    placeholder="you@example.com"
                                    value={email}
                                    onChange={handleEmailChange}
                                    className={`pl-9 ${emailError ? "border-red-500" : ""}`}
                                />
                            </div>
                            {emailError && <p className="text-red-500 text-sm">{emailError}</p>}
                            {message && <p className="text-green-600 text-sm">{message}</p>}
                        </div>

                        <Button type="submit" className="w-full bg-emerald-600 hover:bg-emerald-700">
                            Send reset link
                        </Button>

                        <div className="mt-4 text-center text-sm">
                            <Link to="/login" className="text-emerald-600 hover:text-emerald-700 font-semibold">
                                Back to login
                            </Link>
                        </div>
                    </form>
                </CardContent>
            </Card>
        </div>
    );
};

export default ForgetPasswordPage;
