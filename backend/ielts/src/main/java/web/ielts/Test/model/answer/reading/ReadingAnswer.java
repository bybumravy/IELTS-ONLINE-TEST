package web.ielts.Test.model.answer.reading;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "ReadingAnswer")
public class ReadingAnswer {
    @Id
    private String id;
    private String testId;
    private List<TaskReadingAnswer> taskReadingAnswers = new ArrayList<>();
    private String username;
    private String skill;
    private int totalQuestions;
    private int totalCorrect;
    private double band;
    private LocalDateTime submittedAt;

    public ReadingAnswer() {
    }

    public ReadingAnswer(String id, String testId, List<TaskReadingAnswer> taskReadingAnswers, String username, String skill, int totalQuestions, int totalCorrect, double band, LocalDateTime submittedAt) {
        this.id = id;
        this.testId = testId;
        this.taskReadingAnswers = taskReadingAnswers;
        this.username = username;
        this.skill = skill;
        this.totalQuestions = totalQuestions;
        this.totalCorrect = totalCorrect;
        this.band = band;
        this.submittedAt = submittedAt;
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

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getTotalCorrect() {
        return totalCorrect;
    }

    public void setTotalCorrect(int totalCorrect) {
        this.totalCorrect = totalCorrect;
    }

    public double getBand() {
        return band;
    }

    public void setBand(double band) {
        this.band = band;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    @Override
    public String toString() {
        return "ReadingAnswer{" +
                "id='" + id + '\'' +
                ", testId='" + testId + '\'' +
                ", taskReadingAnswers=" + taskReadingAnswers +
                ", username='" + username + '\'' +
                ", skill='" + skill + '\'' +
                ", totalQuestions=" + totalQuestions +
                ", totalCorrect=" + totalCorrect +
                ", band=" + band +
                ", submittedAt=" + submittedAt +
                '}';
    }
}