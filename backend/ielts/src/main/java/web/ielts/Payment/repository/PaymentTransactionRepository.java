package web.ielts.Payment.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import web.ielts.Payment.model.PaymentTransactions;

public interface PaymentTransactionRepository extends MongoRepository<PaymentTransactions, String> {
    // Có thể thêm custom query nếu cần
} 