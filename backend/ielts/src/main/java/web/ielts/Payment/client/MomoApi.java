package web.ielts.Payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import web.ielts.Payment.model.PaymentRequest;
import web.ielts.Payment.model.PaymentResponse;

import java.util.Map;

@FeignClient(name = "momo", url = "${momo.end-point}")
public interface MomoApi {
    @PostMapping("/create")
    PaymentResponse createMomoQR(@RequestBody PaymentRequest createMomoRequest);
}
