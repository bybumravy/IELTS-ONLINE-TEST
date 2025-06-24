
package web.ielts.Auth.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import web.ielts.Auth.JwtToken;
import web.ielts.Auth.model.VerificationToken;
import web.ielts.Auth.repository.VerificationTokenRepository;
import web.ielts.Auth.repository.AuthRepository;
import web.ielts.Config.EmailConfig;
import web.ielts.User.User;

@Component
public class AuthService {

    @Autowired
    private AuthRepository authRepository;
    @Autowired
    private VerificationTokenRepository tokenRepository;
    @Autowired
    private EmailConfig emailConfig;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    @Value("${jwt.secret}")
    private final String jwtSecret = "J4gKu2KJ3Z5vP8t5NmE+lw6aD3vJ6GpN1kILUBo=";

    // Đăng ký tài khoản mới và gửi email xác thực
    public ResponseEntity<?> register(User newUser) {
        if (authRepository.findByEmail(newUser.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email đã được đăng ký");
        };
        // Tạo token xác thực
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(
                token,
                newUser.getEmail(),
                newUser.getPassword(),
                LocalDateTime.now().plusHours(24)
                ,"student"
        );

        tokenRepository.save(verificationToken);


        emailConfig.sendVerificationEmail(newUser.getEmail(), token);

        return ResponseEntity.ok("Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản.");
    }

    public ResponseEntity<?> verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token);
        System.out.println(verificationToken.toString());
        if (verificationToken == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token không hợp lệ");
        }

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token đã hết hạn");
        }

        User user = new User(verificationToken.getUserEmail(),verificationToken.getPassword(),verificationToken.getRole());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không tìm thấy tài khoản");
        }
        user.setPassword(encoder.encode(user.getPassword()));

        authRepository.save(user);
        String tokenJwt = JwtToken.generateToken(user.getEmail(), user.getRole());

        ResponseCookie cookie = ResponseCookie.from("jwt_token", tokenJwt)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("Lax")
                .build();



        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Xác thực email thành công! Bạn có thể đăng nhập.");
    }

    public ResponseEntity<Map<String, Object>> login(String email, String password) {
        Map<String, Object> response = new HashMap<>();

        User user = authRepository.findByEmail(email);

        System.out.println(user);
        if (user != null && encoder.matches(password, user.getPassword())) {
            String token = JwtToken.generateToken(user.getEmail(), user.getRole());

            ResponseCookie cookie = ResponseCookie.from("jwt_token", token)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(24 * 60 * 60)
                    .sameSite("Lax")
                    .build();

            response.put("status", "success");
            response.put("message", "Login successful");
            System.err.println(response);
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(response);
        } else {
            response.put("status", "fail");
            response.put("message", "Invalid email/account or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    public String getUsernameFromToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Missing token");
        }

        return JwtToken.extractUsername(token);
    }
    public String getRoleFromToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Missing token");
        }

        return JwtToken.extractRole(token);
    }

   public List<ResponseCookie> logout(HttpServletRequest request) {
    // Xoá session
   

    // Xoá jwt_token
    ResponseCookie jwtCookie = ResponseCookie.from("jwt_token", "")
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(0)
            .sameSite("Lax")
            .build();

    // Xoá JSESSIONID
    ResponseCookie jsessionidCookie = ResponseCookie.from("JSESSIONID", "")
            .path("/")
            .maxAge(0)
            .build();

    return List.of(jwtCookie, jsessionidCookie);
}
}

