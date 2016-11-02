package slack.cl.com.mytestapplication.camera;

/**
 * <p>Description:  </p>
 * Created by slack on 2016/11/1 11:59 .
 */

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class ImageUtil {
    /**
     * 旋转Bitmap
     * @param b
     * @param rotateDegree
     * @return
     */
    public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree){
        Matrix matrix = new Matrix();
        matrix.postRotate((float)rotateDegree);
        Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);
        return rotaBitmap;
    }
}
