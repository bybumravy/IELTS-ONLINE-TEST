package web.ielts.Test.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;
import web.ielts.Test.model.answer.writing.WritingAIResponse;

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
        StringBuilder promptBuilder = new StringBuilder("""
        You must to use JSON format to return response.
            You are an IELTS examiner. Review the following Task 1 writing and return a JSON object with:
        - feedback: {
            - score: 6.5
            - errorCorrections: array of objects with {
                - originalText: string (text with error, mark errors with **),
                - correctedText: string,
                - errorType: string (spelling/grammar/vocabulary/linking),
                - explanation: string
            }
            - sentenceImprovements: array of objects with {
                - originalSentence: string,
                - improvedSentence: string,
                - techniquesUsed: array of strings (academic vocab/complex structures/logical flow),
                - bandBoost: string (e.g. "5.0 → 6.0")
            }
            - overallComment: string
        }
        - evaluation: {
            - TaskAchievement: number 1-9,
            - CoherenceCohesion: number 1-9,
            - LexicalResource: number 1-9,
            - Grammar: number 1-9
        }
        - sampleAnswer: string

        Instructions:
        1. For ERROR CORRECTIONS:
            - Mark errors with **double asterisks**
            - Example: "The **datas** shows" → "The data show" (grammar)
    
        2. For SENTENCE IMPROVEMENTS:
            - Use academic vocabulary: "go up" → "increase steadily"
            - Add complex structures: "This changed" → "This underwent a significant transformation"
            - Improve logic: Add linking words like "Consequently," 
            - Specify band score improvement

        Example:
        {
          "score": 6.5 ,
          "feedback": {
            "errorCorrections": [
              {
                "originalText": "The **datas** shows",
                "correctedText": "The data show",
                "errorType": "grammar",
                "explanation": "Subject-verb agreement error"
              }
            ],
            "sentenceImprovements": [
              {
                "originalSentence": "The graph goes up",
                "improvedSentence": "The graph exhibits a steady upward trend, which indicates...",
                "techniquesUsed": ["academic vocab", "relative clause"],
                "bandBoost": "5.0 → 6.5"
              }
            ],
            "overallComment": "Good data coverage but needs more sophisticated language..."
          },
          "evaluation": {
            "TaskAchievement": 7,
            "CoherenceCohesion": 7,
            "LexicalResource": 6,
            "Grammar": 7
          },
          "sampleAnswer": "The bar chart illustrates..."
        }
    
        Question:
        """)
                    .append(question)
                    .append("\nAnswer:\n")
                    .append(answer);


        String prompt = promptBuilder.toString();
        System.out.println("==== PROMPT GỬI AI TASK 1 ====");
        System.out.println(prompt);

        ChatResponse response = chatModel.call(new Prompt(new UserMessage(prompt)));
        String content = response.getResult().getOutput().getText();


        System.out.println("==== RESPONSE FROM AI ====");
        System.out.println(content);
        return parseResponse(content);
    }

    public WritingAIResponse WritingTask2(String question, String answer) {
        StringBuilder promptBuilder = new StringBuilder("""
        You must to use JSON format to return response.
            You are an IELTS examiner. Review the following Task 2 writing and return a JSON object with:
        - feedback: {
            - score: 6.5
            - errorCorrections: array of objects with {
                - originalText: string (text with error, mark errors with **),
                - correctedText: string,
                - errorType: string (spelling/grammar/vocabulary/linking),
                - explanation: string
            }
            - sentenceImprovements: array of objects with {
                - originalSentence: string,
                - improvedSentence: string,
                - techniquesUsed: array of strings (academic vocab/complex structures/logical flow),
                - bandBoost: string (e.g. "5.0 → 6.0")
            }
            - overallComment: string
        }
        - evaluation: {
            - TaskAchievement: number 1-9,
            - CoherenceCohesion: number 1-9,
            - LexicalResource: number 1-9,
            - Grammar: number 1-9
        }
        - sampleAnswer: string

        Instructions:
        1. For ERROR CORRECTIONS:
            - Mark errors with **double asterisks**
            - Example: "The **datas** shows" → "The data show" (grammar)
    
        2. For SENTENCE IMPROVEMENTS:
            - Use academic vocabulary: "go up" → "increase steadily"
            - Add complex structures: "This changed" → "This underwent a significant transformation"
            - Improve logic: Add linking words like "Consequently," 
            - Specify band score improvement

        Example:
        {
          "score": 6.5 ,
          "feedback": {
            "errorCorrections": [
              {
                "originalText": "The **datas** shows",
                "correctedText": "The data show",
                "errorType": "grammar",
                "explanation": "Subject-verb agreement error"
              }
            ],
            "sentenceImprovements": [
              {
                "originalSentence": "The graph goes up",
                "improvedSentence": "The graph exhibits a steady upward trend, which indicates...",
                "techniquesUsed": ["academic vocab", "relative clause"],
                "bandBoost": "5.0 → 6.5"
              }
            ],
            "overallComment": "Good data coverage but needs more sophisticated language..."
          },
          "evaluation": {
            "TaskAchievement": 7,
            "CoherenceCohesion": 7,
            "LexicalResource": 6,
            "Grammar": 7
          },
          "sampleAnswer": "The bar chart illustrates..."
        }
    
        Question:
        """)
                .append(question)
                .append("\nAnswer:\n")
                .append(answer);


        String prompt = promptBuilder.toString();
        System.out.println("==== PROMPT GỬI AI TASK 2 ====");
        System.out.println(prompt);

        ChatResponse response = chatModel.call(new Prompt(new UserMessage(prompt)));
        String content = response.getResult().getOutput().getText();

        System.out.println("==== RESPONSE FROM AI ====");
        System.out.println(content);
        return parseResponse(content);
    }
    private WritingAIResponse parseResponse(String content) {
        try {
            // Dùng regex để tìm đoạn JSON từ { đến } an toàn hơn
            Pattern pattern = Pattern.compile("\\{.*\\}", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(content);

            if (matcher.find()) {
                String jsonPart = matcher.group();

                System.out.println("==== JSON PART ====");
                System.out.println(jsonPart);

                // Parse JSON thành đối tượng Java
                return objectMapper.readValue(jsonPart, WritingAIResponse.class);
            } else {
                throw new IllegalArgumentException("Không tìm thấy JSON hợp lệ trong phản hồi");
            }

        } catch (Exception e) {
            System.err.println("Lỗi khi parse response: " + e.getMessage());
            throw new RuntimeException("Không thể phân tích phản hồi từ AI", e);
        }
    }








}
