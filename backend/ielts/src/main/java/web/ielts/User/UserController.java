package web.ielts.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import web.ielts.Auth.repository.AuthRepository;
import web.ielts.Auth.service.AuthService;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private AuthService authService;

    @PostMapping("/upgrade-premium")
    public ResponseEntity<?> upgradePremium(@CookieValue(value = "jwt_token", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing token");
        }

        try {
            String email = authService.getUsernameFromToken(token);
            User user = authRepository.findByEmail(email);
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
