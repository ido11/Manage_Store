package logic.employees.models;

public class Role {

    private int roleID;
    private String description;

    public Role(int roleID, String description) {
        this.roleID = roleID;
        this.description = description;
    }

    public int getRoleID() {
        return roleID;
    }

    public void setRoleID(int roleID) {
        this.roleID = roleID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
