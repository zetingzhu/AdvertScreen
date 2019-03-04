package ad.vipcare.com.advertscreen;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import ad.vipcare.com.util.FileUtils;

/**
 * Created by zeting
 * Date 19/1/14.
 */

public class Crashtest {

    private static final String TAG = "CrashHandler";
    //路径，文件名前缀，后缀
    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/CrashTest/log/";
    private static final String FILE_NAME_PERFIX = "crash";
    private static final String FILE_NAME_SUFFIX = ".txt";

    private Context mContext ;

    private void showToast(String str  ){
        Toast.makeText(mContext, str , Toast.LENGTH_SHORT ).show() ;
    }

    /**
     * 将异常信息存到SD卡中
     * @param ex
     * @throws IOException
     */
    public void dumpExceptionToSDCard( Context con , Throwable ex) throws IOException {

        this.mContext = con ;
        //如果SD卡不存在或无法使用，则无法把异常信息写入SD卡
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            showToast("无法使用sdcard" ) ;
        }

        showToast("错误信息：" + ex.toString() ) ;
        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        Log.w(TAG , "文件夹是创建成功：" + FileUtils.createOrExistsDir(PATH) );

        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date(current));
        File file = new File(PATH + FILE_NAME_PERFIX + time + FILE_NAME_SUFFIX);

        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            pw.println(time);
            dumpPhoneInfo(pw);
            pw.println();
            ex.printStackTrace(pw);
            pw.close();
        } catch (Exception e) {
            Log.e(TAG, "dump crash info failed");
            showToast("写文件错误：" + e.toString() ) ;
        }
    }

    /**
     * 记录手机对应的 信息
     * @param pw
     * @throws PackageManager.NameNotFoundException
     */
    private void dumpPhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        pw.print("App Version: ");
        pw.print(pi.versionName);
        pw.print('_');
        pw.println(pi.versionCode);

        //android版本号
        pw.print("OS Version: ");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.println(Build.VERSION.SDK_INT);

        //手机制造商
        pw.print("Vendor: ");
        pw.println(Build.MANUFACTURER);

        //手机型号
        pw.print("Model: ");
        pw.println(Build.MODEL);

        //cpu架构
        pw.print("CPU ABI: ");
        pw.println(Build.CPU_ABI);
    }
}
