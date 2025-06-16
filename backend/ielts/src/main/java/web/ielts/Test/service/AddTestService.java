package web.ielts.Test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web.ielts.Test.model.add.*;
import web.ielts.Test.repository.add.*;

@Service
public class AddTestService {

    @Autowired
    private AddTestRepository testRepository;

    @Autowired
    private AddListeningRepository listeningRepository;

    @Autowired
    private AddReadingRepository readingRepository;

    @Autowired
    private AddWritingRepository writingRepository;

    @Autowired
    private AddSpeakingRepository speakingRepository;

    public void saveFullTest(AddTestRequest request) {
        // Lưu test chính
        testRepository.save(request.getTest());

        // Gán testId cho các kỹ năng nếu chưa có
        String testId = request.getTest().getTestId();

        AddListening listening = request.getListening();
        if (listening != null) {
            listening.setTestId(testId);
            listeningRepository.save(listening);
        }

        AddReading reading = request.getReading();
        if (reading != null) {
            reading.setTestId(testId);
            readingRepository.save(reading);
        }

        AddWriting writing = request.getWriting();
        if (writing != null) {
            writing.setTestId(testId);
            writingRepository.save(writing);
        }

        AddSpeaking speaking = request.getSpeaking();
        if (speaking != null) {
            speaking.setTestId(testId);
            speakingRepository.save(speaking);
        }
    }
}
