package ad.vipcare.com.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by allen on 18/5/14.
 * 将日志信息写入到文件
 */
public class LogToFile22 {

    private static String TAG = "LogToFile";

    private static String logPath = null;//log日志存放路径

    private static SimpleDateFormat dateFormatFile = new SimpleDateFormat("yyyy-MM-dd", Locale.US);//日期格式;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.US);//日期格式;

    private static Date date = new Date();//因为log日志是使用日期命名的，使用静态成员变量主要是为了在整个程序运行期间只存在一个.log文件中;

    private static Date clearDate ;// 记录上次删除日期

    /**
     * 初始化，须在使用之前设置，最好在Application创建时调用
     *
     * @param context
     */
    public static void init(Context context) {
        String basePath = getFilePath(context) ;
        if (basePath == null ){
            logPath = null ;
            return ;
        }
        logPath = basePath + "/Logs";//获得文件储存路径,在后面加"/Logs"建立子文件夹
    }

    /**
     * 获得文件存储路径
     *
     * @return
     */
    public static String getFilePath(Context context) {

        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {//如果外部储存可用
                return context.getExternalFilesDir(null).getPath();//获得外部存储路径,默认路径为 /storage/emulated/0/Android/data/com.waka.workspace.logtofile/files/Logs/log_2016-03-14_16-15-09.log
            } else {
                return context.getFilesDir().getPath();//直接存在/data/data里，非root手机是看不到的
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "获取设备地址错误：" + e );
            return null ;
        }
    }

    /**
     * 将log信息写入文件中
     *
     */
    public static boolean writeToFile( ) {
        if (null == logPath) {
            Log.e(TAG, "logPath == null ，未初始化LogToFile");
        }
        String fileName = logPath + "/log_" + dateFormatFile.format(date ) + ".log";//log日志名，使用时间命名，保证不重复

        //如果父路径不存在
        File file = new File(logPath);
        if (!file.exists()) {
            file.mkdirs();//创建父路径
        }
//        Log.e(TAG, "文件是否创建成功：" + file.exists());
        return file.exists() ;
    }


}

