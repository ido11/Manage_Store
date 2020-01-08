package logic.deliveries.Windows;

import logic.deliveries.models.*;
import logic.employees.models.Shift;
import logic.inventory.models.Product;
import presentation.deliveries.DeliveriesPrinter;
import presistence.dao.deliveries.*;
import logic.employees.models.ShiftAssigning;
import presistence.dao.employees.ShiftsAssigningDAO;
import presistence.dao.employees.ShiftsDAO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

public class DeliveryManager {

    private static final String HR_NOTI_FILE_NAME = "hr_notifications";
    private SupplierDAO supplierDAO = new SupplierDAO();
    private DeliveryDAO deliveryDAO = new DeliveryDAO();
    private ProductDeliveryDAO productDeliveryDAO = new ProductDeliveryDAO();
    private SiteReportDAO siteReportDAO = new SiteReportDAO();
    private TruckDAO truckDAO = new TruckDAO();
    private logic.inventory.models.Product product;
    private Branch branch;
    private BranchDAO branchDao = new BranchDAO();
    private int amount;
    private List<Delivery> deliveries;
    private Supplier supplier;

    public java.util.Date produceDelivery(SupplierOrder so){
        product = so.getProduct();
        amount = so.getProdAmount();
        branch = branchDao.findID(so.getBranch());
        supplier = supplierDAO.findID(so.getSupplier());
        deliveries = deliveryDAO.findAvailableDeliveries();

        for (Delivery d:deliveries) {
            if(!AvailableStockKeeper(d.getStartDate(),d.getStartTime(),branch)) continue;
            if(contains(d,supplier)){
                if(addProduct(d,1)){
                    DeliveriesPrinter.getInstance().printMessage(false,"Added to delivery: "+d.getDeliveryID()
                            +"\nScheduled for "+d.getStartDate().toLocalDate().toString()+" "+d.getStartTime().toLocalTime().toString());
                    return d.getStartDate();
                }
            }

        }

        for (Delivery d:deliveries) {
            if(!AvailableStockKeeper(d.getStartDate(),d.getStartTime(),branch)) continue;
            if(contains(d,branch)){
                if(addProduct(d,2)){
                    DeliveriesPrinter.getInstance().printMessage(false,"Added to delivery: "+d.getDeliveryID()
                            +"\nScheduled for "+d.getStartDate().toLocalDate().toString()+" "+d.getStartTime().toLocalTime().toString());
                    return d.getStartDate();
                }
            }

        }


        for (Delivery d:deliveries) {
            if(!AvailableStockKeeper(d.getStartDate(),d.getStartTime(),branch)) continue;
            if(addProduct(d,3)){
                DeliveriesPrinter.getInstance().printMessage(false,"Added to delivery: "+d.getDeliveryID()
                        +"\nScheduled for "+d.getStartDate().toLocalDate().toString()+" "+d.getStartTime().toLocalTime().toString());
                return d.getStartDate();
            }
        }
        Delivery d = createDelivery();
        if(d!=null){
            DeliveriesPrinter.getInstance().printMessage(false,"Created new delivery: "+d.getDeliveryID()
                    +"\nScheduled for "+d.getStartDate().toLocalDate().toString()+" "+d.getStartTime().toLocalTime().toString());
            return d.getStartDate();
        }
        else{
            DeliveriesPrinter.getInstance().printMessage(false,"Unable to process delivery at this time,\n" +
                    "informing HR manager");
            try {
                informHR(supplier);
            } catch (IOException e) {
                DeliveriesPrinter.getInstance().printMessage(true,"Failed to inform HR");
            }
        }


        return null;
    }

    private void informHR(Supplier supplier) throws IOException {
        String[] d = getOptionalShift();
        if (d==null) {
            DeliveriesPrinter.getInstance().printMessage(true,"Cant find a suitable date for adding a driver shift, try again later");
            return;
        }
        File yourFile = new File(HR_NOTI_FILE_NAME);
        yourFile.createNewFile(); // if file already exists will do nothing
        String notification = "Failed to create delivery,#please consider adding a driver shift at Date: "+d[0]+" Time: "+d[1];
        saveNotificationToFile(notification,true);

    }

    private String[] getOptionalShift() {

        ShiftsAssigningDAO assDAO = new ShiftsAssigningDAO();
        ShiftsDAO shiftsDAO = new ShiftsDAO();
        List<ShiftAssigning> stockShifts = assDAO.getAvailableStockKeepers(branch);
        if (stockShifts!= null){
            for(ShiftAssigning ds:stockShifts) {
                Shift s = shiftsDAO.findByID(ds.getShiftID());
                Truck t = truckDAO.findAvailable(new Date(s.getDateTime().getTime()), s.getShiftTime(), amount * product.getWeight());
                if (t!= null){
                    DateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");
                    String[] output = {dtf.format(s.getDateTime()),s.getShiftTime()};
                    return output;
                }

            }
            return null;
        }
        else
            return null;

    }

