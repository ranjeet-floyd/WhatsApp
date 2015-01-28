package in.istore.bitblue.app.pojo;

public class Category {
    private String CategoryName;

    public Category() {
    }

    public Category(String categoryName) {
        CategoryName = categoryName;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }
}
