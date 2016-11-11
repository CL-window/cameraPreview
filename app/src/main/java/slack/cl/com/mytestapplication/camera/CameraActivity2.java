package slack.cl.com.mytestapplication.camera;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
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
                if (!b) {
                    changeLayout();
                }
            }
        });

        findViewById(R.id.camera_about).setOnClickListener(this);
    }

    private void changeLayout() {
        ViewGroup.LayoutParams params = mFrameLayout.getLayoutParams();
        params.width = FrameLayout.LayoutParams.MATCH_PARENT;
        params.height = FrameLayout.LayoutParams.MATCH_PARENT;
        mFrameLayout.setLayoutParams(params);
        mTextView.setVisibility(View.GONE);
    }

    // 短边铺满，长边缩放  摄像头旋转过90
    private void changeLayout(int width, int height) {
        ViewGroup.LayoutParams params = mFrameLayout.getLayoutParams();
        params.width = DisplayUtil.getScreenWidth(this);
        params.height = (int) (((float) width) / ((float) height) * DisplayUtil.getScreenWidth(this));
        mFrameLayout.setLayoutParams(params);
        mTextView.setVisibility(View.VISIBLE);
        int gys = gys2(params.height, params.width);
        mTextView.setText("预览界面：" + params.height + "*" + params.width
        +"  " + params.height / gys +":"+ params.width / gys);
    }

    int gys2(int m, int n)//递归实现
    {
        int k, y;
        if (m < n) {
            k = m;
            m = n;
            n = k;
        }
        y = m % n;
        if (y == 0) {
            return n;
        } else {
            m = n;
            n = y;
            return gys2(m, n);
        }
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
                        initData();
                    }
                }
            }, 100);
        } else {
            initData();
        }
    }

    private void initData() {
        CameraInterface.getInstance().doStartPreview(textureView.getSurfaceTexture(), previewRate);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                list = CameraInterface.getInstance().getSupportedPreviewSizes();
                Collections.sort(list, mSizeComparator);
                mRecyclerView.setAdapter(new CameraPreviewSizeAdapter(CameraActivity2.this, list, itemClickListener));
            }
        });
    }

    CameraPreviewSizeAdapter.ItemClickListener itemClickListener = new CameraPreviewSizeAdapter.ItemClickListener() {

        @Override
        public void onItemClick(int pos) {
            previewRate = ((float) list.get(pos).height) / ((float) list.get(pos).width);
            CameraInterface.getInstance().doStartPreview(textureView.getSurfaceTexture(), list.get(pos));
            if (changeLayout) {
                // change view size
                changeLayout(list.get(pos).width, list.get(pos).height);
            } else {
                changeLayout();
            }

        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.camera_about:
                showAbout();
                break;
        }
    }

    /**
     * 从大到小排序
     */
    private Comparator<Camera.Size> mSizeComparator = new Comparator<Camera.Size>() {
        @Override
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            int l = lhs.width * lhs.height;
            int r = rhs.width * rhs.height;
            if (l > r) {
                return -1;
            } else if (l < r) {
                return 1;
            }

            return 0;
        }
    };

    private void showAbout() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Camera Parameters");
        StringBuilder about_string = new StringBuilder();
        Camera.Parameters parameters = CameraInterface.getInstance().getParameters();
        if (parameters == null) {
            return;
        }

        about_string.append("\n--Android API version: ");
        about_string.append(Build.VERSION.SDK_INT);
        about_string.append("\n--Device manufacturer: ");
        about_string.append(Build.MANUFACTURER);
        about_string.append("\n--Device model: ");
        about_string.append(Build.MODEL);
        about_string.append("\n--Device code-name: ");
        about_string.append(Build.HARDWARE);
        about_string.append("\n--Device variant: ");
        about_string.append(Build.DEVICE);
        {
            ActivityManager activityManager = (ActivityManager) getSystemService(
                    Activity.ACTIVITY_SERVICE);
            about_string.append("\n--Standard max heap (MB): ");
            about_string.append(activityManager.getMemoryClass());
            about_string.append("\n--Large max heap (MB): ");
            about_string.append(activityManager.getLargeMemoryClass());
        }
        {
            Point display_size = new Point();
            Display display = getWindowManager().getDefaultDisplay();
            display.getSize(display_size);
            about_string.append("\n--Display size: ");
            about_string.append(display_size.x);
            about_string.append("x");
            about_string.append(display_size.y);
        }

        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        if (sizes.size() > 0) {
            about_string.append("\n--Preview resolutions: ");
            for (int i = 0; i < sizes.size(); i++) {
                if (i > 0) {
                    about_string.append(", ");
                }
                about_string.append(sizes.get(i).width);
                about_string.append("x");
                about_string.append(sizes.get(i).height);
            }
        }

        sizes = parameters.getSupportedPictureSizes();
        if (sizes.size() > 0) {
            about_string.append("\n--Photo resolutions: ");
            for (int i = 0; i < sizes.size(); i++) {
                if (i > 0) {
                    about_string.append(", ");
                }
                about_string.append(sizes.get(i).width);
                about_string.append("x");
                about_string.append(sizes.get(i).height);
            }
        }

        sizes = parameters.getSupportedVideoSizes();
        if (sizes.size() > 0) {
            about_string.append("\n--Video resolutions: ");
            for (int i = 0; i < sizes.size(); i++) {
                if (i > 0) {
                    about_string.append(", ");
                }
                about_string.append(sizes.get(i).width);
                about_string.append("x");
                about_string.append(sizes.get(i).height);
            }
        }

        alertDialog.setMessage(about_string);
        alertDialog.setPositiveButton("OK", null);
        alertDialog.show();


    }
}

