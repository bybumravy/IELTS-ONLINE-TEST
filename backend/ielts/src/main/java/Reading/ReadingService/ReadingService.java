package Reading.ReadingService;

import org.springframework.beans.factory.annotation.Autowired;

import Reading.ReadingModel.Reading;
import Reading.ReadingRepositry.ReadingRepositry.ReadingRepository;

public class ReadingService {
    @Autowired
    private ReadingRepository readingRepository;

    public Reading getReadingByTestId(String testId) {
        return readingRepository.findByTestId(testId);
    }
}
