package logic.suppliers.models;

/**
 * A DTO entity that represents a user.
 * Used mainly to communicate between logic and persistence layers
 */
public class Supplier {



    //Fields
    private int supplierNum;
    private String name;
    private String bankAccount;
    private String paymentCond;
    private String phoneNum;
    //private String supplyingMethod;

    //Constructors
    public Supplier(int supplierNum,String name, String bankAccount, String paymentCond, String phoneNum) {
        this.supplierNum = supplierNum;
        this.name = name;
        this.bankAccount = bankAccount;
        this.paymentCond = paymentCond;
        this.phoneNum = phoneNum;
        //this.supplyingMethod = supplyingMethod;
    }

    public Supplier(int supplierNum){
        this.supplierNum = supplierNum;
    }

    //Getters and Setters
    public int getSupplierNum() {
        return supplierNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getPaymentCond() {
        return paymentCond;
    }

    public void setPaymentCond(String paymentCond) {
        this.paymentCond = paymentCond;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }



}
