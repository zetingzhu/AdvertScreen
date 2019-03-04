package ad.vipcare.com.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ad.vipcare.com.advertscreen.ActivityAdvert;

/**
 * 自启动广播
 * Created by zeting
 * Date 19/1/29.
 */
public class MyBootReceiver extends BroadcastReceiver {
    private static final String TAG = "CrashHandler";

    String bootAction =  Intent.ACTION_BOOT_COMPLETED ;// android.intent.action.BOOT_COMPLETED

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w(TAG , "启动广播：" + intent.getAction() ) ;
        if (intent.getAction().equals(bootAction)) {
            Intent i = new Intent(context, ActivityAdvert.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
