package web.ielts.Test.model.answer;

import java.util.List;

class Task {
    private int taskNumber;
    private String title;
    private String paragraph;
    private List<SectionAnswer> sections;

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

    public String getParagraph() {
        return paragraph;
    }

    public void setParagraph(String paragraph) {
        this.paragraph = paragraph;
    }

    public List<SectionAnswer> getSections() {
        return sections;
    }

    public void setSections(List<SectionAnswer> sections) {
        this.sections = sections;
    }
}
