package logic.suppliers.models;

import java.util.LinkedList;
/*
This Object represents a supplier that does'nt deliver is product
 */
public class SupplierType3 extends Supplier {

    public SupplierType3(int supplierNum, String name, String bankAccount, String paymentCond, String phoneNum) {
        super(supplierNum , name, bankAccount, paymentCond , phoneNum );
    }

    public SupplierType3(int supplierNum){
        super(supplierNum);
    }

    public int getSupplierNum()
    {
        return super.getSupplierNum();
    }


}
