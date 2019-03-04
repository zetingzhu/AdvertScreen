package com.vipcare.listenerservice;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 启动监听服务
        startService(new Intent(MainActivity.this , ServiceListen.class ));
        // 注册权限
//        requestPermiss() ;
        checkUsagePermission();

//        new Handler().postDelayed(new Runnable(){
//            public void run() {
//                // 3s 后启动APP
//                startADActivity() ;
//            }
//        }, 7000 );

    }


    public void requestPermiss(){
        // 申请权限的使用
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.PACKAGE_USAGE_STATS ) != PackageManager.PERMISSION_GRANTED) {
            // 没有权限，申请权限。
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.PACKAGE_USAGE_STATS)) {
                // 用户拒绝过这个权限了，应该提示用户，为什么需要这个权限。
                Log.i(TAG, "用户拒绝过这个权限了，应该提示用户，为什么需要这个权限。" );
            } else {
                Log.i(TAG, "申请权限" );
                //  申请读取系统磁盘权限
                ActivityCompat.requestPermissions(this ,new String[]{Manifest.permission.PACKAGE_USAGE_STATS } , 1101 );
            }
        } else {
            // 有权限了，去放肆吧。

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1101: {
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限被用户同意，可以去放肆了。
                    Log.i(TAG, "申请权限 成功" );
                } else {
                    // 权限被用户拒绝了，洗洗睡吧。
                    Log.i(TAG, "申请权限 失败" );
                }
                return;
            }
        }
    }

    private boolean checkUsagePermission() {
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
            AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            mode = appOps.checkOpNoThrow("android:get_usage_stats", android.os.Process.myUid(), getPackageName());
            boolean granted = mode == AppOpsManager.MODE_ALLOWED;
            if (!granted) {
                Log.i(TAG, "跳转到权限界面开启权限" );
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivityForResult(intent, 1);
                return false;
            }
        }
        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            mode = appOps.checkOpNoThrow("android:get_usage_stats", android.os.Process.myUid(), getPackageName());
            boolean granted = mode == AppOpsManager.MODE_ALLOWED;
            if (!granted) {
                Toast.makeText(this, "请开启该权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 启动广播app
     */
    public void startADActivity(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        ComponentName componentName = new ComponentName("ad.vipcare.com.advertscreen", "ad.vipcare.com.advertscreen.ActivityAdvert");
        intent.setComponent(componentName);
        startActivity(intent);
    }

}
