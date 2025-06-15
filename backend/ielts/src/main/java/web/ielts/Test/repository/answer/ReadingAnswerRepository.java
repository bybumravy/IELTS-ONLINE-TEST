package web.ielts.Test.repository.answer;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import web.ielts.Test.model.answer.reading.ReadingAnswer;

@Repository
public interface ReadingAnswerRepository extends MongoRepository<ReadingAnswer, String> {
    // You can add custom queries here if needed
}