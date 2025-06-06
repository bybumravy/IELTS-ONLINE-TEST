package web.ielts.Test.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import web.ielts.Test.model.Writing;

public interface WritingTestRepository extends MongoRepository<Writing, String> {
    Writing findByTestId(String testId);
}
