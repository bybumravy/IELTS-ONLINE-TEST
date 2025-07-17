package web.ielts.Payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import web.ielts.Payment.model.PaymentTransaction;
import web.ielts.Payment.repository.PaymentTransactionRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payment/transactions")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class TransactionController {
    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    // API trả về danh sách giao dịch (có thể dùng cho bảng chi tiết)
    @GetMapping
    public List<PaymentTransaction> getAllTransactions() {
        return paymentTransactionRepository.findAll();
    }

    // API trả về thống kê tổng tiền theo tháng (12 tháng gần nhất)
    @GetMapping("/statistics")
    public List<Stat> getStatistics(@RequestParam(defaultValue = "month") String type) {
        List<PaymentTransaction> all = paymentTransactionRepository.findAll();
        Map<String, Long> keyToTotal = new HashMap<>();
        DateTimeFormatter fmt;
        for (PaymentTransaction tx : all) {
            if (tx.getCreatedAt() == null) continue;
            LocalDate date = tx.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String key;
            switch (type) {
                case "day":
                    fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    key = date.format(fmt);
                    break;
                case "week":
                    WeekFields weekFields = WeekFields.of(Locale.getDefault());
                    int weekNum = date.get(weekFields.weekOfWeekBasedYear());
                    key = date.getYear() + "-W" + String.format("%02d", weekNum);
                    break;
                case "year":
                    key = String.valueOf(date.getYear());
                    break;
                case "month":
                default:
                    fmt = DateTimeFormatter.ofPattern("yyyy-MM");
                    key = date.format(fmt);
            }
            keyToTotal.put(key, keyToTotal.getOrDefault(key, 0L) + tx.getAmount());
        }
        // Sắp xếp key tăng dần
        List<Stat> stats = keyToTotal.entrySet().stream()
                .map(e -> new Stat(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(Stat::getKey))
                .collect(Collectors.toList());
        return stats;
    }

    // DTO trả về cho thống kê
    public static class Stat {
        private String key; // ngày/tháng/năm/tuần
        private long totalAmount;
        public Stat(String key, long totalAmount) {
            this.key = key;
            this.totalAmount = totalAmount;
        }
        public String getKey() { return key; }
        public long getTotalAmount() { return totalAmount; }
    }
} 