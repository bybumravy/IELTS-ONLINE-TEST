package web.ielts.User;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "user")
public class User implements UserDetails {

    private String firstName;
    private String lastName;
    @Id
    private String email;
    private String password;
    private String role;
    private LocalDate premiumExpiry;
    private String googleID;

    private String createdAt;
    public User(String email, String password,String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public boolean isPremiumActive() {
        return premiumExpiry != null && premiumExpiry.isAfter(LocalDate.now());
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getUsername() {
        return email; // Bạn dùng email làm username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // hoặc logic thực tế
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}