import { BrowserRouter as Router, Routes, Route } from "react-router-dom"
import { HomePage } from "@/pages/HomePage"
import IELTSWritingPractice from "@/pages/DoTest/WritingTest"
import { AuthProvider } from "@/contexts/AuthContext"
import Login from "@/components/sections/Login"
import IELTSTest from "./pages/IELTSTest"
import { MainLayout } from "@/components/layout/MainLayout"
import TipPage from "@/pages/TipPage";
import RegisterPage from "@/components/sections/Register";
import TipDetail from "@/pages/TipDetail";
import ListTestPage from "@/pages/ListTestPage";
import StaffLogin from "./components/sections/StaffLogin"
import { StaffPage } from "./pages/StaffPage"
import AddTest from "./pages/AddTest"
import { StaffLayout } from "./components/layout/StaffLayout"

export default function App() {
    return (
        <AuthProvider>
            <Router>
                <Routes>
                    <Route path="/" element={<MainLayout><HomePage /></MainLayout>}/>
                    <Route path="/login" element={<Login />}/>
                    <Route path="/staff-login" element={<StaffLogin />}/>
                    <Route path="/staff-page" element={<StaffLayout><StaffPage /></StaffLayout>}/>
                    <Route path="/add-test" element={<StaffLayout><AddTest /></StaffLayout>}/>
                    <Route path="/student/listAllTips" element={<MainLayout><TipPage /></MainLayout>}/>
                    <Route path="/register" element={<RegisterPage />}/>
                    <Route path="/reading" element={<MainLayout><IELTSTest /></MainLayout>}/>
                    <Route path="/DoTest/writing" element={<IELTSWritingPractice />} />
                    <Route path="/:skill/:id" element={<MainLayout><TipDetail /></MainLayout> as React.ReactNode} />
                    <Route path="/tips/:skill" element={<MainLayout><TipPage/></MainLayout> as React.ReactNode} />
                    <Route path="/test" element={<MainLayout><ListTestPage /></MainLayout>} />
                    <Route path="/test/:skill" element={<MainLayout><ListTestPage /></MainLayout>} />
                </Routes>
            </Router>
        </AuthProvider>
    )
}