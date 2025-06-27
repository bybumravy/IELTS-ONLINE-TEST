import React from 'react';
import {
  ExpandMore as ExpandMoreIcon,
  Headphones as ListeningIcon,
  MenuBook as ReadingIcon,
  Edit as WritingIcon,
  RecordVoiceOver as SpeakingIcon,
} from '@mui/icons-material';
import { format } from 'date-fns';
import { vi } from 'date-fns/locale';
import type { TestHistory } from '../../services/historyService';

interface HistoryCardProps {
  item: TestHistory;
}

const HistoryCard: React.FC<HistoryCardProps> = ({ item }) => {
  const [expanded, setExpanded] = React.useState(false);

  const getSkillIcon = () => {
    switch (item.skill) {
      case 'listening':
        return <ListeningIcon className="text-blue-500" />;
      case 'reading':
        return <ReadingIcon className="text-green-500" />;
      case 'writing':
        return <WritingIcon className="text-yellow-500" />;
      case 'speaking':
        return <SpeakingIcon className="text-red-500" />;
      default:
        return null;
    }
  };

  const getSkillColor = () => {
    switch (item.skill) {
      case 'listening':
        return 'bg-blue-100 text-blue-700';
      case 'reading':
        return 'bg-green-100 text-green-700';
      case 'writing':
        return 'bg-yellow-100 text-yellow-700';
      case 'speaking':
        return 'bg-red-100 text-red-700';
      default:
        return 'bg-gray-100 text-gray-700';
    }
  };

  const getSkillText = () => {
    switch (item.skill) {
      case 'listening':
        return 'Nghe';
      case 'reading':
        return 'Đọc';
      case 'writing':
        return 'Viết';
      case 'speaking':
        return 'Nói';
      default:
        return '';
    }
  };

  const scorePercentage = (item.score / item.maxScore) * 100;
  const getBarColor = () => {
    if (scorePercentage >= 70) return 'bg-green-500';
    if (scorePercentage >= 50) return 'bg-yellow-500';
    return 'bg-red-500';
  };

  return (
    <div className="bg-white rounded-lg shadow p-4 mb-4 transition-transform hover:-translate-y-0.5 hover:shadow-lg">
      <div className="flex justify-between items-center">
        <div className="flex items-center gap-2">
          {getSkillIcon()}
          <span className="text-lg font-semibold">{item.testName}</span>
        </div>
        <div className="flex items-center gap-4">
          <span className={`px-2 py-1 rounded text-xs font-medium ${getSkillColor()}`}>{getSkillText()}</span>
          <span className="text-gray-500 text-sm">
            {format(new Date(item.submittedAt), 'dd/MM/yyyy HH:mm', { locale: vi })}
          </span>
          <button
            className={`transition-transform ${expanded ? 'rotate-180' : ''}`}
            onClick={() => setExpanded(!expanded)}
            aria-label="Xem thêm"
          >
            <ExpandMoreIcon />
          </button>
        </div>
      </div>

      <div className="mt-4">
        <div className="flex justify-between items-center mb-1">
          <span className="text-gray-500 text-sm">
            Điểm số: {item.score}/{item.maxScore}
          </span>
          <span className="text-gray-500 text-sm">
            {scorePercentage.toFixed(1)}%
          </span>
        </div>
        <div className="w-full h-2 bg-gray-200 rounded">
          <div
            className={`h-2 rounded ${getBarColor()}`}
            style={{ width: `${scorePercentage}%` }}
          ></div>
        </div>
      </div>

      {expanded && (
        <div className="mt-4">
          {item.feedback && (
            <span className="text-gray-600 text-sm">Phản hồi: {item.feedback}</span>
          )}
        </div>
      )}
    </div>
  );
};

export default HistoryCard; 