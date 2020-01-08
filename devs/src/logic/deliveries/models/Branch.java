package logic.deliveries.models;

public class Branch extends Site {
    private int id;

    public Branch(String address, String phoneNumber, String contactPerson, String area, int id) {
        super(address, phoneNumber, contactPerson, area);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String toString(){

        return ("\nid: "+ id+super.toString());
    }

}
