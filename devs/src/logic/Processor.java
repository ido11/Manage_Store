package logic;

import logic.deliveries.Windows.DataManager;
import logic.employees.EmployeesInteractor;
import logic.employees.models.Employee;
import logic.employees.models.Role;
import logic.inventory.*;
import logic.suppliers.SupplierInteractor;
import presentation.employees.EmployeesPrinter;
import presentation.inventory.InventoryPrinter;
import presentation.suppliers.SupplierPrinter;
import presistence.Repository;
import presistence.dao.employees.EmployeeDAO;
import presistence.dao.employees.RoleDAO;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import static org.fusesource.jansi.Ansi.ansi;

public class Processor {

    private static final String HR_NOTI_FILE_NAME = "hr_notifications";
    private static Repository repo; //A repository instance used to communicate with database
    private static InventoryPrinter prompter;
    private static SupplierInteractor suppInteractor;
    private static EmployeesInteractor empInteractor;
    private static InventoryInteractor inventoryInteractor;
    private static DataManager deliveryInteractor;
    private static ArrayList<Interactor> menuItems = null;
    private static Boolean[] deliveryCanelationFlags = new Boolean[3];
    private static DeliveryCancelation cancel;

    private static Employee connected = null;

    public Processor() {
        repo = new Repository();
        prompter = new InventoryPrinter();
        suppInteractor = new SupplierInteractor(new SupplierPrinter(), repo);
        empInteractor = new EmployeesInteractor(new EmployeesPrinter(), connected, repo);
        inventoryInteractor = InventoryInteractor.getInstance(repo, prompter);
        deliveryInteractor = new DataManager();
        for(int i=0 ; i< deliveryCanelationFlags.length; i++)
            deliveryCanelationFlags[i] = false;
        cancel = new DeliveryCancelation();
        repo.connect();
    }

    public void process() throws IOException {
        boolean quit = false;
        while(!quit)
        {
            if(connected == null)
            {
                String id = empInteractor.getPrinter().loginMenu();
                empInteractor.validateID(id);
                Employee emp = new Employee(Integer.parseInt(id));
                connected = ((EmployeeDAO)repo.getEmployeeDAO(Modules.EMPLOYEE)).findByKey(emp);
                if(connected != null) {
                    connected.setRoles(((RoleDAO) repo.getEmployeeDAO(Modules.ROLE)).findByVal(connected));
                    menuItems = new ArrayList<>();
                    menuItems.add(empInteractor);
                    for (Role role : connected.getRoles()) {
                        switch (role.getDescription()) {
                            case "stock keeper":
                                connected.setStockKeeper(true);
                                if(!menuItems.contains(inventoryInteractor))
                                    menuItems.add(inventoryInteractor);
                                if(!menuItems.contains(suppInteractor))
                                    menuItems.add(suppInteractor);
                                if(!menuItems.contains(cancel))
                                    menuItems.add(cancel);
                                break;
                            case "logistics manager":
                                if(!menuItems.contains(deliveryInteractor))
                                    menuItems.add(deliveryInteractor);
                                if(!menuItems.contains(cancel))
                                    menuItems.add(cancel);
                                connected.setLogistics(true);
                                break;
                            case "HR":
                                if(!menuItems.contains(deliveryInteractor))
                                    menuItems.add(deliveryInteractor);
                                if(!menuItems.contains(cancel))
                                    menuItems.add(cancel);
                                connected.setHR(true);
                                break;
                            case "store manager":
                                if(!menuItems.contains(suppInteractor))
                                    menuItems.add(suppInteractor);
                                if(!menuItems.contains(deliveryInteractor))
                                    menuItems.add(deliveryInteractor);
                                if(!menuItems.contains(inventoryInteractor))
                                    menuItems.add(inventoryInteractor);
                                connected.setManager(true);
                                break;
                            case "driver":
                                connected.setDriver(true);
                                break;
                        }
                    }
                    prompter.printMessage(false, "Employee " + connected.getId() + " connected successfully\nChoose managing option:");
                } else prompter.printMessage(true, "Employee ID does not exist in system!");
            }
            int index = 1;
            if(connected != null) {
                System.out.print(ansi().bold().render("@|green Connected as ID: " + connected.getId() + "\n\n|@"));
                if (connected .isHR()) {
                    LinkedList<String> noti = getHRNotifications();
                    if(noti.size()>0){
                        System.out.println(ansi().bold().render("@|blue You have "+noti.size()+" new Notifications: \n|@"));
                        for (int i = 0;i<noti.size() ;i++){
                            if(!noti.get(i).equals("\n"))
                                System.out.println(ansi().bold().render("@|blue "+(i+1)+")"+noti.get(i)+"\n|@"));
                        }
                    }
                }
                for (Interactor interactor : menuItems) {
                    System.out.print(ansi().bold().render("@|yellow " + index + "." + interactor.getMenuDescription() + "\n|@"));
                    index++;
                }



                System.out.print(ansi().bold().render("@|yellow " + index + ".LOGOUT\n|@"));
                System.out.print(ansi().bold().render("@|yellow " + (index + 1) + ".EXIT\n|@"));
                int choice = prompter.menuSelection(1, menuItems.size() + 2);
                if (choice != menuItems.size() + 1 && choice != menuItems.size() + 2 && choice <= menuItems.size()+2 && choice >= 1) {
                    prompter.printMessage(false, "Please choose an option:");
                    menuItems.get(choice - 1).start(connected);
                } else if (choice == menuItems.size() + 1) {
                    connected = null; //DISCONNECT
                    prompter.printMessage(false, "DISCONNECTED");
                } else if (choice == menuItems.size() + 2) {
                    quit = true;
                }
            }
        }
        prompter.printMessage(false, "Goodbye, Hope you enjoyed our system.");
        prompter.closePrompter();
        suppInteractor.closeSupplierPrompter();
        empInteractor.getPrinter().closePrinter();
        repo.closeConnection();
    }

