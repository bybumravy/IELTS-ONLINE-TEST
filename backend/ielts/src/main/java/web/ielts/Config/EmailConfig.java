package web.ielts.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailConfig {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String token) {
        String subject = "Xác thực Email đăng ký tài khoản";
        String verificationUrl = "https://www.languages.io.vn/verify-email?token=" + token;
        String body = "Bạn vui lòng nhấn vào đường dẫn sau để xác thực tài khoản:\n" + verificationUrl;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}