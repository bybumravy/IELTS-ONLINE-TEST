package web.ielts.Test.repository.answer;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import web.ielts.Test.model.answer.listening.ListeningAnswer;

@Repository
public interface ListeningAnswerRepository extends MongoRepository<ListeningAnswer, String> {
}
