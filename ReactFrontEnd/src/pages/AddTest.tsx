import type { FC } from 'react';
import { useState, useEffect } from 'react';
import type { Question, Section, Task, Test, QuestionField, QuestionValue, QuestionUpdateHandler } from '../types/test';
import { MAX_QUESTIONS_PER_SKILL, MIN_OPTIONS, skillColors } from '../types/test';
import { AddListening } from '../components/test/AddListening';
import { AddReading } from '../components/test/AddReading';
import { AddWriting } from '../components/test/AddWriting';
import { AddSpeaking } from '../components/test/AddSpeaking';
import { motion, AnimatePresence } from 'framer-motion';
import RichTextEditor from '../components/RichTextEditor';

interface QuestionFormProps {
  question: Question;
  onUpdate: (field: keyof Question, value: QuestionValue) => void;
  skillType: keyof typeof skillColors;
}

const QuestionForm: FC<QuestionFormProps> = ({ 
  question, 
  onUpdate, 
  skillType 
}) => {
  const addOption = () => {
    const newOptions = [...question.options, ''];
    onUpdate('options', newOptions);
  };

  const removeOption = (indexToRemove: number) => {
    if (question.options.length <= MIN_OPTIONS) {
      alert(`Minimum ${MIN_OPTIONS} options required`);
      return;
    }
    const newOptions = question.options.filter((_, index) => index !== indexToRemove);
    onUpdate('options', newOptions);
  };

  return (
    <motion.div 
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: -20 }}
      className="bg-white p-4 rounded-lg mb-4 shadow-sm"
    >
      <h5 className="font-medium mb-3 font-sans">Question {question.questionNumber}</h5>
      <div className="space-y-4">
        <div>
          <label className="block font-medium mb-2 font-sans">Question:</label>
          <input 
            type="text" 
            className="w-full rounded border px-3 py-2 focus:ring-2 focus:ring-blue-200 focus:border-blue-400 transition-all"
            placeholder="Enter question"
            value={question.question}
            onChange={(e) => onUpdate('question', e.target.value)}
          />
        </div>
        
        <div>
          <label className="block font-medium mb-2 font-sans">Answer:</label>
          <input 
            type="text" 
            className="w-full rounded border px-3 py-2 focus:ring-2 focus:ring-blue-200 focus:border-blue-400 transition-all"
            placeholder="Enter answer"
            value={question.answer}
            onChange={(e) => onUpdate('answer', e.target.value)}
          />
        </div>
        
        <div>
          <label className="block font-medium mb-2 font-sans">
            Explanation:
            <button
              type="button"
              onClick={() => onUpdate('isRichText', !question.isRichText)}
              className="ml-2 px-2 py-1 text-sm rounded bg-gray-100 hover:bg-gray-200"
            >
              {question.isRichText ? 'Switch to Plain Text' : 'Switch to Rich Text'}
            </button>
          </label>
          {question.isRichText ? (
            <RichTextEditor
              value={question.explanation}
              onChange={(value) => onUpdate('explanation', value)}
            />
          ) : (
            <textarea 
              className="w-full rounded border px-3 py-2 focus:ring-2 focus:ring-blue-200 focus:border-blue-400 transition-all"
              rows={3}
              placeholder="Enter explanation"
              value={question.explanation}
              onChange={(e) => onUpdate('explanation', e.target.value)}
            />
          )}
        </div>
        
        <div>
          <label className="block font-medium mb-2 font-sans">Options:</label>
          <div className="space-y-2">
            <AnimatePresence>
              {question.options.map((option, optIndex) => (
                <motion.div
                  key={`option-${optIndex}`}
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  exit={{ opacity: 0, x: 20 }}
                  className="flex items-center gap-2"
                >
                  <input
                    type="text"
                    className="flex-1 rounded border px-3 py-2 focus:ring-2 focus:ring-blue-200 focus:border-blue-400 transition-all"
                    placeholder={`Option ${optIndex + 1}`}
                    value={option}
                    onChange={(e) => {
                      const newOptions = [...question.options];
                      newOptions[optIndex] = e.target.value;
                      onUpdate('options', newOptions);
                    }}
                  />
                  <button
                    type="button"
                    onClick={() => removeOption(optIndex)}
                    className="p-2 text-red-500 hover:text-red-700"
                  >
                    ×
                  </button>
                </motion.div>
              ))}
            </AnimatePresence>
          </div>
          <button
            type="button"
            onClick={addOption}
            className={`mt-2 px-4 py-2 ${skillColors[skillType].button} rounded ${skillColors[skillType].buttonHover} transition-all font-sans`}
          >
            + Add Option
          </button>
        </div>
      </div>
    </motion.div>
  );
};

