package logic.deliveries.models;

import logic.suppliers.models.Product;

public interface SupplierOrder {
    int getBranch();
    int getSupplier();
    logic.inventory.models.Product getProduct();
    int getProdAmount();
}
