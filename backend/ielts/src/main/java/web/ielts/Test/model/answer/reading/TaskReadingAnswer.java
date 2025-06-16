package web.ielts.Test.model.answer.reading;

import java.util.List;

class TaskReadingAnswer {
    private int taskNumber;
    private String title;
    private String paragraph;
    private List<SectionReadingAnswer> sections;

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

    public List<SectionReadingAnswer> getSections() {
        return sections;
    }

    public void setSections(List<SectionReadingAnswer> sections) {
        this.sections = sections;
    }
}
