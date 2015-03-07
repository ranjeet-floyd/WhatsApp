package in.istore.bitblue.app.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class ImageUtil {

    public static byte[] convertBase64ImagetoByteArrayImage(String base64Image) {
        return Base64.decode(base64Image.getBytes(), Base64.DEFAULT);
    }

    public static String convertByteArrayImagetoBase64Image(byte[] byteImage) {
        return Base64.encodeToString(byteImage, Base64.DEFAULT);
    }

    public static Bitmap convertByteArrayToBitMapImage(byte[] byteImage) {
        return BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length);
    }
}
