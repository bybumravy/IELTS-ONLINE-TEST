import { BrowserRouter as Router, Routes, Route } from "react-router-dom"
import { HomePage } from "@/pages/HomePage"
import IELTSWritingPractice from "@/pages/DoTest/WritingTest"
import { AuthProvider } from "@/contexts/AuthContext"
import Login from "@/components/sections/Login"
import { MainLayout } from "@/components/layout/MainLayout"
import TipPage from "@/pages/TipPage";
import RegisterPage from "@/components/sections/Register";
import TipDetail from "@/pages/TipDetail";
import ListTestPage from "@/pages/ListTestPage";
import WritingResult from "@/pages/Result/WritingResult";
import ListeningTest from "@/pages/DoTest/ListeningTest";
import ReadingTest from "@/pages/DoTest/ReadingTest";
import WritingTest from "@/pages/DoTest/WritingTest";
import VoiceRecorder from "@/pages/DoTest/checkMic";
import SpeakingTest from "@/pages/DoTest/SpeakingTest";
import VerifyEmail from "@/components/sections/VerifyEmail";
import Component from "@/pages/DoTest/Component";
import FulllTest from "@/pages/DoTest/FullTest";
import LoginAdmin from "@/components/sections/LoginAdmin";
import Adminpage from "@/pages/Adminpage";
import SoftProtectedLayout from "@/components/sections/SoftProtectedLayout";
import ProtectedLayout from "@/components/sections/ProtectedLayout";
import ProtectedLayoutRole from "@/components/sections/ProtectedLayoutRole";
import ForgetPassword from "@/components/sections/ForgetPassword";
import ResetPassword from "@/components/sections/ResetPassword";
export default function App() {
    return (
        <AuthProvider>
            <Router>
                <Routes>

                    {/* Public - Không bắt login - Nếu login thì phải role student */}
                    <Route path="/" element={
                        <SoftProtectedLayout allowRoles={["student"]}>
                            <MainLayout><HomePage /></MainLayout>
                        </SoftProtectedLayout>
                    } />

                    <Route path="/student/listAllTips" element={
                        <SoftProtectedLayout allowRoles={["student"]}>
                            <MainLayout><TipPage /></MainLayout>
                        </SoftProtectedLayout>
                    } />

                    <Route path="/:skill/:id" element={
                        <SoftProtectedLayout allowRoles={["student"]}>
                            <MainLayout><TipDetail /></MainLayout>
                        </SoftProtectedLayout>
                    } />

                    <Route path="/tips/:skill" element={
                        <SoftProtectedLayout allowRoles={["student"]}>
                            <MainLayout><TipPage /></MainLayout>
                        </SoftProtectedLayout>
                    } />

                    <Route path="/test" element={
                        <SoftProtectedLayout allowRoles={["student"]}>
                            <MainLayout><ListTestPage /></MainLayout>
                        </SoftProtectedLayout>
                    } />

                    <Route path="/test/:skill" element={
                        <SoftProtectedLayout allowRoles={["student"]}>
                            <MainLayout><ListTestPage /></MainLayout>
                        </SoftProtectedLayout>
                    } />

                    <Route path="/result" element={
                        <SoftProtectedLayout allowRoles={["student"]}>
                            <MainLayout><Component /></MainLayout>
                        </SoftProtectedLayout>
                    } />

                    {/* Trang Admin - bắt login role admin */}
                    <Route path="/adminpage" element={
                        <ProtectedLayoutRole allowRoles={["admin"]}>
                            <Adminpage />
                        </ProtectedLayoutRole>
                    } />

                    {/* Trang kỹ năng - bắt login role student */}
                    <Route path="/test/listening/:testId" element={
                        <ProtectedLayout allowRoles={["student"]}>
                            <ListeningTest />
                        </ProtectedLayout>
                    } />

                    <Route path="/test/reading/:testId" element={
                        <ProtectedLayout allowRoles={["student"]}>
                            <ReadingTest />
                        </ProtectedLayout>
                    } />

                    <Route path="/test/writing/:testId" element={
                        <ProtectedLayout allowRoles={["student"]}>
                            <WritingTest />
                        </ProtectedLayout>
                    } />

                    <Route path="/test/full/:testId" element={
                        <ProtectedLayout allowRoles={["student"]}>
                            <FulllTest />
                        </ProtectedLayout>
                    } />

                    <Route path="/test/speaking/:testId" element={
                        <ProtectedLayout allowRoles={["student"]}>
                            <VoiceRecorder />
                        </ProtectedLayout>
                    } />

                    <Route path="/checkMic/:testId" element={
                        <ProtectedLayout allowRoles={["student"]}>
                            <SpeakingTest />
                        </ProtectedLayout>
                    } />

                    <Route path="/writing-result/:resultId" element={
                        <ProtectedLayout allowRoles={["student"]}>
                            <MainLayout><WritingResult /></MainLayout>
                        </ProtectedLayout>
                    } />

                    {/* Các route public - không cần login */}
                    <Route path="/login" element={<Login />} />
                    <Route path="/loginadmin" element={<LoginAdmin />} />
                    <Route path="/register" element={<RegisterPage />} />
                    <Route path="/verify-email" element={<VerifyEmail />} />
                    <Route path="/forgot-password" element={<ForgetPassword />} />
                    <Route path="/reset-password" element={<ResetPassword />} />
                </Routes>

            </Router>
        </AuthProvider>
    )
}