package logic.suppliers.models;

public class Contact {

    private String name;
    private String phoneNum;
    private int supplierNum;

    public Contact(int supplierNum , String phoneNum , String name){
        this.supplierNum = supplierNum;
        this.name = name;
        this.phoneNum = phoneNum;
    }

    public Contact(int supplierNum){
        this.supplierNum = supplierNum;
    }

    public int getSupplierNum(){
        return this.supplierNum;
    }

    public void setSupplierNum(int supplierNum){
        this.supplierNum = supplierNum;
    }

    public String getPhoneNum(){
        return this.phoneNum;
    }

    public void setPhoneNum(String phoneNum){
        this.phoneNum = phoneNum;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

}
