package web.ielts.Auth;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.util.Map;
import web.ielts.Auth.model.VerificationToken;
import web.ielts.Auth.repository.VerificationTokenRepository;
import web.ielts.Auth.service.AuthService;
import web.ielts.Config.EmailForgetPasswordConfig;

@ExtendWith(MockitoExtension.class)
class AuthServiceForgotPasswordTest {

    @Mock
    private VerificationTokenRepository tokenRepository;

    @Mock
    private EmailForgetPasswordConfig emailForgetPasswordConfig;

    @InjectMocks
    private AuthService authService;

    @Test
    void test_tokenNotFound() {
        String email = "test@example.com";
        when(tokenRepository.findByToken(email)).thenReturn(null);

        ResponseEntity<?> response = authService.forgotpassword(email);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(((Map<?, ?>)response.getBody()).get("message").toString().contains("Token không tồn tại"));
    }

    @Test
    void test_tokenExpired() {
        String email = "test@example.com";

        VerificationToken expiredToken = new VerificationToken();
        expiredToken.setExpiryDate(LocalDateTime.now().minusHours(1));  // expired
        expiredToken.setUserEmail(email);

        when(tokenRepository.findByToken(email)).thenReturn(expiredToken);

        ResponseEntity<?> response = authService.forgotpassword(email);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(((Map<?, ?>)response.getBody()).get("message").toString().contains("Token đã hết hạn"));
    }

    @Test
    void test_tokenValid() {
        String email = "test@example.com";

        VerificationToken validToken = new VerificationToken();
        validToken.setExpiryDate(LocalDateTime.now().plusHours(1));  // valid
        validToken.setUserEmail(email);
        validToken.setToken("validtoken123");

        when(tokenRepository.findByToken(email)).thenReturn(validToken);

        doNothing().when(emailForgetPasswordConfig).sendResetPasswordEmail(email, "validtoken123");

        ResponseEntity<?> response = authService.forgotpassword(email);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Gửi email thành công, vui lòng kiểm tra email.", response.getBody());

        verify(emailForgetPasswordConfig).sendResetPasswordEmail(email, "validtoken123");
    }
}
