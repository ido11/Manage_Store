package logic.deliveries.models;

import logic.employees.models.Employee;
import logic.employees.models.Employee;

import java.util.Date;

public class Driver extends Employee {
    private String license;

    public Driver(int id, String firstName, String lastName, int salary, Date firstEmployed, String employmentCond,String license){
        super(id,firstName,lastName,salary,firstEmployed,employmentCond);
        this.license = license;
    }


    public String getLicense(){
        return license;
    }


    public void setLicense(String license){
        this.license = license;
    }

    public String toString(){
        return "\nID: " + getKey() + " \nlicense: " + license + " \nname: " + getFirstName()+" "+getLastName();
    }
}
