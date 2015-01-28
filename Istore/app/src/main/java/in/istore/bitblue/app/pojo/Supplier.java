package in.istore.bitblue.app.pojo;

public class Supplier {

    private String Name;
    private long Mobile;
    private String Address;
    private String StartDate;

    public Supplier() {
    }

    public Supplier(String name, long mobile, String address, String startDate) {
        Name = name;
        Mobile = mobile;
        Address = address;
        StartDate = startDate;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public long getMobile() {
        return Mobile;
    }

    public void setMobile(long mobile) {
        Mobile = mobile;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }
}
