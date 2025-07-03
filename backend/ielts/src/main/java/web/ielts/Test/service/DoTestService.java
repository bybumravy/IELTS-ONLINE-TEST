package web.ielts.Test.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import web.ielts.Test.model.*;
import web.ielts.Test.model.answer.listening.ListeningAnswer;
import web.ielts.Test.model.answer.reading.ReadingAnswer;
import web.ielts.Test.model.answer.speaking.SpeakingAnswer;
import web.ielts.Test.model.answer.speaking.SpeakingAnswerPart13;
import web.ielts.Test.model.answer.speaking.SpeakingAnswerPart2;
import web.ielts.Test.model.answer.speaking.SpeakingAnswerQuestion;
import web.ielts.Test.model.answer.writing.WritingAIResponse;
import web.ielts.Test.model.answer.writing.WritingAnswer;
import web.ielts.Test.repository.*;
import web.ielts.Test.repository.answer.ListeningAnswerRepository;
import web.ielts.Test.repository.answer.ReadingAnswerRepository;
import web.ielts.Test.repository.answer.SpeakingAnswerRepository;
import web.ielts.Test.repository.answer.WritingAnswerRepository;
import web.ielts.Test.service.AI.AIService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private TestRepository testRepository;

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

    public Test getTestByTestId(String testId) {
        return testRepository.findById(testId).orElse(null);
    }

    public ReadingAnswer saveReadingAnswer(ReadingAnswer answer) {
        return readingAnswerRepository.save(answer);
    }

    public ListeningAnswer saveListeningAnswer(ListeningAnswer answer) {
        return listeningAnswerRepository.save(answer);
    }

    public WritingAnswer saveWritingAnswer(WritingAnswer answer) { //Unit Test saveWritingAnswer
        System.out.println("====== START SAVING WRITING ANSWER ======");

        // Check 1: Validate input
        if (answer == null) {
            System.out.println("[ERROR] Writing answer must not be null.");
            throw new IllegalArgumentException("Writing answer cannot be null");
        }
        System.out.println("Input validation passed");

        WritingAnswer savedAnswer = writingAnswerRepository.save(answer);
        System.out.println("📝 Saved answer to database with ID: " + savedAnswer.getId());

        // Process Task 1
        System.out.println("\n====== PROCESSING TASK 1 ======");
        var task1 = savedAnswer.getTask1();
        if (task1 != null) {  // Check if Task 1 exists
            System.out.println("🔍 Task 1 detected");
            try {
                // Check 2: Validate task 1 content
                if (StringUtils.isEmpty(task1.getAnswer())) {
                    System.out.println("⚠️ Task 1 answer is empty, skipping evaluation");
                } else {
                    System.out.println("🔄 Calling AI to evaluate Task 1...");
                    WritingAIResponse eval1 = aiService.WritingTask1(
                            task1.getImageUrl(),
                            task1.getQuestion(),
                            task1.getAnswer()
                    );

                    // Check 3: Validate AI response
                    if (eval1 == null) {
                        System.out.println("[WARNING] AI returned null for Task 1");
                    } else {
                        System.out.println("✅ Received Task 1 evaluation from AI");

                        // Set feedback if available
                        if (eval1.getFeedback() != null) {
                            task1.setFeedback(eval1.getFeedback());
                            System.out.println("📝 Feedback updated");
                        } else {
                            System.out.println("[WARNING] Feedback is null");
                        }

                        // Set sample answer if available
                        if (eval1.getSampleAnswer() != null) {
                            task1.setSampleAnswer(eval1.getSampleAnswer());
                            System.out.println("📝 Sample answer updated");
                        }

                        // Set score if available
                        if (eval1.getScore() != null) {
                            task1.setScore(eval1.getScore());
                            System.out.println("⭐ Score: " + eval1.getScore());
                        }

                        // Set detailed evaluation if available
                        if (eval1.getEvaluation() != null) {
                            task1.setEvaluation(eval1.getEvaluation());
                            System.out.println("📊 Detailed evaluation updated");
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("[ERROR] Error evaluating Task 1: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("⏩ No Task 1 to process");
        }

        // Process Task 2
        System.out.println("\n====== PROCESSING TASK 2 ======");
        var task2 = savedAnswer.getTask2();
        if (task2 != null) {  // Check if Task 2 exists
            System.out.println("🔍 Task 2 detected");
            try {
                // Check 4: Validate task 2 content
                if (StringUtils.isEmpty(task2.getAnswer())) {
                    System.out.println("⚠️ Task 2 answer is empty, skipping evaluation");
                } else {
                    System.out.println("🔄 Calling AI to evaluate Task 2...");
                    WritingAIResponse eval2 = aiService.WritingTask2(
                            task2.getQuestion(),
                            task2.getAnswer()
                    );

                    // Check AI response
                    if (eval2 == null) {
                        System.out.println("[WARNING] AI returned null for Task 2");
                    } else {
                        System.out.println("✅ Received Task 2 evaluation from AI");

                        // Set feedback if available
                        if (eval2.getFeedback() != null) {
                            task2.setFeedback(eval2.getFeedback());
                            System.out.println("📝 Feedback updated");
                        }

                        // Set sample answer if available
                        if (eval2.getSampleAnswer() != null) {
                            task2.setSampleAnswer(eval2.getSampleAnswer());
                            System.out.println("📝 Sample answer updated");
                        }

                        // Set score if available
                        if (eval2.getScore() != null) {
                            task2.setScore(eval2.getScore());
                            System.out.println("⭐ Score: " + eval2.getScore());
                        }

                        // Set detailed evaluation if available
                        if (eval2.getEvaluation() != null) {
                            task2.setEvaluation(eval2.getEvaluation());
                            System.out.println("📊 Detailed evaluation updated");
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("[ERROR] Error evaluating Task 2: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("⏩ No Task 2 to process");
        }

        // Save final result
        System.out.println("\n====== SAVING FINAL RESULT ======");
        WritingAnswer finalResult = writingAnswerRepository.save(savedAnswer);
        System.out.println("💾 Final result saved successfully");
        System.out.println("====== PROCESS COMPLETE ======\n");

        return finalResult;
    }

    // Update the answer URLs for speaking audio blobs to S3 URLs
    public void updateAnswerUrls(SpeakingAnswer submission, Map<String, String> fileUrlMap) {
        // Debug log all mappings
        for (Map.Entry<String, String> entry : fileUrlMap.entrySet()) {
            String filename = entry.getKey();
            String s3Url = entry.getValue();
            System.out.println("File: " + filename + " → S3 URL: " + s3Url);
        }

        // Update Part 1
        SpeakingAnswerPart13 part1 = submission.getPart1();
        if (part1 != null && part1.getQuestions() != null) {
            for (SpeakingAnswerQuestion qa : part1.getQuestions()) {
                String blob = qa.getStudentAnswer();
                String filename = extractFileName(blob);
                String s3Url = fileUrlMap.getOrDefault(filename, blob);
                qa.setStudentAnswer(s3Url);
            }
        }

        // Update Part 2
        SpeakingAnswerPart2 part2 = submission.getPart2();
        if (part2 != null) {
            String blob = part2.getStudentAnswer();
            String filename = extractFileName(blob);
            String s3Url = fileUrlMap.getOrDefault(filename, blob);
            part2.setStudentAnswer(s3Url);
        }

        // Update Part 3
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

    // Helper: Extract filename from blob URL
    private String extractFileName(String blobUrl) {
        try {
            return blobUrl.substring(blobUrl.lastIndexOf("/") + 1);
        } catch (Exception e) {
            return blobUrl; // fallback in case of error
        }
    }

    public SpeakingAnswer saveSubmission(SpeakingAnswer submission) {
        return speakingAnswerRepository.save(submission);
    }

    // Upload a file to S3 — converting WebM to MP3 if necessary
    public String uploadFile(MultipartFile file, String key) throws IOException {
        try {
            String originalKey = key;

            // If the file is WebM audio, convert to MP3 before uploading
            if (file.getContentType().equals("audio/webm")) {
                File mp3File = AudioService.convertWebmToMp3(file);
                String mp3Key = originalKey.replace(".webm", ".mp3");

                try (InputStream is = new FileInputStream(mp3File)) {
                    uploadToS3(is, mp3File.length(), mp3Key, "audio/mpeg");
                }

                mp3File.delete();
                return buildUrl(mp3Key);
            }
            // Otherwise, upload directly
            else {
                uploadToS3(file.getInputStream(), file.getSize(), originalKey, file.getContentType());
                return buildUrl(originalKey);
            }
        } catch (Exception e) {
            throw new IOException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    // Helper: Upload file to S3 bucket
    private void uploadToS3(InputStream inputStream, long contentLength, String key, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));
    }

    // Helper: Build public URL for a file in the S3 bucket
    private String buildUrl(String key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, key);
    }
}
