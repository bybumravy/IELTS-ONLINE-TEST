package web.ielts.Test.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Document(collection = "Listening")

public class Listening {
    private String _id; // e.g. "l1"
    private String testId;
    private String audioUrl;
    private List<TaskListening> tasks;

    // getters & setters
    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }
    public String getTestId() { return testId; }
    public void setTestId(String testId) { this.testId = testId; }
    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
    public List<TaskListening> getTasks() { return tasks; }
    public void setTasks(List<TaskListening> tasks) { this.tasks = tasks; }
}

class TaskListening {
    private int taskNumber;
    private String title;
    private String audioIntroduction;
    private List<Section> sections;

    // getters & setters
    public int getTaskNumber() { return taskNumber; }
    public void setTaskNumber(int taskNumber) { this.taskNumber = taskNumber; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAudioIntroduction() { return audioIntroduction; }
    public void setAudioIntroduction(String audioIntroduction) { this.audioIntroduction = audioIntroduction; }
    public List<Section> getSections() { return sections; }
    public void setSections(List<Section> sections) { this.sections = sections; }
}