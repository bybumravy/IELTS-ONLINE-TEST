package web.ielts.Test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.ielts.Test.model.answer.listening.ListeningAnswer;
import web.ielts.Test.model.answer.writing.WritingAnswer;
import web.ielts.Test.repository.answer.WritingAnswerRepository;
import web.ielts.Test.service.ResultService;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/result")
public class ResultTestController {
    @Autowired
    WritingAnswerRepository writingAnswerRepository;
    @Autowired
    private ResultService resultService;
    @GetMapping("/{id}")
    public ResponseEntity<WritingAnswer> getWritingById(@PathVariable String id) {
        return writingAnswerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/listening")
    public ResponseEntity<?> submitListeningAnswer(@RequestBody ListeningAnswer answer) {
        ListeningAnswer saved = resultService.saveAnswer(answer);
        return ResponseEntity.ok(Map.of(
                "message", "Saved successfully",
                "answerId", saved.getId()
        ));
    }

    @GetMapping("/listening/by-id")
    public ResponseEntity<?> getListeningAnswerById(@RequestParam String answerId) {
        return resultService.findById(answerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


}
