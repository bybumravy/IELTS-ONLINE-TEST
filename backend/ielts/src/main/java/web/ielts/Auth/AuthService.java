package web.ielts.Auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
@Component
public class AuthService {
    @Autowired
    private AuthRepository.LoginRepository loginRepository;

    public ResponseEntity<Map<String, Object>> login(String email, String password) {
        Map<String, Object> response = new HashMap<>();

        // Tìm user theo email hoặc account
        System.err.println(email);
        System.err.println(password);
        User user = loginRepository.findByEmail(email);

        if (user != null && user.getPassword().equals(password)) {
            // Tạo JWT token

            String token = JwtToken.generateToken(user.getEmail(), user.getRole());

            // Tạo cookie lưu token
            ResponseCookie cookie = ResponseCookie.from("jwt_token", token)
                    .httpOnly(true)       // cookie chỉ cho HTTP, không cho JS truy cập
                    .secure(false)        // đặt true nếu dùng HTTPS
                    .path("/")            // cookie áp dụng cho toàn bộ site
                    .maxAge(24 * 60 * 60) // thời gian sống cookie 1 ngày (giây)
                    .sameSite("Lax")
                    .build();

            response.put("status", "success");
            response.put("message", "Login successful");


            // Trả về response kèm header Set-Cookie để trình duyệt lưu cookie
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(response);

        } else {
            response.put("status", "fail");
            response.put("message", "Invalid email/account or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

    }
}