const SectionComponent = ({
  section,
  taskNum,
  skillType,
  onMethodChange,
  onAddQuestion,
  onUpdateQuestion
}: {
  section: Section;
  taskNum: number;
  skillType: keyof typeof skillColors;
  onMethodChange: (method: string) => void;
  onAddQuestion: () => void;
  onUpdateQuestion: (questionIndex: number, field: keyof Question, value: QuestionValue) => void;
}) => (
  <div className={`${skillColors[skillType].section} p-4 rounded-lg mb-4`}>
    <h4 className="font-medium mb-3 font-sans">Section {section.sectionNumber}</h4>
    <div className="space-y-4">
      <div>
        <label className="block font-medium mb-2 font-sans">Method:</label>
        <select 
          className="w-full rounded border px-3 py-2 focus:ring-2 focus:ring-blue-200 focus:border-blue-400 transition-all"
          value={section.method}
          onChange={(e) => onMethodChange(e.target.value)}
        >
          <option value="">Select method</option>
          <option value="multiple-choice">Multiple Choice</option>
          <option value="fill-blank">Fill in the Blank</option>
          <option value="true-false">True/False</option>
        </select>
      </div>

      {section.questions.map((question, qIndex) => (
        <QuestionForm
          key={`question-${qIndex}`}
          question={question}
          skillType={skillType}
          onUpdate={(field, value) => onUpdateQuestion(qIndex, field, value)}
        />
      ))}

      <button 
        onClick={onAddQuestion}
        className={`w-full ${skillColors[skillType].button} rounded-lg p-2 text-gray-700 ${skillColors[skillType].buttonHover} transition-all font-sans`}
      >
        + Add Question
      </button>
    </div>
  </div>
);

interface SkillSectionProps {
  title: string;
  skillType: keyof typeof skillColors;
  taskCount: number;
  questionCount: number;
  sections: { [key: number]: Section[] };
  onAddSection: (taskNum: number) => void;
  onMethodChange: (taskNum: number, sectionNum: number, method: string) => void;
  onAddQuestion: (taskNum: number, sectionNum: number) => void;
  onUpdateQuestion: (taskNum: number, sectionNum: number, questionIndex: number, field: keyof Question, value: QuestionValue) => void;
  children?: React.ReactNode;
}

const SkillSection: React.FC<SkillSectionProps> = ({
  title,
  skillType,
  taskCount,
  questionCount,
  sections,
  onAddSection,
  onMethodChange,
  onAddQuestion,
  onUpdateQuestion,
  children
}) => (
  <div className="mb-8">
    <h2 className={`text-xl font-semibold mb-4 ${skillColors[skillType].bg} p-3 rounded font-sans`}>
      {title} (Question {questionCount})
    </h2>
    <div className="space-y-6">
      {Array.from({ length: taskCount }, (_, i) => i + 1).map((taskNum) => (
        <div key={`${skillType}-task-${taskNum}`} className="border rounded-lg p-4">
          <h3 className="font-semibold mb-4 font-sans">Task {taskNum}</h3>
          {children}
          
          {sections[taskNum].map((section) => (
            <SectionComponent
              key={`${skillType}-section-${taskNum}-${section.sectionNumber}`}
              section={section}
              taskNum={taskNum}
              skillType={skillType}
              onMethodChange={(method) => onMethodChange(taskNum, section.sectionNumber, method)}
              onAddQuestion={() => onAddQuestion(taskNum, section.sectionNumber)}
              onUpdateQuestion={(qIndex, field, value) => 
                onUpdateQuestion(taskNum, section.sectionNumber, qIndex, field, value)
              }
            />
          ))}
          
          <button 
            onClick={() => onAddSection(taskNum)}
            className={`w-full ${skillColors[skillType].button} rounded-lg p-2 text-gray-700 ${skillColors[skillType].buttonHover} transition-all font-sans`}
          >
            + Add Section
          </button>
        </div>
      ))}
    </div>
  </div>
);

