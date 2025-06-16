package web.ielts.Test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.ielts.Test.model.add.AddTestRequest;
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
}
