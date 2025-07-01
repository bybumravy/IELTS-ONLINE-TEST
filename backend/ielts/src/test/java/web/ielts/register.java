package web.ielts;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import web.ielts.Auth.model.VerificationToken;
import web.ielts.Auth.repository.AuthRepository;
import web.ielts.Auth.repository.VerificationTokenRepository;
import web.ielts.Auth.service.AuthService;
import web.ielts.Config.EmailConfig;
import web.ielts.User.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class register {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private VerificationTokenRepository tokenRepository;

    @Mock
    private EmailConfig emailConfig;

    /**
     * Testcase TC01:
     * Chức năng: Kiểm tra khi email trống
     * Câu lệnh được thực thi:
     *  - if (user.getEmail().isEmpty()) → true
     * Nhánh được phủ: Nhánh if đầu tiên kiểm tra email rỗng
     */
    @Test
    void testRegister_EmailEmpty() {
        User user = new User();
        user.setEmail("");
        user.setPassword("password123");

        ResponseEntity<?> response = authService.register(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email không được để trống", response.getBody());
    }

    /**
     * Testcase TC02:
     * Chức năng: Kiểm tra khi password trống
     * Câu lệnh được thực thi:
     *  - if (user.getEmail().isEmpty()) → false
     *  - else if (user.getPassword().isEmpty()) → true
     * Nhánh được phủ: Nhánh else if kiểm tra password rỗng
     */
    @Test
    void testRegister_PasswordEmpty() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("");

        ResponseEntity<?> response = authService.register(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Mật khẩu không được để trống", response.getBody());
    }

    /**
     * Testcase TC03:
     * Chức năng: Kiểm tra khi email đã tồn tại trong database
     * Câu lệnh được thực thi:
     *  - if (user.getEmail().isEmpty()) → false
     *  - else if (user.getPassword().isEmpty()) → false
     *  - else if (authRepository.findByEmail(email) != null) → true
     * Nhánh được phủ: Nhánh else if kiểm tra email đã tồn tại
     */
    @Test
    void testRegister_EmailAlreadyExists() {
        User user = new User();
        user.setEmail("existing@example.com");
        user.setPassword("password123");

        when(authRepository.findByEmail("existing@example.com")).thenReturn(new User());

        ResponseEntity<?> response = authService.register(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email đã được đăng ký", response.getBody());
    }

    /**
     * Testcase TC04:
     * Chức năng: Kiểm tra khi password quá ngắn (dưới 6 ký tự)
     * Câu lệnh được thực thi:
     *  - if (user.getEmail().isEmpty()) → false
     *  - else if (user.getPassword().isEmpty()) → false
     *  - else if (authRepository.findByEmail(email) != null) → false
     *  - else if (user.getPassword().length() < 6) → true
     * Nhánh được phủ: Nhánh else if kiểm tra độ dài mật khẩu
     */
    @Test
    void testRegister_PasswordTooShort() {
        User user = new User();
        user.setEmail("new@example.com");
        user.setPassword("123");

        when(authRepository.findByEmail("new@example.com")).thenReturn(null);

        ResponseEntity<?> response = authService.register(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Mật khẩu phải có ít nhất 6 ký tự", response.getBody());
    }

    /**
     * Testcase TC05:
     * Chức năng: Kiểm tra khi đăng ký thành công
     * Câu lệnh được thực thi:
     *  - if (user.getEmail().isEmpty()) → false
     *  - else if (user.getPassword().isEmpty()) → false
     *  - else if (authRepository.findByEmail(email) != null) → false
     *  - else if (user.getPassword().length() < 6) → false
     *  - else → true (đăng ký thành công)
     * Nhánh được phủ: Nhánh else cuối
     * Ngoài ra kiểm tra việc:
     *  - Gọi save() của tokenRepository đúng 1 lần
     *  - Gửi email xác nhận đúng 1 lần
     */
    @Test
    void testRegister_Success() {
        User user = new User();
        user.setEmail("new@example.com");
        user.setPassword("password123");

        when(authRepository.findByEmail("new@example.com")).thenReturn(null);

        ResponseEntity<?> response = authService.register(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản.", response.getBody());

        verify(tokenRepository, times(1)).save(any(VerificationToken.class));
        verify(emailConfig, times(1)).sendVerificationEmail(eq("new@example.com"), anyString());
    }
}
