package logic.suppliers.models;

public class Discount {

    int supplierNum;
    String code;
    int minAmount;
    int discount;

    public Discount(int supplierNum , String code , int minAmount , int discount){

        this.supplierNum = supplierNum;
        this.code = code;
        this.minAmount = minAmount;
        this.discount = discount;
    }

    public Discount(int supplierNum){
        this.supplierNum = supplierNum;
    }

    public Discount(int supplierNum, String code){
        this.supplierNum = supplierNum;
        this.code = code;
    }


    public int getSupplierNum() {
        return supplierNum;
    }

    public void setSupplierNum(int supplierNum){
        this.supplierNum = supplierNum;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code){
        this.code = code;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount){
        this.discount = discount;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public void setminAmount(int minAmount){
        this.minAmount = minAmount;
    }


}
