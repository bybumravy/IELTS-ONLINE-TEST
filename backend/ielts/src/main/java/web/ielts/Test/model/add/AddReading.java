package web.ielts.Test.model.add;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "RequestReading")
public class AddReading {
    @Id
    private String id;
    private String testId;
    private List<AddReadingTask> tasks;

    public AddReading() {}

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

    public List<AddReadingTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<AddReadingTask> tasks) {
        this.tasks = tasks;
    }
}

class AddReadingTask {
    private int taskNumber;
    private String paragraph;
    private List<AddReadingSection> sections;

    public AddReadingTask() {}

    public int getTaskNumber() {
        return taskNumber;
    }

    public void setTaskNumber(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    public String getParagraph() {
        return paragraph;
    }

    public void setParagraph(String paragraph) {
        this.paragraph = paragraph;
    }

    public List<AddReadingSection> getSections() {
        return sections;
    }

    public void setSections(List<AddReadingSection> sections) {
        this.sections = sections;
    }
}

class AddReadingSection {
    private int sectionNumber;
    private String type;
    private String imageUrl;
    private String introduction;
    private List<AddReadingQuestion> questions;

    public AddReadingSection() {}

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public List<AddReadingQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<AddReadingQuestion> questions) {
        this.questions = questions;
    }
}

class AddReadingQuestion {
    private int questionNumber;
    private String question;
    private String answer;
    private String explanation;
    private List<String> options;

    public AddReadingQuestion() {}

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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}
