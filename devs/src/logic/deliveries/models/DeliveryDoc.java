package logic.deliveries.models;

import java.util.LinkedList;
import java.util.List;

public class DeliveryDoc {
    private int deliveryID;
    private Driver driver;
    private Truck truck;
    private List<SiteReport> siteReports;

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Truck getTruck() {
        return truck;
    }

    public void setTruck(Truck truck) {
        this.truck = truck;
    }


    public int getDeliveryID() {
        return deliveryID;
    }

    public void setDeliveryID(int deliveryID) {
        this.deliveryID = deliveryID;
    }

    public List<SiteReport> getSiteReports() {
        return siteReports;
    }

    public void setSiteReports(List<SiteReport> siteReports) {
        this.siteReports = siteReports;
    }

    public DeliveryDoc(int deliveryID, Driver driver, Truck truck, List<SiteReport> siteReports) {
        this.deliveryID = deliveryID;
        this.driver = driver;
        this.truck = truck;
        this.siteReports = siteReports;
        if(siteReports==null) this.siteReports = new LinkedList<>();
    }

    public String toString(){
        String output ="\ndeliveryID: " + deliveryID + " \ndriver details: " + driver.toString() + " \ntruck details:" + truck.toString()+" \nSite reports: ";
        for (SiteReport sr:siteReports) {
            output=output+sr.toString();
        }
        return output;
    }
}
