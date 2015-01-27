package in.istore.bitblue.app.utilities;

import android.app.Application;
import android.graphics.Bitmap;

import java.util.ArrayList;

import in.istore.bitblue.app.pojo.Product;

public class GlobalVariables extends Application {
    private String fbName;
    private String fbEmail;
    private String gName;
    private String gEmail;
    private Bitmap profPic;
    private static int prodImageCount = 1;
    private String imagePath;
    private int storeId;
    private long adminMobile;

    private ArrayList<Product> productsList;

    public String getFbEmail() {
        return fbEmail;
    }

    public void setFbEmail(String fbEmail) {
        this.fbEmail = fbEmail;
    }

    public String getFbName() {
        return fbName;
    }

    public void setFbName(String fbName) {
        this.fbName = fbName;
    }

    public String getgName() {
        return gName;
    }

    public void setgName(String gName) {
        this.gName = gName;
    }

    public String getgEmail() {
        return gEmail;
    }

    public void setgEmail(String gEmail) {
        this.gEmail = gEmail;
    }

    public Bitmap getProfPic() {
        return profPic;
    }

    public void setProfPic(Bitmap profPic) {
        this.profPic = profPic;
    }

    public int getProdImageCount() {
        return prodImageCount;
    }

    public void setProdImageCount(int prodImageCount) {
        GlobalVariables.prodImageCount = prodImageCount;
    }

    public void increaseProImgCount() {
        ++prodImageCount;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public ArrayList<Product> getProductsList() {
        return productsList;
    }

    public void setProductsList(ArrayList<Product> productsList) {
        this.productsList = productsList;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public long getAdminMobile() {
        return adminMobile;
    }

    public void setAdminMobile(long adminMobile) {
        this.adminMobile = adminMobile;
    }
}
