package web.ielts.Test.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web.ielts.Test.repository.*;
import web.ielts.Test.dto.ListTest;
import web.ielts.Test.model.Test;
import web.ielts.Test.model.Listening;
import web.ielts.Test.model.Reading;
import web.ielts.Test.model.Writing;
import web.ielts.Test.model.Speaking;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TestService {
    private static final Logger logger = LoggerFactory.getLogger(TestService.class);

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
        List<Test> tests = testRepository.findAll();
        logger.info("Found {} tests in database", tests.size());

        return tests.stream()
                .filter(t -> t.getCreatedAt() != null && !t.getCreatedAt().isEmpty())
                .map(t -> {
                    try {
                        LocalDate date = LocalDate.parse(t.getCreatedAt());
                        int year = date.getYear();
                        return new ListTest(t.getId(), t.getTestTitle(), year);
                    } catch (DateTimeParseException e) {
                        logger.warn("Invalid createdAt format for test '{}': {}", t.getTestTitle(), t.getCreatedAt());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(ListTest::getYear));
    }

    public Map<Integer, List<ListTest>> getListeningTestsByYear() {
        List<Listening> listenings = listeningRepository.findAll();
        logger.info("Found {} listenings in collection", listenings.size());
        List<Test> allTests = testRepository.findAll();
        logger.info("Found {} tests in collection", allTests.size());

        Map<String, Test> testMap = allTests.stream()
                .collect(Collectors.toMap(Test::getId, t -> t));
        logger.info("Created testMap with {} entries", testMap.size());

        List<ListTest> dtoList = listenings.stream()
                .map(listening -> {
                    String testId = listening.getTestId();
                    Test test = testMap.get(testId);
                    if (test == null) {
                        logger.warn("No test found for testId: {}", testId);
                        return null;
                    }
                    if (test.getCreatedAt() == null || test.getCreatedAt().isEmpty()) {
                        logger.warn("Invalid createdAt for testId: {}", testId);
                        return null;
                    }
                    logger.info("Processing testId {} with createdAt: {}", testId, test.getCreatedAt());
                    try {
                        int year = LocalDate.parse(test.getCreatedAt()).getYear();
                        return new ListTest(test.getId(), test.getTestTitle(), year);
                    } catch (DateTimeParseException e) {
                        logger.warn("Invalid date format for testId {}: {}", testId, test.getCreatedAt());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        logger.info("Created dtoList with {} entries", dtoList.size());

        return dtoList.stream()
                .collect(Collectors.groupingBy(ListTest::getYear));
    }

    public Map<Integer, List<ListTest>> getReadingTestsByYear() {
        List<Reading> readings = readingRepository.findAll();
        logger.info("Found {} readings in collection", readings.size());
        List<Test> allTests = testRepository.findAll();
        logger.info("Found {} tests in collection", allTests.size());

        Map<String, Test> testMap = allTests.stream()
                .collect(Collectors.toMap(Test::getId, t -> t));
        logger.info("Created testMap with {} entries", testMap.size());

        List<ListTest> dtoList = readings.stream()
                .map(reading -> {
                    String testId = reading.getTestId();
                    Test test = testMap.get(testId);
                    if (test == null) {
                        logger.warn("No test found for testId: {}", testId);
                        return null;
                    }
                    if (test.getCreatedAt() == null || test.getCreatedAt().isEmpty()) {
                        logger.warn("Invalid createdAt for testId: {}", testId);
                        return null;
                    }
                    logger.info("Processing testId {} with createdAt: {}", testId, test.getCreatedAt());
                    try {
                        int year = LocalDate.parse(test.getCreatedAt()).getYear();
                        return new ListTest(test.getId(), test.getTestTitle(), year);
                    } catch (DateTimeParseException e) {
                        logger.warn("Invalid date format for testId {}: {}", testId, test.getCreatedAt());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        logger.info("Created dtoList with {} entries", dtoList.size());

        return dtoList.stream()
                .collect(Collectors.groupingBy(ListTest::getYear));
    }

    public Map<Integer, List<ListTest>> getWritingTestsByYear() {
        List<Writing> writings = writingRepository.findAll();
        logger.info("Found {} writings in collection", writings.size());
        List<Test> allTests = testRepository.findAll();
        logger.info("Found {} tests in collection", allTests.size());

        Map<String, Test> testMap = allTests.stream()
                .collect(Collectors.toMap(Test::getId, t -> t));
        logger.info("Created testMap with {} entries", testMap.size());

        List<ListTest> dtoList = writings.stream()
                .map(writing -> {
                    String testId = writing.getTestId();
                    Test test = testMap.get(testId);
                    if (test == null) {
                        logger.warn("No test found for testId: {}", testId);
                        return null;
                    }
                    if (test.getCreatedAt() == null || test.getCreatedAt().isEmpty()) {
                        logger.warn("Invalid createdAt for testId: {}", testId);
                        return null;
                    }
                    logger.info("Processing testId {} with createdAt: {}", testId, test.getCreatedAt());
                    try {
                        int year = LocalDate.parse(test.getCreatedAt()).getYear();
                        return new ListTest(test.getId(), test.getTestTitle(), year);
                    } catch (DateTimeParseException e) {
                        logger.warn("Invalid date format for testId {}: {}", testId, test.getCreatedAt());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        logger.info("Created dtoList with {} entries", dtoList.size());

        return dtoList.stream()
                .collect(Collectors.groupingBy(ListTest::getYear));
    }

    public Map<Integer, List<ListTest>> getSpeakingTestsByYear() {
        List<Speaking> speakings = speakingRepository.findAll();
        logger.info("Found {} speakings in collection", speakings.size());
        List<Test> allTests = testRepository.findAll();
        logger.info("Found {} tests in collection", allTests.size());

        Map<String, Test> testMap = allTests.stream()
                .collect(Collectors.toMap(Test::getId, t -> t));
        logger.info("Created testMap with {} entries", testMap.size());

        List<ListTest> dtoList = speakings.stream()
                .map(speaking -> {
                    String testId = speaking.getTestId();
                    Test test = testMap.get(testId);
                    if (test == null) {
                        logger.warn("No test found for testId: {}", testId);
                        return null;
                    }
                    if (test.getCreatedAt() == null || test.getCreatedAt().isEmpty()) {
                        logger.warn("Invalid createdAt for testId: {}", testId);
                        return null;
                    }
                    logger.info("Processing testId {} with createdAt: {}", testId, test.getCreatedAt());
                    try {
                        int year = LocalDate.parse(test.getCreatedAt()).getYear();
                        return new ListTest(test.getId(), test.getTestTitle(), year);
                    } catch (DateTimeParseException e) {
                        logger.warn("Invalid date format for testId {}: {}", testId, test.getCreatedAt());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        logger.info("Created dtoList with {} entries", dtoList.size());

        return dtoList.stream()
                .collect(Collectors.groupingBy(ListTest::getYear));
    }

    public List<Test> getAllTests() {
        return testRepository.findAll();
    }
}