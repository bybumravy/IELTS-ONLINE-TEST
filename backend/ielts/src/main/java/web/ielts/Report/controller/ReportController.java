package web.ielts.Report.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.ielts.Report.model.Report;
import web.ielts.Report.repository.ReportRepository;


@RestController
@RequestMapping("/api/report")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ReportController {
    private final ReportRepository reportRepository;

    public ReportController(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }


    @PostMapping
    public ResponseEntity<String> submitFeedback(@RequestBody Report report) {
        reportRepository.save(report);
        return ResponseEntity.ok("Feedback submitted successfully.");
    }
}
