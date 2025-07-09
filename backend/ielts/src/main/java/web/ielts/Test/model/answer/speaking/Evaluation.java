package web.ielts.Test.model.answer.speaking;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Evaluation {
    @JsonProperty("LexicalResource")
    private LexicalResource lexicalResource;

    @JsonProperty("Grammar")
    private Grammar grammar;

    public Evaluation() {}


    public LexicalResource getLexicalResource() {
        return lexicalResource;
    }

    public void setLexicalResource(LexicalResource lexicalResource) {
        this.lexicalResource = lexicalResource;
    }

    public Grammar getGrammar() {
        return grammar;
    }

    public void setGrammar(Grammar grammar) {
        this.grammar = grammar;
    }

    @Override
    public String toString() {
        return "Evaluation{" +
                "lexicalResource=" + lexicalResource +
                ", grammar=" + grammar +
                '}';
    }
}
