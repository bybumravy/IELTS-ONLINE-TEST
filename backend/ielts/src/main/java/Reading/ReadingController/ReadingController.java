package Reading.ReadingController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import Reading.ReadingModel.Reading;
import Reading.ReadingService.ReadingService;




@CrossOrigin
@RequestMapping("/api")
public class ReadingController {
 @Autowired
    private ReadingService readingService;

    @GetMapping("/reading/{testId}")
    public ResponseEntity<Reading> getReading(@PathVariable String testId) {
        Reading reading = readingService.getReadingByTestId(testId);
        if (reading != null) {
            return ResponseEntity.ok(reading);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
