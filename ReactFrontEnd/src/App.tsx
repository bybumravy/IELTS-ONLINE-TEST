import { BrowserRouter as Router, Routes, Route } from "react-router-dom"
import { AuthProvider } from "@/contexts/AuthContext"
import ErrorBoundary from "@/components/ErrorBoundary"

// Layouts
import { MainLayout } from "@/components/layout/MainLayout"
import { StaffLayout } from "@/components/layout/Staff/StaffLayout"

//Payment
import VnPayResultPage from "./pages/Payment/VNPayResultPage"
import PremiumPage from "@/pages/Payment/PremiumPage.tsx";

// Pages - Public
import { HomePage } from "@/pages/HomePage"
import Login from "@/components/sections/Login"
import RegisterPage from "@/components/sections/Register"
import VerifyEmail from "@/components/sections/VerifyEmail"
import Contact from "@/pages/Contact"
import HelpCenter from "@/pages/HelpCenter"
import ErrorPage from "@/pages/ErrorPage"
import NotFoundPage from "@/pages/NotFoundPage"

// Tips
import TipPage from "@/pages/TipPage"
import TipDetail from "@/pages/TipDetail"

// Test Pages
import ListTestPage from "@/pages/ListTestPage"
import ListeningTest from "@/pages/DoTest/ListeningTest"
import ReadingTest from "@/pages/DoTest/ReadingTest"
import WritingTest from "@/pages/DoTest/WritingTest"
import SpeakingTest from "@/pages/DoTest/SpeakingTest"
import VoiceRecorder from "@/pages/DoTest/checkMic"
import FulllTest from "@/pages/DoTest/FullTest"

//Vocab
import Vocabulary from "./pages/practice/Vocabulary"
import ForgetPassword from "@/components/sections/ForgetPassword";
import ResetPassword from "@/components/sections/ResetPassword";

// Result
import WritingResult from "@/pages/Result/WritingResult"
import ListeningResult from "./pages/Result/ListeningResult"
import HistoryPage from "@/pages/HistoryPage"

// Admin

import LoginAdmin from "@/components/sections/LoginAdmin"
import Adminpage from "@/pages/Adminpage"

// Staff
import StaffLogin from "@/components/sections/StaffLogin"
import { StaffPage } from "@/pages/StaffPage"
import AddTest from "@/pages/AddTest"
import AcceptTestPage from "@/pages/AcceptTestPage"
import RequestTestDetailPage from "@/pages/RequestTestDetailPage"
import VocabularyList from "./pages/student/VocabularyList"

// Protected Layouts
import SoftProtectedLayout from "@/components/sections/SoftProtectedLayout"
import ProtectedLayout from "@/components/sections/ProtectedLayout"
import ProtectedLayoutRole from "@/components/sections/ProtectedLayoutRole"


export default function App() {
    return (
        <ErrorBoundary>
            <AuthProvider>
                <Router>
                    <Routes>

                        {/* ========== Public Routes (No login required) ========== */}
                        <Route path="/" element={
                            <SoftProtectedLayout allowRoles={["student"]}>
                                <MainLayout><HomePage /></MainLayout>
                            </SoftProtectedLayout>
                        } />
                        <Route path="/login" element={<Login />} />
                        <Route path="/register" element={<RegisterPage />} />
                        <Route path="/verify-email" element={<VerifyEmail />} />
                        <Route path="/forgot-password" element={<ForgetPassword />} />
                        <Route path="/reset-password" element={<ResetPassword />} />
                        <Route path="/contact" element={<MainLayout><Contact /></MainLayout>} />
                        <Route path="/help" element={<MainLayout><HelpCenter /></MainLayout>} />

                        {/* ========== Soft Protected (Login optional, show more for students) ========== */}
                        <Route path="/student/listAllTips" element={
                            <SoftProtectedLayout allowRoles={["student"]}>
                                <MainLayout><TipPage /></MainLayout>
                            </SoftProtectedLayout>
                        } />
                        <Route path="/tips/:skill" element={
                            <SoftProtectedLayout allowRoles={["student"]}>
                                <MainLayout><TipPage /></MainLayout>
                            </SoftProtectedLayout>
                        } />
                        <Route path="/:skill/:id" element={
                            <SoftProtectedLayout allowRoles={["student"]}>
                                <MainLayout><TipDetail /></MainLayout>
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


                        <Route path="/practice/vocabulary" element={<SoftProtectedLayout allowRoles={["student"]}><MainLayout><VocabularyList /></MainLayout></SoftProtectedLayout>} />

                        {/* ========== Student Test Routes (Login required) ========== */}
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
                        <Route path="/test/full/:testId" element={
                            <ProtectedLayout allowRoles={["student"]}>
                                <FulllTest />
                            </ProtectedLayout>
                        } />
                        <Route path="/writing-result/:resultId" element={
                            <ProtectedLayout allowRoles={["student"]}>
                                <MainLayout><WritingResult /></MainLayout>
                            </ProtectedLayout>
                        } />
                        <Route path="/listening-result/:resultId" element={
                            <ProtectedLayout allowRoles={["student"]}>
                                <MainLayout><ListeningResult /></MainLayout>
                            </ProtectedLayout>
                        } />
                        <Route path="/test-history" element={
                            <ProtectedLayout allowRoles={["student"]}>
                                <MainLayout><HistoryPage /></MainLayout>
                            </ProtectedLayout>
                        } />

                        <Route path="/premium" element={<MainLayout><PremiumPage /></MainLayout>} />
                        <Route path="/vnpay-result" element={<MainLayout><VnPayResultPage /></MainLayout>} />
                        {/* ========== Admin Routes ========== */}
                        <Route path="/loginadmin" element={<LoginAdmin />} />
                        <Route path="/adminpage" element={
                            <ProtectedLayoutRole allowRoles={["admin"]}>
                                <Adminpage />
                            </ProtectedLayoutRole>
                        } />

                        {/* ========== Staff Routes ========== */}
                        <Route path="/staff-login" element={<StaffLogin />} />
                        <Route path="/staff-page" element={<ProtectedLayoutRole allowRoles={["teacher", "manager"]}><StaffLayout><StaffPage /></StaffLayout></ProtectedLayoutRole>} />
                        <Route path="/add-test" element={<ProtectedLayout allowRoles={["teacher"]}><StaffLayout><AddTest /></StaffLayout></ProtectedLayout>} />
                        <Route path="/accept-tests" element={<ProtectedLayout allowRoles={["manager"]}><StaffLayout><AcceptTestPage /></StaffLayout></ProtectedLayout>} />
                        <Route path="/request-test-detail" element={<ProtectedLayout allowRoles={["manager"]}><StaffLayout><RequestTestDetailPage /></StaffLayout></ProtectedLayout>} />
                        <Route path="/add-vocabulary" element={<ProtectedLayout allowRoles={["teacher"]}><StaffLayout><Vocabulary /></StaffLayout></ProtectedLayout>} />

                        {/* ========== Error Pages ========== */}
                        <Route path="/error" element={<ErrorPage />} />

                        {/* ========== 404 Not Found (Catch-all) ========== */}
                        <Route path="*" element={<NotFoundPage />} />

                    </Routes>
                </Router>
            </AuthProvider>
        </ErrorBoundary>
    )
}
