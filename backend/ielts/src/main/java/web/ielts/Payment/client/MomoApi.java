package web.ielts.Payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "momo-client", url = "https://test-payment.momo.vn")
public interface MomoApi {
    @PostMapping("/v2/gateway/api/create")
    Map<String, Object> createPayment(@RequestBody Map<String, Object> request);
}
