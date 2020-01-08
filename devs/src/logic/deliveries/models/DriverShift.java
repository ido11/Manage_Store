package logic.deliveries.models;

import java.sql.Date;

public class DriverShift {
    private Date date;
    private Driver driver;
    private String type;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DriverShift(Date date, Driver driver, String type) {
        this.date = date;
        this.driver = driver;
        this.type = type;
    }

    public String toString(){
        return "\ndate: " + date.toString() + " \ndriver:" + driver.toString() + " \ntype: " + type;
    }
}
