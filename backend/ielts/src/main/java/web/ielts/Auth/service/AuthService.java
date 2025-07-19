//
//package web.ielts.Auth.service;
//
//import java.time.LocalDateTime;
//import java.util.*;
//
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseCookie;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import jakarta.servlet.http.HttpServletRequest;
//import web.ielts.Auth.JwtToken;
//import web.ielts.Auth.model.VerificationToken;
//import web.ielts.Auth.repository.VerificationTokenRepository;
//import web.ielts.Auth.repository.AuthRepository;
//import web.ielts.Config.EmailConfig;
//import web.ielts.Config.EmailForgetPasswordConfig;
//import web.ielts.User.User;
//
//import static web.ielts.Auth.JwtToken.*;
//
//@Component
//public class AuthService {
//
//    @Autowired
//    private AuthRepository authRepository;
//    @Autowired
//    private VerificationTokenRepository tokenRepository;
//    @Autowired
//    private EmailConfig emailConfig;
//    @Autowired
//    private EmailForgetPasswordConfig emailForgetPasswordConfig;
//    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
//    @Value("${jwt.secret}")
//    private final String jwtSecret = "J4gKu2KJ3Z5vP8t5NmE+lw6aD3vJ6GpN1kILUBo=";
//
//    // Đăng ký tài khoản mới và gửi email xác thực
//    public ResponseEntity<?> register(User newUser) {
//        if (authRepository.findByEmail(newUser.getEmail()) != null) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email đã được đăng ký");
//        };
//        // Tạo token xác thực
//        String token = UUID.randomUUID().toString();
//        VerificationToken verificationToken = new VerificationToken(
//                token,
//                newUser.getEmail(),
//                newUser.getPassword(),
//                LocalDateTime.now().plusHours(24)
//                ,"student"
//        );
//
//        tokenRepository.save(verificationToken);
//
//
//        emailConfig.sendVerificationEmail(newUser.getEmail(), token);
//
//        return ResponseEntity.ok("Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản.");
//    }
//    public ResponseEntity<?> forgotpassword(String email) {
//        User user = authRepository.findByEmail(email);
//        String token = UUID.randomUUID().toString();
//        VerificationToken verificationToken = new VerificationToken(
//                token,
//                user.getEmail(),
//                user.getPassword(),
//                LocalDateTime.now().plusHours(24)
//                ,"student"
//        );// giả định tìm theo token hoặc email
//        tokenRepository.save(verificationToken);
//
//                // Token hợp lệ
//                // Thực hiện reset password hoặc gửi email xác nhận
//                emailForgetPasswordConfig.sendResetPasswordEmail(verificationToken.getUserEmail(), token);
//
//                return ResponseEntity.ok("Gửi email thành công, vui lòng kiểm tra email.");
//            }
//    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
//        Cookie[] cookies = request.getCookies();
//        if (cookies == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token missing");
//        }
//
//        String refreshToken = null;
//        for (Cookie cookie : cookies) {
//            if ("refreshToken".equals(cookie.getName())) {
//                refreshToken = cookie.getValue();
//                break;
//            }
//        }
//
//        if (refreshToken == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token missing");
//        }
//
//        try {
//            // Kiểm tra token
//            String email = getUsernameFromToken(refreshToken);
//            User user = authRepository.findByEmail(email);
//
//            if (user == null) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
//            }
//            if (isTokenExpired(refreshToken)) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expired");
//            }
//
//            // Tạo access token và refresh token mới
//            ResponseCookie cookie = createJwtCookie(user.getEmail(), user.getRole(),user.isPremium());
//            ResponseCookie refreshTokenCookie = createRefreshTokenCookie(user.getEmail(), user.getRole(),user.isPremium());
//
//            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
//            response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
//
//            return ResponseEntity.ok(Map.of("status", "success"));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
//        }
//    }
//
//
//    public ResponseEntity<?> resetPassword(String token, String newPassword) {
//        VerificationToken verificationToken = tokenRepository.findByToken(token);
//        if (verificationToken == null) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token không hợp lệ");
//        }
//
//        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token đã hết hạn");
//        }
//
//        // Thêm check password mới
//
//
//        User user = authRepository.findByEmail(verificationToken.getUserEmail());
//        if(user == null){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User không tồn tại");
//        }
//
//        user.setPassword(encoder.encode(newPassword));
//        authRepository.save(user);
//
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "Đặt lại mật khẩu thành công");
//        response.put("role", user.getRole().toLowerCase());
//
//        return ResponseEntity.ok(response);
//    }
//    public ResponseCookie createJwtCookie(String email, String role,boolean isPremium) {
//        String tokenJwt = generateAccessToken(email, role,isPremium);
//
//        return ResponseCookie.from("jwt_token", tokenJwt)
//                .httpOnly(true)
//                .secure(false) // lên production thì đổi thành true (nếu có https)
//                .path("/")
//                .maxAge(24 * 60 * 60)
//                .sameSite("Lax")
//                .build();
//    }
//    public ResponseCookie createRefreshTokenCookie(String email, String role,boolean isPremium) {
//        String refreshToken = generateRefreshToken(email, role,isPremium);
//        return ResponseCookie.from("refreshToken", refreshToken)
//                .httpOnly(true)
//                .secure(false)
//                .path("/")
//                .maxAge(7 * 24 * 60 * 60) // 7 ngày
//                .sameSite("Strict")
//                .build();
//    }
//    public ResponseEntity<?> verifyEmail(String token) {
//        VerificationToken verificationToken = tokenRepository.findByToken(token);
//        System.out.println(verificationToken.toString());
//        if (verificationToken == null) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token không hợp lệ");
//        }
//
//        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token đã hết hạn");
//        }
//
//        User user = new User(verificationToken.getUserEmail(),verificationToken.getPassword(),verificationToken.getRole());
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không tìm thấy tài khoản");
//        }
//        user.setPassword(encoder.encode(user.getPassword()));
//
//        authRepository.save(user);
//        ResponseCookie cookie = createJwtCookie(user.getEmail(), user.getRole(),user.isPremium());
//        ResponseCookie refreshTokenCookie = createRefreshTokenCookie(user.getEmail(), user.getRole(),user.isPremium());
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
//        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
//
//        return ResponseEntity.ok()
//                .headers(headers)
//                .body("Xác thực email thành công! Bạn có thể đăng nhập.");
//    }
//
//    public ResponseEntity<Map<String, Object>> login(String email, String password,String path) {
//        Map<String, Object> response = new HashMap<>();
//
//        User user = authRepository.findByEmail(email);
//        String role = user.getRole();
//
//
//
//        if (user != null && encoder.matches(password, user.getPassword()) ) {
//            ResponseCookie cookie = createJwtCookie(user.getEmail(), role,user.isPremium());
//            ResponseCookie refreshTokenCookie = createRefreshTokenCookie(user.getEmail(), role,user.isPremium());
//            response.put("status", "success");
//            response.put("message", "Login successful");
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
//            headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
//
//            return ResponseEntity.ok()
//                    .headers(headers)
//                    .body(response);
//        } else {
//            response.put("status", "fail");
//            response.put("message", "Invalid email/account or password");
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
//        }
//    }
//
//    public String getUsernameFromToken(String token) {
//        if (token == null || token.isEmpty()) {
//            throw new RuntimeException("Missing token");
//        }
//
//        return JwtToken.extractUsername(token);
//    }
//    public String getRoleFromToken(String token) {
//        if (token == null || token.isEmpty()) {
//            throw new RuntimeException("Missing token");
//        }
//
//        return JwtToken.extractRole(token);
//    }
//    public boolean isPremium(String token) {
//        if (token == null || token.isEmpty()) {
//            throw new RuntimeException("Missing token");
//        }
//
//        return JwtToken.extractIsPremium(token);
//    }
//   public List<ResponseCookie> logout(HttpServletRequest request) {
//    // Xoá session
//
//
//    // Xoá jwt_token
//    ResponseCookie jwtCookie = ResponseCookie.from("jwt_token", "")
//            .httpOnly(true)
//            .secure(false)
//            .path("/")
//            .maxAge(0)
//            .sameSite("Lax")
//            .build();
//       ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", "")
//               .httpOnly(true)
//               .secure(false)
//               .path("/")
//               .maxAge(0)
//               .sameSite("Strict")
//               .build();
//    // Xoá JSESSIONID
//    ResponseCookie jsessionidCookie = ResponseCookie.from("JSESSIONID", "")
//            .path("/")
//            .maxAge(0)
//            .build();
//
//    return List.of(jwtCookie, jsessionidCookie,refreshTokenCookie);
//}
//
//
//
//
//    public User getUserByEmail(String email) {
//        return authRepository.findByEmail(email);
//    }
//
//
//
//}
