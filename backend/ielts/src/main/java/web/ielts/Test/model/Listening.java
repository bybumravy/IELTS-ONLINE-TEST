package web.ielts.Test.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Document(collection = "Listening")

public class Listening {
    @Id
    private String testId;
    private String audioUrl;
    private List<TaskListening> tasks;


    // getters & setters
    public String getTestId() { return testId; }
    public void setTestId(String testId) { this.testId = testId; }
    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
    public List<TaskListening> getTasks() { return tasks; }
    public void setTasks(List<TaskListening> tasks) { this.tasks = tasks; }
}

class TaskListening {
    private int taskNumber;
    private List<Section> sections;


    public int getTaskNumber() { return taskNumber; }
    public void setTaskNumber(int taskNumber) { this.taskNumber = taskNumber; }
    public List<Section> getSections() { return sections; }
    public void setSections(List<Section> sections) { this.sections = sections; }
}