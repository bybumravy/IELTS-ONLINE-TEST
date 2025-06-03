"use client"
import { HeroSection } from "@/components/sections/HeroSection"
import { LatestTestsSection } from "@/components/sections/LatestTestsSection"
import { TipsSection } from "@/components/sections/TipsSection"
import { FAQSection } from "@/components/sections/FAQSection"
import { latestTests, tips, faqs } from "@/data/mockData"

export function HomePage() {
    return (
        <div className="min-h-screen bg-gray-50">
            <HeroSection />
            <LatestTestsSection tests={latestTests} />
            <TipsSection tips={tips} />
            <FAQSection faqs={faqs} />
        </div>
    )
}
