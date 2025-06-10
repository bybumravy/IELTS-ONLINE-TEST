import { StaffNavigationMenu } from "./StaffNavigationMenu";
import { useAuth } from "@/contexts/AuthContext";


export function StaffHeader() {
  const { user, logout } = useAuth(); 
  console.log("StaffHeader - User:", user);
  console.log("StaffHeader - Role:", user?.role);
  
  if (!user || (user.role !== "teacher" && user.role !== "manager")) {
    console.log("StaffHeader - Returning null due to role check");
    return null;
  }

  return (
    <header className="sticky top-0 z-50 w-full border-b border-slate-200 bg-white/75 backdrop-blur-sm">
      <StaffNavigationMenu role={user.role} onLogout={logout} />
    </header>
  );
}
