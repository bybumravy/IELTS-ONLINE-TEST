package web.ielts.Test.model.answer.speaking;

public class LexicalResource {
    private String scoreEva;
    private String reviewEva;

    public LexicalResource(String scoreEva, String reviewEva) {
        this.scoreEva = scoreEva;
        this.reviewEva = reviewEva;
    }

    public LexicalResource() {
    }

    // Getters and setters
    public String getScoreEva() {
        return scoreEva;
    }

    public void setScoreEva(String scoreEva) {
        this.scoreEva = scoreEva;
    }

    public String getReviewEva() {
        return reviewEva;
    }

    public void setReviewEva(String reviewEva) {
        this.reviewEva = reviewEva;
    }

    @Override
    public String toString() {
        return "LexicalResource{" +
                "scoreEva='" + scoreEva + '\'' +
                ", reviewEva='" + reviewEva + '\'' +
                '}';
    }
}
