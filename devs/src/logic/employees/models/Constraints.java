package logic.employees.models;

import java.util.Date;
import java.text.SimpleDateFormat;

public class Constraints {
    // Fields
    private int empID;
    private String shiftTime;
    private Date date;


    //Constructor


    public int getEmpID() {
        return empID;
    }

    public void setEmpID(int empID) {
        this.empID = empID;
    }

    public String getShiftTime() {
        return shiftTime;
    }

    public void setShiftTime(String shiftTime) {
        this.shiftTime = shiftTime;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Constraints(int empID, String shiftTime, Date date) {
        this.empID = empID;
        this.shiftTime = shiftTime;
        this.date = date;
    }
}
