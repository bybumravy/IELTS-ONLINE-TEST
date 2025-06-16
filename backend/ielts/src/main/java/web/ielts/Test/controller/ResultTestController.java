package web.ielts.Test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.ielts.Test.model.answer.writing.WritingAnswer;
import web.ielts.Test.repository.answer.WritingAnswerRepository;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/result")
public class ResultTestController {
    @Autowired
    WritingAnswerRepository writingAnswerRepository;

    @GetMapping("/{id}")
    public ResponseEntity<WritingAnswer> getWritingById(@PathVariable String id) {
        return writingAnswerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


}
