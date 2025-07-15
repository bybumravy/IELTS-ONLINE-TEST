package web.ielts.Test.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web.ielts.Test.model.answer.speaking.EvaluationResult;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class AiSpeakingService {
    @Autowired WhisperService whisperService;
    @Autowired
    private AIService aiService;
    public EvaluationResult evaluateSpeaking(
            JsonNode transcriptText,
            String question,
            int partNumber,
            Map<String, Object> analyzeVoice,
            List<String> cueCard
    ) {
        // ✅ 1. Tạo prompt đúng cho từng part
        String prompt = aiService.buildSpeakingPrompt(
                partNumber,
                question,
                transcriptText,
                 analyzeVoice,
                cueCard

        );

        // ✅ 2. Gọi GPT
        String gptResponse = aiService.callSpeakingPart(prompt);

        System.out.println("=== GPT RESPONSE ===");
        System.out.println(gptResponse);

        // ✅ 3. Parse JSON
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(gptResponse, EvaluationResult.class);
        } catch (JsonProcessingException e) {
            System.err.println("❌ Lỗi khi parse GPT response thành EvaluationResult:");
            e.printStackTrace();
            return null;
        }
    }
}