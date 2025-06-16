import { Button } from "@/components/ui/button";
import { Clock, Printer, Maximize2, Menu } from "lucide-react";
import { Link } from "react-router-dom";
import { BookOpen } from "lucide-react";
import { useEffect, useState } from "react";

interface DoTestHeaderProps {
    initialTime: number; // truyền thời gian ban đầu vào (giây)
    onSubmit: () => void;
}

export function DoTestHeader({ initialTime, onSubmit }: DoTestHeaderProps) {
    const [timeRemaining, setTimeRemaining] = useState(initialTime);

    useEffect(() => {
        const timer = setInterval(() => {
            setTimeRemaining((prev) => {
                if (prev <= 1) {
                    clearInterval(timer);
                    onSubmit(); // hết giờ tự submit
                    return 0;
                }
                return prev - 1;
            });
        }, 1000);

        return () => clearInterval(timer); // cleanup khi unmount
    }, [onSubmit]);

    const formatTime = (seconds: number) => {
        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = seconds % 60;
        return `${minutes}:${remainingSeconds < 10 ? "0" : ""}${remainingSeconds}`;
    };

    return (
        <header className="bg-white border-b border-gray-200 px-6 py-3">
            <div className="flex items-center justify-between">
                {/* logo */}
                <div className="flex items-center">
                    <Link to="/" className="flex items-center space-x-2">
                        <div className="w-8 h-8 bg-emerald-600 rounded-lg flex items-center justify-center">
                            <BookOpen className="w-5 h-5 text-white" />
                        </div>
                        <span className="text-xl font-bold text-gray-900">IELTS Master</span>
                    </Link>
                </div>

                {/* time */}
                <div className="flex items-center space-x-2 text-orange-600">
                    <Clock className="w-5 h-5" />
                    <span className="font-medium">{formatTime(timeRemaining)}</span>
                </div>

                {/* action buttons */}
                <div className="flex items-center space-x-2">
                    <Button variant="ghost" size="sm">
                        <Printer className="w-4 h-4" />
                    </Button>
                    <Button variant="ghost" size="sm">
                        <Maximize2 className="w-4 h-4" />
                    </Button>
                    <Button variant="ghost" size="sm">
                        <Menu className="w-4 h-4" />
                    </Button>
                    <Button
                        onClick={onSubmit}
                        className="bg-orange-600 hover:bg-orange-700 text-white px-6"
                    >
                        Submit
                    </Button>
                </div>
            </div>
        </header>
    );
}
