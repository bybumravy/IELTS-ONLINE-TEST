package web.ielts.Test.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.List;

@Document(collection = "Speaking")
public class Speaking {
    @Id
    private String id;
    private String testId;
    private SpeakingPart part1;
    private SpeakingPart2 part2;
    private SpeakingPart part3;

    public Speaking() {
    }

    public Speaking(String id, String testId, SpeakingPart part1, SpeakingPart2 part2, SpeakingPart part3) {
        this.id = id;
        this.testId = testId;
        this.part1 = part1;
        this.part2 = part2;
        this.part3 = part3;
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

    public SpeakingPart getPart1() {
        return part1;
    }

    public void setPart1(SpeakingPart part1) {
        this.part1 = part1;
    }

    public SpeakingPart2 getPart2() {
        return part2;
    }

    public void setPart2(SpeakingPart2 part2) {
        this.part2 = part2;
    }

    public SpeakingPart getPart3() {
        return part3;
    }

    public void setPart3(SpeakingPart part3) {
        this.part3 = part3;
    }

    public static class SpeakingPart {
        private List<String> questions;

        public SpeakingPart() {
        }

        public SpeakingPart(List<String> questions) {
            this.questions = questions;
        }

        public List<String> getQuestions() {
            return questions;
        }

        public void setQuestions(List<String> questions) {
            this.questions = questions;
        }
    }

    public static class SpeakingPart2 {
        private String cueCard;
        private List<String> suggestions;

        public SpeakingPart2() {
        }

        public SpeakingPart2(String cueCard, List<String> suggestions) {
            this.cueCard = cueCard;
            this.suggestions = suggestions;
        }

        public String getCueCard() {
            return cueCard;
        }

        public void setCueCard(String cueCard) {
            this.cueCard = cueCard;
        }

        public List<String> getSuggestions() {
            return suggestions;
        }

        public void setSuggestions(List<String> suggestions) {
            this.suggestions = suggestions;
        }
    }
}