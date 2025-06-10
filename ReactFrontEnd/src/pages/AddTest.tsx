import type { FC } from 'react';
import { useState, useEffect } from 'react';
import type { Test, Task } from '@/types/apiTypes';
import { MAX_QUESTIONS_PER_SKILL, MIN_OPTIONS } from '@/types/apiTypes';
import { AddListening } from '@/components/sections/addTest/AddListening';
import { AddWriting } from '@/components/sections/addTest/AddWriting';
import { AddSpeaking } from '@/components/sections/addTest/AddSpeaking';
import { AddReading } from '@/components/sections/addTest/AddReading';

interface TestDataState extends Test {
  newTag: string;
}

const skillTabs = [
  { id: 'general', label: 'General Info' } as const,
  { id: 'listening', label: 'Listening' } as const,
  { id: 'reading', label: 'Reading' } as const,
  { id: 'writing', label: 'Writing' } as const,
  { id: 'speaking', label: 'Speaking' } as const,
];

type Skill = typeof skillTabs[number]['id'];

const AddTest: FC = () => {
  const [testData, setTestData] = useState<TestDataState>({
    testId: '',
    title: '',
    description: '',
    tags: [],
    createdAt: '',
    updatedAt: '',
    listening: [],
    reading: [],
    writing: [],
    speaking: [],
    newTag: '',
  });
  const [activeTab, setActiveTab] = useState<Skill>('general');

  useEffect(() => {
    const generateTestId = async () => {
      const testCount = 0; // Thay bằng API call nếu cần
      setTestData((prev) => ({
        ...prev,
        testId: `TEST${String(testCount + 1).padStart(4, '0')}`,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      }));
    };
    generateTestId();
  }, []);

  const validateTest = () => {
    const skillCounts = { listening: 0, writing: 0, speaking: 0 };
    
    Object.entries(skillCounts).forEach(([skill, _]) => {
      const tasks = testData[skill as keyof typeof skillCounts] as Task[];
      tasks?.forEach((task) => {
        task.sections?.forEach((section) => {
          skillCounts[skill as keyof typeof skillCounts] += section.questions?.length || 0;
        });
      });
    });

    const invalidSkills = Object.entries(skillCounts)
      .filter(([_, count]) => count !== MAX_QUESTIONS_PER_SKILL)
      .map(([skill, count]) => `${skill} (${count}/${MAX_QUESTIONS_PER_SKILL})`);

    if (invalidSkills.length) {
      alert(`Each skill must have exactly ${MAX_QUESTIONS_PER_SKILL} questions. Invalid: ${invalidSkills.join(', ')}`);
      return false;
    }

    let hasInvalidOptions = false;
    const skillsToCheck: (keyof Test)[] = ['listening'];
    skillsToCheck.forEach((skill) => {
      const tasks = testData[skill] as Task[];
      tasks?.forEach((task) => {
        task.sections?.forEach((section) => {
          section.questions?.forEach((question) => {
            if (question.options?.length < MIN_OPTIONS) {
              hasInvalidOptions = true;
            }
          });
        });
      });
    });

    if (hasInvalidOptions) {
      alert(`All questions must have at least ${MIN_OPTIONS} options.`);
      return false;
    }

    return true;
  };

  const handleSave = async () => {
    if (!validateTest()) return;

    try {
      const updatedTestData = { ...testData, updatedAt: new Date().toISOString() };
      const collections = {
        test: updatedTestData,
        listening: updatedTestData.listening,
        writing: updatedTestData.writing,
        speaking: updatedTestData.speaking,
      };

      Object.entries(collections).forEach(([name, data]) => {
        const jsonData = JSON.stringify(data, null, 2);
        const blob = new Blob([jsonData], { type: 'application/json' });
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `${updatedTestData.testId}_${name}.json`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
      });

      alert('Test saved successfully!');
    } catch (error) {
      console.error('Error saving test:', error);
      alert('Error saving test.');
    }
  };

  const addTag = () => {
    if (testData.newTag.trim()) {
      setTestData((prev) => ({
        ...prev,
        tags: [...prev.tags, prev.newTag.trim()],
        newTag: '',
      }));
    }
  };

  const removeTag = (index: number) => {
    setTestData((prev) => ({
      ...prev,
      tags: prev.tags.filter((_, i) => i !== index),
    }));
  };

  const handleInputChange = (field: keyof TestDataState, value: string) => {
    setTestData((prev) => ({ ...prev, [field]: value }));
  };

  const renderTabContent = () => {
    switch (activeTab) {
      case 'general':
        return (
          <div className="space-y-4">
            <div className="flex items-center gap-4">
              <label className="w-24 font-semibold">Test ID:</label>
              <input
                type="text"
                value={testData.testId}
                disabled
                className="flex-1 rounded border px-3 py-2 bg-gray-100"
              />
            </div>
            <div className="flex items-center gap-4">
              <label className="w-24 font-semibold">Test Title:</label>
              <input
                type="text"
                value={testData.title}
                onChange={(e) => handleInputChange('title', e.target.value)}
                className="flex-1 rounded border px-3 py-2 focus:ring-2 focus:ring-blue-200 focus:border-blue-400"
              />
            </div>
            <div className="flex items-center gap-4">
              <label className="w-24 font-semibold">Created At:</label>
              <input
                type="date"
                value={testData.createdAt.split('T')[0]}
                readOnly
                className="flex-1 rounded border px-3 py-2 bg-gray-100"
              />
            </div>
            <div className="flex items-center gap-4">
              <label className="w-24 font-semibold">Tags:</label>
              <div className="flex-1">
                <div className="flex flex-wrap gap-2 mb-2">
                  {testData.tags.map((tag, index) => (
                    <span
                      key={index}
                      className="bg-blue-100 text-blue-800 px-2 py-1 rounded-full text-sm flex items-center"
                    >
                      {tag}
                      <button onClick={() => removeTag(index)} className="ml-2 text-blue-600 hover:text-blue-800">
                        ×
                      </button>
                    </span>
                  ))}
                </div>
                <div className="flex gap-2">
                  <input
                    type="text"
                    value={testData.newTag}
                    onChange={(e) => handleInputChange('newTag', e.target.value)}
                    className="flex-1 rounded border px-3 py-2 focus:ring-2 focus:ring-blue-200 focus:border-blue-400"
                    placeholder="Add a tag"
                  />
                  <button
                    onClick={addTag}
                    className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
                  >
                    +
                  </button>
                </div>
              </div>
            </div>
          </div>
        );
      case 'listening':
        return <AddListening />;
      case 'reading':
        return <AddReading />;
      case 'writing':
        return <AddWriting />;
      case 'speaking':
        return <AddSpeaking />;
      default:
        return null;
    }
  };

  return (
    <div className="bg-gray-100 min-h-screen p-6 font-sans">
      <div className="max-w-4xl mx-auto bg-white rounded-lg shadow-md p-6">
        <h1 className="text-center text-2xl font-bold mb-6">ADD TEST</h1>

        <div className="flex border-b mb-6">
          {skillTabs.map((tab) => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`px-4 py-2 font-medium transition-all ${
                activeTab === tab.id ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-500 hover:text-gray-700'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </div>

        <div className="mb-8">{renderTabContent()}</div>

        <div className="flex justify-between">
          <button
            onClick={() => {
              const currentIndex = skillTabs.findIndex((tab) => tab.id === activeTab);
              if (currentIndex > 0) setActiveTab(skillTabs[currentIndex - 1].id);
            }}
            className={`px-4 py-2 rounded-lg ${
              activeTab === 'general'
                ? 'bg-gray-300 cursor-not-allowed'
                : 'bg-gray-500 text-white hover:bg-gray-600'
            }`}
            disabled={activeTab === 'general'}
          >
            Previous
          </button>

          {activeTab === 'speaking' ? (
            <button
              onClick={handleSave}
              className="bg-green-500 text-white px-6 py-2 rounded-lg hover:bg-green-600"
            >
              Save Test
            </button>
          ) : (
            <button
              onClick={() => {
                const currentIndex = skillTabs.findIndex((tab) => tab.id === activeTab);
                if (currentIndex < skillTabs.length - 1) setActiveTab(skillTabs[currentIndex + 1].id);
              }}
              className="bg-blue-500 text-white px-6 py-2 rounded-lg hover:bg-blue-600"
            >
              Next
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

export default AddTest;