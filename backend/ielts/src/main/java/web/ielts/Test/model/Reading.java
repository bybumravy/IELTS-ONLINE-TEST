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

    public static class ReadingSection {
        private int sectionNumber;
        private List<String> questions;
        private List<String> answers;
        private List<String> explanations;
        private String method;

        public ReadingSection() {
        }

        public ReadingSection(int sectionNumber, List<String> questions, List<String> answers, List<String> explanations, String method) {
            this.sectionNumber = sectionNumber;
            this.questions = questions;
            this.answers = answers;
            this.explanations = explanations;
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

        public List<String> getAnswers() {
            return answers;
        }

        public void setAnswers(List<String> answers) {
            this.answers = answers;
        }

        public List<String> getExplanations() {
            return explanations;
        }

        public void setExplanations(List<String> explanations) {
            this.explanations = explanations;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }
    }
}
