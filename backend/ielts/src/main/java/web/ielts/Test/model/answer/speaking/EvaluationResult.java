package web.ielts.Test.model.answer.speaking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;
@JsonIgnoreProperties(ignoreUnknown = true)
public class EvaluationResult {
    private String transcript;
    private double fluency;
    private double grammar;
    private double vocabulary;
    private double pronunciation;
    private double score;
    private Feedback feedback;


    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    public double getFluency() {
        return fluency;
    }

    public void setFluency(double fluency) {
        this.fluency = fluency;
    }

    public double getGrammar() {
        return grammar;
    }

    public void setGrammar(double grammar) {
        this.grammar = grammar;
    }

    public double getVocabulary() {
        return vocabulary;
    }

    public void setVocabulary(double vocabulary) {
        this.vocabulary = vocabulary;
    }

    public double getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(double pronunciation) {
        this.pronunciation = pronunciation;
    }

    public double getOverallBand() {
        return overallBand;
    }

    public void setOverallBand(double overallBand) {
        this.overallBand = overallBand;
    }

    @Override
    public String toString() {
        return "EvaluationResult{" +
                "transcript='" + transcript + '\'' +
                ", fluency=" + fluency +
                ", grammar=" + grammar +
                ", vocabulary=" + vocabulary +
                ", pronunciation=" + pronunciation +
                ", overallBand=" + overallBand +
                ", feedback=" + feedback +
                '}';
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    // ✅ Constructor mặc định để Jackson có thể deserialize
    public EvaluationResult() {}



}
