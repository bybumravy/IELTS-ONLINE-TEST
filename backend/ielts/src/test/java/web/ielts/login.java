package web.ielts;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import web.ielts.Auth.repository.AuthRepository;
import web.ielts.Auth.service.AuthService;
import web.ielts.User.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class login {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private PasswordEncoder encoder;

    /**
     * Testcase TC-001:
     * Chức năng: Kiểm tra nếu email hoặc password rỗng
     * Câu lệnh:
     *  - if (email == null || email.isEmpty() || password == null || password.isEmpty()) → true
     * Nhánh phủ: Nhánh trả về BAD_REQUEST với message lỗi điền thiếu
     */
    @Test
    void testLogin_EmailOrPasswordEmpty() {
        ResponseEntity<Map<String, Object>> response = authService.login("", "password123");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email và mật khẩu không được để trống", response.getBody().get("message"));

        response = authService.login("test@example.com", "");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email và mật khẩu không được để trống", response.getBody().get("message"));
    }

    /**
     * Testcase TC-002:
     * Chức năng: Kiểm tra nếu email không chứa '@'
     * Câu lệnh:
     *  - if (!email.contains("@")) → true
     * Nhánh phủ: Nhánh trả về BAD_REQUEST với message email không hợp lệ
     */
    @Test
    void testLogin_InvalidEmailFormat() {
        ResponseEntity<Map<String, Object>> response = authService.login("invalidemail.com", "password123");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email không hợp lệ", response.getBody().get("message"));
    }

    /**
     * Testcase TC-003:
     * Chức năng: Kiểm tra nếu không tìm thấy User với email đó
     * Câu lệnh:
     *  - if (user == null) → true
     * Nhánh phủ: Nhánh trả về UNAUTHORIZED với message sai email hoặc password
     */
    @Test
    void testLogin_UserNotFound() {
        when(authRepository.findByEmail("notfound@example.com")).thenReturn(null);

        ResponseEntity<Map<String, Object>> response = authService.login("notfound@example.com", "password123");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid email/account or password", response.getBody().get("message"));
    }

    /**
     * Testcase TC-004:
     * Chức năng: Kiểm tra nếu password nhập vào không khớp password đã lưu
     * Câu lệnh:
     *  - if (user == null) → false
     *  - if (!encoder.matches(password, user.getPassword())) → true
     * Nhánh phủ: Nhánh trả về UNAUTHORIZED với message sai password
     */
    @Test
    void testLogin_InvalidPassword() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        when(authRepository.findByEmail("test@example.com")).thenReturn(user);
        when(encoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        ResponseEntity<Map<String, Object>> response = authService.login("test@example.com", "wrongPassword");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid email/account or password", response.getBody().get("message"));
    }

    /**
     * Testcase TC-005:
     * Chức năng: Kiểm tra nếu email và password hợp lệ
     * Câu lệnh:
     *  - if (user == null) → false
     *  - if (!encoder.matches(password, user.getPassword())) → false
     * Nhánh phủ: Nhánh trả về OK với message đăng nhập thành công
     */
    @Test
    void testLogin_Success() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRole("USER");

        when(authRepository.findByEmail("test@example.com")).thenReturn(user);
        when(encoder.matches("password123", "encodedPassword")).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = authService.login("test@example.com", "password123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("Login successful", response.getBody().get("message"));
    }
}
