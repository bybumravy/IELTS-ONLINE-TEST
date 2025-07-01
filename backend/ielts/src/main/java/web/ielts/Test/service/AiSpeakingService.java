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



//        String vocabularyDescriptors = """
//    Band 9: Total flexibility and precise use in all contexts. Sustained use of accurate and idiomatic language.
//    Band 8: Wide resource, readily and flexibly used to discuss all topics and convey precise meaning...
//    Band 7: Resource flexibly used to discuss a variety of topics. Some ability to use less common and idiomatic items...
//    Band 6: Resource sufficient to discuss topics at length. Vocabulary use may be inappropriate but meaning is clear...
//    Band 5: Resource sufficient to discuss familiar and unfamiliar topics but there is limited flexibility...
//    Band 4: Resource sufficient for familiar topics but only basic meaning can be conveyed...
//    Band 3: Resource limited to simple vocabulary used primarily to convey personal information...
//    Band 2: Very limited resource. Utterances consist of isolated words...
//    Band 1: No resource bar a few isolated words. No communication possible.
//    """;
//
//        String grammarDescriptors = """
//    Band 9: Structures are precise and accurate at all times...
//    Band 8: Wide range of structures, flexibly used. The majority of sentences are error free...
//    Band 7: A range of structures flexibly used. Error-free sentences are frequent...
//    Band 6: Produces a mix of short and complex sentence forms and a variety of structures with limited flexibility...
//    Band 5: Basic sentence forms are fairly well controlled. Complex structures are attempted but with errors...
//    Band 4: Can produce basic sentence forms and some short utterances are error-free. Subordinate clauses are rare...
//    Band 3: Basic sentence forms are attempted but grammatical errors are numerous...
//    Band 2: No evidence of basic sentence forms.
//    Band 1: No rateable language unless memorised.
//    """;

        String prompt = """
You are an official IELTS Speaking examiner. Please evaluate the candidate's response to a Part 2 question.

Evaluate all **4 criteria** strictly using IELTS Band Descriptors and audio prosody features:

1. Fluency and Coherence  
2. Lexical Resource  
3. Grammatical Range and Accuracy  
4. Pronunciation

---

📌 Question:  
%s

📄 Transcript:  
%s

🎧 Prosody features:  
- Average Pitch: %.2f Hz  
- Intonation Range: %.2f Hz  
- Speech Rate: %.2f words/second  
- Pause Count: %d  
- Emphasized Words: %s

---

## FLUENCY & COHERENCE Band Descriptors:
Band 9: Fluent with only very occasional repetition or self-correction...  
Band 8: Fluent with only very occasional repetition or hesitation...  
Band 7: Able to keep going and readily produce long turns...  
Band 6: Able to keep going but some coherence lost due to hesitation...  
Band 5: Relies on repetition/self-correction, overuses discourse markers...  
Band 4: Frequent repetition and self-correction, coherence breakdowns...  
Band 3: Long pauses, limited ability to go beyond simple responses...  
Band 2: Pauses before nearly every word, speech has little meaning...  
Band 1: Speech is totally incoherent...

## PRONUNCIATION Band Descriptors:
Band 9: Uses full range of phonological features, accent has no effect...  
Band 8: Wide range of features, flexible intonation, accent minimal...  
Band 7: Some features of band 8, mostly clear with rare issues...  
Band 6: Variable control of phonological features, some mispronunciations...  
Band 5: Frequent mispronunciations, control of rhythm limited...  
Band 4: Limited control of intonation and stress, understanding requires effort...  
Band 3: Frequent mispronunciation, some unintelligible parts...  
Band 2: Little intelligibility, mostly unintelligible...  
Band 1: Unintelligible...

---

## STRICT DEDUCTION RULES

### Vocabulary Deductions:
- Basic/vague words (e.g., "thing", "nice"): -0.25  
- Word repetition (3+): -0.25  
- Missing topic-specific words: -0.25 to -0.5  
- Slightly awkward choice: -0.25  
- Incorrect word usage: -0.5  
- Forced idioms: -0.5  
- No idioms where expected: -0.25  

### Grammar Deductions:
- Subject-verb agreement errors: -0.25  
- Tense inconsistency: -0.25 to -0.5  
- Article/word order errors: -0.25 to -0.5  
- No complex sentence forms: -0.25 to -0.5  
- Grammar obscures meaning: -1.0  
- Sentence fragments: -0.5  

### Fluency Deductions:
- > 10 pauses: -0.5  
- Frequent self-correction or repetition: -0.25 to -0.5  
- Speech rate < 2.5 wps: -0.5  
- Hesitations at content words: -0.25  

### Pronunciation Deductions:
- > 2 mispronounced key words: -0.5  
- Intonation range < 50 Hz: -0.25  
- Wrong word stress: -0.25  
- Unclear speech due to poor chunking: -0.5  

---

⚠️ RULES:  
- Apply all deduction rules strictly.  
- No Band 8+ if > 3 minor errors or any major one.  
- A candidate with noticeable hesitation or mispronunciation should not receive Band 7+.  
- Use both the descriptors AND the prosodic data.  

---

📤 RETURN RESPONSE IN JSON FORMAT ONLY:

{
  "fluency": <score>,
  "vocabulary": <score>,
  "grammar": <score>,
  "pronunciation": <score>,
  "overallBand": <average>,
  "feedback": "<summary>",
  "fluencyErrors": [
    { "issue": "...", "deduction": -0.25 }
  ],
  "vocabularyErrors": [
    { "error": "...", "reason": "...", "deduction": -0.25 }
  ],
  "grammarErrors": [
    { "error": "...", "reason": "...", "deduction": -0.25 }
  ],
  "pronunciationErrors": [
    { "issue": "...", "deduction": -0.25 }
  ],
  "transcript": "<full transcript>"
}
""".formatted(
                question,
                transcriptText,
                avgPitch,
                intonationRange,
                speechRate,
                pauseCount,
                emphasizedWords
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