    private void saveNotificationToFile(String notification, boolean append)
    {
        try {
            if(!append) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(HR_NOTI_FILE_NAME));
                writer.write(notification);
                writer.close();
            } else {
                BufferedWriter writer = new BufferedWriter(new FileWriter(HR_NOTI_FILE_NAME, true));
                writer.append('\n');
                writer.append(notification);
                writer.close();
            }
        } catch (IOException e) {}
    }

    private boolean addProduct(Delivery d,int option){
        double prodWeight = product.getWeight()*amount;
        double currWeight = getTruckWeight(d);
        if(currWeight+prodWeight>d.getDeliveryDoc().getTruck().getMaxWeight()){
            d=upgradeTruck(d,currWeight+prodWeight-d.getDeliveryDoc().getTruck().getMaxWeight());
            currWeight = getTruckWeight(d);
        }
        if(currWeight+prodWeight<=d.getDeliveryDoc().getTruck().getMaxWeight()){
            ProductDelivery prd=productDeliveryDAO.findID(d.getDeliveryID(),supplier.getAddress(),branch.getAddress(),product.getBarcode());
            if(prd!=null){
                prd.setCountOfProducts(prd.getCountOfProducts()+amount);
                productDeliveryDAO.update(prd);
                d=deliveryDAO.findID(d.getDeliveryID());
            }
            else {
                prd = new ProductDelivery(d.getDeliveryID(), supplier, branch, product, amount);
            }
            int fromOrder=0;
            int toOrder = 0;
            if(option == 1){
                for(SiteReport sr:d.getDeliveryDoc().getSiteReports()){
                    if(sr.getSite().getAddress().equals(supplier.getAddress())){
                        fromOrder = sr.getOrder();
                        break;
                    }
                }
                for(SiteReport sr:d.getDeliveryDoc().getSiteReports()){
                    if(sr.getSite().getAddress().equals(branch.getAddress())){
                        toOrder = sr.getOrder();
                        updateWeight(fromOrder,toOrder,d.getDeliveryDoc().getSiteReports());
                        return true;
                    }
                }
                toOrder = d.getDeliveryDoc().getSiteReports().size()+1;
                SiteReport sr = new SiteReport(d.getDeliveryID(),branch,d.getDeliveryDoc().getTruck().getBaseWeight(),toOrder,new LinkedList<>());
                sr.getProductDelivery().add(prd);
                siteReportDAO.insert(sr);
                updateWeight(fromOrder,toOrder,d.getDeliveryDoc().getSiteReports());
                deliveryDAO.update(d);
                return true;
            }
            if(option == 2){
                for(SiteReport sr:d.getDeliveryDoc().getSiteReports()){
                    sr.setOrder(sr.getOrder()+1);
                    siteReportDAO.update(sr);
                }
                fromOrder = 1;
                SiteReport s = new SiteReport(d.getDeliveryID(),supplier,d.getDeliveryDoc().getTruck().getBaseWeight()+(product.getWeight()*amount),1,new LinkedList<>());
                s.getProductDelivery().add(prd);
                siteReportDAO.insert(s);
                d.getDeliveryDoc().getSiteReports().add(s);
                for(SiteReport sr:d.getDeliveryDoc().getSiteReports()){
                    if(sr.getSite().getAddress().equals(branch.getAddress())){
                        toOrder = sr.getOrder();
                        break;
                    }
                }
                updateWeight(fromOrder,toOrder,d.getDeliveryDoc().getSiteReports());
                deliveryDAO.update(d);
                return true;
            }
            else if(option==3){
                for(SiteReport sr:d.getDeliveryDoc().getSiteReports()){
                    sr.setOrder(sr.getOrder()+1);
                    siteReportDAO.update(sr);
                }
                fromOrder = 1;
                SiteReport s = new SiteReport(d.getDeliveryID(),supplier,d.getDeliveryDoc().getTruck().getBaseWeight()+(product.getWeight()*amount),fromOrder,new LinkedList<>());
                s.getProductDelivery().add(prd);
                siteReportDAO.insert(s);
                d.getDeliveryDoc().getSiteReports().add(s);
                toOrder = d.getDeliveryDoc().getSiteReports().size()+1;
                SiteReport sr = new SiteReport(d.getDeliveryID(),branch,d.getDeliveryDoc().getTruck().getBaseWeight(),toOrder,new LinkedList<>());
                sr.getProductDelivery().add(prd);
                siteReportDAO.insert(sr);
                d.getDeliveryDoc().getSiteReports().add(sr);
                updateWeight(fromOrder,toOrder,d.getDeliveryDoc().getSiteReports());
                deliveryDAO.update(d);
                return true;
            }

        }


        return false;
    }

    private void updatePrd(Delivery d) {
        for(SiteReport sr:d.getDeliveryDoc().getSiteReports()){
            for(ProductDelivery prd : sr.getProductDelivery()){
                if(prd.getSuppSite().getAddress().equals(supplier.getAddress())&&
                prd.getProduct().getBarcode()==product.getBarcode()&&
                prd.getDeliveryID()==d.getDeliveryID()&&
                prd.getBranchSite().getAddress().equals(branch.getAddress()))
                    prd.setCountOfProducts(prd.getCountOfProducts()+amount);
            }
        }
    }


    private Delivery upgradeTruck(Delivery d, double v) {
        String shift;
        if(d.getStartTime().before(Time.valueOf("16:00:00")))
            shift = "Morning";
        else
            shift = "Evening";
        Truck t = truckDAO.findAvailable(d.getStartDate(),shift,v);
        if(t!=null&&canDrive(t,d.getDeliveryDoc().getDriver())){
            double dif = t.getBaseWeight()-d.getDeliveryDoc().getTruck().getBaseWeight();
            d.getDeliveryDoc().setTruck(t);
            for(SiteReport sr:d.getDeliveryDoc().getSiteReports())
                sr.setWeightOnSite((sr.getWeightOnSite()+dif));
            deliveryDAO.update(d);
        }
        return d;
    }

    private boolean canDrive(Truck t, Driver driver) {
        if(t.getType().equals("A"))
            return true;
        if(t.getType().equals("B")&&!(driver.getLicense().equals("A")))return true;
        if(t.getType().equals("C")&&(driver.getLicense().equals("C")||driver.getLicense().equals("D")))return true;
        if(t.getType().equals("D")&&(driver.getLicense().equals("D")))return true;
        return false;
    }

    private void updateWeight(int fromOrder, int toOrder, List<SiteReport> siteReports) {
        for(SiteReport sr: siteReports){
            if(sr.getOrder()<toOrder&&sr.getOrder()>=fromOrder){
                sr.setWeightOnSite(sr.getWeightOnSite()+(product.getWeight()*amount));
                siteReportDAO.update(sr);
            }
        }
    }


    private double getTruckWeight(Delivery d){
        double currWeight = 0;
        for(SiteReport sr:d.getDeliveryDoc().getSiteReports()){
            if(sr.getWeightOnSite()>currWeight) currWeight = sr.getWeightOnSite();
        }
        return currWeight;
    }

    private Delivery createDelivery() {
        DriverDAO driverDAO = new DriverDAO();
        ShiftsAssigningDAO assDAO = new ShiftsAssigningDAO();
        ShiftsDAO shiftsDAO = new ShiftsDAO();
        List<ShiftAssigning> driverShifts = assDAO.getAvailableDrivers();
        for(ShiftAssigning ds:driverShifts){
            Shift s = shiftsDAO.findByID(ds.getShiftID());
            if(!AvailableStockKeeper(new Date(s.getDateTime().getTime()),s.getShiftTime(),branch)) continue;
            Truck t = truckDAO.findAvailable(new Date(s.getDateTime().getTime()),(s.getShiftTime()),amount*product.getWeight());
            Driver d = driverDAO.findID(ds.getEmpID());
            if(t!=null&&canDrive(t,d)){
                int did = deliveryDAO.getNewID();
                ProductDelivery pd = new ProductDelivery(did,supplier,branch,product,amount);
                SiteReport sr1 = new SiteReport(did,supplier,t.getBaseWeight()+(amount*product.getWeight()),1,new LinkedList<>());
                sr1.getProductDelivery().add(pd);
                SiteReport sr2 = new SiteReport(did,branch,t.getBaseWeight(),2,new LinkedList<>());
                sr1.getProductDelivery().add(pd);
                DeliveryDoc dd = new DeliveryDoc(did,d,t,new LinkedList<>());
                dd.getSiteReports().add(sr1);
                dd.getSiteReports().add(sr2);
                Delivery del = new Delivery(did,dd,new Date(s.getDateTime().getTime()),s.getShiftTime().equals("morning")?Time.valueOf("08:00:00"):Time.valueOf("16:00:00"));
                deliveryDAO.insert(del);
                return del;
            }
        }
        return null;
    }


    private boolean AvailableStockKeeper(Date date, Object type, Branch branch) {
        String time;

        if(type instanceof Time){
            try {
                if((new SimpleDateFormat("hh:mm:ss")).parse("17:59:00").before(((Time) type))) time = "night";
                else time = "morning";
            } catch (ParseException e) {
                return false;
            }
        }
        else time = (String)type;
        return (new ShiftsAssigningDAO()).AvailableSK(date, time, branch);
    }

    private boolean contains(Delivery d, Site site) {
        if(d.getDeliveryDoc()==null) {
            return false;
        }
        else if(d.getDeliveryDoc().getSiteReports()==null) {
            return false;
        }
        for(SiteReport sr :d.getDeliveryDoc().getSiteReports()){
            if(site.getAddress().equals(sr.getSite().getAddress()))
                return true;
        }
        return false;
    }

}
