// SectionComponent.tsx
import type { FC } from 'react';
import type { Question, Section, QuestionValue } from '@/types/apiTypes';
import { skillColors } from '@/types/apiTypes';
import { QuestionForm } from './QuestionForm';

export interface SectionComponentProps {
  section: Section;
  taskNum: number;
  skillType: keyof typeof skillColors;
  onMethodChange: (method: string) => void;
  onAddQuestion: () => void;
  onUpdateQuestion: (questionIndex: number, field: keyof Question, value: QuestionValue) => void;
  onDeleteQuestion: (questionIndex: number) => void; // 🆕 thêm dòng này
  onUpdateIntroduction: (introduction: string) => void;
  onUpdateParagraph?: (content: string) => void;
  onUpdateImage?: (file: File | null) => void;
  onDeleteSection: () => void;
}

export const SectionComponent: FC<SectionComponentProps> = ({
  section,
  skillType,
  onMethodChange,
  onAddQuestion,
  onUpdateQuestion,
  onDeleteQuestion, // 🆕 destructure mới
  onUpdateIntroduction,
  onUpdateImage,
  onDeleteSection,
}) => {
  const isMaxQuestions = section.questions.length >= 40;

  return (
    <div className={`${skillColors[skillType].section} p-4 rounded-lg mb-4 relative`}>
      <button
        type="button"
        onClick={onDeleteSection}
        className="absolute top-2 right-2 text-red-500 hover:text-red-700 text-xl font-bold"
        title="Delete Section"
      >
        ×
      </button>
      <h4 className="font-medium mb-3 font-sans">Section {section.sectionNumber}</h4>

      <div className="space-y-4">
        {/* Introduction */}
        <div>
          <label className="block font-medium mb-2 font-sans">Introduction:</label>
          <textarea
            className="w-full rounded border px-3 py-2 focus:ring-2 focus:ring-blue-200 focus:border-blue-400 transition-all"
            value={section.introduction}
            onChange={(e) => onUpdateIntroduction(e.target.value)}
            rows={4}
            placeholder="Enter introduction text..."
          />
        </div>

        {/* Image upload */}
        {skillType === 'listening' && section.method === 'map-labeling' && (
          <div>
            <label className="block font-medium mb-2 font-sans">Image:</label>
            <input
              type="file"
              accept="image/*"
              className="w-full"
              onChange={(e) => onUpdateImage?.(e.target.files?.[0] || null)}
            />
          </div>
        )}

        {/* Method */}
        <div>
          <label className="block font-medium mb-2 font-sans">Method:</label>
          <select
            className="w-full rounded border px-3 py-2 focus:ring-2 focus:ring-blue-200 focus:border-blue-400 transition-all"
            value={section.method}
            onChange={(e) => onMethodChange(e.target.value)}
          >
            <option value="multiple-choice">Multiple Choice</option>
            {/* Option rendering logic */}
            {/* ... */}
          </select>
        </div>

        {/* Questions */}
        {section.questions.map((question, qIndex) => (
          <QuestionForm
            key={`question-${qIndex}`}
            question={question}
            skillType={skillType}
            method={section.method}
            onUpdate={(field, value) => onUpdateQuestion(qIndex, field, value)}
            onDelete={() => onDeleteQuestion(qIndex)} // 🆕 truyền vào đúng cách
          />
        ))}

        <button
          type="button"
          onClick={onAddQuestion}
          className={`w-full ${skillColors[skillType].button} rounded-lg p-2 text-gray-700 ${skillColors[skillType].buttonHover} transition-all font-sans`}
          disabled={isMaxQuestions}
        >
          + Add Question {isMaxQuestions ? '(Max Reached)' : ''}
        </button>
      </div>
    </div>
  );
};
