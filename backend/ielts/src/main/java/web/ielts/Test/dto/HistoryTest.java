package web.ielts.Test.dto;

public class HistoryTest {
    private String testID;
    private String username;
    private String skill;

    public HistoryTest(String testID, String username, String skill) {
        this.testID = testID;
        this.username = username;
        this.skill = skill;
    }

    public String getTestID() {
        return testID;
    }

    public HistoryTest() {
    }

    public void setTestID(String testID) {
        this.testID = testID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    @Override
    public String toString() {
        return "HistoryTest{" +
                "testID='" + testID + '\'' +
                ", username='" + username + '\'' +
                ", skill='" + skill + '\'' +
                '}';
    }
}
