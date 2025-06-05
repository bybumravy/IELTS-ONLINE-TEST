package web.ielts.Tips.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web.ielts.Tips.dto.TipDTO;
import web.ielts.Tips.model.ListeningTips;
import web.ielts.Tips.model.ReadingTips;
import web.ielts.Tips.repository.ListeningTipRepository;
import web.ielts.Tips.repository.ReadingTipRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TipsService {
    @Autowired
    private ListeningTipRepository listeningTipRepo;
//    @Autowired
//    private SpeakingTipRepository speakingTipRepo;
    @Autowired
    private ReadingTipRepository readingTipRepo;
//    @Autowired
//    private WritingTipRepository writingTipRepo;
    private TipDTO mapToDTO(ListeningTips tip) {
    return new TipDTO(tip.getId(), tip.getSkill(), tip.getType(), tip.getDescription());
}
//    private TipDTO mapToDTO(SpeakingTip tip) {
//        return new TipDTO(tip.getId(), tip.getSkill(), tip.getTitle(), tip.getDescription());
//    }
    private TipDTO mapToDTO(ReadingTips tip) {
        return new TipDTO(tip.getId(), tip.getSkill(), tip.getType(), tip.getDescription());
    }
//    private TipDTO mapToDTO(WritingTip tip) {
//        return new TipDTO(tip.getId(), tip.getSkill(), tip.getTitle(), tip.getDescription());
//    }

public List<ReadingTips> getAllReadingTips() {
    return readingTipRepo.findAll();
}
    public Map<String, TipDTO> getOneTipEachSkill() {
        Map<String, TipDTO> tips = new HashMap<>();

        listeningTipRepo.findTopByOrderByIdDesc()
                .ifPresent(tip -> tips.put("listeningTip", mapToDTO(tip)));

//        speakingTipRepo.findTopByOrderByCreatedAtDesc()
//                .ifPresent(tip -> tips.put("speakingTip", mapToDTO(tip)));

        readingTipRepo.findTopByOrderByIdDesc()
                .ifPresent(tip -> tips.put("readingTip", mapToDTO(tip)));

//        writingTipRepo.findTopByOrderByCreatedAtDesc()
//                .ifPresent(tip -> tips.put("writingTip", mapToDTO(tip)));

        return tips;
    }
}
