package web.ielts.Tips.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import web.ielts.Tips.model.ListeningTips;
import web.ielts.Tips.model.ReadingTips;

import java.util.Optional;

public interface ReadingTipRepository extends MongoRepository<ReadingTips, String> {
    Optional<ReadingTips> findTopByOrderByIdDesc();

}
