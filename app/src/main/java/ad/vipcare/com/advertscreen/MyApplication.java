package ad.vipcare.com.advertscreen;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import ad.vipcare.com.Crash.CrashHandler;
import ad.vipcare.com.usb.DialogProgress;
import ad.vipcare.com.util.LogPlus;
import ad.vipcare.com.util.LogToFile;
import interfaces.heweather.com.interfacesmodule.view.HeConfig;

/**
 *
 * Created by zeting
 * Date 19/1/9.
 */
public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    // 下载进度条
    private DialogProgress pd ;

    private static MyApplication mInstance;

    private Long downloadId = 0L ; // 下载任务的ID号

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        context = getApplicationContext();
        //为应用设置异常处理器
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(context);

        // 初始化日志写入文件
        LogToFile.init(this);

        // 执行天气查询初始化
        HeConfig.init("HE1702141801231165", "223db782f720499c9380506f35b1ecd9");

        // 初始化日志打印文件
        LogPlus.init("AD" , Log.VERBOSE , false );

    }
}
