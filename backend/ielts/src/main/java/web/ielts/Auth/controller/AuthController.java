package web.ielts.Auth.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import jakarta.servlet.http.HttpServletRequest;
import web.ielts.Auth.dto.AuthDTO;
import web.ielts.Auth.service.AuthService;
import web.ielts.User.User;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthService authservice;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User newUser) {

        return authservice.register(newUser);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {


        return authservice.verifyEmail(token);
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDTO loginRequest) {
        System.out.println("From page: " + loginRequest.getFromPath());  // in ra /loginadmin
        return authservice.login(
                loginRequest.getEmail(),
                loginRequest.getPassword()
                ,loginRequest.getFromPath()
        );
    }

    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo(@CookieValue(value = "jwt_token", required = false) String token) {
        try {
            String username = authservice.getUsernameFromToken(token);
            String role = authservice.getRoleFromToken(token);
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
    List<ResponseCookie> cookies = authservice.logout(request);

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
