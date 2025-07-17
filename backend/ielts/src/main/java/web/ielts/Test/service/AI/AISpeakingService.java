package web.ielts.Test.service.AI;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web.ielts.Test.model.AI.EvaluationResult;
import web.ielts.Test.service.AIService;

import java.util.Map;

@Service
public class AISpeakingService {
    @Autowired
    private AIService aiService;
    public EvaluationResult evaluateSpeaking(String transcriptText, Map<String, Object> prosodyFeatures, String question) {
        String emphasizedWords = prosodyFeatures.get("emphasizedWords").toString();
        double avgPitch = (double) prosodyFeatures.get("avgPitch");
        double intonationRange = (double) prosodyFeatures.get("intonationRange");
        double speechRate = (double) prosodyFeatures.get("speechRate");
        int pauseCount = (int) prosodyFeatures.get("pauseCount");

        question = " Talk about your hometown";

        transcriptText = "Well, Hanoi, the capital of Vietnam, is my hometown. The city, located in the north of Vietnam, is the heart of the politics and culture of the whole country. To me, what is so unique about Hanoi is that it has four distinct seasons. As Vietnam has a tropical climate, summer in Hanoi is super sticky, but there is chilly fall, which is also my favorite season. \n" +
                "\n" +
                "At this time, I often get around at night by my motorbike to sense the aromatic scent of milk flowers which sprawls all over the atmosphere thanks to the breeze. However, not everyone loves it, some might think the fragrance is too much to take. I would say that Hanoi is one of the well-known tourist attractions in Vietnam, given that there is an array of historical sites.\n" +
                "\n" +
                "Visitors should tour around places like Hoa Lo prison, which maintained treaties and prisoner figures, or visit Huu Tiep lake to see the remnant of the B-52 Stratofortress and then drop by Ngoc Ha flower village. One more activity that tourists should try is crossing the crossroad without traffic lights. I know it’s a little bit eccentric to do this, but chaotic traffic has been a culture of Vietnam.\n" +
                "\n" +
                "Nonetheless, I do recommend that travelers do this under the guidance of a native since it’s quite dangerous. If I am asked whether Hanoi is a decent place to live, I will have a lot of considerations. Sure the living expenses are exorbitant, but the services from health care to entertainment here are fast and convenient, not to mention the labour market is huge. So, in my opinion, although residing in Hanoi is challenging, it’s still worth staying in this city.";

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
You are an official IELTS Speaking examiner. You are evaluating a real IELTS Part 2 speaking response.

You MUST strictly evaluate only these two criteria:

1. Lexical Resource (Vocabulary)
2. Grammatical Range and Accuracy

---

# IELTS Part 2 Question:
%s

# Candidate Transcript:
%s

---

# Vocabulary Band Descriptors:
%s

# Grammar Band Descriptors:
%s

---

# Vocabulary Deduction Rules (MANDATORY):
- Basic or vague words (e.g., "thing", "nice"): -0.25
- Word repetition (3+ times): -0.25
- Missing topic-specific words (e.g., tourist sites, culture terms): -0.25 to -0.5
- Slightly awkward word choice or register: -0.25
- Incorrect or illogical word usage: -0.5
- Forced/unnatural idioms or collocations (e.g., “too much to take”): -0.5
- No idioms or descriptive phrases where expected: -0.25
- Too informal in formal context (e.g., “super sticky”): -0.25
- Wrong collocation (e.g., “have a lot of considerations”): -0.25
- Misuse of verbs with abstract nouns (e.g., “maintained treaties”): -0.5

# Grammar Deduction Rules (MANDATORY):
- Subject-verb agreement errors: -0.25
- Tense inconsistency: -0.25 to -0.5
- Incorrect article, word order, or preposition: -0.25 to -0.5
- Redundant or unclear sentence structure: -0.25
- Complex sentence misuse: -0.5
- Grammar obscures logic or natural meaning: -1.0
- Overuse of passive voice with unclear agent: -0.25

---

# SCORING RULES — STRICT ENFORCEMENT:

-  DO NOT skip deduction rules even for minor errors.
-  You MUST deduct all points strictly based on above rules.
-  Apply deduction **even for subtle or stylistic issues**.
- ⚠ IF 3 or more minor errors in ANY category (vocab or grammar) → MAX Band 7.5
- ⚠ IF total number of errors (vocab + grammar) ≥ 5 → MAX Band 6.5
- ⚠ IF total deduction ≥ 1.0 → MAX Band 6.5
- ⚠ IF any error affects logic or meaning → Deduct ≥ 0.5 AND MAX Band 6.5


# Final Output: Respond ONLY in this exact JSON format:

{
  "vocabulary": <score>,
  "grammar": <score>,
  "overallBand": <average>,
  "feedback": "<one-paragraph summary>",
  "vocabularyErrors": [
    { "error": "...", "reason": "...", "deduction": -0.25 }
  ],
  "grammarErrors": [
    { "error": "...", "reason": "...", "deduction": -0.25 }
  ],
  "transcript": "<verbatim transcript here>"
}
""".formatted(
                question,
                transcriptText,
                vocabularyDescriptors,
                grammarDescriptors
        );


        System.out.println("=== PROMPT TO GPT ===");


        String gptResponse = aiService.callSpeakingPart(prompt);

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
