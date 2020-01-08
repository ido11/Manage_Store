package logic.suppliers;

import logic.Interactor;
import logic.employees.models.Employee;
import logic.suppliers.models.*;
import presentation.suppliers.SupplierPrinter;
import presistence.Repository;
import presistence.dao.suppliers.*;
import logic.Modules;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class SupplierInteractor implements Interactor {

    private boolean shutdown; //Determines whether the programs needs to shutdown.
    private static Supplier connected = null; //Current connected user instance
    private Repository repo; //A repository instance used to communicate with database
    private static SupplierPrinter printer; //A CLI printer instance used to present and retrieve date to and from end-user

    //Constructor
    public SupplierInteractor(SupplierPrinter printer, Repository repo)
    {
        this.repo = repo;
        this.printer = printer;
        this.shutdown = false;
    }

    public void start(Employee emp)
    {
        try {
            while(!shutdown) {
                while (!processLogin()){
                    //While no user is connected prompt user to login
                }
                if (!shutdown) {
                    if(connected != null) {
                        printer.printMessage(false, "Connected Successfully!");
                    }
                    else{
                        printer.printMessage(false, "Deleted Successfully!");
                    }
                    while (connected != null && processMainMenu())
                        ; //As long as the user is connected prompt the main menu options.
                }
            }
            //After user chose to exit, close all object instances.
            printer.printMessage(false, "Have a good day!");
            shutdown = false;
            shutdown = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeSupplierPrompter()
    {
        printer.closePrinter();
    }

    public Supplier checkSupplierValidity(int supplierNum)
    {
        Supplier supplier = (Supplier) repo.getSupplierDAO(Modules.SUPPLIER).findByKey(new Supplier(supplierNum));
        if(supplier != null) {
            connected = supplier;
        }
        else return null;
        return supplier;
    }

    private boolean processLogin () throws IOException
    {
        int choice =  printer.loginMenu();
        if(choice == 1){
                String Num = printer.loginDetails();
                int suppNum = 0;
                try{
                    suppNum = Integer.parseInt(Num);
                }
                catch (Exception e){
                    printer.printMessage(true , "supplier number contains digits only");
                    return false;
                }
                Supplier supplier = checkSupplierValidity(suppNum);
                if(supplier == null){
                    printer.printMessage(true , "supplier not found");
                    /*
                    compNum =  printer.loginDetails();
                    supplier = checkSupplierValidity(compNum);
                */
                    return false;
                }
                connected = supplier;
                return true;
        }
        else if(choice == 2){
                int c= printer.RegisterChoice();
                if(c == 0 || c < 1 || c > 3){
                    printer.printMessage(true , "illegal choice");
                    return false;
                }
                String[] details = printer.Register();
                LinkedList<String> days = new LinkedList<>();
                try{
                    Integer.parseInt(details[0]);
                    Integer.parseInt(details[6]);
                }
                catch(Exception e){
                    printer.printMessage(true , "supplier number and contact phonecall contains digits only");
                    return false;
                }
                if(c == 1){
                    days = printer.RegisterDays();
                }
                Supplier supplier ;
                logic.deliveries.models.Supplier delSup = new logic.deliveries.models.Supplier(details[7],details[6],details[5],"SOUTH",Integer.parseInt(details[0])); //shauli's supplier

                //creating the new supplier
                if(c == 1) {
                    supplier = new SupplierType1(Integer.parseInt(details[0]), details[1], details[2], details[3], details[4], days);
                    connected = supplier;
                }
                else if(c == 2){
                    supplier = new SupplierType2(Integer.parseInt(details[0]), details[1], details[2], details[3], details[4]);
                    connected = supplier;
                }
                else{
                    supplier = new SupplierType3(Integer.parseInt(details[0]), details[1], details[2], details[3], details[4]);
                    connected = supplier;
                }
                Contact contact = new Contact(connected.getSupplierNum() , details[5] , details[6]);

                //checking if the supplier already exist
                if(repo.getSupplierDAO(Modules.SUPPLIER).findByKey(supplier) != null){
                    printer.printMessage(true, "supplier already exist");
                    return false;
                }

                //inserting the new supplier to the data base
                if(c == 1)
                    repo.getSupplierDAO(Modules.SUPPLIER1).insert(supplier);
                else if(c == 2)
                    repo.getSupplierDAO(Modules.SUPPLIER2).insert(supplier);
                else
                    repo.getSupplierDAO(Modules.SUPPLIER3).insert(supplier);
                repo.getSupplierDAO(Modules.CONTACTS).insert(contact);
                (new presistence.dao.deliveries.SupplierDAO()).insert(delSup);
                return true;
        }
        else if(choice == 3) {
            String details = printer.Deleter();
            int toDelete = 0;
            try{
                toDelete = Integer.parseInt(details);
            }
            catch (Exception e){
                printer.printMessage(true , "supplier number contains digits only");
                return false;
            }
            if (details == "")
                return false;
            Supplier supplier = new Supplier(toDelete);
            if(repo.getSupplierDAO(Modules.SUPPLIER).findByKey(supplier) != null) {
                repo.getSupplierDAO(Modules.SUPPLIER).delete(supplier);
            }
            else{
                printer.printMessage(true, "supplier not found!");
                return false;
            }
            return true;
        }
        else if(choice ==4){
            shutdown = true;
            return true;
    }
        else{
                printer.printMessage(true , "choice out of bound");
                return false;
        }
    }

    /**
     * Calls each method by the menu selection the end-user made.
     * @return true while user is still connected, false otherwise
     * @throws IOException
     */
    private boolean processMainMenu() throws IOException {
        switch (printer.mainMenu()) {
            case 1:
                processEditMenu();
                break;
            case 2:
                processViewProfile();
                break;
            case 3:
                processViewProduct();
                break;
            case 4:
                processViewDiscount();
                break;
            case 5:
                processViewContact();
                break;
            case 6:
                processViewPresentingCompany();
                break;
            case 7:
                processAddProduct();
                break;
            case 8:
                processAddDiscount();
                break;
            case 9:
                processAddContact();
                break;
            case 10:
                processAddPresentingCompany();
                break;
            case 11:
                processDeleteProduct();
                break;
            case 12:
                processDeleteDiscount();
                break;
            case 13:
                processDeleteContact();
                break;
            case 14:
                processDeleteCompany();
                break;
            case 15:
                connected = null;
                return false;
            default:
                break;
        }
        return true;
    }

    private void processEditMenu() throws IOException {
        int supplierNum = connected.getSupplierNum();
        int type = 1;
        SupplierType1 sup1 = new SupplierType1(supplierNum);
        SupplierType2 sup2 = new SupplierType2(supplierNum);
        SupplierType3 sup3 = new SupplierType3(supplierNum);
        if(((SupplierType1DAO)repo.getSupplierDAO(Modules.SUPPLIER1)).isSupplier1(new Supplier(supplierNum))){
            sup1 = (SupplierType1)repo.getSupplierDAO(Modules.SUPPLIER1).findByKey(sup1);
        }
        else if(((SupplierType2DAO)repo.getSupplierDAO(Modules.SUPPLIER2)).isSupplier2(new Supplier(supplierNum))){
            sup2 = (SupplierType2)repo.getSupplierDAO(Modules.SUPPLIER2).findByKey(sup2);
            type = 2;
        }
        else{
            sup3 = (SupplierType3)repo.getSupplierDAO(Modules.SUPPLIER3).findByKey(sup3);
            type = 3;
        }
        if (sup1 != null || sup2 != null || sup3 != null) {
            boolean edit = true;
            while (edit) {
                String[] toEdit = printer.editMenu();
                int choice = Integer.parseInt(toEdit[0]);
                String value = toEdit[1];
                //Update user details in the selected parameter with the new inserted value
                switch (choice) {
                    case 1:
                        sup1.setName(value);
                        if(type == 1) {
                            sup1.setName(value);
                            repo.getSupplierDAO(Modules.SUPPLIER1).update(sup1, Integer.toString(supplierNum), 0, 0);
                        }
                        else if(type == 2) {
                            sup2.setName(value);
                            repo.getSupplierDAO(Modules.SUPPLIER2).update(sup2, Integer.toString(supplierNum), 0, 0);
                        }
                        else {
                            sup3.setName(value);
                            repo.getSupplierDAO(Modules.SUPPLIER3).update(sup3, Integer.toString(supplierNum), 0, 0);
                        }
                        break;
                    case 2:
                        if(type == 1) {
                            sup1.setBankAccount(value);
                            repo.getSupplierDAO(Modules.SUPPLIER1).update(sup1, Integer.toString(supplierNum), 0, 0);
                        }
                        else if(type == 2) {
                            sup2.setBankAccount(value);
                            repo.getSupplierDAO(Modules.SUPPLIER2).update(sup2, Integer.toString(supplierNum), 0, 0);
                        }
                        else {
                            sup2.setBankAccount(value);
                            repo.getSupplierDAO(Modules.SUPPLIER3).update(sup3 , Integer.toString(supplierNum), 0, 0);
                        }
                        break;
                    case 3:
                        if(type == 1) {
                            sup1.setPaymentCond(value);
                            repo.getSupplierDAO(Modules.SUPPLIER1).update(sup1, Integer.toString(supplierNum), 0, 0);
                        }
                        else if(type == 2) {
                            sup2.setPaymentCond(value);
                            repo.getSupplierDAO(Modules.SUPPLIER2).update(sup2, Integer.toString(supplierNum), 0, 0);
                        }
                        else {
                            sup3.setPaymentCond(value);
                            repo.getSupplierDAO(Modules.SUPPLIER3).update(sup3 , Integer.toString(supplierNum), 0, 0);
                        }
                        break;
                    case 4:
                        if(type == 1) {
                            sup1.setPhoneNum(value);
                            repo.getSupplierDAO(Modules.SUPPLIER1).update(sup1, Integer.toString(supplierNum), 0, 0);
                        }
                        else if(type == 2) {
                            sup2.setPhoneNum(value);
                            repo.getSupplierDAO(Modules.SUPPLIER2).update(sup2, Integer.toString(supplierNum), 0, 0);
                        }
                        else {
                            sup3.setPhoneNum(value);
                            repo.getSupplierDAO(Modules.SUPPLIER3).update(sup3 , Integer.toString(supplierNum), 0, 0);
                        }
                        break;
                    case 5:
                        LinkedList<String> s = new LinkedList<>();
                        LinkedList<String> prevS = sup1.getDays();
                        for(int i = 1; i<toEdit.length ; i++){
                            s.add(toEdit[i]);
                        }

                        if(type == 1) {
                            sup1.setDays(s);
                            ((SupplierType1DAO) repo.getSupplierDAO(Modules.SUPPLIER1)).updateDays(sup1, prevS);
                        }
                        else
                            printer.printMessage(true, "Supplier type doesnt contain Suppliying days");
                        break;
                    case 6:
                        edit = false;
                        printer.printMessage(false, null);
                        break;
                    default:
                        printer.printMessage(true, "Invalid selection!");
                        break;
                }
            }
        }
        else printer.printMessage(true, "Username does not exist!");
    }

    private void processViewProfile() throws IOException {

        int supplierNum = connected.getSupplierNum();
        Supplier supplier = (SupplierType1) repo.getSupplierDAO(Modules.SUPPLIER1).findByKey(new SupplierType1(supplierNum));
        if(supplier == null){
            supplier = (SupplierType2) repo.getSupplierDAO(Modules.SUPPLIER2).findByKey(new SupplierType2(supplierNum));
        }
        if(supplier == null){
            supplier = (SupplierType3) repo.getSupplierDAO(Modules.SUPPLIER3).findByKey(new SupplierType3(supplierNum));
        }


        String[] details = {Integer.toString(supplierNum), supplier.getName(), supplier.getBankAccount(), supplier.getPaymentCond(), supplier.getPhoneNum()};
        if(((SupplierType1DAO)repo.getSupplierDAO(Modules.SUPPLIER1)).isSupplier1(supplier)){
            printer.printProfileType1(details, ((SupplierType1)supplier).getDays());
        }
        else {
            printer.printProfile(details);
        }
    }

    private void processViewProduct() throws IOException{
        int supplierNum = connected.getSupplierNum();
        List<Product> products = ((ProductDAO)repo.getSupplierDAO(Modules.SUPPLIERPRODUCTS)).findByKeys(new logic.suppliers.models.Product(supplierNum));
        printer.printProduct(products);
    }

    private void processAddProduct() throws IOException{
        String[] details = printer.addProduct();
        try{
            Integer.parseInt(details[2]);
            Integer.parseInt(details[3]);
            Product product = new Product(  details[0] , details[1] , connected.getSupplierNum() ,  Integer.parseInt(details[2]) , Integer.parseInt(details[3]));
            if (repo.getSupplierDAO(Modules.SUPPLIERPRODUCTS).findByKey(product) != null){
                printer.printMessage(false, "product already exist");
            }
            else {
                Discount discount = ((DiscountDAO) repo.getSupplierDAO(Modules.DISCOUNT)).findByKey(new Discount(connected.getSupplierNum(), product.getCode(), 0, 0));
                boolean disc = false;
                double newPrice = 2;
                if (discount != null && discount.getMinAmount() <= product.getAmount()) {
                    double a = discount.getDiscount();
                    double b = a / 100;
                    double c = (1 - b);
                    double d = product.getPrice();
                    newPrice = c * d;

                    product.setPrice((int) newPrice);
                    disc = true;
                }
                repo.getSupplierDAO(Modules.SUPPLIERPRODUCTS).insert(product);
                if (disc) {
                    printer.printMessage(false, "You deserve a discount! the new price for your purchase is " + newPrice);
                }
            }
        }
        catch(Exception e){
            printer.printMessage(false, "Price and Amount contains digits only!");
        }

    }


    private void processViewDiscount() throws IOException{
        int supplierNum = connected.getSupplierNum();
        List<Discount> discounts = ((DiscountDAO)repo.getSupplierDAO(Modules.DISCOUNT)).findByKeys(new Discount(supplierNum));
        printer.printDiscount(discounts);
    }

    private void processAddDiscount() throws IOException {
        String[] details = printer.addDiscount();
        try{
            Integer.parseInt(details[1]);
            Integer.parseInt(details[2]);
            Discount discount = new Discount(connected.getSupplierNum(), details[0], Integer.parseInt(details[1]), Integer.parseInt(details[2]));
            if (repo.getSupplierDAO(Modules.DISCOUNT).findByKey(discount) != null){
                printer.printMessage(false, "discount already exist");
            }
            else{
                repo.getSupplierDAO(Modules.DISCOUNT).insert(discount);
            }
        }
        catch(Exception e){
            printer.printMessage(false, "Amount and discount percent contains digits only!");
        }

    }


    private void processViewContact() {
        int supplierNum = connected.getSupplierNum();
        List<Contact> contacts = ((ContactDAO)repo.getSupplierDAO(Modules.CONTACTS)).findByKeys(new Contact(supplierNum));
        printer.printContacts(contacts);
    }


    private void processAddContact() throws IOException{
        String[] details = printer.addContact();
        Contact contact = new Contact(connected.getSupplierNum(), details[0] ,  details[1] );
        if (repo.getSupplierDAO(Modules.CONTACTS).findByKey(contact) != null){
            printer.printMessage(false, "contact already exist");
        }
        else {
            repo.getSupplierDAO(Modules.CONTACTS).insert(contact);
        }
    }

    private void processDeleteProduct() {
        String ans = printer.DeleteProduct();
        if(!ans.equals((""))){
            if(repo.getSupplierDAO(Modules.SUPPLIERPRODUCTS).findByKey(new Product("" ,  ans  , connected.getSupplierNum() , 0 , 0)) != null)
            {
                repo.getSupplierDAO(Modules.SUPPLIERPRODUCTS).delete(new Product("", ans, connected.getSupplierNum(), 0, 0));
            }
            else {
                printer.printMessage(false, "No such product!");
            }
        }
    }

    private void processDeleteDiscount() {
        String ans = printer.DeleteDiscount();
        if(!ans.equals((""))){
            if(repo.getSupplierDAO(Modules.DISCOUNT).findByKey(new Discount( connected.getSupplierNum(), ans , 0 , 0)) != null){
                repo.getSupplierDAO(Modules.DISCOUNT).delete(new Discount( connected.getSupplierNum(), ans , 0 , 0));
            }
            else {
                printer.printMessage(false, "No such discount!");
            }
        }
    }

    private void processDeleteContact() {
        String ans = printer.DeleteContact();
        try{
            Integer.parseInt(ans);
            if(!ans.equals("")){
                if(repo.getSupplierDAO(Modules.CONTACTS).findByKey(new Contact(connected.getSupplierNum(), ans, "")) != null) {
                    if(((ContactDAO)repo.getSupplierDAO(Modules.CONTACTS)).findByKeys(new Contact(connected.getSupplierNum())).size() >= 2 )
                        repo.getSupplierDAO(Modules.CONTACTS).delete(new Contact(connected.getSupplierNum(), ans, ""));
                    else
                        printer.printMessage(true, "supplier have one contact and he must have at least one contact");
                }
                else {
                    printer.printMessage(true, "No such contact!");
                }
            }
        }
        catch(Exception e){
            printer.printMessage(true, "phone Number contains digits only!");
        }



    }

    private void processViewPresentingCompany() {
        int supplierNum = connected.getSupplierNum();
        List<Company> companies = ((CompanyDAO)repo.getSupplierDAO(Modules.COMPANY)).findByKeys(new Company(0 , supplierNum , ""));
        printer.printCompanies(companies);
    }


    private void processAddPresentingCompany() {
        String[] details = printer.addCompany();
        try{
            Integer.parseInt(details[0]);
            Company company = new Company( Integer.parseInt(details[0]) ,connected.getSupplierNum(),  details[1] );
            if (repo.getSupplierDAO(Modules.COMPANY).findByKey(company) != null){
                printer.printMessage(false, "company already exist");
            }
            else {
                repo.getSupplierDAO(Modules.COMPANY).insert(company);
            }
        }
        catch(Exception e){
            printer.printMessage(false, "Company Number contains digits only!");
        }

    }

    private void processDeleteCompany() {
        String sans = printer.DeleteCompany();
        int ans = 0;
        try{
            ans = Integer.parseInt(sans);
            if(sans != "" && ans != 0 ) {
                if (repo.getSupplierDAO(Modules.COMPANY).findByKey(new Company(ans, connected.getSupplierNum(), "")) != null) {
                    repo.getSupplierDAO(Modules.COMPANY).delete(new Company(ans, connected.getSupplierNum(), ""));
                }
                else {
                    printer.printMessage(false, "No such company!");
                }
            }
        }
        catch(Exception e){
            printer.printMessage(false, "Company Number contains digits only!");
        }

    }

    public SupplierPrinter getSupplierPrinter()
    {
        return printer;
    }

    @Override
    public String getMenuDescription() {
        return "Manage supplier details.";
    }
}
