"use client"

import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group"
import { Label } from "@/components/ui/label"
import { Input } from "@/components/ui/input"
import { Clock, FileText, Maximize2, Menu, ChevronLeft, ChevronRight } from "lucide-react"

async function fetchReadingByTestId() {
  try {
    const response = await fetch(`http://localhost:8080/api/readings/2`);

    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }
    console.log(response)
    const reading = await response.json();
    console.log("Reading data:", reading);

    return reading;
  } catch (error) {
    console.error("Failed to fetch reading:", error);
    return null;
  }
}
interface Answer {
  questionId: string
  answer: string | boolean
  type: "multiple-choice" | "true-false" | "fill-blank"
}

interface Question {
  id: string
  type: "multiple-choice" | "true-false" | "fill-blank"
  question: string
  options?: string[]
  correctAnswer: string | boolean
}

const testData = {
  part1: [
    {
      id: "p1q1",
      type: "multiple-choice" as const,
      question: "What does the writer say about the performance of older typists on the test?",
      options: [
        "They used different motor skills from younger typists.",
        "They had been more efficiently trained than younger typists.",
        "They used more time-saving techniques than younger typists.",
        "They had better concentration skills than younger typists.",
      ],
      correctAnswer: "They used more time-saving techniques than younger typists.",
    },
    {
      id: "p1q2",
      type: "true-false" as const,
      question: "The experiment with rats showed that brain structure only changed when given familiar toys.",
      correctAnswer: false,
    },
    {
      id: "p1q3",
      type: "fill-blank" as const,
      question:
        "Mental function changes are determined by three factors: mental lifestyle, chronic disease, and _______ of the mind.",
      correctAnswer: "flexibility",
    },
  ],
  part2: [
    {
      id: "p2q1",
      type: "multiple-choice" as const,
      question: "According to the passage, what is the main benefit of regular exercise for the brain?",
      options: [
        "It increases the size of brain cells",
        "It improves blood flow to the brain",
        "It reduces stress hormones",
        "It enhances memory formation",
      ],
      correctAnswer: "It improves blood flow to the brain",
    },
    {
      id: "p2q2",
      type: "true-false" as const,
      question: "Research shows that learning new skills can create new neural pathways in adult brains.",
      correctAnswer: true,
    },
    {
      id: "p2q3",
      type: "fill-blank" as const,
      question:
        "The study found that participants who engaged in _______ activities showed improved cognitive performance.",
      correctAnswer: "challenging",
    },
  ],
  part3: [
    {
      id: "p3q1",
      type: "multiple-choice" as const,
      question: "What does the research suggest about brain plasticity in older adults?",
      options: [
        "It decreases significantly after age 60",
        "It remains constant throughout life",
        "It can be maintained through mental stimulation",
        "It only affects memory functions",
      ],
      correctAnswer: "It can be maintained through mental stimulation",
    },
    {
      id: "p3q2",
      type: "true-false" as const,
      question: "Social interaction has no significant impact on cognitive decline in elderly people.",
      correctAnswer: false,
    },
    {
      id: "p3q3",
      type: "fill-blank" as const,
      question:
        "The most effective way to maintain brain health is through a combination of physical exercise, mental challenges, and _______ engagement.",
      correctAnswer: "social",
    },
  ],
}

