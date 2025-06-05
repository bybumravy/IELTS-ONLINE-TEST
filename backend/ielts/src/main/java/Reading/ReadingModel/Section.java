package Reading.ReadingModel;

import java.util.List;

public class Section {
    private int sectionNumber;
    private List<String> questions;
    private List<String> answer;
    private List<String> explain;
    private String method;
    public Section(int sectionNumber, List<String> questions, List<String> answer, List<String> explain,
            String method) {
        this.sectionNumber = sectionNumber;
        this.questions = questions;
        this.answer = answer;
        this.explain = explain;
        this.method = method;
    }
    public int getSectionNumber() {
        return sectionNumber;
    }
    public void setSectionNumber(int sectionNumber) {
        this.sectionNumber = sectionNumber;
    }
    public List<String> getQuestions() {
        return questions;
    }
    public void setQuestions(List<String> questions) {
        this.questions = questions;
    }
    public List<String> getAnswer() {
        return answer;
    }
    public void setAnswer(List<String> answer) {
        this.answer = answer;
    }
    public List<String> getExplain() {
        return explain;
    }
    public void setExplain(List<String> explain) {
        this.explain = explain;
    }
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    @Override
    public String toString() {
        return "Section [sectionNumber=" + sectionNumber + ", questions=" + questions + ", answer=" + answer
                + ", explain=" + explain + ", method=" + method + ", getSectionNumber()=" + getSectionNumber()
                + ", getQuestions()=" + getQuestions() + ", getAnswer()=" + getAnswer() + ", getExplain()="
                + getExplain() + ", getMethod()=" + getMethod() + ", getClass()=" + getClass() + ", hashCode()="
                + hashCode() + ", toString()=" + super.toString() + "]";
    }
    
}
