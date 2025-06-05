package web.ielts.Auth;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

<<<<<<< HEAD
@Repository
public interface AuthRepository extends MongoRepository<User, String> {
    User findByEmail(String email);
}
=======
import web.ielts.User.User;

@Repository
public interface AuthRepository extends MongoRepository<User, String> {
    User findByEmail(String email);
}
>>>>>>> 1c618f771c725eff356d15eb2edf462ef54426f6
