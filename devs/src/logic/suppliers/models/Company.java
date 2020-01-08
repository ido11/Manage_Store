package logic.suppliers.models;

public class Company {

    private int companyNum;
    private int supplierNum;
    private String name;


    public Company(int companyNum ,int supplierNum, String name){
        this.companyNum = companyNum;
        this.name = name;
        this.supplierNum = supplierNum;
    }


    public int getCompanyNum(){
        return  this.companyNum;
    }

    public void setCompanyNum(int companyNum){
        this.companyNum = companyNum;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getSupplierNum(){
        return  this.supplierNum;
    }

    public void setSupplierNum(int supplierNum){
        this.supplierNum = supplierNum;
    }
}
