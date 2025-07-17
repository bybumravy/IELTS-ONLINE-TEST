package web.ielts.Test.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web.ielts.Test.model.answer.speaking.FleCohAnswer;
import web.ielts.Test.model.answer.speaking.SpeakingAnswerQuestion;
import web.ielts.Test.service.AI.AIService;

import java.util.List;

@Service
public class AiSpeakingService {
    @Autowired WhisperService whisperService;
    @Autowired
    private AIService aiService;
    public void evaluateSpeaking(
            String Ob_id,
            JsonNode transcriptText,
            String question,
            int partNumber,
            FleCohAnswer analyzeVoice,
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

    }
}