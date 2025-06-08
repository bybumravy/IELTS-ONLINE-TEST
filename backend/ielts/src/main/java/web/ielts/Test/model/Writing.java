package web.ielts.Test.model;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Document(collection = "Writing")
public class Writing {
    @Id
    private ObjectId id;
    private String testId;
    private List<Task> tasks;

    // Getter & Setter

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    // Inner class cho Task
    public static class Task {

        private int taskNumber;
        private String type;
        private String question;
        private String sampleAnswer;

        // Getter & Setter

        public int getTaskNumber() {
            return taskNumber;
        }

        public void setTaskNumber(int taskNumber) {
            this.taskNumber = taskNumber;
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

        public String getSampleAnswer() {
            return sampleAnswer;
        }

        public void setSampleAnswer(String sampleAnswer) {
            this.sampleAnswer = sampleAnswer;
        }
    }
}
