package web.ielts.Auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import web.ielts.Auth.repository.AuthRepository;
import web.ielts.User.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

class CustomOAuth2SuccessHandlerTest {

    @InjectMocks
    private CustomOAuth2SuccessHandler handler;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private DefaultOAuth2User mockOAuthUser(String email, String sub) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", email);
        attributes.put("sub", sub);
        return new DefaultOAuth2User(null, attributes, "email");
    }

    // TC01: User chưa tồn tại (BVA - user == null)
    @Test
    void test_NewUser_CreatedWithStudentRole() throws IOException {
        String email = "newuser@example.com";
        String sub = "google123";
        DefaultOAuth2User oAuth2User = mockOAuthUser(email, sub);

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(authRepository.findByEmail(email)).thenReturn(null);

        User savedUser = new User();
        savedUser.setEmail(email);
        savedUser.setRole("STUDENT");

        when(authRepository.save(any(User.class))).thenReturn(savedUser);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(authRepository).save(any(User.class));
        verify(response).addHeader(eq("Set-Cookie"), contains("jwt_token"));
        verify(response).sendRedirect("http://localhost:5173");
    }

    // TC02: User role = STUDENT (EP)
    @Test
    void test_StudentUser_RedirectToHome() throws IOException {
        String email = "student@example.com";
        DefaultOAuth2User oAuth2User = mockOAuthUser(email, "sub1");

        User user = new User();
        user.setEmail(email);
        user.setRole("STUDENT");

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(authRepository.findByEmail(email)).thenReturn(user);
        when(authRepository.save(user)).thenReturn(user);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(response).sendRedirect("http://localhost:5173");
    }

    // TC03: User role = ADMIN (EP)
    @Test
    void test_AdminUser_RedirectToAdminPage() throws IOException {
        String email = "admin@example.com";
        DefaultOAuth2User oAuth2User = mockOAuthUser(email, "sub2");

        User user = new User();
        user.setEmail(email);
        user.setRole("ADMIN");

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(authRepository.findByEmail(email)).thenReturn(user);
        when(authRepository.save(user)).thenReturn(user);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(response).sendRedirect("http://localhost:5173/adminpage");
    }

    // TC04: User role = TEAcher (EP - mixed case)
    @Test
    void test_TeacherUser_RedirectToStaffPage() throws IOException {
        String email = "teacher@example.com";
        DefaultOAuth2User oAuth2User = mockOAuthUser(email, "sub3");

        User user = new User();
        user.setEmail(email);
        user.setRole("TEAcher");

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(authRepository.findByEmail(email)).thenReturn(user);
        when(authRepository.save(user)).thenReturn(user);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(response).sendRedirect("http://localhost:5173/teacher-page");
    }

    // TC05: User tồn tại nhưng role = null (BVA - role null)
    @Test
    void test_UserWithNullRole_AssignedStudentRole() throws IOException {
        String email = "norole@example.com";
        DefaultOAuth2User oAuth2User = mockOAuthUser(email, "sub4");

        User user = new User();
        user.setEmail(email);
        user.setRole(null); // role = null

        User updatedUser = new User();
        updatedUser.setEmail(email);
        updatedUser.setRole("STUDENT");

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(authRepository.findByEmail(email)).thenReturn(user);
        when(authRepository.save(any(User.class))).thenReturn(updatedUser);

        handler.onAuthenticationSuccess(request, response, authentication);


        verify(authRepository).save(any(User.class));
        verify(response).addHeader(eq("Set-Cookie"), contains("jwt_token"));
        verify(response).sendRedirect("http://localhost:5173");
    }

    // TC06: User có role không hợp lệ (EP - fallback case)
    @Test
    void test_UserWithUnknownRole_FallbackToStaffPage() throws IOException {
        String email = "stranger@example.com";
        DefaultOAuth2User oAuth2User = mockOAuthUser(email, "sub5");

        User user = new User();
        user.setEmail(email);
        user.setRole("UNKNOWN"); // Invalid role

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(authRepository.findByEmail(email)).thenReturn(user);
        when(authRepository.save(user)).thenReturn(user);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(response).sendRedirect("http://localhost:5173/staff-page");
    }
}

