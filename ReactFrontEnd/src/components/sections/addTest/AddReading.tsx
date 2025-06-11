import type { FC } from 'react';
import { useState } from 'react';
import type { Question, Section, QuestionValue } from '@/types/apiTypes';
import { skillColors } from '@/types/apiTypes';
import { SectionComponent } from './SectionComponent';

export const AddReading: FC = () => {
  const [sections, setSections] = useState<{ [key: number]: Section[] }>({
    1: [{ sectionNumber: 1, introduction: '', questions: [], type: '' }],
    2: [{ sectionNumber: 1, introduction: '', questions: [], type: '' }],
    3: [{ sectionNumber: 1, introduction: '', questions: [], type: '' }]
  });

  const [paragraphs, setParagraphs] = useState<{ [key: number]: string }>({
    1: '',
    2: '',
    3: ''
  });

  const [questionCounter, setQuestionCounter] = useState(1);

  const handleAddSection = (taskNum: number) => {
    const currentSections = sections[taskNum];
    const newSectionNumber = currentSections.length + 1;
    setSections({
      ...sections,
      [taskNum]: [
        ...currentSections,
        { sectionNumber: newSectionNumber, introduction: '', questions: [], type: '' }
      ]
    });
  };

  const handleAddQuestion = (taskNum: number, sectionNum: number) => {
    const newQuestion: Question = {
      questionNumber: questionCounter,
      question: '',
      answer: '',
      explanation: '',
      options: ['', '', '', ''],
      isRichText: false
    };

    const updatedSections = [...sections[taskNum]];
    const sectionIndex = updatedSections.findIndex(s => s.sectionNumber === sectionNum);
    updatedSections[sectionIndex] = {
      ...updatedSections[sectionIndex],
      questions: [...updatedSections[sectionIndex].questions, newQuestion]
    };

    setSections({
      ...sections,
      [taskNum]: updatedSections
    });

    setQuestionCounter(prev => prev + 1);
  };

  const handleDeleteQuestion = (taskNum: number, sectionNum: number, questionIndex: number) => {
    const updatedSections = [...sections[taskNum]];
    const sectionIndex = updatedSections.findIndex(s => s.sectionNumber === sectionNum);

    const newQuestions = [...updatedSections[sectionIndex].questions];
    newQuestions.splice(questionIndex, 1);

    updatedSections[sectionIndex] = {
      ...updatedSections[sectionIndex],
      questions: newQuestions
    };

    setSections({
      ...sections,
      [taskNum]: updatedSections
    });
  };

  const handleDeleteSection = (taskNum: number, sectionNum: number) => {
    const updatedSections = sections[taskNum].filter(s => s.sectionNumber !== sectionNum);
    const newSections = {
      ...sections,
      [taskNum]: updatedSections
    };

    // Recalculate questionCounter
    let totalQuestions = 0;
    Object.values(newSections).forEach(taskSections => {
      taskSections.forEach(section => {
        totalQuestions += section.questions.length;
      });
    });

    setSections(newSections);
    setQuestionCounter(totalQuestions + 1);
  };

  const handleMethodChange = (taskNum: number, sectionNum: number, type: string) => {
    const updatedSections = [...sections[taskNum]];
    const sectionIndex = updatedSections.findIndex(s => s.sectionNumber === sectionNum);
    updatedSections[sectionIndex] = { ...updatedSections[sectionIndex], type };
    setSections({ ...sections, [taskNum]: updatedSections });
  };

  const handleUpdateQuestion = (
    taskNum: number,
    sectionNum: number,
    questionIndex: number,
    field: keyof Question,
    value: QuestionValue
  ) => {
    const updatedSections = [...sections[taskNum]];
    const sectionIndex = updatedSections.findIndex(s => s.sectionNumber === sectionNum);
    const updatedQuestions = [...updatedSections[sectionIndex].questions];
    updatedQuestions[questionIndex] = {
      ...updatedQuestions[questionIndex],
      [field]: value
    };
    updatedSections[sectionIndex] = {
      ...updatedSections[sectionIndex],
      questions: updatedQuestions
    };
    setSections({
      ...sections,
      [taskNum]: updatedSections
    });
  };

  const handleUpdateIntroduction = (taskNum: number, sectionNum: number, introduction: string) => {
    const updatedSections = [...sections[taskNum]];
    const sectionIndex = updatedSections.findIndex(s => s.sectionNumber === sectionNum);
    updatedSections[sectionIndex] = { ...updatedSections[sectionIndex], introduction };
    setSections({ ...sections, [taskNum]: updatedSections });
  };

  const handleUpdateTaskParagraph = (taskNum: number, content: string) => {
    setParagraphs({ ...paragraphs, [taskNum]: content });
  };

  return (
    <div className="mb-8">
      <h2 className={`text-xl font-semibold mb-4 ${skillColors.reading.bg} p-3 rounded font-sans`}>
        Reading (Question {questionCounter - 1})
      </h2>
      <div className="space-y-6">
        {[1, 2, 3].map((taskNum) => (
          <div key={`reading-task-${taskNum}`} className="border rounded-lg p-4">
            <h3 className="font-semibold mb-4 font-sans">Task {taskNum}</h3>

            {/* Paragraph input */}
            <div>
              <label className="block font-medium mb-2 font-sans">Paragraph:</label>
              <textarea
                className="w-full rounded border px-3 py-2 focus:ring-2 focus:ring-blue-200 focus:border-blue-400 transition-all"
                value={paragraphs[taskNum]}
                onChange={(e) => handleUpdateTaskParagraph(taskNum, e.target.value)}
                rows={4}
                placeholder="Enter paragraph text..."
              />
            </div>

            {sections[taskNum]?.map((section) => (
              <SectionComponent
                key={`reading-section-${taskNum}-${section.sectionNumber}`}
                section={section}
                taskNum={taskNum}
                skillType="reading"
                onMethodChange={(type: string) => handleMethodChange(taskNum, section.sectionNumber, type)}
                onAddQuestion={() => handleAddQuestion(taskNum, section.sectionNumber)}
                onUpdateQuestion={(qIndex: number, field: keyof Question, value: QuestionValue) =>
                  handleUpdateQuestion(taskNum, section.sectionNumber, qIndex, field, value)
                }
                onUpdateIntroduction={(introduction: string) =>
                  handleUpdateIntroduction(taskNum, section.sectionNumber, introduction)
                }
                onUpdateParagraph={() => {}}
                onDeleteSection={() => handleDeleteSection(taskNum, section.sectionNumber)}
                onDeleteQuestion={(qIndex) => handleDeleteQuestion(taskNum, section.sectionNumber, qIndex)} // ✅ Đúng rồi
              />
            ))}

            <button
              type="button"
              onClick={() => handleAddSection(taskNum)}
              className={`w-full ${skillColors.reading.button} rounded-lg p-2 text-gray-700 ${skillColors.reading.buttonHover} transition-all font-sans`}
            >
              + Add Section
            </button>
          </div>
        ))}
      </div>
    </div>
  );
};
