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

    public UserDTO createUser(UserDTO user) {
        // Check nếu email đã tồn tại -> báo lỗi
        if (adminRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }
        // Nếu chưa có thì insert
        user.setCreatedAt(LocalDateTime.now().toString());
        return adminRepository.save(user);
    }

    public UserDTO updateUser(String originalEmail, UserDTO user) {
        // 1. Lấy user theo originalEmail
        UserDTO existingUser = adminRepository.findByEmail(originalEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + originalEmail));

        // 2. Nếu user đổi sang email mới → kiểm tra email đó có bị user khác dùng chưa
        if (!existingUser.getEmail().equals(user.getEmail())) {
            boolean emailExists = adminRepository.existsByEmail(user.getEmail());
            if (emailExists) {
                throw new RuntimeException("Email is already used by another user: " + user.getEmail());
            }
        }
        adminRepository.deleteById(originalEmail);
        // 3. Cập nhật thông tin
        existingUser.setEmail(user.getEmail());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setPhone(user.getPhone());
        existingUser.setGender(user.getGender());
        existingUser.setCountry(user.getCountry());
        existingUser.setTimeZone(user.getTimeZone());
        existingUser.setCuurency(user.getCuurency());
        existingUser.setPremium(user.isPremium());
        existingUser.setBirthDate(user.getBirthDate());
        existingUser.setRole(user.getRole());

        // 4. Lưu lại
        return adminRepository.save(existingUser);
    }
    public void deleteUserByEmail(String email) {
        adminRepository.deleteByEmail(email);
    }
    public boolean existsByEmail(String email) {
        return adminRepository.existsByEmail(email);
    }
}
