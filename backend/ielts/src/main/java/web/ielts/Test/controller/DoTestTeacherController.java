package web.ielts.Test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.ielts.Test.model.Writing;
import web.ielts.Test.model.answer.writing.WritingAnswer;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/verify")
public class DoTestTeacherController {
    @Autowired
    private DoTestTeacherService doTestTeacherService;
    @GetMapping("writingbyteacher/{testId}")
    public ResponseEntity<WritingAnswer> getWritingAnswerByTestId(@PathVariable String testId) {
        return doTestTeacherService.getWritingAnswerByTestId(testId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
