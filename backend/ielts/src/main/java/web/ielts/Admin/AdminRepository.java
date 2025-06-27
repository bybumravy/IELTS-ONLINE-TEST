package web.ielts.Admin;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import web.ielts.User.UserDTO;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends MongoRepository<UserDTO, String> {
    List<UserDTO> findByRole(String role);
    boolean existsByEmail(String email);

    Optional<UserDTO> findByEmail(String email);
    void deleteByEmail(String email);
}