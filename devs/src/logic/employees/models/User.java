package logic.employees.models;

/**
 * A DTO entity that represents a user.
 * Used mainly to communicate between logic and persistence layers
 */
public class User {

    //Fields
    private boolean master;
    private boolean HR;
    private boolean TM;
    private boolean driver;
    private String username;
    private String password;
    private int id;
    private String firstName;
    private String lastName;

    //Constructors
    public User(String username, String password, int id, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.master = false;
        this.HR = false;
        this.TM = false;
    }

    public User(String username)
    {
        this.username = username;
    }

    //Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setMaster(boolean master) {this.master = master; }

    public void setHR(boolean HR) {this.HR = HR; }

    public boolean isMaster() { return master; }

    public boolean isHR() { return HR; }

    public boolean isTM() { return TM; }

    public void setTM(boolean TM) {
        this.TM = TM;
    }

    public void setDriver(boolean driver) {
        this.driver = driver;
    }

    public boolean isDriver() {
        return driver;
    }
}
