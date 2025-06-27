package web.ielts.Test.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import web.ielts.Test.model.answer.writing.WritingAIResponse;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AIService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    @Value("${openai.api.key}")
    private String openaiApiKey;
    public AIService(ObjectMapper objectMapper) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.objectMapper = objectMapper;
    }

    public WritingAIResponse WritingTask1(String imageUrl, String question, String answer) {
        String prompt = buildTask1Prompt(question, answer);
        String response = callOpenAITask1(prompt, imageUrl);

        return parseResponse(response, answer);
    }

    public WritingAIResponse WritingTask2(String question, String answer) {
        String prompt = buildTask2Prompt(question, answer);
        String response = callOpenAITask2(prompt);

        return parseResponse(response, answer);
    }

    private String callOpenAITask1(String promptText, String imageUrl) {
        try {
            if (!imageUrl.startsWith("https://")) {
                throw new IllegalArgumentException("Image URL must be a valid HTTPS URL");
            }

            // Log URL ảnh trước khi gửi
            System.out.println("==== IMAGE URL BEING SENT TO OPENAI ====");
            System.out.println(imageUrl);
            System.out.println("==== VERIFYING IMAGE ACCESSIBILITY ====");

            String requestBody = """
        {
          "model": "gpt-4o",
          "messages": [
            {
              "role": "user",
              "content": [
                { "type": "text", "text": %s },
                { "type": "image_url", "image_url": { "url": %s } }
              ]
            }
          ],
          "temperature": 0.2
        }
        """.formatted(
                    objectMapper.writeValueAsString(promptText),
                    objectMapper.writeValueAsString(imageUrl)
            );

            // Log request body (ẩn API key)
            System.out.println("==== REQUEST TO OPENAI (SANITIZED) ====");
            System.out.println(requestBody.replace(openaiApiKey, "***"));

            String response = webClient.post()
                    .uri("/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + openaiApiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode jsonNode = objectMapper.readTree(response);

            // Kiểm tra xem response có chứa thông tin về ảnh không
            if (jsonNode.has("usage")) {
                JsonNode usage = jsonNode.get("usage");
                int imageTokens = usage.has("image_tokens") ? usage.get("image_tokens").asInt() : 0;
                System.out.println("==== IMAGE PROCESSING INFO ====");
                System.out.println("Image tokens used: " + imageTokens);
                System.out.println("Model: " + jsonNode.get("model").asText());
            }

            String content = jsonNode.get("choices").get(0).get("message").get("content").asText();
            System.out.println("==== FULL RESPONSE FROM OPENAI ====");
            System.out.println(content);

            return content;

        } catch (Exception e) {
            System.err.println("==== OPENAI API ERROR ====");
            e.printStackTrace();
            throw new RuntimeException("OpenAI API error: " + e.getMessage());
        }
    }

//    Call AIP co anh
    private String callOpenAITask2(String prompt) {
        try {
            String requestBody = """
            {
              "model": "gpt-4o",
              "messages": [
                { "role": "user", "content": %s }
              ],
              "temperature": 0.1
            }
            """.formatted(objectMapper.writeValueAsString(prompt));

            String response = webClient.post()
                    .uri("/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + openaiApiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode jsonNode = objectMapper.readTree(response);
            String content = jsonNode.get("choices").get(0).get("message").get("content").asText();
            System.out.println("==== RESPONSE FROM OPENAI ====");
            System.out.println(content);

            return content;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("OpenAI API error: " + e.getMessage());
        }
    }

    //Prompt cho Writing 1
    private String buildTask1Prompt(String question, String answer) {
        String promptBuilder1 =
                "You must return response strictly in JSON format.\n" +
                "You are an IELTS examiner analyzing Writing Task 1 based on visual data. Extremely strict grading " +
                "1. DATA VERIFICATION:\n" +
                "   - Cross-check ALL data points/trends between image and student's answer\n" +
                "   - Flag ANY discrepancies\n" +
                "   - Verify ALL numerical values/percentages against visual data (tolerance: 0% error)\n" +
                "   - Missing key features = automatic Band 5 cap"+
                "\n" +
                "2. EVALUATION (Official IELTS Criteria):\n" +
                "• Task Achievement (25%):\n" +
                "     - [MUST HAVE] Clear overview paragraph (missing = max Band 5)\n" +
                "     - Accurate data reporting (1 error = -0.5 band)\n" +
                "     - Appropriate detail selection\n" +
                "   • Coherence & Cohesion (25%):\n" +
                "     - Logical paragraphing (Introduction/Overview/Details)\n" +
                "     - Effective linking (but not repetitive)\n" +
                "     - Progression (Band 7+ requires progression beyond listing)\n" +
                "   • Lexical Resource (25%):\n" +
                "     - Academic vocabulary (Band 9 requires ≥8 advanced terms)\n" +
                "     - Collocation accuracy (e.g. \"sharp increase\" not \"fast increase\")\n" +
                "     - Spelling (3 errors = -0.5 band)\n" +
                "   • Grammar (25%):\n" +
                "     - Tense accuracy (graph data must use past tense if historical)\n" +
                "     - Complex structures (Band 7+ needs ≥3 complex sentences)\n" +
                "     - Punctuation (comma errors = -0.5 band)"+
                "\n" +
                "3. SCORING SYSTEM:\n" +
                "   9.0 = Expert | 7.5-8.5 = Good | 6.0-7.0 = Competent | 5.5 = Limited | ≤5.0 = Problematic\n" +
                "   - Deduct 0.5 band per 2 major errors\n" +
                "   - Automatic caps: No overview → max 5.0 | Data errors → max 6.5"+

                "RESPONSE FORMAT:\n" +
                "- score: decimal (overall band score, e.g. 6.5)\n" +
                "- feedback: {\n" +
                "    (In errorCorrections only vocabulary (word choice) mistakes should be corrected in this section, and each correction must be for a single word only.)\n" +
                "    errorCorrections: [{\n" +
                "      originalText: string,  // EXACT match required\n" +
                "      correctedText: string,\n" +
                "      errorType: string,\n" +
                "      explanation: string,\n" +
                "      sentenceContext: string // the full sentence from the answer that contains the originalText; must match exactly as in the answer\n" +
                "    }],\n" +
                "    (sentenceImprovements section should improve entire sentences by enhancing academic vocabulary, sentence structure, or clarity, aiming to raise the band score.)\n" +
                "    sentenceImprovements: [{\n" +
                "      originalSentence: string,\n" +
                "      improvedSentence: string,\n" +
                "      techniquesUsed: [string],\n" +
                "      bandBoost: string (6 -> 6.5)\n" +
                "    }],\n" +
                "    overallComment: string\n" +
                "}\n" +
                "- evaluation: {\n" +
                "    TaskAchievement: {scoreEva: string, reviewEva: string},\n" +
                "    CoherenceCohesion: {scoreEva: string, reviewEva: string},\n" +
                "    LexicalResource: {scoreEva: string, reviewEva: string},\n" +
                "    Grammar: {scoreEva: string, reviewEva: string}\n" +
                "  }\n" +
                "sampleAnswer: string (Optional band 9 model)"+
                "Question:\n" + question + "\n" +
                "Original Answer:\n" + answer;

        return promptBuilder1;
    }

    private String buildTask2Prompt(String question, String answer) {
        String promptBuilder2 =
                "You must return response strictly in JSON format.\n" +
                        "You are an IELTS examiner analyzing Writing Task 2. Extremely strict grading" +
                        "1. DATA VERIFICATION:\n" +
                        "   - Cross-check ALL data points/trends between image and student's answer\n" +
                        "   - Flag ANY discrepancies\n" +
                        "   - Verify ALL numerical values/percentages against visual data (tolerance: 0% error)\n" +
                        "   - Missing key features = automatic Band 5 cap"+
                        "\n" +
                        "2. EVALUATION (Official IELTS Criteria):\n" +
                        "• Task Achievement (25%):\n" +
                        "     - [MUST HAVE] Clear overview paragraph (missing = max Band 5)\n" +
                        "     - Accurate data reporting (1 error = -0.5 band)\n" +
                        "     - Appropriate detail selection\n" +
                        "   • Coherence & Cohesion (25%):\n" +
                        "     - Logical paragraphing (Introduction/Overview/Details)\n" +
                        "     - Effective linking (but not repetitive)\n" +
                        "     - Progression (Band 7+ requires progression beyond listing)\n" +
                        "   • Lexical Resource (25%):\n" +
                        "     - Academic vocabulary (Band 9 requires ≥8 advanced terms)\n" +
                        "     - Collocation accuracy (e.g. \"sharp increase\" not \"fast increase\")\n" +
                        "     - Spelling (3 errors = -0.5 band)\n" +
                        "   • Grammar (25%):\n" +
                        "     - Tense accuracy (graph data must use past tense if historical)\n" +
                        "     - Complex structures (Band 7+ needs ≥3 complex sentences)\n" +
                        "     - Punctuation (comma errors = -0.5 band)"+
                        "\n" +
                        "3. SCORING SYSTEM:\n" +
                        "   9.0 = Expert | 7.5-8.5 = Good | 6.0-7.0 = Competent | 5.5 = Limited | ≤5.0 = Problematic\n" +
                        "   - Deduct 0.5 band per 2 major errors\n" +
                        "   - Automatic caps: No overview → max 5.0 | Data errors → max 6.5"+

                        "RESPONSE FORMAT:\n" +
                        "- score: decimal (overall band score, e.g. 6.5)\n" +
                        "- feedback: {\n" +
                        "    (In errorCorrections only vocabulary (word choice) mistakes should be corrected in this section, and each correction must be for a single word only.)\n" +
                        "    errorCorrections: [{\n" +
                        "      originalText: string,  // EXACT match required\n" +
                        "      correctedText: string,\n" +
                        "      errorType: string,\n" +
                        "      explanation: string,\n" +
                        "      sentenceContext: string // the full sentence from the answer that contains the originalText; must match exactly as in the answer\n" +
                        "    }],\n" +
                        "    (sentenceImprovements section should improve entire sentences by enhancing academic vocabulary, sentence structure, or clarity, aiming to raise the band score.)\n" +
                        "    sentenceImprovements: [{\n" +
                        "      originalSentence: string,\n" +
                        "      improvedSentence: string,\n" +
                        "      techniquesUsed: [string],\n" +
                        "      bandBoost: string (6 -> 6.5)\n" +
                        "    }],\n" +
                        "    overallComment: string\n" +
                        "}\n" +
                        "- evaluation: {\n" +
                        "    TaskAchievement: {scoreEva: string, reviewEva: string},\n" +
                        "    CoherenceCohesion: {scoreEva: string, reviewEva: string},\n" +
                        "    LexicalResource: {scoreEva: string, reviewEva: string},\n" +
                        "    Grammar: {scoreEva: string, reviewEva: string}\n" +
                        "  }\n" +
                        "sampleAnswer: string (Optional band 9 model)"+
                        "Question:\n" + question + "\n" +
                        "Original Answer:\n" + answer;

        return promptBuilder2;
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

                return response;
            } else {
                throw new IllegalArgumentException("Không tìm thấy JSON hợp lệ trong phản hồi");
            }

        } catch (Exception e) {
            System.err.println("Lỗi khi parse response: " + e.getMessage());
            throw new RuntimeException("Không thể phân tích phản hồi từ AI", e);
        }
    }

}
