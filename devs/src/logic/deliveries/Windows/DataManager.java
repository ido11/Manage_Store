package logic.deliveries.Windows;

import logic.Interactor;
import logic.deliveries.models.*;
import logic.employees.models.Employee;
import presentation.deliveries.DeliveriesPrinter;
import presentation.inventory.printers.Printer;
import presistence.dao.deliveries.*;
import presistence.dao.inventory.ProductsDAO;

import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

public class DataManager implements Interactor {
    private boolean show = true;
    private Employee emp;

    public void start(Employee emp) {
        this.emp = emp;
        show=true;
        do {
            int[] choises = DeliveriesPrinter.getInstance().tablesManager();
            switch (choises[0]) {
                case 1:
                    manageBranch(choises[1]);
                    break;
                case 2:
                    manageDelivery(choises[1]);
                    break;
                case 3:
                    manageDeliveryDoc(choises[1]);
                    break;
                case 4:
                    manageProduct(choises[1]);
                    break;
                case 5:
                    manageProductDelivery(choises[1]);
                    break;
                case 6:
                    manageSiteReport(choises[1]);
                    break;
                case 7:
                    manageSite(choises[1]);
                    break;
                case 8:
                    manageSupplier(choises[1]);
                    break;
                case 9:
                    manageTruck(choises[1]);
                    break;
                case 10:
                    show = false;
                    break;
                default:
                    break;
            }
            if(choises[1] == 1) DeliveriesPrinter.getInstance().waitForEnter();
        } while (show);
    }

    private void manageBranch(int mode){
        BranchDAO dao = new BranchDAO();
        if(mode == 1){
            int ID = DeliveriesPrinter.getInstance().getID();
            Branch b = dao.findID(ID);
            DeliveriesPrinter.getInstance().waitForEnter();
            if(b==null)
                DeliveriesPrinter.getInstance().printMessage(true,"ID non-exist");
            else
                DeliveriesPrinter.getInstance().printMessage(false,b.toString());

        }
        else if (emp.isManager()) {
            DeliveriesPrinter.getInstance().printMessage(true, "As store manager you can only view data");
        }
        else if(mode == 2) {
            int ID = DeliveriesPrinter.getInstance().getID();
            Branch b = dao.findID(ID);
            if(b!=null) {
                String[] data = DeliveriesPrinter.getInstance().getBranchData();
                b.setArea(data[0]);
                b.setContactPerson(data[1]);
                b.setPhoneNumber(data[2]);
                dao.update(b);
            }
            else
                DeliveriesPrinter.getInstance().printMessage(true, "ID non-exist");
        }
        else if(mode == 3){
            int ID = DeliveriesPrinter.getInstance().getID();
            String Site = DeliveriesPrinter.getInstance().getSiteAddress();
            String[] data = DeliveriesPrinter.getInstance().getBranchData();
            Branch b = new Branch(Site,data[2],data[1],data[0],ID);
            dao.insert(b);
        }
        else if(mode==4){
            int ID = DeliveriesPrinter.getInstance().getID();
            Branch b = dao.findID(ID);
            if(b==null)
                DeliveriesPrinter.getInstance().printMessage(true,"ID non-exist");
            else
                dao.delete(b);
        }
    }

