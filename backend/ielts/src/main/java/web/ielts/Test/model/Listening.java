package web.ielts.Test.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "Listening")
public class Listening {
    @Id
    private String id;
    private String testId;
    private List<ListeningSection> sections;

    public Listening(String id, String testId, List<ListeningSection> sections) {
        this.id = id;
        this.testId = testId;
        this.sections = sections;
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

    public List<ListeningSection> getSections() {
        return sections;
    }

    public void setSections(List<ListeningSection> sections) {
        this.sections = sections;
    }

    public static class ListeningSection {
        private int sectionNumber;
        private String audioUrl;
        private String imageUrl;
        private String method;
        private List<String> questions;
        private List<String> answers;

        public ListeningSection() {
        }

        public ListeningSection(int sectionNumber, String audioUrl, String imageUrl, String method, List<String> questions, List<String> answers) {
            this.sectionNumber = sectionNumber;
            this.audioUrl = audioUrl;
            this.imageUrl = imageUrl;
            this.method = method;
            this.questions = questions;
            this.answers = answers;
        }

        public int getSectionNumber() {
            return sectionNumber;
        }

        public void setSectionNumber(int sectionNumber) {
            this.sectionNumber = sectionNumber;
        }

        public String getAudioUrl() {
            return audioUrl;
        }

        public void setAudioUrl(String audioUrl) {
            this.audioUrl = audioUrl;
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