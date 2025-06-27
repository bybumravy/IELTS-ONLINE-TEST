package web.ielts.User.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import web.ielts.User.User;

public interface UserRepository extends MongoRepository<User, String> {
}
