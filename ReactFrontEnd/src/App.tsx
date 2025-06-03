"use client"

import { useState } from "react"
import { BrowserRouter as Router, Routes, Route } from "react-router-dom"
import { Header } from "@/components/layout/Header"
import { Footer } from "@/components/layout/Footer"
import { HomePage } from "@/pages/HomePage"
import {AuthProvider} from "@/contexts/AuthContext";


export default function App() {

  return (
      <AuthProvider>
      <Router>
        <div className="min-h-screen bg-gray-50">
          <Header/>
          <Routes>
            <Route path="/" element={<HomePage />} />
            {/*<Route path="/dashboard" element={<Dashboard />} />*/}
            {/*<Route path="/tests/listening" element={<ListeningTests />} />*/}
            {/*<Route path="/tests/reading" element={<ReadingTests />} />*/}
            {/*<Route path="/tests/writing" element={<WritingTests />} />*/}
            {/*<Route path="/tests/speaking" element={<SpeakingTests />} />*/}
            {/*<Route path="/tests/all" element={<AllTests />} />*/}
            {/* Add more routes for tips pages */}
          </Routes>
          <Footer />
        </div>
      </Router>
          </AuthProvider>
  )
}
