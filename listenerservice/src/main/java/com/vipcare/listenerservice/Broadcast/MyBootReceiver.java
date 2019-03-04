package com.vipcare.listenerservice.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.vipcare.listenerservice.MainActivity;

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
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
