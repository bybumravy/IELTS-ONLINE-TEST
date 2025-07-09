package web.ielts.Test.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import web.ielts.Test.service.IpaScraperService;

import java.util.Map;

@RestController
@RequestMapping("/api/ipa")
public class IpaController {

    private final IpaScraperService scraperService = new IpaScraperService();

    @GetMapping("/{word}")
    public ResponseEntity<Map<String, String>> getIpa(@PathVariable String word) {
        return scraperService.getIpa(word)
                .map(ipa -> ResponseEntity.ok(Map.of("word", word, "ipa", ipa)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("word", word, "error", "No IPA found")));
    }
}
