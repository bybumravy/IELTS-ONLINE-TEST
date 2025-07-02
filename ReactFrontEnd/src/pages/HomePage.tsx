import { HeroSection } from "@/components/sections/HeroSection"
import { LatestTestsSection } from "@/components/sections/LatestTestsSection"
import { TipsSection } from "@/components/sections/TipsSection"
// import { FAQSection } from "@/components/sections/FAQSection"
import {useEffect, useState} from "react";
interface Tip {
    id: string;
    skill: string;
    type: string;
    description: string;
}
interface IELTSTest {
    id: string
    testTitle: string
    tags: string[]
    createdAt: string
}

export function HomePage() {
    const [tests, setTests] = useState<IELTSTest[]>([]);
    const [tips, setTips] = useState<{ [key: string]: Tip } | null>(null);
    const API_URL = import.meta.env.VITE_API_URL;
    useEffect(() => {
        fetch(`${API_URL}/api/3-tests`)
            .then(res => res.json())
            .then(data => setTests(data))

        fetch(`${API_URL}/api/all/tips-summary`)
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