package web.ielts.Test.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import web.ielts.Test.model.Listening;
import web.ielts.Test.model.Reading;
import web.ielts.Test.model.Speaking;
import web.ielts.Test.model.Writing;
import web.ielts.Test.model.answer.listening.ListeningAnswer;
import web.ielts.Test.model.answer.reading.ReadingAnswer;
import web.ielts.Test.model.answer.speaking.SpeakingAnswer;
import web.ielts.Test.model.answer.speaking.SpeakingAnswerPart13;
import web.ielts.Test.model.answer.speaking.SpeakingAnswerPart2;
import web.ielts.Test.model.answer.speaking.SpeakingAnswerQuestion;
import web.ielts.Test.model.answer.writing.EvaluationWritingAnswer;
import web.ielts.Test.model.answer.writing.WritingAIResponse;
import web.ielts.Test.model.answer.writing.WritingAnswer;
import web.ielts.Test.repository.ListeningRepository;
import web.ielts.Test.repository.ReadingRepository;
import web.ielts.Test.repository.SpeakingRepository;
import web.ielts.Test.repository.WritingRepository;
import web.ielts.Test.repository.answer.ListeningAnswerRepository;
import web.ielts.Test.repository.answer.ReadingAnswerRepository;
import web.ielts.Test.repository.answer.SpeakingAnswerRepository;
import web.ielts.Test.repository.answer.WritingAnswerRepository;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DoTestService {
    @Autowired
    private WritingRepository writingRepository;

    @Autowired
    private ListeningRepository listeningRepository;

    @Autowired
    private ReadingRepository readingRepository;

    @Autowired
    private ReadingAnswerRepository readingAnswerRepository;

    @Autowired
    private WritingAnswerRepository writingAnswerRepository;

    @Autowired
    private ListeningAnswerRepository listeningAnswerRepository;
    @Autowired
    private SpeakingAnswerRepository speakingAnswerRepository;
    @Autowired
    private AIService aiService;
    @Autowired
    private SpeakingRepository speakingRepository;
    @Autowired
    private S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.region}")
    private String region;


    public Speaking getSpeakingByTestId(String testId) {
        return speakingRepository.findByTestId(testId);
    }
    public Optional<Writing> getWritingByTestId(String testId) {
        return writingRepository.findById(testId);
    }

    public List<Listening> getAllListeningTests() {
        return listeningRepository.findAll();
    }

    public Listening getListeningByTestId(String testId) {
        return listeningRepository.findByTestId(testId);
    }

    public Reading getReadingByTestId(String testId) {
        return readingRepository.findByTestId(testId);
    }

    public ReadingAnswer saveReadingAnswer(ReadingAnswer answer) {
        return readingAnswerRepository.save(answer);
    }

    public ListeningAnswer saveListeningAnswer(ListeningAnswer answer) {

        return listeningAnswerRepository.save(answer);
    }


    public WritingAnswer saveWritingAnswer(WritingAnswer answer) {
        WritingAnswer savedAnswer = writingAnswerRepository.save(answer);

        var task1 = savedAnswer.getTask1();
        try {
            WritingAIResponse eval1 = aiService.WritingTask1(task1.getQuestion(), task1.getAnswer());
            task1.setFeedback(eval1.getFeedback());
            task1.setSampleAnswer(eval1.getSampleAnswer());
            EvaluationWritingAnswer evaluation1 = eval1.getEvaluation();
            EvaluationWritingAnswer task1Eva = task1.getEvaluation();
            if (evaluation1 != null && task1Eva != null) {
                task1Eva.setTaskAchievement(evaluation1.getTaskAchievement());
                task1Eva.setCoherenceCohesion(evaluation1.getCoherenceCohesion());
                task1Eva.setLexicalResource(evaluation1.getLexicalResource());
                task1Eva.setGrammar(evaluation1.getGrammar());
            }
        } catch (Exception e) {
            task1.setFeedback("Error getting AI evaluation: " + e.getMessage());
        }

        var task2 = savedAnswer.getTask2();
        try {
            WritingAIResponse eval2 = aiService.WritingTask2(task2.getQuestion(), task2.getAnswer());
            task2.setFeedback(eval2.getFeedback());
            task2.setSampleAnswer(eval2.getSampleAnswer());
            EvaluationWritingAnswer evaluation2 = eval2.getEvaluation();
            EvaluationWritingAnswer task2Eva = task2.getEvaluation();
            if (evaluation2 != null && task2Eva != null) {
                task2Eva.setTaskAchievement(evaluation2.getTaskAchievement());
                task2Eva.setCoherenceCohesion(evaluation2.getCoherenceCohesion());
                task2Eva.setLexicalResource(evaluation2.getLexicalResource());
                task2Eva.setGrammar(evaluation2.getGrammar());
            }
        } catch (Exception e) {
            task2.setFeedback("Error getting AI evaluation: " + e.getMessage());
        }

        return writingAnswerRepository.save(savedAnswer);
    }

    public String uploadFile(MultipartFile file, String key) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key) // Không nối thêm filename nữa
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
    }
    public void updateAnswerUrls(SpeakingAnswer submission, Map<String, String> fileUrlMap) {
        // ✅ Debug log
        for (Map.Entry<String, String> entry : fileUrlMap.entrySet()) {
            String filename = entry.getKey();
            String s3Url = entry.getValue();
            System.out.println("File: " + filename + " → S3 URL: " + s3Url);
        }

        // 🔹 Part 1
        SpeakingAnswerPart13 part1 = submission.getPart1();
        if (part1 != null && part1.getQuestions() != null) {
            for (SpeakingAnswerQuestion qa : part1.getQuestions()) {
                String blob = qa.getStudentAnswer(); // blob:http://localhost/...
                String filename = extractFileName(blob);
                String s3Url = fileUrlMap.getOrDefault(filename, blob); // dùng filename
                System.out.println("Blob: " + blob + " → Filename: " + filename + " → S3: " + s3Url);
                qa.setStudentAnswer(s3Url);
            }
        }

        // 🔹 Part 2
        SpeakingAnswerPart2 part2 = submission.getPart2();
        if (part2 != null) {
            String blob = part2.getStudentAnswer();
            String filename = extractFileName(blob);
            String s3Url = fileUrlMap.getOrDefault(filename, blob);
            part2.setStudentAnswer(s3Url);
        }

        // 🔹 Part 3
        SpeakingAnswerPart13 part3 = submission.getPart3();
        if (part3 != null && part3.getQuestions() != null) {
            for (SpeakingAnswerQuestion qa : part3.getQuestions()) {
                String blob = qa.getStudentAnswer();
                String filename = extractFileName(blob);
                String s3Url = fileUrlMap.getOrDefault(filename, blob);
                qa.setStudentAnswer(s3Url);
            }
        }
    }
    private String extractFileName(String blobUrl) {
        // Ví dụ input: blob:http://localhost:5173/cd13919f-ec76-4e5e-a348-95e5c3f1265c
        try {
            return blobUrl.substring(blobUrl.lastIndexOf("/") + 1);
        } catch (Exception e) {
            return blobUrl; // fallback
        }
    }


    public SpeakingAnswer saveSubmission(SpeakingAnswer submission) {
        return speakingAnswerRepository.save(submission);
    }
}
