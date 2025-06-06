package web.ielts.Test.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "Writing")
public class Writing {
    @Id
    private String id;
    private String testId;
    private WritingTask task1;
    private WritingTask task2;

    public Writing() {
    }

    public Writing(String id, String testId, WritingTask task1, WritingTask task2) {
        this.id = id;
        this.testId = testId;
        this.task1 = task1;
        this.task2 = task2;
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

    public WritingTask getTask1() {
        return task1;
    }

    public void setTask1(WritingTask task1) {
        this.task1 = task1;
    }

    public WritingTask getTask2() {
        return task2;
    }

    public void setTask2(WritingTask task2) {
        this.task2 = task2;
    }

    public static class WritingTask {
        private String prompt;
        private String imageUrl;

        public WritingTask() {
        }

        public WritingTask(String prompt, String imageUrl) {
            this.prompt = prompt;
            this.imageUrl = imageUrl;
        }

        public String getPrompt() {
            return prompt;
        }

        public void setPrompt(String prompt) {
            this.prompt = prompt;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }
}