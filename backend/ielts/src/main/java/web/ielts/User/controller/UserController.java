package web.ielts.User.controller;


import com.sun.tools.jconsole.JConsoleContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.ielts.Auth.repository.AuthRepository;
import web.ielts.Auth.service.AuthService;
import web.ielts.User.User;
import web.ielts.User.UserDTO;
import web.ielts.User.repository.UserRepository;

import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private AuthService authService;

    // Lấy thông tin user theo username
    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String username) {
        Optional<User> userOpt = userRepository.findById(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            UserDTO dto = new UserDTO();
            dto.setUserName(user.getEmail());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setBirthDate(user.getBirthDate());
            dto.setGender(user.getGender());
            dto.setPhone(user.getPhone());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Update thông tin user
    @PutMapping("/{username}")
    public ResponseEntity<?> updateUser(@PathVariable String username, @RequestBody UserDTO updatedUserDto) {
        Optional<User> userOpt = userRepository.findById(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User existingUser = userOpt.get();

        // Cập nhật các field
        existingUser.setFirstName(updatedUserDto.getFirstName());
        existingUser.setLastName(updatedUserDto.getLastName());
        existingUser.setBirthDate(updatedUserDto.getBirthDate());
        existingUser.setGender(updatedUserDto.getGender());
        existingUser.setPhone(updatedUserDto.getPhone());

        userRepository.save(existingUser);

        // Trả về DTO
        UserDTO responseDto = new UserDTO();
        responseDto.setUserName(existingUser.getEmail());
        responseDto.setFirstName(existingUser.getFirstName());
        responseDto.setLastName(existingUser.getLastName());
        responseDto.setBirthDate(existingUser.getBirthDate());
        responseDto.setGender(existingUser.getGender());
        responseDto.setPhone(existingUser.getPhone());

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/upgrade-premium")
    public ResponseEntity<?> upgradePremium(@CookieValue(value = "jwt_token", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing token");
        }

        try {
            String username = authService.getUsernameFromToken(token);
            User user = authRepository.findByEmail(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            user.setPremium(true);
            authRepository.save(user);

            return ResponseEntity.ok("Premium status updated");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }


}
