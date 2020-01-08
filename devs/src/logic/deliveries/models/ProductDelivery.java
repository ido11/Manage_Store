package logic.deliveries.models;

public class ProductDelivery {
    private int deliveryID;
    private Site suppSite;
    private Site branchSite;
    private logic.inventory.models.Product product;
    private int countOfProducts;

    public ProductDelivery(int deliveryID, Site suppSite, Site branchSite, logic.inventory.models.Product product, int countOfProducts) {
        this.deliveryID = deliveryID;
        this.suppSite = suppSite;
        this.branchSite = branchSite;
        this.product = product;
        this.countOfProducts = countOfProducts;
    }

    public int getDeliveryID() {
        return deliveryID;
    }

    public void setDeliveryID(int deliveryID) {
        this.deliveryID = deliveryID;
    }

    public Site getSuppSite() {
        return suppSite;
    }

    public void setSuppSite(Site suppSite) {
        this.suppSite = suppSite;
    }

    public Site getBranchSite() {
        return branchSite;
    }

    public void setBranchSite(Site branchSite) {
        this.branchSite = branchSite;
    }

    public logic.inventory.models.Product getProduct() {
        return product;
    }

    public void setProduct(logic.inventory.models.Product product) {
        this.product = product;
    }

    public int getCountOfProducts() {
        return countOfProducts;
    }

    public void setCountOfProducts(int countOfProducts) {
        this.countOfProducts = countOfProducts;
    }

    public  String toString(){
        return  "\ndelivery id: " + deliveryID + " \nsupplierSIte: " + suppSite.toString() + " \nbranchSite: " + branchSite.toString() + " \nproguct : " + product.toString() + " \namount: " + countOfProducts;
    }


}
