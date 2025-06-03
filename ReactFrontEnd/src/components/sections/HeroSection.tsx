import { Button } from "@/components/ui/button"

export function HeroSection() {
    return (
        <section className="bg-gradient-to-r from-emerald-600 to-emerald-700 text-white py-20">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
                <h1 className="text-4xl md:text-6xl font-bold mb-6">Master Your IELTS Test</h1>
                <p className="text-xl md:text-2xl mb-8 text-emerald-100">
                    Practice with real IELTS tests, get expert tips, and achieve your target band score
                </p>
                <div className="flex flex-col sm:flex-row gap-4 justify-center">
                    <Button size="lg" className="bg-white text-emerald-600 hover:bg-gray-100">
                        Start Free Test
                    </Button>
                    <Button size="lg" variant="outline" className="border-white text-white hover:bg-white hover:text-emerald-600">
                        View All Tests
                    </Button>
                </div>
            </div>
        </section>
    )
}
