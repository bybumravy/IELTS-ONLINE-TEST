package web.ielts.Payment.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import web.ielts.Payment.model.PaymentTransaction;

public interface PaymentTransactionRepository extends MongoRepository<PaymentTransaction, String> {
    PaymentTransaction findByOrderId(String orderId);
}
