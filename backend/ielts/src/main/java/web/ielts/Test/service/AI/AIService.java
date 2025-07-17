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
                return buildSpeakingPart2Prompt(question, transcript, cueCard);
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
            "- Vocabulary: repetition\n" +
            "\n" +
            "• Coherence / Logic:\n" +
            "- Coherence: unclear meaning\n" +
            "- Coherence: repetition of ideas\n" +
            "- Coherence: poor connection\n" +
            "- Coherence: off-topic\n" +
            "- Coherence: abrupt transition\n" +
            "- Coherence: lack of cohesion devices\n" +
            "- Coherence: disorganized idea structure\n" +
            "- Coherence: unsupported point\n" +
            "\n" +
            "• Fluency-related:\n" +
            "- Fluency: frequent hesitation\n" +
            "- Fluency: excessive self-correction\n" +
            "- Fluency: unnatural pause\n" +
            "- Fluency: slow delivery\n" +
            "- Fluency: choppy rhythm\n"+
            "\n"+
            "• Pronunciation-related:\n" +
            "- Pronunciation: incorrect word stress\n" +
            "- Pronunciation: incorrect intonation pattern\n"
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
                        "Note: For simple factual questions in Part 1 (e.g., “What is your name?”), brief but relevant answers are acceptable. Do not penalize short responses if they clearly address the question.\n" +

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
//                        "Fluency features based on acoustic analysis: {meanIntensity}, {speechRate}, {pauseCount} in\n" + praatResults+
                        "\n" +
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
                        "RESPONSE FORMAT:\n" +
                        "- score: decimal (overall band score, e.g. 6.5)\n" +
                        "- transcript: string ( transcript of the original answer)\n" +
                        "- feedback: {\n" +
                       "\"(In `errorCorrections`, include only short phrases or words that are clearly incorrect due to **grammar** or **lexical resource** problems. \" +\n" +
                        "\"Do NOT include any issues related to **fluency**, **coherence**, or **pronunciation** such as hesitations, pauses, repetition, unnatural phrasing, or unclear idea progression.)\\n\\n\" +\n" +
                        "\n" +
                        "\"Each entry in the `errorCorrections` array must follow this format:\\n\" +\n" +
                        "\"errorCorrections: [{\\n\" +\n" +
                        "\"  originalText: string,            // The incorrect word or phrase\\n\" +\n" +
                        "\"  correctedText: string,           // The corrected version\\n\" +\n" +
                        "\"  errorType: string,               // Only 'Grammar' or 'Lexical' allowed\\n\" +\n" +
                        "\"  explanation: string,             // Why it was incorrect (e.g., wrong tense, article usage, word choice)\\n\" +\n" +
                        "\"  sentenceContext: string          // The full sentence where the error occurred\\n\" +\n" +
                        "\"}]\\n\\n\" +\n" +
                        "\n" +
                        "\"\uD83D\uDEAB Do NOT include:\\n\" +\n" +
                        "\"- Pauses, repetitions, fillers like 'uh', 'um'\\n\" +\n" +
                        "\"- Vague or disorganized sentence structure\\n\" +\n" +
                        "\"- Cohesion or logic issues (these go in Fluency and Coherence section)\\n\\n\" +\n" +
                        "\n" +
                        "\"✅ Only include:\\n\" +\n" +
                        "\"- Grammar errors (e.g., subject-verb agreement, tense, preposition, article)\\n\" +\n" +
                        "\"- Lexical errors (e.g., wrong word choice, inappropriate collocation)\\n\";"+

                        "},\n" + // ✅ Đóng đúng object feedback
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
                        "LexicalResource: {scoreEva: string, reviewEva: string},\n" +
                        "Grammar: {scoreEva: string, reviewEva: string}\n" +
                        "Fluency and coherence: {scoreEva: string, reviewEva: string}\n"+
                        "Pronunciation: {scoreEva: string, reviewEva: string}\n"+
                        "},\n" +
                        "- sampleAnswer: string (Optional band 9 model)\n" +
                        "Question:\n" + questions + "\n" +
                        "Original Answer:\n" + transcript;

        return speakingPart1;
    }


    public String buildSpeakingPart2Prompt(String question,JsonNode transcipt,List<String> cueCards){
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
                        "You must only select errorType from the following list. Do not invent or rephrase. Do not include any punctuation-related error types."
                        +errorType+
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
                        "2. SCORING SYSTEM:\n" +
                        "   9.0 = Expert | 7.5-8.5 = Good | 6.0-7.0 = Competent | 5.5 = Limited | ≤5.0 = Problematic\n" +
                        "   - Deduct 0.5 band per 2 major errors\n" +
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
                        "    Fluency and coherence: {scoreEva: string, reviewEva: string}\n" +
                        "}\n" +
                        "sampleAnswer: string (Optional band 9 model)\n" +
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
                        "You must only select errorType from the following list. Do not invent or rephrase. Do not include any punctuation-related error types."
                        +errorType+

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
                        "- score: decimal (overall band score, e.g. 6.5)\n" +
                        "- feedback: {\n" +
                        "    (In errorCorrections, only include corrections where the originalText is clearly incorrect in terms of LexicalResource,grammar or fluency/coherence)\n" +
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
                        "    Fluency and coherence: {scoreEva: string, reviewEva: string}\n" +
                        "}\n" +
                        "sampleAnswer: string (Optional band 9 model)\n" +
                        "Question:\n" + questions + "\n" +
                        "Original Answer:\n" + transcipt;

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
    private static final String IELTS_PUBLIC_DESCRIPTORSLexicalResourceWrtingTask2 =
            "  - IELTS Public Descriptors:\n" +
                     "• Band 9: Full flexibility and precise use are widely evident. A wide range of vocabulary is used accurately and appropriately with very natural and sophisticated control of lexical features. Minor errors in spelling and word formation are extremely rare and have minimal impact on communication.\n" +
                    "\n" +
                    "• Band 8: A wide resource is fluently and flexibly used to convey precise meanings. There is skilful use of uncommon and/or idiomatic items when appropriate, despite occasional inaccuracies in word choice and collocation. Occasional errors in spelling and/or word formation may occur, but have minimal impact on communication.\n" +
                    "\n" +
                    "• Band 7: The resource is sufficient to allow some flexibility and precision. There is some ability to use less common and/or idiomatic items. An awareness of style and collocation is evident, though inappropriacies occur. There are only a few errors in spelling and/or word formation and they do not detract from overall clarity.\n" +
                    "\n" +
                    "• Band 6: The resource is generally adequate and appropriate for the task. The meaning is generally clear in spite of a rather restricted range or a lack of precision in word choice. If the writer is a risk-taker, there will be a wider range of vocabulary used but higher degrees of inaccuracy or inappropriacy. There are some errors in spelling and/or word formation, but these do not impede communication.\n" +
                    "\n" +
                    "• Band 5: The resource is limited but minimally adequate for the task. Simple vocabulary may be used accurately but the range does not permit much variation in expression. There may be frequent lapses in the appropriacy of word choice and a lack of flexibility is apparent in frequent simplifications and/or repetitions. Errors in spelling and/or word formation may be noticeable and may cause some difficulty for the reader.\n" +
                    "\n" +
                    "• Band 4: The resource is limited and inadequate for or unrelated to the task. Vocabulary is basic and may be used repetitively. There may be inappropriate use of lexical chunks (e.g., memorised phrases, formulaic language and/or language from the input material). Inappropriate word choice and/or errors in word formation and/or in spelling may impede meaning.\n" +
                    "\n" +
                    "• Band 3: The resource is inadequate (which may be due to the response being significantly under-length). Possible over-dependence on input material or memorised language. Control of word choice and/or spelling is very limited, and errors predominate. These errors may severely impede meaning.\n" +
                    "\n" +
                    "• Band 2: The resource is extremely limited with few recognisable strings, apart from memorised phrases. There is no apparent control of word choice or spelling.\n" +
                    "\n" +
                    "• Band 1: Responses of 20 words or fewer are rated at Band 1. No resource is apparent, except for a few isolated words."
            ;
    private static final String IELTS_PUBLIC_DESCRIPTORSGrammarWrtingTask2 =
            "  - IELTS Public Descriptors: " +
                    "• Band 9: A wide range of structures is used with full flexibility and control. " +
                    "Punctuation and grammar are used appropriately throughout. " +
                    "Minor errors are extremely rare and have minimal impact on communication. " +

                    "• Band 8: A wide resource is fluently and flexibly used to convey precise meanings. " +
                    "There is skilfully use of uncommon and/or idiomatic items when appropriate, despite occasional inaccuracies in word choice and collocation. " +
                    "Occasional errors in spelling and/or word formation may occur, but have minimal impact on communication. " +

                    "• Band 7: A variety of complex structures is used with some flexibility and accuracy. " +
                    "Grammar and punctuation are generally well controlled, and error-free sentences are frequent. " +
                    "A few errors in grammar may persist, but these do not impede communication. " +

                    "• Band 6: A mix of simple and complex sentence forms is used but flexibility is limited. " +
                    "Examples of more complex structures are not marked by the same level of accuracy as in simple structures. " +
                    "Errors in grammar and punctuation occur, but rarely impede communication. " +

                    "• Band 5: The range of structures is limited and rather repetitive. " +
                    "Although complex sentences are attempted, they tend to be faulty, and the greatest accuracy is achieved on simple sentences. " +
                    "Grammatical errors may be frequent and cause some difficulty for the reader. " +
                    "Punctuation may be faulty. " +

                    "• Band 4: A very limited range of structures is used. " +
                    "Subordinate clauses are rare and simple sentences predominate. " +
                    "Some structures are produced accurately but grammatical errors are frequent and may impede meaning. " +
                    "Punctuation is often faulty or inadequate. " +

                    "• Band 3: Sentence forms are attempted, but errors in grammar and punctuation predominate (except in memorised phrases or those taken from the input material). " +
                    "This prevents most meaning from coming through. " +
                    "Length may be insufficient to provide evidence of control of sentence forms. " +

                    "• Band 2: There is little or no evidence of sentence forms (except in memorised phrases). " +

                    "• Band 1: Responses of 20 words or fewer are rated at Band 1. " +
                    "No rateable language is evident.";
    private static final String IELTS_PUBLIC_DESCRIPTORSTaskResponseTask2 =
            "  - IELTS Public Descriptors: " +
                    "• Band 9: The prompt is appropriately addressed and explored in depth.\n" +
                    "A clear and fully developed position is presented which directly\n" +
                    "answers the question/s.\n" +
                    "Ideas are relevant, fully extended and well supported.\n" +
                    "Any lapses in content or support are extremely rare. " +

                    "• Band 8: The prompt is appropriately and sufficiently addressed.\n" +
                    "A clear and well-developed position is presented in response to the\n" +
                    "question" +
                    "Ideas are relevant, well extended and supported.\n" +
                    "There may be occasional omissions or lapses in content.\n " +

                    "• Band 7: A variety of complex structures is used with some flexibility and accuracy. " +
                    "Grammar and punctuation are generally well controlled, and error-free sentences are frequent. " +
                    "A few errors in grammar may persist, but these do not impede communication. " +

                    "• Band 6: A mix of simple and complex sentence forms is used but flexibility is limited. " +
                    "Examples of more complex structures are not marked by the same level of accuracy as in simple structures. " +
                    "Errors in grammar and punctuation occur, but rarely impede communication. " +

                    "• Band 5: The range of structures is limited and rather repetitive. " +
                    "Although complex sentences are attempted, they tend to be faulty, and the greatest accuracy is achieved on simple sentences. " +
                    "Grammatical errors may be frequent and cause some difficulty for the reader. " +
                    "Punctuation may be faulty. " +

                    "• Band 4: A very limited range of structures is used. " +
                    "Subordinate clauses are rare and simple sentences predominate. " +
                    "Some structures are produced accurately but grammatical errors are frequent and may impede meaning. " +
                    "Punctuation is often faulty or inadequate. " +

                    "• Band 3: Sentence forms are attempted, but errors in grammar and punctuation predominate (except in memorised phrases or those taken from the input material). " +
                    "This prevents most meaning from coming through. " +
                    "Length may be insufficient to provide evidence of control of sentence forms. " +

                    "• Band 2: There is little or no evidence of sentence forms (except in memorised phrases). " +

                    "• Band 1: Responses of 20 words or fewer are rated at Band 1. " +
                    "No rateable language is evident.";

    private String buildTask2Prompt(String question, String answer) {
        String promptBuilder2 =
                "You must return response strictly in JSON format.\n" +
                        "You are an IELTS examiner analyzing Writing Task 2. Extremely strict grading" +
                        "Before evaluation, you must first carefully understand:\n" +
                        "1. The question being asked (context and requirements)\n" +
                        "2. The full transcript of the user's response (content, grammar, vocabulary)\n" +
                        "3. You must evaluate whether the response is relevant to the question and does not go off-topic.\n" +
                        "If the response is completely off-topic, you must give Band 3.0 for fluency and coherence\n" +
                        "4. You must strictly check if the candidate addresses **all bullet points** in the cue card:\n" +
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
                        "✅ +0.25 bonus to the Lexical Resource score if the candidate fulfills at least one of the following:\n" +
                        "Accurate use of academic vocabulary\n" +
                        "e.g., mitigate, infrastructure, sustainability\n" +
                        "\n" +
                        "Correct use of academic collocations\n" +
                        "e.g., “pose a threat,” “play a crucial role,” “bring about change”\n" +
                        "\n" +
                        "Use of idiomatic expressions that are appropriate for formal writing\n" +
                        "e.g., “a double-edged sword,” “a stepping stone to success”\n" +
                        "\n" +
                        "Effective paraphrasing of key task terms\n" +
                        "e.g., “young people” → “the younger generation,” “adolescents”\n" +
                        "\n" +
                        "Consistently appropriate word choice, with no significant vocabulary errors throughout the essay\n" +
                        "\n"+
                        " Deduct 0.5 point in total for that error type about Lexical Resource  (only once)\"+\n" +
                        " Moreover, apply the following criteria to ensure a more accurate and appropriate evaluation:\" +"+
                        IELTS_PUBLIC_DESCRIPTORSLexicalResourceWrtingTask2+
                        "• Grammar (25%):\n" +
                       " +0.25 point\n" +
                        "The candidate demonstrates a wide range of complex grammatical structures (e.g., conditionals, inversion, cleft sentences, relative clauses…) with high accuracy, and the remaining errors do not affect meaning.\n" +
                        "→ Apply this if the candidate is around Band 6–7 but shows strong evidence of reaching Band 8:\n" +
                        "Several complex or uncommon structures are used\n" +
                        "These structures are mostly accurate\n" +
                        "Remaining grammatical errors are minor and do not hinder understanding\n" +
                        " Deduct 0.5 point in total for that error type about Grammar  (only once)\"+\n" +
                        " Moreover, apply the following criteria to ensure a more accurate and appropriate evaluation:\" +"+
                        IELTS_PUBLIC_DESCRIPTORSGrammarWrtingTask2+
                        " +0.25 point\n" +
                        "Over 80% of all sentences are compound or complex, and most of them are grammatically and punctuation accurate.\n" +
                        "→ Apply this when there is clear evidence of control over a variety of clauses such as: Relative clauses,Adverbial clauses,Noun clauses,Correct usage of punctuation (commas, semicolons, etc.)" +
                        " +0.25 point\n" +
                        "There are no serious grammar errors throughout the essay, and the proportion of error-free sentences is ≥ 60%.\n" +
                        "→ Apply this if the overall accuracy is high, even if the grammatical range is not very wide."+

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
