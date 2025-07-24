"use client"

import { useEffect, useState } from "react"
import { useSearchParams, useNavigate } from "react-router-dom"
import { useAuth } from "@/contexts/AuthContext"
import { Button } from "@/components/ui/button"
import { CheckCircle, XCircle, Loader2, AlertTriangle, Mail } from "lucide-react"

export default function VerifyEmail() {
    const { fetchUser } = useAuth()
    const [searchParams] = useSearchParams()
    const [message, setMessage] = useState("Verifying your email...")
    const [isSuccess, setIsSuccess] = useState<boolean | null>(null)
    const [isLoading, setIsLoading] = useState(true)
    const token = searchParams.get("token")
    const navigate = useNavigate()
    const API_URL = import.meta.env.VITE_API_URL

    useEffect(() => {
        const verify = async () => {
            try {
                setIsLoading(true)
                const res = await fetch(`${API_URL}/api/verify-email?token=${token}`, {
                    method: "GET",
                    credentials: "include",
                })

                if (!res.ok) {
                    throw new Error("Verification failed")
                }

                await fetchUser() // Update user from backend
                setMessage("Email verified successfully! You can now access all features.")
                setIsSuccess(true)
            } catch (err: any) {
                setMessage("Email verification failed. The link may be expired or invalid.")
                setIsSuccess(false)
            } finally {
                setIsLoading(false)
            }
        }

        if (token) {
            verify()
        } else {
            setMessage("Verification token is missing from the URL.")
            setIsSuccess(false)
            setIsLoading(false)
        }
    }, [token, fetchUser, API_URL])

    const handleGoHome = () => {
        navigate("/")
    }

    const handleResendEmail = () => {
        // Add resend email logic here if needed
        console.log("Resend email functionality")
    }

    return (
        <div className="min-h-screen bg-gray-100 flex items-center justify-center p-4">
            <div className="w-full max-w-md">
                <div className="bg-white rounded-2xl shadow-lg p-8 text-center">
                    {/* Icon Section */}
                    <div className="mb-6">
                        {isLoading ? (
                            <div className="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto">
                                <Loader2 className="h-8 w-8 text-blue-600 animate-spin" />
                            </div>
                        ) : isSuccess === true ? (
                            <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto">
                                <CheckCircle className="h-8 w-8 text-green-600" />
                            </div>
                        ) : isSuccess === false ? (
                            <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto">
                                <XCircle className="h-8 w-8 text-red-600" />
                            </div>
                        ) : (
                            <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mx-auto">
                                <Mail className="h-8 w-8 text-gray-600" />
                            </div>
                        )}
                    </div>

                    {/* Title */}
                    <h1 className="text-2xl font-bold mb-4 text-gray-800">
                        {isLoading ? "Verifying Email" : isSuccess === true ? "Email Verified!" : "Verification Failed"}
                    </h1>

                    {/* Message */}
                    <p className="text-gray-600 mb-6 leading-relaxed">{message}</p>

                    {/* Action Buttons */}
                    <div className="space-y-3">
                        {isSuccess === true && (
                            <Button onClick={handleGoHome} className="w-full bg-green-600 hover:bg-green-700">
                                <CheckCircle className="h-4 w-4 mr-2" />
                                Continue to Home
                            </Button>
                        )}

                        {isSuccess === false && (
                            <div className="space-y-3">
                                <Button onClick={handleGoHome} variant="outline" className="w-full bg-transparent">
                                    Go to Home
                                </Button>
                                <Button
                                    onClick={handleResendEmail}
                                    variant="outline"
                                    className="w-full border-blue-600 text-blue-600 hover:bg-blue-50 bg-transparent"
                                >
                                    <Mail className="h-4 w-4 mr-2" />
                                    Resend Verification Email
                                </Button>
                            </div>
                        )}

                        {isLoading && (
                            <div className="flex items-center justify-center text-sm text-gray-500">
                                <Loader2 className="h-4 w-4 mr-2 animate-spin" />
                                Please wait while we verify your email...
                            </div>
                        )}
                    </div>

                    {/* Additional Info */}
                    {isSuccess === false && (
                        <div className="mt-6 p-4 bg-amber-50 border border-amber-200 rounded-xl">
                            <div className="flex items-center justify-center mb-2">
                                <AlertTriangle className="h-4 w-4 text-amber-600 mr-2" />
                                <span className="text-sm font-medium text-amber-800">Need Help?</span>
                            </div>
                            <p className="text-xs text-amber-700">
                                If you continue to have issues, please contact our support team or try requesting a new verification
                                email.
                            </p>
                        </div>
                    )}
                </div>

                {/* Footer */}
                <div className="text-center mt-6">
                    <p className="text-sm text-gray-500">
                        Having trouble? <button className="text-blue-600 hover:underline">Contact Support</button>
                    </p>
                </div>
            </div>
        </div>
    )
}
