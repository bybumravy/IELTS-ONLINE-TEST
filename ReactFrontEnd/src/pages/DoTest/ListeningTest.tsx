import {useState, useRef, useEffect} from "react"
import { Button } from "@/components/ui/button"
import { DoTestHeader } from "@/components/layout/doTest/DoTestHeader"
import { Play, Pause, RotateCcw, RefreshCw, ChevronLeft, ChevronRight, Volume2, ChevronDown } from "lucide-react"

type Question = {
    question: string;
    answer: string;
    options: string[];
    explanation: string;
};

type Section = {
    sectionNumber: number;
    type: string;
    imageUrl: string;
    introduction: string;
    questions: Question[];
};

type TaskListening = {
    taskNumber: number;
    title: string;
    audioIntroduction: string;
    sections: Section[];
};

type ListeningTest = {
    _id: string;
    testId: string;
    audioUrl: string;
    tasks: TaskListening[];
};


export default function IeltsListeningTest() {
    const [currentPart, setCurrentPart] = useState(1)
    const [isPlaying, setIsPlaying] = useState(false)
    const [currentTime, setCurrentTime] = useState("-21:45")
    const [volume, setVolume] = useState(75)
    const audioRef = useRef<HTMLAudioElement | null>(null)
    const [listeningTest, setListeningTest] = useState<ListeningTest | null>(null)
    const [sections, setSections] = useState<Section[]>([])
    const [tasks, setTasks] = useState<TaskListening[]>([])


    useEffect(() => {
        fetch("http://localhost:8080/api/listening/t01")
            .then((res) => res.json())
            .then((data) => {
                setListeningTest(data)
                setTasks(data.tasks);
                // setSections(data.tasks.sections);
                console.log("Fetched tasks:", data.tasks)


            })
            .catch((err) => console.error("Failed to load listening test:", err))
    }, [])
    const currentTask = tasks?.find((task) => task.taskNumber === Number(currentPart)) || null;

    // const currentSection = currentTask?.sections?.[0];
    const togglePlayPause = () => {
        if (audioRef.current) {
            if (isPlaying) {
                audioRef.current.pause()
            } else {
                audioRef.current.play()
            }
            setIsPlaying(!isPlaying)
        }
    }



    const handleVolumeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const newVolume = Number.parseInt(e.target.value)
        setVolume(newVolume)
        if (audioRef.current) {
            audioRef.current.volume = newVolume / 100
        }
    }

    const resetAudio = () => {
        if (audioRef.current) {
            audioRef.current.currentTime = 0
            setCurrentTime("-21:45")
        }
    }

    return (
        <div className="flex flex-col min-h-screen">
            {currentTask && (
                <audio ref={audioRef} src={listeningTest?.audioUrl} />
            )}

            <div className="sticky top-0 z-50 flex flex-col shadow-sm">
                <DoTestHeader />

                <div className="bg-white border-b border-gray-200 px-4 py-3 flex items-center gap-3">
                    <Button
                        variant="outline"
                        size="icon"
                        className="rounded-full w-8 h-8 border-gray-300"
                        onClick={resetAudio}
                    >
                        <RotateCcw className="w-4 h-4" />
                    </Button>

                    <Button
                        variant="outline"
                        size="icon"
                        className="rounded-full w-10 h-10 border-gray-300 bg-teal-500 text-white hover:bg-teal-600"
                        onClick={togglePlayPause}
                    >
                        {isPlaying ? <Pause className="w-5 h-5" /> : <Play className="w-5 h-5" />}
                    </Button>

                    <div className="flex-1">
                        <input
                            type="range"
                            min="0"
                            max="100"
                            className="w-full h-1 bg-gray-200 rounded-lg appearance-none cursor-pointer"
                        />
                    </div>

                    <div className="flex items-center gap-2">
                        <Volume2 className="w-5 h-5 text-gray-500" />
                        <input
                            type="range"
                            min="0"
                            max="100"
                            value={volume}
                            onChange={handleVolumeChange}
                            className="w-24 h-1 bg-gray-200 rounded-lg appearance-none cursor-pointer"
                        />
                        <div className="flex items-center gap-1 text-gray-700 border border-gray-300 rounded px-2 py-1">
                            <span>Source 1</span>
                            <ChevronDown className="w-4 h-4" />
                        </div>
                    </div>
                </div>
            </div>
            {/* Main Content */}
            <div className="flex-1 p-6 max-w-7xl mx-auto w-full">
                {currentTask ? (
                    <>
                        <h1 className="text-2xl font-bold text-blue-900 mb-6">
                            Part {currentTask.taskNumber}: {currentTask.title}
                        </h1>

                        {currentTask.sections.map((section, sectionIdx) => (
                            <div key={sectionIdx} className="mb-10">
                                <h2 className="text-xl font-semibold text-teal-600 mb-2">
                                    Section {section.sectionNumber}
                                </h2>

                                {section.introduction && (
                                    <p className="text-gray-700 italic mb-4">{section.introduction}</p>
                                )}

                                {section.imageUrl && (
                                    <img
                                        src={section.imageUrl}
                                        alt="Listening illustration"
                                        className="w-full h-auto rounded mb-4"
                                    />
                                )}

                                {/* Hiển thị danh sách câu hỏi của section */}
                                {section.questions.map((question, qIdx) => (
                                    <div key={qIdx} className="mb-6">
                                        <p className="text-gray-800 font-medium mb-3">
                                            {qIdx + 1}. {question.question}
                                        </p>

                                        {/* Hiển thị options nếu có */}
                                        {question.options?.length > 0 ? (
                                            <div className="space-y-2 mb-3">
                                                {question.options.map((option, optIdx) => (
                                                    <div key={optIdx} className="flex items-center">
                                                        <input
                                                            type="radio"
                                                            id={`sec${sectionIdx}-q${qIdx}-opt${optIdx}`}
                                                            name={`sec${sectionIdx}-question-${qIdx}`}
                                                            className="mr-2"
                                                        />
                                                        <label htmlFor={`sec${sectionIdx}-q${qIdx}-opt${optIdx}`}>
                                                            {option}
                                                        </label>
                                                    </div>
                                                ))}
                                            </div>
                                        ) : (
                                            <input
                                                type="text"
                                                placeholder="Your answer"
                                                className="w-full border border-gray-300 rounded p-2"
                                            />
                                        )}
                                    </div>
                                ))}
                            </div>
                        ))}
                    </>
                ) : (
                    <p className="text-gray-600">No task available.</p>
                )}
            </div>


            {/* Progress Bar */}
            {listeningTest && listeningTest.tasks && (
            <div className="sticky bottom-0 bg-white border-t border-gray-200 p-4">
                <div className="max-w-7xl mx-auto grid grid-cols-4 gap-4">
                    {listeningTest.tasks.map((task) => {
                        const isActive = currentPart === task.taskNumber
                        return (
                            <div
                                key={task.taskNumber}
                                onClick={() => setCurrentPart(task.taskNumber)}
                                className={`border rounded-lg p-4 cursor-pointer transition duration-200 text-center ${
                                    isActive
                                        ? "border-teal-500 bg-teal-50 text-teal-700"
                                        : "border-gray-200 bg-white hover:bg-gray-50 text-gray-800"
                                }`}
                            >
                                <h3 className="font-semibold text-sm">Part {task.taskNumber}</h3>
                            </div>
                        )
                    })}
                </div>
            </div>
                )}

        </div>
    )
}
