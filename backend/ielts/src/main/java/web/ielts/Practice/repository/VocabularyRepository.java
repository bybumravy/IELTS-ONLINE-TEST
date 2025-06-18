package web.ielts.Practice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import  web.ielts.Practice.model.*;
import java.util.List;

public interface VocabularyRepository extends MongoRepository<Vocabulary, String> {
    List<Vocabulary> findByTopic(String topic);
    List<Vocabulary> findByBand(String band);
    List<Vocabulary> findByTopicAndBand(String topic, String band);
}