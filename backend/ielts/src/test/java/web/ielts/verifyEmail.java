package web.ielts;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.http.ResponseCookie;
import org.mockito.junit.jupiter.MockitoExtension;
import web.ielts.Auth.model.VerificationToken;
import web.ielts.Auth.repository.AuthRepository;
import web.ielts.Auth.repository.VerificationTokenRepository;
import web.ielts.Auth.service.AuthService;
import web.ielts.User.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class verifyEmail {

    @InjectMocks
    private AuthService authService;

    @Mock
    private VerificationTokenRepository tokenRepository;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private PasswordEncoder encoder;

    /**
     * Testcase TC01:
     * Chức năng: Kiểm tra với token không hợp lệ (không tìm thấy trong DB)
     * Câu lệnh được thực thi:
     *  - findByToken() trả về null
     *  - if (token == null) → true
     * Nhánh được phủ: Nhánh if token null
     */
    @Test
    void testVerifyEmail_InvalidToken() {
        when(tokenRepository.findByToken("invalidToken")).thenReturn(null);

        ResponseEntity<?> response = authService.verifyEmail("invalidToken");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Token không hợp lệ", response.getBody());
    }

    /**
     * Testcase TC02:
     * Chức năng: Kiểm tra với token hợp lệ nhưng đã hết hạn
     * Câu lệnh được thực thi:
     *  - findByToken() trả về token
     *  - if (token.getExpiryDate().isBefore(now)) → true
     * Nhánh được phủ: Nhánh kiểm tra token hết hạn
     */
    @Test
    void testVerifyEmail_ExpiredToken() {
        VerificationToken token = new VerificationToken();
        token.setToken("expiredToken");
        token.setExpiryDate(LocalDateTime.now().minusMinutes(1));

        when(tokenRepository.findByToken("expiredToken")).thenReturn(token);

        ResponseEntity<?> response = authService.verifyEmail("expiredToken");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Token đã hết hạn", response.getBody());
    }

    /**
     * Testcase TC03:
     * Chức năng: Kiểm tra với token hợp lệ và chưa hết hạn
     * Câu lệnh được thực thi:
     *  - findByToken() trả về token
     *  - if (token.getExpiryDate().isBefore(now)) → false
     *  - encode password
     *  - lưu User vào AuthRepository
     *  - tạo JWT token cookie
     * Nhánh được phủ: Nhánh success
     */
    @Test
    void testVerifyEmail_Success() {
        VerificationToken token = new VerificationToken();
        token.setToken("validToken");
        token.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        token.setUserEmail("test@example.com");
        token.setPassword("plainPassword");
        token.setRole("USER");

        when(tokenRepository.findByToken("validToken")).thenReturn(token);
        when(encoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(authRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        ResponseEntity<?> response = authService.verifyEmail("validToken");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Xác thực email thành công! Bạn có thể đăng nhập.", response.getBody());

        // Kiểm tra header chứa cookie jwt_token
        String setCookie = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertNotNull(setCookie);
        assertTrue(setCookie.contains("jwt_token"));

        verify(authRepository, times(1)).save(any(User.class));
    }

    //Cần 3 TestCase để phủ tất cả các nhánh (TC-01, TC-02, TC-03)
}
