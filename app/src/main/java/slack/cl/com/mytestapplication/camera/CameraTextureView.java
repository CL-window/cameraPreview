package slack.cl.com.mytestapplication.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

/**
 * <p>Description: CameraTextureView </p>
 * Created by slack on 2016/11/1 11:44 .
 */

public class CameraTextureView extends TextureView implements TextureView.SurfaceTextureListener
{
    private final static String TAG = "CameraSurfaceView";
    private SurfaceTexture mSurfaceTexture;


    public CameraTextureView(Context context) {
        this(context,null);
    }

    public CameraTextureView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CameraTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSurfaceTextureListener(this);
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture,int width, int height) {
        Log.i(TAG, "onSurfaceTextureAvailable...");
        mSurfaceTexture = surfaceTexture;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture,int width, int height) {
        mSurfaceTexture = surfaceTexture;
        Log.i(TAG, "onSurfaceTextureSizeChanged...");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        mSurfaceTexture = null;
        Log.i(TAG, "onSurfaceTextureDestroyed...");
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        mSurfaceTexture = surfaceTexture;
//        Log.i(TAG, "onSurfaceTextureUpdated...");
    }

    public SurfaceTexture getSurfaceTexture(){
        return mSurfaceTexture;
    }

}
