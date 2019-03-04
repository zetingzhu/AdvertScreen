package ad.vipcare.com.timeUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;

import ad.vipcare.com.eventbus.HideMenuEvent;

/**
 * 监听事件的广播
 * Created by zeting
 * Date 19/1/10.
 */

public class DataChangeReceiver extends BroadcastReceiver {

    private static final String TAG = "TIME/BR";
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "时间发生变化。。。 " + intent.getAction() );
        try {
            // 如果时间到了凌晨1点就通知更新
            Calendar calendar= Calendar.getInstance();  //获取当前时间，作为图标的名字
            int hour=calendar.get(Calendar.HOUR_OF_DAY) ;
            int minute=calendar.get(Calendar.MINUTE) ;
            int second=calendar.get(Calendar.SECOND) ;
            if (hour == 4 && minute == 0 && second == 0 ) {
                // 如果当前时间为凌晨q点就更新应用
                EventBus.getDefault().post(new HideMenuEvent(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
