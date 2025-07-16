package web.ielts.Test.model.answer.speaking;


import java.util.List;

public class SpeakingAnswerPart2 {
    private int partNumber;
    private String title;
    private String question;
    private EvaluationResult evaluationResults;


    private double averageScore;
    public SpeakingAnswerPart2() {
    }

    private String instruction;
    private List<String> cueCards;
    private String studentAnswer;

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getStudentAnswer() {
        return studentAnswer;
    }

    public EvaluationResult getEvaluationResults() {
        return evaluationResults;
    }

    public void setEvaluationResults(EvaluationResult evaluationResults) {
        this.evaluationResults = evaluationResults;
    }

    public void setStudentAnswer(String studentAnswer) {
        this.studentAnswer = studentAnswer;
    }

    public SpeakingAnswerPart2(int partNumber, String title, String question, String instruction, List<String> cueCards, String studentAnswer) {
        this.partNumber = partNumber;
        this.title = title;
        this.question = question;
        this.instruction = instruction;
        this.cueCards = cueCards;
        this.studentAnswer = studentAnswer;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public int getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getCueCards() {
        return cueCards;
    }

    public void setCueCards(List<String> cueCards) {
        this.cueCards = cueCards;
    }

    @Override
    public String toString() {
        return "SpeakingAnswerPart2{" +
                "partNumber=" + partNumber +
                ", title='" + title + '\'' +
                ", question='" + question + '\'' +
                ", evaluationResults=" + evaluationResults +
                ", averageScore=" + averageScore +
                ", instruction='" + instruction + '\'' +
                ", cueCards=" + cueCards +
                ", studentAnswer='" + studentAnswer + '\'' +
                '}';
    }
}