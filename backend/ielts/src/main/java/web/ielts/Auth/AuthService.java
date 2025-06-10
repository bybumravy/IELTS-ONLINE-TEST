
package web.ielts.Auth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import web.ielts.User.User;

@Component
public class AuthService {

    @Autowired
    private AuthRepository loginRepository;  // inject interface LoginRepository

    public ResponseEntity<Map<String, Object>> login(String email, String password) {
        Map<String, Object> response = new HashMap<>();

        User user = loginRepository.findByEmail(email);

        System.out.println(user);
        if (user != null && user.getPassword().equals(password)) {
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

