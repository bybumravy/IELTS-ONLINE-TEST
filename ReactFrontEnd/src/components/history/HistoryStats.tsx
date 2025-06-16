import React from 'react';
import {
  EmojiEvents as TrophyIcon,
  TrendingUp as TrendingIcon,
  Star as StarIcon,
} from '@mui/icons-material';
import type { TestHistory } from '../../services/historyService';

interface HistoryStatsProps {
  items: TestHistory[];
}

const HistoryStats: React.FC<HistoryStatsProps> = ({ items }) => {
  const totalTests = items.length;
  const averageScore = items.reduce((acc, item) => acc + (item.score / item.maxScore) * 100, 0) / totalTests || 0;
  const highestScore = Math.max(...items.map(item => (item.score / item.maxScore) * 100), 0);

  const StatCard = ({ title, value, icon, color }: {
    title: string;
    value: string;
    icon: React.ReactNode;
    color: string;
  }) => (
    <div className={`flex flex-col items-center bg-white rounded-lg shadow p-4 border`} style={{ borderColor: color + '30', backgroundColor: color + '10' }}>
      <div className="mb-2" style={{ color }}>{icon}</div>
      <div className="text-3xl font-bold mb-1">{value}</div>
      <div className="text-gray-500 text-sm">{title}</div>
    </div>
  );

  return (
    <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
      <StatCard
        title="Total Tests"
        value={totalTests.toString()}
        icon={<TrophyIcon />}
        color="#2196f3"
      />
      <StatCard
        title="Average Score"
        value={`${averageScore.toFixed(1)}%`}
        icon={<TrendingIcon />}
        color="#4caf50"
      />
      <StatCard
        title="Highest Score"
        value={`${highestScore.toFixed(1)}%`}
        icon={<StarIcon />}
        color="#ff9800"
      />
    </div>
  );
};

export default HistoryStats; 