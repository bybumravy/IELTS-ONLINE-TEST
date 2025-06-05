package web.ielts.Tips.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import web.ielts.Tips.dto.TipDTO;
import web.ielts.Tips.model.ReadingTips;
import web.ielts.Tips.repository.ReadingTipRepository;
import web.ielts.Tips.service.TipsService;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")

@RestController
@RequestMapping("/api")
public class TipsController {

    @Autowired
    private TipsService tipsService;


    @GetMapping("/reading-tips")
    public ResponseEntity<List<ReadingTips>> getAllTips() {
        System.out.println("Endpoint /reading-tips called");
        List<ReadingTips> tips = tipsService.getAllReadingTips();
        System.out.println("Tips found: " + tips.size());
        return ResponseEntity.ok(tips);
    }


    @GetMapping("/tips-summary")
    public ResponseEntity<Map<String, TipDTO>> getTipsSummary() {
        Map<String, TipDTO> tips = tipsService.getOneTipEachSkill();
        return ResponseEntity.ok(tips);
    }
}
