package web.ielts.Student.model;

import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.util.Date;

@Data
@Document(collection = "answers")
public class StudentResult {
    private String username;
    private String skill;
    private int band;
    private int totalCorrect;
    private Date submittedAt;
}
