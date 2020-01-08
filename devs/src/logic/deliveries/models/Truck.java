package logic.deliveries.models;

public class Truck {
    private String licensePlate;
    private String type;
    private double baseWeight;
    private double maxWeight;


    public Truck(String licensePlate, String type, double baseWeight, double maxWeight) {
        this.licensePlate = licensePlate;
        this.type = type;
        this.baseWeight = baseWeight;
        this.maxWeight = maxWeight;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getBaseWeight() {
        return baseWeight;
    }

    public void setBaseWeight(double baseWeight) {
        this.baseWeight = baseWeight;
    }

    public double getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public String toString() {
        return "\nlicense plate: " + licensePlate + " \ntype: " + type +" \nbase weight: " + baseWeight + " \nmax weight: " + maxWeight;
    }
}
