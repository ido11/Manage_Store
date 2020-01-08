package logic.suppliers.models;

public class Product {

    int supplierNum;
    int price;
    int amount;
    String desc;
    String code;

    public Product(String code, String desc , int supplierNum , int  price, int  amount){
        this.desc = desc;
        this.code = code;
        this.supplierNum = supplierNum;
        this.price = price;
        this.amount = amount;
    }

    public Product(int supplierNum){
        this.supplierNum = supplierNum;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getSupplierNum() {
        return supplierNum;
    }

    public void setSupplierNum(int supplierNum) {
        this.supplierNum = supplierNum;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCode() {
        return code.trim();
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
