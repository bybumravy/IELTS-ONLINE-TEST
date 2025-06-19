
package web.ielts.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.config.Customizer;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import web.ielts.Auth.CustomOAuth2SuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    @Autowired
    private JwtAuthenticationFilter jwtFilter;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
         http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults()) //
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/momo/create",
                                "/api/momo/ipn-handler",
                                "/api/payment/callback",
                                "/api/login",
                                "/api/user-info",
                                "/api/logout",
                                "/oauth2/**",
                                "/login/oauth2/",
                                "/oauth2/",
                                "/api/*",
                                "/api/payment/status/**",
                                "/api/tips-summary",
                                "/api/*/*",
                                "/api/3-tests",
                                "/api/*/*/*"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                 .oauth2Login(oauth2 -> oauth2
                         .successHandler(customOAuth2SuccessHandler)
                 )
                 .sessionManagement(session -> session
                         .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                 )
                 .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}



