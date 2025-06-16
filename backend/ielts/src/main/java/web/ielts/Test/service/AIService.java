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
        - feedback: string
        - evaluation: object with:
            - TaskAchievement: score 1-9 (Does the candidate cover all key features and support comparisons where relevant?)
            - CoherenceCohesion: score 1-9 (Is information logically organised and clearly linked?)
            - LexicalResource: score 1-9 (Is vocabulary varied and appropriate for describing data?)
            - Grammar: score 1-9 (Is grammar range and accuracy good?)
        - sampleAnswer: string

        Example:
        {
          "feedback": "Your report covers the main trends well...",
          "evaluation": {
            "TaskAchievement": "7",
            "CoherenceCohesion": "7",
            "LexicalResource": "8",
            "Grammar": "6"
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
        String prompt = """
        You must to use JSON format to return response.
        You are an IELTS examiner. Review the following Task 2 essay and return a JSON object with:
        - feedback: string
        - evaluation: object with:
            - TaskAchievement: score 1-9 (Does the essay address all parts of the task with well-developed ideas?)
            - CoherenceCohesion: score 1-9 (Is the essay logically organized with effective linking?)
            - LexicalResource: score 1-9 (Is vocabulary varied and precise?)
            - Grammar: score 1-9 (Is grammar range and accuracy good?)
        - sampleAnswer: string

        Example:
        {
          "feedback": "Your essay presents clear arguments...",
          "evaluation": {
            "TaskAchievement": "7",
            "CoherenceCohesion": "8",
            "LexicalResource": "8",
            "Grammar": "7"
          },
          "sampleAnswer": "It is often argued that..."
        }

        Question:
        """ + question + "\nAnswer:\n" + answer;

        System.out.println("==== PROMPT GỬI AI TASK 2 ====");
        System.out.println(prompt);

        ChatResponse response = chatModel.call(new Prompt(new UserMessage(prompt)));
        String content = response.getResult().toString();

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