export default function IELTSTest() {
  const [currentPart, setCurrentPart] = useState(1)
  const [answers, setAnswers] = useState<Record<number, Answer[]>>({})
  const [timeRemaining, setTimeRemaining] = useState(46 * 60) // 46 minutes in seconds

  useEffect(() => {
    const timer = setInterval(() => {
      setTimeRemaining((prev) => (prev > 0 ? prev - 1 : 0))
    }, 1000)

    return () => clearInterval(timer)
  }, [])

  const formatTime = (seconds: number) => {
    const minutes = Math.floor(seconds / 60)
    return `${minutes} minutes remaining`
  }

  // Handle multiple choice questions
  const updateAnswer = (newAnswer: Answer) => {
    setAnswers((prev) => {
      const currentAnswers = prev[currentPart] || [];
  
      // Lọc câu trả lời cũ cho câu hỏi đó trong part hiện tại
      const filtered = currentAnswers.filter(a => a.questionId !== newAnswer.questionId);
  
      const newAnswersForPart = [...filtered, newAnswer];
  
      // Sắp xếp theo số câu hỏi (vd câu hỏi có id "Q1", "Q2")
      newAnswersForPart.sort((a, b) => {
        const numA = parseInt(a.questionId.match(/\d+$/)?.[0] || "0");
        const numB = parseInt(b.questionId.match(/\d+$/)?.[0] || "0");
        return numA - numB;
      });
  
      return {
        ...prev,
        [currentPart]: newAnswersForPart,
      };
    });
  }
  
  const handleMultipleChoice = (questionId: string, selectedAnswer: string) => {
    const newAnswer: Answer = {
      questionId,
      answer: selectedAnswer,
      type: "multiple-choice",
    }
    updateAnswer(newAnswer)
  }
  
  const handleTrueFalse = (questionId: string, selectedAnswer: boolean) => {
    const newAnswer: Answer = {
      questionId,
      answer: selectedAnswer,
      type: "true-false",
    }
    updateAnswer(newAnswer)
  }
  
  const handleFillBlank = (questionId: string, inputValue: string) => {
    const newAnswer: Answer = {
      questionId,
      answer: inputValue,
      type: "fill-blank",
    }
    updateAnswer(newAnswer)
  }
  // Get answer for a specific question
  const getAnswer = (questionId: string) => {
    const currentAnswers = answers[currentPart] || [];
    return currentAnswers.find(a => a.questionId === questionId)?.answer;
  }

  // Submit test function
  const handleSubmit = () => {
    console.log("Test submitted with answers:", answers)
    // Here you would typically send the answers to a server
    alert("Test submitted successfully!")
  }

  const getCurrentPartData = () => {
    switch (currentPart) {
      case 1:
        return testData.part1
      case 2:
        return testData.part2
      case 3:
        return testData.part3
      default:
        return testData.part1
    }
  }
  const [reading, setReading] = useState(null);

  useEffect(() => {
    async function fetchReadingByTestId() {
      try {
        const response = await fetch(`http://localhost:8080/api/readings/2`);
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        const data = await response.json();
        console.log("Reading data:", data);
        setReading(data);
      } catch (error) {
        console.error("Failed to fetch reading:", error);
      }
    }

    fetchReadingByTestId();
  }, [])
  const renderQuestion = (question: Question, index: number) => {
    const questionNumber = (currentPart - 1) * 3 + index + 1

    switch (question.type) {
      case "multiple-choice":
        return (
          <div key={question.id} className="mb-6">
            <p className="font-medium mb-3">
              {questionNumber}. {question.question}
            </p>
            <RadioGroup
              value={(getAnswer(question.id) as string) || ""}
              onValueChange={(value :string) => handleMultipleChoice(question.id, value)}
              className="space-y-2"
            >
              {question.options?.map((option, optionIndex) => (
                <div key={optionIndex} className="flex items-center space-x-2">
                  <RadioGroupItem value={option} id={`${question.id}-${optionIndex}`} />
                  <Label htmlFor={`${question.id}-${optionIndex}`} className="flex-1 cursor-pointer">
                    <span className="inline-flex items-center justify-center w-6 h-6 bg-blue-100 text-blue-800 rounded-full text-sm font-medium mr-2">
                      {String.fromCharCode(65 + optionIndex)}
                    </span>
                    {option}
                  </Label>
                </div>
              ))}
            </RadioGroup>
          </div>
        )

      case "true-false":
        return (
          <div key={question.id} className="mb-6">
            <p className="font-medium mb-3">
              {questionNumber}. {question.question}
            </p>
            <RadioGroup
              value={getAnswer(question.id)?.toString() || ""}
              onValueChange={(value :string) => handleTrueFalse(question.id, value === "true")}
              className="flex space-x-6"
            >
              <div className="flex items-center space-x-2">
                <RadioGroupItem value="true" id={`${question.id}-true`} />
                <Label htmlFor={`${question.id}-true`} className="cursor-pointer">
                  True
                </Label>
              </div>
              <div className="flex items-center space-x-2">
                <RadioGroupItem value="false" id={`${question.id}-false`} />
                <Label htmlFor={`${question.id}-false`} className="cursor-pointer">
                  False
                </Label>
              </div>
            </RadioGroup>
          </div>
        )

      case "fill-blank":
        return (
          <div key={question.id} className="mb-6">
            <p className="font-medium mb-3">
              {questionNumber}. {question.question}
            </p>
            <Input
              value={(getAnswer(question.id) as string) || ""}
              onChange={(e) => handleFillBlank(question.id, e.target.value)}
              placeholder="Type your answer here..."
              className="max-w-md"
            />
          </div>
        )

      default:
        return null
    }
  }

  return (
    
    <div className="min-h-screen bg-gray-50">
      
      {/* Header */}
      <header className="bg-white border-b border-gray-200 px-6 py-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-4">
            <div className="w-12 h-12 bg-orange-500 rounded-lg flex items-center justify-center">
              <span className="text-white font-bold text-lg">ot</span>
            </div>
            <div>
              <div className="text-sm text-gray-600">IELTS</div>
              <div className="text-sm font-medium">Online Tests.com</div>
            </div>
          </div>

          <div className="flex items-center space-x-6">
            <div className="flex items-center space-x-2 text-green-600">
              <Clock className="w-5 h-5" />
              <span className="font-medium">{formatTime(timeRemaining)}</span>
            </div>

            <div className="flex items-center space-x-4">
              <Button variant="ghost" size="sm">
                <FileText className="w-4 h-4 mr-2" />
                Review
              </Button>
              <Button variant="ghost" size="sm">
                <Maximize2 className="w-4 h-4" />
              </Button>
              <Button variant="ghost" size="sm">
                <Menu className="w-4 h-4" />
              </Button>
              <Button className="bg-green-600 hover:bg-green-700" onClick={handleSubmit}>
                Submit
                <ChevronRight className="w-4 h-4 ml-2" />
              </Button>
            </div>
          </div>
        </div>
      </header>

      <div className="flex">
        {/* Left Panel - Reading Passage */}
        <div className="w-1/2 p-6">
          <Card>
            <CardContent className="p-6">
              <div className="mb-6">
                <h1 className="text-2xl font-bold text-blue-900 mb-2">PART {currentPart}</h1>
                <h2 className="text-xl font-bold text-blue-900 mb-4">READING PASSAGE {currentPart}</h2>
                <p className="text-sm text-gray-600 mb-6">
                  You should spend about 20 minutes on <strong>Questions 1-13</strong>, which are based on Reading
                  Passage {currentPart} below.
                </p>
              </div>

              <div className="mb-6">
            
              </div>

              <div className="prose max-w-none">
                <h3 className="text-xl font-bold text-blue-900 mb-4">How the mind ages</h3>
                <p className="text-sm text-gray-700 mb-4">
                  <em>
                    The way mental function changes is largely determined by three factors-mental lifestyle, the impact
                    of chronic disease and flexibility of the mind.
                  </em>
                </p>
                <p className="text-sm text-gray-700 leading-relaxed">
                  Experiments have shown that younger monkeys consistently outperform their older colleagues on memory
                  tests. Formerly, psychologists concluded that memory and other mental functions in humans deteriorate
                  over time because of changes in the brain. Thus mental decline after young adulthood appeared
                  inevitable. The truth, however, is not quite so simple.
                </p>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Right Panel - Questions */}
        <div className="w-1/2 p-6">
          <Card>
            <CardContent className="p-6">
              <div className="mb-6">
                <h3 className="text-lg font-bold text-green-600 mb-2">
                  Questions {(currentPart - 1) * 3 + 1}-{currentPart * 3}
                </h3>
                <p className="text-sm text-gray-600">Choose the correct answer and write your responses below.</p>
              </div>

              <div className="space-y-6">
                {getCurrentPartData().map((question, index) => renderQuestion(question, index))}
              </div>
            </CardContent>
          </Card>
        </div>
      </div>

      {/* Bottom Navigation */}
      <div className="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 p-4">
        <div className="flex items-center justify-between max-w-7xl mx-auto">
          <div className="flex items-center space-x-2">
            {[1, 2, 3].map((part) => (
              <Button
                key={part}
                variant={currentPart === part ? "default" : "outline"}
                size="sm"
                onClick={() => setCurrentPart(part)}
                className={currentPart === part ? "bg-green-600 hover:bg-green-700" : ""}
              >
                Part {part}
              </Button>
            ))}
          </div>

          <div className="flex items-center space-x-4 text-sm text-gray-600">
            <span>Part 2: 0 of 3 questions</span>
            <span>Part 3: 0 of 3 questions</span>
          </div>

          <div className="flex items-center space-x-2">
            <Button
              variant="outline"
              size="sm"
              onClick={() => setCurrentPart(Math.max(1, currentPart - 1))}
              disabled={currentPart === 1}
            >
              <ChevronLeft className="w-4 h-4 mr-1" />
              Previous
            </Button>
            <Button
              variant="outline"
              size="sm"
              onClick={() => setCurrentPart(Math.min(3, currentPart + 1))}
              disabled={currentPart === 3}
            >
              Next
              <ChevronRight className="w-4 h-4 ml-1" />
            </Button>
          </div>
        </div>
      </div>
    </div>
  )
}
