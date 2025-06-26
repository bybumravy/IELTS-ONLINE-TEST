package web.ielts.Test.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web.ielts.Test.model.answer.listening.ListeningAnswer;
import web.ielts.Test.repository.answer.ListeningAnswerRepository;

import java.util.Optional;

@Service
public class ResultService {
    @Autowired
    private ListeningAnswerRepository repository;

    public ListeningAnswer saveAnswer(ListeningAnswer answer) {
        return repository.save(answer); // trả về answer có ID
    }

    public Optional<ListeningAnswer> findById(String answerId) {
        return repository.findById(answerId);
    }

}
