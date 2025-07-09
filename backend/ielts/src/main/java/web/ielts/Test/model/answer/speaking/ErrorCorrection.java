package web.ielts.Test.model.answer.speaking;

public class ErrorCorrection {
    private String originalText;
    private String correctedText;
    private String errorType;
    private String explanation;
    private String sentenceContext;

    public ErrorCorrection() {

    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getCorrectedText() {
        return correctedText;
    }

    public void setCorrectedText(String correctedText) {
        this.correctedText = correctedText;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    @Override
    public String toString() {
        return "ErrorCorrection{" +
                "originalText='" + originalText + '\'' +
                ", correctedText='" + correctedText + '\'' +
                ", errorType='" + errorType + '\'' +
                ", explanation='" + explanation + '\'' +
                ", sentenceContext='" + sentenceContext + '\'' +
                '}';
    }

    public String getSentenceContext() {
        return sentenceContext;
    }

    public void setSentenceContext(String sentenceContext) {
        this.sentenceContext = sentenceContext;
    }
}
