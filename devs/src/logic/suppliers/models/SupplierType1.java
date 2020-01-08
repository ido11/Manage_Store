package logic.suppliers.models;

import java.util.LinkedList;
/*
This Object represents a supplier that deliver his products in permanent days
 */
public class SupplierType1 extends Supplier {

    private LinkedList<String> days;

    public SupplierType1(int supplierNum, String name, String bankAccount, String paymentCond, String phoneNum , LinkedList<String> days) {
        super(supplierNum , name, bankAccount, paymentCond , phoneNum );
        this.days = days;
    }

    public SupplierType1(int supplierNum){
        super(supplierNum);
    }

    public LinkedList<String> getDays(){
        return this.days;
    }

    public void setDays(LinkedList<String> days){
        this.days = days;
    }
}
