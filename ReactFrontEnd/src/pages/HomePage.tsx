import { HeroSection } from "@/components/sections/HeroSection"
import { LatestTestsSection } from "@/components/sections/LatestTestsSection"
import { TipsSection } from "@/components/sections/TipsSection"
// import { FAQSection } from "@/components/sections/FAQSection"
import {useEffect, useState} from "react";
interface Tip {
    id: number
    skill: string
    title: string
    description: string
}


export function HomePage() {
    const [tests, setTests] = useState([])
    const [tips, setTips] = useState<{ [key: string]: Tip } | null>(null);
    useEffect(() => {
        fetch("http://localhost:8080/api/3-tests")
            .then(res => res.json())
            .then(data => setTests(data))

        fetch("http://localhost:8080/api/tips-summary")
            .then(res => res.json())
            .then(data => setTips(data));
    }, [])
    return (
        <div className="min-h-screen bg-gray-50">
            <HeroSection />
            <LatestTestsSection tests={tests} />
            {tips && <TipsSection tips={tips} />}
            {/*<FAQSection faqs={faqs} />*/}
        </div>
    )
}
