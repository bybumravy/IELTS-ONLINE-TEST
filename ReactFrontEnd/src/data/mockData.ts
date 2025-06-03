import { BookOpen, Headphones, PenTool, Mic } from "lucide-react"
import type { IELTSTest, Tip, FAQ } from "@/types"

export const latestTests: IELTSTest[] = [
    {
        id: 1,
        title: "IELTS Academic Practice Test #47",
        type: "Academic",
        duration: "2h 45m",
        sections: ["Listening", "Reading", "Writing", "Speaking"],
        difficulty: "Intermediate",
        participants: 1247,
        rating: 4.8,
        isNew: true,
    },
    {
        id: 2,
        title: "IELTS General Training Test #23",
        type: "General Training",
        duration: "2h 45m",
        sections: ["Listening", "Reading", "Writing", "Speaking"],
        difficulty: "Advanced",
        participants: 892,
        rating: 4.7,
        isNew: false,
    },
    {
        id: 3,
        title: "IELTS Listening Practice Test",
        type: "Listening Only",
        duration: "30m",
        sections: ["Listening"],
        difficulty: "Beginner",
        participants: 2156,
        rating: 4.9,
        isNew: true,
    },
]

export const tips: Tip[] = [
    {
        id: 1,
        skill: "Listening",
        title: "Master IELTS Listening: Key Strategies for Success",
        description: "Learn essential techniques to improve your listening comprehension and score higher.",
        readTime: "5 min read",
        icon: Headphones,
        color: "bg-blue-500",
    },
    {
        id: 2,
        skill: "Reading",
        title: "Speed Reading Techniques for IELTS Success",
        description: "Discover proven methods to read faster while maintaining comprehension.",
        readTime: "7 min read",
        icon: BookOpen,
        color: "bg-green-500",
    },
    {
        id: 3,
        skill: "Writing",
        title: "IELTS Writing Task 2: Essay Structure Guide",
        description: "Master the perfect essay structure for high band scores in Writing Task 2.",
        readTime: "10 min read",
        icon: PenTool,
        color: "bg-purple-500",
    },
    {
        id: 4,
        skill: "Speaking",
        title: "Confidence Building for IELTS Speaking Test",
        description: "Overcome anxiety and speak with confidence in your IELTS speaking exam.",
        readTime: "6 min read",
        icon: Mic,
        color: "bg-orange-500",
    },
]

export const faqs: FAQ[] = [
    {
        question: "What is the IELTS test format?",
        answer:
            "IELTS consists of four sections: Listening (30 minutes), Reading (60 minutes), Writing (60 minutes), and Speaking (11-14 minutes). The total test time is approximately 2 hours and 45 minutes.",
    },
    {
        question: "How is IELTS scored?",
        answer:
            "IELTS uses a 9-band scoring system. Each section is scored from 0-9, and your overall band score is the average of all four sections, rounded to the nearest half band.",
    },
    {
        question: "What's the difference between Academic and General Training IELTS?",
        answer:
            "Academic IELTS is for university study, while General Training is for work experience, training programs, or migration. The Listening and Speaking tests are the same, but Reading and Writing differ in content and style.",
    },
    {
        question: "How often can I take the IELTS test?",
        answer:
            "You can take IELTS as many times as you want. There's no limit on the number of times you can take the test, but you should allow enough time between tests to improve your English skills.",
    },
    {
        question: "How long are IELTS results valid?",
        answer:
            "IELTS results are valid for two years from the test date. After this period, the results are no longer considered current for most purposes.",
    },
]
