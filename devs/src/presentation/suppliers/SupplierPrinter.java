package presentation.suppliers;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import jline.ConsoleReader;
import logic.suppliers.models.Company;
import logic.suppliers.models.Contact;
import logic.suppliers.models.Discount;
import logic.suppliers.models.Product;
import logic.suppliers.models.Company;
import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.*;


public class SupplierPrinter {

    //Fields
    private static Scanner reader;

    //Constructor
    public SupplierPrinter() {
        AnsiConsole.systemInstall(); //To support CLI ansi colors via multiple OS
        flushScreen(null, false);
        System.out.println(ansi().bold().render(
                "@|magenta Thank you for choosing our system for your business!|@\n"));

        reader = new Scanner(System.in).useDelimiter("\n");
    }

    public void closePrinter() {
        AnsiConsole.systemUninstall();
        reader.close();
    }

    public void flushScreen(String message, boolean error) {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else System.out.print("\033[H\033[2J");//Runtime.getRuntime().exec("clear");
        } catch (final Exception e) {}
        System.out.println(ansi().eraseScreen().bold().render(
                "@|cyan GROUP 11 SUPPLIER MANAGEMENT SYSTEM!\n" +
                        "================================|@"));
        if (message != null)
            printMessage(error, message);

    }

    public void printMessage(boolean error, String message) {
        flushScreen(null, false);
        System.out.print((message != null) ? (error ? "ERROR: " : "MESSAGE: ") : "");
        if(message != null) {
            int msg = error ? 1 : 0;
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

    public int loginMenu() throws IOException {
        //flushScreen(null, false);
        Character mask = '*';
        System.out.println(ansi().bold().render("@|yellow Please choose your option  |@"));
        System.out.println(ansi().bold().render("@|yellow 1) Login to an existing supplier |@"));
        System.out.println(ansi().bold().render("@|yellow 2) Create a new supplier |@"));
        System.out.println(ansi().bold().render("@|yellow 3) Delete a supplier |@"));
        System.out.println(ansi().bold().render("@|yellow 4) Exit |@"));

        String choice = reader.next();
        return Integer.parseInt(choice.trim());
    }

    public String loginDetails() throws IOException{
        System.out.println(ansi().bold().render("@|green Login Details:\n------------|@"));
        System.out.println(ansi().bold().render("@|yellow Enter company number:  |@"));

        String companyNum = reader.next();
        return companyNum.trim();
    }

    public String Deleter() throws IOException{
        System.out.println(ansi().bold().render("@|green Delete:\n------------|@"));
        System.out.println(ansi().bold().render("@|yellow Enter company number:  |@"));
        String companyNum = reader.next();
        System.out.println(ansi().bold().render("@|yellow Are you sure you want to delete this supplier? (Y|N) |@"));
        String answere = reader.next();
        if (answere.trim().equals("Y")){
            return companyNum.trim();
        }
        return "";
    }

    public int RegisterChoice() {
        System.out.println(ansi().bold().render("@|green Enter your choice:  |@"));
        System.out.println(ansi().bold().render("@|yellow 1)Register as a supplier that comes in permanent days  |@"));
        System.out.println(ansi().bold().render("@|yellow 2)Register as a supplier that comes according to an order |@"));
        System.out.println(ansi().bold().render("@|yellow 3)Register as a supplier that requires delivery for his product |@"));
        String choice = reader.next().trim();
        try{
            int ans = Integer.parseInt(choice);
            return ans;
        }
        catch(Exception e){
            return 0;
        }
    }
    public String[] Register(){

        System.out.println(ansi().bold().render("@|green Register:\n------------|@"));
        System.out.println(ansi().bold().render("@|yellow Enter company number:  |@"));
        String companyNum = reader.next();

        System.out.println(ansi().bold().render("@|yellow Enter supplier name:  |@"));
        String supplierNum = reader.next();


        System.out.println(ansi().bold().render("@|yellow Enter bank account number:  |@"));
        String bankAccount = reader.next();


        System.out.println(ansi().bold().render("@|yellow Enter payment conditions:  |@"));
        String paymentCond = reader.next();


        System.out.println(ansi().bold().render("@|yellow Enter phone number:  |@"));
        String phoneNum = reader.next();


        System.out.println(ansi().bold().render("@|yellow You must add one contact  |@"));
        System.out.println(ansi().bold().render("@|yellow Enter contact name:  |@"));
        String contactName = reader.next();

        System.out.println(ansi().bold().render("@|yellow Enter contact cellphone:  |@"));
        String contactPhone = reader.next();

        System.out.println(ansi().bold().render("@|yellow Enter Address:  |@"));
        String address = reader.next();


        String[] ans = {companyNum.trim() , supplierNum, bankAccount, paymentCond , phoneNum,contactName,contactPhone.trim(),address};
        return ans;
    }

    public int mainMenu() {
        boolean valid = false;
        int action = 0;
        while (!valid) {
            System.out.println(ansi().bold().render("@|green == Main Menu ==|@"));
            System.out.println(ansi().bold().render("@|yellow Please choose your option  |@"));
            System.out.println(ansi().bold().render("@|yellow 1)  Edit supplier details  |@"));
            System.out.println(ansi().bold().render("@|yellow 2)  Print supplier details  |@"));
            System.out.println(ansi().bold().render("@|yellow 3)  Print product report  |@"));
            System.out.println(ansi().bold().render("@|yellow 4)  Print 'amount discount report'  |@"));
            System.out.println(ansi().bold().render("@|yellow 5)  Print contacts  |@"));
            System.out.println(ansi().bold().render("@|yellow 6)  Print presenting companies  |@"));
            System.out.println(ansi().bold().render("@|yellow 7)  Add product |@"));
            System.out.println(ansi().bold().render("@|yellow 8)  Add discount |@"));
            System.out.println(ansi().bold().render("@|yellow 9)  Add contact |@"));
            System.out.println(ansi().bold().render("@|yellow 10) Add presenting company |@"));
            System.out.println(ansi().bold().render("@|yellow 11) Delete product |@"));
            System.out.println(ansi().bold().render("@|yellow 12) Delete discount |@"));
            System.out.println(ansi().bold().render("@|yellow 13) Delete contact |@"));
            System.out.println(ansi().bold().render("@|yellow 14) Delete presenting company |@"));
            System.out.println(ansi().bold().render("@|yellow 15) Return to Login menu |@"));

            System.out.print(ansi().bold().render("@|cyan CHOICE: |@"));
            try {
                action = Integer.parseInt(reader.next().trim());
                //Validates the choice is within bounds
                if (action < 1 ||  action > 15) {
                    flushScreen("choice out of bounds", true);
                }
                else if(action == 15){
                    flushScreen("",false);
                    valid = true;
                }
                else valid = true;
            } catch (Exception e) {
                // flushScreen("Invalid menu choice!", true);
            }
        }
        return action;
    }

    public String[] editMenu() throws IOException {
        boolean valid = false;
        int choice = 0;
        while(!valid) {
            System.out.println(ansi().bold().render(
                    "@|green Choose a field to edit:\n" +
                            "------------------------\n|@"+
                            "@|yellow 1) - Supplier Name |@\n" +
                            "@|yellow 2) - Bank account |@\n" +
                            "@|yellow 3) - Payment condition |@\n" +
                            "@|yellow 4) - Phone number |@\n" +
                            "@|yellow 5) - Supplying days |@\n" +
                            "@|yellow 6) - RETURN TO MAIN MENU |@\n"));
            System.out.print(ansi().bold().render("@|green CHOICE: |@"));
            try {
                choice = Integer.parseInt(reader.next().trim());
                //Validates the choice is within bounds
                valid = (choice > 0 && choice < 7);
                if(!valid)
                    throw new NumberFormatException();
            } catch (Exception e) {
                flushScreen("Invalid selection!", true);
            }
        }
        if(choice != 6 && choice != 5) {
            System.out.println(ansi().bold().render("@|yellow Enter new value:|@"));
            return new String[]{choice + "", reader.next()};
        }
        else if(choice == 5) {
            List<String> days = new LinkedList<>();
            System.out.println(ansi().bold().render("@|yellow Enter new value:|@"));
            String day = reader.next().trim();
            while(!checkDayValidity(day)){
                System.out.println(ansi().bold().render("@|red Error: illegal input\n|@"));
                System.out.println(ansi().bold().render("@|yellow Enter new value:|@"));
                day = reader.next().trim();
            }
            while (!day.equals("q")) {
                System.out.println(ansi().bold().render("@|yellow Enter new value:|@"));
                System.out.println(ansi().bold().render("@|yellow if you want to quit enter 'q':|@"));
                if(!checkDayValidity(day)) {
                    System.out.println(ansi().bold().render("@|red Error: illegal input\n|@"));
                }
                else
                    days.add(day);
                day = reader.next().trim();
            }
            String[] s = new String[days.size() + 1];
            s[0] = choice + "";
            for (int i = 0; i< days.size() ; i++){
                s[i+1] = days.get(i);
            }
            return  s;
        }
        else return new String[]{choice + "", null};
    }

    public void printProfile(String[] details) {
        flushScreen(null, false);
        System.out.println(ansi().bold().render(
                "@|green SUPPLIER PROFILE: \n"+
                        "--------------\n|@"+
                        "@|yellow Supplier number number: |@"+details[0]+
                        "\n@|yellow Name: |@"+details[1]+
                        "\n@|yellow Bank account: |@"+details[2]+
                        "\n@|yellow Payment condition: |@"+details[3]+
                        "\n@|yellow Phone number: |@"+details[4]+
                        "\n\n@|cyan PRESS 'Q' KEY TO RETURN |@"));
        try {
            reader.next();
        }
        catch(Exception e) {}
        flushScreen(null, false);
    }

    public void printProfileType1(String[] details , LinkedList<String> days) {
        flushScreen(null, false);
        System.out.println(ansi().bold().render(
                "@|green SUPPLIER PROFILE: \n"+
                        "--------------\n|@"+
                        "@|yellow Supplier number number: |@"+details[0]+
                        "\n@|yellow Name: |@"+details[1]+
                        "\n@|yellow Bank account: |@"+details[2]+
                        "\n@|yellow Payment condition: |@"+details[3]+
                        "\n@|yellow Phone number: |@"+details[4]));
        for(int i=0 ; i<days.size() ; i++){
            System.out.println(days.get(i));
        }
        System.out.println(ansi().bold().render("\n\n@|cyan PRESS 'Q' KEY TO RETURN |@"));

        try {
            reader.next();
        }
        catch(Exception e) {}
        flushScreen(null, false);
    }

    public void printProduct(List<Product> products) {
        flushScreen(null, false);
        Product temp;
        int i=0;
        while(i < products.size()) {
            temp =products.get(i);
            System.out.println(ansi().bold().render(
                    "@|green Product: \n" +
                            "--------------\n|@" +
                            "@|yellow Supplier number: |@" + temp.getSupplierNum() +
                            "\n@|yellow Code: |@" + temp.getCode() +
                            "\n@|yellow Description: |@" + temp.getDesc() +
                            "\n@|yellow Price: |@" + temp.getPrice() +
                            "\n@|yellow Amount: |@" + temp.getAmount()));
            i++;
        }
        System.out.println(ansi().bold().render("\n\n@|cyan PRESS 'Q' TO RETURN |@"));
        try {
            reader.next();
        }
        catch(Exception e) {}
        flushScreen(null, false);
    }

    public String[] addProduct() throws IOException{
        System.out.println(ansi().bold().render("@|green Add Product:\n------------|@"));

        System.out.println(ansi().bold().render("@|yellow Enter code:  |@"));
        String code = reader.next();
        code = code.trim();

        System.out.println(ansi().bold().render("@|yellow Enter  description :  |@"));
        String description  = reader.next();

        System.out.println(ansi().bold().render("@|yellow Enter price :  |@"));
        String price = reader.next();

        System.out.println(ansi().bold().render("@|yellow Enter amount:  |@"));
        String amount = reader.next();


        String[] ans = {code, description, price.trim(), amount.trim()};
        return ans;
    }

    public void printDiscount(List<Discount> discounts) {
        flushScreen(null, false);
        Discount temp;
        int i=0;
        while(i < discounts.size()) {
            temp = discounts.get(i);
            System.out.println(ansi().bold().render(
                    "\n@|green Discount: \n" +
                            "--------------\n|@" +
                            "@|yellow supplier number: |@" + temp.getSupplierNum() +
                            "\n@|yellow Code: |@" + temp.getCode() +
                            "\n@|yellow Min amount: |@" + temp.getMinAmount() +
                            "\n@|yellow Discount: |@" + temp.getDiscount()));
            i++;
        }
        System.out.println(ansi().bold().render("\n@|cyan PRESS 'Q' TO RETURN |@"));
        try {
            reader.next();
        }
        catch(Exception e) {}
        flushScreen(null, false);
    }

    public String[] addDiscount() throws IOException{
        System.out.println(ansi().bold().render("@|green Add Discount:\n------------|@"));

        System.out.println(ansi().bold().render("@|yellow Enter code:  |@"));
        String code = reader.next();

        System.out.println(ansi().bold().render("@|yellow Enter  min amount :  |@"));
        String minAmount  = reader.next();

        System.out.println(ansi().bold().render("@|yellow Enter discount :  |@"));
        String discount = reader.next();


        String[] ans = { code, minAmount.trim(), discount.trim()};
        return ans;
    }

    public void printContacts(List<Contact> contacts) {
        flushScreen(null, false);
        if(contacts != null) {
            Contact temp;
            int i = 0;
            while (i < contacts.size()) {
                temp = contacts.get(i);
                System.out.println(ansi().bold().render(
                        "@|green Contact: \n" +
                                "--------------\n|@" +
                                "@|yellow supplier number: |@" + temp.getSupplierNum() +
                                "\n@|yellow cellphone: |@" + temp.getPhoneNum() +
                                "\n@|yellow name: |@" + temp.getName()));
                i++;
            }
        }
        System.out.println(ansi().bold().render("\n@|cyan PRESS 'Q' TO RETURN |@"));
        try {
            reader.next();
        }
        catch(Exception e) {}
        flushScreen(null, false);

    }

    public String[] addContact() {
        System.out.println(ansi().bold().render("@|green Add Contact:\n------------|@"));

        System.out.println(ansi().bold().render("@|yellow Enter cellphone:  |@"));
        String cellphone = reader.next().trim();

        System.out.println(ansi().bold().render("@|yellow Enter  name :  |@"));
        String name  = reader.next();



        String[] ans = { cellphone , name };
        return ans;

    }

    public String DeleteProduct() {
        System.out.println(ansi().bold().render("@|green Delete Product:\n------------|@"));
        System.out.println(ansi().bold().render("@|yellow Enter product code:  |@"));
        String code = reader.next();
        System.out.println(ansi().bold().render("@|yellow Are you sure you want to delete this product? (Y|N) |@"));
        String answere = reader.next().trim();
        if (answere.equals("Y")){
            return code;
        }
        return "";
    }

    public String DeleteDiscount() {

        System.out.println(ansi().bold().render("@|green Delete Discount:\n------------|@"));
        System.out.println(ansi().bold().render("@|yellow Enter product code:  |@"));
        String code = reader.next();
        System.out.println(ansi().bold().render("@|yellow Are you sure you want to delete this discount? (Y|N) |@"));
        String answere = reader.next();
        if (answere.trim().equals("Y")){
            return code;
        }
        return "";
    }

    public String DeleteContact() {
        System.out.println(ansi().bold().render("@|green Delete Contact:\n------------|@"));
        System.out.println(ansi().bold().render("@|yellow Enter product cellphone:  |@"));
        String cellphone = reader.next().trim();
        System.out.println(ansi().bold().render("@|yellow Are you sure you want to delete this contact? (Y|N) |@"));
        String answer = reader.next();
        if (answer.trim().equals("Y")){
            return cellphone;
        }
        return "";

    }

    public String[] addCompany() {

        System.out.println(ansi().bold().render("@|green Add Presenting Company:\n------------|@"));

        System.out.println(ansi().bold().render("@|yellow Enter company number:  |@"));
        String companyNum = reader.next().trim();

        System.out.println(ansi().bold().render("@|yellow Enter  company name :  |@"));
        String name  = reader.next();



        String[] ans = { companyNum , name };
        return ans;
    }

    public void printCompanies(List<Company> companies) {
        flushScreen(null, false);
        if(companies != null) {
            Company temp;
            int i = 0;
            while (i < companies.size()) {
                temp = companies.get(i);
                System.out.println(ansi().bold().render(
                        "@|green Contact: \n" +
                                "--------------\n|@" +
                                "@|yellow company number: |@" + temp.getCompanyNum() +
                                "\n@|yellow name: |@" + temp.getName()));
                i++;
            }
        }
        System.out.println(ansi().bold().render("\n@|green PRESS 'Q' TO RETURN |@"));
        try {
            reader.next();
        }
        catch(Exception e) {}
        flushScreen(null, false);

    }

    public String DeleteCompany() {
        System.out.println(ansi().bold().render("@|green Delete Company:\n------------|@"));
        System.out.println(ansi().bold().render("@|yellow Enter company number:  |@"));
        String companyNum = reader.next().trim();
        System.out.println(ansi().bold().render("@|yellow Are you sure you want to delete this contact? (Y|N) |@"));
        String answer = reader.next().trim();
        if (answer.equals("Y")){
            return companyNum;
        }
        return "";
    }


    public LinkedList<String> RegisterDays() {
        LinkedList<String> days = new LinkedList<>();
        System.out.println(ansi().bold().render("@|yellow Enter Delivery day( first letter must be a capital letter ):  |@"));
        String day = reader.next().trim();
        while(!day.equals("q")){
            if(!checkDayValidity(day))
                System.out.println(ansi().bold().render("@|red illegal input  |@"));
            else
            if(!days.contains(day))
                days.add(day);
            else
                System.out.println(ansi().bold().render("@|red You already entered that day  |@"));
            System.out.println(ansi().bold().render("@|yellow Enter Delivery day( first letter must be a capital letter ):  |@"));
            System.out.println(ansi().bold().render("@|yellow Enter enter 'q' if you want to quit  |@"));
            day = reader.next().trim();

        }
        return days;
    }

    private boolean checkDayValidity(String day) {
        switch (day){
            case "Sunday":
                return true;
            case "Monday":
                return true;
            case "Tuesday":
                return true;
            case "Wednesday":
                return true;
            case "Thursday":
                return true;
            case "Friday":
                return true;
            case "Saturday":
                return true;
            case "q":
                return true;
            default:
                return false;
        }


    }


}