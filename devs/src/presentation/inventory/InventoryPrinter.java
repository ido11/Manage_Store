package presentation.inventory;

import java.io.IOException;
import java.util.*;

import jline.ConsoleReader;
import logic.Modules;
import logic.deliveries.models.Branch;
import logic.inventory.models.*;
import logic.suppliers.models.Product;
import logic.inventory.datatypes.Date;
import logic.suppliers.models.SupplierType1;
import org.fusesource.jansi.AnsiConsole;
import presentation.inventory.TableList;
import presentation.inventory.printers.*;


import static org.fusesource.jansi.Ansi.*;

/**
 * A class used to print and get information from end-user.
 * Used for user communication with the CLI.
 */
public class InventoryPrinter {

    //Fields
    public final int ILLEGAL = -1;
    public final int RETURN = 0;
    private static boolean initiated = false;

    private final int MAX_BARCODE_LENGTH = 9;
    protected static ConsoleReader reader;
    protected static Scanner scan = new Scanner(System.in);

    private static Map<Modules, Printer> printers = new HashMap<>() {{
        put(Modules.BRANCHES, new BranchesPrinter());
        put(Modules.PRODUCTS, new ProductsPrinter());
        put(Modules.DISCOUNTS, new DiscountsPrinter());
        put(Modules.CATEGORIES, new CategoryPrinter());
        put(Modules.STOCK, new StockPrinter());
        put(Modules.REPORTS, new ReportsPrinter());
        put(Modules.DEFECTS, new DefectsPrinter());
    }};


    //Constructor
    public InventoryPrinter()
    {
        if(!initiated) {
            AnsiConsole.systemInstall(); //To support CLI ansi colors via multiple OS
            try {
                reader = new ConsoleReader();
            } catch (IOException e) {
                e.printStackTrace();
            }
            flushScreen(null, false);
            System.out.println(ansi().bold().render(
                    "@|magenta Thank you for choosing our system for your business!|@\n"));
            initiated = true;
        }
    }

    /**
     * Closes the printer object
     */
    public void closePrompter()
    {
        AnsiConsole.systemUninstall();
        scan.close();
    }

    /**
     * Clears the screen and prints program title
     * @param message message to print below the title
     * @param error determines whether the message provided is an error message
     */
    private void flushScreen(String message, boolean error) {
        try
        {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else System.out.print("\033[H\033[2J");//Runtime.getRuntime().exec("clear");
        }
        catch (final Exception e) {}
        System.out.println(ansi().eraseScreen().bold().render(
                            "@|cyan GROUP 11 Inventory Management System!\n" +
                            "=======================================|@"));
        if(message != null)
            printMessage(error, message);

    }

    public int menuSelection(int lowerBound, int upperBound)
    {
        int action = 0;
        System.out.print(ansi().bold().render("@|cyan CHOICE: |@"));
        try {
            action = Integer.parseInt(reader.readLine());
            if (action < lowerBound || action > upperBound)
                throw new IllegalArgumentException();
        } catch (Exception e) {
            flushScreen("Invalid menu choice!", true);
        }
        return action;
    }

    public int mainPrompt()
    {
        boolean valid = false;
        while (!valid) {
            System.out.println(ansi().eraseScreen().bold().render(
                    "@|cyan GROUP 11 Inventory & Suppliers system!\n" +
                            "=======================================|@"));
            System.out.println(ansi().bold().render("@|yellow Choose a system to manage: |@"));
            System.out.println(ansi().bold().render(
                    "@|green === System Menu === |@\n" +
                            "@|yellow 1|@ - Inventory\n" +
                            "@|yellow 2|@ - Suppliers\n" +
                            "@|yellow 3|@ - Exit\n"));
            return menuSelection(1, 3);
        }
        return ILLEGAL;
    }


    /**
     * Prints the main menu that is relevant to the connected user
     * determined by the @param master.
     * @return The menu choice given by the user.
     */
    public int mainMenu(StoreBranch branch, Date date, String day, ArrayList<String> periodic, ArrayList<String> deliveries) {
        boolean valid = false;
        while (!valid) {
            System.out.println(ansi().bold().render("@|magenta Currently connected to store: |@" + branch.getName() +
                                                    "@|magenta \nStore Branch No. |@" + branch.getBranchID() +
                                                    "@|magenta \nCurrent date: |@" + date.toString() + "@|magenta \nCurrent day: |@"+ day +""));
            boolean first = true;
            if(periodic.size() > 1) {
                for (String m : periodic)
                    if (first) {
                        System.out.println(ansi().bold().render("@|yellow " + m + "|@"));
                        first = false;
                    } else
                        System.out.println(ansi().render("@|cyan " + m + "|@"));
                System.out.println("\n");
            }

            if(deliveries.size() > 1) {
                for (String m : deliveries)
                    if (first) {
                        System.out.println(ansi().bold().render("@|yellow " + m + "|@"));
                        first = false;
                    } else
                        System.out.println(ansi().render("@|cyan " + m + "|@"));
                System.out.println("\n");
            }
            else System.out.println("\n");

            System.out.println(ansi().bold().render("@|yellow Enter a field to manage: |@"));
            System.out.println(ansi().bold().render(
                    "@|green === Management Menu === |@\n" +
                    "@|yellow 1|@ - Products\n" +
                    "@|yellow 2|@ - Product Categories\n" +
                    "@|yellow 3|@ - Branch Stock\n" +
                    "@|yellow 4|@ - Chain Store Discounts\n" +
                    "@|yellow 5|@ - Defected products\n" +
                    "@|yellow 6|@ - Reports And Notifications\n" +
                    "@|yellow 7|@ - Switch Branch\n" +
                    "@|yellow 8|@ - Exit\n"));
            return menuSelection(1, 7);
        }
        return ILLEGAL;
    }

