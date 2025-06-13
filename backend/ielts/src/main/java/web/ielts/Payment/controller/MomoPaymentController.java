package web.ielts.Payment.controller;

import web.ielts.Payment.model.PaymentRequest;
import web.ielts.Payment.model.PaymentResponse;
import web.ielts.Payment.model.MomoCallbackRequest;
import web.ielts.Payment.model.PaymentTransaction;
import web.ielts.Payment.repository.PaymentTransactionRepository;
import web.ielts.Payment.service.MomoPaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class MomoPaymentController {

    @Autowired
    private MomoPaymentService momoPaymentService;

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    // Tạo thanh toán MoMo
    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest request) {
        PaymentResponse response = momoPaymentService.createPayment(request);
        return ResponseEntity.ok(response);
    }

    // Xử lý callback (quay lại trình duyệt sau khi thanh toán)
    @PostMapping("/callback")
    public ResponseEntity<String> paymentCallback(@RequestBody MomoCallbackRequest request) {
        boolean isValid = momoPaymentService.verifyCallback(
                request.getPartnerCode(),
                request.getOrderId(),
                request.getRequestId(),
                request.getAmount(),
                request.getOrderInfo(),
                request.getOrderType(),
                request.getTransId(),
                request.getResultCode(),
                request.getMessage(),
                request.getPayType(),
                request.getSignature()
        );

        if (isValid) {
            // TODO: Update DB or business logic
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.badRequest().body("Invalid signature");
        }
    }

    // Xử lý IPN từ MoMo (notifyUrl) — lưu kết quả vào DB
    @PostMapping("/ipn")
    public ResponseEntity<String> handleMomoIpn(@RequestBody MomoCallbackRequest request) {
        // Tạo raw hash giống logic của MoMo
        String rawHash = "partnerCode=" + request.getPartnerCode()
                + "&orderId=" + request.getOrderId()
                + "&requestId=" + request.getRequestId()
                + "&amount=" + request.getAmount()
                + "&orderInfo=" + request.getOrderInfo()
                + "&orderType=" + request.getOrderType()
                + "&transId=" + request.getTransId()
                + "&resultCode=" + request.getResultCode()
                + "&message=" + request.getMessage()
                + "&payType=" + request.getPayType();

        boolean isValid = momoPaymentService.verifySignature(rawHash, request.getSignature());

        // Lưu giao dịch vào MongoDB
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setOrderId(request.getOrderId());
        transaction.setPartnerCode(request.getPartnerCode());
        transaction.setRequestId(request.getRequestId());
        transaction.setAmount(request.getAmount());
        transaction.setOrderInfo(request.getOrderInfo());
        transaction.setOrderType(request.getOrderType());
        transaction.setTransId(request.getTransId());
        transaction.setResultCode(request.getResultCode());
        transaction.setMessage(request.getMessage());
        transaction.setPayType(request.getPayType());
        transaction.setSignature(request.getSignature());
        transaction.setVerified(isValid);

        paymentTransactionRepository.save(transaction);

        return isValid
                ? ResponseEntity.ok("success")
                : ResponseEntity.badRequest().body("Invalid signature in IPN");
    }


    // Truy vấn trạng thái thanh toán theo orderId
    @GetMapping("/status/{orderId}")
    public ResponseEntity<PaymentResponse> checkPaymentStatus(@PathVariable String orderId) {
        PaymentTransaction transaction = paymentTransactionRepository.findByOrderId(orderId);

        if (transaction == null) {
            return ResponseEntity.status(404).body(
                    PaymentResponse.builder()
                            .orderId(orderId)
                            .resultCode(1)
                            .message("Order not found")
                            .build()
            );

        }

        return ResponseEntity.ok(
                PaymentResponse.builder()
                        .orderId(orderId)
                        .resultCode(transaction.getResultCode())
                        .message(transaction.getMessage())
                        .build()
        );

    }
}
