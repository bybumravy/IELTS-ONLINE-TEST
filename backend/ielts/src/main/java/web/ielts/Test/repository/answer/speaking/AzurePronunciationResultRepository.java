package web.ielts.Test.repository.answer.speaking;

import org.springframework.data.mongodb.repository.MongoRepository;
import web.ielts.Test.model.answer.speaking.AzurePronunciationResult;

public interface AzurePronunciationResultRepository extends MongoRepository<AzurePronunciationResult, String> {
    // Có thể thêm các hàm truy vấn nếu cần
} 