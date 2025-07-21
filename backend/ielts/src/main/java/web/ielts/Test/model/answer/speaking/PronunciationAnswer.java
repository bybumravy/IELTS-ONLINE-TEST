package web.ielts.Test.model.answer.speaking;

import java.util.List;

public class PronunciationAnswer {
    private double score;
    private String stressTranscript;
    private List<StressMismatch> stressMismatchesDetailed;
    private List<IntonationSentence> pronunciationEvaluation;
    private String transcript;
    private List<String> importantWords;
    private List<String> emphasizedWords;
    private List<String> correctEmphasizedWords;
    private List<String> correctEmphasizedSentences;

    public PronunciationAnswer() {
    }

    public PronunciationAnswer(double score, String stressTranscript, List<StressMismatch> stressMismatchesDetailed, List<IntonationSentence> pronunciationEvaluation) {
        this.score = score;
        this.stressTranscript = stressTranscript;
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
        return stressTranscript;
    }

    public void setStressTranscript(String stressTranscript) {
        this.stressTranscript = stressTranscript;
    }

    public List<StressMismatch> getStressMismatchesDetailed() {
        return stressMismatchesDetailed;
    }

    public void setStressMismatchesDetailed(List<StressMismatch> stressMismatchesDetailed) {
        this.stressMismatchesDetailed = stressMismatchesDetailed;
    }

    public List<IntonationSentence> getPronunciationEvaluation() {
        return pronunciationEvaluation;
    }

    public void setPronunciationEvaluation(List<IntonationSentence> intonationSentence) {
        this.pronunciationEvaluation = intonationSentence;
    }

    public String getTranscript() {
        return transcript;
    }
    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }
    public List<String> getImportantWords() {
        return importantWords;
    }
    public void setImportantWords(List<String> importantWords) {
        this.importantWords = importantWords;
    }
    public List<String> getEmphasizedWords() {
        return emphasizedWords;
    }
    public void setEmphasizedWords(List<String> emphasizedWords) {
        this.emphasizedWords = emphasizedWords;
    }
    public List<String> getCorrectEmphasizedWords() {
        return correctEmphasizedWords;
    }
    public void setCorrectEmphasizedWords(List<String> correctEmphasizedWords) {
        this.correctEmphasizedWords = correctEmphasizedWords;
    }
    public List<String> getCorrectEmphasizedSentences() {
        return correctEmphasizedSentences;
    }
    public void setCorrectEmphasizedSentences(List<String> correctEmphasizedSentences) {
        this.correctEmphasizedSentences = correctEmphasizedSentences;
    }
}
