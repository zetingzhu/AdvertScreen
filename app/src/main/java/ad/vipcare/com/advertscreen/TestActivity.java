package ad.vipcare.com.advertscreen;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

import ad.vipcare.com.timeUtil.TimeService;
import ad.vipcare.com.usb.AUSBBroadCastReceiver;

/**
 * Created by zeting
 * Date 19/1/10.
 */

public class TestActivity extends Activity {

    private static final String TAG = "TestActivity";
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        //启动后台服务
        Intent service=new Intent(this, TimeService.class);
        startService(service);

        String str = null ;
        new String(str);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 100);

    }



    public static void hideUiMenu(Activity mContext){
        mContext.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }



}
