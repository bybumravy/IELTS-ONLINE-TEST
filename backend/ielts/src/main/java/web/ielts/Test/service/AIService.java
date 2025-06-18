package web.ielts.Test.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;
import web.ielts.Test.model.answer.writing.WritingAIResponse;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AIService {

    private final OpenAiChatModel chatModel;
    private final ObjectMapper objectMapper;

    public AIService(OpenAiChatModel chatModel, ObjectMapper objectMapper) {
        this.chatModel = chatModel;
        this.objectMapper = objectMapper;
    }


//    public String getFeedback(String studentAnswer) {
//        System.out.println("Loading...");
//        String prompt = """
//        You are an IELTS examiner. Please review the following writing task answer and give a detailed feedback focusing on grammar, coherence, lexical resource, and task response. Be constructive and concise.
//
//        Answer:
//        """ + studentAnswer;
//
//        // Gửi prompt và lấy content trả về
//        var response = chatModel
////                .prompt()
////                .user(prompt)
//                .call(prompt);
//        System.out.println(response.toString());
//        return response.toString();
//    }

    public WritingAIResponse WritingTask1(String question, String answer) {
        // Create a helper string with character positions for the AI
        String answerWithPositions = createAnswerWithPositions(answer);
        
        StringBuilder promptBuilder = new StringBuilder("""
    You must return response strictly in JSON format.
    You are an IELTS examiner. Review the following IELTS Writing Task 1 student answer based on the question provided and return a JSON object containing:
    - feedback: {
        - score: decimal (overall band score, e.g. 6.5)
        - errorCorrections: array of {
            - originalText: string (exact text as it appears in the answer)
            - correctedText: string
            - errorType: string (grammar, vocabulary, coherence, etc.)
            - explanation: string
            - startIndex: number (0-based character position where originalText starts)
            - endIndex: number (0-based character position immediately after originalText ends)
        }
        - sentenceImprovements: array of {
            - originalSentence: string
            - improvedSentence: string
            - techniquesUsed: array of strings (e.g. "academic vocab", "complex structure")
            - bandBoost: string (e.g. "5.5 → 6.5")
        }
        - overallComment: string
    }
    - evaluation: {
        - TaskAchievement: 1-9
        - CoherenceCohesion: 1-9
        - LexicalResource: 1-9
        - Grammar: 1-9
    }
    - sampleAnswer: string

    CRITICAL INSTRUCTIONS FOR CHARACTER POSITIONS:
    1. startIndex must be the exact 0-based character position where originalText begins in the Answer string
    2. endIndex must be the character position immediately after the last character of originalText
    3. originalText must be the EXACT text as it appears in the Answer (including spaces, punctuation, case)
    4. Verify that answer.substring(startIndex, endIndex) equals originalText exactly
    5. Count every character including spaces, newlines, and punctuation
    6. Use the character position reference below to find exact positions

    Question:
    """).append(question)
                .append("\nAnswer with character positions (for reference):\n")
                .append(answerWithPositions)
                .append("\n\nOriginal Answer:\n")
                .append(answer);

        String prompt = promptBuilder.toString();
        System.out.println("==== PROMPT GỬI AI TASK 1 ====");
        System.out.println(prompt);

        ChatResponse response = chatModel.call(new Prompt(new UserMessage(prompt)));
        String content = response.getResult().getOutput().getText();


        System.out.println("==== RESPONSE FROM AI ====");
        System.out.println(content);
        return parseResponse(content, answer);
    }

    private String createAnswerWithPositions(String answer) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < answer.length(); i++) {
            char c = answer.charAt(i);
            if (c == '\n') {
                sb.append("\\n[").append(i).append("]");
            } else if (c == ' ') {
                sb.append(" [").append(i).append("]");
            } else {
                sb.append(c).append("[").append(i).append("]");
            }
        }
        return sb.toString();
    }

    public WritingAIResponse WritingTask2(String question, String answer) {
        // Create a helper string with character positions for the AI
        String answerWithPositions = createAnswerWithPositions(answer);
        
        StringBuilder promptBuilder = new StringBuilder("""
    You must return response strictly in JSON format.
    You are an IELTS examiner. Review the following IELTS Writing Task 2 student answer based on the question provided and return a JSON object containing:
    - feedback: {
        - score: decimal (overall band score, e.g. 6.5)
        - errorCorrections: array of {
            - originalText: string (exact text as it appears in the answer)
            - correctedText: string
            - errorType: string (grammar, vocabulary, coherence, etc.)
            - explanation: string
            - startIndex: number (0-based character position where originalText starts)
            - endIndex: number (0-based character position immediately after originalText ends)
        }
        - sentenceImprovements: array of {
            - originalSentence: string
            - improvedSentence: string
            - techniquesUsed: array of strings (e.g. "academic vocab", "complex structure")
            - bandBoost: string (e.g. "5.5 → 6.5")
        }
        - overallComment: string
    }
    - evaluation: {
        - TaskAchievement: 1-9
        - CoherenceCohesion: 1-9
        - LexicalResource: 1-9
        - Grammar: 1-9
    }
    - sampleAnswer: string

    CRITICAL INSTRUCTIONS FOR CHARACTER POSITIONS:
    1. startIndex must be the exact 0-based character position where originalText begins in the Answer string
    2. endIndex must be the character position immediately after the last character of originalText
    3. originalText must be the EXACT text as it appears in the Answer (including spaces, punctuation, case)
    4. Verify that answer.substring(startIndex, endIndex) equals originalText exactly
    5. Count every character including spaces, newlines, and punctuation
    6. Use the character position reference below to find exact positions

    Question:
    """).append(question)
                .append("\nAnswer with character positions (for reference):\n")
                .append(answerWithPositions)
                .append("\n\nOriginal Answer:\n")
                .append(answer);



        String prompt = promptBuilder.toString();
        System.out.println("==== PROMPT GỬI AI TASK 2 ====");
        System.out.println(prompt);

        ChatResponse response = chatModel.call(new Prompt(new UserMessage(prompt)));
        String content = response.getResult().getOutput().getText();

        System.out.println("==== RESPONSE FROM AI ====");
        System.out.println(content);
        return parseResponse(content, answer);
    }
    private WritingAIResponse parseResponse(String content, String originalAnswer) {
        try {
            // Dùng regex để tìm đoạn JSON từ { đến } an toàn hơn
            Pattern pattern = Pattern.compile("\\{.*\\}", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(content);

            if (matcher.find()) {
                String jsonPart = matcher.group();

                System.out.println("==== JSON PART ====");
                System.out.println(jsonPart);

                // Parse JSON thành đối tượng Java
                WritingAIResponse response = objectMapper.readValue(jsonPart, WritingAIResponse.class);
                
                // Validate and correct character positions if feedback exists
                if (response.getFeedback() != null && response.getFeedback().getErrorCorrections() != null) {
                    validateErrorCorrections(response.getFeedback().getErrorCorrections());
                    validateAndCorrectPositions(response.getFeedback().getErrorCorrections(), originalAnswer);
                }
                
                return response;
            } else {
                throw new IllegalArgumentException("Không tìm thấy JSON hợp lệ trong phản hồi");
            }

        } catch (Exception e) {
            System.err.println("Lỗi khi parse response: " + e.getMessage());
            throw new RuntimeException("Không thể phân tích phản hồi từ AI", e);
        }
    }

    private void validateErrorCorrections(List<WritingAIResponse.ErrorCorrection> corrections) {
        for (int i = 0; i < corrections.size(); i++) {
            WritingAIResponse.ErrorCorrection correction = corrections.get(i);
            
            // Check if originalText is not empty
            if (correction.getOriginalText() == null || correction.getOriginalText().trim().isEmpty()) {
                System.err.println("Empty originalText for correction " + i);
                corrections.remove(i);
                i--; // Adjust index after removal
                continue;
            }
            
            // Check if indices are valid
            if (correction.getStartIndex() < 0 || correction.getEndIndex() <= correction.getStartIndex()) {
                System.err.println("Invalid indices for correction " + i + ": startIndex=" + 
                    correction.getStartIndex() + ", endIndex=" + correction.getEndIndex());
                // Reset to safe values - will be handled by frontend
                correction.setStartIndex(-1);
                correction.setEndIndex(-1);
            }
        }
    }

    /**
     * Validates and corrects error correction positions against the original answer text
     */
    private void validateAndCorrectPositions(List<WritingAIResponse.ErrorCorrection> corrections, String originalAnswer) {
        for (WritingAIResponse.ErrorCorrection correction : corrections) {
            if (correction.getStartIndex() >= 0 && correction.getEndIndex() > correction.getStartIndex()) {
                // Check if the text at the specified position matches originalText
                try {
                    String actualText = originalAnswer.substring(correction.getStartIndex(), correction.getEndIndex());
                    if (!actualText.equals(correction.getOriginalText())) {
                        System.err.println("Text mismatch for correction: expected='" + correction.getOriginalText() + 
                            "', actual='" + actualText + "' at positions " + correction.getStartIndex() + "-" + correction.getEndIndex());
                        
                        // Try to find the correct position
                        int correctStart = originalAnswer.indexOf(correction.getOriginalText());
                        if (correctStart != -1) {
                            int correctEnd = correctStart + correction.getOriginalText().length();
                            System.out.println("Corrected position: " + correctStart + "-" + correctEnd);
                            correction.setStartIndex(correctStart);
                            correction.setEndIndex(correctEnd);
                        } else {
                            // If text not found, mark as invalid
                            correction.setStartIndex(-1);
                            correction.setEndIndex(-1);
                        }
                    }
                } catch (StringIndexOutOfBoundsException e) {
                    System.err.println("Index out of bounds for correction: " + correction.getOriginalText());
                    correction.setStartIndex(-1);
                    correction.setEndIndex(-1);
                }
            }
        }
    }







}
