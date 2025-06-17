package web.ielts.Practice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.ielts.Practice.model.*;
import web.ielts.Practice.service.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/vocabulary")
public class VocabularyController {
    @Autowired
    private VocabularyService vocabularyService;

    @GetMapping
    public ResponseEntity<List<Vocabulary>> getAllVocabularies() {
        return ResponseEntity.ok(vocabularyService.getAllVocabularies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vocabulary> getVocabularyById(@PathVariable String id) {
        Optional<Vocabulary> vocabulary = vocabularyService.getVocabularyById(id);
        return vocabulary.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Vocabulary> addVocabulary(@RequestBody Vocabulary vocabulary) {
        return ResponseEntity.ok(vocabularyService.addVocabulary(vocabulary));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vocabulary> updateVocabulary(@PathVariable String id, @RequestBody Vocabulary vocabulary) {
        return ResponseEntity.ok(vocabularyService.updateVocabulary(id, vocabulary));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVocabulary(@PathVariable String id) {
        vocabularyService.deleteVocabulary(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Vocabulary>> filterVocabularies(
            @RequestParam(required = false) Topic topic,
            @RequestParam(required = false) Band band
    ) {
        if (topic != null && band != null) {
            return ResponseEntity.ok(vocabularyService.getByTopicAndBand(topic, band));
        } else if (topic != null) {
            return ResponseEntity.ok(vocabularyService.getByTopic(topic));
        } else if (band != null) {
            return ResponseEntity.ok(vocabularyService.getByBand(band));
        } else {
            return ResponseEntity.ok(vocabularyService.getAllVocabularies());
        }
    }
}