import axios from 'axios';

export interface TestHistory {
  id: string;
  testId: string;
  testName: string;
  skill: 'listening' | 'reading' | 'writing' | 'speaking';
  score: number;
  maxScore: number;
  submittedAt: string;
  feedback?: string;
}

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

export const getStudentTestHistory = async (studentId: string, skill?: string): Promise<TestHistory[]> => {
  try {
    const url = skill 
      ? `${API_URL}/students/${studentId}/history?skill=${skill}`
      : `${API_URL}/students/${studentId}/history`;
    const response = await axios.get(url);
    return response.data;
  } catch (error) {
    console.error('Error fetching test history:', error);
    throw error;
  }
};

export const getTestDetails = async (testId: string): Promise<TestHistory> => {
  try {
    const response = await axios.get(`${API_URL}/tests/${testId}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching test details:', error);
    throw error;
  }
}; 