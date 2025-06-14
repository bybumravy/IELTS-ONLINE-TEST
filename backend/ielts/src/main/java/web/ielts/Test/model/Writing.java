package web.ielts.Test.model;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Document(collection = "Writing")
public class Writing {
    @Id
    private String testId;
    private Task task1;
    private Task task2;

    // Getter & Setter

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public Task getTask1() {
        return task1;
    }

    public void setTask1(Task task1) {
        this.task1 = task1;
    }

    public Task getTask2() {
        return task2;
    }

    public void setTask2(Task task2) {
        this.task2 = task2;
    }

    // Inner class cho Task
    public static class Task {


        private String type;
        private String question;
        private String imageUrl;


        // Getter & Setter


        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }


    }
}
