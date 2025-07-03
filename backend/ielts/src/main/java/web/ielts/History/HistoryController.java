//package web.ielts.History;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import web.ielts.Test.dto.HistoryTest;
//import web.ielts.Test.service.DoTestService;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/history")
//public class HistoryController {
//
//    @Autowired
//    private DoTestService doTestService;
//
//    @GetMapping("/{username}")
//    public ResponseEntity<List<HistoryTest>> getHistory(@PathVariable String username) {
//        try {
//            List<HistoryTest> history = doTestService.getHistoryByUsername(username);
//            return ResponseEntity.ok(history);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body(new ArrayList<>());
//        }
//    }
//
//}
