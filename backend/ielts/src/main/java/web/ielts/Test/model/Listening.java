package web.ielts.Test.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "Listening")
public class Listening {
    @Id
    private String id;
    private String testId;
    private List<ListeningTask> task;

    public Listening(String id, String testId, List<ListeningTask> task) {
        this.id = id;
        this.testId = testId;
        this.task = task;
    }

    public Listening() {
    }

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

    public List<ListeningTask> getSections() {
        return task;
    }

    public void setSections(List<ListeningTask> sections) {
        this.task = task;
    }

public static class ListeningTask{
        private int taskNumber;
        private String audioUrl;
        private List<ListeningSection> sections;

    public ListeningTask(int taskNumber, String audioUrl, List<ListeningSection> sections) {
        this.taskNumber = taskNumber;
        this.audioUrl = audioUrl;
        this.sections = sections;
    }

    public int getTaskNumber() {
        return taskNumber;
    }

    public void setTaskNumber(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public List<ListeningSection> getSections() {
        return sections;
    }

    public void setSections(List<ListeningSection> sections) {
        this.sections = sections;
    }

    public ListeningTask() {
    }

}
    public static class Question{
        private String question;
        private String answer;
        private String explanation;
        private List<String> options;


        public Question() {
        }

        public Question(String question, String answer, String explanation,List<String> options) {
            this.question = question;
            this.options = options;
            this.answer = answer;
            this.explanation = explanation;
        }

        public List<String> getOptions() {
            return options;
        }

        public void setOptions(List<String> options) {
            this.options = options;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }

    public static class ListeningSection {
        private int sectionNumber;
        private String imageUrl;
        private String method;
        private String introduction;
        private List<String> questions;
        private List<String> answers;

        public ListeningSection() {
        }

        public ListeningSection(int sectionNumber, String imageUrl, String method, List<String> questions, List<String> answers,String introduction) {
            this.sectionNumber = sectionNumber;
            this.imageUrl = imageUrl;
            this.method = method;
            this.questions = questions;
            this.answers = answers;
            this.introduction = introduction;
        }

        public String getIntroduction() {
            return introduction;
        }

        public void setIntroduction(String introduction) {
            this.introduction = introduction;
        }

        public int getSectionNumber() {
            return sectionNumber;
        }

        public void setSectionNumber(int sectionNumber) {
            this.sectionNumber = sectionNumber;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public List<String> getQuestions() {
            return questions;
        }

        public void setQuestions(List<String> questions) {
            this.questions = questions;
        }

        public List<String> getAnswers() {
            return answers;
        }

        public void setAnswers(List<String> answers) {
            this.answers = answers;
        }
    }
}