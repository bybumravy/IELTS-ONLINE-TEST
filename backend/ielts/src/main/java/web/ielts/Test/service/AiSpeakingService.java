package web.ielts.Test.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web.ielts.Test.model.EvaluationResult;

import java.util.Map;

@Service
public class AiSpeakingService {
    @Autowired
    private AIService aiService;
    public EvaluationResult evaluateSpeaking(String transcriptText, Map<String, Object> prosodyFeatures, String question) {
        String emphasizedWords = prosodyFeatures.get("emphasizedWords").toString();
        double avgPitch = (double) prosodyFeatures.get("avgPitch");
        double intonationRange = (double) prosodyFeatures.get("intonationRange");
        double speechRate = (double) prosodyFeatures.get("speechRate");
        int pauseCount = (int) prosodyFeatures.get("pauseCount");

        question = "Describe a program on computer/a mobile app on the phone that you use. You should say:\n" +
                "What the program/app is;\n" +
                "When/where you found it;\n" +
                "How you use it;\n" +
                "And explain how you feel about it.";

        transcriptText = "In the age of tech obsession like today, a smartphone teeming with a variety of useful apps is so crucial that those without it are immediately considered behind the time. I also have one for my own and the app that I find among the most beneficial for me is Google Maps - an app that assists in navigation.\n" +
                "\n" +
                "Google Maps is the archetype of web mapping service so nearly every-one have a smartphone has to have it. I myself am no exception as when my parents bought me one during secondary school years, I downloaded Google Maps right away.\n" +
                "\n" +
                "Using Google Maps is not that hard because simply put, it's just an online map but the way you make use of that app is what matters. This app comes in handy to me mostly in self-navigation and route plan-ning, especially when I am in an unfamiliar place like when I was a university freshman coming to Hanoi or traveling to a faraway destina-tion.\n" +
                "\n" +
                "Google Maps has helped me a great deal in familiarizing myself with the road system of a place and moulding me as \"a living Google Maps\", as my friends often call me. Before, I was just a freshman who was still wet behind the ears. But now that I use that app so often, I have come to know Hanoi like the back of my hand now, which really impresses my friends, even some of Hanoi-born ones. When it comes to traveling - my ultimate hobby, Google Maps is my best buddy in navigation, especially in some places with extremely compli-cated road systems like Dalat or Saigon.\n" +
                "\n" +
                "So Google Maps seems to be part and parcel to me whenever on road. But just one small word to remember: Don't trust Google Maps every time because sometimes, it may lead you to some surprises, mostly not so pleasant."; // giữ nguyên đoạn transcript như bạn đang dùng

        String vocabularyDescriptors = """ 
    Band 9: Total flexibility and precise use in all contexts. Sustained use of accurate and idiomatic language.
    Band 8: Wide resource, readily and flexibly used to discuss all topics and convey precise meaning...
    Band 7: Resource flexibly used to discuss a variety of topics. Some ability to use less common and idiomatic items...
    Band 6: Resource sufficient to discuss topics at length. Vocabulary use may be inappropriate but meaning is clear...
    Band 5: Resource sufficient to discuss familiar and unfamiliar topics but there is limited flexibility...
    Band 4: Resource sufficient for familiar topics but only basic meaning can be conveyed...
    Band 3: Resource limited to simple vocabulary used primarily to convey personal information...
    Band 2: Very limited resource. Utterances consist of isolated words...
    Band 1: No resource bar a few isolated words. No communication possible.
    """;

        String grammarDescriptors = """
    Band 9: Structures are precise and accurate at all times...
    Band 8: Wide range of structures, flexibly used. The majority of sentences are error free...
    Band 7: A range of structures flexibly used. Error-free sentences are frequent...
    Band 6: Produces a mix of short and complex sentence forms and a variety of structures with limited flexibility...
    Band 5: Basic sentence forms are fairly well controlled. Complex structures are attempted but with errors...
    Band 4: Can produce basic sentence forms and some short utterances are error-free. Subordinate clauses are rare...
    Band 3: Basic sentence forms are attempted but grammatical errors are numerous...
    Band 2: No evidence of basic sentence forms.
    Band 1: No rateable language unless memorised.
    """;

        String prompt = """
You are an official IELTS Speaking examiner. Please evaluate the candidate's response to a Part 2 question.

Evaluate only two criteria:

1. Lexical Resource (Vocabulary)
2. Grammatical Range and Accuracy

---

Question:
%s

Transcript:
%s

---

Vocabulary Band Descriptors:
%s

Grammar Band Descriptors:
%s

---

Apply these strict deduction rules:

### Vocabulary Deductions:
- Basic or vague words (e.g., "thing", "nice"): -0.25
- Word repetition (3+ times): -0.25
- Missing topic-specific words: -0.25 to -0.5
- Slightly awkward word choice: -0.25
- Incorrect word usage that causes confusion: -0.5
- Forced/unnatural idioms: -0.5
- No idioms where expected: -0.25


### Grammar Deductions:
- Subject-verb agreement errors: -0.25
- Tense inconsistency: -0.25 to -0.5
- Incorrect article or word order: -0.25 to -0.5
- No complex sentence forms: -0.25 to -0.5
- Grammar obscures meaning: -1.0
- Sentence fragments: -0.5

---

⚠️ STRICT RULES FOR SCORING ⚠️

- You MUST apply all deduction rules strictly and consistently.
- DO NOT be lenient or generous. This is a real IELTS test.
- DO NOT give band 8+ unless the transcript is nearly error-free.
- If more than 3 minor errors are found, Band 7+ is not allowed.
- If any error affects clarity or meaning, deduct significantly.
- A candidate with common grammar mistakes should not receive more than Band 6.
- Apply Vocabulary Band Descriptors and Grammar Band Descriptors to score more correctly
---

You MUST:
- List at least 3 vocabulary errors and 3 grammar errors if present.
- For each error:
  - Quote the phrase
  - Explain the issue
  - State deduction amount

Show calculation:
e.g. Vocabulary: 8.0 - 0.25 (repetition) = 7.75

Respond only in this JSON format:

{
  "vocabulary": <score>,
  "grammar": <score>,
  "overallBand": <average>,
  "feedback": "<summary>",
  "vocabularyErrors": [
    { "error": "...", "reason": "...", "deduction": -0.25 },
    ...
  ],
  "grammarErrors": [
    { "error": "...", "reason": "...", "deduction": -0.25 },
    ...
  ],
  "transcript": "<full transcript>"
}
""".formatted(
                question,
                transcriptText,
                vocabularyDescriptors,
                grammarDescriptors
        );

        System.out.println("=== PROMPT TO GPT ===");
        System.out.println(prompt);

        String gptResponse = aiService.call(prompt);

        System.out.println("=== GPT RESPONSE ===");
        System.out.println(gptResponse);

        return parseGptResult(gptResponse, transcriptText); // implement parsing JSON to EvaluationResult
    }
    private EvaluationResult parseGptResult(String json, String transcript) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            EvaluationResult result = mapper.readValue(json, EvaluationResult.class);
            result.setTranscript(transcript);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error parsing GPT response", e);
        }
    }
}
