package web.ielts.Test.model.answer.writing;

public class EvaluationWritingAnswer {
    private String TaskAchievement;
    private String CoherenceCohesion;
    private String LexicalResource;
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
