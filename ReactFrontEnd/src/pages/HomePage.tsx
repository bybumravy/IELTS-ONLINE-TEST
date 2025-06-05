import { HeroSection } from "@/components/sections/HeroSection"
import { LatestTestsSection } from "@/components/sections/LatestTestsSection"
// import { TipsSection } from "@/components/sections/TipsSection"
// import { FAQSection } from "@/components/sections/FAQSection"
import {useEffect, useState} from "react";


export function HomePage() {
    const [tests, setTests] = useState([])
    useEffect(() => {
        fetch("http://localhost:8080/api/tests")
            .then(res => res.json())
            .then(data => setTests(data))

        fetch("http://localhost:8080/api/tips")
            .then(res => res.json())
            .then(data => setTests(data))
    }, [])
    return (
        <div className="min-h-screen bg-gray-50">
            <HeroSection />
            <LatestTestsSection tests={tests} />
            {/*<TipsSection tips={tips} />*/}
            {/*<FAQSection faqs={faqs} />*/}
        </div>
    )
}
