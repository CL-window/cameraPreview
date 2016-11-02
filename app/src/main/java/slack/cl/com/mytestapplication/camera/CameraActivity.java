package slack.cl.com.mytestapplication.camera;

import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;


import java.util.List;

import slack.cl.com.mytestapplication.R;
//  http://blog.csdn.net/yanzi1225627/article/details/33028041
public class CameraActivity extends BaseActivity implements CameraInterface.CamOpenOverCallback{


    private static final String TAG = "CameraActivity";
    private final int REQ_CAMERA_PERMISSION = 0x01;
    CameraSurfaceView surfaceView = null;
    ImageButton shutterBtn;
    float previewRate = -1f;
    private RecyclerView mRecyclerView;
    private List<Camera.Size> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkOrRequestCameraPermission(REQ_CAMERA_PERMISSION);
        setContentView(R.layout.activity_camera);
        initUI();
        initViewParams();

//        shutterBtn.setOnClickListener(this);

//        findViewById(R.id.screen_all).setOnClickListener(this);
//        findViewById(R.id.screen_16).setOnClickListener(this);
//        findViewById(R.id.screen_4).setOnClickListener(this);
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

    private void initUI(){
        surfaceView = (CameraSurfaceView)findViewById(R.id.camera_surfaceview);
        shutterBtn = (ImageButton)findViewById(R.id.btn_shutter);
        mRecyclerView = (RecyclerView) findViewById(R.id.camera_recycleView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void initViewParams(){
//        ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
//        Point p = DisplayUtil.getScreenMetrics(this);
//        params.width = p.x;
//        params.height = p.y;
//        surfaceView.setLayoutParams(params);

        previewRate = DisplayUtil.getScreenRate(this); //默认全屏的比例预览
        Log.i(TAG, "previewRate : " + previewRate);
//        ViewGroup.LayoutParams p2 = shutterBtn.getLayoutParams();
//        p2.width = DisplayUtil.dip2px(this, 80);
//        p2.height = DisplayUtil.dip2px(this, 80);
//        shutterBtn.setLayoutParams(p2);

    }

    private void openCamera(){
        Thread openThread = new Thread(){
            @Override
            public void run() {
                // TODO Auto-generated method stub
                CameraInterface.getInstance().doOpenCamera(CameraActivity.this);
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
        if(surfaceView.getSurfaceHolder() == null){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(surfaceView.getSurfaceHolder() == null){
                        mHandler.postDelayed(this,100);
                    }else {
                        CameraInterface.getInstance().doStartPreview(surfaceView.getSurfaceHolder(), previewRate);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                list = CameraInterface.getInstance().getSupportedPreviewSizes();
                                mRecyclerView.setAdapter(new CameraPreviewSizeAdapter(CameraActivity.this,list,itemClickListener));
                            }
                        });
                    }
                }
            }, 100);
        }else {
            CameraInterface.getInstance().doStartPreview(surfaceView.getSurfaceHolder(), previewRate);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    list = CameraInterface.getInstance().getSupportedPreviewSizes();
                    mRecyclerView.setAdapter(new CameraPreviewSizeAdapter(CameraActivity.this,list,itemClickListener));
                }
            });
        }



    }

    CameraPreviewSizeAdapter.ItemClickListener itemClickListener = new CameraPreviewSizeAdapter.ItemClickListener(){

        @Override
        public void onItemClick(int pos) {
            previewRate = ((float) list.get(pos).height) / ((float) list.get(pos).width);
            CameraInterface.getInstance().doStartPreview(surfaceView.getSurfaceHolder(), list.get(pos));
        }
    };

    @Override
    public void onClick(View view) {
        switch(view.getId()){
//            case R.id.btn_shutter:
////                CameraInterface.getInstance().doTakePicture();
//                break;

            case R.id.screen_all:
                previewRate = DisplayUtil.getScreenRate(this);
                break;
            case R.id.screen_16:
                previewRate = 4f / 3f;
                break;
            case R.id.screen_4:
                previewRate = 16f / 9f;
                break;
            default:break;
        }
        CameraInterface.getInstance().doStartPreview(surfaceView.getSurfaceHolder(), previewRate);
    }
}
