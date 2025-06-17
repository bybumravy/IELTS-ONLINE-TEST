package web.ielts.Practice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import  web.ielts.Practice.model.*;
import java.util.List;

public interface VocabularyRepository extends MongoRepository<Vocabulary, String> {
    List<Vocabulary> findByTopic(Topic topic);
    List<Vocabulary> findByBand(Band band);
    List<Vocabulary> findByTopicAndBand(Topic topic, Band band);
}