package web.ielts.Test.model.answer.speaking;

public class PronunciationEvaluation {
    private String text;
    private String sentenceText;

    public PronunciationEvaluation() {
    }

    public PronunciationEvaluation(String text, String sentenceText) {
        this.text = text;
        this.sentenceText = sentenceText;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSentenceText() {
        return sentenceText;
    }

    public void setSentenceText(String sentenceText) {
        this.sentenceText = sentenceText;
    }

    @Override
    public String toString() {
        return "PronunciationEvaluation{" +
                "text='" + text + '\'' +
                ", sentenceText='" + sentenceText + '\'' +
                '}';
    }
}
