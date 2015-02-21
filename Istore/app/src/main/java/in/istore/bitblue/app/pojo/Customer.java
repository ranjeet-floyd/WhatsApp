package in.istore.bitblue.app.pojo;

public class Customer {
    private long Mobile;
    private String PurchaseAmount;

    public Customer() {
    }

    public Customer(long mobile, String purchaseAmount) {
        Mobile = mobile;
        PurchaseAmount = purchaseAmount;
    }

    public long getMobile() {
        return Mobile;
    }

    public void setMobile(long mobile) {
        Mobile = mobile;
    }

    public String getPurchaseAmount() {
        return PurchaseAmount;
    }

    public void setPurchaseAmount(String purchaseAmount) {
        PurchaseAmount = purchaseAmount;
    }
}
