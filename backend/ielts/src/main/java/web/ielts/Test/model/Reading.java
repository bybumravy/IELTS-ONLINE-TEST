package web.ielts.Test.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "Reading")
public class Reading {
    @Id
    private String id;
    private String testId;
    private List<ReadingTask> tasks;

    public Reading() {
    }

    public Reading(String id, String testId, List<ReadingTask> tasks) {
        this.id = id;
        this.testId = testId;
        this.tasks = tasks;
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

    public List<ReadingTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<ReadingTask> tasks) {
        this.tasks = tasks;
    }

    public static class ReadingTask {
        private int taskNumber;
        private String title;
        private String graph;
        private List<ReadingSection> sections;

        public ReadingTask() {
        }

        public ReadingTask(int taskNumber, String title, String graph, List<ReadingSection> sections) {
            this.taskNumber = taskNumber;
            this.title = title;
            this.graph = graph;
            this.sections = sections;
        }

        public int getTaskNumber() {
            return taskNumber;
        }

        public void setTaskNumber(int taskNumber) {
            this.taskNumber = taskNumber;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getGraph() {
            return graph;
        }

        public void setGraph(String graph) {
            this.graph = graph;
        }

        public List<ReadingSection> getSections() {
            return sections;
        }

        public void setSections(List<ReadingSection> sections) {
            this.sections = sections;
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

        public String getQuestion() {
            return question;
        }

        public List<String> getOptions() {
            return options;
        }

        public void setOptions(List<String> options) {
            this.options = options;
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

    public static class ReadingSection {
        private int sectionNumber;
        private String introduction;
        private List<Question> questions;
        private String method;

        public ReadingSection() {
        }

        public ReadingSection(int sectionNumber, String method, List<Question> questions, String introduction) {
            this.sectionNumber = sectionNumber;
            this.method = method;
            this.introduction = introduction;
            this.questions = questions;
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

        public List<Question> getQuestions() {
            return questions;
        }

        public void setQuestions(List<Question> questions) {
            this.questions = questions;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }
    }
}