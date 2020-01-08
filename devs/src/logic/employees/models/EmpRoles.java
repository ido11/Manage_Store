package logic.employees.models;

public class EmpRoles {

    //Fields
    private Pair<Integer,Integer> key; // <empID,roleID>
    private String roleDescription;

    //Constructors
    public EmpRoles(int empID, int roleID, String roleDesc) {
        this.key = new Pair<>(empID, roleID);
        this.roleDescription = roleDesc;
    }

    public EmpRoles() {
        this.key = new Pair<>();
    }

    //Getters and Setters
    public int getEmpId() {
        return this.key.getFirst().intValue();
    }

    public int getRoleId() {
        return this.key.getSecond().intValue();
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setEmpID(int id) {
        this.key.setFirst(id);
    }

    public void setRoleID(int id) {
        this.key.setSecond(id);
    }


    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

    public Pair<Integer, Integer> getKey() {
        return key;
    }

}