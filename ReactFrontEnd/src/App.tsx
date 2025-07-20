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

import FulllTest from "@/pages/DoTest/FullTest"

//Vocab
import Vocabulary from "./pages/practice/Vocabulary"
import ForgetPassword from "@/components/sections/ForgetPassword";
import ResetPassword from "@/components/sections/ResetPassword";
import VocabularyGame from "@/pages/student/VocabularyGame.tsx";

// Result
import WritingResult from "@/pages/Result/WritingResult"
import ListeningResult from "./pages/Result/ListeningResult"
import HistoryPage from "@/pages/HistoryPage"
import SpeakingResult from "@/pages/Result/SpeakingResult.tsx";
// Admin

import LoginAdmin from "@/components/sections/LoginAdmin"
import AdminPage from "./pages/AdminPage"

// Staff
import StaffLogin from "@/components/sections/StaffLogin"
import { StaffPage } from "@/pages/StaffPage"
import AddTest from "@/pages/AddTest"
import AcceptTestPage from "@/pages/AcceptTestPage"
import RequestTestDetailPage from "@/pages/RequestTestDetailPage"
import VocabularyList from "./pages/student/VocabularyList"
import ReviewReport from "@/pages/ReviewReport.tsx";
import UserManagementPage from "./pages/UserManagementPage";
import ManageStudentsPage from "./pages/ManageStudentsPage";
import ManageTeachersPage from "./pages/ManageTeachersPage";

// Protected Layouts
import SoftProtectedLayout from "@/components/sections/SoftProtectedLayout"
import ProtectedLayout from "@/components/sections/ProtectedLayout"
import ProtectedLayoutRole from "@/components/sections/ProtectedLayoutRole"
import ReadingResult from "@/pages/Result/ReadingResult.tsx";
import TransactionPage from "./pages/TransactionPage"
import MatchingGamePage from '@/pages/student/MatchingGamePage';

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
                        <Route path="/student/vocabulary-game" element={<MainLayout><VocabularyGame /></MainLayout>} />


                        <Route path="/practice/vocabulary" element={<SoftProtectedLayout allowRoles={["student"]}><MainLayout><VocabularyList /></MainLayout></SoftProtectedLayout>} />

                        <Route path="/student/vocabulary-matching-game" element={<MainLayout><MatchingGamePage /></MainLayout>}/>

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
                            <ProtectedLayout allowRoles={["student"]} requirePremium={true}>
                                <WritingTest />
                            </ProtectedLayout>
                        } />
                        <Route path="/test/speaking/:testId" element={
                            <ProtectedLayout allowRoles={["student"]} requirePremium={true}>
                                <SpeakingTest/>
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
                        <Route path="/reading-result/:resultId" element={
                            <ProtectedLayout allowRoles={["student"]}>
                                <MainLayout><ReadingResult /></MainLayout>
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
                        <Route path="/login-admin" element={<LoginAdmin />} />
                        <Route path="/admin-page" element={
                            <ProtectedLayoutRole allowRoles={["admin"]}>
                                <AdminPage />
                            </ProtectedLayoutRole>
                        } />

                        {/* ========== Staff Routes ========== */}
                        <Route path="/staff-login" element={<StaffLogin />} />
                        <Route path="/staff-page" element={<ProtectedLayoutRole allowRoles={["teacher", "manager"]}><StaffLayout><StaffPage /></StaffLayout></ProtectedLayoutRole>} />
                        <Route path="/add-test" element={<ProtectedLayout allowRoles={["teacher"]}><StaffLayout><AddTest /></StaffLayout></ProtectedLayout>} />
                        <Route path="/accept-tests" element={<ProtectedLayout allowRoles={["manager"]}><StaffLayout><AcceptTestPage /></StaffLayout></ProtectedLayout>} />
                        <Route path="/request-test-detail" element={<ProtectedLayout allowRoles={["manager"]}><StaffLayout><RequestTestDetailPage /></StaffLayout></ProtectedLayout>} />
                        <Route path="/transactions" element={<ProtectedLayout allowRoles={["manager"]}><StaffLayout><TransactionPage/></StaffLayout></ProtectedLayout>} />
                        <Route path="/add-vocabulary" element={<ProtectedLayout allowRoles={["teacher"]}><StaffLayout><Vocabulary /></StaffLayout></ProtectedLayout>} />
                        <Route path="/review-report" element={<ProtectedLayout allowRoles={["teacher"]}><StaffLayout><ReviewReport /></StaffLayout></ProtectedLayout>} />
                        <Route path="/user-management" element={<ProtectedLayoutRole allowRoles={["manager"]}><StaffLayout><UserManagementPage /></StaffLayout></ProtectedLayoutRole>} />
                        <Route path="/manage-students" element={<ProtectedLayoutRole allowRoles={["manager"]}><StaffLayout><ManageStudentsPage /></StaffLayout></ProtectedLayoutRole>} />
                        <Route path="/manage-teachers" element={<ProtectedLayoutRole allowRoles={["manager"]}><StaffLayout><ManageTeachersPage /></StaffLayout></ProtectedLayoutRole>} />
                        {/* ========== Error Pages ========== */}
                        <Route path="/error" element={<ErrorPage />} />

                        <Route path="/speakingResult" element={<SpeakingResult />} />

                        {/* ========== 404 Not Found (Catch-all) ========== */}
                        <Route path="*" element={<NotFoundPage />} />

                    </Routes>
                </Router>
            </AuthProvider>
        </ErrorBoundary>
    )
}
