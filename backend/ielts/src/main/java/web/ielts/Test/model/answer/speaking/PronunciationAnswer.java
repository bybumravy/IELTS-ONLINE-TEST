package web.ielts.Test.model.answer.speaking;

import java.util.List;

public class PronunciationAnswer {
    private double score;
    private String StressTranscript;
    private List<StressMismatch> stressMismatchesDetailed;
    private List<PronunciationEvaluation> pronunciationEvaluation;

    public PronunciationAnswer() {
    }

    public PronunciationAnswer(double score, String stressTranscript, List<StressMismatch> stressMismatchesDetailed, List<PronunciationEvaluation> pronunciationEvaluation) {
        this.score = score;
        StressTranscript = stressTranscript;
        this.stressMismatchesDetailed = stressMismatchesDetailed;
        this.pronunciationEvaluation = pronunciationEvaluation;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getStressTranscript() {
        return StressTranscript;
    }

    public void setStressTranscript(String stressTranscript) {
        StressTranscript = stressTranscript;
    }

    public List<StressMismatch> getStressMismatchesDetailed() {
        return stressMismatchesDetailed;
    }

    public void setStressMismatchesDetailed(List<StressMismatch> stressMismatchesDetailed) {
        this.stressMismatchesDetailed = stressMismatchesDetailed;
    }

    public List<PronunciationEvaluation> getPronunciationEvaluation() {
        return pronunciationEvaluation;
    }

    public void setPronunciationEvaluation(List<PronunciationEvaluation> pronunciationEvaluation) {
        this.pronunciationEvaluation = pronunciationEvaluation;
    }
}
