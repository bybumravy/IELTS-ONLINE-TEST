package web.ielts.History.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import web.ielts.History.dto.HistoryTest;
import web.ielts.History.service.HistoryService;

import java.util.List;

@Controller
@RequestMapping("/history")
public class HistoryController {

    @Autowired
    private HistoryService historyService;



    @GetMapping("/listening/{username}")
    public List<HistoryTest> getListeningAnswerByTestId(@PathVariable String username) {
        return historyService.getListeningByUsername(username);
    }
    @GetMapping("/reading/{username}")
    public List<HistoryTest> getReadingAnswerByTestId(@PathVariable String username) {
        return historyService.getReadingByUsername(username);
    }
    @GetMapping("/writing/{username}")
    public List<HistoryTest> getWritingAnswerByTestId(@PathVariable String username) {
        return historyService.getWritingByUsername(username);
    }
    @GetMapping("/speaking/{username}")
    public List<HistoryTest> getSpeakingAnswerByTestId(@PathVariable String username) {
        return historyService.getSpeakingByUsername(username);
    }

}
