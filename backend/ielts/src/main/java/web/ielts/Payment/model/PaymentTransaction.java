package web.ielts.Payment.model;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
}
