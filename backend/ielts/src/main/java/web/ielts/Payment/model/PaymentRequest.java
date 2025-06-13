package web.ielts.Payment.model;

import lombok.*;

@Data
public class PaymentRequest {
    private String partnerCode;
    private String requestType;
    private String orderId;
    private long amount;
    private String orderInfo;
    private String requestId;
    private String returnUrl;
    private String notifyUrl;
    private String lang;
    private String extraData;
    private String signature;

    public PaymentRequest(String orderId, long amount, String orderInfo, String returnUrl, String notifyUrl, String extraData) {
        this.orderId = orderId;
        this.amount = amount;
        this.orderInfo = orderInfo;
        this.returnUrl = returnUrl;
        this.notifyUrl = notifyUrl;
        this.extraData = extraData;
    }
}
