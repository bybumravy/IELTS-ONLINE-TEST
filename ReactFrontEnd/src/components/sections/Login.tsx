import  { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "@/contexts/AuthContext";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Label } from "@/components/ui/label";

const LoginPage = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();
  const { login } = useAuth();

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    try {
      await login(email, password);
      navigate("/");
    } catch (error) {
      alert("Login failed");
      console.error(error);
    }
  };

  const handleGoogleLogin = () => {
    window.location.href = "http://localhost:8080/oauth2/authorization/google";
  };

  return (
      <div className="min-h-screen flex items-center justify-center bg-gray-100">
        <Card className="w-full max-w-md shadow-xl border rounded-2xl">
          <CardHeader>
            <CardTitle className="text-center text-2xl font-bold">Login to Your Account</CardTitle>
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
              <Button type="submit" className="w-full">
                Sign In
              </Button>
              <Button type="button" variant="outline" className="w-full" onClick={handleGoogleLogin}>
                Continue with Google
              </Button>
            </form>

            <p className="text-center text-sm text-muted-foreground mt-4">
              Don't have an account? <strong className="text-primary">Sign up</strong>
            </p>
          </CardContent>
        </Card>
      </div>
  );
};

export default LoginPage;