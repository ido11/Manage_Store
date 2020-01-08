package logic.deliveries.models;

import java.util.LinkedList;
import java.util.List;

public class SiteReport {
    private int DeliveryID;
    private Site site;
    private double weightOnSite;
    private int order;
    private List<ProductDelivery> productDelivery;

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public double getWeightOnSite() {
        return weightOnSite;
    }

    public void setWeightOnSite(double weightOnSite) {
        this.weightOnSite = weightOnSite;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public List<ProductDelivery> getProductDelivery() {
        return productDelivery;
    }

    public void setProductDelivery(List<ProductDelivery> productDelivery) {
        this.productDelivery = productDelivery;
    }

    public int getDeliveryID() {
        return DeliveryID;
    }

    public void setDeliveryID(int deliveryID) {
        DeliveryID = deliveryID;
    }

    public SiteReport(int deliveryID, Site site, double weightOnSite, int order, List<ProductDelivery> productDelivery) {
        DeliveryID = deliveryID;
        this.site = site;
        this.weightOnSite = weightOnSite;
        this.order = order;
        this.productDelivery = productDelivery;
    }

    public String toString(){
        String output =  "\nsite: " + site.toString() + " \nweight on site: " + weightOnSite + "\norder: " + order + "\nproducts list:";
        for(ProductDelivery pd : productDelivery){
             output = output + pd.toString();
        }
        return  output;
    }

    public String siteRInfo(){
        List<Site> branches = new LinkedList<Site>();
        String output ="\nSupplier Details:\n" +
                "address: " + site.getAddress() + " contact person: " + site.getContactPerson() + " phone: " + site.getPhoneNumber() + "\n";
        for(ProductDelivery pd : productDelivery) {
            if (!branches.contains(pd.getBranchSite()))
                branches.add(pd.getBranchSite());
        }
        for(Site s : branches){
            output = output + "\n Destination Details: \n" +
                    "address: " + s.getAddress() + " contact person: " + s.getContactPerson() + " phone: " + s.getPhoneNumber() + "\n" +
                    "The Order Contains:\n" + "Product Name \t Amount \n";
            for (ProductDelivery pd : productDelivery){
                if(pd.getBranchSite().equals(s))
                    output = output + pd.getProduct().getName() + "\t" + pd.getCountOfProducts() + "\n";
            }
        }
        return output;
    }

}
