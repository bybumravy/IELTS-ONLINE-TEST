package web.ielts.Payment.dto;

import lombok.*;

@Data

public class PaymentRequest {
    private String orderId;
    private Long amount;
    private String orderInfo;
    private String returnUrl;
    private String notifyUrl;
    private String extraData;
    private String requestType = "captureWallet";
} 