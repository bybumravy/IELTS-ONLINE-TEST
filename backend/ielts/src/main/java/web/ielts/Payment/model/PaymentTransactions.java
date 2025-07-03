package web.ielts.Payment.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

    @Document(collection = "paymentTransactions")
    public class PaymentTransactions {
        @Id
        private String id;
        private String email;
        private String type;
        private double amount;
        private String paymentMethod;
        private String status;
        private LocalDate createdAt;
        private String message;

        public PaymentTransactions() {
        }

        public PaymentTransactions(String id, String email, String type, double amount, String paymentMethod, String status, LocalDate createdAt, String message) {
            this.id = id;
            this.email = email;
            this.type = type;
            this.amount = amount;
            this.paymentMethod = paymentMethod;
            this.status = status;
            this.createdAt = createdAt;
            this.message = message;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public LocalDate getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDate createdAt) {
            this.createdAt = createdAt;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
