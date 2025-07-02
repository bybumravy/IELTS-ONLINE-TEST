

package web.ielts.Auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import web.ielts.Auth.JwtToken;
import web.ielts.Config.JwtAuthenticationFilter;
import web.ielts.User.CustomUserDetailsService;
import web.ielts.User.User;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    JwtAuthenticationFilter filter;

    @Mock
    CustomUserDetailsService userDetailsService;

    @Mock
    FilterChain filterChain;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    UserDetails userDetails;

    // ✅ Subclass để override field userDetailsService
    class TestableFilter extends JwtAuthenticationFilter {
        public void setUserDetailsService(CustomUserDetailsService service) {
            super.userDetailsService = service;
        }
    }

    private Cookie createCookie(String name, String value) {
        return new Cookie(name, value);
    }

    @BeforeEach
    void setup() {
        TestableFilter testableFilter = new TestableFilter();
        testableFilter.setUserDetailsService(userDetailsService);
        this.filter = testableFilter;

        SecurityContextHolder.clearContext();
    }

    @Test
    void test_noToken() throws ServletException, IOException {
        when(request.getCookies()).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/api/any");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void test_validToken_setsAuthentication() throws Exception {
        String token = "valid.token";
        String username = "user123";

        when(request.getCookies()).thenReturn(new Cookie[]{
                new Cookie("jwt_token", token)
        });

        // Tạo spy của SecurityContext và set cho SecurityContextHolder
        SecurityContext spyContext = spy(SecurityContextHolder.createEmptyContext());
        SecurityContextHolder.setContext(spyContext);

        // Mock UserDetails đúng kiểu
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                username, "password", List.of()
        );

        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        try (MockedStatic<JwtToken> jwt = mockStatic(JwtToken.class)) {
            jwt.when(() -> JwtToken.extractUsername(token)).thenReturn(username);
            jwt.when(() -> JwtToken.isTokenValid(token, userDetails)).thenReturn(true);

            // Call filter
            filter.doFilterInternal(request, response, filterChain);

            verify(userDetailsService).loadUserByUsername(username);
            verify(filterChain).doFilter(request, response);

            // Verify setAuthentication() được gọi với authentication không null
            verify(spyContext).setAuthentication(argThat(auth -> auth != null));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
    @Test
    void test_token_invalid_exception_caught() throws Exception {
        String token = "invalid.token";

        when(request.getCookies()).thenReturn(new Cookie[]{
                createCookie("jwt_token", token)
        });
        when(request.getRequestURI()).thenReturn("/api/any");

        try (MockedStatic<JwtToken> jwt = mockStatic(JwtToken.class)) {
            jwt.when(() -> JwtToken.extractUsername(token)).thenThrow(new RuntimeException("bad token"));

            filter.doFilterInternal(request, response, filterChain);

            // Still proceeds with filter chain
            verify(filterChain).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }
    }
}
