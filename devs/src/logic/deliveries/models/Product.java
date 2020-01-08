package logic.deliveries.models;

public class Product {
    private int id;
    private String name;
    private double weight;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Product(int id, String name, double weight) {
        this.id = id;
        this.name = name;
        this.weight = weight;
    }

    public String toString(){
        return "\nID: " + id + " \nname: " + name + " \nweight : " + weight;
    }
}
