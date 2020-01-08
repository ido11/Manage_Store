package logic.deliveries.models;

public abstract class Site {
    private String address;
    private String phoneNumber;
    private String contactPerson;
    private String area;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Site(String address, String phoneNumber, String contactPerson, String area) {
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.contactPerson = contactPerson;
        this.area = area;
    }

    public String toString(){
        return "\naddress: " + address + " \nphoneNumber: " + phoneNumber + " \ncontact Person: " + contactPerson + " \narea: " + area;
    }
}
