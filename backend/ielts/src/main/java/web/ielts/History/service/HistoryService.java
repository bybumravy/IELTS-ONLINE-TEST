package web.ielts.History.service;

import org.springframework.beans.factory.annotation.Autowired;
import web.ielts.History.dto.HistoryTest;
import web.ielts.Test.model.answer.listening.ListeningAnswer;
import web.ielts.Test.model.answer.reading.ReadingAnswer;
import web.ielts.Test.model.answer.speaking.SpeakingAnswer;
import web.ielts.Test.model.answer.writing.WritingAnswer;
import org.springframework.stereotype.Service;
import web.ielts.Test.repository.answer.ListeningAnswerRepository;
import web.ielts.Test.repository.answer.ReadingAnswerRepository;
import web.ielts.Test.repository.answer.SpeakingAnswerRepository;
import web.ielts.Test.repository.answer.WritingAnswerRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoryService {

    @Autowired
    private ReadingAnswerRepository readingAnswerRepository;

    @Autowired
    private WritingAnswerRepository writingAnswerRepository;

    @Autowired
    private ListeningAnswerRepository listeningAnswerRepository;
    @Autowired
    private SpeakingAnswerRepository speakingAnswerRepository;

    public List<HistoryTest> getListeningByUsername(String username) {
        List<ListeningAnswer> answers = listeningAnswerRepository.findByUsername(username);
        System.out.println("12");
        List<HistoryTest> historyTests = answers.stream().map(answer -> {
            HistoryTest history = new HistoryTest();
            history.setUsername(answer.getUsername());
            history.setSkill("listening");
            history.setTestID(answer.getTestId());
            history.setBand(answer.getBand());
            history.setSubmittedAt(answer.getSubmittedAt());
            return history;
        }).collect(Collectors.toList());
        return historyTests;
    }

    public List<HistoryTest> getWritingByUsername(String username) {
        List<WritingAnswer> answers = writingAnswerRepository.findByUsername(username);
        System.out.println("12");
        List<HistoryTest> historyTests = answers.stream().map(answer -> {
            HistoryTest history = new HistoryTest();
            history.setUsername(answer.getUsername());
            history.setSkill("writing");
            history.setTestID(answer.getTestId());
            return history;
        }).collect(Collectors.toList());
        return historyTests;
    }

    public List<HistoryTest> getSpeakingByUsername(String username) {
        List<SpeakingAnswer> answers = speakingAnswerRepository.findByUsername(username);
        System.out.println("12");
        List<HistoryTest> historyTests = answers.stream().map(answer -> {
            HistoryTest history = new HistoryTest();
            history.setUsername(answer.getUsername());
            history.setSkill("speaking");
            history.setTestID(answer.getTestId());
            return history;
        }).collect(Collectors.toList());
        return historyTests;
    }


    public List<HistoryTest> getReadingByUsername(String username) {
        List<ReadingAnswer> answers = readingAnswerRepository.findByUsername(username);
        System.out.println("12");
        List<HistoryTest> historyTests = answers.stream().map(answer -> {
            HistoryTest history = new HistoryTest();
            history.setUsername(answer.getUsername());
            history.setSkill("reading");
            history.setTestID(answer.getTestId());
            return history;
        }).collect(Collectors.toList());
        return historyTests;
    }
}