    private void manageDelivery(int mode){
        DeliveryDAO dao = new DeliveryDAO();
        if(mode == 1){
            int ID = DeliveriesPrinter.getInstance().getID();
            Delivery d = dao.findID(ID);
            if(d==null)
                DeliveriesPrinter.getInstance().printMessage(true,"ID non-exist");
            else
                DeliveriesPrinter.getInstance().printMessage(false,d.toString());

        }
        else if (emp.isManager()) {
            DeliveriesPrinter.getInstance().printMessage(true, "As store manager you can only view data");
        }
        else if(mode == 2){
            int ID = DeliveriesPrinter.getInstance().getID();
            Delivery d = dao.findID(ID);
            if(d!=null) {
                String[] data = DeliveriesPrinter.getInstance().getDeliveryData();
                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat timeFormater = new SimpleDateFormat("hh:mm:ss");
                try {
                    d.setStartDate(new Date(dateFormatter.parse(data[0]).getTime()));
                    d.setStartTime(new Time(timeFormater.parse(data[1]).getTime()));
                    dao.update(d);
                } catch (ParseException e) {
                    DeliveriesPrinter.getInstance().printMessage(true, "Invalid dateTime format");
                }
            }
            else
                DeliveriesPrinter.getInstance().printMessage(true, "ID non-exist");
        }
        else if(mode == 3){
            DeliveryDocDAO ddao = new DeliveryDocDAO();
            int ID = DeliveriesPrinter.getInstance().getID();
            String[] data = DeliveriesPrinter.getInstance().getDeliveryData();
            DeliveryDoc dd = ddao.findID(ID);
            Delivery d = null;
            boolean succ;
            do {
                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat timeFormater = new SimpleDateFormat("hh:mm:ss");
                try {
                    d = new Delivery(ID, dd, new Date(dateFormatter.parse(data[0]).getTime()), new Time(timeFormater.parse(data[1]).getTime()));
                    succ = true;
                } catch (Exception e) {
                    succ = false;
                }
            } while(!succ);
            dao.insert(d);
        }
        else if(mode==4){
            int ID = DeliveriesPrinter.getInstance().getID();
            Delivery d = dao.findID(ID);
            if (d == null)
                DeliveriesPrinter.getInstance().printMessage(true, "ID non-exist");
            else
                dao.delete(d);
        }
    }
    private void manageDeliveryDoc(int mode){
        DeliveryDocDAO dao = new DeliveryDocDAO();
        if(mode == 1){
            int ID = DeliveriesPrinter.getInstance().getID();
            DeliveryDoc d = dao.findID(ID);
            if(d==null)
                DeliveriesPrinter.getInstance().printMessage(true,"ID non-exist");
            else
                DeliveriesPrinter.getInstance().printMessage(false,d.toString());

        }
        else if (emp.isManager()) {
            DeliveriesPrinter.getInstance().printMessage(true, "As store manager you can only view data");
        }
        else if(mode == 2){
            int ID = DeliveriesPrinter.getInstance().getID();
            DeliveryDoc d = dao.findID(ID);
            if(d!=null) {
                TruckDAO tdao = new TruckDAO();
                DriverDAO ddao = new DriverDAO();
                String[] data = DeliveriesPrinter.getInstance().getDeliveryDocData();
                d.setDriver(ddao.findID(Integer.parseInt(data[0])));
                d.setTruck(tdao.findlicense(data[1]));
                dao.update(d);
            }
            else
                DeliveriesPrinter.getInstance().printMessage(true, "ID non-exist");
        }
        else if(mode == 3){
            TruckDAO tdao = new TruckDAO();
            DriverDAO ddao = new DriverDAO();
            int ID = DeliveriesPrinter.getInstance().getID();
            String[] data = DeliveriesPrinter.getInstance().getDeliveryDocData();
            Driver d = ddao.findID(Integer.parseInt(data[0]));
            Truck t = tdao.findlicense(data[1]);
            if(d==null||t==null) {
                DeliveriesPrinter.getInstance().waitForEnter();
                DeliveriesPrinter.getInstance().printMessage(true,"Invlid Detailes!");
            }
            else {
                DeliveryDoc dd = new DeliveryDoc(ID, d, t, new LinkedList<SiteReport>());
                dao.insert(dd);
            }
        }
        else if(mode == 4){
            int ID = DeliveriesPrinter.getInstance().getID();
            DeliveryDoc d = dao.findID(ID);
            if(d==null)
                DeliveriesPrinter.getInstance().printMessage(true,"ID non-exist");
            else
                dao.delete(d);

        }
    }

