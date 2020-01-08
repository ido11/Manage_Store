package logic.employees.models;

import java.util.Date;

public class ShiftAssigning {
    private int shiftID;
    private int roleID;
    private int empID;
    private String roleDesc;

    public ShiftAssigning(int shiftID, int roleID, int empID, String roleDesc) {
        this.shiftID = shiftID;
        this.roleDesc = roleDesc;
        this.empID = empID;
        this.roleID = roleID;
    }

    public ShiftAssigning(int shiftID, int roleID, int empID) {
        this.shiftID = shiftID;
        this.empID = empID;
        this.roleID = roleID;
    }

    public int getKey() {
        return shiftID;
    }

    public void setKey(int shiftID, int roleID, int empID, String roleDesc) {
        this.shiftID = shiftID;
        this.roleDesc = roleDesc;
        this.empID = empID;
        this.roleID = roleID;
    }

    public int getEmpID() {
        return this.empID;
    }

    public int getRoleID() {
        return this.roleID;
    }

    public String getRoleDesc() {
        return this.roleDesc;
    }

    public int getShiftID() {
        return shiftID;
    }

    public void setShiftID(int shiftID) {
        this.shiftID = shiftID;
    }
}