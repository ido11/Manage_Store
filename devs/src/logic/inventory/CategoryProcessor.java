package logic.inventory;

import logic.Modules;
import logic.inventory.models.ProductCategory;
import presentation.inventory.InventoryPrinter;
import presentation.inventory.printers.CategoryPrinter;
import presistence.Repository;
import presistence.dao.inventory.CategoryDAO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoryProcessor implements InventoryProcessor {


    private Repository repo; //A repository instance used to communicate with database
    private InventoryPrinter prompter;

    public CategoryProcessor(InventoryPrinter prompter, Repository repo) {
        this.prompter = prompter;
        this.repo = repo;
    }

    @Override
    public void process() {
        boolean menu = false;
        while (!menu) {
            boolean isStoreManager = InventoryInteractor.emp.isManager();
            switch (((CategoryPrinter)prompter.getPrinter(Modules.CATEGORIES)).printMenu()) {
                case 1:
                    if (isStoreManager) {
                        prompter.printMessage(true, "As store manager you can only view data");
                    }
                    else {
                        prompter.printMessage(false, null);
                        processAddNewCategory();
                    }
                    break;
                case 2:
                    if (isStoreManager) {
                        prompter.printMessage(true, "As store manager you can only view data");
                    }
                    else {
                        prompter.printMessage(false, null);
                        processRenameCategory();
                    }
                    break;
                case 3:
                    if (isStoreManager) {
                        prompter.printMessage(true, "As store manager you can only view data");
                    }
                    else {
                        processDeleteCategory();
                    }
                    break;
                case 4:
                    prompter.printMessage(false, null);
                    processDisplayCategoryDetails();
                    break;
                case 5:
                    processPrintAllCategories();
                    break;
                case 6: menu = true;
                    break;
            }
        }
    }

    private void processPrintAllCategories() {
        List<ProductCategory> categories = ((CategoryDAO) repo.getInventoryDAO(Modules.CATEGORIES)).findAll();
        ((CategoryPrinter)prompter.getPrinter(Modules.CATEGORIES)).printCategories(categories, true, "Press ENTER to return.", false, true);
        prompter.printMessage(false, null);
    }

    private void processDisplayCategoryDetails() {
        String name = ((CategoryPrinter)prompter.getPrinter(Modules.CATEGORIES)).categoryPrompt(" to display");
        if(name != null) {
            ProductCategory p;
            if ((p = ((CategoryDAO) repo.getInventoryDAO(Modules.CATEGORIES)).findByName(new ProductCategory(name))) != null) {
                ((CategoryPrinter)prompter.getPrinter(Modules.CATEGORIES)).printCategories(new ArrayList<>(Arrays.asList(p)), true, "Found Category.\nPress ENTER to return.", false, true);
            } prompter.printMessage(true, "Category '" + name + "' does not exist in system.");
        } else prompter.printMessage(false, null);
    }

    private void processDeleteCategory() {
        prompter.printMessage(null, "DELETING A CATEGORY WILL UN-ASSIGN THE CATEGORY FROM ALL ATTACHED PRODUCTS!");
        String name = ((CategoryPrinter)prompter.getPrinter(Modules.CATEGORIES)).categoryPrompt(" to remove");
        if(name != null) {
            if (((CategoryDAO) repo.getInventoryDAO(Modules.CATEGORIES)).findByName(new ProductCategory(name)) != null) {
                ((CategoryDAO) repo.getInventoryDAO(Modules.CATEGORIES)).delete(new ProductCategory(name));
                prompter.printMessage(false, "Category '" + name + "' deleted successfully.");
            }
        } else prompter.printMessage(false, null);
    }

    private void processRenameCategory() {
        String name = ((CategoryPrinter)prompter.getPrinter(Modules.CATEGORIES)).categoryPrompt(" to rename");
        if(name != null) {
            if (((CategoryDAO) repo.getInventoryDAO(Modules.CATEGORIES)).findByName(new ProductCategory(name)) != null) {
                String newName = ((CategoryPrinter)prompter.getPrinter(Modules.CATEGORIES)).categoryPrompt(" to change to");
                if(newName != null) {
                    if (((CategoryDAO) repo.getInventoryDAO(Modules.CATEGORIES)).findByName(new ProductCategory(newName)) == null) {
                        ((CategoryDAO) repo.getInventoryDAO(Modules.CATEGORIES)).update(new ProductCategory(name), 0, newName, 0);
                        prompter.printMessage(false, "Category '" + name + "' renamed successfully to '" + newName + "'.");
                    } else prompter.printMessage(true, "Failed changing name to category '" + name + "'.\n" +
                                                                    "Category '" + newName + "' already exists in system.");
                } else prompter.printMessage(false, null);
            } else prompter.printMessage(true, "Category '" + name + "' does not exist in system.");
        } else prompter.printMessage(false, null);
    }

    private void processAddNewCategory() {
        String name = ((CategoryPrinter)prompter.getPrinter(Modules.CATEGORIES)).categoryPrompt("");
        if(name != null) {
            if (((CategoryDAO) repo.getInventoryDAO(Modules.CATEGORIES)).findByName(new ProductCategory(name)) == null) {
                ((CategoryDAO) repo.getInventoryDAO(Modules.CATEGORIES)).insertCategory(new ProductCategory(name));
                prompter.printMessage(false, "Category '" + name + "' added successfully.");

            } else prompter.printMessage(true, "Category '" + name + "' already exists in system.");
        } else prompter.printMessage(false, null);
    }
}
