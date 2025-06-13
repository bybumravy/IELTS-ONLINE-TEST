package web.ielts.Payment.controller;

import web.ielts.Payment.model.PaymentRequest;
import web.ielts.Payment.model.PaymentResponse;
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

    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest request) {
        PaymentResponse response = momoPaymentService.createPayment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/callback")
    public ResponseEntity<String> paymentCallback(
            @RequestParam String partnerCode,
            @RequestParam String orderId,
            @RequestParam String requestId,
            @RequestParam Long amount,
            @RequestParam String orderInfo,
            @RequestParam String orderType,
            @RequestParam String transId,
            @RequestParam Integer resultCode,
            @RequestParam String message,
            @RequestParam String payType,
            @RequestParam String signature) {

        boolean isValid = momoPaymentService.verifyCallback(
                partnerCode, orderId, requestId, amount, orderInfo,
                orderType, transId, resultCode, message, payType, signature
        );

        if (isValid) {
            // TODO: Update order status in database
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.badRequest().body("Invalid signature");
        }
    }

    @GetMapping("/status/{orderId}")
    public ResponseEntity<PaymentResponse> checkPaymentStatus(@PathVariable String orderId) {
        // TODO: Implement payment status check from database
        PaymentResponse response = new PaymentResponse();
        response.setOrderId(orderId);
        response.setResultCode(0);
        response.setMessage("Payment successful");
        return ResponseEntity.ok(response);
    }
} 