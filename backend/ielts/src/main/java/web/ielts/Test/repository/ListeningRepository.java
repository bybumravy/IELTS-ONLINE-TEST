package web.ielts.Test.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import web.ielts.Test.model.Listening;

public interface ListeningRepository extends MongoRepository<Listening, String> {
}