package web.ielts.Test.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import web.ielts.Test.model.EvaluationResult;
import web.ielts.Test.service.AiSpeakingService;
import web.ielts.Test.service.ProsodyService;
import web.ielts.Test.service.WhisperService;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/speaking")
public class SpeakingEvaluationController {
    @Autowired
    private  WhisperService whisper;
    @Autowired
    private  ProsodyService prosody;
    @Autowired
    private AiSpeakingService aiSpeakingService;

    @PostMapping("/evaluate")
    public ResponseEntity<EvaluationResult> evaluate(@RequestBody Map<String, String> req) throws IOException {
        String url = req.get("audioUrl");
        //System.out.println(url);
        String question = req.get("question");
        String trascriptText =whisper.transcribe(url);

        JsonNode transcript = whisper.transcribeWithTimestamps(url);

        System.out.println(transcript);

       Map<String, Object> prosodyFeatures = prosody.analyze(url,transcript);

        EvaluationResult result = aiSpeakingService.evaluateSpeaking(trascriptText, prosodyFeatures,question);
        return ResponseEntity.ok(result);
    }
}