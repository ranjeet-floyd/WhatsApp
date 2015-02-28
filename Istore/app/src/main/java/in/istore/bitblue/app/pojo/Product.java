package in.istore.bitblue.app.pojo;

import java.io.Serializable;

public class Product implements Serializable{
    private String Id;
    private byte[] Image;
    private String Category;
    private String Name;
    private String Desc;
    private int Quantity;
    private int MinLimit;
    private float CostPrice;
    private float SellingPrice;
    private String Supplier;
    private String AddedDate;
    private int IsFavorite;
    private int sellby;
    private String custMob;
    private String deliverAddress;
    private String SoldDate;
    private int SoldQuantity;
    private int RemQuantity;
    private double SellPrice;

    public String getDeliverAddress() {
        return deliverAddress;
    }

    public void setDeliverAddress(String deliverAddress) {
        this.deliverAddress = deliverAddress;
    }

    public String getCustMob() {
        return custMob;
    }

    public void setCustMob(String custMob) {
        this.custMob = custMob;
    }

    public int getSellby() {
        return sellby;
    }

    public void setSellby(int sellby) {
        this.sellby = sellby;
    }

    public Product() {
    }

    public Product(String id, byte[] image, String name, String desc, int quantity, float sellingPrice) {
        Id = id;
        Image = image;
        Name = name;
        Desc = desc;
        Quantity = quantity;
        SellingPrice = sellingPrice;
    }

    public Product(String id, byte[] image, String category, String name, String desc, int quantity, int minlimit, float costprice, float sellingprice, String supplier, String addedDate) {
        Id = id;
        Image = image;
        Category = category;
        Name = name;
        Desc = desc;
        Quantity = quantity;
        MinLimit = minlimit;
        CostPrice = costprice;
        SellingPrice = sellingprice;
        Supplier = supplier;
        AddedDate = addedDate;
    }

    public Product(String id, byte[] image, String category, String name, int quantity, String desc, int minLimit, float costPrice, float sellingPrice, String supplier) {
        Id = id;
        Image = image;
        Category = category;
        Name = name;
        Quantity = quantity;
        Desc = desc;
        MinLimit = minLimit;
        CostPrice = costPrice;
        SellingPrice = sellingPrice;
        Supplier = supplier;
    }

    public Product(String id, byte[] image, String name) {
        Id = id;
        Image = image;
        Name = name;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public byte[] getImage() {
        return Image;
    }

    public void setImage(byte[] image) {
        Image = image;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public int getMinLimit() {
        return MinLimit;
    }

    public void setMinLimit(int minLimit) {
        MinLimit = minLimit;
    }

    public float getCostPrice() {
        return CostPrice;
    }

    public void setCostPrice(float costPrice) {
        CostPrice = costPrice;
    }

    public float getSellingPrice() {
        return SellingPrice;
    }

    public void setSellingPrice(float sellingPrice) {
        SellingPrice = sellingPrice;
    }

    public String getSupplier() {
        return Supplier;
    }

    public void setSupplier(String supplier) {
        Supplier = supplier;
    }

    public String getAddedDate() {
        return AddedDate;
    }

    public void setAddedDate(String addedDate) {
        AddedDate = addedDate;
    }

    public int getIsFavorite() {
        return IsFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        IsFavorite = isFavorite;
    }

    public String getSoldDate() {
        return SoldDate;
    }

    public void setSoldDate(String soldDate) {
        SoldDate = soldDate;
    }


    public int getSoldQuantity() {
        return SoldQuantity;
    }

    public void setSoldQuantity(int soldQuantity) {
        SoldQuantity = soldQuantity;
    }

    public int getRemQuantity() {
        return RemQuantity;
    }

    public void setRemQuantity(int remQuantity) {
        RemQuantity = remQuantity;
    }

    public double getSellPrice() {
        return SellPrice;
    }

    public void setSellPrice(double sellPrice) {
        SellPrice = sellPrice;
    }
}
