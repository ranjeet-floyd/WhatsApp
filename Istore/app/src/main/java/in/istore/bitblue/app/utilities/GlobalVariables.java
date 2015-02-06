package in.istore.bitblue.app.utilities;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;

import in.istore.bitblue.app.pojo.Product;

public class GlobalVariables extends Application {
    private String fbName;
    private String fbEmail;
    private String gName;
    private String gEmail;
    private String Email;
    private Bitmap profPic;
    private static int prodImageCount = 1;
    private String imagePath;
    private int storeId;
    private long adminMobile;
    private long staffId;
    private String AdminPass;
    private String StaffPass;
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

    public long getStaffId() {
        return staffId;
    }

    public void setStaffId(long staffId) {
        this.staffId = staffId;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getAdminPass() {
        return AdminPass;
    }

    public void setAdminPass(String adminPass) {
        AdminPass = adminPass;
    }

    public String getStaffPass() {
        return StaffPass;
    }

    public void setStaffPass(String staffPass) {
        StaffPass = staffPass;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
