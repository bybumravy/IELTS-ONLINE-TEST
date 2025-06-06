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



export default function App() {
    return (
        <AuthProvider>
            <Router>
                <Routes>
                    <Route path="/" element={<MainLayout><HomePage /></MainLayout>}/>
                    <Route path="/login" element={<Login />}/>
                    <Route path="/student/listAllTips" element={<MainLayout><TipPage /></MainLayout>}/>
                    <Route path="/register" element={<RegisterPage />}/>
                    <Route path="/reading" element={<MainLayout><IELTSTest /></MainLayout>}/>
                    <Route path="/DoTest/writing" element={<IELTSWritingPractice />} />
                    <Route path="/:skill/:id" element={<TipDetail /> as React.ReactNode} />
                    <Route path="/tips/:skill" element={<TipPage/> as React.ReactNode} />
                </Routes>
            </Router>
        </AuthProvider>
    )
}
