package web.ielts.Payment.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "coursePremium")
public class Course {
    private String id;
    private String name;
    private long price;
    private long originalPrice;
    private String description;
    private String duration;
}