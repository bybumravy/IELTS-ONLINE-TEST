"use client"
import { Link } from "react-router-dom";
import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import {
    NavigationMenu,
    NavigationMenuContent,
    NavigationMenuItem,
    NavigationMenuLink,
    NavigationMenuList,
    NavigationMenuTrigger,
} from "@/components/ui/navigation-menu"
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from "@/components/ui/accordion"
import { Skeleton } from "@/components/ui/skeleton"
import { Alert, AlertDescription } from "@/components/ui/alert"
import {
    BookOpen,
    Headphones,
    PenTool,
    Eye,
    Mic,
    Clock,
    Star,
    Users,
    Award,
    ChevronRight,
    Mail,
    Phone,
    MapPin,
    AlertCircle,
    Loader2,
} from "lucide-react"

// API Configuration
const API_BASE_URL = "http://localhost:8080/api"

// Mock data for fallback when API is unavailable
const mockData = {
    latestTests: [
        {
            id: 1,
            title: "IELTS Academic Practice Test #45",
            type: "ACADEMIC",
            duration: 165,
            difficulty: "INTERMEDIATE",
            sections: ["LISTENING", "READING", "WRITING", "SPEAKING"],
            participantCount: 1250,
            averageRating: 4.8,
        },
        {
            id: 2,
            title: "IELTS General Training Test #23",
            type: "GENERAL_TRAINING",
            duration: 165,
            difficulty: "BEGINNER",
            sections: ["LISTENING", "READING", "WRITING", "SPEAKING"],
            participantCount: 890,
            averageRating: 4.6,
        },
        {
            id: 3,
            title: "IELTS Speaking Practice Test",
            type: "ACADEMIC",
            duration: 15,
            difficulty: "ADVANCED",
            sections: ["SPEAKING"],
            participantCount: 2100,
            averageRating: 4.9,
        },
    ],
    tips: [
        {
            id: 1,
            skill: "LISTENING",
            title: "Master IELTS Listening",
            description: "Learn effective strategies to improve your listening comprehension and score higher.",
            tipPoints: ["Practice with different accents", "Take notes while listening", "Predict answers before hearing"],
            author: "Dr. Sarah Johnson",
            featured: true,
        },
        {
            id: 2,
            skill: "READING",
            title: "Excel in IELTS Reading",
            description: "Develop speed reading techniques and comprehension skills for better performance.",
            tipPoints: ["Skim and scan effectively", "Manage your time wisely", "Practice different question types"],
            author: "Prof. Michael Chen",
            featured: true,
        },
        {
            id: 3,
            skill: "WRITING",
            title: "Perfect Your IELTS Writing",
            description: "Master essay structure and improve your writing coherence and cohesion.",
            tipPoints: ["Plan before writing", "Use varied vocabulary", "Check grammar and spelling"],
            author: "Dr. Emily Davis",
            featured: true,
        },
        {
            id: 4,
            skill: "SPEAKING",
            title: "Boost Your IELTS Speaking",
            description: "Build confidence and fluency for the speaking test with proven techniques.",
            tipPoints: ["Practice daily conversations", "Record yourself speaking", "Learn topic-specific vocabulary"],
            author: "James Wilson",
            featured: true,
        },
    ],
    faqs: [
        {
            id: 1,
            question: "What is the IELTS test format?",
            answer:
                "IELTS consists of four sections: Listening (30 minutes), Reading (60 minutes), Writing (60 minutes), and Speaking (11-14 minutes). The total test time is approximately 2 hours and 45 minutes.",
            category: "General",
        },
        {
            id: 2,
            question: "How is IELTS scored?",
            answer:
                "IELTS uses a 9-band scoring system. Each section is scored from 0-9, and your overall band score is the average of all four sections, rounded to the nearest half band.",
            category: "Scoring",
        },
        {
            id: 3,
            question: "What's the difference between Academic and General Training?",
            answer:
                "Academic IELTS is for university study, while General Training is for work experience, training programs, or migration. The Listening and Speaking tests are the same, but Reading and Writing differ.",
            category: "Test Types",
        },
        {
            id: 4,
            question: "How often can I take the IELTS test?",
            answer:
                "You can take IELTS as many times as you want. There's no limit on the number of times you can take the test, but you should allow enough time between tests to improve your English.",
            category: "General",
        },
        {
            id: 5,
            question: "How long are IELTS results valid?",
            answer:
                "IELTS results are valid for 2 years from the test date. After this period, the results are no longer considered current for official purposes.",
            category: "Results",
        },
        {
            id: 6,
            question: "Can I get my IELTS results online?",
            answer:
                "Yes, you can view your results online 13 days after your test date. You'll receive an email notification when your results are available.",
            category: "Results",
        },
    ],
    user: {
        id: 1,
        name: "John Doe",
        email: "john.doe@example.com",
        profileImage: null,
        role: "USER",
        isPremium: false,
    },
}

