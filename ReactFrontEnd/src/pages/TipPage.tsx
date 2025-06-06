import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Input } from "@/components/ui/input"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Search } from "lucide-react"
import {useEffect, useMemo, useState} from "react";
import {useNavigate} from "react-router-dom";


interface Tip {
    id: string | number;
    type: string;
    skill: string;
    description: string;
}
function TipPage() {
    const [tips, setTips] = useState<Tip[]>([]);
    const [skill, setSkill] = useState<skill>("Reading")
    const [search, setSearch] = useState<string>("")
    const navigate = useNavigate();

    const handleNavigate = (path: string): void => {
        navigate(path);
    };

    type skill = "Listening" | "Reading" | "Writing" | "Speaking";
    // useEffect(() => {
    //     fetch("http://localhost:8080/api/read/${id}")
    //         .then((res) => res.json())
    //         .then((data: Tip[]) => {
    //             console.log("Fetched data:", data);
    //             console.log("JSON:", JSON.stringify(data, null, 2));
    //             setTips(data);
    //         })
    //         .catch((err) => console.error(err));
    // }, []);
    useEffect(() => {
        fetch(`http://localhost:8080/api/student/${skill}`)
            .then((res) => res.json())
            .then((data: Tip[]) => {
                setTips(data);
            })
            .catch((err) => console.error(err));
    }, [skill]);

    const filtered  = useMemo(() => {
        return tips.filter(
            (t) =>
                t.skill === skill &&
                t.type.toLowerCase().includes(search.trim().toLowerCase()),
        );
    }, [tips, skill, search]);
    return (
        <div className="min-h-screen bg-background">
            <header className="bg-primary text-primary-foreground py-8">
                <div className="container mx-auto px-4 ">
                    <h1 className="text-4xl md:text-5xl font-bold text-center">IELTS Tips</h1>
                    <p className="text-center mt-3 text-primary-foreground/90 text-lg">Hướng dẫn và mẹo cho kỳ thi IELTS</p>
                </div>
            </header>

            <div className="container mx-auto px-4 py-8">
                <div className="flex flex-col md:flex-row gap-4 mb-8 place-items-center grid">
                    <div className="flex relative w-full md:w-96">
                        <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
                        <Input
                            type="search"
                            placeholder="Tìm kiếm các dạng IELTS..."
                            className="pl-8 w-full"
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                        />
                    </div>
                </div>

                {/*ShadCN/ui Tabs thiết kế với onValueChange: (value: string) => void nhưng setSkill nhận Skill thay vì string*/}
                <Tabs defaultValue={skill} onValueChange={(value) => setSkill(value as skill)} className="w-full grid place-items-center">
                    <TabsList className="grid grid-cols-4 mb-8 bg-primary/10 place-items-center w-full">
                        <>{["Listening", "Reading", "Writing", "Speaking"].map((s) => (
                            <TabsTrigger
                                key={s}
                                value={s}
                                className="data-[state=active]:bg-primary data-[state=active]:text-primary-foreground w-full">
                                {s}
                            </TabsTrigger>
                        ))}</>
                    </TabsList>


                    <TabsContent value={skill} className="space-y-6">
                        <h2 className="text-2xl font-bold mb-6 text-primary">IELTS {skill}</h2>
                        <>{filtered.length === 0 ? (
                            <p className="text-muted-foreground">Not found tip.</p>
                        ):(
                            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                                {filtered.map((t) => (
                                    <QuestionTypeCard key={t.id} title={t.type} description={t.description}
                                                      onClick={() => handleNavigate(`/tipDetail/${t.id}`)}/>
                                ))}
                            </div>
                        )}</>
                    </TabsContent>
                </Tabs>
            </div>
        </div>
    )
}
interface QuestionTypeCardProps {
    title: string
    description: string
    onClick?: () => void;
}

function QuestionTypeCard({ title, description, onClick }: QuestionTypeCardProps) {
    return (
        <Card onClick={onClick}
              className="hover:shadow-lg transition-shadow duration-200 border-l-4 border-l-primary">
            <CardHeader>
                <CardTitle className="text-primary">{title}</CardTitle>
            </CardHeader>
            <CardContent>
                <CardDescription className="text-base leading-relaxed">{description}</CardDescription>
            </CardContent>
        </Card>
    )
}
export default TipPage;