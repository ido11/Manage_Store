package logic.deliveries.models;

import java.sql.Time;
import java.sql.Date;

public class Delivery {
    private int deliveryID;
    private DeliveryDoc deliveryDoc;
    private Date startDate;
    private Time startTime;

    public DeliveryDoc getDeliveryDoc() {
        return deliveryDoc;
    }

    public void setDeliveryDoc(DeliveryDoc deliveryDoc) {
        this.deliveryDoc = deliveryDoc;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public int getDeliveryID() {
        return deliveryID;
    }

    public void setDeliveryID(int deliveryID) {
        this.deliveryID = deliveryID;
    }

    public Delivery(int deliveryID, DeliveryDoc deliveryDoc, Date startDate, Time startTime) {
        this.deliveryID = deliveryID;
        this.deliveryDoc = deliveryDoc;
        this.startDate = startDate;
        this.startTime = startTime;
    }

    public String toString(){
        return "deliveryID: "+ deliveryID + "\ndeliveryDoc: " +deliveryDoc + " \nstartDate: " + startDate.toString() +" \nstartTime: " + startTime.toString();
    }

    public String DeliveryInfo(){
        String output = "delivery details: " +
                "\ndate: " + this.startDate + "   truck number: " + this.deliveryDoc.getTruck() +
                "\ntime: " + this.startTime + "   driver: " + this.deliveryDoc.getDriver() + "\n";
        for(SiteReport sr : deliveryDoc.getSiteReports()){
            if(sr.getSite() instanceof  Supplier)
                output = output + sr.siteRInfo();
        }
        return output;
    }

}
