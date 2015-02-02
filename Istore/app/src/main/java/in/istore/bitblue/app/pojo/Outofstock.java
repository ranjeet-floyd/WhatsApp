package in.istore.bitblue.app.pojo;

public class Outofstock {
    private String ProdName;
    private int RemQuantity;
    private long SuppMobile;

    public Outofstock() {
    }

    public Outofstock(String prodName, int remQuantity, long suppMobile) {
        ProdName = prodName;
        RemQuantity = remQuantity;
        SuppMobile = suppMobile;
    }

    public String getProdName() {
        return ProdName;
    }

    public void setProdName(String prodName) {
        ProdName = prodName;
    }

    public int getRemQuantity() {
        return RemQuantity;
    }

    public void setRemQuantity(int remQuantity) {
        RemQuantity = remQuantity;
    }

    public long getSuppMobile() {
        return SuppMobile;
    }

    public void setSuppMobile(long suppMobile) {
        SuppMobile = suppMobile;
    }
}
