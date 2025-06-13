package web.ielts.Payment.service;

import web.ielts.Config.MomoConfig;
import web.ielts.Payment.client.MomoApi;
import web.ielts.Payment.model.PaymentRequest;
import web.ielts.Payment.model.PaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MomoPaymentService {

    @Autowired
    private MomoConfig momoConfig;

    @Autowired
    private RestTemplate restTemplate;
    private static final Logger log = LoggerFactory.getLogger(MomoPaymentService.class);

    @Autowired
    private MomoApi momoApi;

    public Map<String, Object> createPayment(Map<String, Object> data) {
        return momoApi.createPayment(data);
    }

    public PaymentResponse createPayment(PaymentRequest request) {
        try {
            String requestId = UUID.randomUUID().toString();
            String orderId = request.getOrderId();
            String orderInfo = request.getOrderInfo();
            Long amount = request.getAmount();
            String returnUrl = momoConfig.getReturnUrl();
            String notifyUrl = momoConfig.getNotifyUrl();
            String requestType = request.getRequestType() != null ? request.getRequestType() : momoConfig.getRequestType();


            // Create raw hash
            String rawHash =
                    "&accessKey=" + momoConfig.getAccessKey() +
                    "&amount=" + amount +
                    "&extraData=" + request.getExtraData()+
                    "&ipnUrl=" + notifyUrl +
                    "&orderId=" + orderId +
                    "&orderInfo=" + orderInfo +
                    "partnerCode=" + momoConfig.getPartnerCode() +
                    "&returnUrl=" + returnUrl +
                    "&requestId=" + requestId +
                    "&requestType=" + requestType;

            // Create signature
            String signature = hmacSHA256(rawHash, momoConfig.getSecretKey());

            // Log for debugging
            log.debug("MoMo rawHash: {}", rawHash);
            log.debug("MoMo signature: {}", signature);

            // Create request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("partnerCode", momoConfig.getPartnerCode());
            requestBody.put("accessKey", momoConfig.getAccessKey());
            requestBody.put("requestId", requestId);
            requestBody.put("amount", amount);
            requestBody.put("orderId", orderId);
            requestBody.put("orderInfo", orderInfo);
            requestBody.put("returnUrl", returnUrl);
            requestBody.put("ipnUrl", notifyUrl);
            requestBody.put("extraData", request.getExtraData());
            requestBody.put("requestType", requestType);
            requestBody.put("signature", signature);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Make request to MoMo
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            Map<String, Object> response = restTemplate.postForObject(
                    momoConfig.getEndpoint(),
                    entity,
                    Map.class
            );

            // Convert response to PaymentResponse
            PaymentResponse paymentResponse = new PaymentResponse();
            paymentResponse.setRequestId(requestId);
            paymentResponse.setOrderId(orderId);
            paymentResponse.setPayUrl((String) response.get("payUrl"));
            paymentResponse.setResultCode((Integer) response.get("resultCode"));
            paymentResponse.setMessage((String) response.get("message"));

            return paymentResponse;
        } catch (Exception e) {
            throw new RuntimeException("Error creating payment: " + e.getMessage());
        }
    }

    public boolean verifySignature(String rawHash, String signature) {
        try {
            String expectedSignature = hmacSHA256(rawHash, momoConfig.getSecretKey());
            if (!signature.equals(expectedSignature)) {
                log.warn("MoMo signature mismatch: expected={}, actual={}", expectedSignature, signature);
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("Error verifying signature", e);
            return false;
        }
    }

    public boolean verifyCallback(String partnerCode, String orderId, String requestId,
                                  Long amount, String orderInfo, String orderType,
                                  String transId, Integer resultCode, String message,
                                  String payType, String signature) {
        String rawHash = "partnerCode=" + partnerCode +
                "&orderId=" + orderId +
                "&requestId=" + requestId +
                "&amount=" + amount +
                "&orderInfo=" + orderInfo +
                "&orderType=" + orderType +
                "&transId=" + transId +
                "&resultCode=" + resultCode +
                "&message=" + message +
                "&payType=" + payType;

        return verifySignature(rawHash, signature);
    }

    private String hmacSHA256(String data, String secret) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKey);
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(rawHmac);
    }
} 