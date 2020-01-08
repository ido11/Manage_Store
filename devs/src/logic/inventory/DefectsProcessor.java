package logic.inventory;

import logic.Modules;
import logic.inventory.datatypes.Date;
import logic.inventory.models.DefectiveProduct;
import logic.inventory.models.StockProducts;
import logic.inventory.models.StoreBranch;
import presentation.inventory.InventoryPrinter;
import presentation.inventory.printers.DefectsPrinter;
import presistence.Repository;
import presistence.dao.inventory.DefectiveDAO;
import presistence.dao.inventory.StockDAO;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefectsProcessor implements InventoryProcessor {

    private Repository repo; //A repository instance used to communicate with database
    private InventoryPrinter prompter;
    private StoreBranch activeBranch;
    private Date currentDate;

    public DefectsProcessor(InventoryPrinter prompter, Repository repo) {
        this.prompter = prompter;
        this.repo = repo;
    }


    @Override
    public void process() {
        boolean menu = false;
        while (!menu) {
            boolean isStoreManager = InventoryInteractor.emp.isManager();
            switch (prompter.getPrinter(Modules.DEFECTS).printMenu()) {
                case 1:
                    if (isStoreManager) {
                        prompter.printMessage(true, "As store manager you can only view data");
                    }
                    else {
                        prompter.printMessage(false, null);
                        processReportDefectiveProduct();
                    }
                    break;
                case 2:
                    if (isStoreManager) {
                        prompter.printMessage(true, "As store manager you can only view data");
                    }
                    else {
                        prompter.printMessage(false, null);
                        processRemoveFromDefective();
                    }
                    break;
                case 3:
                    prompter.printMessage(false, null);
                    processViewAllDefective();
                    break;
                case 4:
                    menu = true;
                    break;
            }
        }
    }

    private void processReportDefectiveProduct() {
        int barcode = prompter.barcodePrompt();
        List<StockProducts> stock = ((StockDAO)repo.getInventoryDAO(Modules.STOCK)).findByBarcodeAndLocation(activeBranch, barcode);
        if(stock.size() != 0) {
            List<Map.Entry<Integer, StockProducts>> stock_with_id = new ArrayList<>();
            for (int i = 0; i < stock.size(); i++)
                stock_with_id.add(new AbstractMap.SimpleImmutableEntry<>(i + 1, stock.get(i)));
            ((DefectsPrinter) prompter.getPrinter(Modules.DEFECTS)).printAllStockByLocationAndID(stock_with_id, false);
            int id = ((DefectsPrinter) prompter.getPrinter(Modules.DEFECTS)).printAskIdentifier(stock_with_id.size());
            if (id != prompter.ILLEGAL) {
                StockProducts toReport = stock_with_id.get(id - 1).getValue();
                ((StockDAO) repo.getInventoryDAO(Modules.STOCK)).delete(toReport);
                ((DefectiveDAO) repo.getInventoryDAO(Modules.DEFECTS)).insert(new DefectiveProduct(toReport.getBarcode(),
                        toReport.getExpiration_date(), toReport.getLocationID(),
                        toReport.getQuantity(), "Reported", currentDate));
                prompter.printMessage(false, "Reported items successfully, items with the following details:\n" +
                                                            "Bracode: " + toReport.getBarcode() + ", Name: " + toReport.getName() + ", Expiration Date: " + toReport.getExpiration_date() +
                                                            "\nMoved to defective section.");
            }
        } else prompter.printMessage(true, "Did not find any matching items in stock with barcode: "+barcode);
    }

    private void processRemoveFromDefective() {
        int barcode = prompter.barcodePrompt();
        List<DefectiveProduct> defects = ((DefectiveDAO)repo.getInventoryDAO(Modules.DEFECTS)).findAll(activeBranch, barcode);
        if(defects.size() != 0) {
            List<Map.Entry<Integer, DefectiveProduct>> defects_with_id = new ArrayList<>();
            for (int i = 0; i < defects.size(); i++)
                defects_with_id.add(new AbstractMap.SimpleImmutableEntry<>(i + 1, defects.get(i)));
            ((DefectsPrinter) prompter.getPrinter(Modules.DEFECTS)).printAllDefectiveByLocationAndID(defects_with_id, false);
            int id = ((DefectsPrinter) prompter.getPrinter(Modules.DEFECTS)).printAskIdentifier(defects_with_id.size());
            if (id != prompter.ILLEGAL) {
                DefectiveProduct toRemove = defects_with_id.get(id - 1).getValue();
                ((DefectiveDAO) repo.getInventoryDAO(Modules.DEFECTS)).delete(toRemove);
                prompter.printMessage(false, "Deleted items successfully from defective items section, items with the following details:\n" +
                        "Bracode: " + toRemove.getBarcode() + ", Name: " + toRemove.getName() + ", Date Reported: " + toRemove.getDate_reported());
            }
        } else prompter.printMessage(true, "Did not find any matching items in stock with barcode: "+barcode);
    }

    private void processViewAllDefective() {
        List<DefectiveProduct> defects = ((DefectiveDAO)repo.getInventoryDAO(Modules.DEFECTS)).findAll(activeBranch, null);
        if(defects.size() != 0) {
            List<Map.Entry<Integer, DefectiveProduct>> defects_with_id = new ArrayList<>();
            for (int i = 0; i < defects.size(); i++)
                defects_with_id.add(new AbstractMap.SimpleImmutableEntry<>(i + 1, defects.get(i)));
            ((DefectsPrinter) prompter.getPrinter(Modules.DEFECTS)).printAllDefectiveByLocationAndID(defects_with_id, true);
            prompter.printMessage(false, null);
        } else prompter.printMessage(true, "No products are currently in the defective section.");
    }

    public void setActiveBranch(StoreBranch activeStoreBranch) {
        this.activeBranch = activeStoreBranch;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }
}
