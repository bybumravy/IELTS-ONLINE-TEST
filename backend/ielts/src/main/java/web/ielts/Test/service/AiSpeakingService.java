package web.ielts.Test.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web.ielts.Test.model.answer.speaking.EvaluationResult;

import java.util.Map;

@Service
public class AiSpeakingService {
    @Autowired
    private AIService aiService;
    public EvaluationResult evaluateSpeakingPart1(String transcriptText, String question) {

        System.out.println("=== PROMPT TO GPT ===");


        String gptResponse = aiService.callSpeakingPart(aiService.buildSpeakingPart1Promot(question, transcriptText));

        System.out.println("=== GPT RESPONSE ===");
        System.out.println(gptResponse);
        EvaluationResult result = null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.readValue(gptResponse, EvaluationResult.class);
        } catch (JsonProcessingException e) {
            System.err.println("Lỗi khi parse GPT response thành EvaluationResult:");
            e.printStackTrace();
        }
        return result;
    }
}