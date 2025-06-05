package web.ielts.Tips.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import web.ielts.Test.model.Test;
import web.ielts.Test.repository.TestRepository;
import web.ielts.Tips.repository.TipsRepository;

import java.util.List;

public class TipsController {
    @Autowired
    private TipsRepository tipsRepository;


    @GetMapping("/3-tests")
    public List<Test> getThreeTests() {
        return tipsRepository.findAll(PageRequest.of(0, 2)).getContent();
    }
}
