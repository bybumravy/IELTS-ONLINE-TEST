package web.ielts.Test.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import web.ielts.Test.dto.HistoryTest;
import web.ielts.Test.model.*;
import web.ielts.Test.model.answer.listening.ListeningAnswer;
import web.ielts.Test.model.answer.reading.ReadingAnswer;
import web.ielts.Test.model.answer.speaking.*;
import web.ielts.Test.model.answer.writing.WritingAIResponse;
import web.ielts.Test.model.answer.writing.WritingAnswer;
import web.ielts.Test.repository.*;
import web.ielts.Test.repository.answer.ListeningAnswerRepository;
import web.ielts.Test.repository.answer.ReadingAnswerRepository;
import web.ielts.Test.repository.answer.SpeakingAnswerRepository;
import web.ielts.Test.repository.answer.WritingAnswerRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private TestRepository testRepository;
    @Autowired
    private AIService aiService;
    @Autowired
    private SpeakingRepository speakingRepository;
    @Autowired
    private S3Client s3Client;
    @Autowired
    private  WhisperService whisper;
    @Autowired
    private AiSpeakingService aiSpeakingService;
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
    public Test getTestByTestId(String testId) {
        return testRepository.findById(testId).orElse(null);
    }

    public ReadingAnswer saveReadingAnswer(ReadingAnswer answer) {
        return readingAnswerRepository.save(answer);
    }

    public ListeningAnswer saveListeningAnswer(ListeningAnswer answer) {

        return listeningAnswerRepository.save(answer);
    }


    public WritingAnswer saveWritingAnswer(WritingAnswer answer) {
        WritingAnswer savedAnswer = writingAnswerRepository.save(answer);
        if(savedAnswer.getGradingMethod().equalsIgnoreCase("AI")) {
            // Xử lý Task 1
            System.out.println("helolllllll ai "+savedAnswer.getGradingMethod());
            var task1 = savedAnswer.getTask1();
            try {
                WritingAIResponse eval1 = aiService.WritingTask1(task1.getImageUrl(), task1.getQuestion(), task1.getAnswer());

                // Set feedback và sample answer
                task1.setFeedback(eval1.getFeedback());
                task1.getFeedback().setErrorCorrections(eval1.getFeedback().getErrorCorrections());
                task1.getFeedback().setOverallComment(eval1.getFeedback().getOverallComment());
                task1.getFeedback().setSentenceImprovements(eval1.getFeedback().getSentenceImprovements());
                task1.setSampleAnswer(eval1.getSampleAnswer());
                task1.setScore(eval1.getScore());

                // Log evaluation
               System.out.println("================================");
                System.out.println("Task 1 Evaluation:");
                System.out.println("- Task Achievement: " + eval1.getEvaluation().getTaskAchievement());
                System.out.println("- Coherence Cohesion: " + eval1.getEvaluation().getCoherenceCohesion());
                System.out.println("- Lexical Resource: " + eval1.getEvaluation().getLexicalResource());
                System.out.println("- Grammar: " + eval1.getEvaluation().getGrammar());

                // Set evaluation
//            if (task1.getEvaluation() == null) {
//                task1.setEvaluation(new WritingEvaluation());
//            }
                task1.setEvaluation(eval1.getEvaluation());
                task1.getEvaluation().setTaskAchievement(eval1.getEvaluation().getTaskAchievement());
                task1.getEvaluation().setCoherenceCohesion(eval1.getEvaluation().getCoherenceCohesion());
                task1.getEvaluation().setLexicalResource(eval1.getEvaluation().getLexicalResource());
                task1.getEvaluation().setGrammar(eval1.getEvaluation().getGrammar());

            } catch (Exception e) {
                System.out.println("Error evaluating Task 1: " + e.getMessage());
            }

            // Xử lý Task 2
            var task2 = savedAnswer.getTask2();
            try {

                WritingAIResponse eval2 = aiService.WritingTask2(task2.getQuestion(), task2.getAnswer());

                // Set feedback và sample answer
                task2.setFeedback(eval2.getFeedback());
                task2.setSampleAnswer(eval2.getSampleAnswer());
                task2.setScore(eval2.getScore());


                task2.getFeedback().setErrorCorrections(eval2.getFeedback().getErrorCorrections());
                task2.getFeedback().setSentenceImprovements(eval2.getFeedback().getSentenceImprovements());
                task2.getFeedback().setOverallComment(eval2.getFeedback().getOverallComment());
                // Log evaluation
                System.out.println("================================");
                System.out.println("Task 2 Evaluation:");
                System.out.println("- Task Achievement: " + eval2.getEvaluation().getTaskAchievement());
                System.out.println("- Coherence Cohesion: " + eval2.getEvaluation().getCoherenceCohesion());
                System.out.println("- Lexical Resource: " + eval2.getEvaluation().getLexicalResource());
                System.out.println("- Grammar: " + eval2.getEvaluation().getGrammar());

                // Set evaluation
//            if (task2.getEvaluation() == null) {
//                task2.setEvaluation(new WritingEvaluation());
//            }
                task2.setEvaluation(eval2.getEvaluation());
                task2.getEvaluation().setTaskAchievement(eval2.getEvaluation().getTaskAchievement());
                task2.getEvaluation().setCoherenceCohesion(eval2.getEvaluation().getCoherenceCohesion());
                task2.getEvaluation().setLexicalResource(eval2.getEvaluation().getLexicalResource());
                task2.getEvaluation().setGrammar(eval2.getEvaluation().getGrammar());

            } catch (Exception e) {
                System.out.println("Error evaluating Task 2: " + e.getMessage());
            }
        }
        return writingAnswerRepository.save(savedAnswer);
    }

    public void updateAnswerUrls(SpeakingAnswer submission, Map<String, String> fileUrlMap) {
        // ✅ Debug log


        // 🔹 Part 1
        // 🔹 Part 1
        String gradingMethod = submission.getGradingMethod();
        SpeakingAnswerPart13 part1 = submission.getPart1();
        if (part1 != null && part1.getQuestions() != null) {

            double totalScore = 0;
            int validQuestionCount = part1.getQuestions().size();

            for (SpeakingAnswerQuestion qa : part1.getQuestions()) {
                String blob = qa.getStudentAnswer();
                String filename = extractFileName(blob);
                String s3Url = fileUrlMap.getOrDefault(filename, blob);
                String s3UrlNotEncrypt = s3Url;

                if (s3UrlNotEncrypt == null || s3UrlNotEncrypt.trim().isEmpty() || !s3UrlNotEncrypt.startsWith("http")) {

                    continue;
                }

                s3Url = UrlEncryptor.encodeUrl(s3Url);
                qa.setStudentAnswer(s3Url);
                if(gradingMethod.equalsIgnoreCase("ai")){
                    try {
                       String transcript = whisper.transcribe(s3UrlNotEncrypt);
                       System.out.println(transcript);
//                     EvaluationResult eval = aiSpeakingService.evaluateSpeaking(transcript, qa.getQuestion(),1);
//                     qa.setEvaluationResults(eval);
//
//                     totalScore += eval.getScore();

                    } catch (Exception e) {
                        System.err.println("❌ Lỗi khi chấm câu hỏi: " + qa.getQuestion());
                        e.printStackTrace();
                    }
                }
            }
            if(gradingMethod.equalsIgnoreCase("ai")) {
                double avgScore = totalScore/validQuestionCount;
                System.out.println("✅ Average Part 1 Score: " + avgScore);
                part1.setAverageScore(avgScore);
            }

        }

// 🔹 Part 2
        SpeakingAnswerPart2 part2 = submission.getPart2();
        if (part2 != null) {
            String blob = part2.getStudentAnswer();
            String filename = extractFileName(blob);
            String s3Url = fileUrlMap.getOrDefault(filename, blob);
            String s3UrlNotEncrypt = s3Url;


            if (s3UrlNotEncrypt == null) {
                System.out.println("⚠️ Bỏ qua Part 2 do URL không hợp lệ: " + s3UrlNotEncrypt);
            } else {
                s3Url = UrlEncryptor.encodeUrl(s3Url);
                part2.setStudentAnswer(s3Url);
                if(gradingMethod.equalsIgnoreCase("ai")) {
                    try {
                        String transcript = whisper.transcribe(s3UrlNotEncrypt);

//                     EvaluationResult eval = aiSpeakingService.evaluateSpeaking(transcript, part2.getQuestion(),2);
//                     part2.setEvaluationResults(eval);
//                   part2.setAverageScore(eval.getScore());

                        // validQuestionCount++;
                    } catch (Exception e) {
                        System.err.println("❌ Lỗi khi chấm Part 2");
                        e.printStackTrace();
                    }
                }



            }

        }

  //Part 3
       SpeakingAnswerPart13 part3 = submission.getPart3();
        if (part3 != null && part3.getQuestions() != null) {

            double totalScore = 0;
            int validQuestionCount = part3.getQuestions().size();

            for (SpeakingAnswerQuestion qa : part3.getQuestions()) {
                String blob = qa.getStudentAnswer();
                String filename = extractFileName(blob);
                String s3Url = fileUrlMap.getOrDefault(filename, blob);
                String s3UrlNotEncrypt = s3Url;

                if (s3UrlNotEncrypt == null || s3UrlNotEncrypt.trim().isEmpty() || !s3UrlNotEncrypt.startsWith("http")) {

                    continue;
                }

                s3Url = UrlEncryptor.encodeUrl(s3Url);
                qa.setStudentAnswer(s3Url);
                if(gradingMethod.equalsIgnoreCase("ai")) {
                    try {
//                        String transcript = whisper.transcribe(s3UrlNotEncrypt);
//                     EvaluationResult eval = aiSpeakingService.evaluateSpeaking(transcript, qa.getQuestion(),3);
//                   qa.setEvaluationResults(eval);
//                    totalScore += eval.getScore();

                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                }


            }
            if(gradingMethod.equalsIgnoreCase("ai")) {
                double avgScore = totalScore/validQuestionCount;
                System.out.println("✅ Average Part 3 Score: " + avgScore);
                part3.setAverageScore(avgScore);
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
    public String uploadFile(MultipartFile file, String key) throws IOException {
        try {
            // Giữ nguyên key gốc (không thay đổi đường dẫn thư mục)
            String originalKey = key;

            // Kiểm tra nếu là file WebM thì chuyển đổi
            if (file.getContentType().equals("audio/webm")) {
                File mp3File = AudioService.convertWebmToMp3(file);

                // Chỉ thay đổi phần đuôi file từ .webm sang .mp3
                String mp3Key = originalKey.replace(".webm", ".mp3");

                try (InputStream is = new FileInputStream(mp3File)) {
                    uploadToS3(is, mp3File.length(), mp3Key, "audio/mpeg");
                }

                mp3File.delete();
                return buildUrl(mp3Key);
            }
            // Upload trực tiếp nếu không phải WebM
            else {
                uploadToS3(file.getInputStream(), file.getSize(), originalKey, file.getContentType());
                return buildUrl(originalKey);
            }
        } catch (Exception e) {
            throw new IOException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    private void uploadToS3(InputStream inputStream, long contentLength,
                            String key, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest,
                RequestBody.fromInputStream(inputStream, contentLength));
    }

    private String buildUrl(String key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, key);
    }
    public List<HistoryTest> getListeningByUsername(String username) {
        List<ListeningAnswer> answers = listeningAnswerRepository.findByUsername(username);
        System.out.println("12");
        List<HistoryTest> historyTests = answers.stream().map(answer -> {
            HistoryTest history = new HistoryTest();
            history.setUsername(answer.getUsername());
            history.setSkill("listening");
            history.setTestID(answer.getTestId());
            return history;
        }).collect(Collectors.toList());
        return historyTests;
    }

    public List<HistoryTest> getWritingByUsername(String username) {
        List<WritingAnswer> answers = writingAnswerRepository.findByUsername(username);
        System.out.println("12");
        List<HistoryTest> historyTests = answers.stream().map(answer -> {
            HistoryTest history = new HistoryTest();
            history.setUsername(answer.getUsername());
            history.setSkill("writing");
            history.setTestID(answer.getTestId());
            return history;
        }).collect(Collectors.toList());
        return historyTests;
    }

    public List<HistoryTest> getSpeakingByUsername(String username) {
        List<SpeakingAnswer> answers = speakingAnswerRepository.findByUsername(username);
        System.out.println("12");
        List<HistoryTest> historyTests = answers.stream().map(answer -> {
            HistoryTest history = new HistoryTest();
            history.setUsername(answer.getUsername());
            history.setSkill("speaking");
            history.setTestID(answer.getTestId());
            return history;
        }).collect(Collectors.toList());
        return historyTests;
    }


    public List<HistoryTest> getReadingByUsername(String username) {
        List<ReadingAnswer> answers = readingAnswerRepository.findByUsername(username);
        System.out.println("12");
        List<HistoryTest> historyTests = answers.stream().map(answer -> {
            HistoryTest history = new HistoryTest();
            history.setUsername(answer.getUsername());
            history.setSkill("reading");
            history.setTestID(answer.getTestId());
            return history;
        }).collect(Collectors.toList());
        return historyTests;
    }
}