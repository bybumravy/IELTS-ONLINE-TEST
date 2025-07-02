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




    @PostMapping("/createuser")
    public ResponseEntity<Object> createUser(@RequestBody UserDTO user) {
        try {
            UserDTO createdUser = adminService.createUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create user: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PutMapping("updateuser")
    public ResponseEntity<?> updateUser(@RequestBody Map<String, Object> data) {
        String originalEmail = (String) data.get("originalEmail");

        // Map sang UserDTO
        UserDTO user = new UserDTO();
        user.setEmail((String) data.get("email"));
        user.setFirstName((String) data.get("firstName"));
        user.setLastName((String) data.get("lastName"));
        user.setPhone((String) data.get("phone"));
        user.setGender((String) data.get("gender"));
        user.setCountry((String) data.get("country"));
        user.setTimeZone((String) data.get("timeZone"));
        user.setCuurency((String) data.get("cuurency"));
        user.setPremium(Boolean.parseBoolean(data.get("premium").toString()));
        user.setBirthDate((String) data.get("birthDate"));
        user.setRole((String) data.get("role"));

        // Gọi service
        adminService.updateUser(originalEmail, user);

        return ResponseEntity.ok("Updated");
    }
    @DeleteMapping("/deleteuser/{email}")
    public ResponseEntity<?> deleteUser(@PathVariable String email) {
        try {
            adminService.deleteUserByEmail(email);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete user: " + e.getMessage());
        }
    }
}
