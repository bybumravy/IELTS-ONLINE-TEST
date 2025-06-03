import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import type { Tip } from "@/types/apiTypes"

interface TipsSectionProps {
    tips: Tip[]
}

export function TipsSection({ tips }: TipsSectionProps) {
    return (
        <section className="py-16 bg-gray-50">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="text-center mb-12">
                    <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-4">Expert IELTS Tips</h2>
                    <p className="text-lg text-gray-600">Learn from our experts and improve your IELTS performance</p>
                </div>

                <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
                    {tips.map((tip) => (
                        <Card key={tip.id} className="hover:shadow-lg transition-shadow cursor-pointer">
                            <CardHeader>
                                <div className={`w-12 h-12 ${tip.color} rounded-lg flex items-center justify-center mb-4`}>
                                    <tip.icon className="w-6 h-6 text-white" />
                                </div>
                                <Badge variant="outline" className="w-fit mb-2">
                                    {tip.skill}
                                </Badge>
                                <CardTitle className="text-lg leading-tight">{tip.title}</CardTitle>
                            </CardHeader>
                            <CardContent>
                                <p className="text-gray-600 text-sm mb-4">{tip.description}</p>
                                <div className="flex items-center justify-between">
                                    <span className="text-xs text-gray-500">{tip.readTime}</span>
                                    <Button variant="ghost" size="sm" className="text-emerald-600 hover:text-emerald-700">
                                        Read More
                                    </Button>
                                </div>
                            </CardContent>
                        </Card>
                    ))}
                </div>
            </div>
        </section>
    )
}
