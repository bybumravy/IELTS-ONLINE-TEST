package web.ielts.Tips.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import web.ielts.Tips.model.ListeningTips;

import java.util.Optional;

public interface ListeningTipRepository extends MongoRepository<ListeningTips, String> {
    Optional<ListeningTips> findTopByOrderByIdDesc();
}
