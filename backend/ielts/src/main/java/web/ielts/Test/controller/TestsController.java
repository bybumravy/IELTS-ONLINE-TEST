package web.ielts.Test.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import web.ielts.Test.model.Test;
import web.ielts.Test.repository.TestRepository;


@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class TestsController {

    @Autowired
    private TestRepository testRepo;


    @GetMapping("/3-tests")
    public List<Test> getThreeTests() {
        return testRepo.findAll(PageRequest.of(0, 3)).getContent();
    }


}
