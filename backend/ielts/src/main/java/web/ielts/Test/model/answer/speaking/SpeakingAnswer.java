package web.ielts.Test.model.answer.speaking;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "SpeakingAnswer")
public class SpeakingAnswer {
    public SpeakingAnswer() {
    }

    @Id
    private String id;
    private String testId;
    private String name;
    private SpeakingAnswerPart13 part1;
    private SpeakingAnswerPart2 part2;
    private SpeakingAnswerPart13 part3;

    public SpeakingAnswer(String testId) {
        this.testId = testId;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SpeakingAnswerPart13 getPart1() {
        return part1;
    }

    public void setPart1(SpeakingAnswerPart13 part1) {
        this.part1 = part1;
    }

    public SpeakingAnswerPart2 getPart2() {
        return part2;
    }

    public void setPart2(SpeakingAnswerPart2 part2) {
        this.part2 = part2;
    }

    public SpeakingAnswerPart13 getPart3() {
        return part3;
    }

    public void setPart3(SpeakingAnswerPart13 part3) {
        this.part3 = part3;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SpeakingAnswer(String id, String testId, String name, SpeakingAnswerPart13 part1, SpeakingAnswerPart2 part2, SpeakingAnswerPart13 part3) {
        this.id = id;
        this.testId = testId;
        this.name = name;
        this.part1 = part1;
        this.part2 = part2;
        this.part3 = part3;
    }

}