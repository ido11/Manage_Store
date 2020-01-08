package presistence;

import logic.Modules;
import org.sqlite.SQLiteConfig;
import presistence.dao.*;
import presistence.dao.employees.*;
import presistence.dao.inventory.*;
import presistence.dao.suppliers.*;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A class used to communicate with the database
 */
public class Repository {

    //Fields
    private Map<Modules, DAO> inventory_daoMap; //Maps all DAO type to corresponding DAO objects
    private Map<Modules, DAO> supplier_daoMap; //Maps all DAO type to corresponding DAO objects
    private Map<Modules, DAO> employees_daoMap; //Maps all DAO type to corresponding DAO objects

    private final String DB_MASTER = "master"; //Constant master user username
    public static final String DB_NAME = "data.db"; //Database file name
    private final String CONN_URL = "jdbc:sqlite:"; //Database connection string
    private final String DRIVER = "org.sqlite.JDBC";
    public static Connection conn = null; //Conenction to database

    //Constructor
    public Repository () {
        //Map Entity type to corresponding DAO object
        inventory_daoMap = new HashMap<>() {{
            // Inventory DAO Mapping
            put(Modules.BRANCHES, new BranchesDAO());
            put(Modules.PRODUCTS, new ProductsDAO());
            put(Modules.DISCOUNTS, new DiscountsDAO());
            put(Modules.CATEGORIES, new CategoryDAO());
            put(Modules.LOCATIONS, new LocationsDAO());
            put(Modules.DEFECTS, new DefectiveDAO());
            put(Modules.STOCK, new StockDAO());

        }};

        supplier_daoMap = new HashMap<>() {{
            // Supplier DAO Mapping
            put(Modules.SUPPLIER, new SupplierDAO());
            put(Modules.SUPPLIER1, new SupplierType1DAO());
            put(Modules.SUPPLIER2, new SupplierType2DAO());
            put(Modules.SUPPLIER3, new SupplierType3DAO());
            put(Modules.SUPPLIERPRODUCTS, new ProductDAO());
            put(Modules.DISCOUNT, new DiscountDAO());
            put(Modules.CONTACTS, new ContactDAO());
            put(Modules.COMPANY, new CompanyDAO());
        }};

        employees_daoMap = new HashMap<>() {{
            put(Modules.USER, new UserDAO());
            put(Modules.CONSTRIANTS, new ConstraintsDAO());
            put(Modules.SHIFT, new ShiftsDAO());
            put(Modules.EMP_ROLES_DESC, new EmpRolesDescDAO());
            put(Modules.EMPLOYEE, new EmployeeDAO());
            put(Modules.SHIFTS_ASSIGNING, new ShiftsAssigningDAO());
            put(Modules.ROLE, new RoleDAO());
        }};
    }

    /**
     * Set the connection instance to connect to database using the connection string and DB filname
     * @throws SQLException
     */
    private void setConnection() throws SQLException
    {
        SQLiteConfig config = new SQLiteConfig();
        config.enforceForeignKeys(true);
        conn = DriverManager.getConnection(CONN_URL+DB_NAME, config.toProperties());
        conn.setAutoCommit(false);
    }

