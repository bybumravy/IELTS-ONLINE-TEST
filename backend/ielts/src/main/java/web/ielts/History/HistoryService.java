package web.ielts.History;

import web.ielts.Test.dto.HistoryTest;
import web.ielts.Test.model.answer.listening.ListeningAnswer;
import web.ielts.Test.model.answer.reading.ReadingAnswer;
import web.ielts.Test.model.answer.speaking.SpeakingAnswer;
import web.ielts.Test.model.answer.writing.WritingAnswer;

// ... các import khác

@Service
public class HistoryService {
    @Autowired
    private ListeningAnswerRepository listeningRepo;
    @Autowired
    private ReadingAnswerRepository readingRepo;
    @Autowired
    private WritingAnswerRepository writingRepo;
    @Autowired
    private SpeakingAnswerRepository speakingRepo;

    public List<HistoryTest> getHistoryByUsername(String username) {
        List<HistoryTest> history = new ArrayList<>();
        history.addAll(getListeningByUsername(username));
        history.addAll(getReadingByUsername(username));
        history.addAll(getWritingByUsername(username));
        history.addAll(getSpeakingByUsername(username));
        return history;
    }

    public List<HistoryTest> getListeningByUsername(String username) {
        List<ListeningAnswer> answers = listeningRepo.findByUsername(username);
        List<HistoryTest> result = new ArrayList<>();
        for (ListeningAnswer ans : answers) {
            HistoryTest h = new HistoryTest();
            h.setId(ans.getId());
            h.setTestId(ans.getTestId());
            h.setTestName(ans.getTestName());
            h.setUsername(ans.getUsername());
            h.setSkill("listening");
            h.setScore(ans.getScore());
            h.setMaxScore(ans.getMaxScore());
            h.setSubmittedAt(ans.getSubmittedAt());
            result.add(h);
        }
        return result;
    }

    public List<HistoryTest> getReadingByUsername(String username) {
        List<ReadingAnswer> answers = readingRepo.findByUsername(username);
        List<HistoryTest> result = new ArrayList<>();
        for (ReadingAnswer ans : answers) {
            HistoryTest h = new HistoryTest();
            h.setTestID(ans.getTestId());
            h.setUsername(ans.getUsername());
            h.setSkill("reading");
            result.add(h);
        }
        return result;
    }

    public List<HistoryTest> getWritingByUsername(String username) {
        List<WritingAnswer> answers = writingRepo.findByUsername(username);
        List<HistoryTest> result = new ArrayList<>();
        for (WritingAnswer ans : answers) {
            HistoryTest h = new HistoryTest();
            h.setTestID(ans.getTestId());
            h.setUsername(ans.getUsername());
            h.setSkill("writing");
            result.add(h);
        }
        return result;
    }

    public List<HistoryTest> getSpeakingByUsername(String username) {
        List<SpeakingAnswer> answers = speakingRepo.findByUsername(username);
        List<HistoryTest> result = new ArrayList<>();
        for (SpeakingAnswer ans : answers) {
            HistoryTest h = new HistoryTest();
            h.setTestID(ans.getTestId());
            h.setUsername(ans.getUsername());
            h.setSkill("speaking");
            result.add(h);
        }
        return result;
    }
}
