package web.ielts.Test.model.answer.listening;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document("ListeningAnswer")
public class ListeningAnswer {
    @Id
    private String id;
    private String testId;
    private List<TaskListeningAnswer> tasks;
    private String username;
    private String skill;
    private int totalQuestions;
    private int totalCorrect;
    private double band;
    private LocalDateTime submittedAt;

}