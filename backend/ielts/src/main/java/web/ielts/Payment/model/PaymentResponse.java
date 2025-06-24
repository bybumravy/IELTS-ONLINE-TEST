package web.ielts.Payment.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentResponse {
    private String partnerCode;
    private String requestId;
    private String orderId;
    private Long amount;
    private Long responseTime;
    private int resultCode;
    private String message;
    private String payUrl;
    private String deepLink;
    private String qrCodeUrl;
} 