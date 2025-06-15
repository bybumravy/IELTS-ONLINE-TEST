package web.ielts.Test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.ielts.Test.model.Listening;
import web.ielts.Test.model.Reading;
import web.ielts.Test.model.Speaking;
import web.ielts.Test.model.Writing;
import web.ielts.Test.model.answer.listening.ListeningAnswer;
import web.ielts.Test.model.answer.reading.ReadingAnswer;
import web.ielts.Test.model.answer.writing.WritingAnswer;
import web.ielts.Test.service.DoTestService;
import java.util.List;


@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/verify")
public class DoTestController {

    @Autowired
    private DoTestService doTestService;

    @GetMapping("writing/{testId}")
    public ResponseEntity<Writing> getWritingByTestId(@PathVariable String testId) {
        return doTestService.getWritingByTestId(testId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

//    @GetMapping("/tests")
//    public ResponseEntity<List<Listening>> getAllTests() {
//        return ResponseEntity.ok(doTestService.getAllListeningTests());
//    }

    @GetMapping("/listening/{testId}")
    public ResponseEntity<Listening> getListeningByTestId(@PathVariable String testId) {
        Listening listening = doTestService.getListeningByTestId(testId);
        return listening != null ? ResponseEntity.ok(listening) : ResponseEntity.notFound().build();
    }

    @GetMapping("/reading/{testId}")
    public ResponseEntity<Reading> getReadingByTestId(@PathVariable String testId) {
        Reading reading = doTestService.getReadingByTestId(testId);
        return reading != null ? ResponseEntity.ok(reading) : ResponseEntity.notFound().build();
    }
    @GetMapping("speaking/{testId}")
    public ResponseEntity<Speaking> getSpeakingByTestId(@PathVariable String testId) {
        Speaking speaking = doTestService.getSpeakingByTestId(testId);
        if (speaking != null) {
            return ResponseEntity.ok(speaking);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/reading/submit")
    public ResponseEntity<ReadingAnswer> saveReadingAnswer(@RequestBody ReadingAnswer answer) {
        return ResponseEntity.ok(doTestService.saveReadingAnswer(answer));
    }

    @PostMapping("/writing/submit")
    public ResponseEntity<WritingAnswer> saveWritingAnswer(@RequestBody WritingAnswer answer) {
        return ResponseEntity.ok(doTestService.saveWritingAnswer(answer));
    }

    @PostMapping("/listening/submit")
    public ResponseEntity<ListeningAnswer> saveListeningAnswer(@RequestBody ListeningAnswer answer) {
        System.out.println("hi");
        return ResponseEntity.ok(doTestService.saveListeningAnswer(answer));
    }


}
