package web.ielts.User;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "user")
public class UserDTO {

    private String email;
    private List<String> roles;

    public UserDTO(String email, List<String> roles) {
        this.email = email;
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getRoles() {
        return roles;
    }
    public UserDTO() {
    }


}