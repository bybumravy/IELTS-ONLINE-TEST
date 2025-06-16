package web.ielts.Test.model.answer.writing;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EvaluationWritingAnswer {
    @JsonProperty("TaskAchievement")
    private String TaskAchievement;

    @JsonProperty("CoherenceCohesion")
    private String CoherenceCohesion;
    @JsonProperty("LexicalResource")
    private String LexicalResource;
    @JsonProperty("Grammar")
    private String Grammar;

    public String getTaskAchievement() {
        return TaskAchievement;
    }

    public void setTaskAchievement(String taskAchievement) {
        TaskAchievement = taskAchievement;
    }

    public String getCoherenceCohesion() {
        return CoherenceCohesion;
    }

    public void setCoherenceCohesion(String coherenceCohesion) {
        CoherenceCohesion = coherenceCohesion;
    }

    public String getLexicalResource() {
        return LexicalResource;
    }

    public void setLexicalResource(String lexicalResource) {
        LexicalResource = lexicalResource;
    }

    public String getGrammar() {
        return Grammar;
    }

    public void setGrammar(String grammar) {
        Grammar = grammar;
    }
}
