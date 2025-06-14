package web.ielts.Test.repository.answer;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import web.ielts.Test.model.answer.writing.WritingAnswer;

@Repository
public interface WritingAnswerRepository extends MongoRepository<WritingAnswer, String> {
}