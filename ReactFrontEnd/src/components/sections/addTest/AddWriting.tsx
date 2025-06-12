import type { FC } from 'react';
import { useState, useEffect } from 'react';
// import type { Task } from '@/types/apiTypes';
import { skillColors } from '@/types/apiTypes';

interface WritingTask {
  prompt: string;
  imageFile?: File;
}

const WRITING_AUTOSAVE_KEY = 'test_autosave_writing';

export const AddWriting: FC = () => {
  const [tasks, setTasks] = useState<WritingTask[]>(() => {
    // Try to load saved writing data
    const savedData = localStorage.getItem(WRITING_AUTOSAVE_KEY);
    if (savedData) {
      try {
        const parsedData = JSON.parse(savedData);
        // Since we can't store File objects in localStorage, we need to handle them separately
        return parsedData.map((task: WritingTask) => ({
          prompt: task.prompt,
          imageFile: undefined // Reset imageFile as it can't be stored in localStorage
        }));
      } catch (e) {
        console.error('Error loading autosaved writing data:', e);
      }
    }
    // Return default state if no saved data
    return [
      { prompt: '' },
      { prompt: '' }
    ];
  });

  // Auto-save effect
  useEffect(() => {
    try {
      // Store only the data that can be serialized
      const dataToStore = tasks.map(task => ({
        prompt: task.prompt
      }));
      localStorage.setItem(WRITING_AUTOSAVE_KEY, JSON.stringify(dataToStore));
    } catch (e) {
      console.error('Error auto-saving writing data:', e);
    }
  }, [tasks]);

  const handlePromptChange = (taskIndex: number, prompt: string) => {
    const updatedTasks = [...tasks];
    updatedTasks[taskIndex] = { ...updatedTasks[taskIndex], prompt };
    setTasks(updatedTasks);
  };

  const handleImageChange = (taskIndex: number, file: File | undefined) => {
    const updatedTasks = [...tasks];
    updatedTasks[taskIndex] = { ...updatedTasks[taskIndex], imageFile: file };
    setTasks(updatedTasks);
  };

  return (
    <div className={`mb-8 ${skillColors.writing.bg} rounded-lg p-4`}>
      <h2 className="text-xl font-semibold mb-4 font-sans">Writing</h2>
      <div className="space-y-6">
        {tasks.map((task, taskIndex) => (
          <div key={`writing-task-${taskIndex + 1}`} className="border rounded-lg p-4 bg-white">
            <h3 className="font-semibold mb-4 font-sans">Task {taskIndex + 1}</h3>
            <div className="space-y-4">
              <textarea
                className="w-full rounded border px-3 py-2 focus:ring-2 focus:ring-blue-200 focus:border-blue-400 transition-all"
                rows={4}
                placeholder="Enter task prompt"
                value={task.prompt}
                onChange={(e) => handlePromptChange(taskIndex, e.target.value)}
              />
              {taskIndex === 0 && (
                <div>
                  <label className="block font-medium mb-2 font-sans">Supporting Image:</label>
                  <input
                    type="file" 
                    accept="image/*" 
                    className="w-full"
                    onChange={(e) => handleImageChange(taskIndex, e.target.files?.[0])}
                  />
                </div>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}; 