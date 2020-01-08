package logic.inventory;

import logic.Interactor;
import logic.Modules;
import logic.deliveries.Windows.DeliveryManager;
import logic.deliveries.models.Branch;
import logic.deliveries.models.SupplierOrder;
import logic.employees.models.Employee;
import logic.inventory.models.DefectiveProduct;
import logic.inventory.models.Location;
import logic.inventory.models.StockProducts;
import logic.inventory.models.StoreBranch;
import logic.inventory.datatypes.Date;
import logic.suppliers.models.*;
import presentation.inventory.InventoryPrinter;
import presentation.inventory.printers.BranchesPrinter;
import presentation.inventory.printers.StockPrinter;
import presistence.Repository;
import presistence.dao.inventory.*;
import presistence.dao.suppliers.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A class used to communicate between End-user and the Database
     * and to perform all logical program actions.
     */
    public class InventoryInteractor implements Interactor {
        public static Employee emp;
        private static InventoryInteractor instance = null;
        public final String ORDER_FILENAME = "orders.dat";
        public final String DELIVERY_FILENAME = "deliveries.dat";
        private boolean ordered = false;

        private static Map<Modules, InventoryProcessor> processors;

        private static boolean shutdown; //Determines whether the programs needs to shutdown.
        private static StoreBranch activeStoreBranch = null; //Current connected store branch
        private static Date currentDate;
        private Repository repo;


        private static InventoryPrinter prompter; //A CLI prompter instance used to present and retrieve date to and from end-user

        //Constructor
        private InventoryInteractor(InventoryPrinter prompt, Repository repo)
        {
            this.repo = repo;
            prompter = prompt;
            shutdown = false;
            processors = new HashMap<>() {{
                    put(Modules.PRODUCTS, new ProductsProcessor(prompter, repo));
                    put(Modules.DISCOUNTS, new DiscountProcessor(prompter, repo));
                    put(Modules.CATEGORIES, new CategoryProcessor(prompter, repo));
                    put(Modules.STOCK, new StockProcessor(prompter, repo));
                    put(Modules.REPORTS, new ReportsProcessor(prompter, repo));
                    put(Modules.DEFECTS, new DefectsProcessor(prompter, repo));
            }};
        }

        public static InventoryInteractor getInstance(Repository repo, InventoryPrinter prompt)
        {
            if (instance == null)
                instance = new InventoryInteractor(prompt, repo);
            return instance;
        }

    /**
     * Main processor loop to run until end-user chose to exist and shutdown flag is changed.
     */
    public void start(Employee emp)
    {
        this.emp = emp;
        activeStoreBranch = null;
        shutdown = false;
        currentDate = Date.getCurrentDate();
        ((ReportsProcessor)processors.get(Modules.REPORTS)).setCurrentDate(currentDate);
        ((DefectsProcessor)processors.get(Modules.DEFECTS)).setCurrentDate(currentDate);
        while(!shutdown) {
            while (!logToBranch()) //While no user is connected prompt user to login
                prompter.printMessage(true, "Illegal store branch ID.");
            if(shutdown)
                break;
            ((ReportsProcessor)processors.get(Modules.REPORTS)).setActiveBranch(activeStoreBranch);
            ((StockProcessor)processors.get(Modules.STOCK)).setActiveBranch(activeStoreBranch);
            ((DefectsProcessor)processors.get(Modules.DEFECTS)).setActiveBranch(activeStoreBranch);
            ((ProductsProcessor)processors.get(Modules.PRODUCTS)).setActiveBranch(activeStoreBranch);
            Map.Entry<List<StockProducts>, List<StockProducts>> expired = ((StockDAO)repo.getInventoryDAO(Modules.STOCK)).findExpired(activeStoreBranch);
            ArrayList<String> messages3 = checkDeliveryOrder();
            List<StockProducts> critical_amount = ((StockDAO)repo.getInventoryDAO(Modules.STOCK)).findCriticalAmount(activeStoreBranch);
            if(expired.getKey().size() != 0 || critical_amount.size() != 0)
                prompter.promptNotifications(expired.getKey(), critical_amount, currentDate);
            prompter.printMessage(false, "Please enter amounts for automated supplier order: ");
            ArrayList<String> messages = orderCriticalAmount(critical_amount);
            StringBuilder message = new StringBuilder("");
            if(messages.size() > 1)
                for(String m : messages)
                    message.append("\n"+m);
            moveExpiredToDefects(expired);
            ArrayList<String> messages2 = new ArrayList<>();
            Date temp = currentDate;
            currentDate = Date.getCurrentDate();//As long as the user is connected prompt the main menu options.
            if(!temp.toString().equals(currentDate.toString()))
                ordered = false;
            if(!ordered) {
                messages2 = checkPeriodicOrder();
                for(String m : messages2)
                    message.append("\n"+m);
                for(String m : messages3)
                    message.append("\n"+m);
            }
            prompter.printMessage(false, "Welcome to Store Branch "+activeStoreBranch.getName()+" No."+activeStoreBranch.getBranchID()+message);
            while (processMainMenu(currentDate, Date.getCurrentDay(), messages2, messages3)) {
                temp = currentDate;
                currentDate = Date.getCurrentDate();//As long as the user is connected prompt the main menu options.
                if(!temp.toString().equals(currentDate.toString()))
                    ordered = false;
                if(!ordered) {
                    checkPeriodicOrder();
                }
                critical_amount = ((StockDAO)repo.getInventoryDAO(Modules.STOCK)).findCriticalAmount(activeStoreBranch);
                if(expired.getKey().size() != 0 || critical_amount.size() != 0)
                    prompter.promptNotifications(expired.getKey(), critical_amount, currentDate);
                orderCriticalAmount(critical_amount);
                ((ReportsProcessor)processors.get(Modules.REPORTS)).setCurrentDate(currentDate);
                ((DefectsProcessor)processors.get(Modules.DEFECTS)).setCurrentDate(currentDate);
            }
        }
        //After user chose to exit, close all object instances.
        shutdown = false;
    }

    private ArrayList<String> checkPeriodicOrder() {
        ArrayList<String> message = new ArrayList<>();
        message.add("\nAutomated periodic order from supplier messages:");
        List<String> m = checkSupplyOrder();
        if(m.size() != 0)
            for(String mess : m)
                message.add(mess);
        List<SupplierType1> sup = ((SupplierType1DAO) repo.getSupplierDAO(Modules.SUPPLIER1)).findAllBySupplyDay(Date.getValueDay(Date.getDayValue(Date.getCurrentDay()) + 1));
        if(!(new File(ORDER_FILENAME).isFile())) {
            for (SupplierType1 s : sup) {
                prompter.printMessage(false, "Periodic order to be available on " + Date.getValueDay(Date.getDayValue(Date.getCurrentDay()) + 1) + " from supplier " + s.getSupplierNum() + ":");
                boolean supply = prompter.supplyPrompt(s, Date.getValueDay(Date.getDayValue(Date.getCurrentDay()) + 1));
                if (supply) {
                    List<logic.suppliers.models.Product> productList = ((ProductDAO) repo.getSupplierDAO(Modules.SUPPLIERPRODUCTS)).findBySupplier(s);
                    if (productList.size() != 0) {
                        if (!(new File(ORDER_FILENAME).isFile()))
                            saveOrderToFile(Date.getValueDay(Date.getDayValue(Date.getCurrentDay()) + 1), false, ORDER_FILENAME);
                        for (Product p : productList) {
                            p.setCode(p.getCode().trim());
                            StringBuilder order = new StringBuilder("");
                            logic.inventory.models.Product p2;
                            if ((p2 = ((ProductsDAO) repo.getInventoryDAO(Modules.PRODUCTS)).findByKey(new logic.inventory.models.Product(Integer.parseInt(p.getCode())))) != null) {
                                order.append(s.getSupplierNum() + ",");
                                order.append(p.getCode() + ",");
                                int min_amount_to_order = p2.getMinimal_amount();
                                if(p.getAmount() >= min_amount_to_order) {
                                    int amount = prompter.orderAmountPrompt(p, min_amount_to_order);
                                    logic.suppliers.models.Discount discount = ((DiscountDAO) repo.getSupplierDAO(Modules.DISCOUNT)).findByKey(new Discount(s.getSupplierNum(), p2.getBarcode() + ""));
                                    if (discount != null)
                                        if (discount.getMinAmount() <= amount) {
                                            ((DiscountProcessor) processors.get(Modules.DISCOUNTS)).discountSupplier(p2.getBarcode(), discount.getDiscount());
                                            message.add("Discount from supplier " + discount.getSupplierNum() + " of " + discount.getDiscount() + "% recently given for product barcode: " + p2.getBarcode() + " by ordering more than " + discount.getMinAmount() + " UNITS. ");
                                        } else {
                                            List<logic.inventory.models.Discount> d = ((DiscountsDAO) repo.getInventoryDAO(Modules.DISCOUNTS)).findByBarcode(new logic.inventory.models.Discount(p2.getBarcode(), "Supplier"), true, true);
                                            for (logic.inventory.models.Discount disc : d) {
                                                Date date;
                                                ((DiscountsDAO) repo.getInventoryDAO(Modules.DISCOUNTS)).update(new logic.inventory.models.Discount(disc.getDiscountID(), disc.getBarcode(),
                                                                disc.getDiscounter(), disc.getPercentage(), disc.getDate_given(), (date = Date.parseDate(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime())))),
                                                        disc.getDiscountID(), 0, 0);
                                                ((DiscountProcessor) processors.get(Modules.DISCOUNTS)).reDiscount(disc);
                                            }
                                        }
                                    if (amount != 0) {
                                        Date date = ((StockPrinter) prompter.getPrinter(Modules.STOCK)).promptExpirationDate();
                                        Location location = ((StockPrinter) prompter.getPrinter(Modules.STOCK)).promptLocation(activeStoreBranch);
                                        Location actual, switched = null;
                                        if (location != null) {
                                            location.setPlace_identifier(location.getPlace_identifier().toUpperCase());
                                            if ((actual = ((LocationsDAO) repo.getInventoryDAO(Modules.LOCATIONS)).findLocation(location)) == null) {
                                                ((LocationsDAO) repo.getInventoryDAO(Modules.LOCATIONS)).insert(location);
                                                actual = ((LocationsDAO) repo.getInventoryDAO(Modules.LOCATIONS)).findLocation(location);
                                            }
                                            location.switchPlace();
                                            if ((switched = ((LocationsDAO) repo.getInventoryDAO(Modules.LOCATIONS)).findLocation(location)) == null) {
                                                ((LocationsDAO) repo.getInventoryDAO(Modules.LOCATIONS)).insert(location);
                                                switched = ((LocationsDAO) repo.getInventoryDAO(Modules.LOCATIONS)).findLocation(location);
                                            }
                                            int old_amount = p.getAmount();
                                            Product updated = p;
                                            updated.setAmount(old_amount - amount);
                                            if (updated.getAmount() == 0)
                                                ((ProductDAO) repo.getSupplierDAO(Modules.SUPPLIERPRODUCTS)).delete(updated);
                                            else
                                                ((ProductDAO) repo.getSupplierDAO(Modules.SUPPLIERPRODUCTS)).update(updated, p.getSupplierNum() + "", "", "");
                                            order.append(amount + ",");
                                            order.append(date.toString() + ",");
                                            order.append(actual.getLocationID() + ",");
                                            order.append(switched.getLocationID());
                                            saveOrderToFile(order.toString(), true, ORDER_FILENAME);
                                        }
                                    }
                                }
                            }
                        }
                        message.add("Supplier number " + s.getSupplierNum() + " is preparing the periodic order (Day of supply: " + Date.getValueDay(Date.getDayValue(Date.getCurrentDay()) + 1) + ").");
                    } else prompter.promptNoProductsSupplier(s.getSupplierNum());
                }
            }
        }

        if(message.size() == 1)
            message.add("None.");
        ordered = true;
        return message;
    }

    private List<String> checkSupplyOrder() {
        File f = new File(ORDER_FILENAME);
        List<String> suppliers = new ArrayList<>();
        List<String> message = new ArrayList<>();
        boolean today = false;
        if((f.isFile()))
        {
            try {
                BufferedReader br = new BufferedReader(new FileReader(ORDER_FILENAME));
                boolean first = true;
                String st;
                while ((st = br.readLine()) != null) {
                    if(first)
                        today = Date.getCurrentDay().equals(st);
                    if(today && !first) {
                        StringTokenizer tokenizer = new StringTokenizer(st, ",");
                        String sup = tokenizer.nextToken();
                        int barcode = Integer.parseInt(tokenizer.nextToken());
                        int amount = Integer.parseInt(tokenizer.nextToken());
                        Date date = Date.parseDate(tokenizer.nextToken());
                        int location = Integer.parseInt(tokenizer.nextToken());
                        int locationSwitched = Integer.parseInt(tokenizer.nextToken());
                        StockProducts prod = new StockProducts(barcode, date, location, amount);
                        if(((StockDAO) repo.getInventoryDAO(Modules.STOCK)).findByKey(new StockProducts(prod.getBarcode(), new Date(1, 1, 2100), -1, 0)) == null) {
                            ((StockDAO) repo.getInventoryDAO(Modules.STOCK)).insert(prod);
                            ((StockDAO) repo.getInventoryDAO(Modules.STOCK)).insert(new StockProducts(prod.getBarcode(), date, locationSwitched, 0));
                        } else {
                            ((StockDAO) repo.getInventoryDAO(Modules.STOCK)).updateAll(prod, prod.getBarcode(), new Date(1, 1, 2100), -1);
                            StockProducts prodSwitched = new StockProducts(prod.getBarcode(), date, locationSwitched, 0);
                            ((StockDAO) repo.getInventoryDAO(Modules.STOCK)).updateAll(prodSwitched, prodSwitched.getBarcode(), new Date(1, 1, 2100), -2);
                        }
                        suppliers.add(sup);
                    }
                    first = false;
                }
            }
            catch (IOException e) {}
            if(today)
                for(String supllier : suppliers)
                message.add("All periodic order scheduled for "+Date.getCurrentDay()+" from supplier number "+supllier+" have been added to stock.");
        }
        if(today)
            f.delete();
        return message;
    }

    private ArrayList<String> checkDeliveryOrder()
    {
        File f = new File(DELIVERY_FILENAME);
        ArrayList<String> message = new ArrayList<>();
        File tempFile = new File("temp_delivery.dat");
        if((f.isFile()))
        {
            try {
                BufferedReader br = new BufferedReader(new FileReader(DELIVERY_FILENAME));
                boolean first = true;
                String st;
                while ((st = br.readLine()) != null && !st.equals("\n")) {
                    StringTokenizer tokenizer = new StringTokenizer(st, ",");
                    String date = tokenizer.nextToken();
                    if(Date.compareDateToToday(Date.parseDate(date))) {
                        boolean checkDefected = false;
                        int supplier_num = Integer.parseInt(tokenizer.nextToken());
                        int barcode = Integer.parseInt(tokenizer.nextToken());
                        int amount = Integer.parseInt(tokenizer.nextToken());
                        Date d = Date.parseDate(tokenizer.nextToken());
                        int location = Integer.parseInt(tokenizer.nextToken());
                        int locationSwitched = Integer.parseInt(tokenizer.nextToken());
                        int defected = prompter.printHowManyDefected(date, barcode, amount);
                        while (!checkDefected) {
                            if (defected != 0) {
                                while (defected > amount) {
                                    prompter.printMessage(true, "Illegal number of defected products entered, please enter a number less or equal to the amount received.");
                                    defected = prompter.printHowManyDefected(date, barcode, amount);
                                }
                                if (defected < amount) {
                                    ((DefectiveDAO) repo.getInventoryDAO(Modules.DEFECTS)).insert(new DefectiveProduct(barcode, Date.parseDate("0000-00-00"), location, defected, "Reported", Date.getCurrentDate()));
                                    amount -= defected;
                                    checkDefected = true;
                                } else if (defected == amount) {
                                    ((DefectiveDAO) repo.getInventoryDAO(Modules.DEFECTS)).insert(new DefectiveProduct(barcode, Date.parseDate("0000-00-00"), location, defected, "Reported", Date.getCurrentDate()));
                                    checkDefected = true;
                                    continue;
                                }
                                StockProducts prod = new StockProducts(barcode, d, location, amount);
                                if (((StockDAO) repo.getInventoryDAO(Modules.STOCK)).findByKey(new StockProducts(prod.getBarcode(), new Date(1, 1, 2100), -1, 0)) == null) {
                                    ((StockDAO) repo.getInventoryDAO(Modules.STOCK)).insert(prod);
                                    ((StockDAO) repo.getInventoryDAO(Modules.STOCK)).insert(new StockProducts(prod.getBarcode(), d, locationSwitched, 0));
                                } else {
                                    ((StockDAO) repo.getInventoryDAO(Modules.STOCK)).updateAll(prod, prod.getBarcode(), new Date(1, 1, 2100), -1);
                                    StockProducts prodSwitched = new StockProducts(prod.getBarcode(), d, locationSwitched, 0);
                                    ((StockDAO) repo.getInventoryDAO(Modules.STOCK)).updateAll(prodSwitched, prodSwitched.getBarcode(), new Date(1, 1, 2100), -2);
                                }
                                message.add("All deliveries scheduled for " + date + " for product barcode " + barcode + " have been added to stock.");
                            }
                        }
                    } else {
                        saveOrderToFile(st, (new File("temp_delivery.dat").isFile()), "temp_delivery.dat");
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        f.delete();
        tempFile.renameTo(f);
        return message;
    }

    private void saveOrderToFile(String order, boolean append, String file)
    {
        try {
            if(!append) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(order);
                writer.close();
            } else {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
                writer.append('\n');
                writer.append(order);
                writer.close();
            }
        } catch (IOException e) {}
    }

    private ArrayList<String> orderCriticalAmount(List<StockProducts> crit) {
        ArrayList<String> ordered_prods = new ArrayList<>();
        ordered_prods.add("Automated order from supplier by shortage messages:");
        logic.deliveries.models.Branch branch;
        if(crit.size() != 0) {
            branch = prompter.getBranchContactDetails(activeStoreBranch);
            if (branch != null) {
                for (StockProducts prod : crit) {
                    boolean ordered = false;
                    int min_amount_to_order = prod.getMinimal_amount() - prod.getQuantity();
                    List<logic.suppliers.models.Product> productList = ((ProductDAO) repo.getSupplierDAO(Modules.SUPPLIERPRODUCTS)).findByBarcode(prod.getBarcode());
                    productList.sort(Comparator.comparing(Product::getPrice));
                    for (Product prod2 : productList) {
                        if ((((SupplierType3DAO) repo.getSupplierDAO(Modules.SUPPLIER3)).findByKey(new SupplierType3(prod2.getSupplierNum())) != null) ||
                                (((SupplierType2DAO) repo.getSupplierDAO(Modules.SUPPLIER2)).findByKey(new SupplierType2(prod2.getSupplierNum())) != null)) {
                            if (prod2.getAmount() >= min_amount_to_order) {
                                //Order by minimum price
                                prompter.printMessage(false, "Order of product barcode " + prod2.getCode() + " due to shortage: ");
                                int amount = prompter.orderAmountPrompt(prod2, min_amount_to_order);
                                if (amount != 0) {
                                    logic.suppliers.models.Discount discount = ((DiscountDAO) repo.getSupplierDAO(Modules.DISCOUNT)).findByKey(new Discount(prod2.getSupplierNum(), prod.getBarcode() + ""));
                                    if (discount != null)
                                        if (discount.getMinAmount() <= amount) {
                                            ((DiscountProcessor) processors.get(Modules.DISCOUNTS)).discountSupplier(prod.getBarcode(), discount.getDiscount());
                                            ordered_prods.add("Discount from supplier " + discount.getSupplierNum() + " of " + discount.getDiscount() + "% given for product barcode: " + prod.getBarcode() + " by ordering more than " + discount.getMinAmount() + " UNITS. ");
                                        } else {
                                            List<logic.inventory.models.Discount> d = ((DiscountsDAO) repo.getInventoryDAO(Modules.DISCOUNTS)).findByBarcode(new logic.inventory.models.Discount(prod.getBarcode(), "Supplier"), true, true);
                                            for (logic.inventory.models.Discount disc : d) {
                                                Date date;
                                                ((DiscountsDAO) repo.getInventoryDAO(Modules.DISCOUNTS)).update(new logic.inventory.models.Discount(disc.getDiscountID(), disc.getBarcode(),
                                                                disc.getDiscounter(), disc.getPercentage(), disc.getDate_given(), (date = Date.parseDate(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime())))),
                                                        disc.getDiscountID(), 0, 0);
                                                ((DiscountProcessor) processors.get(Modules.DISCOUNTS)).reDiscount(disc);
                                            }
                                        }
                                    Date date = ((StockPrinter) prompter.getPrinter(Modules.STOCK)).promptExpirationDate();
                                    Location location = ((StockPrinter) prompter.getPrinter(Modules.STOCK)).promptLocation(activeStoreBranch);
                                    if (location != null) {
                                        location.setPlace_identifier(location.getPlace_identifier().toUpperCase());
                                        Location actual, switched = null;
                                        if ((actual = ((LocationsDAO) repo.getInventoryDAO(Modules.LOCATIONS)).findLocation(location)) == null) {
                                            ((LocationsDAO) repo.getInventoryDAO(Modules.LOCATIONS)).insert(location);
                                            actual = ((LocationsDAO) repo.getInventoryDAO(Modules.LOCATIONS)).findLocation(location);
                                        }
                                        location.switchPlace();
                                        if ((switched = ((LocationsDAO) repo.getInventoryDAO(Modules.LOCATIONS)).findLocation(location)) == null) {
                                            ((LocationsDAO) repo.getInventoryDAO(Modules.LOCATIONS)).insert(location);
                                            switched = ((LocationsDAO) repo.getInventoryDAO(Modules.LOCATIONS)).findLocation(location);
                                        }
                                        int old_amount = prod2.getAmount();
                                        Product updated = prod2;
                                        updated.setAmount(old_amount - amount);
                                        if (updated.getAmount() == 0)
                                            ((ProductDAO) repo.getSupplierDAO(Modules.SUPPLIERPRODUCTS)).delete(updated);
                                        else
                                            ((ProductDAO) repo.getSupplierDAO(Modules.SUPPLIERPRODUCTS)).update(updated, prod2.getSupplierNum() + "", "", "");
                                        StockProducts updated2 = prod;
                                        updated2.setQuantity(amount);
                                        updated2.setExpiration_date(date);
                                        updated2.setLocationID(location.getLocationID());
                                        if (((SupplierType2DAO) repo.getSupplierDAO(Modules.SUPPLIER2)).findByKey(new SupplierType2(prod2.getSupplierNum())) != null) {
                                            StockProducts product = new StockProducts(updated2.getBarcode(), date, actual.getLocationID(), updated2.getQuantity());
                                            if (((StockDAO) repo.getInventoryDAO(Modules.STOCK)).findByKey(new StockProducts(prod.getBarcode(), new Date(1, 1, 2100), -1, 0)) == null) {
                                                ((StockDAO) repo.getInventoryDAO(Modules.STOCK)).insert(new StockProducts(updated2.getBarcode(), date, actual.getLocationID(), updated2.getQuantity()));
                                                ((StockDAO) repo.getInventoryDAO(Modules.STOCK)).insert(new StockProducts(updated2.getBarcode(), date, switched.getLocationID(), 0));

                                            } else {
                                                ((StockDAO) repo.getInventoryDAO(Modules.STOCK)).updateAll(product, product.getBarcode(), new Date(1, 1, 2100), -1);
                                                product.setLocationID(switched.getLocationID());
                                                product.setQuantity(0);
                                                ((StockDAO) repo.getInventoryDAO(Modules.STOCK)).updateAll(product, product.getBarcode(), new Date(1, 1, 2100), -2);
                                            }
                                            ordered = true;
                                            break;
                                        } else { //Supplier Type 3
                                            SupplierType3 supp = ((SupplierType3DAO) repo.getSupplierDAO(Modules.SUPPLIER3)).findByKey(new SupplierType3(prod2.getSupplierNum()));
                                            DeliveryManager delivery_manager = new DeliveryManager();
                                            java.util.Date d = delivery_manager.produceDelivery(new SupplierOrder() {
                                                @Override
                                                public int getBranch() {
                                                    return activeStoreBranch.getBranchID();
                                                }

                                                @Override
                                                public int getSupplier() {
                                                    return supp.getSupplierNum();
                                                }

                                                @Override
                                                public logic.inventory.models.Product getProduct() {
                                                    return new logic.inventory.models.Product(Integer.parseInt(prod2.getCode()), ((ProductsDAO) repo.getInventoryDAO(Modules.PRODUCTS)).findByKey(new logic.inventory.models.Product(Integer.parseInt(prod2.getCode()))).getWeight());
                                                }

                                                @Override
                                                public int getProdAmount() {
                                                    return prod2.getAmount();
                                                }
                                            });
                                            prompter.waitForEnter();
                                            if (d != null) {
                                                Date deliveryDate = Date.javaDateToDate(d);
                                                StringBuilder order = new StringBuilder("");
                                                order.append(deliveryDate.toString() + ",");
                                                order.append(supp.getSupplierNum() + ",");
                                                order.append(prod2.getCode() + ",");
                                                order.append(amount + ",");
                                                order.append(date.toString() + ",");
                                                order.append(actual.getLocationID() + ",");
                                                order.append(switched.getLocationID());
                                                boolean append = (new File(DELIVERY_FILENAME).isFile());
                                                saveOrderToFile(order.toString(), append, DELIVERY_FILENAME);
                                            } else
                                                ordered_prods.add("Did not find available delivery for product barcode " + prod2.getCode() + ".");
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (!ordered)
                        ordered_prods.add("No supplier available for product: " + prod.getName() + ", Barcode: " + prod.getBarcode() + ", to order " + min_amount_to_order + " above minimal amount.");
                }
                if (ordered_prods.size() == 1)
                    ordered_prods.add("None.");
            }
        }
        return ordered_prods;
    }

    private void moveExpiredToDefects(Map.Entry<List<StockProducts>, List<StockProducts>> expired) {
        for(StockProducts ex : expired.getKey()) {
            ((StockDAO)repo.getInventoryDAO(Modules.STOCK)).delete(ex);
            ((DefectiveDAO)repo.getInventoryDAO(Modules.DEFECTS)).insert(new DefectiveProduct(ex.getBarcode(), ex.getExpiration_date(), ex.getLocationID(),
                                                                    ex.getQuantity(), "Expiration Date", currentDate));
        }

        for(StockProducts ex : expired.getValue()) {
            ((StockDAO)repo.getInventoryDAO(Modules.STOCK)).delete(ex);
            ((DefectiveDAO)repo.getInventoryDAO(Modules.DEFECTS)).insert(new DefectiveProduct(ex.getBarcode(), ex.getExpiration_date(), ex.getLocationID(),
                    ex.getQuantity(), "Expiration Date", currentDate));
        }
    }

    public boolean logToBranch()
    {
        try {
            while (activeStoreBranch == null) {
                switch (prompter.getPrinter(Modules.BRANCHES).printMenu()) {
                    case 1:
                        prompter.printMessage(false, null);
                        if((activeStoreBranch = ((BranchesDAO)repo.getInventoryDAO(Modules.BRANCHES)).findByKey(
                                new StoreBranch(((BranchesPrinter)prompter.getPrinter(Modules.BRANCHES)).chooseBranch(false)))) == null)
                            throw new IllegalArgumentException();
                        break;
                    case 2:
                        processOpenNewBranch();
                        break;
                    case 3:
                        processCloseBranch();
                        break;
                    case 4:
                        processViewChain();
                        break;
                    case 5:
                        prompter.printMessage(false, "Goodbye, hope you enjoyed our system.");
                        shutdown = true;
                        return true;

                }
            }
        } catch (Exception e) {
            prompter.printMessage(true, "Illegal store branch ID.");
            return false;
        }
        return true;
    }

    private void processOpenNewBranch() throws IOException {
        prompter.printMessage(false, null);
        String name = ((BranchesPrinter)prompter.getPrinter(Modules.BRANCHES)).promptNewBranchName();
        if(name.equals("Q") || name.equals("q")) {
            prompter.printMessage(false, null);
            return;
        }
        else if(name.length() < 3){
            prompter.printMessage(true, "Branch name must be at least 3 letters.");
            return;
        }
        while(((BranchesDAO)repo.getInventoryDAO(Modules.BRANCHES)).findByName(new StoreBranch(name)) != null)
        {
            prompter.printMessage(true, "Store branch name already exists in system!\nPlease choose another name.");
            name = ((BranchesPrinter)prompter.getPrinter(Modules.BRANCHES)).promptNewBranchName();
            if(name.equals("Q") || name.equals("q")) {
                prompter.printMessage(false, null);
                return;
            }
            else if(name.length() < 3){
                prompter.printMessage(true, "Branch name must be at least 3 letters.");
                return;
            }
        }
        ((BranchesDAO)repo.getInventoryDAO(Modules.BRANCHES)).insert(new StoreBranch(name));
        int branchID = ((BranchesDAO)repo.getInventoryDAO(Modules.BRANCHES)).findByName(new StoreBranch(name)).getBranchID();
        prompter.printMessage(false, "Congratulations! A new store branch named:\n"
                                                +"'" + name + "' has been opened for business.\n" +
                                                "New branch ID is: "+branchID);
    }

    private void processCloseBranch() throws IOException {
        prompter.printMessage(null, "BEWARE, DELETING A STORE BRANCH WILL DELETE ENTIRE BRANCH STOCK DATA");
        int branchID = ((BranchesPrinter)prompter.getPrinter(Modules.BRANCHES)).chooseBranch(true);
        if (((BranchesDAO)repo.getInventoryDAO(Modules.BRANCHES)).findByKey(new StoreBranch(branchID)) != null) {
            Boolean delete = prompter.deletePrompt();
            while (delete == null) {
                prompter.printMessage(null, "Illegal Choice, please choose Y\\N\n" +
                                                        "BEWARE, DELETING A STORE BRANCH WILL DELETE ENTIRE BRANCH STOCK DATA");
                delete = prompter.deletePrompt();
            }
            if(delete) {
                ((BranchesDAO) repo.getInventoryDAO(Modules.BRANCHES)).delete(new StoreBranch(branchID));
                prompter.printMessage(false, "Branch ID "+branchID+" deleted successfully!");
            }
        } else prompter.printMessage(true, "Branch ID entered does not exist.");
    }

    private void processViewChain() {
        ((BranchesPrinter)prompter.getPrinter(Modules.BRANCHES)).printChainBranches(((BranchesDAO)repo.getInventoryDAO(Modules.BRANCHES)).findAll());
        prompter.printMessage(false, null);
    }

    private boolean processMainMenu(Date date, String day, ArrayList<String> messages, ArrayList<String> messages2)
    {
        switch(prompter.mainMenu(activeStoreBranch, date, day, messages, messages2))
        {
            case 1:
                prompter.printMessage(false, "Choose A product management option:");
                processors.get(Modules.PRODUCTS).process();
                break;
            case 2:
                prompter.printMessage(false, "Choose A category management option:");
                processors.get(Modules.CATEGORIES).process();
                break;
            case 3:
                prompter.printMessage(false, "Choose A stock management option:");
                processors.get(Modules.STOCK).process();
                break;
            case 4:
                processors.get(Modules.DISCOUNTS).process();
                break;
            case 5:
                prompter.printMessage(false, "Choose A defects management option:");
                processors.get(Modules.DEFECTS).process();
                break;
            case 6:
                prompter.printMessage(false, "Choose A report to issue:");
                processors.get(Modules.REPORTS).process();
                return false;
            case 7:
                prompter.printMessage(false, null);
                activeStoreBranch = null;
                return false;
            case 8:
                prompter.printMessage(false, "Goodbye, hope you enjoyed our system.");
                activeStoreBranch = null;
                shutdown = true;
                return false;

        }
        prompter.printMessage(false, null);
        return true;
    }

    @Override
    public String getMenuDescription() {
        return "Manage Store Inventory";
    }
}
