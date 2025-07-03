package web.ielts.Payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.ielts.Payment.dto.PaymentTransactionDto;
import web.ielts.Payment.model.PaymentTransactions;
import web.ielts.Payment.service.TransactionService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class TransactionController {

    @Autowired
    private TransactionService service;

    @GetMapping
    public List<PaymentTransactions> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public PaymentTransactions getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public PaymentTransactions create(@RequestBody PaymentTransactions transaction) {
        return service.save(transaction);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    @PostMapping("/transactions/save")
    public ResponseEntity<String> recordTransaction(@RequestBody PaymentTransactionDto dto, Principal principal) {
        String email = principal.getName(); // Lấy email từ JWT
        service.saveTransaction(
                email, dto.getType(), dto.getAmount(), dto.getPaymentMethod(), dto.getStatus(), dto.getMessage()
        );
        return ResponseEntity.ok("Giao dịch đã được lưu");
    }

    @GetMapping("/user/transactions")
    public ResponseEntity<List<PaymentTransactions>> getMyTransactions(Principal principal) {
        String email = principal.getName();
        return ResponseEntity.ok(service.getUserTransactions(email));
    }

}
