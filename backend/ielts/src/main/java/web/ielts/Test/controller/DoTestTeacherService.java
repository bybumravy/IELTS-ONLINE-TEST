package web.ielts.Test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web.ielts.Test.model.answer.writing.WritingAnswer;
import web.ielts.Test.repository.answer.WritingAnswerRepository;

import java.util.Optional;
@Service
public class DoTestTeacherService {
    @Autowired
    private WritingAnswerRepository writingAnswerRepository;
    public Optional<WritingAnswer> getWritingAnswerByTestId(String testId) {
        System.out.println(writingAnswerRepository.findById(testId).toString());
        return writingAnswerRepository.findById(testId);
    }
    public WritingAnswer saveWritingAnswer(WritingAnswer writingAnswer) {
        System.out.println(writingAnswerRepository.save(writingAnswer));
        return writingAnswerRepository.save(writingAnswer);
    }
}
