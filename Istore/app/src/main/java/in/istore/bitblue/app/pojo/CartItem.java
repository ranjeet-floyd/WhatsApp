package in.istore.bitblue.app.pojo;

public class CartItem {

    String ItemId;
    String ItemName;
    int ItemSoldQuantity;
    float ItemSellPrice;
    float ItemTotalAmnt;

    public CartItem() {
    }

    public CartItem(String itemId, String itemName, int itemSoldQuantity, float itemTotalAmnt) {
        ItemId = itemId;
        ItemName = itemName;
        ItemSoldQuantity = itemSoldQuantity;
        ItemTotalAmnt = itemTotalAmnt;
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

    public float getItemTotalAmnt() {
        return ItemTotalAmnt;
    }

    public void setItemTotalAmnt(float itemTotalAmnt) {
        ItemTotalAmnt = itemTotalAmnt;
    }

    public float getItemSellPrice() {
        return ItemSellPrice;
    }

    public void setItemSellPrice(float itemSellPrice) {
        ItemSellPrice = itemSellPrice;
    }

}
