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
    public List<UserDTO> getUsersByRole(@PathVariable String role) {

        return adminService.getUsersByRole(role);
    }






    @PutMapping("/updateuser")
    public ResponseEntity<?> updateUser(@RequestBody Map<String, Object> data) {
        System.out.println("dang update");

        String email = (String) data.get("email");
        List<String> roles = (List<String>) data.get("roles"); // 👈 nhận danh sách roles
        System.out.println(roles);
        adminService.updateUser(email, roles);
        return ResponseEntity.ok("Updated");
    }
    @PostMapping("/addrole")
    public ResponseEntity<?> addRole(@RequestBody Map<String, Object> data) {
        System.out.println("dang add");

        String email = (String) data.get("email");
        List<String> roles = (List<String>) data.get("roles"); // 👈 nhận danh sách roles
        System.out.println(roles);
        adminService.updateUser(email, roles);
        return ResponseEntity.ok("Updated");
    }
    @DeleteMapping("/deleterole")
    public ResponseEntity<?> deleteRole(@RequestBody Map<String, Object> data) {
        System.out.println("dang delete");

        String email = (String) data.get("email");
        String roleToDelete = (String)data.get("roleToDelete");
        List<String> roles = (List<String>) data.get("roles"); // 👈 nhận danh sách roles
        System.out.println(roles);
        adminService.updateUser(email, roles);
        return ResponseEntity.ok("Updated");
    }
}