    /**
     * A method used to flush the current screen and add a message in the top of the new screen
     * @param error Determines if the @param message given is an error message
     * @param message The message to be shown on top of the new screen, if null nothing will be printed.
     */
    public void printMessage(Boolean error, String message) {
        flushScreen(null, false);
        System.out.print((message != null) ? ((error==null) ? "WARNING: " : error ? "ERROR: " : "MESSAGE: ") : "");
        if(message != null) {
            int msg = (error == null) ? 1 : error ? 1 : 0;
            switch (msg) {
                case 0:
                    System.out.print(ansi().render("@|green " + message + "|@\n\n"));
                    break;
                case 1:
                    System.out.print(ansi().render("@|red " + message + "|@\n\n"));
                    break;
            }
        }
    }

    public int barcodePrompt() {
        boolean valid = false;
        int barcode;
        while(!valid) {
            System.out.print(ansi().render("@|red Enter (q\\Q) to cancel |@\n"));
            System.out.print(ansi().render("@|yellow Enter product BARCODE number: |@\n"));
            try {
                String code = reader.readLine();
                if(code.trim().equals("q") || code.trim().equals("Q"))
                    return RETURN;
                else if(code.length() > 9)
                    throw new IllegalArgumentException();
                else if((barcode = Integer.parseInt(code))> 0)
                    return barcode;
                else throw new IllegalArgumentException();
            } catch (Exception e) {
                printMessage(true, "Illegal barcode entered!\n" +
                        "Barcode length must be a numeric value greater than 0 with max "+ MAX_BARCODE_LENGTH + " digits.");
            }
        }
        return ILLEGAL;
    }

    public String nameSearchPrompt() {
        boolean valid = false;
        while(!valid) {
            System.out.print(ansi().render("@|red Enter (q\\Q) to cancel |@\n"));
            System.out.print(ansi().render("@|yellow Enter A name to search by: |@\n"));
            try {
                String name = reader.readLine();
                if(name.trim().equals("q") || name.trim().equals("Q"))
                    return null;
                return name;
            } catch (Exception e) {
                printMessage(true, "Illegal name entered.");
            }
        }
        return null;
    }

    public int orderAmountPrompt(Product prod, int min) {
        boolean valid = false;
        Integer toOrder = null;
        while(!valid) {
            System.out.print(ansi().render("@|cyan Order for product: "+prod.getCode() +" from supplier: "+ prod.getSupplierNum() +" |@\n"));
            System.out.print(ansi().render("@|yellow Enter amount to order (MIN "+min+", MAX "+prod.getAmount()+", 0 to deny): |@\n"));
            try {
                String amount = reader.readLine();
                toOrder = Integer.parseInt(amount);
                if(toOrder > prod.getAmount() || (toOrder < min && toOrder != 0))
                    throw new IllegalArgumentException();
                else valid = true;
            } catch (Exception e) {
                printMessage(true, "Illegal amount to order entered. (Check not exceeding max amount and above min amount)");
            }
        }
        return toOrder;
    }

    public int byBarcodeNamePrompt(String manegement, String title) {
        boolean valid = false;
        while (!valid) {
            System.out.println(ansi().bold().render(
                            "@|green == " + manegement + " management menu ==|@\n" +
                            "@|yellow "+title+" |@\n" +
                            "@|yellow 1|@ - Barcode\n" +
                            "@|yellow 2|@ - Searching by name\n" +
                            "@|yellow 3|@ - CANCEL\n"));
           return menuSelection(1, 3);
        }
        return ILLEGAL;
    }

    public Boolean deletePrompt() {
        System.out.print(ansi().bold().render(
                "@|red \nARE YOU SURE YOU WANT TO DELETE? Y\\N: |@"));
        try {
            String ans = reader.readLine();
            return (ans.equals("Y") || ans.equals("y")) ? true :
                    (ans.equals("N") || ans.equals("n")) ? false : null;
        } catch (Exception e) {}
        return null;
    }

