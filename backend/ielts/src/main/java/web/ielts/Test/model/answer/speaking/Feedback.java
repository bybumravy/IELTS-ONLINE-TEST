package web.ielts.Test.model.answer.speaking;

import java.util.List;

public class Feedback {
    private List<ErrorCorrection> errorCorrections;
    private List<SentenceImprovement> sentenceImprovements;
    private String overallComment;
    private Evaluation evaluation;

    public Feedback() {
    }

    public List<ErrorCorrection> getErrorCorrections() {
        return errorCorrections;
    }

    public void setErrorCorrections(List<ErrorCorrection> errorCorrections) {
        this.errorCorrections = errorCorrections;
    }

    public List<SentenceImprovement> getSentenceImprovements() {
        return sentenceImprovements;
    }

    public void setSentenceImprovements(List<SentenceImprovement> sentenceImprovements) {
        this.sentenceImprovements = sentenceImprovements;
    }

    public String getOverallComment() {
        return overallComment;
    }

    public void setOverallComment(String overallComment) {
        this.overallComment = overallComment;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "errorCorrections=" + errorCorrections +
                ", sentenceImprovements=" + sentenceImprovements +
                ", overallComment='" + overallComment + '\'' +
                ", evaluation=" + evaluation +
                '}';
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }
}