    private void manageProduct(int mode){
        ProductDAO dao = new ProductDAO ();
        if(mode == 1){
            int ID = DeliveriesPrinter.getInstance().getID();
            Product p = dao.findID(ID);
            if(p==null)
                DeliveriesPrinter.getInstance().printMessage(true,"ID non-exist");
            else
                DeliveriesPrinter.getInstance().printMessage(false,p.toString());

        }
        else if (emp.isManager()) {
            DeliveriesPrinter.getInstance().printMessage(true, "As store manager you can only view data");
        }
        else if(mode == 2) {
            int ID = DeliveriesPrinter.getInstance().getID();
            Product p = dao.findID(ID);
            if (p != null) {
                String[] data = DeliveriesPrinter.getInstance().getProductData();
                p.setName(data[0]);
                p.setWeight(Double.parseDouble(data[1]));
                dao.update(p);
            } else
                DeliveriesPrinter.getInstance().printMessage(true, "ID non-exist");
        }
        else if(mode==3){
            int ID = DeliveriesPrinter.getInstance().getID();
            String[] data = DeliveriesPrinter.getInstance().getProductData();
            Product p = new Product(ID,data[0],Double.parseDouble(data[1]));
            dao.insert(p);
        }
        else if(mode == 4){
            int ID = DeliveriesPrinter.getInstance().getID();
            Product p = dao.findID(ID);
            if(p==null)
                DeliveriesPrinter.getInstance().printMessage(true,"ID non-exist");
            else
                dao.delete(p);

        }
    }
    private void manageProductDelivery(int mode){
        ProductDeliveryDAO dao = new ProductDeliveryDAO();
        if(mode == 1){
            String[] Pd = DeliveriesPrinter.getInstance().getProductDelivery();
            ProductDelivery prod = dao.findID(Integer.parseInt(Pd[0]), Pd[1],Pd[2], Integer.parseInt(Pd[3]));
            if(prod==null)
                DeliveriesPrinter.getInstance().printMessage(true,"Product delivery non-exist");
            else
                DeliveriesPrinter.getInstance().printMessage(false,prod.toString());

        }
        else if (emp.isManager()) {
            DeliveriesPrinter.getInstance().printMessage(true, "As store manager you can only view data");
        }
        else if(mode == 2) {
            String[] Pd = DeliveriesPrinter.getInstance().getProductDelivery();
            ProductDelivery prod = dao.findID(Integer.parseInt(Pd[0]), Pd[1],Pd[2], Integer.parseInt(Pd[3]));
            if (prod != null) {
                int data = DeliveriesPrinter.getInstance().getProductDeliveryData();
                prod.setCountOfProducts(data);
                dao.update(prod);
            } else
                DeliveriesPrinter.getInstance().printMessage(true, "ID non-exist");
        }
        else if(mode == 3){
            SiteDAO sdao = new SiteDAO();
            ProductsDAO pdao = new ProductsDAO();
            String[] Pd = DeliveriesPrinter.getInstance().getProductDelivery();
            int data = DeliveriesPrinter.getInstance().getProductDeliveryData();
            ProductDelivery pd = new ProductDelivery(Integer.parseInt(Pd[0]),sdao.findAddrs(Pd[1]),sdao.findAddrs(Pd[2]),pdao.findByKey(new logic.inventory.models.Product(Integer.parseInt(Pd[3]))),data);
            dao.insert(pd);
        }
        else if(mode == 4){
            String[] Pd = DeliveriesPrinter.getInstance().getProductDelivery();
            ProductDelivery prod = dao.findID(Integer.parseInt(Pd[0]), Pd[1],Pd[2], Integer.parseInt(Pd[3]));
            if(prod==null)
                DeliveriesPrinter.getInstance().printMessage(true,"Product delivery non-exist");
            else
                dao.delete(prod);

        }
    }
    private void manageSiteReport(int mode){

        SiteReportDAO dao = new SiteReportDAO();
        if(mode == 1){
            String[] siter = DeliveriesPrinter.getInstance().getSiteReport();
            SiteReport sr = dao.findID(Integer.parseInt(siter[0]), siter[1]);
            if(sr==null)
                DeliveriesPrinter.getInstance().printMessage(true,"Site report non-exist");
            else
                DeliveriesPrinter.getInstance().printMessage(false,sr.toString());

        }
        else if (emp.isManager()) {
            DeliveriesPrinter.getInstance().printMessage(true, "As store manager you can only view data");
        }
        else if(mode == 2) {
            DeliveriesPrinter.getInstance().printMessage(true, "Can't update siteReports");
        }
        else if(mode == 3){
            SiteDAO sdao = new SiteDAO();
            String[] siter = DeliveriesPrinter.getInstance().getSiteReport();
            String[] data = DeliveriesPrinter.getInstance().getSiteReportData();
            SiteReport sr = new SiteReport(Integer.parseInt(siter[0]), sdao.findAddrs(siter[1]),Double.parseDouble(data[0]),Integer.parseInt(data[1]),new LinkedList<>());
            dao.insert(sr);
        }
        else if(mode == 4){
            String[] siter = DeliveriesPrinter.getInstance().getSiteReport();
            SiteReport sr = dao.findID(Integer.parseInt(siter[0]), siter[1]);
            if(sr==null)
                DeliveriesPrinter.getInstance().printMessage(true,"Site report non-exist");
            else
                dao.delete(sr);
            DeliveriesPrinter.getInstance().waitForEnter();

        }
    }
    private void manageSite(int mode){
        SiteDAO dao = new SiteDAO();
        if(mode == 1){
            String site = DeliveriesPrinter.getInstance().getSiteAddress();
            Site s = dao.findAddrs(site);
            if(s==null)
                DeliveriesPrinter.getInstance().printMessage(true,"Site non-exist");
            else
                DeliveriesPrinter.getInstance().printMessage(false,s.toString());

        }
        else if (emp.isManager()) {
            DeliveriesPrinter.getInstance().printMessage(true, "As store manager you can only view data");
        }
        else if(mode==2){

            String site = DeliveriesPrinter.getInstance().getSiteAddress();
            Site s = dao.findAddrs(site);
            if(s!=null){
                String[] data = DeliveriesPrinter.getInstance().getSiteData();
                s.setArea(data[0]);
                s.setContactPerson(data[1]);
                s.setPhoneNumber(data[2]);
                try {
                    dao.update(s);
                } catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }
            else
                DeliveriesPrinter.getInstance().printMessage(true,"Site non-exist");
        }
        else if(mode==3) {
            DeliveriesPrinter.getInstance().printMessage(true,"Can't create new site that are not supplier or branch");
        }
        else if(mode == 4){
            String site = DeliveriesPrinter.getInstance().getSiteAddress();
            Site s = dao.findAddrs(site);
            if(s==null)
                DeliveriesPrinter.getInstance().printMessage(true,"Site non-exist");
            else {
                try {
                    dao.delete(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
    private void manageSupplier(int mode){
        SupplierDAO dao = new SupplierDAO ();
        if(mode == 1){
            int ID = DeliveriesPrinter.getInstance().getID();
            Supplier s = dao.findID(ID);
            DeliveriesPrinter.getInstance().waitForEnter();
            if(s==null)
                DeliveriesPrinter.getInstance().printMessage(true,"ID non-exist");
            else
                DeliveriesPrinter.getInstance().printMessage(false,s.toString());

        }
        else if (emp.isManager()) {
            DeliveriesPrinter.getInstance().printMessage(true, "As store manager you can only view data");
        }
        else if(mode==2){
            ProductDAO pdao = new ProductDAO();
            int ID = DeliveriesPrinter.getInstance().getID();
            Supplier s = dao.findID(ID);
            if(s!=null){
                List<String> data = DeliveriesPrinter.getInstance().getSupplierData();
                List<Product> products = new LinkedList<>();
                for(int i = 1;i<data.size();i++){
                    products.add(pdao.findID(Integer.parseInt(data.get(i))));
                }
                try {
                    dao.update(s);
                } catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }
            else
                DeliveriesPrinter.getInstance().printMessage(true,"Supplier non-exist");
        }
        else if(mode == 3){
            ProductDAO pdao = new ProductDAO();
            String addrss = DeliveriesPrinter.getInstance().getSiteAddress();
            String[] siteData = DeliveriesPrinter.getInstance().getSiteData();
            int ID = DeliveriesPrinter.getInstance().getID();
            List<String> data = DeliveriesPrinter.getInstance().getSupplierData();
            List<Product> products = new LinkedList<>();
            for(int i = 1;i<data.size();i++){
                products.add(pdao.findID(Integer.parseInt(data.get(i))));
            }
            Supplier s = new Supplier(addrss,siteData[2],siteData[1],siteData[0],ID);
            dao.insert(s);
        }
        else if(mode == 4){
            int ID = DeliveriesPrinter.getInstance().getID();
            Supplier s = dao.findID(ID);
            if(s==null)
                DeliveriesPrinter.getInstance().printMessage(true,"ID non-exist");
            else
                dao.delete(s);

        }
    }
    private void manageTruck(int mode){
        TruckDAO dao = new TruckDAO();
        if(mode == 1){
            String lp = DeliveriesPrinter.getInstance().getLicensePlate();
            Truck t = dao.findlicense(lp);
            if(t==null)
                DeliveriesPrinter.getInstance().printMessage(true,"Truck non-exist");
            else
                DeliveriesPrinter.getInstance().printMessage(false,t.toString());

        }
        else if (emp.isManager()) {
            DeliveriesPrinter.getInstance().printMessage(true, "As store manager you can only view data");
        }
        else if(mode==2){
            String lp = DeliveriesPrinter.getInstance().getLicensePlate();
            Truck t = dao.findlicense(lp);
            if(t!=null){
                String[] data = DeliveriesPrinter.getInstance().getTruckData();
                t.setType(data[0]);
                t.setBaseWeight(Double.parseDouble(data[1]));
                t.setMaxWeight(Double.parseDouble(data[2]));
                try {
                    dao.update(t);
                } catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }
            else
                DeliveriesPrinter.getInstance().printMessage(true,"Supplier non-exist");
        }
        else if(mode == 3){
            String lp = DeliveriesPrinter.getInstance().getLicensePlate();
            String[] data = DeliveriesPrinter.getInstance().getTruckData();
            Truck t = new Truck(lp,data[0],Double.parseDouble(data[1]),Double.parseDouble(data[2]));
            dao.insert(t);
        }
        else if(mode == 4){
            String lp = DeliveriesPrinter.getInstance().getLicensePlate();
            Truck t = dao.findlicense(lp);
            if(t==null)
                DeliveriesPrinter.getInstance().printMessage(true,"Truck non-exist");
            else
                dao.delete(t);

        }
    }


    @Override
    public String getMenuDescription() {
        return "Manage deliveries.";
    }
}

