package logic.deliveries.models;

import java.util.List;

public class Supplier extends Site {
    private int id;


    public Supplier(String address, String phoneNumber, String contactPerson, String area, int id) {
        super(address, phoneNumber, contactPerson, area);
        this.id = id;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString() {
        return "\nid: " + id +super.toString();
    }
}


