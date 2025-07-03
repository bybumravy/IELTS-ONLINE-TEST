package web.ielts.Test.model.answer.reading;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Document(collection = "ReadingAnswer")
public class ReadingAnswer {
    @Id
    private String id;
    private String testId;
    private List<TaskReadingAnswer> taskReadingAnswers;
    private String username;
    private String skill;
    private int totalQuestions;
    private int totalCorrect;
    private double band;
    private LocalDateTime submittedAt;


}