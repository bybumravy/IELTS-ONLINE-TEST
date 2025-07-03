package web.ielts.Test.model.answer.speaking;

public class ScoreReview {
    private String scoreEva;
    private String reviewEva;

    public ScoreReview() {
    }

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
        return "ScoreReview{" +
                "scoreEva='" + scoreEva + '\'' +
                ", reviewEva='" + reviewEva + '\'' +
                '}';
    }
}