type Skill = 'general' | 'listening' | 'reading' | 'writing' | 'speaking';

const skillTabs: { id: Skill; label: string }[] = [
  { id: 'general', label: 'General Info' },
  { id: 'listening', label: 'Listening' },
  { id: 'reading', label: 'Reading' },
  { id: 'writing', label: 'Writing' },
  { id: 'speaking', label: 'Speaking' }
];

interface TestDataState extends Test {
  newTag: string;
}

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
    newTag: ''
  });
  const [activeTab, setActiveTab] = useState<Skill>('general');

  useEffect(() => {
    // Generate testId based on the number of tests
    const generateTestId = async () => {
      try {
        // This will be replaced with actual API call
        const testCount = 0; // await getTestCount();
        setTestData(prev => ({
          ...prev,
          testId: `TEST${String(testCount + 1).padStart(4, '0')}`,
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString()
        }));
      } catch (error) {
        console.error('Error generating test ID:', error);
      }
    };

    generateTestId();
  }, []);

  const validateTest = () => {
    const skillCounts = {
      listening: 0,
      reading: 0,
      writing: 0,
      speaking: 0
    };

    // Count questions for each skill
    Object.entries(skillCounts).forEach(([skill, _]) => {
      testData[skill as keyof typeof skillCounts].forEach((task: Task) => {
        task.sections.forEach((section: Section) => {
          skillCounts[skill as keyof typeof skillCounts] += section.questions.length;
        });
      });
    });

    // Validate question counts
    const invalidSkills = Object.entries(skillCounts)
      .filter(([_, count]) => count !== MAX_QUESTIONS_PER_SKILL)
      .map(([skill, count]) => `${skill} (${count}/${MAX_QUESTIONS_PER_SKILL})`);

    if (invalidSkills.length > 0) {
      alert(`Each skill must have exactly ${MAX_QUESTIONS_PER_SKILL} questions. Invalid skills: ${invalidSkills.join(', ')}`);
      return false;
    }

    // Validate options
    let hasInvalidOptions = false;
    Object.values(testData).forEach(tasks => {
      if (Array.isArray(tasks)) {
        tasks.forEach((task: Task) => {
          task.sections.forEach((section: Section) => {
            section.questions.forEach((question: Question) => {
              if (question.options.length < MIN_OPTIONS) {
                hasInvalidOptions = true;
              }
            });
          });
        });
      }
    });

    if (hasInvalidOptions) {
      alert(`All questions must have at least ${MIN_OPTIONS} options.`);
      return false;
    }

    return true;
  };

  const handleSave = async () => {
    if (!validateTest()) {
      return;
    }

    try {
      // Update timestamps
      const now = new Date().toISOString();
      const updatedTestData = {
        ...testData,
        updatedAt: now
      };

      // Save test data to JSON files
      const collections = {
        test: updatedTestData,
        listening: updatedTestData.listening,
        reading: updatedTestData.reading,
        speaking: updatedTestData.speaking
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
      alert('Error saving test. Please try again.');
    }
  };

  const addTag = () => {
    if (testData.newTag.trim() !== '') {
      setTestData(prev => ({
        ...prev,
        tags: [...prev.tags, prev.newTag.trim()],
        newTag: ''
      }));
    }
  };

  const removeTag = (indexToRemove: number) => {
    setTestData(prev => ({
      ...prev,
      tags: prev.tags.filter((_, index) => index !== indexToRemove)
    }));
  };

  const handleInputChange = (field: keyof typeof testData, value: string) => {
    setTestData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const getCurrentDate = () => {
    const today = new Date();
    return today.toISOString().split('T')[0];
  };

  const renderTabContent = () => {
    switch (activeTab) {
      case 'general':
        return (
          <div className="space-y-4">
            <div className="flex items-center space-x-4">
              <label className="w-24 font-semibold">Test ID:</label>
              <input 
                type="text" 
                value={testData.testId}
                disabled
                className="flex-1 rounded border px-3 py-2 bg-gray-100"
              />
            </div>
            <div className="flex items-center space-x-4">
              <label className="w-24 font-semibold">Test Title:</label>
              <input 
                type="text" 
                value={testData.title}
                onChange={(e) => handleInputChange('title', e.target.value)}
                className="flex-1 rounded border px-3 py-2 focus:ring-2 focus:ring-blue-200 focus:border-blue-400 transition-all" 
              />
            </div>
            <div className="flex items-center space-x-4">
              <label className="w-24 font-semibold">Create at:</label>
              <input 
                type="date" 
                className="flex-1 rounded border px-3 py-2 focus:ring-2 focus:ring-blue-200 focus:border-blue-400 transition-all" 
                defaultValue={getCurrentDate()}
                readOnly
              />
            </div>
            <div className="space-y-2">
              <div className="flex items-center space-x-4">
                <label className="w-24 font-semibold">Tags:</label>
                <div className="flex-1">
                  <div className="flex flex-wrap gap-2 mb-2">
                    {testData.tags.map((tag, index) => (
                      <span 
                        key={index} 
                        className="bg-blue-100 text-blue-800 px-2 py-1 rounded-full text-sm flex items-center"
                      >
                        {tag}
                        <button 
                          onClick={() => removeTag(index)}
                          className="ml-2 text-blue-600 hover:text-blue-800"
                        >
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
                      className="flex-1 rounded border px-3 py-2 focus:ring-2 focus:ring-blue-200 focus:border-blue-400 transition-all"
                      placeholder="Add a tag"
                    />
                    <button
                      onClick={addTag}
                      className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition-all"
                    >
                      +
                    </button>
                  </div>
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
        <h1 className="text-center text-2xl font-bold mb-6 font-sans">ADD TEST</h1>

        {/* Navigation Tabs */}
        <div className="flex border-b mb-6">
          {skillTabs.map((tab) => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`px-4 py-2 font-medium transition-all ${
                activeTab === tab.id
                  ? 'text-blue-600 border-b-2 border-blue-600'
                  : 'text-gray-500 hover:text-gray-700'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </div>

        {/* Tab Content */}
        <div className="mb-8">
          {renderTabContent()}
        </div>

        {/* Navigation Buttons */}
        <div className="flex justify-between">
          <button
            onClick={() => {
              const currentIndex = skillTabs.findIndex(tab => tab.id === activeTab);
              if (currentIndex > 0) {
                setActiveTab(skillTabs[currentIndex - 1].id);
              }
            }}
            className={`px-4 py-2 rounded-lg transition-all ${
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
              className="bg-green-500 text-white px-6 py-2 rounded-lg hover:bg-green-600 transition-all font-sans"
            >
              Save Test
            </button>
          ) : (
            <button
              onClick={() => {
                const currentIndex = skillTabs.findIndex(tab => tab.id === activeTab);
                if (currentIndex < skillTabs.length - 1) {
                  setActiveTab(skillTabs[currentIndex + 1].id);
                }
              }}
              className="bg-blue-500 text-white px-6 py-2 rounded-lg hover:bg-blue-600 transition-all font-sans"
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
  