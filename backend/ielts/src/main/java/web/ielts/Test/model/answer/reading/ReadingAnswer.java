package web.ielts.Test.model.answer.reading;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "ReadingAnswer")
public class ReadingAnswer {
    @Id
    private String id;
    private String testId;
    private List<TaskReadingAnswer> taskReadingAnswers;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public List<TaskReadingAnswer> getTasks() {
        return taskReadingAnswers;
    }

    public void setTasks(List<TaskReadingAnswer> taskReadingAnswers) {
        this.taskReadingAnswers = taskReadingAnswers;
    }
}

