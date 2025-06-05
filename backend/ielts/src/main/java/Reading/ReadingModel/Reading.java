package Reading.ReadingModel;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document("Test")
public class Reading {
    private String id;
    private String testID;
    private List<Task> tasks;
    public String getId() {
        return id;
    }
    public Reading(String id, String testID, List<Task> tasks) {
        this.id = id;
        this.testID = testID;
        this.tasks = tasks;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTestID() {
        return testID;
    }
    public void setTestID(String testID) {
        this.testID = testID;
    }
    public List<Task> getTasks() {
        return tasks;
    }
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Reading{");
        sb.append("id=").append(id);
        sb.append(", testID=").append(testID);
        sb.append(", tasks=").append(tasks);
        sb.append('}');
        return sb.toString();
    }
    
}
