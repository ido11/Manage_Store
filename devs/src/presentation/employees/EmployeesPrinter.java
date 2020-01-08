package presentation.employees;

import java.io.IOException;
import java.util.Scanner;

import jline.ConsoleReader;
import org.fusesource.jansi.AnsiConsole;
import presistence.dao.Result;

import static org.fusesource.jansi.Ansi.*;

/**
 * A class used to print and get information from end-user.
 * Used for user communication with the CLI.
 */
public class EmployeesPrinter {

    //Fields
    private static Scanner scan;
    protected static ConsoleReader reader;

    //Constructor
    public EmployeesPrinter()
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
                "@|cyan GROUP 11 USER MANAGEMENT SYSTEM!\n" +
                        "================================|@"));
        if(message != null)
            printMessage(error, message);

    }

    /**
     * Prints user login menu.
     * @return User input of username and password.
     * @throws IOException
     */
    public String loginMenu() throws IOException {
        System.out.println(ansi().bold().render("@|green Connection:\n------------|@"));
        System.out.println(ansi().bold().render("@|yellow Please enter your ID:|@"));

       // return reader.readLine();
        return scan.nextLine();
    }

    /**
     * Prints the main menu that is relevant to the connected user
     * determined by the @param master.
     * @return The menu choice given by the user.
     */
    public int mainMenu(boolean HR,boolean SM,boolean Driver) {
        boolean valid = false;
        int action = 0;
        int bound = 0;
        while (!valid) {
            if (HR || SM) {
                bound = 10;
                System.out.println(ansi().bold().render(
                        "@|green == Main Menu ==|@\n" +
                                "@|yellow 1|@ - View your shifts assigning\n" +
                                "@|yellow 2|@ - View your schedule constraints\n" +
                                "@|yellow 3|@ - Edit your schedule constraints/data \n" +
                                "@|yellow 4|@ - Manage shifts assigning \n" +
                                "@|yellow 5|@ - View employees' shifts constraints\n" +
                                "@|yellow 6|@ - View shifts\n" +
                                "@|yellow 7|@ - Update shifts\n" +
                                "@|yellow 8|@ - Manage Employees\n" +
                                "@|yellow 9|@ - Add a job to employee\n" +
                                "@|yellow 10|@ - Back\n"));
            }  else if (Driver) {
                bound = 5;
                System.out.println(ansi().bold().render(
                        "@|green == Main Menu ==|@\n" +
                                "@|yellow 1|@ - View your shifts assigning\n" +
                                "@|yellow 2|@ - View your schedule constraints\n" +
                                "@|yellow 3|@ - Edit your schedule constraints/data\n" +
                                "@|yellow 4|@ - Show next Delivery Doc\n" +
                                "@|yellow 5|@ - Back\n"));
            } else {
                bound = 4;
                System.out.println(ansi().bold().render(
                        "@|green == Main Menu ==|@\n" +
                                "@|yellow 1|@ - View your shifts assigning\n" +
                                "@|yellow 2|@ - View your schedule constraints\n" +
                                "@|yellow 3|@ - Edit your schedule constraints/data\n" +
                                "@|yellow 4|@ - Back\n"));
            }
            System.out.print(ansi().bold().render("@|cyan CHOICE: |@"));
            try {
                 action = Integer.parseInt(scan.nextLine());
                //action = Integer.parseInt(reader.readLine());
                //Validates the choice is within bounds
                if (action < 1 || action > bound)
                    throw new IllegalArgumentException();
                else valid = true;
            } catch (Exception e) {
                flushScreen("Invalid menu choice!", true);
            }
        }
        return action;
    }

    /**
     * Prints master user message to select the username to manage
     * @return The username to manage.
     * @throws IOException
     */
    public String masterManage() throws IOException{
        flushScreen(null, false);
        System.out.println(ansi().bold().render("@|yellow Enter USERNAME to manage:|@"));
       // return scan.nextLine();
        return reader.readLine();
    }

    /**
     * Prints a add user prompt to enter new user details.
     * @return The new username and ID inserted by the end-user.
     * @throws IOException
     */
    public String[] addUserPrompt() throws IOException {
        String[] newUserDet = new String[2];
        System.out.println(ansi().bold().render(
                "@|green INSERT NEW USER DETAILS:\n"+
                        "------------------------|@"));
        System.out.print(ansi().bold().render("@|yellow Enter username: |@"));
        newUserDet[0] = scan.nextLine();
       // newUserDet[0] = reader.readLine();
        System.out.print(ansi().bold().render("@|yellow Enter user ID: |@"));
        newUserDet[1] =scan.nextLine();
      //  newUserDet[1] =reader.readLine();
        return newUserDet;
    }

    /**
     * Prints a delete user prompt to enter username to delete.
     * @return The username to delete if user chose y, null if user chose n and empty string if choice is invalid.
     * @throws IOException
     */
    public String printDeleteUser() throws IOException {
        String username;
        String choice;
        System.out.println(ansi().bold().render(
                "@|red USER DELETION:\n"+
                        "----------------|@"));
        System.out.print(ansi().bold().render("@|yellow Enter username to delete: |@"));
        username =scan.nextLine();
        System.out.print(ansi().bold().render("\n@|yellow ARE YOU SURE YOU WANT TO PROCEED? y/n : |@"));
        choice = scan.nextLine();
        if(choice.equals("n") || choice.equals("N"))
            return null;
        else if(choice.equals("y") || choice.equals("Y"))
            return username;
        else return "";
    }

    /**
     * Prints a prompt to ask user for additional details before insertion of a new user.
     * @return An array of all the new user parameters inserted by the end-user.
     * @throws IOException
     */
    public String[] printAddUser() throws IOException{
        String[] userInfo = new String[3];
        System.out.println(ansi().bold().render(
                "@|green INSERT ADDITIONAL USER DETAILS:\n"+
                        "-------------------------------|@"));
        System.out.print(ansi().bold().render("@|yellow Enter password: |@"));
        userInfo[0] = scan.nextLine();
        System.out.print(ansi().bold().render("@|yellow Enter First Name: |@"));
        userInfo[1] = scan.nextLine();
        System.out.print(ansi().bold().render("@|yellow Enter Last Name: |@"));
        userInfo[2] = scan.nextLine();
        return userInfo;
    }

    /**
     * Prints a menu with all the user fields that the user can edit.
     * @return An array containing the field choice user chose to edit and the new value.
     * @throws IOException
     */
    public String[] editMenu() throws IOException {
        boolean valid = false;
        int choice = 0;
        while(!valid) {
            System.out.println(ansi().bold().render(
                    "@|green Choose a field to edit:\n" +
                            "------------------------\n|@"+
                            "@|yellow 1|@ - Username\n" +
                            "@|yellow 2|@ - Password\n" +
                            "@|yellow 3|@ - ID\n" +
                            "@|yellow 4|@ - First Name\n" +
                            "@|yellow 5|@ - Last Name\n" +
                            "@|yellow 6|@ - RETURN TO MAIN MENU\n"));
            System.out.print(ansi().bold().render("@|cyan CHOICE: |@"));
            try {
                choice = Integer.parseInt(scan.nextLine());
                //Validates the choice is within bounds
                valid = (choice > 0 && choice < 7);
                if(!valid)
                    throw new NumberFormatException();
            } catch (Exception e) {
                flushScreen("Invalid selection!", true);
            }
        }
        if(choice != 6) {
            System.out.println(ansi().bold().render("@|yellow Enter new value:|@"));
            String value;
            if(choice != 2)
                value = scan.nextLine();
            else value = scan.nextLine();
            return new String[]{choice + "", value};
        }
        else return new String[]{choice + "", null};
    }

    /**
     * Prints a user profile with the details given in the @param details
     * @param details An array containing all user details to be shown on profile.
     */
    public void printProfile(String[] details) {
        flushScreen(null, false);
        System.out.println(ansi().bold().render(
                "@|green USER PROFILE: \n"+
                        "--------------\n|@"+
                        "@|yellow Username: |@"+details[0]+
                        "\n@|yellow Password: |@"+details[1]+
                        "\n@|yellow ID: |@"+details[2]+
                        "\n@|yellow First Name: |@"+details[3]+
                        "\n@|yellow Last Name: |@"+details[4]+
                        "\n\n@|cyan PRESS ENTER KEY TO RETURN... |@"));
        try {
            scan.nextLine();
        }
        catch(Exception e) {}
        flushScreen(null, false);
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

    public void printMyScheduleConstraints(String[] constraints) {
        if(constraints.length == 0){
            System.out.println(ansi().bold().render("there is no constraints available"));
            System.out.println(ansi().bold().render("press any key to return to menu"));
            scan.nextLine();
            return ;
        }
        flushScreen(null, false);
        int counter = 1;
        String toPrint;
        for (int i=0; i<constraints.length; i = i+3) {
            toPrint =  "("+counter+") shiftTime: " + constraints[i+1] + ", dateTime: " + constraints[i+2];
            System.out.println(ansi().bold().render(toPrint));
            counter++;
        }
        try {
            System.out.println(ansi().bold().render("press any key to return to menu"));
            scan.nextLine();
            return;
        }
        catch(Exception e) {}
        flushScreen(null, false);
    }

    public String[] printEditScheduleConstraints(boolean b){
        if (b){
            System.out.println("Please enter 0 or 1 !!");
        }
        String[] ans = new String[3];
        System.out.println("For delete press 0 ,for insert press 1");
        ans[0] = scan.nextLine();
        System.out.println("Enter date");
        ans[1] = scan.nextLine();
        System.out.println("Enter shift time (morning/night)");
        ans[2] = scan.nextLine();
        return ans;
    }

    public void printShifts(String[] shifts){
        if(shifts.length == 0){
            System.out.println(ansi().bold().render("there is no shifts available"));
            System.out.println(ansi().bold().render("Press any key to return to menu"));
            scan.nextLine();
            return;
        }
        flushScreen(null, false);
        int counter = 1;
        String toPrint;
        for (int i=0; i<shifts.length; i = i+4) {
            toPrint =  "("+counter+") Manager ID: " + shifts[i] + ", date of Shift: "  + shifts[i+1] + ", Shift ID: " + shifts[i+2] + ", shift time: " + shifts[i + 3];
            System.out.println(ansi().bold().render(toPrint));
            counter++;
        }
        try {
            System.out.println(ansi().bold().render("Press any key to return to menu"));
            scan.nextLine();
            return;
        }
        catch(Exception e) {}
        flushScreen(null, false);
        return ;
    }

    public void printScheduleConstraints(String[] constraints){
        if(constraints.length == 0){
            System.out.println(ansi().bold().render("there is no constraints available"));
            System.out.println(ansi().bold().render("Press any key to return to menu"));
            scan.nextLine();
            return;
        }
        flushScreen(null, false);
        int counter = 1;
        String toPrint;
        for (int i=0; i<constraints.length; i = i+3) {
            toPrint =  "("+counter+") Employee ID: " + constraints[i] + " shiftTime: "  + constraints[i+1] + "dateTime: " + constraints[i+2];
            System.out.println(ansi().bold().render(toPrint));
            counter++;
        }
        try {
            System.out.println(ansi().bold().render("Press any key to return to menu"));
            scan.nextLine();
            return;
        }
        catch(Exception e) {}
        flushScreen(null, false);
        return;
    }

    public void printSucDel(Result res){
        if (res.equals(Result.SUCCESS)){
            System.out.println(ansi().bold().render("Deleted succecfuly!!!!"));
        }
        else{
            System.out.println(ansi().bold().render("There is no such constraint to delete"));
        }
    }

    public void printSucInsert(Result res){
        if (res.equals(Result.SUCCESS)){
            System.out.println(ansi().bold().render("Insert succecfuly!!!!"));
        }
        else{
            System.out.println(ansi().bold().render("There is already constraint for this time!"));
        }
    }

    public String printUpdateShifts(boolean b){
        if (b){
            System.out.println("please enter 0 or 1");
        }
        System.out.println("For delete press 0 ,for insert press 1");
        return scan.nextLine();
    }

    public String printDeleteShift(){
        System.out.println("Enter shift ID");
        return scan.nextLine();
    }

    public String[] printInsShift(){
        String[] ans = new String[3];
        System.out.println("Enter manager ID: ");
        ans[0] = scan.nextLine();
        System.out.println("Enter date of shift: ");
        ans[1] = scan.nextLine();
        System.out.println("Enter shift time (morning/night)");
        ans[2] = scan.nextLine();
        return ans;
    }

    public void printSucDelShift(Result res){
        if (res.equals(Result.SUCCESS)){
            System.out.println(ansi().bold().render("Deleted succecfuly!!!!"));
        }
        else{
            System.out.println(ansi().bold().render("There is no such shift to delete"));
        }
    }

    public void printSucInsertShift(Result res){
        if (res.equals(Result.SUCCESS)){
            System.out.println(ansi().bold().render("Insert succecfuly!!!!"));
        }
        else{
            System.out.println(ansi().bold().render("There is already shift with this ID!"));
        }
    }

    public void printMyShiftAss(String[] myShiftsAssigns){
        flushScreen(null, false);
        for (int i=0; i<myShiftsAssigns.length; i=i+4){
            System.out.println(ansi().bold().render("====shift's number: " + myShiftsAssigns[i] + "===="));
            System.out.println(ansi().bold().render("roleID: " + myShiftsAssigns[i+1]));
            System.out.println(ansi().bold().render("role description: " + myShiftsAssigns[i+2]));
            System.out.println(ansi().bold().render("employee ID: " + myShiftsAssigns[i+3]));
            System.out.println(ansi().bold().render("===================="));
        }
        try {
            System.out.println(ansi().bold().render("Return to menu press any key"));
            scan.nextLine();
            return;
        }
        catch(Exception e) {}
        flushScreen(null, false);
    }

    public int manageShiftsAssignPrompt(String[] myShiftsAssigns) {
        flushScreen(null, false);
        for (int i=0; i<myShiftsAssigns.length; i=i+4){
            System.out.println(ansi().bold().render("====shift's number: " + myShiftsAssigns[i] + "===="));
            System.out.println(ansi().bold().render("roleID: " + myShiftsAssigns[i+1]));
            System.out.println(ansi().bold().render("role description: " + myShiftsAssigns[i+2]));
            System.out.println(ansi().bold().render("employee ID: " + myShiftsAssigns[i+3]));
            System.out.println(ansi().bold().render("===================="));
        }
        try {
            System.out.println(ansi().bold().render("(1) Delete a shift assigning"));
            System.out.println(ansi().bold().render("(2) Insert a new shift assigning"));
            System.out.println(ansi().bold().render("(3) back to main menu"));

            String choice = scan.nextLine();
            while (!choice.equals("1") && !choice.equals("2") && !choice.equals("3")) {
                System.out.println(ansi().bold().render("invalid input, try again."));
                choice = scan.nextLine();
            }

            return Integer.parseInt(choice);
        }
        catch(Exception e) {}
        flushScreen(null, false);
        return 0;
    }

    public int[] editShiftsAssign() {
        // assuming all shifts assigns are already printed on screen
        int[] details = new int[3];
        System.out.println(ansi().bold().render("Enter shift's num:"));
        details[0] = castIntErr("Enter shift's num:");
        System.out.println(ansi().bold().render("Enter role ID: "));
        details[1] = castIntErr("Enter role ID: ");
        System.out.println(ansi().bold().render("Enter employee ID: (in case of insert - if you don't have an employee yet, enter 0)"));
        details[2] = castIntErr("Enter employee ID: (in case of insert - if you don't have an employee yet, enter 0)");
        return details;
    }

    public int castIntErr(String s){
        int inp = 0;
        try{
            inp = Integer.parseInt(scan.nextLine());
        }
        catch (Exception e){
            printERR();
            System.out.println(ansi().bold().render(s));
            castIntErr(s);
        }
        return inp;
    }

    public boolean printSucEditShiftAssign(Result res) {
        if (res.equals(Result.SUCCESS)){
            System.out.println(ansi().bold().render("Updates succecfuly!!!!"));
            return true;
        }
        else{
            System.out.println(ansi().bold().render("There was a problem with one of the values. Try again :)"));
            return false;
        }
    }

    public int manageEmplyeesPrompt(String[] employees) {
        flushScreen(null, false);
        for (int i=0; i<employees.length; i=i+6){
            System.out.println(ansi().bold().render("====Employee's ID: " + employees[i] + "===="));
            System.out.println(ansi().bold().render("name: " + employees[i+1]) + " " + employees[i+2]);
            System.out.println(ansi().bold().render("salary: " + employees[i+3]));
            System.out.println(ansi().bold().render("first employed in: " + employees[i+4]));
            System.out.println(ansi().bold().render("Employment condition: " + employees[i+5]));
            System.out.println(ansi().bold().render("======================"));
        }
        try {
            System.out.println(ansi().bold().render("(1) Delete an employee :("));
            System.out.println(ansi().bold().render("(2) Edit employee's details"));
            System.out.println(ansi().bold().render("(3) Insert a new employee :)"));
            System.out.println(ansi().bold().render("(4) back to main menu"));

            String choice = scan.nextLine();
            while (!choice.equals("1") && !choice.equals("2") && !choice.equals("3") && !choice.equals("4")) {
                System.out.println(ansi().bold().render("invalid input, try again."));
                choice = scan.nextLine();
            }
            return Integer.parseInt(choice);
        }
        catch(Exception e) {}
        flushScreen(null, false);
        return 0;
    }

    public int deleteEmployee(int[] ids) {
        System.out.println(ansi().bold().render("enter the id of the employee you want to delete: "));
        return getEmpValid(ids, false);
    }

    private int getEmpValid(int[] ids, boolean newEmp) {
        int id = Integer.parseInt(scan.nextLine());
        boolean valid = false;
        boolean alreadyExists = false;
        while (!valid) {
            for (int i = 0; i < ids.length && !valid; i++) {
                if (ids[i] == id) {
                    valid = true;
                    if (newEmp)
                        alreadyExists = true;
                }
            }
            if (!alreadyExists && !valid)
                valid = true;
            if (!valid || alreadyExists) {
                System.out.println(ansi().bold().render("ID not valid! try again :) "));
                id = scan.nextInt();
            }
        }

        return id;
    }

    public String[] updateEmployee(int[] ids, boolean isNew) {
        String[] toRet = new String[6];
        System.out.println(ansi().bold().render("enter the id of the employee you want to insert/update: "));
        toRet[0] = String.valueOf(getEmpValid(ids, isNew)) ;

        System.out.println(ansi().bold().render("enter the first name: "));
        toRet[1] = scan.nextLine();
        System.out.println(ansi().bold().render("enter the last name: "));
        toRet[2] = scan.nextLine();
        System.out.println(ansi().bold().render("enter the salary: "));
        toRet[3] = scan.nextLine();

        System.out.println(ansi().bold().render("enter the first Employed date: "));
        toRet[4] = scan.nextLine();

        System.out.println(ansi().bold().render("enter the employment condition: "));
        toRet[5] = scan.nextLine();

        return toRet;
    }

    public void noRole(){
        System.out.println(ansi().bold().render("The employee can't do this job :("));
    }

    public void noCons(){
        System.out.println(ansi().bold().render("The employee not have this constraint :("));
    }

    public void mangNoCons(){
        System.out.println(ansi().bold().render("The employee not have this constraint :("));
    }

    public void printERR(){
        System.out.println(ansi().bold().render("Invalid Input!!"));
    }

    public String[] addJobEmp(){
        String ret[] = new String[2];
        System.out.println(ansi().bold().render("Insert employee ID: "));
        ret[0] = scan.nextLine();
        System.out.println(ansi().bold().render("Insert role ID: "));
        ret[1] = scan.nextLine();
        return ret;
    }

    public String editDataOrCons(boolean b){
        if (b){
            System.out.println(ansi().bold().render("Please insert 1 or 2 !!"));
        }
        System.out.println(ansi().bold().render("For edit yor Details press 1"));
        System.out.println(ansi().bold().render("For edit yor constraints press 2"));
        return scan.nextLine();
    }

    public String[] getEmpData() {
        String[] toRet = new String[5];
        System.out.println(ansi().bold().render("enter the first name: "));
        toRet[0] = scan.nextLine();
        System.out.println(ansi().bold().render("enter the last name: "));
        toRet[1] = scan.nextLine();
        System.out.println(ansi().bold().render("enter the salary: "));
        toRet[2] = scan.nextLine();

        System.out.println(ansi().bold().render("enter the first Employed date: "));
        toRet[3] = scan.nextLine();

        System.out.println(ansi().bold().render("enter the employment condition: "));
        toRet[4] = scan.nextLine();

        return toRet;
    }

    public void printSucEd(Result res){
        if (res.equals(Result.SUCCESS)){
            System.out.println(ansi().bold().render("Edit succecfuly!!!!"));
        }
        else{
            System.out.println(ansi().bold().render("There is problem with the arguments!!"));
        }
    }

    public void sucAddJobEmp(Result res){
        if (res.equals(Result.SUCCESS)){
            System.out.println(ansi().bold().render("Insert succecfuly!!!!"));
        }
        else{
            System.out.println(ansi().bold().render("Fail to insert !!!!"));
        }
    }

    public void noShiftNum() {
        System.out.println(ansi().bold().render("Shift doesn't exists!"));
    }

    public String getDriverLicense() {
        System.out.println(ansi().bold().render("Enter the Drivers License(A,B,C,D): "));
        String license = scan.nextLine();
        while (!(license.equals("A")||license.equals("B")||license.equals("C")||license.equals("D"))){
            System.out.println(ansi().bold().render("Invalid input!\nEnter the Drivers License(A,B,C,D): "));
            license = scan.nextLine();
        }
        return license;
    }
}
