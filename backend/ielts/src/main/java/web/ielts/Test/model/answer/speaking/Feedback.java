package web.ielts.Test.model.answer.speaking;

import java.util.List;

public class Feedback {
    private List<ErrorCorrection> errorCorrections;
    private String overallComment;


    public Feedback() {
    }


    public List<ErrorCorrection> getErrorCorrections() {
        return errorCorrections;
    }

    public void setErrorCorrections(List<ErrorCorrection> errorCorrections) {
        this.errorCorrections = errorCorrections;
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
                ", overallComment='" + overallComment + '\'' +

                '}';
    }


}
