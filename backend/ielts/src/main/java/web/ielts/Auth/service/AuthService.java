
package web.ielts.Auth.service;

import java.time.LocalDateTime;
import java.util.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
import web.ielts.Config.EmailForgetPasswordConfig;
import web.ielts.User.User;

import static web.ielts.Auth.JwtToken.*;

@Component
public class AuthService {

    @Autowired
    private AuthRepository authRepository;
    @Autowired
    private VerificationTokenRepository tokenRepository;
    @Autowired
    private EmailConfig emailConfig;
    @Autowired
    private EmailForgetPasswordConfig emailForgetPasswordConfig;
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
    public ResponseEntity<?> forgotpassword(String email,String redirectUrl) {
        User user = authRepository.findByEmail(email);
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(
                token,
                user.getEmail(),
                user.getPassword(),
                LocalDateTime.now().plusHours(24)
                ,null
        );// giả định tìm theo token hoặc email
        tokenRepository.save(verificationToken);

                // Token hợp lệ
                // Thực hiện reset password hoặc gửi email xác nhận
                emailForgetPasswordConfig.sendResetPasswordEmail(verificationToken.getUserEmail(), token,redirectUrl);

                return ResponseEntity.ok("Gửi email thành công, vui lòng kiểm tra email.");
            }
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        // 1. Lấy refresh token từ cookie
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing refresh token");
        }

        // 2. Kiểm tra hạn token (nếu cần)
        if (JwtToken.isTokenExpired(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expired");
        }

        try {
            // 3. Lấy dữ liệu từ refresh token
            String email = JwtToken.extractUsername(refreshToken);
            String role = JwtToken.extractRole(refreshToken);
            boolean isPremium = JwtToken.extractIsPremium(refreshToken);

            // 4. Tạo lại token mới
            String newAccessToken = JwtToken.generateAccessToken(email, role, isPremium);
            String newRefreshToken = JwtToken.generateRefreshToken(email, role, isPremium);

            // 5. Tạo cookie mới
            ResponseCookie accessCookie = createJwtCookie(email, role, isPremium);
            ResponseCookie refreshCookie = createRefreshTokenCookie(email, role, isPremium);

            // 6. Trả về response với cookie
            response.setHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

            return ResponseEntity.ok("Access token refreshed");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
    }




        public ResponseEntity<?> resetPassword(String token, String newPassword,String redirectURL) {
        VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token không hợp lệ");
        }

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token đã hết hạn");
        }

        // Thêm check password mới


        User user = authRepository.findByEmail(verificationToken.getUserEmail());
        if(user == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User không tồn tại");
        }

        user.setPassword(encoder.encode(newPassword));
        authRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Đặt lại mật khẩu thành công");


        return ResponseEntity.ok(response);
    }
    public ResponseCookie createJwtCookie(String email, String role,boolean isPremium) {
        String tokenJwt = generateAccessToken(email, role,isPremium);

        return ResponseCookie.from("jwt_token", tokenJwt)
                .httpOnly(true)
                .secure(false) // lên production thì đổi thành true (nếu có https)
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("Lax")
                .build();
    }
    public ResponseCookie createRefreshTokenCookie(String email, String role,boolean isPremium) {
        String refreshToken = generateRefreshToken(email, role,isPremium);
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7 ngày
                .sameSite("Strict")
                .build();
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

        User user = new User(
                verificationToken.getUserEmail(),
                verificationToken.getPassword(),
                List.of(verificationToken.getRole()) // tạo list chứa 1 phần tử role
        );
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không tìm thấy tài khoản");
        }
        user.setPassword(encoder.encode(user.getPassword()));

        authRepository.save(user);
        ResponseCookie cookie = createJwtCookie(user.getEmail(),"student",user.isPremium());
        ResponseCookie refreshTokenCookie = createRefreshTokenCookie(user.getEmail(),"student",user.isPremium());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body("Xác thực email thành công! Bạn có thể đăng nhập.");
    }

    public ResponseEntity<Map<String, Object>> login(String email, String password, String path) {
        Map<String, Object> response = new HashMap<>();

        User user = authRepository.findByEmail(email);
        if (user != null && encoder.matches(password, user.getPassword())) {
            List<String> roles = user.getRole(); // ["student", "teacher"]
            String selectedRole = null;
            // ✅ Kiểm tra quyền dựa trên path
            if (path.equals("/login") && roles.contains("student")) {
                selectedRole = "student";
            } else if (path.equals("/staff-login") && roles.contains("teacher")) {
                selectedRole = "teacher";
            } else if (path.equals("/manager-login") && roles.contains("manager")) {
                selectedRole = "manager";
            } else if (path.equals("/login-admin") && roles.contains("admin")) {
                selectedRole = "admin";
            }
            else {
                response.put("status", "fail");
                response.put("message", "You do not have the required role to log in here");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // ✅ Chỉ truyền role phù hợp với path vào token
            ResponseCookie accessTokenCookie = createJwtCookie(user.getEmail(), selectedRole, user.isPremium());
            ResponseCookie refreshTokenCookie = createRefreshTokenCookie(user.getEmail(), selectedRole, user.isPremium());

            response.put("status", "success");
            response.put("message", "Login successful");

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
            headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(response);
        } else {
            response.put("status", "fail");
            response.put("message", "Invalid email or password");
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
    public boolean isPremium(String token) {
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Missing token");
        }

        return JwtToken.extractIsPremium(token);
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
       ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", "")
               .httpOnly(true)
               .secure(false)
               .path("/")
               .maxAge(0)
               .sameSite("Strict")
               .build();
    // Xoá JSESSIONID
    ResponseCookie jsessionidCookie = ResponseCookie.from("JSESSIONID", "")
            .path("/")
            .maxAge(0)
            .build();

    return List.of(jwtCookie, jsessionidCookie,refreshTokenCookie);
}




    public User getUserByEmail(String email) {
        return authRepository.findByEmail(email);
    }



}
