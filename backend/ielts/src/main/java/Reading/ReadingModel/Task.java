package Reading.ReadingModel;

import java.util.List;

public class Task {
    private int taskNumber;
    private String title;
    private String graph;
    private List<Section> sections;
    public Task(int taskNumber, String title, String graph, List<Section> sections) {
        this.taskNumber = taskNumber;
        this.title = title;
        this.graph = graph;
        this.sections = sections;
    }
    public int getTaskNumber() {
        return taskNumber;
    }
    public void setTaskNumber(int taskNumber) {
        this.taskNumber = taskNumber;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getGraph() {
        return graph;
    }
    public void setGraph(String graph) {
        this.graph = graph;
    }
    public List<Section> getSections() {
        return sections;
    }
    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Task{");
        sb.append("taskNumber=").append(taskNumber);
        sb.append(", title=").append(title);
        sb.append(", graph=").append(graph);
        sb.append(", sections=").append(sections);
        sb.append('}');
        return sb.toString();
    }
    
}
