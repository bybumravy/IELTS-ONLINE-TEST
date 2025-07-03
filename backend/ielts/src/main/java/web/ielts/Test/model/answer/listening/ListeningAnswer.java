package web.ielts.Test.model.answer.listening;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
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
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Ho_Chi_Minh")
    private LocalDate submittedAt;
}