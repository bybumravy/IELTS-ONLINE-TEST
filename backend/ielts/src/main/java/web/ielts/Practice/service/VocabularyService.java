package web.ielts.Practice.service;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import  web.ielts.Practice.model.*;
import  web.ielts.Practice.repository.*;

import java.util.List;
import java.util.Optional;

@Service
public class VocabularyService {
    @Autowired
    private VocabularyRepository vocabularyRepository;

    public List<Vocabulary> getAllVocabularies() {
        return vocabularyRepository.findAll();
    }

    public Optional<Vocabulary> getVocabularyById(String id) {
        return vocabularyRepository.findById(id);
    }

    public Vocabulary addVocabulary(Vocabulary vocabulary) {
        return vocabularyRepository.save(vocabulary);
    }

    public void deleteVocabulary(String id) {
        vocabularyRepository.deleteById(id);
    }

    public Vocabulary updateVocabulary(String id, Vocabulary vocabulary) {
        vocabulary.setId(id);
        return vocabularyRepository.save(vocabulary);
    }

//    // Truy vấn theo topic
//    public List<Vocabulary> getByTopic(String topic) {
//        return vocabularyRepository.findByTopic(topic);
//    }
//
//    // Truy vấn theo band
//    public List<Vocabulary> getByBand(String band) {
//        return vocabularyRepository.findByBand(band);
//    }
//
//    // Truy vấn theo cả topic và band
//    public List<Vocabulary> getByTopicAndBand(String topic, String band) {
//        return vocabularyRepository.findByTopicAndBand(topic, band);
//    }
    // Phân trang & tìm kiếm theo từ khóa, topic, band
    public Page<Vocabulary> searchAndPaginate(String keyword, String topic, String band, int page, int size) {
        return vocabularyRepository
                .findByWordContainingIgnoreCaseAndTopicContainingIgnoreCaseAndBandContainingIgnoreCase(
                        keyword == null ? "" : keyword,
                        topic == null ? "" : topic,
                        band == null ? "" : band,
                        PageRequest.of(page, size)
                );
    }

}