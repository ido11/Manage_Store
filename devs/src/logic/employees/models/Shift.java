package logic.employees.models;

import java.util.Date;

public class Shift {
    private int managerID;
    private Date dateTime;
    private int shiftID;
    private String shiftTime;
    private int branchID;

    public Shift(int managerID, Date dateTime, int shiftID, String shiftTime, int branchID) {
        this.shiftID = shiftID;
        this.managerID = managerID;
        this.dateTime = dateTime;
        this.shiftTime = shiftTime;
        this.branchID = branchID;
    }
    public Shift(int shiftID) {
        this.shiftID = shiftID;
    }
    public Shift(int managerID, Date dateTime, String shiftTime) {
        this.managerID = managerID;
        this.dateTime = dateTime;
        this.shiftTime = shiftTime;
    }


    public int getShiftID() {
        return shiftID;
    }

    public void setShiftID(int shiftID) {
        this.shiftID = shiftID;
    }

    public int getManagerID() {
        return managerID;
    }

    public void setManagerID(int managerID) {
        this.managerID = managerID;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getShiftTime() {
        return shiftTime;
    }

    public void setShiftTime(String shiftTime) {
        this.shiftTime = shiftTime;
    }

    public int getBranchID() {
        return branchID;
    }

    public void setBranchID(int branchID) {
        this.branchID = branchID;
    }
}
