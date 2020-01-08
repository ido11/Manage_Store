package logic.employees.models;

import java.util.Date;
import java.util.LinkedList;

public class Employee {

    //Fields
    private int key;
    private boolean HR;
    private boolean Manager;
    private boolean Driver;
    private boolean Logistics;
    private boolean StockKeeper;
    private String firstName;
    private String lastName;
    private int salary;
    private Date firstEmployed;
    private String employmentCond;
    private LinkedList<Role> roles;

    //Constructors
    public Employee(int id, String firstName, String lastName, int salary, Date firstEmployed, String employmentCond) {
        this.key = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
        this.firstEmployed = firstEmployed;
        this.employmentCond = employmentCond;
        this.HR = false;
    }

    public Employee(int id) {
        this.key = id;
    }

    public Employee() {

    }

    //Getters and Setters
    public Date getFirstEmployed() {
        return this.firstEmployed;
    }

    public int getId() {
        return key;
    }

    public void setId(int id) {
        this.key = id;
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

    public void setHR(boolean HR) {this.HR = HR; }

    public boolean isHR() { return HR; }

    public int getKey() {
        return key;
    }

    public int getSalary() {
        return this.salary;
    }

    public String getEmploymentCond() {
        return this.employmentCond;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public void setFirstEmployed(Date firstEmployed) {
        this.firstEmployed = firstEmployed;
    }

    public void setEmploymentCond(String employmentCond) {
        this.employmentCond = employmentCond;
    }

    public boolean isManager() {
        return Manager;
    }

    public void setManager(boolean manager) {
        Manager = manager;
    }

    public boolean isDriver() {
        return Driver;
    }

    public void setDriver(boolean driver) {
        Driver = driver;
    }

    public boolean isLogistics() {
        return Logistics;
    }

    public void setLogistics(boolean logistics) {
        Logistics = logistics;
    }

    public boolean isStockKeeper() {
        return StockKeeper;
    }

    public void setStockKeeper(boolean stockKeeper) {
        StockKeeper = stockKeeper;
    }

    public void setRoles(LinkedList<Role> roles)
    {
        this.roles = roles;
    }

    public LinkedList<Role> getRoles() {
        return roles;
    }
}