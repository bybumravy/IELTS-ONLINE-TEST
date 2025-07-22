package web.ielts.Student.model;

import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.util.Date;

@Data
@Document(collection = "answers")
public class StudentResult {
    private String username;
    private String skill;
    private Double band;           // Sửa kiểu thành Double
    private Integer totalCorrect;  // Sửa kiểu thành Integer
    private Date submittedAt;
}
