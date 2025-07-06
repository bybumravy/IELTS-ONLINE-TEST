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
  const [error, setError] = useState<string | null>(null);
  const { user } = useAuth();

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        setLoading(true);
        setError(null);
        if (user?.username) {
          const data = await getStudentTestHistory(user.username);
          // Sort by submittedAt descending (newest first)
          const sortedData = data.sort((a, b) => 
            new Date(b.submittedAt).getTime() - new Date(a.submittedAt).getTime()
          );
          setTestHistory(sortedData);
          setFilteredHistory(sortedData);
        } else {
          setError('User not authenticated');
        }
      } catch (error) {
        console.error('Error fetching test history:', error);
        setError('Failed to load test history. Please try again later.');
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
      const filtered = testHistory.filter(item => item.skill === selectedSkill);
      // Maintain sorting for filtered results
      const sortedFiltered = filtered.sort((a, b) => 
        new Date(b.submittedAt).getTime() - new Date(a.submittedAt).getTime()
      );
      setFilteredHistory(sortedFiltered);
    }
  }, [selectedSkill, testHistory]);

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-[60vh]">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="container mx-auto px-4 py-8">
        <h1 className="text-3xl font-bold mb-6">History Test</h1>
        <div className="bg-red-50 border border-red-200 rounded-lg p-4">
          <p className="text-red-700">{error}</p>
          <button 
            onClick={() => window.location.reload()} 
            className="mt-2 px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700"
          >
            Try Again
          </button>
        </div>
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
              <div className="text-center py-8">
                <p className="text-gray-500 mb-4">
                  {testHistory.length === 0 
                    ? "Bạn chưa làm bài test nào. Hãy bắt đầu với một bài test!"
                    : "Không có bài test nào cho kỹ năng này."
                  }
                </p>
                {testHistory.length === 0 && (
                  <button 
                    onClick={() => window.location.href = '/list-test'} 
                    className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                  >
                    Xem danh sách bài test
                  </button>
                )}
              </div>
            ) : (
              filteredHistory.map((item, index) => (
                <HistoryCard key={`${item.testID}-${item.skill}-${index}`} item={item} />
              ))
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default HistoryPage;
