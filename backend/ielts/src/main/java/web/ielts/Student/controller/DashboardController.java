package web.ielts.Student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import web.ielts.service.DashboardService;
import web.ielts.Student.model.StudentResult;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/top20")
    public List<Object> getTop20() {
        return dashboardService.getTop20Students();
    }

    @GetMapping("/top3-skills")
    public Map<String, List<StudentResult>> getTop3EachSkill() {
        return dashboardService.getTop3EachSkill();
    }
}
