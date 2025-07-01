package web.ielts;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import web.ielts.Test.model.Writing;
import web.ielts.Test.model.answer.writing.*;
import web.ielts.Test.repository.answer.WritingAnswerRepository;
import web.ielts.Test.service.AIService;
import web.ielts.Test.service.DoTestService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class saveWritingAnswer {

    @InjectMocks
    private DoTestService writingAnswerService;

    @Mock
    private WritingAnswerRepository writingAnswerRepository;

    @Mock
    private AIService aiService;

    /**
     * Testcase TC01:
     * Chức năng: Kiểm tra khi input null
     * Câu lệnh được thực thi:
     *  - if (answer == null) → true
     * Nhánh được phủ: Nhánh if kiểm tra null input
     */
    @Test
    @DisplayName("Test null input should throw exception")
    void testSaveWritingAnswer_NullInput_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            writingAnswerService.saveWritingAnswer(null);
        });
    }

    /**
     * Testcase TC02:
     * Chức năng: Kiểm tra khi task1 và task2 đều null
     * Câu lệnh được thực thi:
     *  - if (answer == null) → false
     *  - if (answer.getTask1() != null) → false
     *  - if (answer.getTask2() != null) → false
     * Nhánh được phủ: Nhánh else không có task nào, chỉ save 2 lần và không gọi AI
     */
    @Test
    @DisplayName("Test no task1 and task2 present")
    void testSaveWritingAnswer_NoTask1NoTask2() {
        WritingAnswer answer = new WritingAnswer();
        when(writingAnswerRepository.save(any())).thenReturn(answer);

        WritingAnswer result = writingAnswerService.saveWritingAnswer(answer);

        assertNotNull(result);
        verify(writingAnswerRepository, times(2)).save(any());
        verifyNoInteractions(aiService);
    }

    /**
     * Testcase TC03:
     * Chức năng: Kiểm tra khi task1 và task2 tồn tại nhưng answer trống
     * Câu lệnh được thực thi:
     *  - if (answer.getTask1() != null) → true
     *  - if (task1.getAnswer() != null && !answer.isEmpty()) → false
     *  - if (answer.getTask2() != null) → true
     *  - if (task2.getAnswer() != null && !answer.isEmpty()) → false
     * Nhánh được phủ: Các nhánh else khi answer là chuỗi rỗng
     */
    @Test
    @DisplayName("Test task1 and task2 with empty answers")
    void testSaveWritingAnswer_EmptyAnswers() {
        TaskWritingAnswer task1 = new TaskWritingAnswer();
        task1.setAnswer("");

        TaskWritingAnswer task2 = new TaskWritingAnswer();
        task2.setAnswer("");

        WritingAnswer answer = new WritingAnswer();
        answer.setTask1(task1);
        answer.setTask2(task2);

        when(writingAnswerRepository.save(any())).thenReturn(answer);

        WritingAnswer result = writingAnswerService.saveWritingAnswer(answer);

        assertNotNull(result);
        verify(writingAnswerRepository, times(2)).save(any());
        verifyNoInteractions(aiService);
    }

    /**
     * Testcase TC04:
     * Chức năng: Kiểm tra khi task1 và task2 có answer nhưng AI service trả về null
     * Câu lệnh được thực thi:
     *  - if (answer.getTask1() != null) → true
     *  - if (task1.getAnswer() != null && !answer.isEmpty()) → true
     *  - gọi aiService.WritingTask1() trả về null
     *  - if (answer.getTask2() != null) → true
     *  - if (task2.getAnswer() != null && !answer.isEmpty()) → true
     *  - gọi aiService.WritingTask2() trả về null
     * Nhánh được phủ: Các nhánh if AI trả về null
     */
    @Test
    @DisplayName("Test AI response is null for both tasks")
    void testSaveWritingAnswer_AIResponseNull() {
        TaskWritingAnswer task1 = new TaskWritingAnswer();
        task1.setAnswer("Some answer");
        task1.setImageUrl("img.png");
        task1.setQuestion("Question?");

        TaskWritingAnswer task2 = new TaskWritingAnswer();
        task2.setAnswer("Another answer");
        task2.setQuestion("Question?");

        WritingAnswer answer = new WritingAnswer();
        answer.setTask1(task1);
        answer.setTask2(task2);

        when(writingAnswerRepository.save(any())).thenReturn(answer);
        when(aiService.WritingTask1(any(), any(), any())).thenReturn(null);
        when(aiService.WritingTask2(any(), any())).thenReturn(null);

        WritingAnswer result = writingAnswerService.saveWritingAnswer(answer);

        assertNotNull(result);
        verify(writingAnswerRepository, times(2)).save(any());
    }

    /**
     * Testcase TC05:
     * Chức năng: Kiểm tra khi AI trả về đầy đủ kết quả cho cả task1 và task2
     * Câu lệnh được thực thi:
     *  - if (answer.getTask1() != null) → true
     *  - if (task1.getAnswer() != null && !answer.isEmpty()) → true
     *  - gọi aiService.WritingTask1() trả về feedback + sample + score
     *  - if (answer.getTask2() != null) → true
     *  - if (task2.getAnswer() != null && !answer.isEmpty()) → true
     *  - gọi aiService.WritingTask2() trả về feedback + sample + score
     * Nhánh được phủ: Các nhánh if nhận được AI response đầy đủ
     */
    @Test
    @DisplayName("Test full AI response returned and updated")
    void testSaveWritingAnswer_FullAIResponse() {
        TaskWritingAnswer task1 = new TaskWritingAnswer();
        task1.setAnswer("Some answer");
        task1.setImageUrl("img.png");
        task1.setQuestion("Question?");

        TaskWritingAnswer task2 = new TaskWritingAnswer();
        task2.setAnswer("Another answer");
        task2.setQuestion("Question?");

        // AI Response cho task1
        WritingAIResponse aiResponse1 = new WritingAIResponse();
        WritingAIResponse.Feedback feedback1 = new WritingAIResponse.Feedback();
        feedback1.setOverallComment("Good");
        aiResponse1.setFeedback(feedback1);
        aiResponse1.setSampleAnswer("Sample");
        aiResponse1.setScore("7.5");
        aiResponse1.setEvaluation(new EvaluationWritingAnswer());

        // AI Response cho task2
        WritingAIResponse aiResponse2 = new WritingAIResponse();
        WritingAIResponse.Feedback feedback2 = new WritingAIResponse.Feedback();
        feedback2.setOverallComment("Excellent");
        aiResponse2.setFeedback(feedback2);
        aiResponse2.setSampleAnswer("Another sample");
        aiResponse2.setScore("8.0");
        aiResponse2.setEvaluation(new EvaluationWritingAnswer());

        WritingAnswer answer = new WritingAnswer();
        answer.setTask1(task1);
        answer.setTask2(task2);

        when(writingAnswerRepository.save(any())).thenReturn(answer);
        when(aiService.WritingTask1(any(), any(), any())).thenReturn(aiResponse1);
        when(aiService.WritingTask2(any(), any())).thenReturn(aiResponse2);

        WritingAnswer result = writingAnswerService.saveWritingAnswer(answer);

        assertNotNull(result);
        verify(writingAnswerRepository, times(2)).save(any());
        verify(aiService).WritingTask1(any(), any(), any());
        verify(aiService).WritingTask2(any(), any());
    }

}
