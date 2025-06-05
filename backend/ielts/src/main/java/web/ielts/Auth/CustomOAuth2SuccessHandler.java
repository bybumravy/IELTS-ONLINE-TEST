package web.ielts.Auth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.ielts.User.User;
@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler{
@Autowired
    private AuthRepository loginRepository;
   
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String googleId = oAuth2User.getAttribute("sub");




        User user = loginRepository.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setPassword(null);
            user.setGoogleID(googleId);
            loginRepository.save(user);
        }

// Tạo JWT với role user vừa lấy (hoặc mới tạo)
        String token = JwtToken.generateToken(email, user.getRole());


        // Tạo Cookie
        ResponseCookie cookie = ResponseCookie.from("jwt_token", token)
                .httpOnly(true)
                .secure(false) // Đặt true nếu dùng HTTPS
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("Lax")
                .build();

        // Gửi cookie về trình duyệt
        response.addHeader("Set-Cookie", cookie.toString());

        // Redirect về frontend (không cần token trên URL nữa)
        response.sendRedirect("http://localhost:5174");
    }

}


