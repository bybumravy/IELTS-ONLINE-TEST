import  { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {useAuth} from "@/contexts/AuthContext";


const LoginPage = ({ setUser }) => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();
    const { login } = useAuth();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            // await login(email, password);
            navigate("/");
        } catch (error) {
            alert('Login failed');
            console.error(error);
        }
    };

    const handleGoogleLogin = () => {
        window.location.href = "http://localhost:8080/oauth2/authorization/google";
    };

    return (
        <div className="login-page">
            <div className="container">
                <div className="logo">Logo</div>
                <h2>LOGIN YOUR ACCOUNT</h2>
                <form onSubmit={handleSubmit}>
                    <label htmlFor="email">Email:</label>
                    <input
                        type="text"
                        id="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />

                    <label htmlFor="password">Password:</label>
                    <input
                        type="password"
                        id="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />

                    <button type="submit" className="google-btn">Sign in</button>
                    <button type="submit" className="google-btn">Sign in</button>
                </form>

                <button className="signup-btn" onClick={handleGoogleLogin}>
                    Continue with Google
                </button>

                <p className="text-center">
                    If you already have an account, please <strong>sign in</strong>.
                </p>
            </div>
        </div>
    );
};

export default LoginPage;