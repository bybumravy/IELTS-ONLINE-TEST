import React from 'react';
import { 
  Headphones as ListeningIcon,
  MenuBook as ReadingIcon,
  Edit as WritingIcon,
  Mic as SpeakingIcon,
  Apps as AllIcon
} from '@mui/icons-material';

interface HistoryFilterProps {
  selectedSkill: string;
  onSkillChange: (skill: string) => void;
}

const HistoryFilter: React.FC<HistoryFilterProps> = ({ selectedSkill, onSkillChange }) => {
  const handleSkillChange = (_event: React.MouseEvent<HTMLElement>, newSkill: string | null) => {
    if (newSkill !== null) {
      onSkillChange(newSkill);
    }
  };

  return (
    <div className="bg-white rounded-lg shadow p-4">
      <h2 className="text-lg font-semibold mb-4">Filter by Skill</h2>
      <div className="flex flex-col space-y-2">
        <button
          className={`flex items-center px-4 py-2 rounded-lg transition-colors ${
            selectedSkill === 'all' 
              ? 'bg-blue-100 text-blue-700' 
              : 'hover:bg-gray-100'
          }`}
          onClick={(e) => handleSkillChange(e, 'all')}
        >
          <AllIcon className="mr-2" />
          All Tests
        </button>
        <button
          className={`flex items-center px-4 py-2 rounded-lg transition-colors ${
            selectedSkill === 'listening' 
              ? 'bg-blue-100 text-blue-700' 
              : 'hover:bg-gray-100'
          }`}
          onClick={(e) => handleSkillChange(e, 'listening')}
        >
          <ListeningIcon className="mr-2" />
          Listening
        </button>
        <button
          className={`flex items-center px-4 py-2 rounded-lg transition-colors ${
            selectedSkill === 'reading' 
              ? 'bg-blue-100 text-blue-700' 
              : 'hover:bg-gray-100'
          }`}
          onClick={(e) => handleSkillChange(e, 'reading')}
        >
          <ReadingIcon className="mr-2" />
          Reading
        </button>
        <button
          className={`flex items-center px-4 py-2 rounded-lg transition-colors ${
            selectedSkill === 'writing' 
              ? 'bg-blue-100 text-blue-700' 
              : 'hover:bg-gray-100'
          }`}
          onClick={(e) => handleSkillChange(e, 'writing')}
        >
          <WritingIcon className="mr-2" />
          Writing
        </button>
        <button
          className={`flex items-center px-4 py-2 rounded-lg transition-colors ${
            selectedSkill === 'speaking' 
              ? 'bg-blue-100 text-blue-700' 
              : 'hover:bg-gray-100'
          }`}
          onClick={(e) => handleSkillChange(e, 'speaking')}
        >
          <SpeakingIcon className="mr-2" />
          Speaking
        </button>
      </div>
    </div>
  );
};

export default HistoryFilter; 