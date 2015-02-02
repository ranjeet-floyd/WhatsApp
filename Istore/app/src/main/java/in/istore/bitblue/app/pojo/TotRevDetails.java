package in.istore.bitblue.app.pojo;

public class TotRevDetails {
    private long Staffid;
    private String ProdName;
    private int Quantity;
    private float PurchaseAmnt;
    private long Mobile;
    private String Date;

    public TotRevDetails() {
    }

    public TotRevDetails(long staffid, String prodName, int quantity, float purchaseAmnt, long mobile) {
        Staffid = staffid;
        ProdName = prodName;
        Quantity = quantity;
        PurchaseAmnt = purchaseAmnt;
        Mobile = mobile;
    }

    public long getStaffid() {
        return Staffid;
    }

    public void setStaffid(long staffid) {
        Staffid = staffid;
    }

    public String getProdName() {
        return ProdName;
    }

    public void setProdName(String prodName) {
        ProdName = prodName;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public float getPurchaseAmnt() {
        return PurchaseAmnt;
    }

    public void setPurchaseAmnt(float purchaseAmnt) {
        PurchaseAmnt = purchaseAmnt;
    }

    public long getMobile() {
        return Mobile;
    }

    public void setMobile(long mobile) {
        Mobile = mobile;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
