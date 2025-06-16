package web.ielts.Payment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import web.ielts.Payment.client.MomoApi;
import web.ielts.Payment.model.PaymentRequest;
import web.ielts.Payment.model.PaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@Slf4j
public class MomoPaymentService {

        @Value(value="${momo.partner-code}")
        private String PARTNER_CODE;

        @Value(value="${momo.access-key}")
        private String ACCESS_KEY;

        @Value(value="${momo.secret-key}")
        private String SECRET_KEY;

        @Value(value="${momo.return-url}")
        private String REDIRECT_URL;

        @Value(value="${momo.ipn-url}")
        private String IPN_URL;

        @Value(value="${momo.request-type}")
        private String REQUEST_TYPE;

        @Autowired
        private MomoApi momoApi;

    public PaymentResponse createQR(String courseId, String courseName, long amount) {
        String orderId = UUID.randomUUID().toString();
        String requestId = UUID.randomUUID().toString();
        String orderInfo = "Thanh toán khóa học: " + courseName;
        String extraData = courseId; // hoặc có thể để là "" nếu không cần

        String rawSignature = String.format(
                "accessKey=%s&amount=%s&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
                ACCESS_KEY, amount, extraData, IPN_URL, orderId, orderInfo, PARTNER_CODE, REDIRECT_URL, requestId, REQUEST_TYPE
        );

        String signature = "";
        try {
            signature = signHmacSHA256(rawSignature, SECRET_KEY);
        } catch (Exception e) {
            log.error(">>> Lỗi khi ký HMAC SHA256: " + e.getMessage());
            return null;
        }

        if (signature.isBlank()) {
            log.error(">>> Signature trống.");
            return null;
        }

        PaymentRequest request = PaymentRequest.builder()
                .partnerCode(PARTNER_CODE)
                .requestType(REQUEST_TYPE)
                .ipnUrl(IPN_URL)
                .redirectUrl(REDIRECT_URL)
                .orderId(orderId)
                .orderInfo(orderInfo)
                .requestId(requestId)
                .extraData(extraData)
                .amount(amount)
                .signature(signature)
                .lang("vi")
                .build();

        return momoApi.createMomoQR(request);
    }

    private String signHmacSHA256(String data, String key) throws Exception {
            Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSHA256.init(secretKey);
            byte[] rawHmac = hmacSHA256.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            String hex = null;
            for (byte b : rawHmac) {
                hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        }
} 