package web.ielts.Test.controller;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.ielts.Test.model.Writing;
import web.ielts.Test.model.answer.writing.WritingAnswer;

import java.util.List;

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

    @PostMapping("/writingteachersubmit")
    public ResponseEntity<WritingAnswer> saveWritingAnswer(@RequestBody WritingAnswer answer) {

        return ResponseEntity.ok(doTestTeacherService.saveWritingAnswer(answer));
        //return ResponseEntity.ok(doTestTeacherService.saveWritingAnswer(answer));
    }
    @GetMapping("/listwriting")
    public List<WritingAnswer> getTeacherGradedAnswers() {
        return doTestTeacherService.getTeacherGradedAnswers();
    }

}
