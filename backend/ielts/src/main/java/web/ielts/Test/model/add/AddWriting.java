package web.ielts.Test.model.add;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "RequestWriting")
public class AddWriting {
    @Id
    private String id;
    private String testId;
    private List<AddWritingTask> tasks;

    public AddWriting() {
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

    public List<AddWritingTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<AddWritingTask> tasks) {
        this.tasks = tasks;
    }


    public static class AddWritingTask {
        private int taskNumber;
        private String imageUrl;
        private String question;

        public AddWritingTask() {
        }

        public int getTaskNumber() {
            return taskNumber;
        }

        public void setTaskNumber(int taskNumber) {
            this.taskNumber = taskNumber;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }
    }
}
