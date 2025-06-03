import { Play, Clock, Star, Users } from "lucide-react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import type { IELTSTest } from "@/types/apiTypes"

interface LatestTestsSectionProps {
    tests: IELTSTest[]
}

export function LatestTestsSection({ tests }: LatestTestsSectionProps) {
    return (
        <section className="py-16 bg-white">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="text-center mb-12">
                    <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-4">Latest IELTS Online Tests</h2>
                    <p className="text-lg text-gray-600">Practice with our newest and most comprehensive IELTS tests</p>
                </div>

                <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
                    {tests.map((test) => (
                        <Card key={test.id} className="hover:shadow-lg transition-shadow">
                            <CardHeader>
                                <div className="flex items-center justify-between mb-2">
                                    <Badge variant={test.isNew ? "default" : "secondary"} className={test.isNew ? "bg-emerald-600" : ""}>
                                        {test.isNew ? "NEW" : test.type}
                                    </Badge>
                                    <div className="flex items-center text-sm text-gray-500">
                                        <Star className="w-4 h-4 text-yellow-400 mr-1" />
                                        {test.rating}
                                    </div>
                                </div>
                                <CardTitle className="text-lg">{test.title}</CardTitle>
                                <CardDescription>
                                    <div className="flex items-center gap-4 text-sm">
                    <span className="flex items-center">
                      <Clock className="w-4 h-4 mr-1" />
                        {test.duration}
                    </span>
                                        <span className="flex items-center">
                      <Users className="w-4 h-4 mr-1" />
                                            {test.participants.toLocaleString()}
                    </span>
                                    </div>
                                </CardDescription>
                            </CardHeader>
                            <CardContent>
                                <div className="flex flex-wrap gap-2 mb-4">
                                    {test.sections.map((section) => (
                                        <Badge key={section} variant="outline" className="text-xs">
                                            {section}
                                        </Badge>
                                    ))}
                                </div>
                                <Button className="w-full bg-emerald-600 hover:bg-emerald-700">
                                    <Play className="w-4 h-4 mr-2" />
                                    Start Test
                                </Button>
                            </CardContent>
                        </Card>
                    ))}
                </div>
            </div>
        </section>
    )
}