    private LinkedList<String> getHRNotifications() {
        LinkedList<String> output = new LinkedList<>();
        try {
            File f = new File(HR_NOTI_FILE_NAME);
            if(!f.isFile()) return  output;
            BufferedReader br = new BufferedReader(new FileReader(HR_NOTI_FILE_NAME));
            String st;
            while ((st = br.readLine()) != null) {
                String newst = st.replace("#", "\n");
                output.add(newst);
            }
            br.close();
            f.delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }

    private static class DeliveryCancelation implements Interactor
    {
        private int HR_index = 0, SK_index = 1, LM_index = 2;

        @Override
        public void start(Employee connected) {
            for (Role role : connected.getRoles()) {
                switch (role.getDescription()) {
                    case "stock keeper":
                            deliveryCanelationFlags[SK_index] = true;
                        break;
                    case "logistics manager":
                        deliveryCanelationFlags[LM_index] = true;
                        break;
                    case "HR":
                        deliveryCanelationFlags[HR_index] = true;
                        break;
                }
            }
            ArrayList<Boolean> flags = new ArrayList<>(Arrays.asList(deliveryCanelationFlags));
            if(!flags.contains(false)) {
                deliveryCanelationFlags[0] = false;
                deliveryCanelationFlags[1] = false;
                deliveryCanelationFlags[2] = false;
                ArrayList<String> deliveries = new ArrayList<>();
                ArrayList<String> new_deliveries;
                String st, last, filename = InventoryInteractor.getInstance(repo, prompter).DELIVERY_FILENAME;
                File f;
                if((f = new File(filename)).isFile())
                {
                    try {
                    BufferedReader br = new BufferedReader(new FileReader(filename));
                        while ((st = br.readLine()) != null && !st.equals("\n"))
                            deliveries.add(st);
                        new_deliveries = new ArrayList<>(deliveries.subList(0, deliveries.size()-1));
                        f.delete();
                        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
                        writer.write(new_deliveries.get(0));
                        writer.close();
                        writer = new BufferedWriter(new FileWriter(filename, true));
                        for(int i = 1; i<new_deliveries.size(); i++)
                        {
                            writer.append('\n');
                            writer.append(new_deliveries.get(i));
                        }
                        writer.close();
                        prompter.printMessage(false, "Last order canceled successfully.");
                    } catch (IOException e) {}
                } else prompter.printMessage(true, "No orders currently exist for cancellation.");
            }
        }

        @Override
        public String getMenuDescription() {
            return "Cancel last performed supplier order.";
        }
    }
}
