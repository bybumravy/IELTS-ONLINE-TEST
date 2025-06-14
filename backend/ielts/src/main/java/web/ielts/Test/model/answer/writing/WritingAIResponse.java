package web.ielts.Test.model.answer.writing;

public class WritingAIResponse {
    private String feedback;
    private EvaluationWritingAnswer evaluation;
    private String sampleAnswer;

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public EvaluationWritingAnswer getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(EvaluationWritingAnswer evaluation) {
        this.evaluation = evaluation;
    }

    public String getSampleAnswer() {
        return sampleAnswer;
    }

    public void setSampleAnswer(String sampleAnswer) {
        this.sampleAnswer = sampleAnswer;
    }
}
