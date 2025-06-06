package web.ielts.Tips.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "WritingTips")
public class WritingTips {
    private String id;

    private String task;
    private String skill;

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    private String description;

    private List<String> strategy;

    private List<String> tips;

    private List<Exercise> exercises;
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class Exercise {
        private String prompt;
        private String modelAnswer;
        private String explanation;

        public String getPrompt() {
            return prompt;
        }

        public void setPrompt(String prompt) {
            this.prompt = prompt;
        }

        public String getModelAnswer() {
            return modelAnswer;
        }

        public void setModelAnswer(String modelAnswer) {
            this.modelAnswer = modelAnswer;
        }

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
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