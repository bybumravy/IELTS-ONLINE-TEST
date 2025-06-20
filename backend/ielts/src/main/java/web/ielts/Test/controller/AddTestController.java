package web.ielts.Test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.ielts.Test.model.*;
import web.ielts.Test.model.add.*;
import web.ielts.Test.service.AddTestService;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class AddTestController {

    @Autowired
    private AddTestService testService;

    @PostMapping("/teacher/request-test")
    public ResponseEntity<String> saveTest(@RequestBody AddTestRequest request) {
        try {
            testService.saveFullTest(request);
            return ResponseEntity.ok("Test saved successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to save test");
        }
    }

//    @PostMapping("/manager/accept-test/{testId}")
//public ResponseEntity<String> acceptTest(@PathVariable String testId) {
//    AddTest addTest = addTestRepo.findById(testId).orElse(null);
//    AddListening addListening = addListeningRepo.findByTestId(testId);
//    AddReading addReading = addReadingRepo.findByTestId(testId);
//    AddWriting addWriting = addWritingRepo.findByTestId(testId);
//    AddSpeaking addSpeaking = addSpeakingRepo.findByTestId(testId);
//
//    if (addTest == null) return ResponseEntity.badRequest().body("Test not found");
//
//    // Chuyển sang model chính
//    Test test = new Test();
//    test.setTestId(addTest.getTestId());
//    test.setTestTitle(addTest.getTestTitle());
//    test.setTags(addTest.getTags());
//    test.setCreatedAt(addTest.getCreateAt().toString());
//    testRepo.save(test);
//
//    Listening listening = convertAddListeningToListening(addListening);
//    if (listening != null) listeningRepo.save(listening);
//
//    Reading reading = convertAddReadingToReading(addReading);
//    if (reading != null) readingRepo.save(reading);
//
//    Writing writing = convertAddWritingToWriting(addWriting);
//    if (writing != null) writingRepo.save(writing);
//
//    Speaking speaking = convertAddSpeakingToSpeaking(addSpeaking);
//    if (speaking != null) speakingRepo.save(speaking);
//
//    // Xóa bản ghi request
//    addTestRepo.deleteById(testId);
//    if (addListening != null) addListeningRepo.delete(addListening);
//    if (addReading != null) addReadingRepo.delete(addReading);
//    if (addWriting != null) addWritingRepo.delete(addWriting);
//    if (addSpeaking != null) addSpeakingRepo.delete(addSpeaking);
//
//    return ResponseEntity.ok("Accepted and moved to main database!");
//}
}
