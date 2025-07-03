package web.ielts.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.*;
import web.ielts.Auth.repository.AuthRepository;
import web.ielts.Auth.service.AuthService;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;

    @PostMapping("/upgrade-premium")
    public String upgradePremium(@AuthenticationPrincipal User user) {
        userService.upgradeToPremium(user.getEmail());
        return "You have successfully upgraded to Premium.";
    }

    // ✅ Lấy thông tin người dùng và tự reset premium nếu hết hạn
    @GetMapping("/me")
    public User getCurrentUser(@AuthenticationPrincipal User user) {
        return userService.resetPremiumIfExpired(user);
    }
}
