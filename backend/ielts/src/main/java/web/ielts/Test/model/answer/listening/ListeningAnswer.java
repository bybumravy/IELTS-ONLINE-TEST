package web.ielts.Test.model.answer.listening;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("ListeningAnswer")
public class ListeningAnswer {
    @Id
    private String id;
    private String testId;
    private List<TaskListeningAnswer> tasks;

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

    public List<TaskListeningAnswer> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskListeningAnswer> tasks) {
        this.tasks = tasks;
    }
    public ListeningAnswer() {
    }
    public ListeningAnswer(String id, String testId, List<TaskListeningAnswer> tasks) {
        this.id = id;
        this.testId = testId;
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "ListeningAnswer{" +
                "id='" + id + '\'' +
                ", testId='" + testId + '\'' +
                ", tasks=" + tasks +
                '}';
    }
}