package web.ielts.Test.model.add;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "RequestSpeaking")
public class AddSpeaking {
    @Id
    private String id;
    private String testId;
    private AddSpeakingPart part1;
    private AddSpeakingPart2 part2;
    private AddSpeakingPart part3;

    public AddSpeaking() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public AddSpeakingPart getPart1() {
        return part1;
    }

    public void setPart1(AddSpeakingPart part1) {
        this.part1 = part1;
    }

    public AddSpeakingPart2 getPart2() {
        return part2;
    }

    public void setPart2(AddSpeakingPart2 part2) {
        this.part2 = part2;
    }

    public AddSpeakingPart getPart3() {
        return part3;
    }

    public void setPart3(AddSpeakingPart part3) {
        this.part3 = part3;
    }
}

class AddSpeakingPart {
    private int partNumber;
    private String title;
    private List<AddSpeakingQuestion> questions;

    public AddSpeakingPart() {}

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

    public List<AddSpeakingQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<AddSpeakingQuestion> questions) {
        this.questions = questions;
    }
}

class AddSpeakingPart2 {
    private int partNumber;
    private String title;
    private String question;
    private List<String> cueCards;

    public AddSpeakingPart2() {}

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
}

class AddSpeakingQuestion {
    private int questionNumber;
    private String question;

    public AddSpeakingQuestion() {}

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
