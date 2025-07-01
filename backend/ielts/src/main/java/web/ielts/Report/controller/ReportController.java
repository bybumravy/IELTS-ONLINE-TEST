package web.ielts.Report.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.ielts.Report.model.Report;
import web.ielts.Report.service.ReportService;

@RestController
@RequestMapping("/api/report")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<String> submitFeedback(@RequestBody Report report) {
        reportService.submitFeedback(report);
        return ResponseEntity.ok("Feedback submitted successfully.");
    }
}
