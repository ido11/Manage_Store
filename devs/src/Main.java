/**
* Submitters:
* Itay Bouganim, 305278384
* Sahar Vaya, 205583453
*/
import logic.Modules;
import logic.Processor;
import logic.deliveries.Windows.DeliveryManager;
import logic.deliveries.models.SupplierOrder;
import logic.inventory.models.Product;
import logic.suppliers.models.SupplierType3;
import presistence.Repository;
import presistence.dao.inventory.ProductsDAO;
import presistence.dao.suppliers.SupplierType3DAO;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        Processor processor = new Processor();
        try {
            processor.process();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
