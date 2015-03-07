package in.istore.bitblue.app.pojo;

public class Staff {
    private String StaffId;
    private String StoreId;
    private String Name;
    private String Mobile;
    private String Passwd;
    private String Address;
    private String JoinDate;
    private String TotalSales;

    public Staff(){}

    public Staff(String staffId, String storeId, String name, String mobile, String passwd, String address, String joinDate) {
        StaffId = staffId;
        StoreId = storeId;
        Name = name;
        Mobile = mobile;
        Passwd = passwd;
        Address = address;
        JoinDate = joinDate;
    }

    public String getStaffId() {
        return StaffId;
    }

    public void setStaffId(String staffId) {
        StaffId = staffId;
    }

    public String getStoreId() {
        return StoreId;
    }

    public void setStoreId(String storeId) {
        StoreId = storeId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
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

    public String getTotalSales() {
        return TotalSales;
    }

    public void setTotalSales(String totalSales) {
        TotalSales = totalSales;
    }
}
