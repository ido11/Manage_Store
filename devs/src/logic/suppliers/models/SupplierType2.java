package logic.suppliers.models;

import java.util.LinkedList;
/*
This Object represents a supplier that arrives according to a specific order
 */
public class SupplierType2 extends Supplier {

    public SupplierType2(int supplierNum, String name, String bankAccount, String paymentCond, String phoneNum ) {
        super(supplierNum , name, bankAccount, paymentCond , phoneNum );
    }

    public SupplierType2(int supplierNum){
        super(supplierNum);
    }
}
