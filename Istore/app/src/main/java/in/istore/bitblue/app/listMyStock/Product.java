package in.istore.bitblue.app.listMyStock;

public class Product {
    private String Id;
    private byte[] Image;
    private String Name;
    private String Desc;
    private String Quantity;
    private String Price;

    public Product() {
    }

    public Product(String id, byte[] image, String name, String desc, String quantity, String price) {
        Id = id;
        Image = image;
        Name = name;
        Desc = desc;
        Quantity = quantity;
        Price = price;
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

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }
}
