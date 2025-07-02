package web.ielts.Auth;

import org.junit.jupiter.api.Test;

import web.ielts.Test.model.answer.speaking.*;
import web.ielts.Test.service.DoTestService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UpdateAnswerUrlsTest {

    private final DoTestService service = new DoTestService(); // ✅ Đúng class

    private String blob(String filename) {
        return "blob:http://localhost/" + filename;
    }

    // ✅ TC01: All parts (Part1, 2, 3) are present with matching blob names
    @Test
    void test_TC01_UpdateAllPartsCorrectly() {
        String b1 = blob("p1_q1.webm");
        String b2 = blob("p2_ans.webm");
        String b3 = blob("p3_q1.webm");

        Map<String, String> fileUrlMap = Map.of(
                "p1_q1.webm", "https://s3.com/p1.mp3",
                "p2_ans.webm", "https://s3.com/p2.mp3",
                "p3_q1.webm", "https://s3.com/p3.mp3"
        );

        SpeakingAnswer submission = new SpeakingAnswer();

        SpeakingAnswerQuestion q1 = new SpeakingAnswerQuestion();
        q1.setStudentAnswer(b1);
        SpeakingAnswerPart13 part1 = new SpeakingAnswerPart13();
        part1.setQuestions(List.of(q1));
        submission.setPart1(part1);

        SpeakingAnswerPart2 part2 = new SpeakingAnswerPart2();
        part2.setStudentAnswer(b2);
        submission.setPart2(part2);

        SpeakingAnswerQuestion q3 = new SpeakingAnswerQuestion();
        q3.setStudentAnswer(b3);
        SpeakingAnswerPart13 part3 = new SpeakingAnswerPart13();
        part3.setQuestions(List.of(q3));
        submission.setPart3(part3);

        service.updateAnswerUrls(submission, fileUrlMap);

        assertEquals("https://s3.com/p1.mp3", submission.getPart1().getQuestions().get(0).getStudentAnswer());
        assertEquals("https://s3.com/p2.mp3", submission.getPart2().getStudentAnswer());
        assertEquals("https://s3.com/p3.mp3", submission.getPart3().getQuestions().get(0).getStudentAnswer());
    }

    // ✅ TC02: All parts are null → nothing should happen
    @Test
    void test_TC02_SubmissionWithNullParts() {
        SpeakingAnswer submission = new SpeakingAnswer(); // all null
        Map<String, String> fileUrlMap = Map.of("any.webm", "https://s3.com/any.mp3");

        assertDoesNotThrow(() -> service.updateAnswerUrls(submission, fileUrlMap));

        assertNull(submission.getPart1());
        assertNull(submission.getPart2());
        assertNull(submission.getPart3());
    }
}