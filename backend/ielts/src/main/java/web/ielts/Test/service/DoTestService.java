package web.ielts.Test.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import web.ielts.Test.model.Listening;
import web.ielts.Test.model.Reading;
import web.ielts.Test.model.Writing;
import web.ielts.Test.model.answer.listening.ListeningAnswer;
import web.ielts.Test.model.answer.reading.ReadingAnswer;
import web.ielts.Test.model.answer.writing.EvaluationWritingAnswer;
import web.ielts.Test.model.answer.writing.WritingAIResponse;
import web.ielts.Test.model.answer.writing.WritingAnswer;
import web.ielts.Test.repository.ListeningRepository;
import web.ielts.Test.repository.ReadingRepository;
import web.ielts.Test.repository.WritingRepository;
import web.ielts.Test.repository.answer.ListeningAnswerRepository;
import web.ielts.Test.repository.answer.ReadingAnswerRepository;
import web.ielts.Test.repository.answer.WritingAnswerRepository;

import java.util.List;
import java.util.Optional;

@Service
public class DoTestService {
    @Autowired
    private WritingRepository writingRepository;

    @Autowired
    private ListeningRepository listeningRepository;

    @Autowired
    private ReadingRepository readingRepository;

    @Autowired
    private ReadingAnswerRepository readingAnswerRepository;

    @Autowired
    private WritingAnswerRepository writingAnswerRepository;

    @Autowired
    private ListeningAnswerRepository listeningAnswerRepository;

    @Autowired
    private AIService aiService;

    public Optional<Writing> getWritingByTestId(String testId) {
        return writingRepository.findById(testId);
    }

    public List<Listening> getAllListeningTests() {
        return listeningRepository.findAll();
    }

    public Listening getListeningByTestId(String testId) {
        return listeningRepository.findByTestId(testId);
    }

    public Reading getReadingByTestId(String testId) {
        return readingRepository.findByTestId(testId);
    }

    public ReadingAnswer saveReadingAnswer(ReadingAnswer answer) {
        return readingAnswerRepository.save(answer);
    }

    public ListeningAnswer saveListeningAnswer(ListeningAnswer answer) {
        return listeningAnswerRepository.save(answer);
    }
    public WritingAnswer saveWritingAnswer(WritingAnswer answer) {
        WritingAnswer savedAnswer = writingAnswerRepository.save(answer);

        var task1 = savedAnswer.getTask1();
        try {
            WritingAIResponse eval1 = aiService.WritingTask1(task1.getQuestion(), task1.getAnswer());
            task1.setFeedback(eval1.getFeedback());
            task1.setSampleAnswer(eval1.getSampleAnswer());
            EvaluationWritingAnswer evaluation1 = eval1.getEvaluation();
            EvaluationWritingAnswer task1Eva = task1.getEvaluation();
            if (evaluation1 != null && task1Eva != null) {
                task1Eva.setTaskAchievement(evaluation1.getTaskAchievement());
                task1Eva.setCoherenceCohesion(evaluation1.getCoherenceCohesion());
                task1Eva.setLexicalResource(evaluation1.getLexicalResource());
                task1Eva.setGrammar(evaluation1.getGrammar());
            }
        } catch (Exception e) {
            task1.setFeedback("Error getting AI evaluation: " + e.getMessage());
        }

        var task2 = savedAnswer.getTask2();
        try {
            WritingAIResponse eval2 = aiService.WritingTask2(task2.getQuestion(), task2.getAnswer());
            task2.setFeedback(eval2.getFeedback());
            task2.setSampleAnswer(eval2.getSampleAnswer());
            EvaluationWritingAnswer evaluation2 = eval2.getEvaluation();
            EvaluationWritingAnswer task2Eva = task2.getEvaluation();
            if (evaluation2 != null && task2Eva != null) {
                task2Eva.setTaskAchievement(evaluation2.getTaskAchievement());
                task2Eva.setCoherenceCohesion(evaluation2.getCoherenceCohesion());
                task2Eva.setLexicalResource(evaluation2.getLexicalResource());
                task2Eva.setGrammar(evaluation2.getGrammar());
            }
        } catch (Exception e) {
            task2.setFeedback("Error getting AI evaluation: " + e.getMessage());
        }

        return writingAnswerRepository.save(savedAnswer);
    }
}
