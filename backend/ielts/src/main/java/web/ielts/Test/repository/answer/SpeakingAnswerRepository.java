package web.ielts.Test.repository.answer;



import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import web.ielts.Test.model.answer.speaking.SpeakingAnswer;

@Repository
public interface SpeakingAnswerRepository extends MongoRepository<SpeakingAnswer, String> {
}