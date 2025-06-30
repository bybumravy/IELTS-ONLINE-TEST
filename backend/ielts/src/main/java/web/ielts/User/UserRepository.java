package web.ielts.User;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    // Tìm user theo email (email là @Id trong class User)
    Optional<User> findByEmail(String email);

}
