package web.ielts.Auth;


import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;
    private boolean premium;
    private String googleID;

    public User() {
    }
    @Override
    public String toString() {
        return "User [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", password=" + password
                + ", role=" + role + ", premium=" + premium + ", googleID=" + googleID + ", createdAt=" + createdAt
                + ", getFirstName()=" + getFirstName() + ", getLastName()=" + getLastName() + ", getEmail()="
                + getEmail() + ", getPassword()=" + getPassword() + ", getRole()=" + getRole() + ", isPremium()="
                + isPremium() + ", getGoogleID()=" + getGoogleID() + ", getCreatedAt()=" + getCreatedAt()
                + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
                + "]";
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public String getGoogleID() {
        return googleID;
    }

    public void setGoogleID(String googleID) {
        this.googleID = googleID;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    private String createdAt;

    public User(String createdAt, String email, String firstName, String googleID, String lastName, String password, boolean premium, String role) {
        this.createdAt = createdAt;
        this.email = email;
        this.firstName = firstName;
        this.googleID = googleID;
        this.lastName = lastName;
        this.password = password;
        this.premium = premium;
        this.role = role;
    }

}