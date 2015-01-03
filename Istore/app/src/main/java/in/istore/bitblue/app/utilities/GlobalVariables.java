package in.istore.bitblue.app.utilities;

import android.app.Application;
import android.graphics.Bitmap;

public class GlobalVariables extends Application {
    private String userName;
    private String userEmail;
    private Bitmap profPic;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Bitmap getProfPic() {
        return profPic;
    }

    public void setProfPic(Bitmap profPic) {
        this.profPic = profPic;
    }
}
