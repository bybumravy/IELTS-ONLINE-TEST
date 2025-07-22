package web.ielts.Student.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import web.ielts.Student.model.StudentResult;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.*;

@Service
public class DashboardService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // Lấy Top 20 sinh viên có totalCorrect cao nhất (gộp mọi kỹ năng)
    public List<StudentResult> getTop10Students() {
        List<StudentResult> all = new ArrayList<>();

        for (String collection : List.of("ListeningAnswer", "ReadingAnswer")) {
            List<StudentResult> results = mongoTemplate.findAll(StudentResult.class, collection);
            // Lọc bỏ các bản ghi bị thiếu totalCorrect
            results = results.stream()
                    .filter(r -> r.getTotalCorrect() != null)
                    .toList();
            all.addAll(results);
        }

        all.sort(Comparator.comparingInt(StudentResult::getTotalCorrect).reversed());
        return all.stream().limit(10).toList(); // ✅ Chỉ lấy top 10
    }



    // Lấy Top 3 sinh viên mỗi kỹ năng theo band
    public Map<String, List<StudentResult>> getTop3EachSkill() {
        Map<String, List<StudentResult>> result = new HashMap<>();

        Map<String, String> skillToCollection = Map.of(
                "writing", "WritingAnswer",
                "listening", "ListeningAnswer",
                "speaking", "SpeakingAnswer",
                "reading", "ReadingAnswer"
        );

        for (String skill : skillToCollection.keySet()) {
            Query query = new Query();
            query.addCriteria(Criteria.where("skill").is(skill));
            query.with(Sort.by(Sort.Direction.DESC, "band"));
            query.limit(3);
            String collectionName = skillToCollection.get(skill);
            List<StudentResult> top3 = mongoTemplate.find(query, StudentResult.class, collectionName);
            result.put(skill, top3);
        }

        return result;
    }
}
