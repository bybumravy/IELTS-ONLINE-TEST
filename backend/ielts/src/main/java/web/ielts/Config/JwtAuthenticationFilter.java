package web.ielts.Config;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import web.ielts.Auth.JwtToken;
import web.ielts.User.CustomUserDetailsService;


import java.io.IOException;
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private CustomUserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws IOException, ServletException {
        String path = request.getRequestURI();

//        if (path.startsWith("/oauth2/") ||
//                path.startsWith("/login/oauth2/") ||
//                path.equals("/api/login")) {
//            filterChain.doFilter(request, response);
//            return;
//        }

        String token = getTokenFromCookies(request);

        if (token != null) {
            try {
                String username = JwtToken.extractUsername(token);
                System.out.println("check 0");
                if (username != null && !(SecurityContextHolder.getContext().getAuthentication()
                        instanceof UsernamePasswordAuthenticationToken)) {

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    System.out.println("check 1");
                    if (JwtToken.isTokenValid(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        System.out.println("check 2");
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                    System.out.println("check 3");
                }
            } catch (Exception e) {
                System.out.println("Token invalid: " + e.getMessage());
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
    private String getTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("jwt_token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

}