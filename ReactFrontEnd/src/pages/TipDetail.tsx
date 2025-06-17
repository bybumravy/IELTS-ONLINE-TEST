import {useEffect, useState} from "react";
import { useParams } from "react-router-dom";
import {Button} from "@/components/ui/button.tsx";
import {PracticeExercise} from "@/components/sections/PracticeExcercise.tsx";
import type {Exercises} from "@/types/apiTypes"
import {StrategyAndTip} from "@/components/sections/StrategyAndTip.tsx";
interface TipDetail {
    id: string | number;
    type: string;
    skill: string;
    description: string;
    strategy?: string[];
    tips?: string[];
    exercises: Exercises[];
}

function TipDetail() {
    const [detail, setDetail] = useState<TipDetail | null>(null);
    const { skill, id } = useParams<{ skill: string; id: string }>();


    useEffect(() => {
        if (!id) return;

        fetch(`http://localhost:8080/api/${skill}/${id}`)
            .then((res) => res.json())
            .then((data: TipDetail) => {
                setDetail(data);
            })
            .catch((error) => {
                console.error("Lỗi khi gọi API chi tiết tip:", error);
                setDetail(null);
            });
    }, [id, skill]);

    if (!detail) {
        return (
            <div className="min-h-screen flex items-center justify-center text-red-500">
                Không tìm thấy dữ liệu.
            </div>
        );
    }
    return(

        <div className="min-h-screen bg-white">
            <section className="py-16 bg-gray-50">
                <div className="container mx-auto px-4">
                    {/* Reading Skill Content */}
                    <div className="max-w-4xl mx-auto">
                        <StrategyAndTip {...detail}/>
                        {/* Practice Exercise */}
                        <PracticeExercise exercises={detail.exercises} skill={skill}/>
                        {/* Optional back button */}
                        <div className="px-6 pb-6">
                            <Button className="w-full bg-emerald-600 hover:bg-emerald-700"
                                    onClick={() => history.back()}>← Back</Button>
                        </div>
                    </div>
                </div>
            </section>
        </div>
    )
};

export default TipDetail;