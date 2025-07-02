package web.ielts.Auth.dto;



public class AuthDTO {
    private String email;
    private String password;
    private String fromPath;
    public AuthDTO() {
    }

    public AuthDTO(String email, String password, String fromPath) {
        this.email = email;
        this.password = password;
        this.fromPath = fromPath;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public String getFromPath() {
        return fromPath;
    }

    public void setFromPath(String fromPath) {
        this.fromPath = fromPath;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
