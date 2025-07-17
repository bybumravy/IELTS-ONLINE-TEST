package web.ielts.Payment.model;


import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.CreatedDate;
import java.util.Date;


@Document(collection = "paymentTransactions")
public class PaymentTransaction {

    @Id
    private String orderId;
    private String partnerCode;
    private String requestId;
    private long amount;
    private String orderInfo;
    private String orderType;
    private String transId;
    private int resultCode;
    private String message;
    private String payType;
    private String signature;

    private boolean verified; // đã xác minh chữ ký hợp lệ hay chưa

    @CreatedDate
    private Date createdAt;

    public PaymentTransaction(String orderId, String partnerCode, String requestId, long amount, String orderInfo, String orderType, String transId, int resultCode, String message, String payType, String signature, boolean verified) {
        this.orderId = orderId;
        this.partnerCode = partnerCode;
        this.requestId = requestId;
        this.amount = amount;
        this.orderInfo = orderInfo;
        this.orderType = orderType;
        this.transId = transId;
        this.resultCode = resultCode;
        this.message = message;
        this.payType = payType;
        this.signature = signature;
        this.verified = verified;
    }

    public PaymentTransaction() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPartnerCode() {
        return partnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
