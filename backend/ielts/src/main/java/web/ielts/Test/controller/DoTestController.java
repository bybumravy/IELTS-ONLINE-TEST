package web.ielts.Test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.ielts.Test.model.Listening;
import web.ielts.Test.model.Reading;
import web.ielts.Test.model.Writing;
import web.ielts.Test.model.answer.ReadingAnswer;
import web.ielts.Test.repository.ListeningRepository;
import web.ielts.Test.repository.ReadingRepository;
import web.ielts.Test.repository.WritingRepository;
import web.ielts.Test.repository.answer.ReadingAnswerRepository;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class DoTestController {
    @Autowired
    private WritingRepository writingRepository;
    @Autowired
    private ReadingAnswerRepository readingAnswerRepository;
    @Autowired
    private ListeningRepository listeningRepository;
    @Autowired
    private ReadingRepository readingRepository;
    @GetMapping("writing/{testId}")
    public ResponseEntity<Writing> getWritingByTestId(@PathVariable String testId) {
        Writing writing = writingRepository.findByTestId(testId);
        if (writing != null) {
            return ResponseEntity.ok(writing);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/tests")
    public ResponseEntity<List<Listening>> getAllTests() {
        return ResponseEntity.ok(listeningRepository.findAll());
    }


    @GetMapping("/listening/{testId}")
    public ResponseEntity<Listening> getListeningByTestId(@PathVariable String testId) {
        Listening listening = listeningRepository.findByTestId(testId);
        if (listening != null) {
            return ResponseEntity.ok(listening);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/reading/{testId}")
    public ResponseEntity<Reading> getTestBySkillAndId(@PathVariable String testId) {
        Reading reading =  readingRepository.findByTestId(testId);

        if (reading != null) {
            return ResponseEntity.ok(reading);
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/answer")
    public ReadingAnswer saveAnswer(@RequestBody ReadingAnswer answer) {
        return readingAnswerRepository.save(answer);
    }
}
