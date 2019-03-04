package ad.vipcare.com.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import ad.vipcare.com.usb.AUsbTestActivity;

/**
 * Created by zeting
 * Date 19/1/10.
 */

public class PermissionUtil {

    /**
     * 请求窗口权限
     */
    public static void requestPermission (Context mCon){
        if (Build.VERSION.SDK_INT >= 23) {
            if (! Settings.canDrawOverlays(mCon)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" +  mCon.getPackageName()));
                ((Activity)mCon).startActivityForResult(intent,10);
            }
        }
    }

    public static void onActivityResult(Context mCon , int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(mCon)) {
                    // SYSTEM_ALERT_WINDOW permission not granted...
                    Toast.makeText(mCon ,"not granted",Toast.LENGTH_SHORT);
                }
            }
        }
    }
}
