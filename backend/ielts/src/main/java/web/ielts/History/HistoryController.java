package web.ielts.History;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    @Autowired
    private DoTestService doTestService;

    @GetMapping("/{username}")
    public ResponseEntity<List<HistoryTest>> getHistory(@PathVariable String username) {
        try {
            List<HistoryTest> history = doTestService.getHistoryByUsername(username);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }
    
}
