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

        question = " What sort of things can neighbors do to help each other";
        transcriptText = "There are a plethora of things that neighbors can support each other, varying from little things such as looking after one's property if they go on holiday or taking delivery if something arrives by post when they're not at home, to bigger deals such as extending helping hands in times of difficulties or distress. I remember 15 years ago when my grandfather was severely ill, had it not been for prompt assistance from our neighbors, my family could have been in great turmoil.";

        System.out.println("=== PROMPT TO GPT ===");


        String gptResponse = aiService.callSpeakingPart(aiService.buildSpeakingPart3Prompt(question,transcriptText));

        System.out.println("=== GPT RESPONSE ===");
        System.out.println(gptResponse);

        return null;
    }
//        String prompt = """
//You are an official IELTS Speaking examiner. You are evaluating a real IELTS Part 2 speaking response
//"1. EVALUATION (Official IELTS Criteria + Public Descriptors):\\n"
//
//You MUST strictly evaluate only these two criteria:
//
//1. Lexical Resource (Vocabulary)
//2. Grammatical Range and Accuracy
//
//---
//
//# IELTS Part 2 Question:
//%s
//
//# Candidate Transcript:
//%s
//
//---
//
//# Vocabulary Band Descriptors:
//%s
//
//# Grammar Band Descriptors:
//%s
//
//---
//
//# Vocabulary Deduction Rules (MANDATORY):
//- Basic or vague words (e.g., "thing", "nice"): -0.25
//- Word repetition (3+ times): -0.25
//- Missing topic-specific words (e.g., tourist sites, culture terms): -0.25 to -0.5
//- Slightly awkward word choice or register: -0.25
//- Incorrect or illogical word usage: -0.5
//- Forced/unnatural idioms or collocations (e.g., “too much to take”): -0.5
//- No idioms or descriptive phrases where expected: -0.25
//- Too informal in formal context (e.g., “super sticky”): -0.25
//- Wrong collocation (e.g., “have a lot of considerations”): -0.25
//- Misuse of verbs with abstract nouns (e.g., “maintained treaties”): -0.5
//
//# Grammar Deduction Rules (MANDATORY):
//- Subject-verb agreement errors: -0.25
//- Tense inconsistency: -0.25 to -0.5
//- Incorrect article, word order, or preposition: -0.25 to -0.5
//- Redundant or unclear sentence structure: -0.25
//- Complex sentence misuse: -0.5
//- Grammar obscures logic or natural meaning: -1.0
//- Overuse of passive voice with unclear agent: -0.25
//
//---
//
//# SCORING RULES — STRICT ENFORCEMENT:
//
//-  DO NOT skip deduction rules even for minor errors.
//-  You MUST deduct all points strictly based on above rules.
//-  Apply deduction **even for subtle or stylistic issues**.
//- ⚠ IF 3 or more minor errors in ANY category (vocab or grammar) → MAX Band 7.5
//- ⚠ IF total number of errors (vocab + grammar) ≥ 5 → MAX Band 6.5
//- ⚠ IF total deduction ≥ 1.0 → MAX Band 6.5
//- ⚠ IF any error affects logic or meaning → Deduct ≥ 0.5 AND MAX Band 6.5
//
//
//# Final Output: Respond ONLY in this exact JSON format:
//
//{
//  "vocabulary": <score>,
//  "grammar": <score>,
//  "overallBand": <average>,
//  "feedback": "<one-paragraph summary>",
//  "vocabularyErrors": [
//    { "error": "...", "reason": "...", "deduction": -0.25 }
//  ],
//  "grammarErrors": [
//    { "error": "...", "reason": "...", "deduction": -0.25 }
//  ],
//  "transcript": "<verbatim transcript here>"
//}
//""".formatted(
//                question,
//                transcriptText,
//                vocabularyDescriptors,
//                grammarDescriptors
//        );




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