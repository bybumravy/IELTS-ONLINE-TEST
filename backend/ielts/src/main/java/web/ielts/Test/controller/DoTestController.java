package web.ielts.Test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.ielts.Test.model.Listening;
import web.ielts.Test.model.Reading;
import web.ielts.Test.model.Writing;
import web.ielts.Test.model.answer.ReadingAnswer;
import web.ielts.Test.model.answer.writing.EvaluationWritingAnswer;
import web.ielts.Test.model.answer.writing.WritingAIResponse;
import web.ielts.Test.model.answer.writing.WritingAnswer;
import web.ielts.Test.repository.ListeningRepository;
import web.ielts.Test.repository.ReadingRepository;
import web.ielts.Test.repository.WritingRepository;
import web.ielts.Test.repository.answer.ReadingAnswerRepository;
import web.ielts.Test.repository.answer.WritingAnswerRepository;
import web.ielts.Test.service.AIService;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class DoTestController {

    @Autowired
    private WritingRepository writingRepository;

    @Autowired
    private ReadingAnswerRepository readingAnswerRepository;

    @Autowired
    private WritingAnswerRepository writingAnswerRepository;

    @Autowired
    private ListeningRepository listeningRepository;

    @Autowired
    private ReadingRepository readingRepository;

    @Autowired
    private AIService aiService;

    @GetMapping("writing/{testId}")
    public ResponseEntity<Writing> getWritingByTestId(@PathVariable String testId) {
        Optional<Writing> writingOptional = writingRepository.findById(testId);
        return writingOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/tests")
    public ResponseEntity<List<Listening>> getAllTests() {
        return ResponseEntity.ok(listeningRepository.findAll());
    }

    @GetMapping("/listening/{testId}")
    public ResponseEntity<Listening> getListeningByTestId(@PathVariable String testId) {
        Listening listening = listeningRepository.findByTestId(testId);
        return listening != null ? ResponseEntity.ok(listening) : ResponseEntity.notFound().build();
    }

    @GetMapping("/reading/{testId}")
    public ResponseEntity<Reading> getTestBySkillAndId(@PathVariable String testId) {
        Reading reading = readingRepository.findByTestId(testId);
        return reading != null ? ResponseEntity.ok(reading) : ResponseEntity.notFound().build();
    }

    @PostMapping("/answer")
    public ReadingAnswer saveAnswer(@RequestBody ReadingAnswer answer) {
        return readingAnswerRepository.save(answer);
    }

    @PostMapping("/writing/submit")
    public ResponseEntity<WritingAnswer> saveAnswer(@RequestBody WritingAnswer answer) {

        WritingAnswer savedAnswer = writingAnswerRepository.save(answer);


//        String feedback = aiService.getFeedback(answer.getTask2().getAnswer());
//        System.out.println("General Feedback: " + feedback);


        var task1 = savedAnswer.getTask1();
        try {
            WritingAIResponse eval1 = aiService.WritingTask1(
                    task1.getQuestion(),
                    task1.getAnswer()
            );


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
            System.err.println("Error processing Task 1: " + e.getMessage());
            task1.setFeedback("Error getting AI evaluation: " + e.getMessage());
        }


        var task2 = savedAnswer.getTask2();
        try {
            WritingAIResponse eval2 = aiService.WritingTask2(
                    task2.getQuestion(),
                    task2.getAnswer()
            );


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
            System.err.println("Error processing Task 2: " + e.getMessage());
            task2.setFeedback("Error getting AI evaluation: " + e.getMessage());
        }


        WritingAnswer finalAnswer = writingAnswerRepository.save(savedAnswer);

        return ResponseEntity.ok(finalAnswer);
    }

}
