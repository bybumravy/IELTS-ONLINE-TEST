package web.ielts.Test.model;

import java.util.Map;

public class EvaluationResult {
    private String transcript;
    private Map<String, Double> score;
    private double overallBand;
    private String feedback;

    public EvaluationResult(String transcript, Map<String, Double> score, double overallBand, String feedback) {
        this.transcript = transcript;
        this.score = score;
        this.overallBand = overallBand;
        this.feedback = feedback;
    }

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    public Map<String, Double> getScore() {
        return score;
    }

    public void setScore(Map<String, Double> score) {
        this.score = score;
    }

    public double getOverallBand() {
        return overallBand;
    }

    public void setOverallBand(double overallBand) {
        this.overallBand = overallBand;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
