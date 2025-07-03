package web.ielts.Test.model.answer.speaking;

public class Evaluation {
    private ScoreReview lexicalResource;
    private ScoreReview grammar;

    public Evaluation() {
    }

    @Override
    public String toString() {
        return "Evaluation{" +
                "lexicalResource=" + lexicalResource +
                ", grammar=" + grammar +
                '}';
    }

    public ScoreReview getLexicalResource() {
        return lexicalResource;
    }

    public void setLexicalResource(ScoreReview lexicalResource) {
        this.lexicalResource = lexicalResource;
    }

    public ScoreReview getGrammar() {
        return grammar;
    }

    public void setGrammar(ScoreReview grammar) {
        this.grammar = grammar;
    }
}
