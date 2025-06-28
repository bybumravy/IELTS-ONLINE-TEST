package web.ielts.User;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user")
public class UserDTO {

    private String firstName;
    private String lastName;
    @Id
    private String email;
    private String role;
    private boolean premium;
    private String birthDate;
    private String gender;
    private String phone;
    private String createdAt;
    private String country;
    private String timeZone;
    private String cuurency;
    private String userName;
    public UserDTO() {
    }

    public UserDTO(String firstName, String lastName, String email, String role, boolean premium, String birthDate, String gender, String phone, String createdAt, String country, String timeZone, String cuurency) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.premium = premium;
        this.birthDate = birthDate;
        this.gender = gender;
        this.phone = phone;
        this.createdAt = createdAt;
        this.country = country;
        this.timeZone = timeZone;
        this.cuurency = cuurency;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getCuurency() {
        return cuurency;
    }

    public void setCuurency(String cuurency) {
        this.cuurency = cuurency;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", premium=" + premium +
                ", birthDate='" + birthDate + '\'' +
                ", gender='" + gender + '\'' +
                ", phone='" + phone + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", country='" + country + '\'' +
                ", timeZone='" + timeZone + '\'' +
                ", cuurency='" + cuurency + '\'' +
                '}';
    }
}