    public Boolean supplyPrompt(SupplierType1 sup, String day) {
        String ans;
        Boolean response = null;
        while(response == null) {
            System.out.print(ansi().bold().render(
                    "@|yellow \nSupplier " + sup.getSupplierNum() + " will supply tomorrow (" + day + ") do you want to place order? Y\\N: |@"));
            try {
                ans = reader.readLine();
                response = (ans.equals("Y") || ans.equals("y")) ? true :
                        (ans.equals("N") || ans.equals("n")) ? false : null;
                return response;
            } catch (Exception e) {
            }
        }
        return null;
    }



    public void promptNotifications(List<StockProducts> expired, List<StockProducts> critical_amount, Date currentDate) {
        printMessage(false, null);
        System.out.print(ansi().bold().render("@|yellow NOTIFICATIONS: |@\n"));
        if(expired.size() != 0) {
            int expired_amount = 0;
            for(StockProducts p : expired)
                expired_amount += p.getQuantity();
            System.out.print(ansi().render("@|yellow \nCurrently found |@" + expired_amount + " @|yellow expired products|@"));
            System.out.print(ansi().render("@|yellow \nWill be moved automatically to defects section.|@"));
            System.out.print(ansi().render("@|cyan \nNOTE: Current date is: "+currentDate+"|@\n"));
            TableList expiredTable = new TableList(5,
                    "Product Barcode", "Product Name", "Quantity", "Expiration Date", "Location")
                    .sortBy(1).withUnicode(false);
            expired.forEach(ex -> {
                if (ex.getQuantity() > 0)
                    expiredTable.addRow(ex.getBarcode() + "", ex.getName(),
                            ex.getQuantity() + "", ex.getExpiration_date().toString(), ex.getLocation()); });
            expiredTable.print();
        }
        if(critical_amount.size() != 0) {
            System.out.print(ansi().render("@|yellow \nCurrently found |@" + critical_amount.size() + " @|yellow product barcodes that are about to go out of stock.|@"));
            System.out.print(ansi().render("@|cyan \nNOTE: Expired products are not included.|@\n"));
            TableList criticalTable = new TableList(6,
                    "Product Barcode", "Product Name", "Store Quantity", "Warehouse Quantity", "Total Quantity", "Minimal Amount")
                    .sortBy(1).withUnicode(false);
            critical_amount.forEach(crit -> {
                        if (crit.getQuantity() >= 0)
                                criticalTable.addRow(crit.getBarcode() + "", crit.getName(),
                                crit.getStore_quantity()+"", crit.getWarehouse_quantity()+" Units", crit.getQuantity() + " Units", crit.getMinimal_amount() + " Units"); });
            criticalTable.print();
        }

        System.out.print(ansi().bold().render("@|yellow \nPress ENTER to continue to menu. |@\n"));
        scan.nextLine();
    }

    public Printer getPrinter(Modules module)
    {
        return printers.get(module);
    }

    public void promptNoProductsSupplier(int supplier_num) {
        System.out.print(ansi().render("@|red No products currently available from supplier |@"+supplier_num));
        System.out.print(ansi().bold().render("@|yellow \nPress ENTER to continue. |@\n"));
        scan.nextLine();
    }

    public Branch getBranchContactDetails(logic.inventory.models.StoreBranch branch) {
        try {
            String name = "", phone ="", address = "", addressArea ="";
            System.out.print(ansi().render("@|yellow Do you want to place an order? (If previously made an order no need to re-place) : Y/N  |@\n"));
            String ans = reader.readLine();
            if(ans.equals("Y") || ans.equals("y")) {
                System.out.print(ansi().render("@|yellow Enter delivery contact name: |@\n"));
                name = reader.readLine();
                System.out.print(ansi().render("@|yellow Enter contact name phone number: |@\n"));
                phone = reader.readLine();
                System.out.print(ansi().render("@|yellow Enter delivery address: |@\n"));
                address = reader.readLine();
                System.out.print(ansi().render("@|yellow Enter delivery address area: |@\n"));
                addressArea = reader.readLine();
                return new Branch(address, phone, name, addressArea,branch.getBranchID());
            } else if(ans.equals("N") || ans.equals("n"))
                return null;
        } catch (Exception e) {
            printMessage(true, "Illegal name entered.");
        }
        return null;
    }

    public int printHowManyDefected(String date, int barcode, int amount) {
        try {
            System.out.print(ansi().render("@|cyan Order for product barcode "+barcode+" (amount "+amount+") arrived by delivery for date "+date+". |@\n"));
            System.out.print(ansi().render("@|yellow Enter amount of defected products arrived: |@\n"));
            int defected = Integer.parseInt(reader.readLine());
            return defected;
        } catch (Exception e) {
            printMessage(true, "Illegal name entered.");
        }
        return 0;
    }

    public void waitForEnter()
    {
        System.out.print(ansi().render("@|cyan Press ENTER to continue. |@\n"));
        scan.nextLine();
    }
}
