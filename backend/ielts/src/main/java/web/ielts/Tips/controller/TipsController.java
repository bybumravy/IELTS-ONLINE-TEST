package web.ielts.Tips.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import web.ielts.Tips.dto.TipDTO;
import web.ielts.Tips.service.TipsService;

import java.util.Map;
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class TipsController {
    @Autowired
    private TipsService tipsService;

    @GetMapping("/tips-summary")
    public ResponseEntity<Map<String, TipDTO>> getTipsSummary() {
        Map<String, TipDTO> tips = tipsService.getOneTipEachSkill();
        return ResponseEntity.ok(tips);
    }
}
