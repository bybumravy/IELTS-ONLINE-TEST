package web.ielts.Test.model.answer;

import java.util.List;

public class SectionAnswer {
    private int sectionNumber;
    private String type;
    private String introduction;
    //@JsonProperty("questions")
    private List<QuestionAnswer> questions;

    public SectionAnswer(int sectionNumber, String type, String introduction, List<QuestionAnswer> questions) {
        this.sectionNumber = sectionNumber;
        this.type = type;
        this.introduction = introduction;
        this.questions = questions;
    }

    public int getSectionNumber() {
        return sectionNumber;
    }

    public void setSectionNumber(int sectionNumber) {
        this.sectionNumber = sectionNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public List<QuestionAnswer> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionAnswer> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        return "SectionAnswerStudent{" +
                "sectionNumber=" + sectionNumber +
                ", type='" + type + '\'' +
                ", introduction='" + introduction + '\'' +
                ", questions=" + questions +
                '}';
    }
}