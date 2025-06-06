package web.ielts.Test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.ielts.Test.model.Writing;
import web.ielts.Test.repository.WritingRepository;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class DoTestController {
    @Autowired
    private WritingRepository writingRepository;
    @GetMapping("writing/{testId}")
    public ResponseEntity<Writing> getWritingByTestId(@PathVariable String testId) {
        Writing writing = writingRepository.findByTestId(testId);
        if (writing != null) {
            return ResponseEntity.ok(writing);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
