package web.ielts.Tips.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "ReadingTips")
public class ReadingTips {
    private String id;  // _id trong MongoDB

    private String type;  // "Sentence Completion"

    private String description;
private String skill;

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    private List<String> strategy;

    private List<String> tips;

    private List<Exercise> exercises;
    public static class Exercise {
        private String question;
        private List<String> options;
        private String answer;
        private String explanation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getStrategy() {
        return strategy;
    }

    public void setStrategy(List<String> strategy) {
        this.strategy = strategy;
    }

    public List<String> getTips() {
        return tips;
    }

    public void setTips(List<String> tips) {
        this.tips = tips;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }
}
