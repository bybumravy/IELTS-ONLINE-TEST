package Reading.ReadingRepositry;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import Reading.ReadingModel.Reading;

public class ReadingRepositry {
@Repository
public interface ReadingRepository extends MongoRepository<Reading, String> {
    Reading findByTestId(String testId);
}
}
