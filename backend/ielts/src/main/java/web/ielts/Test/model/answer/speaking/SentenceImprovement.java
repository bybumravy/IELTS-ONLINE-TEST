package web.ielts.Test.model.answer.speaking;

import java.util.List;

public class SentenceImprovement {
    private String originalSentence;
    private String improvedSentence;
    private List<String> techniquesUsed;
    private String bandBoost;

    public SentenceImprovement() {
    }

    public String getOriginalSentence() {
        return originalSentence;
    }

    public void setOriginalSentence(String originalSentence) {
        this.originalSentence = originalSentence;
    }

    public String getImprovedSentence() {
        return improvedSentence;
    }

    public void setImprovedSentence(String improvedSentence) {
        this.improvedSentence = improvedSentence;
    }

    public List<String> getTechniquesUsed() {
        return techniquesUsed;
    }

    public void setTechniquesUsed(List<String> techniquesUsed) {
        this.techniquesUsed = techniquesUsed;
    }

    public String getBandBoost() {
        return bandBoost;
    }

    public void setBandBoost(String bandBoost) {
        this.bandBoost = bandBoost;
    }
}
