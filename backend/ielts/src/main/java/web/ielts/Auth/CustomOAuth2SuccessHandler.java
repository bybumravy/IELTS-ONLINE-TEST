package web.ielts.Auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import web.ielts.Auth.repository.AuthRepository;
import web.ielts.User.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        String roleFromQuery = (String) request.getSession().getAttribute("oauth2_role");
        User user = loginRepository.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setRole(new ArrayList<>(List.of("student")));
            user.setPassword(null);
            user.setGoogleID(googleId);
            user.setPremium(false);
            roleFromQuery = "student";
        } else {
            if (!user.getRole().contains(roleFromQuery)) {
                return;
            }
        }
        user = loginRepository.save(user);

        boolean isPremium = user.isPremium();
        System.out.println(isPremium);
// Tạo JWT với role user vừa lấy (hoặc mới tạo)
        String token = JwtToken.generateAccessToken(email,roleFromQuery,isPremium );


        // Tạo Cookie
        ResponseCookie cookie = ResponseCookie.from("jwt_token", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("Lax")
                .build();

        // Gửi cookie về trình duyệt
        response.addHeader("Set-Cookie", cookie.toString());
        String refreshToken = JwtToken.generateRefreshToken(email, roleFromQuery,isPremium);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7 ngày
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", refreshCookie.toString());
        String redirectUrl;

        switch (roleFromQuery.toUpperCase()) {
            case "STUDENT":
                redirectUrl = "https://www.languages.io.vn/";
                break;
            case "ADMIN":
                redirectUrl = "https://www.languages.io.vn/admin-page";
                break;
            case "TEACHER":
                redirectUrl = "https://www.languages.io.vn/staff-page";
                break;
            default:
                redirectUrl = "https://www.languages.io.vn/staff-page";
                break;
        }

        response.sendRedirect(redirectUrl);


        // Redirect về frontend (không cần token trên URL nữa)

    }

}