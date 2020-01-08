package presentation.deliveries;

import jline.ConsoleReader;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * A class used to print and get information from end-user.
 * Used for user communication with the CLI.
 */
public class DeliveriesPrinter {


    //Fields
    public static DeliveriesPrinter instance=null;
    private static ConsoleReader reader;
    private static Scanner scan;


    public static DeliveriesPrinter getInstance(){
        if(instance == null)
            instance = new DeliveriesPrinter();
        return instance;
    }

    //Constructor
    private DeliveriesPrinter()
    {
        AnsiConsole.systemInstall(); //To support CLI ansi colors via multiple OS
        flushScreen(null, false);
        System.out.println(ansi().bold().render(
                "@|magenta Thank you for choosing our system for your business!|@\n"));
        try {
            scan = new Scanner(System.in);
            reader = new ConsoleReader();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the printer object
     */
    public void closePrinter()
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
                            "@|cyan GROUP 11 DELIVERIES MANAGEMENT SYSTEM!\n" +
                            "==================================|@"));
        if(message != null)
            printMessage(error, message);

    }



    /**
     * Prints the main menu that is relevant to the connected user
     * @return The menu choice given by the user.
     */
    public int mainMenu() {
        boolean valid = false;
        int action = 0;
        while (!valid) {
            System.out.println(ansi().bold().render(
                    "@|green == Main Menu ==|@\n" +
                    "@|yellow 1|@ - Produce Delivery\n" +
                    "@|yellow 2|@ - Manage Data\n" +
                    "@|yellow 3|@ - Exit\n"));
            System.out.print(ansi().bold().render("@|cyan CHOICE: |@"));
            try {
                action = Integer.parseInt(reader.readLine());
                //Validates the choice is within bounds
                if (action < 1 || action>3)
                    throw new IllegalArgumentException();
                else valid = true;
            } catch (Exception e) {
                flushScreen("Invalid menu choice!", true);
            }
        }
        flushScreen(null,false);
        return action;
    }


    public int[] tablesManager(){
        boolean valid = false;
        int[] action = {0,0};
        while (!valid) {
            System.out.println(ansi().bold().render(
                    "@|green == Tables Manager ==|@\n" +
                            "@|cyan Pick a table to manage|@\n" +
                            "@|yellow 1|@ - Branches\n" +
                            "@|yellow 2|@ - Deliveries\n" +
                            "@|yellow 3|@ - DeliveryDocs\n" +
                            "@|yellow 4|@ - Products\n" +
                            "@|yellow 5|@ - ProductDeliveries\n" +
                            "@|yellow 6|@ - SiteReports\n" +
                            "@|yellow 7|@ - Sites\n" +
                            "@|yellow 8|@ - Suppliers\n" +
                            "@|yellow 9|@ - Trucks\n" +
                            "@|yellow 10|@ - Back to Main Menu\n"));
            System.out.print(ansi().bold().render("@|cyan CHOICE: |@"));
            try {
                action[0] = Integer.parseInt(reader.readLine());
                //Validates the choice is within bounds
                if (action[0] < 1 || action[0]>10)
                    throw new IllegalArgumentException();
                else valid = true;
            } catch (Exception e) {
                flushScreen("Invalid choice!", true);
            }
        }
        flushScreen(null,false);
        if(action[0] == 10) return action;
        valid = false;
        while (!valid) {
            System.out.println(ansi().bold().render(
                    "@|green == Pick Mode ==|@\n" +
                            "@|yellow 1|@ - Find\n" +
                            "@|yellow 2|@ - Update\n" +
                            "@|yellow 3|@ - Insert\n" +
                            "@|yellow 4|@ - Delete\n"));
            System.out.print(ansi().bold().render("@|cyan CHOICE: |@"));
            try {
                action[1] = Integer.parseInt(reader.readLine());
                //Validates the choice is within bounds
                if (action[1] < 1 || action[1]>4)
                    throw new IllegalArgumentException();
                else valid = true;
            } catch (Exception e) {
                flushScreen("Invalid choice!", true);
            }
        }
        return action;
    }

    public int getID (){
        while (true) {
            System.out.print(ansi().bold().render("@|cyan Enter Id: |@"));
            try {
                return Integer.parseInt(reader.readLine());
            } catch (Exception e) {
                flushScreen("Invalid choice!", true);
            }
        }
    }

    public String[] getDriverShift(){
        String[] output = {"","",""};
        try{
            do {
                System.out.print(ansi().bold().render("@|cyan Enter the driver id: |@"));
                output[0] = reader.readLine();
            } while(!tryParsInt(output[0]));
            do {
                System.out.print(ansi().bold().render("@|cyan Enter the shift Date(DD-MM-YYYY): |@"));
                output[1] = reader.readLine();
            } while(!tryParseDate(output[1]));
            do {
                System.out.print(ansi().bold().render("@|cyan Enter the Shift type(Morning,Evening): |@"));
                output[2] = reader.readLine();
            }while (!output[2].equals("Morning")&&!output[2].equals("Evening"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }


    public String getSiteAddress(){
        System.out.print(ansi().bold().render("@|cyan Enter Site Address: |@"));
        try {
            return reader.readLine();
        } catch (IOException e) {
            flushScreen("Invalid choice!", true);
            return null;
        }
    }


    public String getLicensePlate(){
        System.out.print(ansi().bold().render("@|cyan Enter License Plate: |@"));
        try {
            return reader.readLine();
        } catch (IOException e) {
            flushScreen("Invalid choice!", true);
            return null;
        }
    }


    public String[] getProductDelivery(){
        String[] output = {"","","",""};
        try{
            do {
                System.out.print(ansi().bold().render("@|cyan Enter the Delivery id: |@"));
                output[0] = reader.readLine();
            }while (!tryParsInt(output[0]));
            System.out.print(ansi().bold().render("@|cyan Enter the Supplier Address: |@"));
            output[1] = reader.readLine();
            System.out.print(ansi().bold().render("@|cyan Enter the Destination Branch Address: |@"));
            output[2] = reader.readLine();
            do {
                System.out.print(ansi().bold().render("@|cyan Enter the Product id: |@"));
                output[3] = reader.readLine();
            }while (!tryParsInt(output[3]));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }

    //(int deliveryID, String siteAddrss)

    public String[] getSiteReport(){
        String[] output = {"",""};
        try{
            do {
                System.out.print(ansi().bold().render("@|cyan Enter the Delivery id: |@"));
                output[0] = reader.readLine();
            } while(!tryParsInt(output[0]));
            System.out.print(ansi().bold().render("@|cyan Enter the Site Address: |@"));
            output[1] = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }






    /**
     * A method used to flush the current screen and add a message in the top of the new screen
     * @param error Determines if the @param message given is an error message
     * @param message The message to be shown on top of the new screen, if null nothing will be printed.
     */
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

    public String[] getBranchData() {
        String[] output = {"","",""};
        try{
            do {
                System.out.print(ansi().bold().render("@|cyan Enter the Area(north, center, south): |@"));
                output[0] = reader.readLine();
            }while (!output[0].equals("north")&&!output[0].equals("center")&&!output[0].equals("south"));
            System.out.print(ansi().bold().render("@|cyan Enter the Contact Person: |@"));
            output[1] = reader.readLine();
            System.out.print(ansi().bold().render("@|cyan Enter the Phone NUmber: |@"));
            output[2] = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }

    public String[] getDeliveryData() {
        String[] output = {"",""};
        try{
            do {
                System.out.print(ansi().bold().render("@|cyan Enter the Start Date(DD-MM-YYYY): |@"));
                output[0] = reader.readLine();
            }while (!tryParseDate(output[0]));
            do {
                System.out.print(ansi().bold().render("@|cyan Enter the Start Time(HH:MM:SS): |@"));
                output[1] = reader.readLine();
            }while (!tryParseTime(output[1]));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }


    public String[] getDeliveryDocData() {
        String[] output = {"",""};
        try{
            do {
                System.out.print(ansi().bold().render("@|cyan Enter the driver id: |@"));
                output[0] = reader.readLine();
            } while(!tryParsInt(output[0]));
            System.out.print(ansi().bold().render("@|cyan Enter the Truck License Number: |@"));
            output[1] = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }

    public String[] getDriverData() {
        String[] output = {"","",""};
        try{
            System.out.print(ansi().bold().render("@|cyan Enter the driver numae: |@"));
            output[0] = reader.readLine();
            while(true) {
                System.out.print(ansi().bold().render("@|cyan Enter the license Type(A,B,C,D): |@"));
                output[1] = reader.readLine();
                if(output[1].equals("A") || output[1].equals("B") || output[1].equals("C") || output[1].equals("D"))
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }


    public String[] getProductData() {
        String[] output = {"",""};
        try{
            System.out.print(ansi().bold().render("@|cyan Enter the product name: |@"));
            output[0] = reader.readLine();
            do {
                System.out.print(ansi().bold().render("@|cyan Enter the weight: |@"));
                output[1] = reader.readLine();
            }while (!tryParseDouble(output[1]));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }



    public int getProductDeliveryData() {
        int output = 0;
        while (true) {
            try {
                System.out.print(ansi().bold().render("@|cyan Enter the Amount: |@"));
                output = Integer.parseInt(reader.readLine());
                return output;
            } catch (IOException e) {
                System.out.print(ansi().bold().render("@|red Invalid input! |@"));
            }
        }
    }

    public String[] getSiteReportData() {
        String[] output = {"",""};
        try{
            do {
                System.out.print(ansi().bold().render("@|cyan Enter the weight on site: |@"));
                output[0] = reader.readLine();
            }while(!tryParseDouble(output[0]));
            do {
                System.out.print(ansi().bold().render("@|cyan Enter the order: |@"));
                output[1] = reader.readLine();
            } while(!tryParsInt(output[1]));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }

    public String[] getSiteData() {
        String[] output = {"","",""};
        try{
            do {
                System.out.print(ansi().bold().render("@|cyan Enter the Area(north, center, south): |@"));
                output[0] = reader.readLine();
            }while (!output[0].equals("north")&&!output[0].equals("center")&&!output[0].equals("south"));
            System.out.print(ansi().bold().render("@|cyan Enter the Contact person: |@"));
            output[1] = reader.readLine();
            System.out.print(ansi().bold().render("@|cyan Enter the phone number: |@"));
            output[2] = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }

    public List<String> getSupplierData() {
        LinkedList<String> output = new LinkedList<>();
        try{
            String ind;
            do {
                System.out.print(ansi().bold().render("@|cyan Enter If Supplier is Independent(True,False): |@"));
                ind=reader.readLine();
            } while (!tryParseBool(ind));
            output.add(ind);
            String[] ids;
            boolean succ;
            do {
                succ = true;
                System.out.print(ansi().bold().render("@|cyan Enter the Product ids([id1,id2,...]): |@"));
                String data = reader.readLine();
                ids = data.substring(1,data.length()-1).split(",");
                for (String id : ids)
                {
                    if (!tryParsInt(id)) succ = false;
                }
            }while (!succ);
            for (String id : ids)
            {
                output.add(id);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }



    public String[] getTruckData() {
        String[] output = {"","",""};
        try{
            while(true) {
                System.out.print(ansi().bold().render("@|cyan Enter the license Type(A,B,C,D): |@"));
                output[0] = reader.readLine();
                if(output[0].equals("A") || output[0].equals("B") || output[0].equals("C") || output[0].equals("D"))
                    break;
            }
            do {
                System.out.print(ansi().bold().render("@|cyan Enter the base weight: |@"));
                output[1] = reader.readLine();
            } while(!tryParseDouble(output[1]));
            do {
                System.out.print(ansi().bold().render("@|cyan Enter the max weight: |@"));
                output[2] = reader.readLine();
            } while(!tryParseDouble(output[2]));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }

    public String[] PrintDelivery(){
        flushScreen(null,false);
        String[] output = {"","",""};
        try{
            do {
                System.out.print(ansi().bold().render("@|cyan Enter the ID of the product you want: |@"));
                output[0] = reader.readLine();
            } while(!tryParsInt(output[0]));
            do {
                System.out.print(ansi().bold().render("@|cyan Enter the Amount: |@"));
                output[1] = reader.readLine();
            } while (!tryParsInt(output[1]));
            System.out.print(ansi().bold().render("@|cyan Enter the Destination Branch Address: |@"));
            output[2] = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }

    public void waitForEnter(){
        try {
            System.out.print(ansi().bold().render("@|green to continue press enter |@"));
            reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean tryParseBool(String s) {
        try{
            Boolean.parseBoolean(s);
            return true;
        } catch (Exception e){
            System.out.print(ansi().bold().render("@|red Invalid boolean value, try again! |@"));
            return false;
        }
    }
    
    private boolean tryParseDate(String s) {
        String[] data = s.split("-");
        if(data.length!=3){
            System.out.print(ansi().bold().render("@|red Invalid Date value, try again! |@"));
            return false;
        }
        try{
            int d=Integer.parseInt(data[0]);
            int m=Integer.parseInt(data[1]);
            int y=Integer.parseInt(data[2]);
            if(d<1||d>31||m<1||m>12){
                System.out.print(ansi().bold().render("@|red Invalid Date value, try again! |@"));
                return false;
            }
            return  true;
        }catch (Exception e){
            System.out.print(ansi().bold().render("@|red Invalid Date value, try again! |@"));
            return false;
        }
    }

    private boolean tryParsInt(String s) {
        try{
            Integer.parseInt(s);
            return true;
        } catch (Exception e){
            System.out.print(ansi().bold().render("@|red Invalid numeric value, try again! |@"));
            return false;
        }
    }

    private boolean tryParseTime(String s) {
        String[] data = s.split(":");
        if(data.length!=3){
            System.out.print(ansi().bold().render("@|red Invalid Time value, try again! |@"));
            return false;
        }
        try{
            int h=Integer.parseInt(data[0]);
            int m=Integer.parseInt(data[1]);
            int sec=Integer.parseInt(data[2]);
            if(sec<0||sec>60||m<0||m>60||h<0||h>24){
                System.out.print(ansi().bold().render("@|red Invalid Time value, try again! |@"));
                return false;
            }
            return true;
        }catch (Exception e){
            System.out.print(ansi().bold().render("@|red Invalid Date Time, try again! |@"));
            return false;
        }
    }

    private boolean tryParseDouble(String s) {
        try{
            Double.parseDouble(s);
            return true;
        } catch (Exception e){
            System.out.print(ansi().bold().render("@|red Invalid numeric value, try again! |@"));
            return false;
        }
    }
}
