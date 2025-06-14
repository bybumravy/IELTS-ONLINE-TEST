package web.ielts.Test.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import web.ielts.Test.repository.*;
import web.ielts.Test.dto.ListTest;
import web.ielts.Test.model.Test;
import web.ielts.Test.model.Listening;
import web.ielts.Test.model.Reading;
import web.ielts.Test.model.Writing;
import web.ielts.Test.model.Speaking;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TestService {
    @Autowired
    private TestRepository testRepository;

    @Autowired
    private ListeningRepository listeningRepository;

    @Autowired
    private ReadingRepository readingRepository;

    @Autowired
    private WritingRepository writingRepository;

    @Autowired
    private SpeakingRepository speakingRepository;

    public Map<Integer, List<ListTest>> getTestsGroupedByYear() {
        return testRepository.findAll().stream()
                .filter(t -> t.getCreatedAt() != null && !t.getCreatedAt().isEmpty())
                .map(t -> toListTest(t))
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(ListTest::getYear));
    }

    public Map<Integer, List<ListTest>> getListeningTestsByYear() {
        return getTestsBySkill(listeningRepository.findAll());
    }

    public Map<Integer, List<ListTest>> getReadingTestsByYear() {
        return getTestsBySkill(readingRepository.findAll());
    }

    public Map<Integer, List<ListTest>> getWritingTestsByYear() {
        return getTestsBySkill(writingRepository.findAll());
    }

    public Map<Integer, List<ListTest>> getSpeakingTestsByYear() {
        return getTestsBySkill(speakingRepository.findAll());
    }

    private <T> Map<Integer, List<ListTest>> getTestsBySkill(List<T> skills) {
        Map<String, Test> testMap = testRepository.findAll().stream()
                .collect(Collectors.toMap(Test::getId, t -> t));

        return skills.stream()
                .map(skill -> {
                    String testId = null;
                    if (skill instanceof Listening) testId = ((Listening) skill).getTestId();
                    else if (skill instanceof Reading) testId = ((Reading) skill).getTestId();
                    else if (skill instanceof Writing) testId = ((Writing) skill).getTestId();
                    else if (skill instanceof Speaking) testId = ((Speaking) skill).getTestId();

                    Test test = testMap.get(testId);
                    return toListTest(test);
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.groupingBy(ListTest::getYear));
    }

    private ListTest toListTest(Test test) {
        if (test == null || test.getCreatedAt() == null || test.getCreatedAt().isEmpty()) return null;
        try {
            int year = LocalDate.parse(test.getCreatedAt()).getYear();
            return new ListTest(test.getId(), test.getTestTitle(), year);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    public void processAndSaveJson(MultipartFile file) throws IOException {
        String json = new String(file.getBytes(), StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        // Create Test object
        Test test = new Test();
        test.setId(root.get("testId").asText());
        test.setTestTitle(root.get("title").asText());
        test.setTags(mapper.convertValue(root.get("tags"), new TypeReference<List<String>>() {}));
        test.setCreatedAt(root.get("createdAt").asText());

        // Save test first to get the ID
        testRepository.save(test);

        // Process and save each skill
        if (root.has("listening")) {
            Listening listening = mapper.convertValue(root.get("listening"), Listening.class);
            listening.setTestId(test.getId());
            listeningRepository.save(listening);
        }

        if (root.has("reading")) {
            Reading reading = mapper.convertValue(root.get("reading"), Reading.class);
            reading.setTestId(test.getId());
            readingRepository.save(reading);
        }

        if (root.has("writing")) {
            Writing writing = mapper.convertValue(root.get("writing"), Writing.class);
            writing.setTestId(test.getId());
            writingRepository.save(writing);
        }

        if (root.has("speaking")) {
            Speaking speaking = mapper.convertValue(root.get("speaking"), Speaking.class);
            speaking.setTestId(test.getId());
            speakingRepository.save(speaking);
        }
    }

}


