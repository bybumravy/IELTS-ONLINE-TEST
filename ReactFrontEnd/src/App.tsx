import { BrowserRouter as Router, Routes, Route } from "react-router-dom"
import { HomePage } from "@/pages/HomePage"
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
import VnPayResultPage from "@/pages/Payment/VnPayResultPage.tsx";
import PremiumPage from "@/pages/Payment/PremiumPage.tsx";
// import Contact from "@/pages/Contact";
// import HelpCenter from "@/pages/HelpCenter";
// import IeltsResult from "@/pages/IeltsResult";
import FullTest from "@/pages/DoTest/FullTest";
import ListeningResult from "@/pages/Result/ListeningResult";
import LoginAdmin from "@/components/sections/admin/LoginAdmin";
import AdminPage from "@/pages/AdminPage";
export default function App() {
    return (
        <AuthProvider>
            <Router>
                <Routes>
                    {/*HomePage*/}
                    <Route path="/" element={<MainLayout><HomePage /></MainLayout>}/>

                    {/*Auth*/}
                    <Route path="/login" element={<Login />}/>
                    {/*<Route path="/staff-login" element={<StaffLogin />}/>*/}
                    <Route path="/verify-email" element={<VerifyEmail/>} />
                    <Route path="/register" element={<RegisterPage />}/>

                    {/*DoTest*/}
                    <Route path="/test/listening/:testId" element={<ListeningTest />} />
                    <Route path="/test/reading/:testId" element={<ReadingTest />} />
                    <Route path="/test/writing/:testId" element={<WritingTest />} />
                    <Route path="/test/full/:testId" element={<FullTest />} />
                    <Route path="/test/speaking/:testId" element={<VoiceRecorder />} />
                    <Route path="/checkMic/:testId" element={<SpeakingTest/>} />

                    {/*Result*/}
                    <Route path="/writing-result/:resultId" element={<MainLayout><WritingResult /></MainLayout>} />
                    <Route path="/listening-result/:resultId" element={<MainLayout><ListeningResult/></MainLayout>} />


                    {/*ListTests&ListTips*/}
                    <Route path="/test" element={<MainLayout><ListTestPage/></MainLayout>} />
                    <Route path="/test/:skill" element={<MainLayout><ListTestPage/></MainLayout>} />
                    <Route path="/student/listAllTips" element={<MainLayout><TipPage /></MainLayout>}/>
                    <Route path="/:skill/:id" element={<MainLayout><TipDetail/></MainLayout>} />
                    <Route path="/tips/:skill" element={<MainLayout><TipPage/></MainLayout>} />

                    {/*UserMenu*/}
                    <Route path="/premium" element={<MainLayout><PremiumPage /></MainLayout>} />
                    <Route path="/vnpay-result" element={<MainLayout><VnPayResultPage /></MainLayout>} />
                    {/*<Route path="/test-history" element={<MainLayout><HistoryPage /></MainLayout>}/>*/}
                    {/*Footer*/}
                    {/*<Route path="/contact" element={<MainLayout><Contact /></MainLayout>}/>*/}
                    {/*<Route path="/help" element={<MainLayout><HelpCenter /></MainLayout>}/>*/}

                    {/*Staff*/}
                    {/*<Route path="/add-test" element={<StaffLayout><AddTest /></StaffLayout>}/>*/}
                    {/*<Route path="/staff-page" element={<StaffLayout><StaffPage /></StaffLayout>}/>*/}

                    {/*Premium*/}
                    {/*<Route path="/api/payment/create" element={<MainLayout><PaymentPage/></MainLayout>} />*/}
                    {/*<Route path="/premium" element={<MainLayout><PremiumPage /></MainLayout>} />*/}


                    <Route path="/loginAdmin" element={<LoginAdmin />} />
                    <Route path="/adminPage" element={
                            <AdminPage />
                    } />
                </Routes>
            </Router>
        </AuthProvider>
    )
}
