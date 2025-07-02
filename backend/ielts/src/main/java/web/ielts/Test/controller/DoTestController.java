package web.ielts.Test.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import web.ielts.Test.dto.HistoryTest;
import web.ielts.Test.model.*;
import web.ielts.Test.model.answer.listening.ListeningAnswer;
import web.ielts.Test.model.answer.reading.ReadingAnswer;
import web.ielts.Test.model.answer.speaking.SpeakingAnswer;
import web.ielts.Test.model.answer.writing.WritingAnswer;
import web.ielts.Test.service.DoTestService;
import web.ielts.User.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    @GetMapping("/fulltest/{testId}")
    public ResponseEntity<Test> getFullTestByTestId(@PathVariable String testId) {
        Test test = doTestService.getTestByTestId(testId);
        return test != null ? ResponseEntity.ok(test) : ResponseEntity.notFound().build();
    }
    @PostMapping("/reading/submit")
    public ResponseEntity<ReadingAnswer> saveReadingAnswer(@RequestBody ReadingAnswer answer) {
        return ResponseEntity.ok(doTestService.saveReadingAnswer(answer));
    }

    @PostMapping("/writing/submit")
    public ResponseEntity<WritingAnswer> saveWritingAnswer(@RequestBody WritingAnswer answer) {
        System.out.println("sadfsadfdsfdsafjasdklfklasdjfkasjdflaskjdflkasdjflkasjdfasfasfasfasdfasdfasdfa");
        System.out.println(answer.toString());
        return ResponseEntity.ok(doTestService.saveWritingAnswer(answer));
    }

    @PostMapping("/listening/submit")
    public ResponseEntity<ListeningAnswer> saveListeningAnswer(@RequestBody ListeningAnswer answer) {
        System.out.println("hi");
        return ResponseEntity.ok(doTestService.saveListeningAnswer(answer));
    }
    @PostMapping("/speaking/submit")
    public ResponseEntity<String> uploadFiles(
            @RequestPart("metadata") MultipartFile metadataJson,
            @RequestPart(value = "files", required = false) MultipartFile[] files,
            @AuthenticationPrincipal User user
    ) {
        String studentUsername = user.getUsername();
        System.out.println("User: " + studentUsername);

        String testId;
        SpeakingAnswer submission;
        SpeakingAnswer saved;

        try {
            String jsonString = new String(metadataJson.getBytes(), StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(jsonString);

            testId = root.get("testId").asText();

            submission = mapper.readValue(jsonString, SpeakingAnswer.class);
            submission.setUsername(studentUsername);
            submission.setId(null); // Lưu lần đầu để sinh _id

            saved = doTestService.saveSubmission(submission);
            System.out.println(saved);
            System.out.println("Saved initial submission with _id: " + saved.getId());
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Lỗi khi đọc hoặc lưu metadata JSON: " + e.getMessage());
        }

        String folderPath = "audio/user/" + studentUsername + "/" + testId + "_" + saved.getId();
        Map<String, String> fileUrlMap = new HashMap<>();

        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                try {
                    String key = folderPath + "/" + file.getOriginalFilename();
                    String url = doTestService.uploadFile(file, key);
                    fileUrlMap.put(file.getOriginalFilename(), url);
                    System.out.println("Uploaded: " + url);
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Upload failed: " + e.getMessage());
                }
            }
        } else {
            System.out.println("No files uploaded, only saving metadata.");
        }

        doTestService.updateAnswerUrls(saved, fileUrlMap);
        doTestService.saveSubmission(saved);

        return ResponseEntity.ok("✅ Upload và cập nhật thành công!");
    }

    @GetMapping("/history/listening/{username}")
    public List<HistoryTest> getListeningAnswerByTestId(@PathVariable String username) {
        return doTestService.getListeningByUsername(username);
    }
    @GetMapping("/history/reading/{username}")
    public List<HistoryTest> getReadingAnswerByTestId(@PathVariable String username) {
        return doTestService.getReadingByUsername(username);
    }
    @GetMapping("/history/writing/{username}")
    public List<HistoryTest> getWritingAnswerByTestId(@PathVariable String username) {
        return doTestService.getWritingByUsername(username);
    }
    @GetMapping("/history/speaking/{username}")
    public List<HistoryTest> getSpeakingAnswerByTestId(@PathVariable String username) {
        return doTestService.getSpeakingByUsername(username);
    }

}
