package web.ielts.Test.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import web.ielts.Test.dto.ListTest;
import web.ielts.Test.model.AI.EvaluationResult;
import web.ielts.Test.model.Test;
import web.ielts.Test.repository.TestRepository;
import web.ielts.Test.service.AI.AISpeakingService;
import web.ielts.Test.service.AI.ProsodyService;
import web.ielts.Test.service.TestService;


@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class TestsController {

    @Autowired
    private TestService testService;
    @Autowired
    private TestRepository testRepo;

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

    @Autowired
    private  ProsodyService prosody;
    @Autowired
    private AISpeakingService aiSpeakingService;

    @GetMapping("/evaluate")
    public void evaluate() throws IOException {
        String jsonString = """
        {
          "task":"transcribe",
          "language":"english",
          "duration":8.460000038146973,
          "text":"I want you to see my result. This is my first time to speak English",
          "words":[
            {"word":"I","start":0.0,"end":1.4800000190734863},
            {"word":"want","start":1.4800000190734863,"end":1.9600000381469727},
            {"word":"you","start":1.9600000381469727,"end":2.3399999141693115},
            {"word":"to","start":2.3399999141693115,"end":2.819999933242798},
            {"word":"see","start":2.819999933242798,"end":3.180000066757202},
            {"word":"my","start":3.180000066757202,"end":3.5799999237060547},
            {"word":"result","start":3.5799999237060547,"end":4.139999866485596},
            {"word":"This","start":4.760000228881836,"end":5.039999961853027},
            {"word":"is","start":5.039999961853027,"end":5.21999979019165},
            {"word":"my","start":5.21999979019165,"end":5.539999961853027},
            {"word":"first","start":5.539999961853027,"end":5.78000020980835},
            {"word":"time","start":5.78000020980835,"end":6.199999809265137},
            {"word":"to","start":6.199999809265137,"end":6.699999809265137},
            {"word":"speak","start":6.699999809265137,"end":7.019999980926514},
            {"word":"English","start":7.019999980926514,"end":7.639999866485596}
          ],
          "usage":{"type":"duration","seconds":9}
        }
        """;

        JsonNode transcript = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            transcript = mapper.readTree(jsonString);

            // Example: print the text field
            System.out.println("Transcript Text: " + transcript.get("text").asText());

            // Example: print number of words
            System.out.println("Total words: " + transcript.get("words").size());

        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = "https://swpieltsbucket.s3.ap-southeast-1.amazonaws.com/audio/user/phamhoangviet05052005@gmail.com/T001_68610f3022439e341eda8a14/part1-1.mp3";
        Map<String, Object> prosodyFeatures = prosody.analyze(url, transcript);
        System.out.println(prosodyFeatures);
    }

}
