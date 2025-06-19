package web.ielts.Test.model.answer.writing;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "WritingAnswer")
public class WritingAnswer {
    @Id
    private String id;
    private String username;
    private String testId;
    private TaskWritingAnswer task1;
    private TaskWritingAnswer task2;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public TaskWritingAnswer getTask1() {
        return task1;
    }

    public void setTask1(TaskWritingAnswer task1) {
        this.task1 = task1;
    }

    public TaskWritingAnswer getTask2() {
        return task2;
    }

    public void setTask2(TaskWritingAnswer task2) {
        this.task2 = task2;
    }

}
