package slack.cl.com.mytestapplication.camera;

/**
 * <p>Description:  </p>
 * Created by slack on 2016/11/1 11:53 .
 */

import java.io.IOException;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;

import static android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT;

public class CameraInterface implements Camera.PreviewCallback{
    private static final String TAG = "CameraInterface";
    private Camera mCamera;
    private Camera.Parameters mParams;
    private boolean isPreviewing = false;
    private float mPreviwRate = -1f;
    private static CameraInterface mCameraInterface;

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        Log.i(TAG, "PreviewCallback ..." + bytes.length );
        camera.addCallbackBuffer(bytes);
    }

    public interface CamOpenOverCallback {
        void cameraHasOpened();
    }

    private CameraInterface() {

    }

    public static synchronized CameraInterface getInstance() {
        if (mCameraInterface == null) {
            mCameraInterface = new CameraInterface();
        }
        return mCameraInterface;
    }

    /**
     * 打开Camera
     *
     * @param callback
     */
    public void doOpenCamera(CamOpenOverCallback callback) {
        Log.i(TAG, "Camera open....");

        try {
            mCamera = Camera.open(CAMERA_FACING_FRONT);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "e :" + e.getMessage());
        }
        Log.i(TAG, "Camera open over....");
        callback.cameraHasOpened();
    }

    /**
     * 开启预览
     *
     * @param holder
     * @param previewRate
     */
    public void doStartPreview(SurfaceHolder holder, float previewRate) {
        Log.i(TAG, "doStartPreview...");
        if (isPreviewing) {
            mCamera.stopPreview();
//            return;
        }
        if (mCamera != null) {

            mParams = mCamera.getParameters();
            dump(mParams);
//            mParams.setPictureFormat(PixelFormat.JPEG);//设置拍照后存储的图片格式
            //设置PreviewSize和PictureSize
//            Size pictureSize = CamParaUtil.getInstance().getPropPictureSize(
//                    mParams.getSupportedPictureSizes(),previewRate, 800);
//            mParams.setPictureSize(pictureSize.width, pictureSize.height);
            Size previewSize = CamParaUtil.getInstance().getPropPreviewSize(
                    mParams.getSupportedPreviewSizes(), previewRate, 800);
            mParams.setPreviewSize(previewSize.width, previewSize.height);

            mCamera.setDisplayOrientation(90);

            List<String> focusModes = mParams.getSupportedFocusModes();
            if (focusModes.contains("continuous-video")) {
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            mCamera.setParameters(mParams);
            mCamera.setPreviewCallbackWithBuffer(this);
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();//开启预览
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            isPreviewing = true;
            mPreviwRate = previewRate;

            mParams = mCamera.getParameters(); //重新get一次
            Log.i(TAG, "PreviewSize--With = " + mParams.getPreviewSize().width
                    + "Height = " + mParams.getPreviewSize().height);
            Log.i(TAG, "PictureSize--With = " + mParams.getPictureSize().width
                    + "Height = " + mParams.getPictureSize().height);
        }
    }

    public void doStartPreview(SurfaceHolder holder, Size size) {
        Log.i(TAG, "doStartPreview...");
        if (isPreviewing) {
            mCamera.stopPreview();
        }
        if (mCamera != null) {

            mParams = mCamera.getParameters();
            dump(mParams);

            mParams.setPreviewSize(size.width, size.height);

            mCamera.setDisplayOrientation(90);

            mCamera.setParameters(mParams);

            mCamera.setPreviewCallbackWithBuffer(this);
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();//开启预览
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            isPreviewing = true;

        }
    }

    public void doStartPreview(SurfaceTexture holder, float previewRate) {
        Log.i(TAG, "doStartPreview..." + previewRate);
        if (isPreviewing) {
            mCamera.stopPreview();
//            return;
        }
        if (mCamera != null) {

            mParams = mCamera.getParameters();
            dump(mParams);

//            Size previewSize = CamParaUtil.getInstance().getPropPreviewSize(
//                    mParams.getSupportedPreviewSizes(), previewRate, 800);
//            mParams.setPreviewSize(previewSize.width, previewSize.height);

            mCamera.setDisplayOrientation(90);

            List<String> focusModes = mParams.getSupportedFocusModes();
            if (focusModes.contains("continuous-video")) {
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            mCamera.setParameters(mParams);
//            mCamera.setPreviewCallback(this);

            // 对质量和帧率要求很高的时候  对buffer内存的重用可以提高preview效率和帧率
//            mCamera.addCallbackBuffer(new byte[previewSize.width*previewSize.height * 2]);
//            mCamera.addCallbackBuffer(new byte[previewSize.width*previewSize.height * 2]);
//            mCamera.addCallbackBuffer(new byte[previewSize.width*previewSize.height * 2]);
//            mCamera.addCallbackBuffer(new byte[previewSize.width*previewSize.height * 2]);
//
//            mCamera.setPreviewCallbackWithBuffer(this);
            try {
                mCamera.setPreviewTexture(holder);
                mCamera.startPreview();//开启预览
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            isPreviewing = true;
            mPreviwRate = previewRate;

//            mParams = mCamera.getParameters(); //重新get一次
//            Log.i(TAG, "PreviewSize--With = " + mParams.getPreviewSize().width
//                    + "Height = " + mParams.getPreviewSize().height);
//            Log.i(TAG, "PictureSize--With = " + mParams.getPictureSize().width
//                    + "Height = " + mParams.getPictureSize().height);
        }
    }

    public void doStartPreview(SurfaceTexture holder, Size size) {
        Log.i(TAG, "doStartPreview...");
        if (isPreviewing) {
            mCamera.stopPreview();
        }
        if (mCamera != null) {

            mParams = mCamera.getParameters();
            dump(mParams);

            mParams.setPreviewSize(size.width, size.height);

            mCamera.setDisplayOrientation(90);

            mCamera.setParameters(mParams);
//            mCamera.setPreviewCallbackWithBuffer(this);
            try {
                mCamera.setPreviewTexture(holder);
                mCamera.startPreview();//开启预览
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            isPreviewing = true;

        }
    }

    private void dump(Camera.Parameters parameters) {
//        Camera.Size size = parameters.getPreviewSize();
//        Log.e(TAG, "PreviewSize: " + size.width + ", " + size.height);
//
//        List<int[]> ranges = parameters.getSupportedPreviewFpsRange();
//        Log.e(TAG, "supported fps size: " + (ranges == null ? 0 : ranges.size()));
//        if (ranges != null) {
//            for (int[] r : ranges) {
//                Log.e(TAG, "Support: (" + r[0] + ", " + r[1] + ")");
//            }
//        }
//
//        List<Integer> supportedFormats = parameters.getSupportedPreviewFormats();
//        if (supportedFormats != null) {
//            for (Integer format : supportedFormats) {
//                Log.e(TAG, "Support Preview Formats: " + cameraFormatForPixelFormat(format) );
//            }
//        }
//
//        List<Camera.Size> supportedSize = parameters.getSupportedPreviewSizes();
//        if (supportedSize != null) {
//            for (Camera.Size ss : supportedSize) {
//                Log.e(TAG, "Support Preview Size: " + "(" + ss.width + ", " + ss.height + " )");
//            }
//        }
//
//        List<String> focusModes = mParams.getSupportedFocusModes();
//        for(String mode : focusModes){
//            Log.i(TAG, "Supported Focus Modes: " + mode);
//        }
//
//        supportedSize = parameters.getSupportedPictureSizes();
//        if (supportedSize != null) {
//            for (Camera.Size ss : supportedSize) {
//                Log.e(TAG, "Supported Picture Size: " + "(" + ss.width + ", " + ss.height + " )");
//            }
//        }
//
//        List<String> antis = parameters.getSupportedAntibanding();
//        if (antis != null) {
//            for (String s : antis) {
//                Log.e(TAG, "Supported antis: " + s);
//            }
//        }
//
//        List<String> mode = parameters.getSupportedSceneModes();
//        if (mode != null) {
//            for (String s : mode) {
//                Log.e(TAG, "Supported scene mode: " + s);
//            }
//        }
//
//        List<String> white = parameters.getSupportedWhiteBalance();
//        if (white != null) {
//            for (String s : white) {
//                Log.e(TAG, "Supported White Balance: " + s);
//            }
//        }
    }

    private String cameraFormatForPixelFormat(int pixel_format) {
        switch(pixel_format) {
            case ImageFormat.NV16:      return "PIXEL_FORMAT_YUV422SP";
            case ImageFormat.NV21:      return "PIXEL_FORMAT_YUV420SP";
            case ImageFormat.YUY2:      return "PIXEL_FORMAT_YUV422I";
            case ImageFormat.YV12:      return "PIXEL_FORMAT_YUV420P";
            case ImageFormat.RGB_565:   return "PIXEL_FORMAT_RGB565";
            case ImageFormat.JPEG:      return "PIXEL_FORMAT_JPEG";
            default:                    return " ";
        }

    }

    /**
     * 停止预览，释放Camera
     */
    public void doStopCamera() {
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            isPreviewing = false;
            mPreviwRate = -1f;
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 拍照
     */
    public void doTakePicture() {
        if (isPreviewing && (mCamera != null)) {
            mCamera.takePicture(mShutterCallback, null, mJpegPictureCallback);
        }
    }

    public List<Size> getSupportedPreviewSizes(){
        if(mCamera == null) return null;
        return mCamera.getParameters().getSupportedPreviewSizes();
    }

    public List<Size> getSupportedPictureSizes(){
        if(mCamera == null) return null;
        return mCamera.getParameters().getSupportedPictureSizes();
    }

    public Camera.Parameters getParameters(){
        if(mCamera == null) return null;
        return mCamera.getParameters();
    }

    /*为了实现拍照的快门声音及拍照保存照片需要下面三个回调变量*/
    ShutterCallback mShutterCallback = new ShutterCallback()
            //快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。
    {
        public void onShutter() {
            // TODO Auto-generated method stub
            Log.i(TAG, "myShutterCallback:onShutter...");
        }
    };
    PictureCallback mRawCallback = new PictureCallback()
            // 拍摄的未压缩原数据的回调,可以为null
    {

        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            Log.i(TAG, "myRawCallback:onPictureTaken...");

        }
    };
    PictureCallback mJpegPictureCallback = new PictureCallback()
            //对jpeg图像数据的回调,最重要的一个回调
    {
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            Log.i(TAG, "myJpegCallback:onPictureTaken...");
            Bitmap b = null;
            if (null != data) {
                b = BitmapFactory.decodeByteArray(data, 0, data.length);//data是字节数据，将其解析成位图
                mCamera.stopPreview();
                isPreviewing = false;
            }
            //保存图片到sdcard
            if (null != b) {
                //设置FOCUS_MODE_CONTINUOUS_VIDEO)之后，myParam.set("rotation", 90)失效。
                //图片竟然不能旋转了，故这里要旋转下
                Bitmap rotaBitmap = ImageUtil.getRotateBitmap(b, 90.0f);
                FileUtil.saveBitmap(rotaBitmap);
            }
            //再次进入预览
            mCamera.startPreview();
            isPreviewing = true;
        }
    };


}
