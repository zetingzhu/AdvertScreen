package ad.vipcare.com.timeUtil;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

/**
 * 监听时间服务
 * Created by zeting
 * Date 19/1/10.
 */

public class TimeService extends Service {

    private static final String TAG = "TIME/SV";
    DataChangeReceiver receiver ;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        Log.i(TAG, "后台进程被创建 ");

            //服务启动广播接收器，使得广播接收器能够在程序退出后在后天继续运行。接收系统时间变更广播事件

        receiver = new DataChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);// 每分钟变化
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED); // 设置系统时间
        intentFilter.addAction(Intent.ACTION_DATE_CHANGED);// 设置日期变化
        registerReceiver(receiver, intentFilter );

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "后台进程。。 ");
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {

        Log.i(TAG, "后台进程被销毁了。。。 ");

        super.onDestroy();

        if (receiver != null ) {
            unregisterReceiver(receiver);
        }
    }

}