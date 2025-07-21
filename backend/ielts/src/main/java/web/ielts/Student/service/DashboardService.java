package web.ielts.Student.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web.ielts.Student.repository.StudentResultRepository;
import web.ielts.Student.model.StudentResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private StudentResultRepository repository;

    public List<Object> getTop20Students() {
        return repository.getTop20Students();
    }

    public Map<String, List<StudentResult>> getTop3EachSkill() {
        Map<String, List<StudentResult>> result = new HashMap<>();
        for (String skill : new String[]{"writing", "listening", "speaking", "reading"}) {
            result.put(skill, repository.getTop3BySkill(skill));
        }
        return result;
    }
}
