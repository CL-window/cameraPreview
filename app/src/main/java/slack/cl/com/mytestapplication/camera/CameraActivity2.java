package slack.cl.com.mytestapplication.camera;

import android.graphics.Point;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import slack.cl.com.mytestapplication.R;

//   https://developer.android.com/reference/android/hardware/Camera.html
public class CameraActivity2 extends BaseActivity implements CameraInterface.CamOpenOverCallback {


    private static final String TAG = "CameraActivity";
    private final int REQ_CAMERA_PERMISSION = 0x01;
    CameraTextureView textureView = null;
    float previewRate = -1f;
    private RecyclerView mRecyclerView;
    private List<Camera.Size> list;
    private FrameLayout mFrameLayout;
    private boolean changeLayout;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);
        initUI();
        initViewParams();
        checkOrRequestCameraPermission(REQ_CAMERA_PERMISSION);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkOrRequestCameraPermission(REQ_CAMERA_PERMISSION);
    }

    @Override
    protected void onStop() {
        super.onStop();
        CameraInterface.getInstance().doStopCamera();
    }

    private void initUI() {
        mFrameLayout = (FrameLayout) findViewById(R.id.camera_layout);
        textureView = (CameraTextureView) findViewById(R.id.camera_surfaceview);
        mRecyclerView = (RecyclerView) findViewById(R.id.camera_recycleView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mTextView = (TextView) findViewById(R.id.camera_text);

        ((CheckBox) findViewById(R.id.checkBox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                changeLayout = b;
                if(!b){
                    changeLayout();
                }
            }
        });
    }

    private void changeLayout(){
        ViewGroup.LayoutParams params = mFrameLayout.getLayoutParams();
        params.width = FrameLayout.LayoutParams.MATCH_PARENT;
        params.height = FrameLayout.LayoutParams.MATCH_PARENT ;
        mFrameLayout.setLayoutParams(params);
        mTextView.setVisibility(View.GONE);
    }

    // 短边铺满，长边缩放  摄像头旋转过90
    private void changeLayout(int width,int height){
        ViewGroup.LayoutParams params = mFrameLayout.getLayoutParams();
        params.width = DisplayUtil.getScreenWidth(this);
        params.height = (int)(((float)width) / ((float)height) * DisplayUtil.getScreenWidth(this)) ;
        mFrameLayout.setLayoutParams(params);
        mTextView.setVisibility(View.VISIBLE);
        mTextView.setText("当前预览界面：" + params.height + "*" + params.width);
    }

    private void initViewParams() {

        previewRate = DisplayUtil.getScreenRate(this); //默认全屏的比例预览
        Log.i(TAG, "previewRate : " + previewRate);

    }

    private void openCamera() {
        Thread openThread = new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                CameraInterface.getInstance().doOpenCamera(CameraActivity2.this);
            }
        };
        openThread.start();
    }

    @Override
    public void onPermissionGranted(int requestCode) {
        Log.i(TAG, "onPermissionGranted...");
        openCamera();
    }

    @Override
    public void onPermissionDenied(int requestCode, @NonNull String[] deniedPermissions) {
        alertPermissionSetting(requestCode, deniedPermissions);
    }

    @Override
    public void onPermissionNeverAskAgain(int requestCode, @NonNull String[] neverAskPermissions) {
        alertPermissionSetting(requestCode, neverAskPermissions);
    }

    private void alertPermissionSetting(int requestCode, String[] permissions) {
        switch (requestCode) {
            case REQ_CAMERA_PERMISSION:
                Log.i(TAG, "alertPermissionSetting..."); // alert need Permission
                break;
        }

    }

    @Override
    public void cameraHasOpened() {
        Log.i(TAG, "cameraHasOpened...");
        if (textureView.getSurfaceTexture() == null) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (textureView.getSurfaceTexture() == null) {
                        mHandler.postDelayed(this, 100);
                    } else {
                        CameraInterface.getInstance().doStartPreview(textureView.getSurfaceTexture(), previewRate);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                list = CameraInterface.getInstance().getSupportedPreviewSizes();
                                mRecyclerView.setAdapter(new CameraPreviewSizeAdapter(CameraActivity2.this, list, itemClickListener));
                            }
                        });
                    }
                }
            }, 100);
        } else {
            CameraInterface.getInstance().doStartPreview(textureView.getSurfaceTexture(), previewRate);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    list = CameraInterface.getInstance().getSupportedPreviewSizes();
                    mRecyclerView.setAdapter(new CameraPreviewSizeAdapter(CameraActivity2.this, list, itemClickListener));
                }
            });
        }


    }

    CameraPreviewSizeAdapter.ItemClickListener itemClickListener = new CameraPreviewSizeAdapter.ItemClickListener() {

        @Override
        public void onItemClick(int pos) {
            previewRate = ((float) list.get(pos).height) / ((float) list.get(pos).width);
            CameraInterface.getInstance().doStartPreview(textureView.getSurfaceTexture(), list.get(pos));
            if(changeLayout) {
                // change view size
                changeLayout(list.get(pos).width,list.get(pos).height);
            }else {
                changeLayout();
            }

        }
    };

    @Override
    public void onClick(View view) {

    }
}

