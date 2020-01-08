package logic.employees;

import logic.Interactor;
import logic.Modules;
import logic.deliveries.models.Driver;
import presistence.dao.deliveries.DriverDAO;
import logic.employees.models.*;
import presentation.employees.EmployeesPrinter;
import presistence.Repository;
import presistence.dao.Result;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

/**
 * A class used to communicate between End-user and the Database
 * and to perform all logical program actions.
 */
public class EmployeesInteractor implements Interactor {

    private final int MAX_ID_LENGTH = 9;
    final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    private boolean shutdown; //Determines whether the programs needs to shutdown.
    private Repository repo; //A repository instance used to communicate with database
    private static EmployeesPrinter printer; //A CLI printer instance used to present and retrieve date to and from end-user
    private static Employee connected = null;

    //Constructor
    public EmployeesInteractor(EmployeesPrinter printer, Employee connectedEmp, Repository repo) {
        this.printer = printer;
        this.shutdown = false;
        connected = connectedEmp;
        this.repo = repo;
    }

    /**
     * Main processor loop to run until end-user chose to exist and shutdown flag is changed.
     */
    public void start(Employee emp) {
        connected = emp;
        try {
                while (processMainMenu())
                    ; //As long as the user is connected prompt the main menu options.
            //After user chose to exit, close all object instances.
          //  repo.closeConnection();
          //  printer.closePrinter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Validated the @param id given contains only digits and
     * that the length of the id is less than MAX_ID_LENGTH
     *
     * @param id id to validate
     * @return true if validated, false otherwise
     */
    public boolean validateID(String id) {
        if (id.length() > MAX_ID_LENGTH)
            return false;
        else {
            try {
                Integer.parseInt(id);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }


    /*private void setIfHR(EmpRoles empTested) {
        LinkedList<EmpRoles> rolesOfEmp = (LinkedList<EmpRoles>)repo.getEmployeeDAO(Modules.EMP_ROLES_DESC).findByVal(empTested.getEmpId());
        for (EmpRoles empRole: rolesOfEmp) {
            if (empRole.getRoleDescription().equals("HR")) {
                connected.setHR(true);
                return;
            }
        }
        connected.setHR(false);
    }

    private void setIfTM(EmpRoles empTested) {
        LinkedList<EmpRoles> rolesOfEmp = (LinkedList<EmpRoles>)repo.getEmployeeDAO(Modules.EMP_ROLES_DESC).findByVal(empTested.getEmpId());
        for (EmpRoles empRole: rolesOfEmp) {
            if (empRole.getRoleDescription().equals("team manager")) {
                connected.setTM(true);
                return;
            }
        }
        connected.setTM(false);
    }

    private void setIfDriver(EmpRoles empTested) {
        LinkedList<EmpRoles> rolesOfEmp = (LinkedList<EmpRoles>)repo.getEmployeeDAO(Modules.EMP_ROLES_DESC).findByVal(empTested.getEmpId());
        for (EmpRoles empRole: rolesOfEmp) {
            if (empRole.getRoleDescription().equals("driver")) {
                connected.setDriver(true);
                return;
            }
        }
        connected.setDriver(false);
    }*/



    /**
     * Change current connected user to null
     */
    private void processLogout() {
        printer.printMessage(false, "USER " + connected.getId() + " DISCONNECTED.");
        connected = null;
    }

    /**
     * Calls each method by the menu selection the end-user made.
     *
     * @return true while user is still connected, false otherwise
     * @throws IOException
     */
    private boolean processMainMenu() throws IOException, SQLException {
        boolean isExit = true;
        /*boolean isHR = connected.isHR(); //connected.isHR();
        boolean isTM = connected.isTM();
        boolean isDriver = connected.isDriver();*/
        int choice = printer.mainMenu(connected.isHR(), connected.isManager(), connected.isDriver());
            if (connected.isHR() || connected.isManager()) {
                switch (choice) {
                    case 1:
                        viewShiftsAss(connected.getId());
                        break;
                    case 2:
                        getConstraints(connected.getId());
                        break;
                    case 3:
                        try {
                            EditEmpData();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 4: {
                        processManageShiftsAssign();
                        break;
                    }
                    case 5: {
                        getAllConstraints();
                        break;
                    }
                    case 6: {
                        getShifts();
                        break;
                    }
                    case 7: {
                        try {
                            if (connected.isManager()) {
                                printer.printMessage(true, "As store manager you can only view data");
                            }
                            else {
                                updateShifts();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 8:
                        processEmployees();
                        break;
                    case 10:
                        isExit = false;
                        break;
                    case 9:
                        if (connected.isManager()) {
                            printer.printMessage(true, "As store manager you can only view data");
                        }
                        else {
                            AddJobEmp();
                        }
                        break;
                }
            }
            else if(connected.isDriver()) {
                switch (choice) {
                    case 1:
                        viewShiftsAss(connected.getId());
                        break;
                    case 2:
                        getConstraints(connected.getId());
                        break;
                    case 3:
                        try {
                            EditEmpData();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 4:
                        showDriverDoc();
                        break;
                    case 5:
                        isExit = false;
                        break;
                }
            }
            else {
                switch (choice) {
                    case 1:
                        viewShiftsAss(connected.getId());
                        break;
                    case 2:
                        getConstraints(connected.getId());
                        break;
                    case 3:
                        try {
                            EditEmpData();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 4:
                        isExit = false;
                        break;
                }
            }
        return isExit;
    }

    private void showDriverDoc() {
       /* logic.deliveries.Windows.MainMenu mm = new MainMenu();
        printer.printMessage(false,mm.getDriverDelivery(connected.getId()));*/
    }

    /**
     * Get a username to delete from end-user and validate user exists and is not the master user.
     * Delete user from database if so, otherwise prompt the end-user with the appropriate message.
     *
     * @throws IOException
     */
    private void processDeleteUser() throws IOException {
        printer.printMessage(false, null);
        String username = "";
        while (username != null && username.isEmpty()) {
            username = printer.printDeleteUser();
            if (username != null && username.isEmpty())
                printer.printMessage(true, "Invalid selection!");
        }
        if (username == null)
            printer.printMessage(false, null);
        else {
            User user = (User) repo.getEmployeeDAO(Modules.USER).findByKey(new User(username));
            if (user != null) {
                if (!user.getUsername().equals(repo.getMasterUsername())) {
                    repo.getEmployeeDAO(Modules.USER).delete(new User(username));
                    printer.printMessage(false, "USER " + username + " deleted successfully!");
                } else printer.printMessage(true, "Cant perform deletion on MASTER user!");
            } else printer.printMessage(true, "USER " + username + " does not exist in system!");
        }
    }

    /**
     * Retrieve user details from the database according to his username, or according to master user selection.
     * If user exists in database print his details to end-user.
     *
     * @param master Will ask master to choose username to show details for if true, will use the connected username otherwise.
     * @throws IOException
     */
   /* private void processViewProfile(boolean master) throws IOException {
        String username = master ? printer.masterManage() : connected.getUsername();
        User user = (User) repo.getEmployeeDAO(Modules.USER).findByKey(new User(username));
        if (user != null) {
            String[] details = {user.getUsername(), user.getPassword(), user.getId() + "", user.getFirstName(), user.getLastName()};
            printer.printProfile(details);
        } else printer.printMessage(true, "Username does not exist in system.");
    }*/

    /**
     * Gets the username from connected user or from prompt if connected user is master.
     * Prompts the edit menu and updates the corresponding user with the new updated details chosen and inserted by end user.
     * Changes current logged user with the new details if he edited his own profile.
     *
     * @param master determines whether the editing user is a master user.
     * @throws IOException
     */
   /* private void processEditMenu(boolean master) throws IOException, SQLException {
        String username = connected.getUsername();
        if (!master)
            printer.printMessage(false, null);
        User user = (User) repo.getEmployeeDAO(Modules.USER).findByKey(new User(username)); //Get user details from repository
        if (user != null) {
            boolean edit = true;
            boolean failed = false;
            while (edit) {
                String[] toEdit = printer.editMenu();
                int choice = Integer.parseInt(toEdit[0]);
                String value = toEdit[1];
                //Update user details in the selected parameter with the new inserted value
                switch (choice) {
                    case 1:
                        if (master && username.equals(repo.getMasterUsername()))
                            printer.printMessage(true, "Master username cannot be changed!");
                        else {
                            User check = (User) repo.getEmployeeDAO(Modules.USER).findByKey(new User(value));
                            if (check == null)
                                user.setUsername(value);
                            else failed = true;
                        }
                        break;
                    case 2:
                        user.setPassword(value);
                        break;
                    case 3:
                        if (validateID(value))
                            user.setId(Integer.parseInt(value));
                        else failed = true;
                        break;
                    case 4:
                        user.setFirstName(value);
                        break;
                    case 5:
                        user.setLastName(value);
                        break;
                    case 6:
                        edit = false;
                        printer.printMessage(false, null);
                        break;
                    default:
                        printer.printMessage(true, "Invalid selection!");
                        break;
                }
                if (!(master && username.equals(repo.getMasterUsername()) && choice == 1) && edit) {
                    if (!failed) {
                        printer.printMessage(false, "Please Wait...");
                        //Update user details
                        repo.getEmployeeDAO(Modules.USER).update(user, username, 0, 0);
                        printer.printMessage(false, "Updated successfully!\n" +
                                "New " + ((choice == 1) ? "Username" : (choice == 2) ? "Password" :
                                (choice == 3) ? "ID" : (choice == 4) ? "First Name" : "Last Name") +
                                " is " + ((choice == 2) ? "updated" : value));
                        user = (User) repo.getEmployeeDAO(Modules.USER).findByKey(new User((choice == 1) ? value : username));
                        //Change current connected user to the updated details user if he was previously connected
                        if (!master || (master && username.equals(repo.getMasterUsername()))) {
                            if (username.equals(repo.getMasterUsername()))
                                user.setMaster(true);
                            connected = user;
                        }
                    } else if (failed && choice == 1)
                        printer.printMessage(true, "Username already exists!");
                    else if (failed && choice == 3)
                        printer.printMessage(true, "ID is not legal, must be only numbers and up to " + MAX_ID_LENGTH + " digits!");

                }
                failed = false;
            }
        } else printer.printMessage(true, "Username does not exist!");
    }*/

    /**
     * Gets new user details from prompt and inserts a new user to the database.
     *
     * @throws IOException
     */
  /*  private void processAddUser() throws IOException {
        printer.printMessage(false, null);
        if (connected.getUsername().equals(repo.getMasterUsername())) {
            String[] newUserDet = printer.addUserPrompt();
            boolean isUsernameValid, isIdValid;
            do {
                isUsernameValid = repo.getEmployeeDAO(Modules.USER).findByKey(new User(newUserDet[0])) == null;
                isIdValid = validateID(newUserDet[1]);
                String error = (!isUsernameValid) ? "Username already exists in system!" : (!isIdValid) ? "ID is not legal, must be only numbers and up to " + MAX_ID_LENGTH + " digits!" : null;
                if (!isUsernameValid || !isIdValid) {
                    printer.printMessage(true, error);
                    newUserDet = printer.addUserPrompt();
                }
            } while (!isIdValid || !isUsernameValid);
            printer.printMessage(false, "Valid Username and ID");
            String[] newUserInfo = printer.printAddUser();
            printer.printMessage(false, "Please Wait...");
            User user = new User(newUserDet[0], newUserInfo[0], Integer.parseInt(newUserDet[1]), newUserInfo[1], newUserInfo[2]);
            repo.getEmployeeDAO(Modules.USER).insert(user);
            printer.printMessage(false, "User " + newUserDet[0] + " Added successfully!");
        }
    }*/

    private void getConstraints(int id) {
        LinkedList<Constraints> cons = (LinkedList<Constraints>) repo.getEmployeeDAO(Modules.CONSTRIANTS).findByVal(id);
        String[] myCons = new String[cons.size() * 3];
        int i = 0;
        for (Constraints c : cons) {
            myCons[i] = Integer.toString(c.getEmpID());
            myCons[i + 1] = c.getShiftTime();
            myCons[i + 2] = df.format(c.getDate());
            i = i+3;
        }
        printer.printMyScheduleConstraints(myCons);

    }

    private void getAllConstraints(){
        LinkedList<Constraints> cons = (LinkedList<Constraints>) repo.getEmployeeDAO(Modules.CONSTRIANTS).findByVal("");
        String[] myCons = new String[cons.size() * 3];
        int i = 0;
        for (Constraints c : cons) {
            myCons[i] = Integer.toString(c.getEmpID());
            myCons[i + 1] = c.getShiftTime();
            myCons[i + 2] = df.format(c.getDate());
            i = i+3;
        }
        printer.printScheduleConstraints(myCons);
    }

    private void getShifts(){
        LinkedList<Shift> shifts = (LinkedList<Shift>) repo.getEmployeeDAO(Modules.SHIFT).findByVal("");
        String[] myShifts = new String[shifts.size() * 4];
        int i = 0;
        for (Shift s : shifts) {
            myShifts[i] = Integer.toString(s.getManagerID());
            myShifts[i + 1] = df.format(s.getDateTime());
            myShifts[i + 2] = Integer.toString(s.getShiftID());
            myShifts[i + 3] = s.getShiftTime();
            i = i + 4;
        }
        printer.printShifts(myShifts);
    }

    private void EditEmpData() throws ParseException {
        String flag = printer.editDataOrCons(false);
        while(!(flag.equals("1")) && !(flag.equals("2"))){
            flag = printer.editDataOrCons(true);
        }
        if (flag.equals("2")) {
            String[] ans = printer.printEditScheduleConstraints(false);
            while (!(ans[0].equals("0")) && !(ans[0].equals("1"))) {
                ans = printer.printEditScheduleConstraints(true);
            }
            int empId = connected.getId();
            Date date = new Date();
            try {
                date = df.parse(ans[1]);
            } catch (Exception e) {
                printer.printERR();
                return;
            }
            String shiftTime = ans[2];
            if (!shiftTime.equals("morning") && !shiftTime.equals("night")) {
                printer.printERR();
                return;
            }
            Constraints con = new Constraints(empId, shiftTime, date);
            if (ans[0].equals("0")) { // delete
                Result res = repo.getEmployeeDAO(Modules.CONSTRIANTS).delete(con);
                printer.printSucDel(res);
            } else if (ans[0].equals("1")) {
                Result res = repo.getEmployeeDAO(Modules.CONSTRIANTS).insert(con);
                printer.printSucInsert(res);
            }
        }
        else{
            String s[] = printer.getEmpData();
            Employee emp = new Employee(connected.getId(), s[0], s[1], Integer.parseInt(s[2]), df.parse(s[3]), s[4]);
            Result res = repo.getEmployeeDAO(Modules.EMPLOYEE).update(emp, connected.getId(), 0, 0);
            printer.printSucEd(res);
        }
    }

    private void updateShifts() throws ParseException {
        String ans = printer.printUpdateShifts(false);
        while (!(ans.equals("0")) && !(ans.equals("1"))){
            ans = printer.printUpdateShifts(true);
        }
        if (ans.equals("0")) { // delete
            String ans2 = printer.printDeleteShift();
            Shift shift = new Shift(Integer.parseInt(ans2));
            Result res = repo.getEmployeeDAO(Modules.SHIFT).delete(shift);
            printer.printSucDelShift(res);
        }
        else if (ans.equals("1")){
            String[] ans2 = printer.printInsShift();
            int MID = Integer.parseInt(ans2[0]);
            Date date = df.parse(ans2[1]);
            String shiftTime = ans2[2];
            Shift shift = new Shift(MID, date, shiftTime);
            if(managerHasConstraint(shift)) {
                Result res = repo.getEmployeeDAO(Modules.SHIFT).insert(shift);
                printer.printSucInsertShift(res);
            }
            else{
                printer.mangNoCons();
            }
        }
    }

    private void processManageShiftsAssign() {
        LinkedList<ShiftAssigning> shiftsAssigns = ( LinkedList<ShiftAssigning>) repo.getEmployeeDAO(Modules.SHIFTS_ASSIGNING).findByVal("");
        String[] myShiftsAssigns = new String[shiftsAssigns.size()*4];
        int i=0;
        for(ShiftAssigning sha : shiftsAssigns){
            myShiftsAssigns[i] = String.valueOf(sha.getKey());
            myShiftsAssigns[i + 1] = String.valueOf(sha.getRoleID());
            myShiftsAssigns[i + 2] = sha.getRoleDesc();
            myShiftsAssigns[i + 3] = String.valueOf(sha.getEmpID());
            i = i +4;
        }
        int choice = printer.manageShiftsAssignPrompt(myShiftsAssigns);
        if (choice == 3)
            return;

        if (connected.isManager()) {
            printer.printMessage(true, "As store manager you can only view data");
            return;
        }

        boolean success = false;
        while (!success) {
            int[] shiftAssignDets = printer.editShiftsAssign();
            ShiftAssigning assign = new ShiftAssigning(shiftAssignDets[0], shiftAssignDets[1], shiftAssignDets[2]);
            boolean validShift = false;
            if (choice == 1) {
                // delete a shift assign
                for (int j=0; j<myShiftsAssigns.length & !validShift; j = j+4 ) {
                    if (myShiftsAssigns[j].equals(String.valueOf(shiftAssignDets[0]))) {
                        validShift = true;
                    }
                }
                if (validShift)
                    success = printer.printSucEditShiftAssign(repo.getEmployeeDAO(Modules.SHIFTS_ASSIGNING).delete(assign));
                else
                    printer.noShiftNum();
            }
            else if(choice == 2) {

                LinkedList<Shift> totalShifts = (LinkedList<Shift>) repo.getEmployeeDAO(Modules.SHIFT).findByVal("");
                validShift = false;
                for (Shift sh : totalShifts) {
                    if (sh.getShiftID() == shiftAssignDets[0]) {
                        validShift = true;
                        break;
                    }
                }
                if (validShift) {
                    boolean yesRole = hasRole(assign);
                    boolean yesCons = hasConstraint(assign);
                    if (yesRole == false) {
                        printer.noRole();
                        return;
                    } else if (yesCons == false) {
                        printer.noCons();
                        return;
                    } else {
                        success = printer.printSucEditShiftAssign(repo.getEmployeeDAO(Modules.SHIFTS_ASSIGNING).insert(assign));
                    }
                } else
                    printer.noShiftNum();
            }
        }
    }

    private void processEmployees() {
        LinkedList<Employee> emps = ( LinkedList<Employee>) repo.getEmployeeDAO(Modules.EMPLOYEE).findByVal(0);
        String[] employees = new String[emps.size()*6];
        int i=0;
        int j=0;
        int[] ids = new int[emps.size()];
        for(Employee emp : emps){
            ids[j] = emp.getId();
            employees[i] = String.valueOf(emp.getKey());
            employees[i + 1] = emp.getFirstName();
            employees[i + 2] = emp.getLastName();
            employees[i + 3] = String.valueOf(emp.getSalary());
            employees[i + 4] = df.format(emp.getFirstEmployed());
            employees[i + 5] = emp.getEmploymentCond();
            i = i +6;
            j++;
        }

        int choice = printer.manageEmplyeesPrompt(employees);

        if (choice == 4)
            return;
        if (connected.isManager()) {
            printer.printMessage(true, "As store manager you can only view data");
            return;
        }
        boolean success = false;
        String[] empDets = new String[6];
        while (!success) {
            switch (choice) {
                case 1:
                    int idToDel = printer.deleteEmployee(ids);
                    Employee empToDel = new Employee(idToDel);
                    success = printer.printSucEditShiftAssign(repo.getEmployeeDAO(Modules.EMPLOYEE).delete(empToDel));
                    break;
                case 2:
                    empDets = printer.updateEmployee(ids, false);
                    try {
                        Date newD = df.parse(empDets[4]);
                        Employee editedEmp = new Employee(Integer.parseInt(empDets[0]), empDets[1], empDets[2], Integer.parseInt(empDets[3]), newD, empDets[5]);
                        success = printer.printSucEditShiftAssign(repo.getEmployeeDAO(Modules.EMPLOYEE).update(editedEmp, editedEmp.getKey(), 0, 0));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    empDets = printer.updateEmployee(ids, true);
                    try {
                        String date = df.format(new Date());
                        Date newDate = df.parse(date);
                        Employee editedEmp = new Employee(Integer.parseInt(empDets[0]), empDets[1], empDets[2], Integer.parseInt(empDets[3]), newDate, empDets[5]);
                        success = printer.printSucEditShiftAssign(repo.getEmployeeDAO(Modules.EMPLOYEE).insert(editedEmp));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    public boolean hasRole(ShiftAssigning assign){
        if(assign.getEmpID() == 0){
            return true;
        }
        boolean yesRole = false;
        LinkedList<EmpRoles> emps = (repo.getEmployeeDAO(Modules.EMP_ROLES_DESC)).findByVal(assign.getEmpID());
        for(EmpRoles e : emps){
            if (e.getRoleId() == assign.getRoleID()){
                yesRole = true;
            }
        }
        return yesRole;
    }

    public boolean hasConstraint(ShiftAssigning assign){
        if(assign.getEmpID() == 0){
            return true;
        }
        Shift shift = new Shift(assign.getShiftID());
        Shift myShift = (Shift) repo.getEmployeeDAO(Modules.SHIFT).findByKey(shift);
        LinkedList<Constraints> cons= repo.getEmployeeDAO(Modules.CONSTRIANTS).findByVal(assign.getEmpID());
        boolean toRet = false;
        for(Constraints c: cons){
            if(c.getShiftTime().equals(myShift.getShiftTime()) && c.getDate().equals(myShift.getDateTime())){
                toRet = true;
            }
        }
        return toRet;
    }

    public boolean managerHasConstraint(Shift shift){
        LinkedList<Constraints> cons= repo.getEmployeeDAO(Modules.CONSTRIANTS).findByVal(shift.getManagerID());
        boolean toRet = false;
        for(Constraints c: cons){
            if(c.getShiftTime().equals(shift.getShiftTime()) && c.getDate().equals(shift.getDateTime())){
                toRet = true;
            }
        }
        return toRet;
    }

    public void viewShiftsAss(int id){
        LinkedList<ShiftAssigning> shiftsAssigns = ( LinkedList<ShiftAssigning>) repo.getEmployeeDAO(Modules.SHIFTS_ASSIGNING).findByVal(id);
        if (shiftsAssigns == null){
            printer.printMyShiftAss(new String[0]);
            return;
        }
        String[] myShiftsAssigns = new String[shiftsAssigns.size()*4];
        int i=0;
        for(ShiftAssigning sha : shiftsAssigns){
            myShiftsAssigns[i] = String.valueOf(sha.getKey());
            myShiftsAssigns[i + 1] = String.valueOf(sha.getRoleID());
            myShiftsAssigns[i + 2] = sha.getRoleDesc();
            myShiftsAssigns[i + 3] = String.valueOf(sha.getEmpID());
            i = i +4;
        }
        printer.printMyShiftAss(myShiftsAssigns);
    }

    public void AddJobEmp(){
        String ans[] = printer.addJobEmp();
        LinkedList<Employee> emps = ( LinkedList<Employee>) repo.getEmployeeDAO(Modules.EMPLOYEE).findByVal(Integer.parseInt(ans[0]));
        if (emps.isEmpty()){
            printer.printERR();
            return;
        }
        LinkedList<Role> roles = repo.getEmployeeDAO(Modules.ROLE).findByVal(Integer.parseInt(ans[1]));
        if (roles.isEmpty()){
            printer.printERR();
            return;
        }
        EmpRoles empRoles = new EmpRoles(Integer.parseInt(ans[0]), Integer.parseInt(ans[1]), "");
        Result res = repo.getEmployeeDAO(Modules.EMP_ROLES_DESC).insert(empRoles);
        if(empRoles.getRoleDescription().equals("driver")) {
            Employee emp = emps.get(0);
            String license = printer.getDriverLicense();
            (new DriverDAO()).insert(new Driver(emp.getId(),emp.getFirstName(),emp.getLastName(),emp.getSalary(),emp.getFirstEmployed(),emp.getEmploymentCond(),license));
        }
        printer.sucAddJobEmp(res);
    }

    private void manageDeliveries(){
        repo.closeConnection();
       /* logic.deliveries.Windows.MainMenu mm = new MainMenu();
        mm.start();
        repo.connect();*/
    }

    public LinkedList<ShiftAssigning> getFutureDriversShifts() {
        LinkedList<ShiftAssigning> result = new LinkedList<>();

        //LinkedList<int> driversIDs = getAllDrivers();

        return result;
    }

    public EmployeesPrinter getPrinter()
    {
        return this.printer;
    }

    @Override
    public String getMenuDescription() {
        return "Manage employees details";
    }
}
