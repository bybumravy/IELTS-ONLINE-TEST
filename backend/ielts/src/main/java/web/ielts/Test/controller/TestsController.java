package web.ielts.Test.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import io.jsonwebtoken.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import web.ielts.Test.dto.ListTest;
import web.ielts.Test.model.*;
import web.ielts.Test.repository.*;
import web.ielts.Test.repository.add.AddTestRepository;
import web.ielts.Test.service.TestService;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class TestsController {

    @Autowired
    private TestService testService;
    
    @Autowired
    private TestRepository testRepo;
    @Autowired
    private AddTestRepository addTestRepo;
    @Autowired
    private ListeningRepository listeningRepository;
    
    @Autowired
    private ReadingRepository readingRepository;
    
    @Autowired
    private WritingRepository writingRepository;
    
    @Autowired
    private SpeakingRepository speakingRepository;

    @GetMapping("/test/all-skill")
    public Map<Integer, List<ListTest>> getTestsGroupedByYear() {
        return testService.getTestsGroupedByYear();
    }

    @GetMapping("/test/listening")
    public Map<Integer, List<ListTest>> getListeningTestsGroupedByYear() {
        return testService.getListeningTestsByYear();
    }

    @GetMapping("/test/reading")
    public Map<Integer, List<ListTest>> getReadingTestsGroupedByYear() {
        return testService.getReadingTestsByYear();
    }

    @GetMapping("/test/writing")
    public Map<Integer, List<ListTest>> getWritingTestsGroupedByYear() {
        return testService.getWritingTestsByYear();
    }

    @GetMapping("/test/speaking")
    public Map<Integer, List<ListTest>> getSpeakingTestsGroupedByYear() {
        return testService.getSpeakingTestsByYear();
    }

    @GetMapping("/3-tests")
    public List<Test> getThreeTests() {
        return testRepo.findAll(PageRequest.of(0, 3)).getContent();
    }

    @GetMapping("/test/count")
    public int volumeOfTest() {
        return (int) addTestRepo.count();
    }
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }

    //test health
}
