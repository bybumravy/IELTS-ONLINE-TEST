package web.ielts.Tips.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web.ielts.Tips.dto.TipDTO;
import web.ielts.Tips.model.ListeningTips;
import web.ielts.Tips.model.ReadingTips;
import web.ielts.Tips.model.SpeakingTips;
import web.ielts.Tips.model.WritingTips;
import web.ielts.Tips.repository.ListeningTipRepository;
import web.ielts.Tips.repository.ReadingTipRepository;
import web.ielts.Tips.repository.SpeakingTipRepository;
import web.ielts.Tips.repository.WritingTipRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TipsService {
    @Autowired
    private ListeningTipRepository listeningTipRepo;
    @Autowired
    private SpeakingTipRepository speakingTipRepo;
    @Autowired
    private ReadingTipRepository readingTipRepo;
    @Autowired
    private WritingTipRepository writingTipRepo;

    private TipDTO mapToDTO(ListeningTips tip) {
    return new TipDTO(tip.getId(), tip.getSkill(), tip.getType(), tip.getDescription());
}
    private TipDTO mapToDTO(SpeakingTips tip) {
        return new TipDTO(tip.getId(), tip.getSkill(), tip.getType(), tip.getDescription());
    }
    private TipDTO mapToDTO(ReadingTips tip) {
        return new TipDTO(tip.getId(), tip.getSkill(), tip.getType(), tip.getDescription());
    }
    private TipDTO mapToDTO(WritingTips tip) {
        return new TipDTO(tip.getId(), tip.getSkill(), tip.getType(), tip.getDescription());
    }
    public List<ReadingTips> getAllTipsReading() {
        return readingTipRepo.findAll();
    }

    public List<ListeningTips> getAllTipsListening() {
        return listeningTipRepo.findAll();
    }

    public List<WritingTips> getAllTipsWriting() {
        return writingTipRepo.findAll();
    }

    public List<SpeakingTips> getAllTipsSpeaking() {
        return speakingTipRepo.findAll();
    }

    public List<ReadingTips> getTipsByReadingSkill(String skill) {
        return readingTipRepo.findBySkill(skill);
    }
    public List<ListeningTips> getTipsByListeningSkill(String skill) {
        return listeningTipRepo.findBySkill(skill);
    }

    public List<WritingTips> getTipsByWritingSkill(String skill) {
        return writingTipRepo.findBySkill(skill);
    }

    public List<SpeakingTips> getTipsBySpeakingSkill(String skill) {
        return speakingTipRepo.findBySkill(skill);
    }


    public ReadingTips getTipByIdReading(String id) {
        return readingTipRepo.findById(id).orElse(null);
    }

    public ListeningTips getTipByListening(String id) {
        return listeningTipRepo.findById(id).orElse(null);
    }

    public WritingTips getTipByWriting(String id) {
        return writingTipRepo.findById(id).orElse(null);
    }

    public SpeakingTips getTipBySpeaking(String id) {
        return speakingTipRepo.findById(id).orElse(null);
    }
public List<ReadingTips> getAllReadingTips() {
    return readingTipRepo.findAll();
}
    public Map<String, TipDTO> getOneTipEachSkill() {
        Map<String, TipDTO> tips = new HashMap<>();

        listeningTipRepo.findTopByOrderByIdDesc()
                .ifPresent(tip -> tips.put("listeningTip", mapToDTO(tip)));

        speakingTipRepo.findTopByOrderByIdDesc()
                .ifPresent(tip -> tips.put("speakingTip", mapToDTO(tip)));

        readingTipRepo.findTopByOrderByIdDesc()
                .ifPresent(tip -> tips.put("readingTip", mapToDTO(tip)));

        writingTipRepo.findTopByOrderByIdDesc()
                .ifPresent(tip -> tips.put("writingTip", mapToDTO(tip)));

        return tips;
    }
}
