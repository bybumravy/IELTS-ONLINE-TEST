package web.ielts.Test.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import web.ielts.Test.model.answer.writing.WritingAIResponse;

import java.util.List;
import java.util.Map;
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
    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String callSpeakingPart(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // 🧠 Cấu hình system message nghiêm ngặt
        String systemMessage = """
You are an official IELTS Speaking examiner. You MUST follow all deduction rules given in the prompt STRICTLY.
- Do not skip even minor vocabulary or grammar errors.
- Always explain each deduction clearly.
- NEVER give full score unless all descriptors are perfectly met.
""";

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(
                        Map.of("role", "system", "content", systemMessage),
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0,
                "top_p", 1,
                "max_tokens", 1500 // bạn có thể tăng lên nếu câu trả lời dài
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.openai.com/v1/chat/completions",
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode root = new ObjectMapper().readTree(response.getBody());
                return root
                        .path("choices")
                        .path(0)
                        .path("message")
                        .path("content")
                        .asText();
            } else {
                throw new RuntimeException("OpenAI API error: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to call OpenAI GPT API or parse response", e);
        }
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
    public String buildSpeakingPrompt(int partNumber, String question, String transcript) {
        switch (partNumber) {
            case 1:
                return buildSpeakingPart1Prompt(question, transcript);
            case 2:
                return buildSpeakingPart2Prompt(question, transcript);
            case 3:
                return buildSpeakingPart3Prompt(question, transcript);
            default:
                throw new IllegalArgumentException("Invalid part number: " + partNumber);
        }
    }
    private static final String IELTS_PUBLIC_DESCRIPTORSLexicalResource =
            "  - IELTS Public Descriptors:\n" +
                    "    • Band 9: Total flexibility and precise use in all contexts. Sustained use of accurate and idiomatic language.\n" +
                    "    • Band 8: Wide resource, readily and flexibly used to discuss all topics and convey precise meaning. Skillful use of less common and idiomatic items despite occasional inaccuracies in word choice and collocation. Effective use of paraphrase as required.\n" +
                    "    • Band 7: Resource flexibly used to discuss a variety of topics. Some ability to use less common and idiomatic items and an awareness of style and collocation is evident though inappropriacies occur. Effective use of paraphrase as required.\n" +
                    "    • Band 6: Resource sufficient to discuss topics at length. Vocabulary use may be inappropriate but meaning is clear. Generally able to paraphrase successfully.\n" +
                    "    • Band 5: Resource sufficient to discuss familiar and unfamiliar topics but there is limited flexibility. Attempts paraphrase but not always with success.\n" +
                    "    • Band 9: Structures are precise and accurate at all times, apart from ‘mistakes’ characteristic of native speaker speech.\n" +
                    "    • Band 8: Wide range of structures, flexibly used. The majority of sentences are error free. Occasional inappropriacies and non-systematic errors occur.\n" +
                    "    • Band 7: A range of structures flexibly used. Error-free sentences are frequent. Some errors persist.\n" +
                    "    • Band 6: Produces a mix of short and complex sentence forms with limited flexibility. Frequent errors in complex structures but communication is maintained.\n" +
                    "    • Band 5: Mostly basic sentence forms. Complex structures are attempted but often contain errors that may reduce clarity.\n"+
                    "    • Band 4: Resource sufficient for familiar topics but only basic meaning can be conveyed on unfamiliar topics. Frequent inappropriateness and errors in word choice. Rarely attempts paraphrase.\n" +
                    "    • Band 3: Resource limited to simple vocabulary used primarily to convey personal information. Vocabulary inadequate for unfamiliar topics.\n" +
                    "    • Band 2: Very limited resource. Utterances consist of isolated words or memorised utterances. Little communication possible without the support of mime or gesture.\n" +
                    "    • Band 1: No resource bar a few isolated words. No communication possible.\n"
                                       ;

    private static final String IELTS_PUBLIC_DESCRIPTORS_GRAMMAR =
            "- IELTS Public Descriptors:\n" +
                    "    • Band 9: Structures are precise and accurate at all times, apart from ‘mistakes’ characteristic of native speaker speech.\n" +
                    "    • Band 8: Wide range of structures, flexibly used. The majority of sentences are error free. Occasional inappropriacies and non-systematic errors occur.\n" +
                    "    • Band 7: A range of structures flexibly used. Error-free sentences are frequent. Some errors persist.\n" +
                    "    • Band 6: Produces a mix of short and complex sentence forms with limited flexibility. Frequent errors in complex structures but communication is maintained.\n" +
                    "    • Band 5: Mostly basic sentence forms. Complex structures are attempted but often contain errors that may reduce clarity.\n"+
                    "    • Band 4: Can produce basic sentence forms and some short utterances are error-free. Subordinate clauses are rare and, overall, turns are short, structures are repetitive and errors are frequent.\n" +
                    "    • Band 3: Basic sentence forms are attempted but grammatical errors are numerous except in apparently memorised utterances.\n" +
                    "    • Band 2: No evidence of basic sentence forms.\n" +
                    "    • Band 1: No rateable language unless memorised.\n";
    private static final String errorType = "• Grammar-related:\n" +
            "- Grammar: tense\n" +
            "- Grammar: subject-verb agreement\n" +
            "- Grammar: article usage\n" +
            "- Grammar: preposition\n" +
            "- Grammar: word form\n" +
            "- Grammar: clause structure\n" +
            "- Grammar: pronoun usage\n" +
            "- Grammar: modal verb\n" +
            "- Grammar: sentence structure\n" +
            "- Grammar: conditional\n" +
            "\n" +
            "• Vocabulary-related:\n" +
            "- Vocabulary: limited range\n" +
            "- Vocabulary: awkward phrasing\n" +
            "- Vocabulary: incorrect word\n" +
            "- Vocabulary: word choice\n" +
            "- Vocabulary: informal expression\n" +
            "- Vocabulary: vague expression\n" +
            "- Vocabulary: repetition\n" +
            "\n" +
            "• Coherence / Logic:\n" +
            "- Coherence: unclear meaning\n" +
            "- Coherence: repetition of ideas\n" +
            "- Coherence: poor connection\n" +
            "- Coherence: off-topic\n" +
            "- Coherence: abrupt transition";
    public String buildSpeakingPart1Prompt(String questions,String answer){
        String speakingPart1 =
                "You must return response strictly in JSON format.\n" +
                      "Note: For simple factual questions in Part 1 (e.g., “What is your name?”), brief but relevant answers are acceptable. Do not penalize short responses if they clearly address the question.\n" +

                       "Note: Spoken responses do not contain punctuation. You must IGNORE all punctuation marks such as commas, periods, question marks, or missing capital letters. \\n\" +\n" +
                        "  Do NOT mark answers down due to missing or incorrect punctuation." +
                        "  Do NOT suggest corrections just to add commas or punctuation"+
                        "If the answer is clearly unrelated or no attempt is made to respond to the question at all,\n" +
                        "→ assign Band 3.0.\n" +
                        "\n" +
                        "However, if the answer is short but still directly addresses the question (e.g., “My name is John”), \n" +
                        "→ proceed with full evaluation based on content, grammar, and vocabulary. Do not mark it as off-topic."+
                        "Before evaluation, you must first carefully understand:\n" +

                        "1. The question being asked (context and requirements)\n" +
                        "2. The full transcript of the user's response (content, grammar, vocabulary)\n" +
                        "3. You must evaluate whether the response is relevant to the question and does not go off-topic.\n" +

                        "You must only select errorType from the following list. Do not invent or rephrase. Do not include any punctuation-related error types."
                        +errorType+


                        "Once fully understood, proceed to scoring using official IELTS Band Descriptors and apply the detailed evaluation criteria provided below.\n" +
                        "1. EVALUATION (Official IELTS Criteria + Public Descriptors):\n" +
                        "\n" +
                        "• Lexical Resource (25%):\n" +
                        "+0.25 if the candidate uses 2 or more correct, natural collocations\n" +
                        "(e.g., “make a living”, “strong bond”)\n" +
                        "→ ✅ Only add once, even if more than 2 collocations are used.\n" +
                        "\n" +
                        "+0.25 if the candidate uses 1 or more idioms or phrasal verbs appropriately\n" +
                        "(e.g., “over the moon”, “give up”)\n" +
                        "→ ✅ Only add once.\n" +
                        "\n" +
                        "+0.25 if there's clear lexical variety (e.g., appropriate use of synonyms, no repetition of basic words)\n" +
                        "→ ✅ Add only once, even if lexical variety is shown throughout.\n" +
                        "\n" +
                        "+0.25 if the candidate successfully paraphrases the question instead of repeating it\n" +
                        "→ ✅ Only add once even if paraphrasing appears in multiple responses."+
                        " If any single errorType occurs more than 3 times,\n" +
                        "→ Deduct 0.5 point in total for that error type (only once)"+
                        " Moreover, apply the following criteria to ensure a more accurate and appropriate evaluation:" +
                        IELTS_PUBLIC_DESCRIPTORSLexicalResource + "\n" +
                        "• Grammatical Range and Accuracy (25%):\n" +
                        "+0.25 if the candidate uses at least 2 different complex structures correctly\n" +
                        "(e.g., conditionals, passives, relative clauses)\n" +
                        "→ ✅ Only add once.\n" +
                        "\n" +
                        "+0.25 if the candidate maintains grammatical variety and accuracy throughout\n" +
                        "→ ✅ Only add once.\n" +
                        "\n" +
                        "+0.25 if the candidate attempts advanced grammar (e.g., modal verbs, inversion, past perfect), even if imperfect\n" +
                        "→ ✅ Only add once."+
                        "Deduct points for frequent grammar errors that affect understanding:\n\n" +
                        " If any single errorType occurs more than 3 times,\n" +
                        "→ Deduct 0.5 point in total for that error type (only once)"+
                        " Moreover, apply the following criteria to ensure a more accurate and appropriate evaluation:" +
                       IELTS_PUBLIC_DESCRIPTORS_GRAMMAR+
                        "2. SCORING SYSTEM:\n" +
                        "   9.0 = Expert | 7.5-8.5 = Good | 6.0-7.0 = Competent | 5.5 = Limited | ≤5.0 = Problematic\n" +
                        "   - Deduct 0.5 band per 2 major errors\n" +
                        "RESPONSE FORMAT:\n" +
                        "- score: decimal (overall band score, e.g. 6.5)\n" +
                        "- transcript: string (full transcript of the original answer)\n" +
                        "- feedback: {\n" +
                        "    errorCorrections: [{\n" +
                        "      originalText: string,\n" +
                        "      correctedText: string,\n" +
                        "      errorType: string,\n" +
                        "      explanation: string,\n" +
                        "      sentenceContext: string\n" +
                        "    }],\n" +  // ✅ Đóng đúng mảng
                        "    overallComment: string\n" +
                        "},\n" + // ✅ Đóng đúng object feedback
                        "- evaluation: {\n" +
                        "    LexicalResource: {scoreEva: string, reviewEva: string},\n" +
                        "    Grammar: {scoreEva: string, reviewEva: string}\n" +
                        "},\n" +
                        "- sampleAnswer: string (Optional band 9 model)\n" +
                        "Question:\n" + questions + "\n" +
                        "Original Answer:\n" + answer;
        return speakingPart1;
    }

    public String buildSpeakingPart2Prompt(String question,String answer){
        String speakingPart2 =
                "You must return response strictly in JSON format.\n" +
                        "You are an official IELTS Speaking examiner. You are evaluating a real IELTS Part 2 speaking response. Extremely strict grading.\n" +
                        "Before evaluation, you must first carefully understand:\n" +
                        "1. The question being asked (context and requirements)\n" +
                        "2. The full transcript of the user's response (content, grammar, vocabulary)\n" +
                        "3. You must evaluate whether the response is relevant to the question and does not go off-topic.\n" +
                        "If the response is completely off-topic, you must give Band 3.0 regardless of other factors.\n" +
                        "You must also check whether the response answers **all bullet points** in the cue card. For **each missing or ignored point**, deduct **0.5 Band** from Fluency & Coherence.\n"+
                        "Once fully understood, proceed to scoring using official IELTS Band Descriptors.\n\n" +
                        "1. EVALUATION (Official IELTS Criteria + Public Descriptors):\n" +
                        "\n" +
                        "• Lexical Resource (25%):\n" +
                        "- Advanced vocabulary must include topic-specific academic collocations.\n" +
                        "  (If vocabulary remains general and safe, cap at Band 6.)\n" +
                        "  - Academic vocabulary (Band 9 requires ≥8 advanced terms,Band 8 >=6,Band 7 >= 4 )\n" +
                        "  - Collocation accuracy (e.g. \"sharp increase\" not \"fast increase\")\n" +
                        "  - Spelling (3 errors = -0.5 band)\n" +
                        "  - Word Choice:\n  • Misuse of formal/informal words, or awkward word forms → -0.25 per issue\n  • ≥3 major word choice issues → cap Band 6.5\n"+
                        "- Deduct points for frequent lexical errors that affect meaning or variety:\n" +
                        "  * Using wrong words or inappropriate vocabulary that confuses meaning: if this occurs more than 3 times, deduct 0.5 points per additional instance.\n" +
                        "  * Repeating simple words or phrases (e.g., 'good', 'nice') more than 3 times: deduct 0.5 points for each repetition beyond the third.\n" +
                        "  * Limited vocabulary range that restricts the ability to express ideas: assess overall impact and deduct accordingly.\n" +
                        "  * Incorrect word forms (e.g., using a verb instead of a noun): if more than 3 instances, deduct 0.5 per extra occurrence.\n" +
                        "  * Overuse of very simple words when better alternatives are possible: if overused excessively (e.g., more than 3 distinct cases), deduct accordingly.\n"+
                        "Moreover, apply the following criteria to ensure a more accurate and appropriate evaluation: "+
                        "  - IELTS Public Descriptors:\n" +
                      IELTS_PUBLIC_DESCRIPTORSLexicalResource+

                        "• Grammatical Range and Accuracy (25%):\n" +
                        "- Must demonstrate a range of sentence structures, including at least 3 complex sentences throughout the response.\n" +
                        "  (If the majority of structures are basic/simple, cap at Band 6.)\n" +
                        "- Tense accuracy: Correct use of verb tenses in context (e.g., past experiences, future plans). Frequent tense errors or tense switching → band deduction.\n" +
                        "- Complex structures: Band 7+ requires the candidate to naturally use structures such as:\n" +
                        "    • Relative clauses (\"which I really enjoy...\")\n" +
                        "    • Conditionals (\"If I had more time...\")\n" +
                        "    • Passive voice (\"It is often said that...\")\n" +
                        "    • Subordinating conjunctions (\"Although, Even though, As soon as...\")\n" +
                        "Deduct points for frequent grammar errors that affect understanding:\n\n" +
                        "- Errors such as incorrect tense, wrong verb forms, missing subjects or verbs, or incorrect sentence structures that confuse meaning.\n" +
                        "  → If any of these errors occur 3 times or more, deduct 0.5 points (only once). [-0.5 one-time deduction]\n\n" +
                        "- Lack of variety in sentence structures (e.g., using only simple sentences):\n" +
                        "  → If this happens 3 times or more, deduct 0.5 points (only once). [-0.5]\n\n" +
                        "- Unclear or confusing sentences:\n" +
                        "  → If 3 or more unclear sentences are found, deduct 0.5 points (only once). [-0.5]\n"+
                        " Moreover, apply the following criteria to ensure a more accurate and appropriate evaluation:"+
                        "Moreover, apply the following criteria to ensure a more accurate and appropriate evaluation: "+
                      IELTS_PUBLIC_DESCRIPTORS_GRAMMAR+

                        "2. SCORING SYSTEM:\n" +
                        "   9.0 = Expert | 7.5-8.5 = Good | 6.0-7.0 = Competent | 5.5 = Limited | ≤5.0 = Problematic\n" +
                        "   - Deduct 0.5 band per 2 major errors\n" +
                        "Even if the sentence is understandable, rewrite it using stronger collocations, cohesive devices, or more precise phrasing to show how the candidate could improve their band.\\n\" +\n" +
                        "Make sure each sentenceImprovement refers to a real sentence from the response and clearly shows how to improve it."+
                        "For sentence improvements,refer explicitly to the public band descriptors:  "+IELTS_PUBLIC_DESCRIPTORSLexicalResource +"and"+IELTS_PUBLIC_DESCRIPTORS_GRAMMAR+". For example, if you assign the answer Band 6.0, you must suggest sentence improvements that elevate it to Band 7.0"+
                        "RESPONSE FORMAT:\n" +
                        "- score: decimal (overall band score, e.g. 6.5)\n" +
                        "- feedback: {\n" +
                        "    errorCorrections: [{\n" +
                        "      originalText: string,\n" +
                        "      correctedText: string,\n" +
                        "      errorType: string,\n" +
                        "      explanation: string,\n" +
                        "      sentenceContext: string\n" +
                        "    }],\n" +
                        "    overallComment: string\n" +
                        "}\n" +
                        "- evaluation: {\n" +
                        "    LexicalResource: {scoreEva: string, reviewEva: string},\n" +
                        "    Grammar: {scoreEva: string, reviewEva: string}\n" +
                        "}\n" +
                        "sampleAnswer: string (Optional band 9 model)\n" +
                        "Question:\n" + question + "\n" +
                        "Original Answer:\n" + answer;
        return speakingPart2;
    }
    public String buildSpeakingPart3Prompt(String questions, String answer) {
        String speakingPart3 =
                "You must return response strictly in JSON format only — do not include any explanation or extra text.\n\n" +

                        "You are an IELTS Speaking examiner evaluating a real IELTS Part 3 response. Grade fairly but generously, based on IELTS Band Descriptors.and evaluation below\n" +

                        "Before evaluation, make sure to:\n" +
                        "1. Understand the context of the question.\n" +
                        "2. Read the full transcript of the user's response.\n" +
                        "3. Check whether the response addresses the question clearly and appropriately.\n" +
                        "⚠️ If the response is completely off-topic, give Band 3.0 regardless of other factors.\n\n" +

                        "====================\n" +
                        "1. EVALUATION CRITERIA \n" +
                        "====================\n\n" +

                        "• Lexical Resource (25%):\n" +
                        "- Reward topic-specific and varied vocabulary, even if slightly awkward.\n" +
                        "- Academic or idiomatic vocabulary is a plus and should be rewarded when used correctly.\n" +
                        "- Do not penalize formal or uncommon phrases (e.g., 'a plethora of') if meaning is clear.\n" +
                        "- Accept repetition and minor word choice issues as long as the message remains clear.\n" +
                        "- Minor idiomatic misuse or over-formality should not lower the score unless it confuses meaning.\n" +
                        "-However, frequent repetition of very simple words (e.g., 'good') more than 3 times limits the maximum band to around 6.5, even if other criteria are met."+
                        "- Deduct points for frequent lexical errors that affect meaning or variety:\n" +
                        "  * Using wrong words or inappropriate vocabulary that confuses meaning: if this occurs more than 3 times, deduct 0.5 points per additional instance.\n" +
                        "  * Repeating simple words or phrases (e.g., 'good', 'nice') more than 3 times: deduct 0.5 points for each repetition beyond the third.\n" +
                        "  * Limited vocabulary range that restricts the ability to express ideas: assess overall impact and deduct accordingly.\n" +
                        "  * Incorrect word forms (e.g., using a verb instead of a noun): if more than 3 instances, deduct 0.5 per extra occurrence.\n" +
                        "  * Overuse of very simple words when better alternatives are possible: if overused excessively (e.g., more than 3 distinct cases), deduct accordingly.\n"+
                        "Moreover, apply the following criteria to ensure a more accurate and appropriate evaluation: "+
                        IELTS_PUBLIC_DESCRIPTORSLexicalResource + "\n\n" +

                        "• Grammatical Range and Accuracy (25%):\n" +
                        "- Accept a mix of simple and complex structures.\n" +
                        "- Occasional grammar errors are expected and acceptable.\n" +
                        "- Reward efforts to use conditionals, modals, passive voice, or inversion even if imperfect.\n" +
                        "- Do not penalize non-critical mistakes like article or tense shifts if communication is successful.\n" +
                        "- Prioritize overall clarity and natural delivery over perfect grammar.\n" +
                        "Deduct points for frequent grammar errors that affect understanding:\n\n" +
                        "- Errors such as incorrect tense, wrong verb forms, missing subjects or verbs, or incorrect sentence structures that confuse meaning.\n" +
                        "  → If any of these errors occur 3 times or more, deduct 0.5 points (only once). [-0.5 one-time deduction]\n\n" +
                        "- Lack of variety in sentence structures (e.g., using only simple sentences):\n" +
                        "  → If this happens 3 times or more, deduct 0.5 points (only once). [-0.5]\n\n" +
                        "- Unclear or confusing sentences:\n" +
                        "  → If 3 or more unclear sentences are found, deduct 0.5 points (only once). [-0.5]\n"+
                        " Moreover, apply the following criteria to ensure a more accurate and appropriate evaluation:"+
                        IELTS_PUBLIC_DESCRIPTORS_GRAMMAR + "\n\n" +

                        "====================\n" +
                        "2. SCORING SYSTEM\n" +
                        "====================\n" +
                        "9.0 = Expert | 7.5–8.5 = Very Good | 6.0–7.0 = Competent | 5.0–5.5 = Limited\n" +
                        "- Only deduct for frequent or serious errors that hinder understanding.\n" +
                        "- Do not reduce scores for high-level vocabulary used correctly but less commonly.\n\n" +
                        " For every error listed in errorCorrections, suggest a corresponding sentence improvement in sentenceImprovements that rewrites the full sentence correctly and more appropriately — in a way that would raise the band score. These improvements should demonstrate better grammar, vocabulary, or fluency.\\n\" +\n" +
                        "Even if the sentence is understandable, rewrite it using stronger collocations, cohesive devices, or more precise phrasing to show how the candidate could improve their band.\\n\" +\n" +
                        "Make sure each sentenceImprovement refers to a real sentence from the response and clearly shows how to improve it."+
                        "For sentence improvements,refer explicitly to the public band descriptors:  "+IELTS_PUBLIC_DESCRIPTORSLexicalResource +"and"+IELTS_PUBLIC_DESCRIPTORS_GRAMMAR+". For example, if you assign the answer Band 6.0, you must suggest sentence improvements that elevate it to Band 7.0"+
                        "RESPONSE FORMAT:\n" +
                        "- score: decimal (overall band score, e.g. 6.5)\n" +
                        "- feedback: {\n" +
                        "    errorCorrections: [{\n" +
                        "      originalText: string,\n" +
                        "      correctedText: string,\n" +
                        "      errorType: string,\n" +
                        "      explanation: string,\n" +
                        "      sentenceContext: string\n" +
                        "    }],\n" +
                        "    overallComment: string\n" +
                        "}\n" +
                        "- evaluation: {\n" +
                        "    LexicalResource: {scoreEva: string, reviewEva: string},\n" +
                        "    Grammar: {scoreEva: string, reviewEva: string}\n" +
                        "}\n" +
                        "sampleAnswer: string (Optional band 9 model)\n" +
                        "Question:\n" + questions + "\n" +
                        "Original Answer:\n" + answer;

        return speakingPart3;
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
                        "- [MUST HAVE] Each main idea must be clearly extended with explanation and/or example. \n" +
                        "  (If ideas are presented without development, cap maximum Band 6.)\n" +
                        "- Ideas must be specific and avoid generalised statements. \n" +
                        "  (Over-generalisation = -0.5 band)\n"+
                        "   - [MUST HAVE] Clear overview paragraph (missing = max Band 5)\n" +
                        "   - Accurate data reporting (1 error = -0.5 band)\n" +
                        "   - Appropriate detail selection\n" +
                        "   - IELTS Public Descriptors:\n" +
                        "     • Band 9: Fully addresses all parts of the task. Presents a fully developed position with relevant, fully extended and well-supported ideas.\n" +
                        "     • Band 8: Sufficiently addresses all parts. Presents a well-developed response with relevant, extended and supported ideas.\n" +
                        "     • Band 7: Addresses all parts. Presents a clear position, extends and supports main ideas though there may be over-generalisation or lack of focus.\n" +
                        "     • Band 6: Addresses most parts. Presents relevant main ideas though some may lack clarity, development or conclusions.\n" +
                        "     • Band 5: Addresses task only partially. Some main ideas limited/irrelevant. Development may be unclear.\n" +
                        "\n" +
                        "• Coherence & Cohesion (25%):\n" +
                        "- Cohesion must include varied linking devices and natural progression.\n" +
                        "  (If listing-type progression dominates, cap at Band 6.)\n"+
                        "   - Logical paragraphing (Introduction/Overview/Details)\n" +
                        "   - Effective linking (but not repetitive)\n" +
                        "   - Progression (Band 7+ requires progression beyond listing)\n" +
                        "   - IELTS Public Descriptors:\n" +
                        "     • Band 9: Uses cohesion naturally so it attracts no attention. Skilfully manages paragraphing.\n" +
                        "     • Band 8: Sequences information and ideas logically. Manages all aspects of cohesion well. Uses paragraphing sufficiently and appropriately.\n" +
                        "     • Band 7: Logically organises information with clear progression. Uses cohesive devices appropriately, though there may be under-/over-use.\n" +
                        "     • Band 6: Arranges information coherently but cohesion may be faulty or mechanical. Paragraphing present but not always logical.\n" +
                        "     • Band 5: Presents information with some organisation but lacks overall progression. Inadequate, inaccurate or over-use of cohesive devices. Poor paragraphing.\n" +
                        "\n" +
                        "• Lexical Resource (25%):\n"+
                        "- Advanced vocabulary must include topic-specific academic collocations. \n" +
                        "  (If vocabulary remains general and safe, cap at Band 6.)\n"+
                        "   - Academic vocabulary (Band 9 requires ≥8 advanced terms)\n" +
                        "   - Collocation accuracy (e.g. \"sharp increase\" not \"fast increase\")\n" +
                        "   - Spelling (3 errors = -0.5 band)\n" +
                        "   - IELTS Public Descriptors:\n" +
                        "     • Band 9: Uses wide range of vocabulary naturally and precisely. Sophisticated control. Rare minor slips.\n" +
                        "     • Band 8: Uses wide range fluently and flexibly. Skilfully uses uncommon items with rare inaccuracies.\n" +
                        "     • Band 7: Uses sufficient range with flexibility. Attempts less common items with some errors.\n" +
                        "     • Band 6: Uses adequate range. Attempts less common terms but with inaccuracy.\n" +
                        "     • Band 5: Limited range. Noticeable spelling/word formation errors. May cause difficulty for the reader.\n" +
                        "\n" +
                        "• Grammar (25%):\n" +
                        "- Minimum of 3 complex structures per body paragraph.\n" +
                        "  (If majority are simple or compound sentences, cap at Band 6.)\n"+
                        "   - Tense accuracy (graph data must use past tense if historical)\n" +
                        "   - Complex structures (Band 7+ needs ≥3 complex sentences)\n" +
                        "   - Punctuation (comma errors = -0.5 band)\n" +
                        "   - IELTS Public Descriptors:\n" +
                        "     • Band 9: Uses wide range of structures with full flexibility and accuracy. Rare minor slips.\n" +
                        "     • Band 8: Uses wide range of structures. Majority of sentences are error-free.\n" +
                        "     • Band 7: Uses variety of complex structures. Frequent error-free sentences.\n" +
                        "     • Band 6: Mix of simple/complex forms. Some errors but rarely reduce communication.\n" +
                        "     • Band 5: Limited range. Frequent grammatical and punctuation errors. Errors can cause difficulty for the reader."+
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
                        "1. EVALUATION (Official IELTS Criteria + Public Descriptors):\n" +
                        "\n" +
                        "• Task Achievement (25%):\n" +
                        "- [MUST HAVE] Each main idea must be clearly extended with explanation and/or example. \n" +
                        "  (If ideas are presented without development, cap maximum Band 6.)\n" +
                        "- Ideas must be specific and avoid generalised statements. \n" +
                        "  (Over-generalisation = -0.5 band)\n"+
                        "   - [MUST HAVE] Clear overview paragraph (missing = max Band 5)\n" +
                        "   - Accurate data reporting (1 error = -0.5 band)\n" +
                        "   - Appropriate detail selection\n" +
                        "   - IELTS Public Descriptors:\n" +
                        "     • Band 9: Fully addresses all parts of the task. Presents a fully developed position with relevant, fully extended and well-supported ideas.\n" +
                        "     • Band 8: Sufficiently addresses all parts. Presents a well-developed response with relevant, extended and supported ideas.\n" +
                        "     • Band 7: Addresses all parts. Presents a clear position, extends and supports main ideas though there may be over-generalisation or lack of focus.\n" +
                        "     • Band 6: Addresses most parts. Presents relevant main ideas though some may lack clarity, development or conclusions.\n" +
                        "     • Band 5: Addresses task only partially. Some main ideas limited/irrelevant. Development may be unclear.\n" +
                        "\n" +
                        "• Coherence & Cohesion (25%):\n" +
                        "- Cohesion must include varied linking devices and natural progression.\n" +
                        "  (If listing-type progression dominates, cap at Band 6.)\n"+
                        "   - Logical paragraphing (Introduction/Overview/Details)\n" +
                        "   - Effective linking (but not repetitive)\n" +
                        "   - Progression (Band 7+ requires progression beyond listing)\n" +
                        "   - IELTS Public Descriptors:\n" +
                        "     • Band 9: Uses cohesion naturally so it attracts no attention. Skilfully manages paragraphing.\n" +
                        "     • Band 8: Sequences information and ideas logically. Manages all aspects of cohesion well. Uses paragraphing sufficiently and appropriately.\n" +
                        "     • Band 7: Logically organises information with clear progression. Uses cohesive devices appropriately, though there may be under-/over-use.\n" +
                        "     • Band 6: Arranges information coherently but cohesion may be faulty or mechanical. Paragraphing present but not always logical.\n" +
                        "     • Band 5: Presents information with some organisation but lacks overall progression. Inadequate, inaccurate or over-use of cohesive devices. Poor paragraphing.\n" +
                        "\n" +
                        "• Lexical Resource (25%):\n"+
                        "- Advanced vocabulary must include topic-specific academic collocations. \n" +
                        "  (If vocabulary remains general and safe, cap at Band 6.)\n"+
                        "   - Academic vocabulary (Band 9 requires ≥8 advanced terms)\n" +
                        "   - Collocation accuracy (e.g. \"sharp increase\" not \"fast increase\")\n" +
                        "   - Spelling (3 errors = -0.5 band)\n" +
                        "   - IELTS Public Descriptors:\n" +
                        "     • Band 9: Uses wide range of vocabulary naturally and precisely. Sophisticated control. Rare minor slips.\n" +
                        "     • Band 8: Uses wide range fluently and flexibly. Skilfully uses uncommon items with rare inaccuracies.\n" +
                        "     • Band 7: Uses sufficient range with flexibility. Attempts less common items with some errors.\n" +
                        "     • Band 6: Uses adequate range. Attempts less common terms but with inaccuracy.\n" +
                        "     • Band 5: Limited range. Noticeable spelling/word formation errors. May cause difficulty for the reader.\n" +
                        "\n" +
                        "• Grammar (25%):\n" +
                        "- Minimum of 3 complex structures per body paragraph.\n" +
                        "  (If majority are simple or compound sentences, cap at Band 6.)\n"+
                        "   - Tense accuracy (graph data must use past tense if historical)\n" +
                        "   - Complex structures (Band 7+ needs ≥3 complex sentences)\n" +
                        "   - Punctuation (comma errors = -0.5 band)\n" +
                        "   - IELTS Public Descriptors:\n" +
                        "     • Band 9: Uses wide range of structures with full flexibility and accuracy. Rare minor slips.\n" +
                        "     • Band 8: Uses wide range of structures. Majority of sentences are error-free.\n" +
                        "     • Band 7: Uses variety of complex structures. Frequent error-free sentences.\n" +
                        "     • Band 6: Mix of simple/complex forms. Some errors but rarely reduce communication.\n" +
                        "     • Band 5: Limited range. Frequent grammatical and punctuation errors. Errors can cause difficulty for the reader."+
                        "\n" +
                        "2. SCORING SYSTEM:\n" +
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
