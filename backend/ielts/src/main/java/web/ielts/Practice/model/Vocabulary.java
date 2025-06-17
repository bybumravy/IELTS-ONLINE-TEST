package web.ielts.Practice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "vocabularies")
public class Vocabulary {
    @Id
    private String id;
    private String word;
    private String translate;
    private String explanation;
    private Topic topic;
    private Band band;
    private List<ExampleSentence> exp;

    public Vocabulary() {}

    public Vocabulary(String word, String translate, String explanation, Topic topic, Band band, List<ExampleSentence> exp) {
        this.word = word;
        this.translate = translate;
        this.explanation = explanation;
        this.topic = topic;
        this.band = band;
        this.exp = exp;
    }

    // Getter & Setter

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }

    public String getTranslate() { return translate; }
    public void setTranslate(String translate) { this.translate = translate; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public Topic getTopic() { return topic; }
    public void setTopic(Topic topic) { this.topic = topic; }

    public Band getBand() { return band; }
    public void setBand(Band band) { this.band = band; }

    public List<ExampleSentence> getExp() { return exp; }
    public void setExp(List<ExampleSentence> exp) { this.exp = exp; }
}