import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "@/contexts/AuthContext";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Label } from "@/components/ui/label";

const RegisterPage = () => {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const navigate = useNavigate();
    const { register } = useAuth(); // đảm bảo bạn có phương thức `register` trong AuthContext

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (password !== confirmPassword) {
            alert("Passwords do not match");
            return;
        }

        try {
            await register(email, password); // sử dụng phương thức register từ context
            navigate("/");
        } catch (error) {
            alert("Registration failed");
            console.error(error);
        }
    };

    const handleGoogleSignup = () => {
        window.location.href = "http://localhost:8080/oauth2/authorization/google";
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100">
            <Card className="w-full max-w-md shadow-xl border rounded-2xl">
                <CardHeader>
                    <CardTitle className="text-center text-2xl font-bold">Create an Account</CardTitle>
                </CardHeader>
                <CardContent>
                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div>
                            <Label htmlFor="email">Email</Label>
                            <Input
                                id="email"
                                type="email"
                                placeholder="you@example.com"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                            />
                        </div>
                        <div>
                            <Label htmlFor="password">Password</Label>
                            <Input
                                id="password"
                                type="password"
                                placeholder="••••••••"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                            />
                        </div>
                        <div>
                            <Label htmlFor="confirm-password">Confirm Password</Label>
                            <Input
                                id="confirm-password"
                                type="password"
                                placeholder="••••••••"
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                required
                            />
                        </div>
                        <Button type="submit" className="w-full">
                            Sign Up
                        </Button>
                        <Button type="button" variant="outline" className="w-full" onClick={handleGoogleSignup}>
                            Continue with Google
                        </Button>
                    </form>

                    <p className="text-center text-sm text-muted-foreground mt-4">
                        Already have an account? <strong className="text-primary">Sign in</strong>
                    </p>
                </CardContent>
            </Card>
        </div>
    );
};

export default RegisterPage;