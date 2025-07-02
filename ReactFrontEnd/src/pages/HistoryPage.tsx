import  { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import type { TestHistory } from '../services/historyService';
import { getStudentTestHistory } from '../services/historyService';
import HistoryCard from '../components/history/HistoryCard';
import HistoryFilter from '../components/history/HistoryFilter';
import HistoryStats from '../components/history/HistoryStats';

const HistoryPage: React.FC = () => {
  const [testHistory, setTestHistory] = useState<TestHistory[]>([]);
  const [filteredHistory, setFilteredHistory] = useState<TestHistory[]>([]);
  const [selectedSkill, setSelectedSkill] = useState('all');
  const [loading, setLoading] = useState(true);
  const { user } = useAuth();

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        if (user?.username) {
          const data = await getStudentTestHistory(user.username);
          setTestHistory(data);
          setFilteredHistory(data);
        }
      } catch (error) {
        console.error('Error fetching test history:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchHistory();
  }, [user]);

  useEffect(() => {
    if (selectedSkill === 'all') {
      setFilteredHistory(testHistory);
    } else {
      setFilteredHistory(testHistory.filter(item => item.skill === selectedSkill));
    }
  }, [selectedSkill, testHistory]);

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-[60vh]">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-6">
        History Test
      </h1>
      
      <div className="flex gap-6">
        <div className="w-1/4">
          <HistoryFilter
            selectedSkill={selectedSkill}
            onSkillChange={setSelectedSkill}
          />
        </div>
        
        <div className="w-3/4">
          <div className="mb-6">
            <HistoryStats items={filteredHistory} />
          </div>
          
          <div>
            {filteredHistory.length === 0 ? (
              <p className="text-gray-500 text-center py-8">
                Đã làm bài nào đâu mà xem lịch sử???
              </p>
            ) : (
              filteredHistory.map((item) => (
                <HistoryCard key={item.id} item={item} />
              ))
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default HistoryPage;
