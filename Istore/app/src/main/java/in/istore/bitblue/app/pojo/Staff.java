package in.istore.bitblue.app.pojo;

public class Staff {
    private int StaffId;
    private int StoreId;
    private String Name;
    private long Mobile;
    private String Passwd;
    private String Address;
    private String JoinDate;

    private long TotalSales;

    public Staff(){}

    public Staff(int staffId, int storeId, String name, long mobile, String passwd, String address, String joinDate) {
        StaffId = staffId;
        StoreId = storeId;
        Name = name;
        Mobile = mobile;
        Passwd = passwd;
        Address = address;
        JoinDate = joinDate;
    }

    public int getStaffId() {
        return StaffId;
    }

    public void setStaffId(int staffId) {
        StaffId = staffId;
    }

    public int getStoreId() {
        return StoreId;
    }

    public void setStoreId(int storeId) {
        StoreId = storeId;
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

    public String getPasswd() {
        return Passwd;
    }

    public void setPasswd(String passwd) {
        Passwd = passwd;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getJoinDate() {
        return JoinDate;
    }

    public void setJoinDate(String joinDate) {
        JoinDate = joinDate;
    }

    public long getTotalSales() {
        return TotalSales;
    }

    public void setTotalSales(long totalSales) {
        TotalSales = totalSales;
    }
}
