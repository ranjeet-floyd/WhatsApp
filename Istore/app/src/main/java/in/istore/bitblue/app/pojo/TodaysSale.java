package in.istore.bitblue.app.pojo;

public class TodaysSale {
    private long StaffId;
    private String ProdName;
    private int Quantity;
    private float PurchaseAmnt;
    private long Mobile;
    private String Optype;
    private String DeliveryAddress;

    public TodaysSale() {
    }

    public TodaysSale(long staffId, String prodName, int quantity, float purchaseAmnt, long mobile) {
        StaffId = staffId;
        ProdName = prodName;
        Quantity = quantity;
        PurchaseAmnt = purchaseAmnt;
        Mobile = mobile;
    }

    public long getStaffId() {
        return StaffId;
    }

    public void setStaffId(long staffId) {
        StaffId = staffId;
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

    public String getOptype() {
        return Optype;
    }

    public void setOptype(String optype) {
        Optype = optype;
    }

    public String getDeliveryAddress() {
        return DeliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        DeliveryAddress = deliveryAddress;
    }
}

