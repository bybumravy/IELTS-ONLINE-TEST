package web.ielts.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailForgetPasswordConfig {
    @Autowired
    private JavaMailSender mailSender;

    public void sendResetPasswordEmail(String toEmail, String token) {
        String subject = "Yêu cầu đặt lại mật khẩu";

        // Nếu redirectURL không rỗng, thêm nó như một query param
        String resetUrl = "http://localhost:5173/reset-password?token=" + token;


        String body = "Bạn đã yêu cầu đặt lại mật khẩu.\n\n" +
                "Vui lòng nhấn vào liên kết sau để thiết lập mật khẩu mới:\n" +
                resetUrl + "\n\n" +
                "Nếu bạn không yêu cầu đặt lại mật khẩu, hãy bỏ qua email này.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}
