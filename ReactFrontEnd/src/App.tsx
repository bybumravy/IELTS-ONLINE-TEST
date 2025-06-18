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
export default function App() {
    return (
        <AuthProvider>
            <Router>
                <Routes>
                    <Route path="/" element={<MainLayout><HomePage /></MainLayout>}/>
                    <Route path="/login" element={<Login />}/>
                    <Route path="/student/listAllTips" element={<MainLayout><TipPage /></MainLayout>}/>
                    <Route path="/register" element={<RegisterPage />}/>
                    <Route path="/test/listening/:testId" element={<ListeningTest />} />
                    <Route path="/test/reading/:testId" element={<ReadingTest />} />
                    <Route path="/test/writing/:testId" element={<WritingTest />} />
                    {/*<Route path="/test/speaking/:testId" element={<SpeakingTest />} />*/}
                    <Route path="/writing-result/:resultId" element={<MainLayout><WritingResult /></MainLayout>} />

                    <Route path="/:skill/:id" element={<MainLayout><TipDetail /></MainLayout>} />
                    <Route path="/tips/:skill" element={<MainLayout><TipPage/></MainLayout>} />
                    <Route path="/test" element={<MainLayout><ListTestPage/></MainLayout>} />
                    <Route path="/test/:skill" element={<MainLayout><ListTestPage /></MainLayout>} />
                    <Route path="/test/speaking/:testId" element={<VoiceRecorder />} />
                    <Route path="/checkMic/:testId" element={<SpeakingTest/>} />
                    <Route path="/verify-email" element={<VerifyEmail/>} />
                    <Route path="/result" element={<MainLayout><Component/></MainLayout> as React.ReactNode} />
                </Routes>
            </Router>
        </AuthProvider>
    )
}