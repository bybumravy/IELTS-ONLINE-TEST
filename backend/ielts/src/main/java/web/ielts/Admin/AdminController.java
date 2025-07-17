package web.ielts.Admin;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.ielts.User.User;
import web.ielts.User.UserDTO;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/getuser")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @GetMapping("/{role}")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable String role) {
        try {
            System.out.println("Fetching users with role: " + role);
            List<UserDTO> users = adminService.findByRole(role);
            System.out.println("Found " + users.size() + " users");
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            e.printStackTrace(); // In stacktrace để xem lỗi gì
            return ResponseEntity.status(500).build();
        }
    }






    @PutMapping("/updateuser")
    public ResponseEntity<?> updateUser(@RequestBody Map<String, Object> data) {
        String email = (String) data.get("email");
        String role =  (String) data.get("role");
        // Map sang UserDTO
        adminService.updateUser(email,role);

        return ResponseEntity.ok("Updated");
    }

}
