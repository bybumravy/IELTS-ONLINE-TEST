import { Button } from "@/components/ui/button"
import { ChevronLeft, ChevronRight } from "lucide-react"

interface TestPart {
    id: number
    name: string
    totalQuestions: number
    completedQuestions: number
    isActive?: boolean
}

interface TestNavigationProps {
    parts: TestPart[]
    currentPart: number
    onPartChange: (partId: number) => void
    onPrevious: () => void
    onNext: () => void
    canGoPrevious: boolean
    canGoNext: boolean
}

export function TestNavigation({
                                   parts,
                                   currentPart,
                                   onPartChange,
                                   onPrevious,
                                   onNext,
                                   canGoPrevious,
                                   canGoNext,
                               }: TestNavigationProps) {
    return (
        <div className="bg-white border-t border-gray-200 px-6 py-4">
            <div className="flex items-center justify-between">
                <div className="flex items-center space-x-4">
                    {parts.map((part) => (
                        <Button
                            key={part.id}
                            variant={part.id === currentPart ? "default" : "outline"}
                            size="sm"
                            onClick={() => onPartChange(part.id)}
                            className={`${
                                part.id === currentPart
                                    ? "bg-cyan-600 hover:bg-cyan-700 text-white"
                                    : "text-gray-600 hover:text-gray-900"
                            }`}
                        >
                            <span className="font-medium">{part.name}:</span>
                            <span className="ml-2">
                {part.completedQuestions} of {part.totalQuestions} questions
              </span>
                        </Button>
                    ))}
                </div>

                <div className="flex items-center space-x-2">
                    <Button
                        variant="outline"
                        size="sm"
                        onClick={onPrevious}
                        disabled={!canGoPrevious}
                        className="w-10 h-10 rounded-full p-0"
                    >
                        <ChevronLeft className="w-4 h-4" />
                    </Button>
                    <Button
                        variant="outline"
                        size="sm"
                        onClick={onNext}
                        disabled={!canGoNext}
                        className="w-10 h-10 rounded-full p-0"
                    >
                        <ChevronRight className="w-4 h-4" />
                    </Button>
                </div>
            </div>
        </div>
    )
}