// Flag to use mock data in preview/development environment
const USE_MOCK_API = true

// API Service Functions
const apiService = {
    // Get latest IELTS tests
    getLatestTests: async () => {
        if (USE_MOCK_API) {
            // Simulate API delay
            await new Promise((resolve) => setTimeout(resolve, 800))
            return mockData.latestTests
        }

        try {
            const response = await fetch(`${API_BASE_URL}/tests/latest`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${localStorage.getItem("token") || ""}`,
                },
            })

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`)
            }

            return await response.json()
        } catch (error) {
            console.error("Error fetching latest tests:", error)

            // Fallback to mock data if API call fails
            console.log("Falling back to mock data for tests")
            return mockData.latestTests
        }
    },

    // Get IELTS tips by skill
    getTipsBySkill: async (skill = null) => {
        if (USE_MOCK_API) {
            // Simulate API delay
            await new Promise((resolve) => setTimeout(resolve, 1000))
            return mockData.tips
        }

        try {
            const url = skill ? `${API_BASE_URL}/tips?skill=${skill}` : `${API_BASE_URL}/tips/featured`

            const response = await fetch(url, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            })

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`)
            }

            return await response.json()
        } catch (error) {
            console.error("Error fetching tips:", error)

            // Fallback to mock data if API call fails
            console.log("Falling back to mock data for tips")
            return mockData.tips
        }
    },

    // Get FAQ data
    getFAQs: async () => {
        if (USE_MOCK_API) {
            // Simulate API delay
            await new Promise((resolve) => setTimeout(resolve, 1200))
            return mockData.faqs
        }

        try {
            const response = await fetch(`${API_BASE_URL}/faqs`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            })

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`)
            }

            return await response.json()
        } catch (error) {
            console.error("Error fetching FAQs:", error)

            // Fallback to mock data if API call fails
            console.log("Falling back to mock data for FAQs")
            return mockData.faqs
        }
    },

    // User authentication
    login: async (credentials) => {
        if (USE_MOCK_API) {
            // Simulate API delay
            await new Promise((resolve) => setTimeout(resolve, 1000))

            // Simulate successful login
            const mockResponse = {
                token: "mock-jwt-token",
                user: mockData.user,
            }

            localStorage.setItem("token", mockResponse.token)
            localStorage.setItem("user", JSON.stringify(mockResponse.user))

            return mockResponse
        }

        try {
            const response = await fetch(`${API_BASE_URL}/auth/login`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(credentials),
            })

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`)
            }

            const data = await response.json()
            if (data.token) {
                localStorage.setItem("token", data.token)
                localStorage.setItem("user", JSON.stringify(data.user))
            }
            return data
        } catch (error) {
            console.error("Error during login:", error)

            if (error.message.includes("Failed to fetch")) {
                // Simulate successful login with mock data if API is unavailable
                console.log("API unavailable, using mock login")
                const mockResponse = {
                    token: "mock-jwt-token",
                    user: mockData.user,
                }

                localStorage.setItem("token", mockResponse.token)
                localStorage.setItem("user", JSON.stringify(mockResponse.user))

                return mockResponse
            }

            throw error
        }
    },

    // Get user profile
    getUserProfile: async () => {
        if (USE_MOCK_API) {
            // Simulate API delay
            await new Promise((resolve) => setTimeout(resolve, 800))
            return mockData.user
        }

        try {
            const token = localStorage.getItem("token")
            if (!token) throw new Error("No token found")

            const response = await fetch(`${API_BASE_URL}/auth/profile`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
            })

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`)
            }

            return await response.json()
        } catch (error) {
            console.error("Error fetching user profile:", error)

            // Fallback to mock data if API call fails
            if (error.message.includes("Failed to fetch")) {
                console.log("API unavailable, using mock user profile")
                return mockData.user
            }

            throw error
        }
    },

    // Start a test
    startTest: async (testId) => {
        if (USE_MOCK_API) {
            // Simulate API delay
            await new Promise((resolve) => setTimeout(resolve, 1000))

            // Return mock test session
            return {
                sessionId: `mock-session-${Date.now()}`,
                testId: testId,
                userId: 1,
                startTime: new Date().toISOString(),
                timeLimit: 165,
                status: "STARTED",
            }
        }

        try {
            const token = localStorage.getItem("token")
            const response = await fetch(`${API_BASE_URL}/tests/${testId}/start`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token || ""}`,
                },
            })

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`)
            }

            return await response.json()
        } catch (error) {
            console.error("Error starting test:", error)

            // Fallback to mock data if API call fails
            if (error.message.includes("Failed to fetch")) {
                console.log("API unavailable, using mock test session")
                return {
                    sessionId: `mock-session-${Date.now()}`,
                    testId: testId,
                    userId: 1,
                    startTime: new Date().toISOString(),
                    timeLimit: 165,
                    status: "STARTED",
                }
            }

            throw error
        }
    },
}

