package slack.cl.com.mytestapplication.camera;


import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import slack.cl.com.mytestapplication.R;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "BaseActivity";

    protected static List<Activity> mActivityList = new LinkedList<>();

    public Handler mHandler;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityList.add(this);
        mHandler = new Handler();
    }


    /**
     * 检查 Camera 权限
     */
    public void checkOrRequestCameraPermission(int requestCode) {
        this.checkOrRequestPermission(requestCode,
                Manifest.permission.CAMERA);
    }

    /**
     * 检查录制权限
     */
    public void checkOrRequestRecordPermission(int requestCode) {
        this.checkOrRequestPermission(requestCode,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO);
    }

    /**
     * 检查 read/write storage 权限
     */
    public void checkOrRequestStoragePermission(int requestCode) {
        this.checkOrRequestPermission(requestCode,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    /**
     * 检查 拍照 权限 ( camera + external storage )
     */
    public void checkOrRequestTakePicturePermission(int requestCode) {
        this.checkOrRequestPermission(requestCode,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    /**
     * 检查并请求权限
     *
     * @param requestCode request code
     * @param permissions 权限
     */
    public void checkOrRequestPermission(int requestCode, String... permissions) {
        if (permissions == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            this.onPermissionGranted(requestCode);
            return;
        }

        int deniedNum = 0;
        String[] deniedPermissions = new String[permissions.length];
        for (String per : permissions) {
            if (ActivityCompat.checkSelfPermission(this, per) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions[deniedNum] = per;
                deniedNum += 1;
            }
        }

        if (deniedNum == 0) {
            this.onPermissionGranted(requestCode);
        } else {
            ActivityCompat.requestPermissions(this,
                    Arrays.copyOfRange(deniedPermissions, 0, deniedNum), requestCode);
        }
    }

    /**
     * 如果这个请求的所有权限都被 granted 了, 就会回调这个方法
     *
     * @param requestCode request code
     */
    public void onPermissionGranted(int requestCode) {
        //
    }

    /**
     * 如果请求的所有权限中任何一个被 denied 了, 都会回调这个方法,
     * 并返回被 denied 掉的权限数组
     *
     * @param requestCode       request code
     * @param deniedPermissions denied 的权限
     */
    public void onPermissionDenied(int requestCode, @NonNull String[] deniedPermissions) {
        //
    }

    /**
     * 如果一个权限被设置为 Never Ask Again, 则会回调此方法
     *
     * @param requestCode         request code
     * @param neverAskPermissions 在这次请求中的所有被 never ask again 的权限
     */
    public void onPermissionNeverAskAgain(int requestCode, @NonNull String[] neverAskPermissions) {
        //
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int deniedNum = 0;
        String[] deniedPermissions = new String[permissions.length];

        int neverAskNum = 0;
        String[] neverAskPermissions = new String[permissions.length];

        for (int i = 0; i < permissions.length; ++i) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                    neverAskPermissions[neverAskNum] = permissions[i];
                    neverAskNum += 1;
                } else {
                    deniedPermissions[deniedNum] = permissions[i];
                    deniedNum += 1;
                }
            }
        }

        if (neverAskNum != 0) {
            this.onPermissionNeverAskAgain(requestCode,
                    Arrays.copyOfRange(neverAskPermissions, 0, neverAskNum));
        } else if (deniedNum != 0) {
            this.onPermissionDenied(requestCode,
                    Arrays.copyOfRange(deniedPermissions, 0, deniedNum));
        } else {
            this.onPermissionGranted(requestCode);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActivityList.remove(this);
    }

    // 全部退出 ，强制更新时使用
    protected void exit(){
        try {
            for (Activity activity : mActivityList) {
                if(activity != null){
                    activity.finish();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            System.exit(0);//退出后台线程,以及销毁静态变量
        }
    }

}
