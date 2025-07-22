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
        String verificationUrl = "http://localhost:5173/verify-email?token=" + token;
        String body = "Bạn vui lòng nhấn vào đường dẫn sau để xác thực tài khoản:\n" + verificationUrl;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    public void sendNotificationToStudent(String studentEmail, String testId, double bandScore) {

        String languageUrl = "http://localhost:5173/";
        // Gửi email thông báo
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(studentEmail);
        message.setSubject("Kết quả bài Writing IELTS của bạn đã có");
        message.setText(String.format(
                "Bài Writing IELTS của bạn (ID: %s) đã được chấm điểm.\n\n" +
                        "Điểm tổng: %.1f\n\n" +
                        "Vui lòng đăng nhập vào hệ thống để xem chi tiết.\n\n" +
                        languageUrl,

                testId, bandScore
        ));

        mailSender.send(message);

        // Có thể thêm gửi thông báo trong hệ thống ở đây nếu cần
    }
}