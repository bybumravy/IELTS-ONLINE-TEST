package web.ielts.Auth;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthService loginService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDTO loginRequest) {
        System.out.println("hello");
        return loginService.login(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );
    }

    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo(@CookieValue(value = "jwt_token", required = false) String token) {
        try {
            String username = loginService.getUsernameFromToken(token);
            String role = loginService.getRoleFromToken(token);
            return ResponseEntity.ok(Map.of(
                    "username", username,
                    "role", role
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing token");
        }
    }
    @PostMapping("/logout")
public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
    List<ResponseCookie> cookies = loginService.logout(request);

    HttpHeaders headers = new HttpHeaders();
    for (ResponseCookie cookie : cookies) {
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    Map<String, Object> response = new HashMap<>();
    response.put("status", "success");
    response.put("message", "Logged out");

    return ResponseEntity.ok()
            .headers(headers)
            .body(response);
}
}
