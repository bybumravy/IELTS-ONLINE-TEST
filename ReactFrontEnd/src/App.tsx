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
import IeltsResult from "@/pages/IeltsResult";
import MomoPayment from "@/pages/Payment/MomoPayment.tsx";

export default function App() {
    return (
        <AuthProvider>
            <Router>
                <Routes>
                    <Route path="/" element={<MainLayout><HomePage /></MainLayout> as React.ReactNode}/>
                    {/*<Route path="/" element={<MainLayout><IeltsResult /></MainLayout> as React.ReactNode}/>*/}
                    <Route path="/login" element={<Login /> as React.ReactNode}/>
                    <Route path="/student/listAllTips" element={<MainLayout><TipPage /></MainLayout> as React.ReactNode}/>
                    <Route path="/register" element={<RegisterPage /> as React.ReactNode}/>
                    <Route path="/reading" element={<MainLayout><IELTSTest /></MainLayout> as React.ReactNode}/>
                    <Route path="/DoTest/writing" element={<IELTSWritingPractice /> as React.ReactNode} />
                    <Route path="/:skill/:id" element={<MainLayout><TipDetail /></MainLayout> as React.ReactNode} />
                    <Route path="/tips/:skill" element={<MainLayout><TipPage/></MainLayout> as React.ReactNode} />
                    <Route path="/result" element={<MainLayout><IeltsResult/></MainLayout> as React.ReactNode} />
                    <Route path="/api/payment/create" element={<MainLayout><MomoPayment/></MainLayout> as React.ReactNode} />
                    <Route path="/test" element={<MainLayout><ListTestPage /></MainLayout> as React.ReactNode} />
                    <Route path="/test/:skill" element={<MainLayout><ListTestPage /></MainLayout> as React.ReactNode} />
                </Routes>
            </Router>
        </AuthProvider>
    )
}