package web.ielts.Test.service.AI;


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

import web.ielts.Test.model.answer.speaking.FleCohAnswer;
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
                "model", "gpt-4o",
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
    public String buildSpeakingPrompt(
            int partNumber,
            String question,
            JsonNode transcript,

            FleCohAnswer analyzeVoice,

            List<String> cueCard
    ) {
        switch (partNumber) {
            case 1:
                return buildSpeakingPart1Prompt(
                        question,
                        transcript,
                        analyzeVoice

                );
            case 2:
                return buildSpeakingPart2Prompt(question, transcript, cueCard,analyzeVoice);
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
    private static final String IELTS_PUBLIC_FLUENCY_AND_COHERENCE =
            "- IELTS Public Descriptors:\n" +
                    "Band 9: Fluent with only very occasional repetition or self-correction. Any hesitation that occurs is used only to prepare the content of the next utterance and not to find words or grammar. Speech is situationally appropriate and cohesive features are fully acceptable. Topic development is fully coherent and appropriately extended.\n" +
                    "Band 8: Fluent with only very occasional repetition or self-correction. Hesitation may occasionally be used to find words or grammar, but most will be content related. Topic development is coherent, appropriate and relevant.\n" +
                    "Band 7: Able to keep going and readily produce long turns without noticeable effort. Some hesitation, repetition and/or self-correction may occur, often mid-sentence and indicate problems with accessing appropriate language. However, these will not affect coherence. Flexible use of spoken discourse markers, connectives and cohesive features.\n" +
                    "Band 6: Able to keep going and demonstrates a willingness to produce long turns. Coherence may be lost at times as a result of hesitation, repetition and/or self-correction. Uses a range of spoken discourse markers, connectives and cohesive features though not always appropriately.\n" +
                    "Band 5: Usually able to keep going, but relies on repetition and self-correction to do so and/or on slow speech. Hesitations are often associated with mid-sentence searches for fairly basic lexis and grammar. Overuse of certain discourse markers, connectives and other cohesive features. More complex speech usually causes disfluency but simpler language may be produced fluently.\n" +
                    "Band 4: Unable to keep going without noticeable pauses. Speech may be slow with frequent repetition. Often self-corrects. Can link simple sentences but often with repetitious use of connectives. Some breakdowns in coherence.\n" +
                    "Band 3: Frequent, sometimes long, pauses occur while candidate searches for words. Limited ability to link simple sentences and go beyond simple responses to questions. Frequently unable to convey basic message.\n" +
                    "Band 2: Lengthy pauses before nearly every word. Isolated words may be recognisable but speech is of virtually no communicative significance.\n" +
                    "Band 1: Essentially none. Speech is totally incoherent.\n";
    private static final String IELTS_PUBLIC_Pronunciation =
            "- IELTS Public Descriptors:\n" +
                    "Band 9: Uses a full range of phonological features to convey precise and/or subtle meaning. Flexible use of features of connected speech is sustained throughout. Can be effortlessly understood throughout. Accent has no effect on intelligibility.\n" +
                    "Band 8: Uses a wide range of phonological features to convey precise and/or subtle meaning. Can sustain appropriate rhythm. Flexible use of stress and intonation across long utterances, despite occasional lapses. Can be easily understood throughout. Accent has minimal effect on intelligibility.\n" +
                    "Band 7: Displays all the positive features of band 6, and some, but not all, of the positive features of band 8.\n" +
                    "Band 6: Uses a range of phonological features, but control is variable. Chunking is generally appropriate, but rhythm may be affected by a lack of stress-timing and/or a rapid speech rate. Some effective use of intonation and stress, but this is not sustained. Individual words or phonemes may be mispronounced but this causes only occasional lack of clarity. Can generally be understood throughout without much effort.\n" +
                    "Band 5: Displays all the positive features of band 4, and some, but not all, of the positive features of band 6.\n" +
                    "Band 4: Uses some acceptable phonological features, but the range is limited. Produces some acceptable chunking, but there are frequent lapses in overall rhythm. Attempts to use intonation and stress, but control is limited. Individual words or phonemes are frequently mispronounced, causing lack of clarity. Understanding requires some effort and there may be patches of speech that cannot be understood.\n" +
                    "Band 3: Displays some features of band 2, and some, but not all, of the positive features of band 4.\n" +
                    "Band 2: Uses few acceptable phonological features (possibly because sample is insufficient). Overall problems with delivery impair attempts at connected speech. Individual words and phonemes are mainly mispronounced and little meaning is conveyed. Often unintelligible.\n" +
                    "Band 1: Can produce occasional individual words and phonemes that are recognisable, but no overall meaning is conveyed. Unintelligible.\n";

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
            "- Vocabulary: repetition\n"

            ;

    public String buildSpeakingPart1Prompt(
            String questions,
            JsonNode transcript,
            FleCohAnswer analyzeVoice

    ) {
//        System.out.println("hello");
//        String praatResults = (String) analyzeVoice.get("prosodyFeatures");
//
//        System.out.println(praatResults);

        String speakingPart1 =
                "You must return response strictly in JSON format.\n" +
                        "Even for simple factual questions (e.g., “What is your name?”), if the response contains fewer than 2 full sentences, you must still limit the score to a **maximum of Band 6.5** in all categories. This ensures minimum development is required.\n+"+

                        "Note: Spoken responses do not contain punctuation. You must IGNORE all punctuation marks such as commas, periods, question marks, or missing capital letters. \\n\" +\n" +
                        "  Do NOT mark answers down due to missing or incorrect punctuation." +
                        "  Do NOT suggest corrections just to add commas or punctuation"+
                        "If the response is completely off-topic, you must give Band 3.0 for fluency and coherence\n" +
                        "\n" +
                        "However, if the answer is short but still directly addresses the question  \n" +
                        "→ proceed with full evaluation based on content, grammar, and vocabulary. Do not mark it as off-topic."+
                        "Before evaluation, you must first carefully understand:\n" +

                        "1. The question being asked (context and requirements)\n" +
                        "2. The full transcript of the user's response (content, grammar, vocabulary)\n" +
                        "3. You must evaluate whether the response is relevant to the question and does not go off-topic.\n" +
                        ""+
                        "4. Do not assign a score of 7.5 or higher if the response is relevant but lacks development.\n" +
                        "If the response is very short (e.g., fewer than 5 sentences), even if it answers the question correctly and fluently, you must treat it as underdeveloped and assign no more than Band 7.0 in any category.\n" +



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
                        "+0.5 if the candidate uses advanced or topic-specific vocabulary naturally and correctly\n" +
                        "\n" +
                        "E.g., “onsen”, “scenic town”, “black eggs” (for the topic of travel in Japan)\n" +
                        "✅ Only add once, regardless of how many topic-specific terms are used."+
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
                        "Fluency and Coherence 25%"+
                        "Fluency features based on acoustic analysis: {meanIntensity}, {speechRate}, {pauseCount} in\n" + analyzeVoice.getMeanIntensity()+" "+analyzeVoice.getSpeechRate()+analyzeVoice.getPauseCount()+
                        "Scoring rules:\n" +
                        "- +0.5 if meanIntensity is between 50–60 dB (clear and stable voice).\n Only add once." +
                        "- +0.25 if meanIntensity is between 60–65 dB (slightly strong but acceptable).\n Only add once." +
                        "- -0.5 if meanIntensity < 45 dB (voice too weak).\n Only add once." +
                        "- +0.5 if speechRate is between 2.0–3.0 words/sec (smooth and fluent).\n Only add once." +
                        "- -0.5 if speechRate < 1.5 words/sec (slow, hesitant).\n Only add once." +
                        "- +0.5 if pauseCount == 0.0 (no unnatural hesitation).\n Only add once." +
                        "- -0.5 if pauseCount > 2 (frequent unnatural pauses).Only add once."+
                        "Coherence  "+
                        "+0.5 if the speaker presents ideas in a clear logical sequence (with an introduction, development, and conclusion).\n Only add once." +
                        "\n" +
                        "+0.25 if cohesive devices (e.g., “however”, “as a result”, “on the other hand”) are used effectively and appropriately.\n Only add once." +
                        "\n" +
                        "+0.25 if the speaker avoids repeating ideas or overemphasizing a single point.\n Only add once." +
                        "\n" +
                        "+0.25 if each sentence connects clearly to the previous one (no abrupt transitions).\n Only add once." +
                        "\n" +
                        "+0.25 if each argument or point is supported with examples, explanations, or reasons.\n Only add once." +
                        "\n" +
                        "+0.5 if the entire response does not contain any coherence-related errors listed below. Only add once."+
                        "→ Deduct 0.5 point in total for that error type (only once) for Coherence"+
                        " Moreover, apply the following criteria to ensure a more accurate and appropriate evaluation:" +
                        IELTS_PUBLIC_FLUENCY_AND_COHERENCE+
                        "2. SCORING SYSTEM:\n" +
                        "   9.0 = Expert | 7.5-8.5 = Good | 6.0-7.0 = Competent | 5.5 = Limited | ≤5.0 = Problematic\n" +
                        "Apply the evaluation criteria to score each individual aspect separately. For example, what is the score for Grammar"+
                        "RESPONSE FORMAT:\n" +
                        "- transcript: string ( transcript of the original answer)\n" +
                        "- question (string)"+
                        ""+
                        "\n" +
                        "IMPORTANT RULES:\n" +
                        "- Only provide feedback **when there is an actual error** in the evaluated category.\n" +
                        "- When evaluating **Grammar**, only identify and comment on **grammar-related errors**.\n" +
                        "- When evaluating **Lexical Resource**, only identify and comment on **vocabulary-related errors**.\n" +
                        "- Do **not** cross over between categories (e.g., do not mention vocabulary issues when scoring grammar).\n" +
                        "- Always include a score in both grammarAnswer and lexicalAnswer, even if there are no errors\n" +
                        "- If there are no errors in a category, do not provide errorText, correctText, explanation, or sentenceContext—only the score"+
                        "For lexical and grammar evaluations:\n" +
                        "✅ `errorText` must contain **only the incorrect word or phrase** (not the full sentence). (1–5 words maximum) \n" +
                        "✅ `correctText` must contain **only the corrected word or phrase**. (1–5 words maximum) \n" +
                        "❌ Do NOT include full sentence rewrites.  \n" +
                        "✅ Provide the full sentence in `sentenceContext` so the error can be understood in context.\n" +
                        "\n" +
                        "You must only select errorType from the following list. Do not invent or rephrase. Do not include any punctuation-related error types."
                        +errorType+

                       "- grammarAnswer (object) with:\n" +
                        "    - score (double)\n" +
                        "    - errorText (string)\n" +
                        "    - correctText (string)\n" +
                        "    - errorType (string)\n" +
                        "    - explanation (string)\n" +
                        "    - sentenceContext (string)\n" +
                        "- lexicalAnswer (object) with:\n" +
                        "    - score (double)\n" +
                        "    - errorText (string)\n" +
                        "    - correctText (string)\n" +
                        "    - errorType (string)\n" +
                        "    - explanation (string)\n" +
                        "    - sentenceContext (string)"+
                        "\"For Fluency and Coherence, provide detailed feedback only after assigning the score. This feedback must be strictly based on the actual fluency and coherence performance observed in Part 1 of the candidate’s response.\n" +
                        "\n" +
                        "Do NOT provide generic or vague comments.\n" +
                        "\n" +
                        "Your feedback must explicitly mention and evaluate the following:\n" +
                        "\n" +
                        "meanIntensity"+
                        "- **Speech rate**: Was the candidate’s speech fast, slow, or appropriately paced?\n" +
                        "- **Number and nature of pauses**: Were there frequent unnatural pauses or hesitations?\n" +
                        "- **Logical progression of ideas**: Did the candidate present ideas in a logical and connected manner?\n" +
                        "- **Use of cohesive devices**: Were linking words (e.g., however, because, so) used correctly and naturally?\n" +
                        "- **Overall clarity**: Was the response easy to follow and understand?"+
                        "- fluencyCohAnswer (object) with:\n" +
                        "    - score (double)"+
                        "    - comment (string)"
                +
                        "Question:\n" + questions + "\n" +
                        "Original Answer:\n" + transcript;

        return speakingPart1;
    }


    public String buildSpeakingPart2Prompt(String question,JsonNode transcipt,List<String> cueCards,FleCohAnswer analyzeVoice){
        String speakingPart2 =
                "You must return response strictly in JSON format.\n" +
                        "You are an official IELTS Speaking examiner. You are evaluating a real IELTS Part 2 speaking response. Extremely strict grading.\n" +
                        "Before evaluation, you must first carefully understand:\n" +
                        "1. The question being asked (context and requirements)\n" +
                        "2. The full transcript of the user's response (content, grammar, vocabulary)\n" +
                        "3. You must evaluate whether the response is relevant to the question and does not go off-topic.\n" +
                        "If the response is completely off-topic, you must give Band 3.0 for fluency and coherence\n" +
                        "4. You must strictly check if the candidate addresses **all bullet points** in the cue card:\n" +

                        " For **each bullet point that is ignored or insufficiently developed**, deduct **0.5 Band** from **Fluency & Coherence**.\n" +
                        "\n" +
                        "You must also check whether the response answers **all bullet points** in the cue card. For **each missing or ignored point**, deduct **0.5 Band** from Fluency & Coherence.\n"+
                        "Once fully understood, proceed to scoring using official IELTS Band Descriptors.\n" +
                        "1. EVALUATION (Official IELTS Criteria + Public Descriptors):\n" +
                        "\n" +
                        "• Lexical Resource (25%):\n" +
                        "+0.25 if the candidate uses 4 or more correct, natural collocations  \n" +
                        "(e.g., “make a living”, “strong bond”)  \n" +
                        "→ ✅ Only add once, even if more than 4 are used.\n" +
                        "\n" +
                        "+0.25 if the candidate uses 2 or more idiomatic expressions appropriately  \n" +
                        "(e.g., “hit the road”, “over the moon”)  \n" +
                        "→ ✅ Only add once.\n" +
                        "\n" +
                        "+0.25 if the candidate uses 2 or more phrasal verbs appropriately  \n" +
                        "(e.g., “give up”, “carry on”)  \n" +
                        "→ ✅ Only add once.\n" +
                        "\n" +
                        "+0.25 if there's clear lexical variety  \n" +
                        "(e.g., appropriate use of synonyms, avoiding repetition of basic words)  \n" +
                        "→ ✅ Only add once, even if lexical variety is shown throughout.\n" +
                        "\n" +
                        "+0.25 if the candidate successfully paraphrases the question  \n" +
                        "(e.g., rephrasing the prompt naturally instead of repeating it)  \n" +
                        "→ ✅ Only add once, even if it appears in multiple parts.\n" +
                        "\n" +
                        "+0.25 if the candidate uses 2 or more topic-specific vocabulary correctly and naturally  \n" +
                        "(e.g., “onsen”, “black eggs”, “scenic town” for travel in Japan)  \n" +
                        "→ ✅ Only add once.\n" +
                        "\n" +
                        "+0.25 if the candidate uses 2 or more advanced academic vocabulary naturally and correctly  \n" +
                        "(e.g., “infrastructure”, “inequality”, “preservation”)  \n" +
                        "→ ✅ Only add once."+
                        " If any single errorType occurs more than 3 times,\n" +
                        "→ Deduct 0.5 point in total for that error type about Lexical Resource (only once)"+

                        "Moreover, apply the following criteria to ensure a more accurate and appropriate evaluation: "+
                        "  - IELTS Public Descriptors:\n" +
                      IELTS_PUBLIC_DESCRIPTORSLexicalResource+

                        "• Grammatical Range and Accuracy (25%):\n" +
                       "+0.25 if the candidate uses at least 2 different complex structures correctly  \n" +
                        "(e.g., conditionals, passive voice, relative clauses)  \n" +
                        "→ ✅ Only add once.\n" +
                        "\n" +
                        "+0.25 if the candidate maintains grammatical variety and overall accuracy throughout  \n" +
                        "→ ✅ Only add once.\n" +
                        "\n" +
                        "+0.25 if the candidate attempts at least 2 advanced grammar forms, even if imperfect  \n" +
                        "(e.g., modal verbs, inversion, past perfect, cleft sentences)  \n" +
                        "→ ✅ Only add once.\n" +
                        "\n" +
                        "+0.25 if the candidate uses at least 2 different verb tenses correctly and appropriately  \n" +
                        "(e.g., past + present perfect)  \n" +
                        "→ ✅ Only add once."+
                        "Deduct points for frequent grammar errors that affect understanding:\n\n" +
                        " If any single errorType occurs more than 3 times,\n" +
                        "→ Deduct 0.5 point in total for that error type about grammar (only once)"+
                        "Moreover, apply the following criteria to ensure a more accurate and appropriate evaluation: "+
                      IELTS_PUBLIC_DESCRIPTORS_GRAMMAR+
                        "Fluency and Coherence 25%"+
                        "Fluency features based on acoustic analysis: {meanIntensity}, {speechRate}, {pauseCount} in\n" + analyzeVoice.getMeanIntensity()+" "+analyzeVoice.getSpeechRate()+analyzeVoice.getPauseCount()+
                        "Scoring rules:\n" +
                        "- +0.5 if meanIntensity is between 50–60 dB (clear and stable voice).\n Only add once." +
                        "- +0.25 if meanIntensity is between 60–65 dB (slightly strong but acceptable).\n Only add once." +
                        "- -0.5 if meanIntensity < 45 dB (voice too weak).\n Only add once." +
                        "- +0.5 if speechRate is between 2.0–3.0 words/sec (smooth and fluent).\n Only add once." +
                        "- -0.5 if speechRate < 1.5 words/sec (slow, hesitant).\n Only add once." +
                        "- +0.5 if pauseCount == 0.0 (no unnatural hesitation).\n Only add once." +
                        "- -0.5 if pauseCount > 2 (frequent unnatural pauses).Only add once."+
                        "Coherence  "+
                        "+0.5 if the speaker presents ideas in a clear logical sequence (with an introduction, development, and conclusion).\n Only add once." +
                        "\n" +
                        "+0.25 if cohesive devices (e.g., “however”, “as a result”, “on the other hand”) are used effectively and appropriately.\n Only add once." +
                        "\n" +
                        "+0.25 if the speaker avoids repeating ideas or overemphasizing a single point.\n Only add once." +
                        "\n" +
                        "+0.25 if each sentence connects clearly to the previous one (no abrupt transitions).\n Only add once." +
                        "\n" +
                        "+0.25 if each argument or point is supported with examples, explanations, or reasons.\n Only add once." +
                        "\n" +
                        "+0.5 if the entire response does not contain any coherence-related errors listed below. Only add once."+
                        "→ Deduct 0.5 point in total for that error type (only once) for Coherence"+
                        " Moreover, apply the following criteria to ensure a more accurate and appropriate evaluation:" +
                        IELTS_PUBLIC_FLUENCY_AND_COHERENCE+
                        "2. SCORING SYSTEM:\n" +
                        "   9.0 = Expert | 7.5-8.5 = Good | 6.0-7.0 = Competent | 5.5 = Limited | ≤5.0 = Problematic\n" +
                        "   - Deduct 0.5 band per 2 major errors\n" +
                        "For lexical and grammar, the errorText should only include the incorrect word, and the correctText should contain the correct word."+
                        "RESPONSE FORMAT:\n" +
                        "- transcript: string ( transcript of the original answer)\n" +
                        "- question (string)"+
                        ""+
                        "IMPORTANT RULES:\n" +
                        "- Only provide feedback **when there is an actual error** in the evaluated category.\n" +
                        "- When evaluating **Grammar**, only identify and comment on **grammar-related errors**.\n" +
                        "- When evaluating **Lexical Resource**, only identify and comment on **vocabulary-related errors**.\n" +
                        "- Do **not** cross over between categories (e.g., do not mention vocabulary issues when scoring grammar).\n" +
                        "- Always include a score in both grammarAnswer and lexicalAnswer, even if there are no errors.\n" +
                        "- If there are no errors in a category, do not provide errorText, correctText, explanation, or sentenceContext—only the score"+

                        "\n" +
                        "For lexical and grammar evaluations:\n" +
                        "✅ `errorText` must contain **only the incorrect word or phrase** (not the full sentence). (1–5 words maximum) \n" +
                        "✅ `correctText` must contain **only the corrected word or phrase**.(1–5 words maximum)  \n" +
                        "❌ Do NOT include full sentence rewrites.  \n" +
                        "✅ Provide the full sentence in `sentenceContext` so the error can be understood in context.\n" +
                        "\n" +
                        "You must only select errorType from the following list. Do not invent or rephrase. Do not include any punctuation-related error types."
                        +errorType+
                        "- grammarAnswer (object) with:\n" +
                        "    - score (double)\n" +
                        "    - errorText (string)\n" +
                        "    - correctText (string)\n" +
                        "    - errorType (string)\n" + // only grammar type
                        "    - explanation (string)\n" +
                        "    - sentenceContext (string)\n" +
                        "- lexicalAnswer (object) with:\n" +
                        "    - score (double)\n" +
                        "    - errorText (string)\n" +
                        "    - correctText (string)\n" +
                        "    - errorType (string)\n" +//only lexical
                        "    - explanation (string)\n" +
                        "    - sentenceContext (string)"+
                        "\"For Fluency and Coherence, provide detailed feedback only after assigning the score. This feedback must be strictly based on the actual fluency and coherence performance observed in Part 1 of the candidate’s response.\n" +
                        "\n" +
                        "Do NOT provide generic or vague comments.\n" +
                        "\n" +
                        "Your feedback must explicitly mention and evaluate the following:\n" +
                        "\n" +
                        "meanIntensity"+
                        "- **Speech rate**: Was the candidate’s speech fast, slow, or appropriately paced?\n" +
                        "- **Number and nature of pauses**: Were there frequent unnatural pauses or hesitations?\n" +
                        "- **Logical progression of ideas**: Did the candidate present ideas in a logical and connected manner?\n" +
                        "- **Use of cohesive devices**: Were linking words (e.g., however, because, so) used correctly and naturally?\n" +
                        "- **Overall clarity**: Was the response easy to follow and understand?"+
                        "- fluencyCohAnswer (object) with:\n" +
                        "    - score (double)"+
                        "    - comment (string)"+
                        "Question:\n" + question + "\n" +
                        "CueCard:\n" + cueCards.toString() + "\n" +
                        "Original Answer:\n" + transcipt;
        return speakingPart2;
    }
    public String buildSpeakingPart3Prompt(String questions, JsonNode transcipt) {
        String speakingPart3 =
                "You must return response strictly in JSON format only — do not include any explanation or extra text.\n\n" +

                        "You are an IELTS Speaking examiner evaluating a real IELTS Part 3 response. Grade fairly but generously, based on IELTS Band Descriptors.and evaluation below\n" +

                        "Before evaluation, make sure to:\n" +
                        "1. Understand the context of the question.\n" +
                        "2. Read the full transcript of the user's response.\n" +
                        "If the response is completely off-topic, you must give Band 3.0 for fluency and coherence\n" +

                        "1. EVALUATION (Official IELTS Criteria + Public Descriptors):\n" +
                        "\n" +
                        "• Lexical Resource (25%):\n" +
                        "+0.25 if the candidate uses 4 or more correct, natural collocations  \n" +
                        "(e.g., “make a living”, “strong bond”)  \n" +
                        "→ ✅ Only add once, even if more than 4 are used.\n" +
                        "\n" +
                        "+0.25 if the candidate uses 2 or more idiomatic expressions appropriately  \n" +
                        "(e.g., “hit the road”, “over the moon”)  \n" +
                        "→ ✅ Only add once.\n" +
                        "\n" +
                        "+0.25 if the candidate uses 2 or more phrasal verbs appropriately  \n" +
                        "(e.g., “give up”, “carry on”)  \n" +
                        "→ ✅ Only add once.\n" +
                        "\n" +
                        "+0.25 if there's clear lexical variety  \n" +
                        "(e.g., appropriate use of synonyms, avoiding repetition of basic words)  \n" +
                        "→ ✅ Only add once, even if lexical variety is shown throughout.\n" +
                        "\n" +
                        "+0.25 if the candidate successfully paraphrases the question  \n" +
                        "(e.g., rephrasing the prompt naturally instead of repeating it)  \n" +
                        "→ ✅ Only add once, even if it appears in multiple parts.\n" +
                        "\n" +
                        "+0.25 if the candidate uses 2 or more topic-specific vocabulary correctly and naturally  \n" +
                        "(e.g., “onsen”, “black eggs”, “scenic town” for travel in Japan)  \n" +
                        "→ ✅ Only add once.\n" +
                        "\n" +
                        "+0.25 if the candidate uses 2 or more advanced academic vocabulary naturally and correctly  \n" +
                        "(e.g., “infrastructure”, “inequality”, “preservation”)  \n" +
                        "→ ✅ Only add once."+
                        "+0.25 if the candidate uses 2 or more abstract or conceptual terms naturally, appropriately, and relevant to the topic  \n" +
                        "(e.g., “globalization”, “social norms”, “freedom of expression” in a discussion about cultural changes)  \n" +
                        "→ ✅ Only add once.\n" +
                        "\n" +
                        "+0.25 if the candidate uses 2 or more logical or argumentation linking devices correctly, naturally, and in a way that supports topic development  \n" +
                        "(e.g., “as a result”, “from my perspective”, “what’s more” in a discussion about social problems)  \n" +
                        "→ ✅ Only add once."+
                        " If any single errorType occurs more than 2 times,\n" +
                        "→ Deduct 0.5 point in total for that error type (only once)"+

                        "Moreover, apply the following criteria to ensure a more accurate and appropriate evaluation: "+
                        "  - IELTS Public Descriptors:\n" +
                        IELTS_PUBLIC_DESCRIPTORSLexicalResource+

                        "• Grammatical Range and Accuracy (25%):\n" +
                        "+0.25 if the candidate uses at least 2 different complex structures correctly  \n" +
                        "(e.g., conditionals, passive voice, relative clauses)  \n" +
                        "→ ✅ Only add once.\n" +
                        "\n" +
                        "+0.25 if the candidate maintains grammatical variety and overall accuracy throughout  \n" +
                        "→ ✅ Only add once.\n" +
                        "\n" +
                        "+0.25 if the candidate attempts at least 2 advanced grammar forms, even if imperfect  \n" +
                        "(e.g., modal verbs, inversion, past perfect, cleft sentences)  \n" +
                        "→ ✅ Only add once.\n" +
                        "\n" +
                        "+0.25 if the candidate uses at least 2 different verb tenses correctly and appropriately  \n" +
                        "(e.g., past + present perfect)  \n" +
                        "→ ✅ Only add once."+
                        "Deduct points for frequent grammar errors that affect understanding:\n\n" +
                        " If any single errorType occurs more than 3 times,\n" +
                        "→ Deduct 0.5 point in total for that error type (only once)"+
                        "Moreover, apply the following criteria to ensure a more accurate and appropriate evaluation: "+
                        IELTS_PUBLIC_DESCRIPTORS_GRAMMAR+
                        "• Fluency and coherence (25%):\n" +
                        "+0.25 if the candidate maintains smooth flow of speech with minimal hesitation (Fluency)\n" +
                        "→ ✅ Only add once.\n" +
                        "\n" +
                        "+0.25 if the candidate uses natural pausing and appropriate pacing (Fluency)\n" +
                        "→ ✅ Only add once.\n" +
                        "\n" +
                        "+0.25 if ideas are logically ordered and connected clearly (Coherence)\n" +
                        "→ ✅ Only add once.\n" +
                        "\n" +
                        "+0.25 if discourse markers / cohesive devices are used appropriately (Coherence)\n" +
                        "→ ✅ Only add once.\n" +
                        "→ Deduct 0.5 point in total for that error type about Fluency and coherence  (only once)"+
                        "If the response does not include a clear structure with an introduction, body, and conclusion, deduct 0.5 point."+
                        " Moreover, apply the following criteria to ensure a more accurate and appropriate evaluation:" +
                        IELTS_PUBLIC_FLUENCY_AND_COHERENCE+

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
                        "2. SCORING SYSTEM:\n" +
                        "   9.0 = Expert | 7.5-8.5 = Good | 6.0-7.0 = Competent | 5.5 = Limited | ≤5.0 = Problematic\n" +
                        "Apply the evaluation criteria to score each individual aspect separately. For example, what is the score for Grammar"+
                        "For lexical and grammar, the errorText should only include the incorrect word, and the correctText should contain the correct word."+
                        "RESPONSE FORMAT:\n" +
                        "- transcript: string ( transcript of the original answer)\n" +
                        "- question (string)"+
                       "IMPORTANT RULES:\n" +
                        "- Only provide feedback **when there is an actual error** in the evaluated category.\n" +
                        "- When evaluating **Grammar**, only identify and comment on **grammar-related errors**.\n" +
                        "- When evaluating **Lexical Resource**, only identify and comment on **vocabulary-related errors**.\n" +
                        "- Do **not** cross over between categories (e.g., do not mention vocabulary issues when scoring grammar).\n" +
                        "- Always include a score in both grammarAnswer and lexicalAnswer, even if there are no errors.\n" +
                        "- If there are no errors in a category, do not provide errorText, correctText, explanation, or sentenceContext—only the score"+
                        "\n" +
                        "For lexical and grammar evaluations:\n" +
                        "✅ `errorText` must contain **only the incorrect word or phrase** (not the full sentence).(1–5 words maximum)  \n" +
                        "✅ `correctText` must contain **only the corrected word or phrase**. (1–5 words maximum) \n" +
                        "❌ Do NOT include full sentence rewrites.  \n" +
                        "✅ Provide the full sentence in `sentenceContext` so the error can be understood in context.\n" +
                        "\n" +
                        "You must only select errorType from the following list. Do not invent or rephrase. Do not include any punctuation-related error types."
                        +errorType+
                        "- grammarAnswer (object) with:\n" +
                        "    - score (double)\n" +
                        "    - errorText (string)\n" +
                        "    - correctText (string)\n" +
                        "    - errorType (string)\n" +
                        "    - explanation (string)\n" +
                        "    - sentenceContext (string)\n" +
                        "- lexicalAnswer (object) with:\n" +
                        "    - score (double)\n" +
                        "    - errorText (string)\n" +
                        "    - correctText (string)\n" +
                        "    - errorType (string)\n" +
                        "    - explanation (string)\n" +
                        "    - sentenceContext (string)"+
                        "\"For Fluency and Coherence, provide detailed feedback only after assigning the score. This feedback must be strictly based on the actual fluency and coherence performance observed in Part 1 of the candidate’s response.\n" +
                        "\n" +
                        "Do NOT provide generic or vague comments.\n" +
                        "\n" +
                        "Your feedback must explicitly mention and evaluate the following:\n" +
                        "\n" +
                        "meanIntensity"+
                        "- **Speech rate**: Was the candidate’s speech fast, slow, or appropriately paced?\n" +
                        "- **Number and nature of pauses**: Were there frequent unnatural pauses or hesitations?\n" +
                        "- **Logical progression of ideas**: Did the candidate present ideas in a logical and connected manner?\n" +
                        "- **Use of cohesive devices**: Were linking words (e.g., however, because, so) used correctly and naturally?\n" +
                        "- **Overall clarity**: Was the response easy to follow and understand?"+
                        "- fluencyCohAnswer (object) with:\n" +
                        "    - score (double)"+
                        "    - comment (string)"+
                        "Question:\n" + questions + "\n" +
                        "Original Answer:\n" + transcipt;

        return speakingPart3;
    }


        public static final String IELTS_PUBLIC_DESCRIPTORS_WritingTask1_TaskAchievement =
                "**Task Achievement (Academic only)**:\n" +
                        "- Band 9: All the requirements of the task are fully and appropriately satisfied.\n" +
                        "There may be extremely rare lapses in content." +
                        "- Band 8: The response covers all the requirements of the task appropriately, relevantly\n" +
                        "and sufficiently.\n" +
                        "Key features are skilfully selected, and clearly presented,\n" +
                        "highlighted and illustrated.\n" +
                        "There may be occasional omissions or lapses in content.\n" +
                        "- Band 7: The response covers the requirements of the task.\n" +
                        "The content is relevant and accurate – there may be a few omissions or lapses.\n" +
                        "The format is appropriate.\n" +
                        "Key features which are selected are covered and clearly\n" +
                        "highlighted but could be more fully or more appropriately illustrated or\n" +
                        "extended.\n" +
                        "It presents a clear overview, the data are appropriately\n" +
                        "categorised, and main trends or differences are identified. " +
                        "- Band 6: The response focuses on the requirements of the task and an appropriate\n" +
                        "format is used.\n" +
                        "Key features which are selected are covered and adequately\n" +
                        "highlighted. A relevant overview is attempted. Information is appropriately\n" +
                        "selected and supported using figures/data.\n" +
                        "Some irrelevant, inappropriate or inaccurate information may occur in\n" +
                        "areas of detail or when illustrating or extending the main points.\n" +
                        "Some details may be missing (or excessive) and further extension or\n" +
                        "illustration may be needed.\n" +
                        "- Band 5: The response generally addresses the requirements of the task. The\n" +
                        "format may be inappropriate in places.\n" +
                        "Key features which are selected are not adequately covered.\n" +
                        "The recounting of detail is mainly mechanical. There may be no data to\n" +
                        "support the description.\n" +
                        "The inclusion of irrelevant, inappropriate or inaccurate material in key\n" +
                        "areas detracts from the task achievement.\n" +
                        "There is limited detail when extending and illustrating the main points.\n" +
                        "- Band 4: The response is an attempt to address the task.\n" +
                        "Few key features have been selected.\n" +
                        "The format may be inappropriate.\n" +
                        "Key features/bullet points which are presented may be irrelevant, repetitive,\n" +
                        "inaccurate or inappropriate." +
                        "- Band 3: The response does not address the requirements of the task (possibly because\n" +
                        "of misunderstanding of the data/diagram/situation).\n" +
                        "Key features/bullet points which are presented may be largely irrelevant.\n" +
                        "Limited information is presented, and this may be used repetitively." +
                        "- Band 2: The content barely relates to the task. " +
                        "- Band 1: Responses of 20 words or fewer are rated at Band 1.\n" +
                        "The content is wholly unrelated to the task.\n" +
                        "Any copied rubric must be discounted.\n" +
                        "- Band 0: Should only be used where a candidate did not attend or attempt the question in any way, used a language other than English throughout, or where there is proof that a candidate’s answer has been totally\n" +
                        "memorised.";

        public static final String IELTS_PUBLIC_DESCRIPTORS_WritingTask1_CoherenceCohesion =
                "**Coherence and Cohesion**:\n" +
                        "- Band 9: The message can be followed effortlessly.\n" +
                        "Cohesion is used in such a way that it very\n" +
                        "rarely attracts attention.\n" +
                        "Any lapses in coherence or cohesion are\n" +
                        "minimal.\n" +
                        "Paragraphing is skilfully managed." +
                        "- Band 8: The message can be followed with ease.\n" +
                        "Information and ideas are logically\n" +
                        "sequenced, and cohesion is well managed.\n" +
                        "Occasional lapses in coherence or\n" +
                        "cohesion may occur.\n" +
                        "Paragraphing is used sufficiently and\n" +
                        "appropriately.\n" +
                        "- Band 7: Information and ideas are logically\n" +
                        "organised and there is a clear progression\n" +
                        "throughout the response. A few lapses\n" +
                        "may occur.\n" +
                        "A range of cohesive devices including\n" +
                        "reference and substitution is used flexibly\n" +
                        "but with some inaccuracies or some\n" +
                        "over/under use." +
                        "- Band 6: Information and ideas are generally\n" +
                        "arranged coherently and there is a clear\n" +
                        "overall progression.\n" +
                        "Cohesive devices are used to some good\n" +
                        "effect but cohesion within and/or\n" +
                        "between sentences may be faulty or\n" +
                        "mechanical due to misuse, overuse or\n" +
                        "omission.\n" +
                        "The use of reference and substitution\n" +
                        "may lack flexibility or clarity and result in\n" +
                        "some repetition or error\n" +
                        "- Band 5: Organisation is evident but is not wholly\n" +
                        "logical and there may be a lack of overall\n" +
                        "progression. Nevertheless, there is a\n" +
                        "sense of underlying coherence to the\n" +
                        "response.\n" +
                        "The relationship of ideas can be followed\n" +
                        "but the sentences are not fluently linked\n" +
                        "to each other.\n" +
                        "There may be limited/overuse of cohesive\n" +
                        "devices with some inaccuracy.\n" +
                        "The writing may be repetitive due to\n" +
                        "inadequate and/or inaccurate use of\n" +
                        "reference and substitution." +
                        "- Band 4: Information and ideas are evident but not\n" +
                        "arranged coherently, and there is no clear\n" +
                        "progression within the response.\n" +
                        "Relationships between ideas can be unclear\n" +
                        "and/or inadequately marked. There is some\n" +
                        "use of basic cohesive devices, which may be\n" +
                        "inaccurate or repetitive.\n" +
                        "There is inaccurate use or a lack of\n" +
                        "substitution or referencing.\n" +
                        "- Band 3: There is no apparent logical organisation.\n" +
                        "Ideas are discernible but difficult to relate\n" +
                        "to each other.\n" +
                        "Minimal use of sequencers or cohesive\n" +
                        "devices. Those used do not necessarily\n" +
                        "indicate a logical relationship between\n" +
                        "ideas.\n" +
                        "There is difficulty in identifying referencing" +
                        "- Band 2: There is little relevant message, or the\n" +
                        "entire response may be off-topic.\n" +
                        "There is little evidence of control of\n" +
                        "organisational features." +
                        "- Band 1: Responses of 20 words or fewer are\n" +
                        "rated at Band 1.\n" +
                        "The writing fails to communicate any\n" +
                        "message and appears to be by a virtual\n" +
                        "non-writer" +
                        "- Band 0: Should only be used where a candidate did not attend or attempt the question in any way, used a language other than English throughout, or where there is proof that a candidate’s answer has been totally\n" +
                        "memorised.";

        public static final String IELTS_PUBLIC_DESCRIPTORS_WritingTask1_LexicalResource =
                "**Lexical Resource**:\n" +
                        "- Band 9: Full flexibility and precise use are evident\n" +
                        "within the scope of the task.\n" +
                        "A wide range of vocabulary is used accurately\n" +
                        "and appropriately with very natural and\n" +
                        "sophisticated control of lexical features.\n" +
                        "Minor errors in spelling and word formation\n" +
                        "are extremely rare and have minimal impact\n" +
                        "on communication." +
                        "- Band 8: A wide resource is fluently and flexibly used\n" +
                        "to convey precise meanings within the scope\n" +
                        "of the task.\n" +
                        "There is skilful use of uncommon and/or\n" +
                        "idiomatic items when appropriate, despite\n" +
                        "occasional inaccuracies in word choice and\n" +
                        "collocation.\n" +
                        "Occasional errors in spelling and/or word\n" +
                        "formation may occur, but have minimal\n" +
                        "impact on communication." +
                        "- Band 7:  The resource is sufficient to allow some\n" +
                        "flexibility and precision.\n" +
                        "There is some ability to use less common\n" +
                        "and/or idiomatic items.\n" +
                        "An awareness of style and collocation is\n" +
                        "evident, though inappropriacies occur.\n" +
                        "There are only a few errors in spelling and/or\n" +
                        "word formation, and they do not detract\n" +
                        "from overall clarity." +
                        "- Band 6: The resource is generally adequate and\n" +
                        "appropriate for the task.\n" +
                        "The meaning is generally clear in spite of a\n" +
                        "rather restricted range or a lack of\n" +
                        "precision in word choice.\n" +
                        "If the writer is a risk-taker, there will be a\n" +
                        "wider range of vocabulary used but higher\n" +
                        "degrees of inaccuracy or inappropriacy.\n" +
                        "There are some errors in spelling and/or\n" +
                        "word formation, but these do not impede\n" +
                        "communication." +
                        "- Band 5: The resource is limited but minimally\n" +
                        "adequate for the task.\n" +
                        "Simple vocabulary may be used accurately\n" +
                        "but the range does not permit much\n" +
                        "variation in expression.\n" +
                        "There may be frequent lapses in\n" +
                        "the appropriacy of word choice, and a lack\n" +
                        "of flexibility is apparent in frequent\n" +
                        "simplifications and/or repetitions.\n" +
                        "Errors in spelling and/or word formation\n" +
                        "may be noticeable and may cause some\n" +
                        "difficulty for the reader.\n" +
                        "- Band 4: The resource is limited and inadequate for\n" +
                        "or unrelated to the task. Vocabulary is basic and\n" +
                        "may be used repetitively.\n" +
                        "There may be inappropriate use of lexical chunks\n" +
                        "(e.g. memorised phrases, formulaic language\n" +
                        "and/or language from the input material).\n" +
                        "Inappropriate word choice and/or errors in word\n" +
                        "formation and/or in spelling may impede\n" +
                        "meaning.\n" +
                        "- Band 3: The resource is inadequate (which may be due to\n" +
                        "the response being significantly underlength).\n" +
                        "Possible over-dependence on input material or\n" +
                        "memorised language.\n" +
                        "Control of word choice and/or spelling is very\n" +
                        "limited, and errors predominate. These errors may\n" +
                        "severely impede meaning." +
                        "- Band 2: The resource is extremely limited with few\n" +
                        "recognisable strings, apart from memorised\n" +
                        "phrases.\n" +
                        "There is no apparent control of word formation\n" +
                        "and/or spelling." +
                        "- Band 1: Responses of 20 words or fewer are rated at\n" +
                        "Band 1.\n" +
                        "No resource is apparent, except for a few isolated\n" +
                        "words." +
                        "- Band 0: Should only be used where a candidate did not attend or attempt the question in any way, used a language other than English throughout, or where there is proof that a candidate’s answer has been totally\n" +
                        "memorised.";

        public static final String IELTS_PUBLIC_DESCRIPTORS_WritingTask1_Grammar =
                "**Grammatical Range and Accuracy**:\n" +
                        "- Band 9: A wide range of structures within the scope\n" +
                        "of the task is used with full flexibility and\n" +
                        "control.\n" +
                        "Punctuation and grammar are used\n" +
                        "appropriately throughout.\n" +
                        "Minor errors are extremely rare and have\n" +
                        "minimal impact on communication" +
                        "- Band 8: A wide range of structures within the scope\n" +
                        "of the task is flexibly and accurately used.\n" +
                        "The majority of sentences are error-free, and\n" +
                        "punctuation is well managed.\n" +
                        "Occasional, non-systematic errors and\n" +
                        "inappropriacies occur, but have minimal\n" +
                        "impact on communication." +
                        "- Band 7: A variety of complex structures is used with\n" +
                        "some flexibility and accuracy.\n" +
                        "Grammar and punctuation are generally well\n" +
                        "controlled, and error-free sentences are\n" +
                        "frequent.\n" +
                        "A few errors in grammar may persist, but\n" +
                        "these do not impede communication." +
                        "- Band 6: A mix of simple and complex sentence\n" +
                        "forms is used but flexibility is limited.\n" +
                        "Examples of more complex structures are\n" +
                        "not marked by the same level of accuracy\n" +
                        "as in simple structures.\n" +
                        "Errors in grammar and punctuation occur,\n" +
                        "but rarely impede communication\n" +
                        "- Band 5: The range of structures is limited and\n" +
                        "rather repetitive.\n" +
                        "Although complex sentences are\n" +
                        "attempted, they tend to be faulty, and the\n" +
                        "greatest accuracy is achieved on simple\n" +
                        "sentences.\n" +
                        "Grammatical errors may be frequent and\n" +
                        "cause some difficulty for the reader.\n" +
                        "Punctuation may be faulty." +
                        "- Band 4: A very limited range of structures is\n" +
                        "used.\n" +
                        "Subordinate clauses are rare and\n" +
                        "simple sentences predominate.\n" +
                        "Some structures are produced accurately\n" +
                        "but grammatical errors are frequent and\n" +
                        "may impede meaning.\n" +
                        "Punctuation is often faulty or inadequate.\n" +
                        "- Band 3: Sentence forms are attempted, but\n" +
                        "errors in grammar and punctuation\n" +
                        "predominate (except in memorised\n" +
                        "phrases or those taken from the input\n" +
                        "material). This prevents most meaning\n" +
                        "from coming through.\n" +
                        "Length may be insufficient to provide\n" +
                        "evidence of control of sentence forms." +
                        "- Band 2: There is little or no evidence of sentence\n" +
                        "forms (except in memorised phrases).\n" +
                        "- Band 1: Responses of 20 words or fewer are\n" +
                        "rated at Band 1.\n" +
                        "No rateable language is evident." +
                        "- Band 0: Should only be used where a candidate did not attend or attempt the question in any way, used a language other than English throughout, or where there is proof that a candidate’s answer has been totally\n" +
                        "memorised.";



    //Prompt cho Writing 1
    private String buildTask1Prompt(String question, String answer) {
        return "You must return response strictly in JSON format.\n" +
                "You are an IELTS examiner analyzing Academic Writing Task 1 based on visual data. Apply extremely strict grading criteria to BOTH data accuracy and language quality (grammar, vocabulary, and spelling).\n" +
                "For all other issues (grammar, academic vocabulary, spelling, sentence structure), ONLY include the smallest possible incorrect unit (usually a word or short phrase) in 'originalText'. Do NOT include full sentences for these error types.\n"+
        "You must carefully cross-check EVERY numerical figure, comparison, and trend against the chart/table/image provided in the question, AND also rigorously check the student's grammar, academic vocabulary, spelling, and sentence structure.\n" +
                "IF ANY numerical value, trend description, date, or percentage does NOT MATCH the data from the visual, you MUST add it to the errorCorrections list by errorType wrong data\n" +
                "1. DATA ANALYSIS REQUIREMENTS:\n" +
                "- Verify ALL data points/trends between visual and student's description\n" +
                "- Only mark discrepancies in errorCorrections if the student's paraphrase changes the original meaning or data value\n" +
                "- Accept paraphrasing if the numerical meaning and trend are accurately conveyed (e.g., “just over 50%” is acceptable for 52%)\n" +
                "- Check for accurate time references (past/present/future tenses)\n\n" +

                "2. EVALUATION CRITERIA (Official IELTS Band Descriptors):\n" +
                "**Task Achievement** (Must include):\n" +
                "- Clear overview paragraph (missing = automatic Band 5 cap)\n" +
                "- Accurate data reporting (1 major error = -0.5 band)\n" +
                "- Appropriate feature selection (minimum 3 key features for Band 6+)\n" +
                "- Logical grouping of information\n" +
                "- " + IELTS_PUBLIC_DESCRIPTORS_WritingTask1_TaskAchievement + "\n\n" +

                "**Coherence and Cohesion** (Must include):\n" +
                "- Logical paragraphing (Introduction/Overview/Details)\n" +
                "- Effective linking (minimum 4 different cohesive devices for Band 6+)\n" +
                "- Progression beyond simple listing (required for Band 7+)\n" +
                "- " + IELTS_PUBLIC_DESCRIPTORS_WritingTask1_CoherenceCohesion + "\n\n" +

                "**Lexical Resource** (Must include):\n" +
                "- Minimum 5 academic vocabulary items (e.g., 'fluctuate' not 'go up and down')\n" +
                "- Accurate collocations (e.g., 'sharp increase' not 'fast increase')\n" +
                "- Spelling (3 errors = -0.5 band)\n" +
                "- " + IELTS_PUBLIC_DESCRIPTORS_WritingTask1_LexicalResource + "\n\n" +

                "**Grammatical Range and Accuracy** (Must include):\n" +
                "- Minimum 3 complex structures per paragraph (Band 6+ requirement)\n" +
                "- Tense consistency (especially for time-based data)\n" +
                "- Punctuation accuracy (comma errors = -0.5 band)\n" +
                "- " + IELTS_PUBLIC_DESCRIPTORS_WritingTask1_Grammar + "\n\n" +

                "3. AUTOMATIC BAND CAPS:\n" +
                "- No overview paragraph = maximum Band 5\n" +
                "- Data inaccuracy = maximum Band 6.5\n" +
                "- Majority simple sentences = maximum Band 6\n" +
                "- General vocabulary only = maximum Band 6\n\n" +

                "4. SCORING SYSTEM:\n" +
                "- 0.5 band deduction per 2 major errors\n" +
                "- Band 9 = Expert | 7.5-8.5 = Good | 6.0-7.0 = Competent | 5.5 = Limited | ≤5.0 = Problematic\n\n" +

                "5. ERROR CORRECTION RULES:\n" +
                "- For errorType 'wrong data', you MAY use the full sentence as 'originalText' if necessary to clearly show the data inaccuracy.\n" +
                "- For errorTypes 'grammar', 'spelling', and 'vocabulary', you MUST only include the smallest incorrect unit (usually 1 word or a short phrase) in 'originalText'. Do NOT include full sentences for these types.\n" +
                "- Each error must be listed separately, even if they appear in the same sentence.\n" +
                "- Always include the full sentence in 'sentenceContext' for clarity.\n" +
                "- Do NOT ignore grammar, vocabulary, or spelling issues — they MUST be corrected even if the data is accurate.\n"

                +

        "RESPONSE FORMAT:\n" +
                "{\n" +
                "  \"score\": number (e.g. 6.5),\n" +
                "  \"feedback\": {\n" +
                "    \"errorCorrections (Follow ERROR CORRECTION RULES:)\": [{\n" +
                "      \"originalText\": string, // Full sentence only for wrong data; word/phrase for other error types\n" +
                "      \"correctedText\": string,(You must fix correctly)\n" +
                "      \"errorType\": \"vocabulary/spelling/grammar/wrong data\",\n" +
                "      \"explanation\": string,\n" +
                "      \"sentenceContext\": \"full original sentence\"\n" +
                "    }],\n" +
                "    \"sentenceImprovements\": [{\n" +
                "      \"originalSentence\": string,\n" +
                "      \"improvedSentence\": string,\n" +
                "      \"techniquesUsed\": [\"academic vocab\", \"complex structure\", etc],\n" +
                "      \"bandBoost\": string (e.g. \"6 → 6.5\")\n" +
                "    }],\n" +
                "    \"overallComment\": string (100+ words)\n" +
                "  },\n" +
                "  \"evaluation\": {\n" +
                "    \"TaskAchievement\": {\"score\": number, \"review\": string},\n" +
                "    \"CoherenceCohesion\": {\"score\": number, \"review\": string},\n" +
                "    \"LexicalResource\": {\"score\": number, \"review\": string},\n" +
                "    \"Grammar\": {\"score\": number, \"review\": string}\n" +
                "  },\n" +
                "  \"sampleAnswer\": string (optional Band 9 model)\n" +
                "}\n\n" +

                "QUESTION:\n" + question + "\n\n" +
                "STUDENT'S ANSWER:\n" + answer;
    }
    private static final String IELTS_PUBLIC_DESCRIPTORS_WritingTask2_TaskResponse =
            "• Band 9: The prompt is appropriately addressed and explored in depth.\n" +
                    "A clear and fully developed position is presented which directly\n" +
                    "answers the question/s.\n" +
                    "Ideas are relevant, fully extended and well supported.\n" +
                    "Any lapses in content or support are extremely rare.\n" +
                    "• Band 8: The prompt is appropriately and sufficiently addressed.\n" +
                    "A clear and well-developed position is presented in response to the\n" +
                    "question/s.\n" +
                    "Ideas are relevant, well extended and supported.\n" +
                    "There may be occasional omissions or lapses in content." +
                    "• Band 7: The main parts of the prompt are appropriately addressed.\n" +
                    "A clear and developed position is presented.\n" +
                    "Main ideas are extended and supported but there may be a\n" +
                    "tendency to over-generalise or there may be a lack of focus and\n" +
                    "precision in supporting ideas/material." +
                    "• Band 6: The main parts of the prompt are addressed (though some may be\n" +
                    "more fully covered than others). An appropriate format is used.\n" +
                    "A position is presented that is directly relevant to the prompt,\n" +
                    "although the conclusions drawn may be unclear, unjustified or\n" +
                    "repetitive.\n" +
                    "Main ideas are relevant, but some may be insufficiently developed\n" +
                    "or may lack clarity, while some supporting arguments and evidence\n" +
                    "may be less relevant or inadequate.\n" +
                    "• Band 5: The main parts of the prompt are incompletely addressed. The\n" +
                    "format may be inappropriate in places.\n" +
                    "The writer expresses a position, but the development is not always\n" +
                    "clear.\n" +
                    "Some main ideas are put forward, but they are limited and are not\n" +
                    "sufficiently developed and/or there may be irrelevant detail.\n" +
                    "There may be some repetition.\n" +
                    "• Band 4: The prompt is tackled in a minimal way, or the answer is\n" +
                    "tangential, possibly due to some misunderstanding of\n" +
                    "the prompt. The format may be inappropriate.\n" +
                    "A position is discernible, but the reader has to read\n" +
                    "carefully to find it.\n" +
                    "Main ideas are difficult to identify and such ideas that\n" +
                    "are identifiable may lack relevance, clarity and/or\n" +
                    "support.\n" +
                    "Large parts of the response may be repetitive. " +
                    "• Band 3: No part of the prompt is adequately addressed, or the\n" +
                    "prompt has been misunderstood.\n" +
                    "No relevant position can be identified, and/or there is\n" +
                    "little direct response to the question/s.\n" +
                    "There are few ideas, and these may be irrelevant or\n" +
                    "insufficiently developed.\n" +
                    "• Band 2: The content is barely related to the prompt.\n" +
                    "No position can be identified.\n" +
                    "There may be glimpses of one or two ideas without\n" +
                    "development. " +
                    "• Band 1: Responses of 20 words or fewer are rated at Band 1.\n" +
                    "The content is wholly unrelated to the prompt.\n" +
                    "Any copied rubric must be discounted." +
                    "• Band 0: Should only be used where a candidate did not attend or attempt the question in any way, used a language other than English throughout, or where there is proof that a candidate’s answer has been totally memorised."

            ;

    private static final String IELTS_PUBLIC_DESCRIPTORS_WritingTask2_CoherenceCohesion =
                    "• Band 9: The message can be followed effortlessly.\n" +
                            "Cohesion is used in such a way that it very\n" +
                            "rarely attracts attention.\n" +
                            "Any lapses in coherence or cohesion are\n" +
                            "minimal.\n" +
                            "Paragraphing is skilfully managed.\n" +
                    "• Band 8: The message can be followed with ease.\n" +
                            "Information and ideas are logically sequenced,\n" +
                            "and cohesion is well managed.\n" +
                            "Occasional lapses in coherence and cohesion\n" +
                            "may occur.\n" +
                            "Paragraphing is used sufficiently and\n" +
                            "appropriately. " +
                    "• Band 7: Information and ideas are logically organised,\n" +
                            "and there is a clear progression throughout\n" +
                            "the response. (A few lapses may occur, but\n" +
                            "these are minor.)\n" +
                            "A range of cohesive devices including\n" +
                            "reference and substitution is used flexibly but\n" +
                            "with some inaccuracies or some over/under\n" +
                            "use.\n" +
                            "Paragraphing is generally used effectively to\n" +
                            "support overall coherence, and the sequencing\n" +
                            "of ideas within a paragraph is generally logical.   " +
                    "• Band 6: Information and ideas are generally arranged\n" +
                            "coherently and there is a clear overall\n" +
                            "progression.\n" +
                            "Cohesive devices are used to some good effect\n" +
                            "but cohesion within and/or between sentences\n" +
                            "may be faulty or mechanical due to misuse,\n" +
                            "overuse or omission.\n" +
                            "The use of reference and substitution may lack\n" +
                            "flexibility or clarity and result in some\n" +
                            "repetition or error.\n" +
                            "Paragraphing may not always be logical and/or\n" +
                            "the central topic may not always be clear.\n" +
                    "• Band 5: Organisation is evident but is not wholly logical\n" +
                            "and there may be a lack of overall progression.\n" +
                            "Nevertheless, there is a sense of underlying\n" +
                            "coherence to the response.\n" +
                            "The relationship of ideas can be followed but\n" +
                            "the sentences are not fluently linked to each\n" +
                            "other.\n" +
                            "There may be limited/overuse of cohesive\n" +
                            "devices with some inaccuracy.\n" +
                            "The writing may be repetitive due to\n" +
                            "inadequate and/or inaccurate use of reference\n" +
                            "and substitution.\n" +
                            "Paragraphing may be inadequate or\n" +
                            "missing.\n" +
                    "• Band 4: Information and ideas are evident but not arranged\n" +
                            "coherently and there is no clear progression within the\n" +
                            "response.\n" +
                            "Relationships between ideas can be unclear and/or\n" +
                            "inadequately marked. There is some use of basic cohesive\n" +
                            "devices, which may be inaccurate or repetitive.\n" +
                            "There is inaccurate use or a lack of substitution or\n" +
                            "referencing.\n" +
                            "There may be no paragraphing and/or no clear main topic\n" +
                            "within paragraphs" +
                    "• Band 3: There is no apparent logical organisation. Ideas are\n" +
                            "discernible but difficult to relate to each other.\n" +
                            "There is minimal use of sequencers or cohesive devices.\n" +
                            "Those used do not necessarily indicate a logical relationship\n" +
                            "between ideas.\n" +
                            "There is difficulty in identifying referencing.\n" +
                            "Any attempts at paragraphing are unhelpful." +
                    "• Band 2: There is little relevant message, or the entire response may\n" +
                            "be off-topic.\n" +
                            "There is little evidence of control of organisational features." +
                    "• Band 1: Responses of 20 words or fewer are rated at Band 1.\n" +
                            "The writing fails to communicate any message and appears\n" +
                            "to be by a virtual non-writer.\n" +
                    "• Band 0: Should only be used where a candidate did not attend or attempt the question in any way, used a language other than English throughout, or where there is proof that a candidate’s answer has been totally memorised.";


    private static final String IELTS_PUBLIC_DESCRIPTORS_WritingTask2_LexicalResource =
            "• Band 9:  Full flexibility and precise use are widely\n" +
                    "evident.\n" +
                    "A wide range of vocabulary is used\n" +
                    "accurately and appropriately with very\n" +
                    "natural and sophisticated control of lexical\n" +
                    "features.\n" +
                    "Minor errors in spelling and word\n" +
                    "formation are extremely rare and hav" +
                    "• Band 8:  A wide resource is fluently and flexibly\n" +
                    "used to convey precise meanings.\n" +
                    "There is skilful use of uncommon and/or\n" +
                    "idiomatic items when appropriate, despite\n" +
                    "occasional inaccuracies in word choice and\n" +
                    "collocation.\n" +
                    "Occasional errors in spelling and/or word\n" +
                    "formation may occur, but have minimal\n" +
                    "impact on communication" +
                    "• Band 7: The resource is sufficient to allow some\n" +
                    "flexibility and precision.\n" +
                    "There is some ability to use less common\n" +
                    "and/or idiomatic items.\n" +
                    "An awareness of style and collocation is\n" +
                    "evident, though inappropriacies occur.\n" +
                    "There are only a few errors in spelling\n" +
                    "and/or word formation and they do not\n" +
                    "detract from overall clarity. " +
                    "• Band 6: The resource is generally adequate and\n" +
                    "appropriate for the task.\n" +
                    "The meaning is generally clear in spite of a\n" +
                    "rather restricted range or a lack of\n" +
                    "precision in word choice.\n" +
                    "If the writer is a risk-taker, there will be a\n" +
                    "wider range of vocabulary used but higher\n" +
                    "degrees of inaccuracy or inappropriacy.\n" +
                    "There are some errors in spelling and/or\n" +
                    "word formation, but these do not impede\n" +
                    "communication.\n" +
                    "• Band 5: The resource is limited but minimally\n" +
                    "adequate for the task.\n" +
                    "Simple vocabulary may be used accurately\n" +
                    "but the range does not permit much\n" +
                    "variation in expression.\n" +
                    "There may be frequent lapses in the\n" +
                    "appropriacy of word choice and a lack of\n" +
                    "flexibility is apparent in frequent\n" +
                    "simplifications and/or repetitions.\n" +
                    "Errors in spelling and/or word formation\n" +
                    "may be noticeable and may cause some\n" +
                    "difficulty for the reader.\n" +
                    "• Band 4: The resource is limited and inadequate for\n" +
                    "or unrelated to the task. Vocabulary is\n" +
                    "basic and may be used repetitively.\n" +
                    "There may be inappropriate use of lexical\n" +
                    "chunks (e.g. memorised phrases, formulaic\n" +
                    "language and/or language from the input\n" +
                    "material).\n" +
                    "Inappropriate word choice and/or errors in\n" +
                    "word formation and/or in spelling\n" +
                    "may impede meaning.  " +
                    "• Band 3: The resource is inadequate (which may be\n" +
                    "due to the response being significantly\n" +
                    "underlength). Possible over-dependence on\n" +
                    "input material or memorised language.\n" +
                    "Control of word choice and/or spelling is\n" +
                    "very limited, and errors predominate. These\n" +
                    "errors may severely impede meaning." +
                    "• Band 2: The resource is extremely limited with few\n" +
                    "recognisable strings, apart from memorised\n" +
                    "phrases.\n" +
                    "There is no apparent control of word\n" +
                    "formation and/or spelling." +
                    "• Band 1: Responses of 20 words or fewer are rated\n" +
                    "at Band 1.\n" +
                    "No resource is apparent, except for a few\n" +
                    "isolated words." +
                    "• Band 0: Should only be used where a candidate did not attend or attempt the question in any way, used a language other than English throughout, or where there is proof that a candidate’s answer has been totally memorised.";


    private static final String IELTS_PUBLIC_DESCRIPTORS_WritingTask2_Grammar =
            "• Band 9: A wide range of structures is used with full\n" +
                    "flexibility and control.\n" +
                    "Punctuation and grammar are used\n" +
                    "appropriately throughout.\n" +
                    "Minor errors are extremely rare and have\n" +
                    "minimal impact on communication.\n" +
                    "• Band 8: A wide range of structures is flexibly and\n" +
                    "accurately used.\n" +
                    "The majority of sentences are error-free,\n" +
                    "and punctuation is well managed.\n" +
                    "Occasional, non-systematic errors and\n" +
                    "inappropriacies occur, but have minimal\n" +
                    "impact on communication.\n" +
                    "• Band 7:  A variety of complex structures is used\n" +
                    "with some flexibility and accuracy.\n" +
                    "Grammar and punctuation are generally\n" +
                    "well controlled, and error-free sentences\n" +
                    "are frequent.\n" +
                    "A few errors in grammar may persist, but\n" +
                    "these do not impede communication.\n" +
                    "• Band 6: A mix of simple and complex sentence\n" +
                    "forms is used but flexibility is limited.\n" +
                    "Examples of more complex structures are\n" +
                    "not marked by the same level of accuracy\n" +
                    "as in simple structures.\n" +
                    "Errors in grammar and punctuation occur,\n" +
                    "but rarely impede communication.\n" +
                    "• Band 5: The range of structures is limited and\n" +
                    "rather repetitive.\n" +
                    "Although complex sentences are\n" +
                    "attempted, they tend to be faulty, and the\n" +
                    "greatest accuracy is achieved on simple\n" +
                    "sentences.\n" +
                    "Grammatical errors may be frequent and\n" +
                    "cause some difficulty for the reader.\n" +
                    "Punctuation may be faulty.\n" +
                    "• Band 4: A very limited range of structures is\n" +
                    "used.\n" +
                    "Subordinate clauses are rare and\n" +
                    "simple sentences predominate.\n" +
                    "Some structures are produced\n" +
                    "accurately but grammatical errors are\n" +
                    "frequent and may impede meaning.\n" +
                    "Punctuation is often faulty or\n" +
                    "inadequate" +
                    "• Band 3: Sentence forms are attempted, but\n" +
                    "errors in grammar and punctuation\n" +
                    "predominate (except in memorised\n" +
                    "phrases or those taken from the input\n" +
                    "material). This prevents most meaning\n" +
                    "from coming through.\n" +
                    "Length may be insufficient to\n" +
                    "provide evidence of control of\n" +
                    "sentence forms. " +
                    "• Band 2: There is little or no evidence of\n" +
                    "sentence forms (except in memorised\n" +
                    "phrases).\n" +
                    "• Band 1: Responses of 20 words or fewer are\n" +
                    "rated at Band 1.\n" +
                    "No rateable language is evident.\n" +
                    "• Band 0: Should only be used where a candidate did not attend or attempt the question in any way, used a language other than English throughout, or where there is proof that a candidate’s answer has been totally memorised.";



    private String buildTask2Prompt(String question, String answer) {
        String promptBuilder2 =
                "You must return response strictly in JSON format.\n" +
                        "You are an IELTS examiner evaluating Writing Task 2 based on the four official criteria: Task Response, Coherence and Cohesion, Lexical Resource, and Grammatical Range and Accuracy. Apply **extremely strict** Band Descriptor standards.\n" +
                        "\n" +
                        "1. Read and understand:\n" +
                        "- The essay question (requirements and context)\n" +
                        "- The full candidate response (content, organization, vocabulary, grammar)\n" +
                        "\n" +
                        "2. Evaluate based on the following criteria:\n" +
                        "\n" +
                        "• Task Response (25%):\n" +
                        "- Does the candidate address **all parts** of the task?\n" +
                        "- Are ideas **clearly presented**, extended and supported with **examples or explanations**?\n" +
                        "- Penalize over-generalization or lack of development (e.g. cap at Band 6).\n" +
                        "- Absence of clear position or conclusion → max Band 6\n" +
                        "- Fully off-topic → max Band 3\n" +
                        IELTS_PUBLIC_DESCRIPTORS_WritingTask2_TaskResponse + "\n" +
                        "\n" +
                        "• Coherence and Cohesion (25%):\n" +
                        "- Logical organization of information\n" +
                        "- Use of cohesive devices (avoid under-/over-use)\n" +
                        "- Effective and logical paragraphing (Intro, Body, Conclusion)\n" +
                        "- Listing-type organization or faulty cohesion → cap at Band 6\n" +
                        IELTS_PUBLIC_DESCRIPTORS_WritingTask2_CoherenceCohesion + "\n" +
                        "\n" +
                        "• Lexical Resource (25%):\n" +
                        "- Range and accuracy of vocabulary\n" +
                        "- Use of less common words and collocations\n" +
                        "- Penalize frequent repetition or incorrect word usage\n" +
                        "- Apply +0.25 bonus if candidate uses any of:\n" +
                        "   • Academic vocabulary accurately (e.g., mitigate, infrastructure)\n" +
                        "   • Formal collocations (e.g., play a crucial role)\n" +
                        "   • Idiomatic expressions (e.g., a double-edged sword) appropriately\n" +
                        "   • Effective paraphrasing of key terms\n" +
                        IELTS_PUBLIC_DESCRIPTORS_WritingTask2_LexicalResource + "\n" +
                        "\n" +
                        "• Grammatical Range and Accuracy (25%):\n" +
                        "- Variety of sentence structures (simple, complex, compound)\n" +
                        "- Use of advanced grammar (e.g., conditionals, clauses, inversion)\n" +
                        "- Control of punctuation\n" +
                        "- Apply +0.25 bonus if:\n" +
                        "   • ≥80% of sentences are complex/compound AND mostly error-free\n" +
                        "   • Error-free sentence rate is ≥60% with no major mistakes\n" +
                        IELTS_PUBLIC_DESCRIPTORS_WritingTask2_Grammar + "\n" +
                        "\n" +
                        "3. Scoring Policy:\n" +
                        "- Final band = average of 4 criteria (rounded to nearest 0.5)\n" +
                        "- Deduct 0.5 for every 2 major lexical or grammar errors\n" +
                        "- Missing overview/conclusion = max 5.0\n" +
                        "- Data misreporting = max 6.5\n" +
                        "\n" +
                        "4. Output Format:\n" +
                        "{\n" +
                        "  score: number (e.g., 6.5),\n" +
                        "  evaluation: {\n" +
                        "    TaskAchievement: {scoreEva: string, reviewEva: string},\n" +
                        "    CoherenceCohesion: {scoreEva: string, reviewEva: string},\n" +
                        "    LexicalResource: {scoreEva: string, reviewEva: string},\n" +
                        "    Grammar: {scoreEva: string, reviewEva: string}\n" +
                        "  },\n" +
                        "  feedback: {\n" +
                        "    errorCorrections: [{\n" +
                        "      originalText: string,\n" +
                        "      correctedText: string,\n" +
                        "      errorType: string,\n" +
                        "      explanation: string,\n" +
                        "      sentenceContext: string\n" +
                        "    }],\n" +
                        "    sentenceImprovements: [{\n" +
                        "      originalSentence: string,\n" +
                        "      improvedSentence: string,\n" +
                        "      techniquesUsed: [string],\n" +
                        "      bandBoost: string\n" +
                        "    }],\n" +
                        "    overallComment: string\n" +
                        "  },\n" +
                        "  sampleAnswer: string (optional Band 9)\n" +
                        "}\n" +
                        "\n" +
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
