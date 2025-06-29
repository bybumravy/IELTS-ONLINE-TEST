package web.ielts.Auth;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import web.ielts.Auth.model.VerificationToken;
import web.ielts.Auth.repository.AuthRepository;
import web.ielts.Auth.repository.VerificationTokenRepository;
import web.ielts.Auth.service.AuthService;
import web.ielts.User.User;

 class ResetPasswordTest {

    @Mock
    private VerificationTokenRepository tokenRepository;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ TC01: FAIL branch - kiểm tra đủ các lỗi (token null, expired, pw ngắn, user null)
    @Test
    void resetPassword_invalidCases_returnsBadRequest() {
        String token = "fake-token";

        // ❌ Case 1: Token không tồn tại
        when(tokenRepository.findByToken(token)).thenReturn(null);
        var res1 = authService.resetPassword(token, "ValidPass123");
        assertEquals(BAD_REQUEST, res1.getStatusCode());
        assertEquals("Token không hợp lệ", res1.getBody());

        // ❌ Case 2: Token hết hạn
        VerificationToken expiredToken = new VerificationToken();
        expiredToken.setToken(token);
        expiredToken.setExpiryDate(LocalDateTime.now().minusMinutes(1));
        when(tokenRepository.findByToken(token)).thenReturn(expiredToken);
        var res2 = authService.resetPassword(token, "ValidPass123");
        assertEquals(BAD_REQUEST, res2.getStatusCode());
        assertEquals("Token đã hết hạn", res2.getBody());

        // ❌ Case 3: Mật khẩu quá ngắn
        VerificationToken validToken = new VerificationToken();
        validToken.setToken(token);
        validToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        when(tokenRepository.findByToken(token)).thenReturn(validToken);
        var res3 = authService.resetPassword(token, "short");
        assertEquals(BAD_REQUEST, res3.getStatusCode());
        assertEquals("Mật khẩu mới phải có ít nhất 8 ký tự", res3.getBody());

        // ❌ Case 4: User không tồn tại
        validToken.setUserEmail("nonexistent@example.com");
        when(authRepository.findByEmail("nonexistent@example.com")).thenReturn(null);
        var res4 = authService.resetPassword(token, "ValidPassword123");
        assertEquals(BAD_REQUEST, res4.getStatusCode());
        assertEquals("User không tồn tại", res4.getBody());
    }

    // ✅ TC02: SUCCESS branch - token hợp lệ, mật khẩu đúng, user tồn tại
    void resetPassword_validInput_returnsSuccessResponse() {
        // Chuẩn bị dữ liệu
        String token = "valid-token";
        String newPassword = "StrongPassword123";
        String encodedPassword = "encodedPassword123";

        // Mock token
        VerificationToken tokenEntity = new VerificationToken();
        tokenEntity.setToken(token);
        tokenEntity.setExpiryDate(LocalDateTime.now().plusDays(1));
        tokenEntity.setUserEmail("user@example.com");

        // Mock user
        User user = new User();
        user.setEmail("user@example.com");
        user.setRole("ADMIN");
        user.setPassword(encodedPassword); // Set password đã mã hóa

        // Mock repository
        when(tokenRepository.findByToken(token)).thenReturn(tokenEntity);
        when(authRepository.findByEmail("user@example.com")).thenReturn(user);

        // Mock encoder
        when(encoder.encode(newPassword)).thenReturn(encodedPassword);
        when(encoder.matches(newPassword, encodedPassword)).thenReturn(true); // Thêm dòng này

        // Gọi phương thức
        ResponseEntity<?> response = authService.resetPassword(token, newPassword);

        // Kiểm tra
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Đặt lại mật khẩu thành công", responseBody.get("message"));
        assertEquals("admin", responseBody.get("role"));

        // Kiểm tra password đã được cập nhật
        verify(authRepository).save(argThat(u ->
                u.getPassword().equals(encodedPassword)
        ));

        // Kiểm tra encoder được gọi
        verify(encoder).encode(newPassword);
        verify(tokenRepository).delete(tokenEntity);
    }
 }

