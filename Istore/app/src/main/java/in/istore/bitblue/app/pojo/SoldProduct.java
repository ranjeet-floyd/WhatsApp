package in.istore.bitblue.app.pojo;

public class SoldProduct {

    private String ItemId;
    private String ItemName;
    private int ItemSoldQuantity;
    private float ItemSellPrice;
    private float ItemTotalAmnt;
    private long Mobile;
    private long StaffId;
    private String Date;
    private String Optype;
    private String DeliveryAddress;
    private String Id;

    public SoldProduct() {
    }

    public SoldProduct(String itemId, String itemName, int itemSoldQuantity, float itemSellPrice, float itemTotalAmnt, long mobile, long staffId, String date) {
        ItemId = itemId;
        ItemName = itemName;
        ItemSoldQuantity = itemSoldQuantity;
        ItemSellPrice = itemSellPrice;
        ItemTotalAmnt = itemTotalAmnt;
        Mobile = mobile;
        StaffId = staffId;
        Date = date;
    }

    public String getItemId() {
        return ItemId;
    }

    public void setItemId(String itemId) {
        ItemId = itemId;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public int getItemSoldQuantity() {
        return ItemSoldQuantity;
    }

    public void setItemSoldQuantity(int itemSoldQuantity) {
        ItemSoldQuantity = itemSoldQuantity;
    }

    public float getItemSellPrice() {
        return ItemSellPrice;
    }

    public void setItemSellPrice(float itemSellPrice) {
        ItemSellPrice = itemSellPrice;
    }

    public float getItemTotalAmnt() {
        return ItemTotalAmnt;
    }

    public void setItemTotalAmnt(float itemTotalAmnt) {
        ItemTotalAmnt = itemTotalAmnt;
    }

    public long getMobile() {
        return Mobile;
    }

    public void setMobile(long mobile) {
        Mobile = mobile;
    }

    public long getStaffId() {
        return StaffId;
    }

    public void setStaffId(long staffId) {
        StaffId = staffId;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
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

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
