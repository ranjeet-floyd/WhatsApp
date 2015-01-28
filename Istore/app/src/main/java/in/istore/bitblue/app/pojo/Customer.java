package in.istore.bitblue.app.pojo;

public class Customer {
    private long Mobile;
    private long PurchaseAmount;

    public Customer() {
    }

    public Customer(long mobile, long purchaseAmount) {
        Mobile = mobile;
        PurchaseAmount = purchaseAmount;
    }

    public long getMobile() {
        return Mobile;
    }

    public void setMobile(long mobile) {
        Mobile = mobile;
    }

    public long getPurchaseAmount() {
        return PurchaseAmount;
    }

    public void setPurchaseAmount(long purchaseAmount) {
        PurchaseAmount = purchaseAmount;
    }
}
