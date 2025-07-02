package web.ielts.Test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web.ielts.Test.model.answer.listening.ListeningAnswer;
import web.ielts.Test.repository.answer.ListeningAnswerRepository;

import java.util.Optional;

    @Service
    public class ResultService {
        @Autowired
        private ListeningAnswerRepository listeningAnswerRepository;
        public ListeningAnswer saveAnswer(ListeningAnswer answer) {
            return listeningAnswerRepository.save(answer); // trả về answer có ID
        }

        public Optional<ListeningAnswer> findById(String answerId) {
            return listeningAnswerRepository.findById(answerId);
        }
        public Optional<ListeningAnswer> getListeningResult(String testId, String username) {
            // Giả sử mỗi user chỉ có 1 answer cho 1 testId
            return listeningAnswerRepository.findByUsername(username)
                    .stream()
                    .filter(ans -> ans.getTestId().equals(testId))
                    .findFirst();
        }
        // TODO: Thêm các hàm cho Reading, Writing, Speaking tương tự
    }