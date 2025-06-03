package web.ielts.Auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {
 @Autowired
 private AuthService loginService;


    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@CookieValue(value = "jwt_token", required = false) String token) {
        if (token == null || !JwtToken.isValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Map<String, Object> data = JwtToken.decode(token);
        return ResponseEntity.ok(data);
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDTO loginRequest) {
        return loginService.login(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );
    }




}
