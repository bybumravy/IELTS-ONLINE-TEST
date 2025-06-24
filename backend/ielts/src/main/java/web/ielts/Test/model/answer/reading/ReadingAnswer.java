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
    private String username;
    private String skill;

    public ReadingAnswer(String id, String testId, List<TaskReadingAnswer> taskReadingAnswers, String username, String skill) {
        this.id = id;
        this.testId = testId;
        this.taskReadingAnswers = taskReadingAnswers;
        this.username = username;
        this.skill = skill;
    }

    public List<TaskReadingAnswer> getTaskReadingAnswers() {
        return taskReadingAnswers;
    }

    public void setTaskReadingAnswers(List<TaskReadingAnswer> taskReadingAnswers) {
        this.taskReadingAnswers = taskReadingAnswers;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

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

