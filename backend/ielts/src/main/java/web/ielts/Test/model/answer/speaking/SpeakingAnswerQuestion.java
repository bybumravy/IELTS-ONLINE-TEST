package web.ielts.Test.model.answer.speaking;


import java.util.List;

public class SpeakingAnswerQuestion {
    private String question;
    private String studentAnswer;
    private EvaluationResult evaluationResults;

    public SpeakingAnswerQuestion(String question, String studentAnswer) {
        this.question = question;
        this.studentAnswer = studentAnswer;
    }

    public EvaluationResult getEvaluationResults() {
        return evaluationResults;
    }

    public void setEvaluationResults(EvaluationResult evaluationResults) {
        this.evaluationResults = evaluationResults;
    }

    public String getQuestion() {
        return question;
    }

    public SpeakingAnswerQuestion() {

    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getStudentAnswer() {
        return studentAnswer;
    }

    public void setStudentAnswer(String studentAnswer) {
        this.studentAnswer = studentAnswer;
    }

    @Override
    public String toString() {
        return "SpeakingAnswerQuestion{" +
                "question='" + question + '\'' +
                ", studentAnswer='" + studentAnswer + '\'' +
                ", evaluationResults=" + evaluationResults +
                '}';
    }
}