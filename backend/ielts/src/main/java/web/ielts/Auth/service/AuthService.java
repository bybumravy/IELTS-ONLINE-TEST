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

    /**
     * Register a new user and send a verification email
     */
    public ResponseEntity<?> register(User newUser) { //Unit Test register

        // Branch 1: Check if email is empty
        if (newUser.getEmail() == null || newUser.getEmail().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email cannot be empty");
        }

        // Branch 2: Check if password is empty
        if (newUser.getPassword() == null || newUser.getPassword().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password cannot be empty");
        }

        // Branch 3: Check if email already exists in the system
        if (authRepository.findByEmail(newUser.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email has already been registered");
        }

        // Branch 4 (Optional): Check if password has a minimum length of 6 characters
        if (newUser.getPassword().length() < 6) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password must be at least 6 characters long");
        }

        // If all validations pass → Create a verification token
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(
                token,
                newUser.getEmail(),
                newUser.getPassword(),
                LocalDateTime.now().plusHours(24),
                "student"
        );

        // Save the verification token into the database
        tokenRepository.save(verificationToken);

        // Send a verification email with the token
        emailConfig.sendVerificationEmail(newUser.getEmail(), token);

        return ResponseEntity.ok("Registration successful! Please check your email to verify your account.");
    }

    /**
     * Verify the user's email based on a verification token
     */
    public ResponseEntity<?> verifyEmail(String token) { //Unit Test verifyEmail
        // Find the token in the database
        VerificationToken verificationToken = tokenRepository.findByToken(token);
        System.out.println(verificationToken.toString());

        // Branch 1: If the token is not found
        if (verificationToken == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
        }

        // Branch 2: If the token has expired
        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token has expired");
        }

        // If token is valid → Create a new user from token details
        User user = new User(
                verificationToken.getUserEmail(),
                verificationToken.getPassword(),
                verificationToken.getRole()
        );

        // Hash the password before saving
        user.setPassword(encoder.encode(user.getPassword()));

        // Save the verified user into the database
        authRepository.save(user);

        // Generate JWT token for the new user
        String tokenJwt = JwtToken.generateToken(user.getEmail(), user.getRole());

        // Create JWT token cookie
        ResponseCookie cookie = ResponseCookie.from("jwt_token", tokenJwt)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Email verification successful! You can now log in.");
    }

    /**
     * Login a user with email and password
     */
    public ResponseEntity<Map<String, Object>> login(String email, String password) { //Unit Test login
        Map<String, Object> response = new HashMap<>();

        // Branch 1: Check if email or password is empty
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            response.put("status", "fail");
            response.put("message", "Email and password cannot be empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Branch 2: Validate email format
        if (!email.contains("@")) {
            response.put("status", "fail");
            response.put("message", "Invalid email format");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Look up user by email
        User user = authRepository.findByEmail(email);

        // Branch 3: Check if user exists and password matches
        if (user != null && encoder.matches(password, user.getPassword())) {
            // Generate JWT token
            String token = JwtToken.generateToken(user.getEmail(), user.getRole());

            // Create JWT cookie
            ResponseCookie cookie = ResponseCookie.from("jwt_token", token)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(24 * 60 * 60)
                    .sameSite("Lax")
                    .build();

            response.put("status", "success");
            response.put("message", "Login successful");

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(response);
        }
        // Branch 4: If email or password is invalid
        else {
            response.put("status", "fail");
            response.put("message", "Invalid email/account or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * Extract the username (email) from a JWT token
     */
    public String getUsernameFromToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Missing token");
        }
        return JwtToken.extractUsername(token);
    }

    /**
     * Extract the user role from a JWT token
     */
    public String getRoleFromToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Missing token");
        }
        return JwtToken.extractRole(token);
    }

    /**
     * Logout by clearing jwt_token and session cookies
     */
    public List<ResponseCookie> logout(HttpServletRequest request) {
        // Clear jwt_token cookie
        ResponseCookie jwtCookie = ResponseCookie.from("jwt_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        // Clear JSESSIONID cookie
        ResponseCookie jsessionidCookie = ResponseCookie.from("JSESSIONID", "")
                .path("/")
                .maxAge(0)
                .build();

        return List.of(jwtCookie, jsessionidCookie);
    }

    /**
     * Fetch a user by their email address
     */
    public User getUserByEmail(String email) {
        return authRepository.findByEmail(email);
    }
}
