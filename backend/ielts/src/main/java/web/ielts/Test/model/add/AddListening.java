package web.ielts.Test.model.add;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "RequestListening")
public class AddListening {
    @Id
    private String id;
    private String testId;
    private String audioUrl;
    private List<AddListeningTask> tasks;

    public AddListening() {}

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

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public List<AddListeningTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<AddListeningTask> tasks) {
        this.tasks = tasks;
    }
}

class AddListeningTask {
    private int taskNumber;
    private List<AddListeningSection> sections;

    public AddListeningTask() {}

    public int getTaskNumber() {
        return taskNumber;
    }

    public void setTaskNumber(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    public List<AddListeningSection> getSections() {
        return sections;
    }

    public void setSections(List<AddListeningSection> sections) {
        this.sections = sections;
    }

    // Getter và Setter
}

class AddListeningSection {
    private int sectionNumber;
    private String type;
    private String imageUrl;
    private String introduction;
    private List<AddListeningQuestion> questions;

    public AddListeningSection() {}

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

    public List<AddListeningQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<AddListeningQuestion> questions) {
        this.questions = questions;
    }
// Getter và Setter
}

class AddListeningQuestion {
    private int questionNumber;
    private String question;
    private String answer;
    private String explanation;
    private List<String> options;

    public AddListeningQuestion() {}

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

    // Getter và Setter
}
