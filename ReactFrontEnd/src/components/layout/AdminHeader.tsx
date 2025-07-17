import { Link, useNavigate } from "react-router-dom";
import { Shield } from "lucide-react";
import { useAuth } from "@/contexts/AuthContext";

export function AdminHeader() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  if (!user || user.role !== "admin") {
    navigate("/");
    return null; 
  }

  const handleLogout = async () => {
    await logout();
    navigate("/login-admin");
  };

  return (
    <header className="sticky top-0 z-50 w-full border-b border-emerald-200 bg-white/90 backdrop-blur-md shadow-md">
      <div className="max-w-7xl mx-auto px-4 flex items-center justify-between h-16">
        <Link to="/admin-page" className="flex items-center space-x-2">
          <div className="w-9 h-9 bg-emerald-600 rounded-lg flex items-center justify-center">
            <Shield className="w-6 h-6 text-white" />
          </div>
          <span className="text-xl font-bold text-emerald-700">ADMIN PANEL</span>
        </Link>
        <nav className="flex items-center space-x-4">
          <button onClick={handleLogout} className="text-emerald-600 hover:text-emerald-800 font-semibold">Logout</button>
        </nav>
      </div>
    </header>
  );
}
