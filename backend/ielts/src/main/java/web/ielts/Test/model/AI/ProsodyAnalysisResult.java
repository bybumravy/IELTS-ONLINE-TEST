package web.ielts.Test.model.AI;

import web.ielts.Test.model.answer.speaking.PronunciationEvaluation;
import web.ielts.Test.model.answer.speaking.StressMismatch;

import java.util.List;

public class ProsodyAnalysisResult {
    private List<StressMismatch> stressMismatchesDetailed;
    private List<PronunciationEvaluation> pronunciationEvaluation;

    public ProsodyAnalysisResult() {}

    public ProsodyAnalysisResult(List<StressMismatch> stressMismatchesDetailed,
                                 List<PronunciationEvaluation> pronunciationEvaluation) {
        this.stressMismatchesDetailed = stressMismatchesDetailed;
        this.pronunciationEvaluation = pronunciationEvaluation;
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

    @Override
    public String toString() {
        return "ProsodyAnalysisResult{" +
                "stressMismatchesDetailed=" + stressMismatchesDetailed +
                ", pronunciationEvaluation=" + pronunciationEvaluation +
                '}';
    }
}