    /**
     * Initiates the connection to the database
     * If database file does not exists, creates it.
     * Sets the connection instance and sets all DAOs connection.
     */
    public void connect()
    {
        try{
            Class.forName(DRIVER);
            if(!(new File(DB_NAME).isFile()))
                createInventoryDB();
            setConnection();
            inventory_daoMap.values().forEach(dao -> dao.setConnection(conn));
            supplier_daoMap.values().forEach(dao -> dao.setConnection(conn));
            employees_daoMap.values().forEach(dao -> dao.setConnection(conn));
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Closes the connection to the database
     */
    public void closeConnection()
    {
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Sets the connection and creates the database tables.
     * The master database user is inserted on creation.
     * @throws SQLException
     */
    private void createInventoryDB() throws SQLException
    {
        setConnection();
        Statement stmt = conn.createStatement();
        //Create DB Tables

        //Inventory database section
        String sql = "CREATE TABLE IF NOT EXISTS products (\n"
                +   "   barcode INTEGER PRIMARY KEY,\n"
                +   "	name TEXT NOT NULL,\n"
                +   "   manufacturer TEXT DEFAULT 'Unknown',\n"
                +   "   cost_price REAL NOT NULL,\n"
                +   "   selling_price REAL NOT NULL,\n"
                +   "   orig_cost_price REAL NOT NULL,\n"
                +   "   orig_selling_price REAL NOT NULL,\n"
                +   "   minimal_amount INTEGER NOT NULL," +
                "       weight DOUBLE NOT NULL);"; //TODO maybe add constraint selling >= cost
        stmt.execute(sql);

            sql =   " CREATE TABLE IF NOT EXISTS discounts (\n"
                +   "   discountID INTEGER PRIMARY KEY AUTOINCREMENT, \n"
                +   "   barcode INTEGER NOT NULL, \n"
                +   "   discounter TEXT NOT NULL,\n"
                +   "	percentage REAL NOT NULL,\n"
                +   "   date_given TEXT NOT NULL,\n"
                +   "   date_ended TEXT DEFAULT NULL,\n"
                +   "   FOREIGN KEY(barcode) REFERENCES products(barcode) ON DELETE CASCADE ON UPDATE CASCADE);";
        stmt.execute(sql);

            sql =   " CREATE TABLE IF NOT EXISTS stock (\n"
                +   "   barcode INTEGER NOT NULL,\n"
                +   "	expiration_date TEXT NOT NULL,\n"
                +   "   locationID INTEGER NOT NULL,\n"
                +   "   quantity INTEGER NOT NULL,\n"
                +   "   FOREIGN KEY(locationID) REFERENCES locations(locationID) ON DELETE CASCADE ON UPDATE CASCADE,\n"
                +   "   PRIMARY KEY (barcode, expiration_date, locationID)," +
                    "   CONSTRAINT quantity_amount CHECK (quantity >= 0));";
        stmt.execute(sql);

            sql =   " CREATE TABLE IF NOT EXISTS defects (\n"
                +   "   barcode INTEGER NOT NULL,\n"
                +   "   locationID INTEGER NOT NULL,\n"
                +   "	quantity INTEGER NOT NULL,\n"
                +   "   reason TEXT NOT NULL,\n"
                +   "   date_reported TEXT NOT NULL,\n"
                +   "   FOREIGN KEY(locationID) REFERENCES locations(locationID) ON DELETE CASCADE ON UPDATE CASCADE,\n"
                +   "   FOREIGN KEY(barcode) REFERENCES products(barcode) ON DELETE NO ACTION ON UPDATE CASCADE,\n"
                +   "   PRIMARY KEY (barcode, locationID));";
        stmt.execute(sql);

            sql =   " CREATE TABLE IF NOT EXISTS category (\n"
                +   "   categoryID INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                +   "   name TEXT NOT NULL UNIQUE," +
                    "   discounted INTEGER DEFAULT NULL," +
                    "   discounter TEXT DEFAULT NULL);";
        stmt.execute(sql);

            sql =   " CREATE TABLE IF NOT EXISTS product_category (\n"
                +   "   barcode INTEGER NOT NULL,\n"
                +   "	categoryID INTEGER NOT NULL,\n"
                +   "   hierarchy INTEGER NOT NULL,\n"
                +   "   FOREIGN KEY(barcode) REFERENCES products(barcode) ON DELETE CASCADE ON UPDATE CASCADE,\n"
                +   "   FOREIGN KEY(categoryID) REFERENCES category(categoryID) ON DELETE CASCADE ON UPDATE CASCADE,\n"
                +   "   PRIMARY KEY(barcode, categoryID));";
        stmt.execute(sql);

            sql =   " CREATE TABLE IF NOT EXISTS branches (\n"
                +   "   branchID INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                +   "	name TEXT NOT NULL UNIQUE," +
                    "   address TEXT);";
            stmt.execute(sql);

            sql =   " CREATE TABLE IF NOT EXISTS locations (\n"
                +   "   locationID INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                +   "	branchID INTEGER NOT NULL,\n"
                +   "   physical_place TEXT NOT NULL,\n"
                +   "   place_identifier TEXT NOT NULL,\n"
                +   "   UNIQUE (physical_place, place_identifier),\n"
                +   "   FOREIGN KEY(branchID) REFERENCES branches(branchID) ON DELETE CASCADE ON UPDATE CASCADE);";
        stmt.execute(sql);

        //supplier database section
            sql = "CREATE TABLE IF NOT EXISTS suppliers (\n"
                + "   supplierNum INTEGER PRIMARY KEY,\n"
                + "	  name TEXT NOT NULL,\n"
                + "   bankAccount TEXT NOT NULL,\n"
                + "   paymentCond TEXT NOT NULL,\n"
                + "   phoneNum TEXT NOT NULL);" ;
        stmt.execute(sql);

            sql = "CREATE TABLE IF NOT EXISTS suppliersType1 (\n"
                + "   supplierNum INTEGER ,\n"
                + "	  day TEXT NOT NULL,"
                + "   FOREIGN KEY(supplierNum) REFERENCES suppliers(supplierNum) ON DELETE CASCADE ON UPDATE CASCADE, "
                + "   PRIMARY KEY(supplierNum , day));";
        stmt.execute(sql);

            sql = "CREATE TABLE IF NOT EXISTS suppliersType2 (\n"
                + "   supplierNum INTEGER PRIMARY KEY,\n"
                + "   FOREIGN KEY(supplierNum) REFERENCES suppliers(supplierNum) ON DELETE CASCADE ON UPDATE CASCADE);";
        stmt.execute(sql);

            sql = "CREATE TABLE IF NOT EXISTS suppliersType3 (\n"
                + "   supplierNum INTEGER PRIMARY KEY,\n"
                + "   FOREIGN KEY(supplierNum) REFERENCES suppliers(supplierNum) ON DELETE CASCADE ON UPDATE CASCADE);";
        stmt.execute(sql);

            sql = "CREATE TABLE IF NOT EXISTS supplierproduct (\n"
                + "   supplierNum INTEGER NOT NULL,\n"
                + "	  code TEXT NOT NULL,\n"
                + "   description TEXT NOT NULL,\n"
                + "   price INTEGER NOT NULL,\n"
                + "   amount INTEGER NOT NULL,\n"
                + "   FOREIGN KEY(supplierNum) REFERENCES suppliers(supplierNum) ON DELETE CASCADE ON UPDATE CASCADE,\n"
                + "   PRIMARY KEY(supplierNum, code))";
        stmt.execute(sql);

            sql = "CREATE TABLE IF NOT EXISTS supplierdiscount (\n"
                + "   supplierNum INTEGER NOT NULL,\n"
                + "	  code TEXT NOT NULL ,\n"
                + "   minAmount INTEGER NOT NULL,\n"
                + "   discountPer INTEGER NOT NULL,\n"
                + "   FOREIGN KEY(supplierNum) REFERENCES suppliers(supplierNum) ON DELETE CASCADE ON UPDATE CASCADE,"
                + "   PRIMARY KEY(supplierNum , code));";

        stmt.execute(sql);

            sql = "CREATE TABLE IF NOT EXISTS contacts (\n"
                + "   supplierNum INTEGER NOT NULL,\n"
                + "	  cellPhone TEXT NOT NULL,\n"
                + "   name TEXT NOT NULL,\n"
                + "   FOREIGN KEY(supplierNum) REFERENCES suppliers(supplierNum) ON DELETE CASCADE ON UPDATE CASCADE,\n"
                + "   PRIMARY KEY(supplierNum , cellPhone));";

        stmt.execute(sql);

         sql = "CREATE TABLE IF NOT EXISTS company (\n"
                + "   companyNum INTEGER ,\n"
                + "   supplierNum INTEGER ,\n"
                + "	  name TEXT NOT NULL,\n"
                + "   FOREIGN KEY(supplierNum) REFERENCES suppliers(supplierNum) ON DELETE CASCADE ON UPDATE CASCADE,\n"
                + "   PRIMARY KEY(companyNum , supplierNum));";
        stmt.execute(sql);

        //Employees & Deliveries database section
        sql = "CREATE TABLE IF NOT EXISTS accountDetails (" +
                "id INTEGER PRIMARY KEY REFERENCES employees (id)," +
                " accountNumber INTEGER NOT NULL," +
                " bankNum INTEGER, branch INTEGER);";
        stmt.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS constraints (" +
                "empID INTEGER REFERENCES employees (id)," +
                " shiftTime STRING NOT NULL," +
                " shiftDate DATE NOT NULL," +
                " PRIMARY KEY (empID, shiftTime, shiftDate));";
        stmt.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS deliveries (" +
                "delivery_id INTEGER PRIMARY KEY," +
                " start_date DATE NOT NULL," +
                " start_time TIME NOT NULL);";
        stmt.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS deliveryDocs (" +
                "delivery_id INTEGER PRIMARY KEY REFERENCES deliveries (delivery_id)," +
                " driver_id INTEGER REFERENCES drivers (ID) NOT NULL," +
                " truck_id TEXT REFERENCES trucks (license_plate) NOT NULL);";
        stmt.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS drivers (" +
                "ID INTEGER PRIMARY KEY REFERENCES employees (id)," +
                " license TEXT CHECK (license IN ('A', 'B', 'C', 'D')) NOT NULL);";
        stmt.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS empRoles (" +
                "empID INTEGER REFERENCES employees (id)," +
                " roleID INTEGER REFERENCES roles (roleID)," +
                " PRIMARY KEY (empID, roleID));";
        stmt.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS employees (" +
                "id INTEGER PRIMARY KEY," +
                " firstname TEXT NOT NULL," +
                " lastname NOT NULL, salary INTEGER," +
                " firstEmployed TIME," +
                " employmentCond TEXT);";
        stmt.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS delivery_products (" +              //Changed name
                "prod_id INTEGER PRIMARY KEY," +
                " name TEXT NOT NULL," +
                " weight DOUBLE CHECK (weight > 0) NOT NULL)";
        stmt.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS productsDeliveries (" +
                "delivery_id INTEGER NOT NULL," +
                " supp_addrs TEXT NOT NULL," +
                " prod_id INTEGER REFERENCES products (prod_id) NOT NULL," +
                " count INTEGER CHECK (count > 0) NOT NULL," +
                " dest_brench_address TEXT REFERENCES sites (address) NOT NULL," +
                " PRIMARY KEY (delivery_id, supp_addrs, prod_id, dest_brench_address)," +
                " FOREIGN KEY (delivery_id, supp_addrs) REFERENCES site_report (delivery_id, site_addrs));";
        stmt.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS roles (" +
                "roleID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " description TEXT)";
        stmt.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS shifts (" +
                "managerId INTEGER REFERENCES employees (id) NOT NULL," +
                " dateTime DATE NOT NULL, shiftID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " shiftTime STRING NOT NULL," +
                " branch_id INTEGER REFERENCES branches (branchID) NOT NULL)";
        stmt.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS shiftsAssigning (" +
                "shiftID INTEGER REFERENCES shifts (shiftID) NOT NULL," +
                " empID INTEGER REFERENCES employees (id)," +
                " roleID INTEGER REFERENCES roles (roleID) NOT NULL," +
                " PRIMARY KEY (shiftID, empID, roleID))";
        stmt.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS site_report (" +
                "delivery_id INTEGER REFERENCES deliveryDocs (delivery_id) NOT NULL," +
                " site_addrs TEXT REFERENCES sites (address) NOT NULL," +
                " weight_on_site DOUBLE, deliveryOrder INTEGER NOT NULL," +
                " PRIMARY KEY (delivery_id, site_addrs))";
        stmt.execute(sql);

        sql = "CREATE TABLE sites (" +
                "address TEXT PRIMARY KEY," +
                " phoneNumber TEXT NOT NULL," +
                " contactPerson Text NOT NULL," +
                " area TEXT NOT NULL CHECK (area IN ('north', 'center', 'south')))";
        stmt.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS supp2prod (" +
                "prod_id INTEGER REFERENCES products (prod_id)," +
                " supp_id INTEGER REFERENCES suppliers (supp_id)," +
                " PRIMARY KEY (prod_id, supp_id))";
        stmt.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS suppliers (" +
                "supp_id INTEGER PRIMARY KEY," +
                " address TEXT REFERENCES sites (address) NOT NULL," +
                " independent BOOLEAN NOT NULL," +
                " prod_id INTEGER REFERENCES products (prod_id))";
        stmt.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS trucks (" +
                "license_plate TEXT PRIMARY KEY," +
                " type TEXT NOT NULL CHECK (type IN ('A', 'B', 'C', 'D'))," +
                " base_weight DOUBLE NOT NULL CHECK (base_weight > 0)," +
                " max_weight DOUBLE NOT NULL CHECK (base_weight < max_weight))";
        stmt.execute(sql);

        conn.commit();
        createTriggers();
        createViews();
    }

    private void createViews() throws SQLException {
        Statement stmt = conn.createStatement();
        String sql =" CREATE VIEW empRolesDesc AS SELECT em.empID, em.roleID, ro.description FROM empRoles em JOIN roles ro ON em.roleID = ro.roleID";
        stmt.execute(sql);

        sql = "CREATE VIEW shiftsAssignRolesDesc AS SELECT shiftID, roleID, description, empID FROM shiftsAssigning sha JOIN roles ro ON sha.roleID = ro.roleID";
        stmt.execute(sql);

        conn.commit();
    }


    /**
     * Sets the connection and creates the database tables.
     * The master database user is inserted on creation.
     * @throws SQLException
     */
    private void createTriggers() throws SQLException
    {
        Statement stmt = conn.createStatement();
        String sql =" CREATE TRIGGER start_discount_store AFTER INSERT ON discounts " +
                    " BEGIN " +
                    "       UPDATE products SET selling_price = (selling_price -(selling_price * NEW.percentage/100)) " +
                    "       WHERE products.barcode = NEW.barcode AND NEW.discounter='Store';" +
                    " END;";
        stmt.execute(sql);

        sql = " CREATE TRIGGER reset_discount_store AFTER UPDATE ON discounts " +
                " FOR EACH ROW WHEN NEW.date_ended IS NOT NULL AND NEW.discounter='Store'"+
                " BEGIN " +
                "       UPDATE products SET selling_price = (SELECT orig_selling_price FROM products WHERE barcode = OLD.barcode) " +
                "       WHERE products.barcode = OLD.barcode AND OLD.discounter='Store';"+
                " END;";
        stmt.execute(sql);

        sql =" CREATE TRIGGER start_discount_supplier AFTER INSERT ON discounts " +
                " BEGIN " +
                "       UPDATE products SET cost_price = (cost_price -(cost_price * NEW.percentage/100)) " +
                "       WHERE products.barcode = NEW.barcode AND NEW.discounter='Supplier';" +
                " END;";
        stmt.execute(sql);

        sql = " CREATE TRIGGER reset_discount_supplier AFTER UPDATE ON discounts " +
                " FOR EACH ROW WHEN NEW.date_ended IS NOT NULL AND NEW.discounter='Supplier'"+
                " BEGIN " +
                "       UPDATE products SET cost_price = (SELECT orig_cost_price FROM products WHERE barcode = OLD.barcode) " +
                "       WHERE products.barcode = OLD.barcode AND OLD.discounter='Supplier';"+
                " END;";
        stmt.execute(sql);

        sql = " CREATE TRIGGER remove_finished_stock AFTER UPDATE ON stock " +
                " FOR EACH ROW WHEN NEW.quantity = 0"+
                " BEGIN " +
                "       DELETE FROM stock WHERE barcode = NEW.barcode AND" +
                "       expiration_date = NEW.expiration_date AND locationID = NEW.expiration_date;" +
                " END;";
        stmt.execute(sql);

        conn.commit();
    }

    //Getters
    public DAO getInventoryDAO(Modules daoType) {
        return inventory_daoMap.get(daoType);
    }
    public DAO getSupplierDAO(Modules daoType) {
        return supplier_daoMap.get(daoType);
    }

    public DAO getEmployeeDAO(Modules daoType) {
        return employees_daoMap.get(daoType);
    }

    public String getMasterUsername()
    {
        return DB_MASTER;
    }

}
