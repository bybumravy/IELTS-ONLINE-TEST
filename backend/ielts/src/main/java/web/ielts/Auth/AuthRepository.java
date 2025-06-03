package web.ielts.Auth;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

public class AuthRepository {
    @Repository
    public interface LoginRepository extends MongoRepository<User, String> {
        User findByEmail(String email);
    }
}
