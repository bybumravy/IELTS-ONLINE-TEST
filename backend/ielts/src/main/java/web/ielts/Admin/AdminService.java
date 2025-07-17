package web.ielts.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web.ielts.User.User;
import web.ielts.User.UserDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service

public class AdminService {
    @Autowired
    private AdminRepository adminRepository;
    public List<UserDTO> findByRole(String role) {
        return adminRepository.findByRole(role);
    }



    public void updateUser(String email, String role) {
        UserDTO userDTO = adminRepository.findByEmail(email);
        userDTO.setRole(role);
        // 4. Lưu lại
        adminRepository.save(userDTO);
    }

    public void deleteUserByEmail(String email) {
        adminRepository.deleteByEmail(email);
    }
    public boolean existsByEmail(String email) {
        return adminRepository.existsByEmail(email);
    }
}
