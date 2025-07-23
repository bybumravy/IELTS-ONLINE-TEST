import { useEffect, useState } from "react";
import { useParams, useNavigate, useSearchParams } from "react-router-dom";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs";
import { BookOpen, FileText, Mic, Volume2 } from "lucide-react";
import { MainLayout } from "@/components/layout/MainLayout";

const API_URL = import.meta.env.VITE_API_URL;

export default function FullTestResult() {
  const { testId } = useParams<{ testId: string }>();
  const [searchParams] = useSearchParams();
  const testAnswerId = testId || searchParams.get("testAnswerId");
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<"listening" | "reading" | "writing" | "speaking">("listening");
  const [results, setResults] = useState<any>({});

  useEffect(() => {
    if (!testAnswerId) return;
    setLoading(true);
    // Giả sử backend có API trả về kết quả từng kỹ năng theo testId và user hiện tại
    fetch(`${API_URL}/api/result/fulltest/${testAnswerId}`, { credentials: "include" })
      .then(res => {
        if (!res.ok) throw new Error("Không tìm thấy kết quả full test");
        return res.json();
      })
      .then(data => {
        setResults(data);
        setLoading(false);
      })
      .catch(e => {
        setError(e.message);
        setLoading(false);
      });
  }, [testAnswerId]);

  if (loading) return (
    <MainLayout>
      <div className="min-h-screen flex items-center justify-center">
        Đang tải kết quả...
      </div>
    </MainLayout>
  );
  if (error) return (
    <MainLayout>
      <div className="min-h-screen flex items-center justify-center text-red-600">
        {error}
      </div>
    </MainLayout>
  );

  return (
    <MainLayout>
      <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 py-10 px-2">
        <div className="max-w-4xl mx-auto space-y-8">
          <div className="bg-white rounded-2xl shadow-2xl p-8 text-center mb-6">
            <h1 className="text-4xl font-extrabold text-emerald-700 mb-2 tracking-tight">Full Test Result</h1>
            <p className="text-gray-500 text-lg">Your overall IELTS test performance</p>
          </div>
          <Tabs value={activeTab} onValueChange={v => setActiveTab(v as typeof activeTab)} className="w-full">
            <TabsList className="grid grid-cols-4 gap-2 mb-8 bg-emerald-50 rounded-xl p-2">
              <TabsTrigger value="listening" className="flex items-center gap-2 text-emerald-700 font-semibold"><Volume2 className="w-4 h-4" />Listening</TabsTrigger>
              <TabsTrigger value="reading" className="flex items-center gap-2 text-emerald-700 font-semibold"><BookOpen className="w-4 h-4" />Reading</TabsTrigger>
              <TabsTrigger value="writing" className="flex items-center gap-2 text-emerald-700 font-semibold"><FileText className="w-4 h-4" />Writing</TabsTrigger>
              <TabsTrigger value="speaking" className="flex items-center gap-2 text-emerald-700 font-semibold"><Mic className="w-4 h-4" />Speaking</TabsTrigger>
            </TabsList>
            <TabsContent value="listening">
              {results.listening ? (
                <Card className="mb-4 shadow-lg border-0 bg-gradient-to-r from-emerald-100 to-emerald-50">
                  <CardContent className="p-6 flex flex-col md:flex-row justify-between items-center">
                    <div className="text-left">
                      <h2 className="text-2xl font-bold mb-2 text-emerald-800">Listening Result</h2>
                      <div className="text-lg">Score: <span className="font-semibold text-emerald-700">{results.listening.band}/9</span></div>
                    </div>
                    <Button onClick={() => navigate(`/listening-result/${results.listening.id}`)} className="mt-4 md:mt-0 bg-emerald-600 hover:bg-emerald-700">View Details</Button>
                  </CardContent>
                </Card>
              ) : <div className="text-gray-400 text-center">No listening result found.</div>}
            </TabsContent>
            <TabsContent value="reading">
              {results.reading ? (
                <Card className="mb-4 shadow-lg border-0 bg-gradient-to-r from-blue-100 to-blue-50">
                  <CardContent className="p-6 flex flex-col md:flex-row justify-between items-center">
                    <div className="text-left">
                      <h2 className="text-2xl font-bold mb-2 text-blue-800">Reading Result</h2>
                      <div className="text-lg">Score: <span className="font-semibold text-blue-700">{results.reading.band}/9</span></div>
                    </div>
                    <Button onClick={() => navigate(`/reading-result/${results.reading.id}`)} className="mt-4 md:mt-0 bg-blue-600 hover:bg-blue-700">View Details</Button>
                  </CardContent>
                </Card>
              ) : <div className="text-gray-400 text-center">No reading result found.</div>}
            </TabsContent>
            <TabsContent value="writing">
              {results.writing ? (
                <Card className="mb-4 shadow-lg border-0 bg-gradient-to-r from-orange-100 to-orange-50">
                  <CardContent className="p-6 flex flex-col md:flex-row justify-between items-center">
                    <div className="text-left">
                      <h2 className="text-2xl font-bold mb-2 text-orange-800">Writing Result</h2>
                      <div className="text-lg">Score: <span className="font-semibold text-orange-700">{results.writing.band || results.writing.score || "-"}/9</span></div>
                    </div>
                    <Button onClick={() => navigate(`/writing-result/${results.writing.id}`)} className="mt-4 md:mt-0 bg-orange-600 hover:bg-orange-700">View Details</Button>
                  </CardContent>
                </Card>
              ) : <div className="text-gray-400 text-center">No writing result found.</div>}
            </TabsContent>
            <TabsContent value="speaking">
              {results.speaking ? (
                <Card className="mb-4 shadow-lg border-0 bg-gradient-to-r from-purple-100 to-purple-50">
                  <CardContent className="p-6 flex flex-col md:flex-row justify-between items-center">
                    <div className="text-left">
                      <h2 className="text-2xl font-bold mb-2 text-purple-800">Speaking Result</h2>
                      <div className="text-lg">Score: <span className="font-semibold text-purple-700">{results.speaking.band}/9</span></div>
                    </div>
                    <Button onClick={() => navigate(`/speaking-result/${results.speaking.id}`)} className="mt-4 md:mt-0 bg-purple-600 hover:bg-purple-700">View Details</Button>
                  </CardContent>
                </Card>
              ) : <div className="text-gray-400 text-center">No speaking result found.</div>}
            </TabsContent>
          </Tabs>
        </div>
      </div>
    </MainLayout>
  );
} 