export default function IELTSHomepage() {
    // State management
    const [isLoggedIn, setIsLoggedIn] = useState(false)
    const [user, setUser] = useState(null)
    const [latestTests, setLatestTests] = useState([])
    const [tips, setTips] = useState([])
    const [faqs, setFaqs] = useState([])
    const [loading, setLoading] = useState({
        tests: true,
        tips: true,
        faqs: true,
    })
    const [errors, setErrors] = useState({
        tests: null,
        tips: null,
        faqs: null,
    })

    // Check if user is logged in on component mount
    useEffect(() => {
        const token = localStorage.getItem("token")
        const userData = localStorage.getItem("user")

        if (token && userData) {
            setIsLoggedIn(true)
            setUser(JSON.parse(userData))

            // Verify token is still valid
            apiService.getUserProfile().catch(() => {
                // Token is invalid, logout user
                handleLogout()
            })
        }
    }, [])

    // Fetch data on component mount
    useEffect(() => {
        const controller = new AbortController();
        const { signal } = controller;

        const fetchAllData = async () => {
            try {
                // Reset all errors at start
                setErrors({ tests: null, tips: null, faqs: null });

                // Start all loading states
                setLoading({ tests: true, tips: true, faqs: true });

                // Fetch data in parallel
                const [testsResponse, tipsResponse, faqsResponse] = await Promise.all([
                    apiService.getLatestTests({ signal }).catch(error => {
                        console.error("Tests fetch error:", error);
                        return { error, isFallback: true };
                    }),
                    apiService.getTipsBySkill({ signal }).catch(error => {
                        console.error("Tips fetch error:", error);
                        return { error, isFallback: true };
                    }),
                    apiService.getFAQs({ signal }).catch(error => {
                        console.error("FAQs fetch error:", error);
                        return { error, isFallback: true };
                    })
                ]);

                // Process responses
                if (!signal.aborted) {
                    setLatestTests(testsResponse.isFallback ? mockData.latestTests : testsResponse);
                    setTips(tipsResponse.isFallback ? mockData.tips : tipsResponse);
                    setFaqs(faqsResponse.isFallback ? mockData.faqs : faqsResponse);

                    // Set errors if any fallbacks were used
                    setErrors({
                        tests: testsResponse.isFallback ? "Failed to load tests. Using fallback data." : null,
                        tips: tipsResponse.isFallback ? "Failed to load tips. Using fallback data." : null,
                        faqs: faqsResponse.isFallback ? "Failed to load FAQs. Using fallback data." : null
                    });
                }
            } catch (error) {
                if (!signal.aborted && error.name !== 'AbortError') {
                    console.error("Global fetch error:", error);
                    // Fallback to all mock data if something went very wrong
                    setLatestTests(mockData.latestTests);
                    setTips(mockData.tips);
                    setFaqs(mockData.faqs);
                    setErrors({
                        tests: "Failed to load data. Using fallback for all.",
                        tips: "Failed to load data. Using fallback for all.",
                        faqs: "Failed to load data. Using fallback for all."
                    });
                }
            } finally {
                if (!signal.aborted) {
                    setLoading({ tests: false, tips: false, faqs: false });
                }
            }
        };
        fetchAllData();
        return () => controller.abort();
    },[]);

    const handleLogin = async (credentials) => {
        try {
            const data = await apiService.login(credentials)
            setIsLoggedIn(true)
            setUser(data.user)
            return true
        } catch (error) {
            console.error("Login failed:", error)
            // For demo purposes, simulate successful login even if API fails
            if (error.message.includes("Failed to fetch")) {
                setIsLoggedIn(true)
                setUser(mockData.user)
                return true
            }
            return false
        }
    }

    const handleLogout = () => {
        localStorage.removeItem("token")
        localStorage.removeItem("user")
        setIsLoggedIn(false)
        setUser(null)
    }

    const handleStartTest = async (testId) => {
        try {


            const testSession = await apiService.startTest(testId)
            // Navigate to test interface with session data
            console.log("Test started:", testSession)
            alert(`Test started successfully! Session ID: ${testSession.sessionId}`)
            // In a real app, you would navigate to the test page
            // window.location.href = `/test/${testSession.sessionId}`
        } catch (error) {
            console.error("Failed to start test:", error)
            alert("Failed to start test. Using mock session instead.")

            // Create a mock session for demo purposes
            const mockSession = {
                sessionId: `mock-session-${Date.now()}`,
                testId: testId,
                status: "STARTED",
            }
            console.log("Mock test session:", mockSession)
        }
    }

    const getSkillIcon = (skill) => {
        const icons = {
            LISTENING: Headphones,
            READING: Eye,
            WRITING: PenTool,
            SPEAKING: Mic,
        }
        return icons[skill?.toUpperCase()] || BookOpen
    }

    // Loading skeleton for tests
    const TestsSkeleton = () => (
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
            {[1, 2, 3].map((i) => (
                <Card key={i}>
                    <CardHeader>
                        <div className="flex items-center justify-between mb-2">
                            <Skeleton className="h-6 w-20" />
                            <Skeleton className="h-4 w-12" />
                        </div>
                        <Skeleton className="h-6 w-full" />
                        <div className="flex items-center space-x-4">
                            <Skeleton className="h-4 w-16" />
                            <Skeleton className="h-4 w-16" />
                        </div>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-3">
                            <div className="flex flex-wrap gap-2">
                                {[1, 2, 3].map((j) => (
                                    <Skeleton key={j} className="h-6 w-16" />
                                ))}
                            </div>
                            <Skeleton className="h-6 w-20" />
                            <Skeleton className="h-10 w-full" />
                        </div>
                    </CardContent>
                </Card>
            ))}
        </div>
    )

    // Loading skeleton for tips
    const TipsSkeleton = () => (
        <div className="grid gap-8 md:grid-cols-2 lg:grid-cols-4">
            {[1, 2, 3, 4].map((i) => (
                <Card key={i} className="text-center">
                    <CardHeader>
                        <Skeleton className="mx-auto mb-4 h-16 w-16 items-center justify-center rounded-full bg-gradient-to-r from-blue-600 to-indigo-600" />
                        <Skeleton className="h-6 w-32 mx-auto" />
                        <Skeleton className="h-4 w-full" />
                        <Skeleton className="h-4 w-3/4 mx-auto" />
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-2">
                            {[1, 2, 3].map((j) => (
                                <Skeleton key={j} className="h-4 w-full" />
                            ))}
                        </div>
                        <Skeleton className="h-10 w-full mt-4" />
                    </CardContent>
                </Card>
            ))}
        </div>
    )

    return (
        <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-indigo-50">
            {/* Header */}
            <header className="sticky top-0 z-50 w-full border-b bg-white/95 backdrop-blur supports-[backdrop-filter]:bg-white/60">
                <div className="container mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex h-16 items-center justify-between">
                        {/* Logo */}
                        <div className="flex items-center space-x-2">
                            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-gradient-to-r from-blue-600 to-indigo-600">
                                <BookOpen className="h-6 w-6 text-white" />
                            </div>
                            <span className="text-xl font-bold text-gray-900">IELTS Master</span>
                        </div>

                        {/* Navigation */}
                        <div className="hidden md:flex justify-center flex-1">
                            <div className="flex space-x-1">
                                <div className="relative group">
                                    <button className="text-black px-4 py-2 text-sm font-medium hover:bg-emerald-600 rounded-md transition-colors flex items-center">
                                        IELTS TESTS
                                    </button>
                                    <div className="absolute top-full left-0 mt-1 w-56 bg-white rounded-lg shadow-xl border opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-200 z-50">
                                        <div className="py-2">
                                            <Link
                                                to="#"
                                                className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-emerald-50 hover:text-emerald-600"
                                            >
                                                <Headphones className="w-4 h-4 mr-3" />
                                                IELTS Listening Tests
                                            </Link>
                                            <Link
                                                to="#"
                                                className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-emerald-50 hover:text-emerald-600"
                                            >
                                                <BookOpen className="w-4 h-4 mr-3" />
                                                IELTS Reading Tests
                                            </Link>
                                            <Link
                                                to="#"
                                                className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-emerald-50 hover:text-emerald-600"
                                            >
                                                <PenTool className="w-4 h-4 mr-3" />
                                                IELTS Writing Tests
                                            </Link>
                                            <Link
                                                to="#"
                                                className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-emerald-50 hover:text-emerald-600"
                                            >

                                                IELTS Speaking Tests
                                            </Link>
                                        </div>
                                    </div>
                                </div>

                                <div className="relative group">
                                    <button className="text-black px-4 py-2 text-sm font-medium hover:bg-emerald-600 rounded-md transition-colors">
                                        IELTS TIPS
                                    </button>
                                    <div className="absolute top-full left-0 mt-1 w-56 bg-white rounded-lg shadow-xl border opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-200 z-50">
                                        <div className="py-2">
                                            <Link
                                                to="#"
                                                className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-emerald-50 hover:text-emerald-600"
                                            >
                                                <Headphones className="w-4 h-4 mr-3" />
                                                Listening Tips
                                            </Link>
                                            <Link
                                                to="#"
                                                className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-emerald-50 hover:text-emerald-600"
                                            >
                                                <BookOpen className="w-4 h-4 mr-3" />
                                                Reading Tips
                                            </Link>
                                            <Link
                                                to="#"
                                                className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-emerald-50 hover:text-emerald-600"
                                            >
                                                <PenTool className="w-4 h-4 mr-3" />
                                                Writing Tips
                                            </Link>
                                            <Link
                                                to="#"
                                                className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-emerald-50 hover:text-emerald-600"
                                            >

                                                Speaking Tips
                                            </Link>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        {/* Auth Section */}
                        <div className="flex items-center space-x-4">
                            {isLoggedIn && user ? (
                                <DropdownMenu>
                                    <DropdownMenuTrigger asChild>
                                        <Button variant="ghost" className="relative h-8 w-8 rounded-full">
                                            <Avatar className="h-8 w-8">
                                                <AvatarImage src={user.profileImage || "/placeholder.svg"} alt={user.name} />
                                                <AvatarFallback>{user.name?.charAt(0) || "U"}</AvatarFallback>
                                            </Avatar>
                                        </Button>
                                    </DropdownMenuTrigger>
                                    <DropdownMenuContent className="w-56" align="end" forceMount>
                                        <div className="flex items-center justify-start gap-2 p-2">
                                            <div className="flex flex-col space-y-1 leading-none">
                                                <p className="font-medium">{user.name}</p>
                                                <p className="w-[200px] truncate text-sm text-muted-foreground">{user.email}</p>
                                            </div>
                                        </div>
                                        <DropdownMenuSeparator />
                                        <DropdownMenuItem>
                                            <Users className="mr-2 h-4 w-4" />
                                            <span>Profile</span>
                                        </DropdownMenuItem>
                                        <DropdownMenuItem>
                                            <Clock className="mr-2 h-4 w-4" />
                                            <span>History</span>
                                        </DropdownMenuItem>
                                        <DropdownMenuItem>
                                            <Award className="mr-2 h-4 w-4" />
                                            <span>Premium</span>
                                        </DropdownMenuItem>
                                        <DropdownMenuSeparator />
                                        <DropdownMenuItem onClick={handleLogout}>
                                            <span>Logout</span>
                                        </DropdownMenuItem>
                                    </DropdownMenuContent>
                                </DropdownMenu>
                            ) : (
                                <div className="flex items-center space-x-2">
                                    <Button variant="ghost" onClick={() => setIsLoggedIn(true)}>
                                        Login
                                    </Button>
                                    <Button onClick={() => setIsLoggedIn(true)}>Register</Button>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </header>

            {/* Hero Section */}
            <section className="relative py-20 lg:py-32">
                <div className="container mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="text-center">
                        <h1 className="text-4xl font-bold tracking-tight text-gray-900 sm:text-6xl">
                            Master Your{" "}
                            <span className="bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent">
                IELTS Journey
              </span>
                        </h1>
                        <p className="mt-6 text-lg leading-8 text-gray-600 max-w-2xl mx-auto">
                            Prepare for IELTS with confidence using our comprehensive practice tests, expert tips, and personalized
                            learning experience.
                        </p>
                        <div className="mt-10 flex items-center justify-center gap-x-6">
                            <Button
                                size="lg"
                                className="bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700"
                                onClick={() => {
                                    if (latestTests.length > 0) {
                                        handleStartTest(latestTests[0].id)
                                    }
                                }}
                            >
                                Start Free Test
                                <ChevronRight className="ml-2 h-4 w-4" />
                            </Button>
                            <Button variant="outline" size="lg">
                                View All Tests
                            </Button>
                        </div>
                    </div>
                </div>
            </section>

            {/* Latest IELTS Tests */}
            <section className="py-16 bg-white">
                <div className="container mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="text-center mb-12">
                        <h2 className="text-3xl font-bold text-gray-900 mb-4">Latest IELTS Online Tests</h2>
                        <p className="text-lg text-gray-600">Practice with our newest and most updated test materials</p>
                    </div>

                    {errors.tests && (
                        <Alert className="mb-6">
                            <AlertCircle className="h-4 w-4" />
                            <AlertDescription>
                                {errors.tests}
                                <Button variant="link" className="ml-2 p-0 h-auto" onClick={fetchLatestTests}>
                                    Try again
                                </Button>
                            </AlertDescription>
                        </Alert>
                    )}

                    {loading.tests ? (
                        <TestsSkeleton />
                    ) : (
                        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
                            {latestTests.map((test) => (
                                <Card key={test.id} className="hover:shadow-lg transition-shadow duration-300">
                                    <CardHeader>
                                        <div className="flex items-center justify-between mb-2">
                                            <Badge variant={test.type === "ACADEMIC" ? "default" : "secondary"}>{test.type}</Badge>
                                            <div className="flex items-center space-x-1">
                                                <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
                                                <span className="text-sm text-gray-600">{test.averageRating || "N/A"}</span>
                                            </div>
                                        </div>
                                        <CardTitle className="text-lg">{test.title}</CardTitle>
                                        <CardDescription>
                                            <div className="flex items-center space-x-4 text-sm">
                                                <div className="flex items-center space-x-1">
                                                    <Clock className="h-4 w-4" />
                                                    <span>{test.duration}m</span>
                                                </div>
                                                <div className="flex items-center space-x-1">
                                                    <Users className="h-4 w-4" />
                                                    <span>{test.participantCount || 0}</span>
                                                </div>
                                            </div>
                                        </CardDescription>
                                    </CardHeader>
                                    <CardContent>
                                        <div className="space-y-3">
                                            <div className="flex flex-wrap gap-2">
                                                {test.sections?.map((section) => (
                                                    <Badge key={section} variant="outline" className="text-xs">
                                                        {section}
                                                    </Badge>
                                                ))}
                                            </div>
                                            <Badge
                                                variant="outline"
                                                className={`${
                                                    test.difficulty === "BEGINNER"
                                                        ? "border-green-200 text-green-700"
                                                        : test.difficulty === "INTERMEDIATE"
                                                            ? "border-yellow-200 text-yellow-700"
                                                            : "border-red-200 text-red-700"
                                                }`}
                                            >
                                                {test.difficulty}
                                            </Badge>
                                            <Button className="w-full mt-4" onClick={() => handleStartTest(test.id)} disabled={loading.tests}>
                                                {loading.tests ? (
                                                    <>
                                                        <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                                                        Loading...
                                                    </>
                                                ) : (
                                                    "Start Test"
                                                )}
                                            </Button>
                                        </div>
                                    </CardContent>
                                </Card>
                            ))}
                        </div>
                    )}
                </div>
            </section>

            {/* Tips Section */}
            <section className="py-16 bg-gray-50">
                <div className="container mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="text-center mb-12">
                        <h2 className="text-3xl font-bold text-gray-900 mb-4">Expert IELTS Tips</h2>
                        <p className="text-lg text-gray-600">Proven strategies to boost your IELTS score</p>
                    </div>

                    {errors.tips && (
                        <Alert className="mb-6">
                            <AlertCircle className="h-4 w-4" />
                            <AlertDescription>
                                {errors.tips}
                                <Button variant="link" className="ml-2 p-0 h-auto" onClick={fetchTips}>
                                    Try again
                                </Button>
                            </AlertDescription>
                        </Alert>
                    )}

                    {loading.tips ? (
                        <TipsSkeleton />
                    ) : (
                        <div className="grid gap-8 md:grid-cols-2 lg:grid-cols-4">
                            {tips.map((tip) => {
                                const IconComponent = getSkillIcon(tip.skill)
                                return (
                                    <Card key={tip.id} className="text-center hover:shadow-lg transition-shadow duration-300">
                                        <CardHeader>
                                            <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-gradient-to-r from-blue-600 to-indigo-600">
                                                <IconComponent className="h-8 w-8 text-white" />
                                            </div>
                                            <CardTitle className="text-xl">{tip.title}</CardTitle>
                                            <CardDescription>{tip.description}</CardDescription>
                                        </CardHeader>
                                        <CardContent>
                                            <ul className="space-y-2 text-sm text-gray-600">
                                                {tip.tipPoints?.slice(0, 3).map((tipItem, index) => (
                                                    <li key={index} className="flex items-start space-x-2">
                                                        <ChevronRight className="h-4 w-4 text-blue-600 mt-0.5 flex-shrink-0" />
                                                        <span>{tipItem}</span>
                                                    </li>
                                                ))}
                                            </ul>
                                            <Button variant="outline" className="w-full mt-4">
                                                Learn More
                                            </Button>
                                        </CardContent>
                                    </Card>
                                )
                            })}
                        </div>
                    )}
                </div>
            </section>

            {/* FAQ Section */}
            <section className="py-16 bg-white">
                <div className="container mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="text-center mb-12">
                        <h2 className="text-3xl font-bold text-gray-900 mb-4">Frequently Asked Questions</h2>
                        <p className="text-lg text-gray-600">Get answers to common IELTS questions</p>
                    </div>

                    {errors.faqs && (
                        <Alert className="mb-6 max-w-3xl mx-auto">
                            <AlertCircle className="h-4 w-4" />
                            <AlertDescription>
                                {errors.faqs}
                                <Button variant="link" className="ml-2 p-0 h-auto" onClick={fetchFAQs}>
                                    Try again
                                </Button>
                            </AlertDescription>
                        </Alert>
                    )}

                    <div className="max-w-3xl mx-auto">
                        {loading.faqs ? (
                            <div className="space-y-4">
                                {[1, 2, 3, 4, 5].map((i) => (
                                    <div key={i} className="border rounded-lg p-4">
                                        <Skeleton className="h-6 w-3/4 mb-2" />
                                        <Skeleton className="h-4 w-full" />
                                        <Skeleton className="h-4 w-2/3" />
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <Accordion type="single" collapsible className="w-full">
                                {faqs.map((faq, index) => (
                                    <AccordionItem key={faq.id || index} value={`item-${faq.id || index}`}>
                                        <AccordionTrigger className="text-left">{faq.question}</AccordionTrigger>
                                        <AccordionContent className="text-gray-600">{faq.answer}</AccordionContent>
                                    </AccordionItem>
                                ))}
                            </Accordion>
                        )}
                    </div>
                </div>
            </section>

            {/* Footer */}
            <footer className="bg-gray-900 text-white">
                <div className="container mx-auto px-4 sm:px-6 lg:px-8 py-12">
                    <div className="grid gap-8 lg:grid-cols-4">
                        {/* Company Info */}
                        <div className="lg:col-span-1">
                            <div className="flex items-center space-x-2 mb-4">
                                <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-gradient-to-r from-blue-600 to-indigo-600">
                                    <BookOpen className="h-6 w-6 text-white" />
                                </div>
                                <span className="text-xl font-bold">IELTS Master</span>
                            </div>
                            <p className="text-gray-400 mb-4">
                                Your trusted partner for IELTS preparation. Master your English skills with our comprehensive practice
                                tests and expert guidance.
                            </p>
                            <div className="flex space-x-4">
                            </div>
                        </div>

                        {/* Quick Links */}
                        <div>
                            <h3 className="text-lg font-semibold mb-4">Quick Links</h3>
                            <ul className="space-y-2 text-gray-400">
                                <li>
                                    <a href="#" className="hover:text-white">
                                        All Tests
                                    </a>
                                </li>
                                <li>
                                    <a href="#" className="hover:text-white">
                                        Practice Tests
                                    </a>
                                </li>
                                <li>
                                    <a href="#" className="hover:text-white">
                                        Study Materials
                                    </a>
                                </li>
                                <li>
                                    <a href="#" className="hover:text-white">
                                        Score Calculator
                                    </a>
                                </li>
                                <li>
                                    <a href="#" className="hover:text-white">
                                        Test Centers
                                    </a>
                                </li>
                            </ul>
                        </div>

                        {/* IELTS Skills */}
                        <div>
                            <h3 className="text-lg font-semibold mb-4">IELTS Skills</h3>
                            <ul className="space-y-2 text-gray-400">
                                <li>
                                    <a href="#" className="hover:text-white">
                                        Listening
                                    </a>
                                </li>
                                <li>
                                    <a href="#" className="hover:text-white">
                                        Reading
                                    </a>
                                </li>
                                <li>
                                    <a href="#" className="hover:text-white">
                                        Writing
                                    </a>
                                </li>
                                <li>
                                    <a href="#" className="hover:text-white">
                                        Speaking
                                    </a>
                                </li>
                                <li>
                                    <a href="#" className="hover:text-white">
                                        Tips & Strategies
                                    </a>
                                </li>
                            </ul>
                        </div>

                        {/* Contact Info */}
                        <div>
                            <h3 className="text-lg font-semibold mb-4">Contact Us</h3>
                            <div className="space-y-3 text-gray-400">
                                <div className="flex items-center space-x-2">
                                    <Mail className="h-4 w-4" />
                                    <span>support@ieltsmaster.com</span>
                                </div>
                                <div className="flex items-center space-x-2">
                                    <Phone className="h-4 w-4" />
                                    <span>+1 (555) 123-4567</span>
                                </div>
                                <div className="flex items-center space-x-2">
                                    <MapPin className="h-4 w-4" />
                                    <span>123 Education St, Learning City</span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="border-t border-gray-800 mt-8 pt-8 text-center text-gray-400">
                        <p>&copy; {new Date().getFullYear()} IELTS Master. All rights reserved.</p>
                    </div>
                </div>
            </footer>
        </div>
    )
}