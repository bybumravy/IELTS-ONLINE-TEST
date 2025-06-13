package web.ielts.Payment.model;

import lombok.Data;

@Data
public class PaymentResponse {
    private String requestId;
    private String orderId;
    private String payUrl;
    private String signature;
    private Integer resultCode;
    private String message;
    private String transId;
    private Long amount;
    private String orderInfo;
    private String orderType;
    private String payType;
} 