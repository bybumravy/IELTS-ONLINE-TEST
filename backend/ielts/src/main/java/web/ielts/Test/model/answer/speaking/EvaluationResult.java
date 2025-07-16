package web.ielts.Test.model.answer.speaking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;
@JsonIgnoreProperties(ignoreUnknown = true)
public class EvaluationResult {
    private String transcript;
   private double score;
    private Feedback feedback;
    private Evaluation evaluation;


    @Override
    public String toString() {
        return "EvaluationResult{" +
                "transcript='" + transcript + '\'' +
                ", score=" + score +
                ", feedback=" + feedback +
                ", evaluation=" + evaluation +
                '}';
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }



    // ✅ Constructor mặc định để Jackson có thể deserialize
    public EvaluationResult() {}



}
