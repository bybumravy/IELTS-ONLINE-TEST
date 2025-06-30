package web.ielts.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User resetPremiumIfExpired(User user) {
        if (user.isPremiumActive()) {
            user.setPremiumExpiry(null);
            userRepository.save(user);
        }
        return user;
    }

    public void upgradeToPremium(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        user.setPremiumExpiry(LocalDateTime.now(ZoneOffset.UTC).plusMinutes(1));// Premium trong 30 ngày
        userRepository.save(user);
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}
