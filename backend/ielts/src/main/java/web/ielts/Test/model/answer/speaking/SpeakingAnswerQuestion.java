package web.ielts.Test.model.answer.speaking;


public class SpeakingAnswerQuestion {
    private String question;
    private String transcript;
    private String audioAnswer;
    private double score;
    private GrammarAnswer grammarAnswer;
    private GrammarAnswer lexicalAnswer;
    private PronunciationAnswer pronunciationAnswer;
    private FleCohAnswer fluencyCohAnswer;

    public SpeakingAnswerQuestion() {
    }

    public SpeakingAnswerQuestion(String question, String transcript, String audioAnswer, double score, GrammarAnswer grammarAnswer, GrammarAnswer lexicalAnswer, PronunciationAnswer pronunciationAnswer, FleCohAnswer fluencyCohAnswer) {
        this.question = question;
        this.transcript = transcript;
        this.audioAnswer = audioAnswer;
        this.score = score;
        this.grammarAnswer = grammarAnswer;
        this.lexicalAnswer = lexicalAnswer;
        this.pronunciationAnswer = pronunciationAnswer;
        this.fluencyCohAnswer = fluencyCohAnswer;
    }

    public String getAudioAnswer() {
        return audioAnswer;
    }

    public void setAudioAnswer(String audioAnswer) {
        this.audioAnswer = audioAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public GrammarAnswer getGrammarAnswer() {
        return grammarAnswer;
    }

    public void setGrammarAnswer(GrammarAnswer grammarAnswer) {
        this.grammarAnswer = grammarAnswer;
    }

    public GrammarAnswer getLexicalAnswer() {
        return lexicalAnswer;
    }

    public void setLexicalAnswer(GrammarAnswer lexicalAnswer) {
        this.lexicalAnswer = lexicalAnswer;
    }

    public PronunciationAnswer getPronunciationAnswer() {
        return pronunciationAnswer;
    }

    public void setPronunciationAnswer(PronunciationAnswer pronunciationAnswer) {
        this.pronunciationAnswer = pronunciationAnswer;
    }

    public FleCohAnswer getFluencyCohAnswer() {
        return fluencyCohAnswer;
    }

    public void setFluencyCohAnswer(FleCohAnswer fluencyCohAnswer) {
        this.fluencyCohAnswer = fluencyCohAnswer;
    }
}