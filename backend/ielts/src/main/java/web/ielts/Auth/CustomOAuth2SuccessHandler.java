package web.ielts.Auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import web.ielts.Auth.repository.AuthRepository;
import web.ielts.User.User;

import java.io.IOException;
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
            user.setRole("student");
            user.setPassword(null);
            user.setGoogleID(googleId);
        } else {
            if (user.getRole() == null) {
                user.setRole("student");
            }
        }
        user = loginRepository.save(user);
        String role = user.getRole();

// Tạo JWT với role user vừa lấy (hoặc mới tạo)
        String token = JwtToken.generateToken(email,role );


        // Tạo Cookie
        ResponseCookie cookie = ResponseCookie.from("jwt_token", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("Lax")
                .build();

        // Gửi cookie về trình duyệt
        response.addHeader("Set-Cookie", cookie.toString());
        if(user.getRole().equalsIgnoreCase("STUDENT")){
            response.sendRedirect("http://localhost:5173");
        }
        else if(user.getRole().equalsIgnoreCase("ADMIN")){
            response.sendRedirect("http://localhost:5173/adminpage");
        }
        else if(user.getRole().equalsIgnoreCase("TEAcher")){
            response.sendRedirect("http://localhost:5173/staff-page");
        }
        else{
            response.sendRedirect("http://localhost:5173/staff-page");
        }
        // Redirect về frontend (không cần token trên URL nữa)

    }

}