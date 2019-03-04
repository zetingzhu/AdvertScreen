package ad.vipcare.com.download;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ad.vipcare.com.util.LogPlus;
import ad.vipcare.com.util.MatchUtil;
import ad.vipcare.com.util.RootUtil;

/**
 * Created by zeting
 * Date 19/1/23.
 */

public class DownloadServise extends Service {
    private static final String TAG = DownloadServise.class.getSimpleName();

    public static final int HANDLE_DOWNLOAD = 0x001;
    public static final String BUNDLE_KEY_DOWNLOAD_URL = "download_url";
    public static final float UNBIND_SERVICE = 2.0F;

    public static final String APKFILENAME = "downloadApk.apk" ;

    private Activity activity;
    private DownloadBinder binder;
    private DownloadManager downloadManager;
    private DownloadChangeObserver downloadObserver;
    private BroadcastReceiver downLoadBroadcast;
    private ScheduledExecutorService scheduledExecutorService;

    //下载任务ID
    private long downloadId;
    private String downloadUrl;
    public static OnProgressListener onProgressListener;

    @SuppressLint("HandlerLeak")
    public Handler downLoadHandler = new Handler() { //主线程的handler
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (onProgressListener != null && HANDLE_DOWNLOAD == msg.what) {
                //被除数可以为0，除数必须大于0
                if (msg.arg1 >= 0 && msg.arg2 > 0) {
                    try {
                        int pro = MatchUtil.getProgress(msg.arg1 , msg.arg2 , 2) ;
                        Log.w(TAG , "下载状态：" +  msg.obj + " - 下载进度：" +  pro ) ;
                        onProgressListener.onProgress(pro);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    private Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                updateProgress();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        binder = new DownloadBinder();
    }

    @Override
    public IBinder onBind(Intent intent) {
        downloadUrl = intent.getStringExtra(BUNDLE_KEY_DOWNLOAD_URL);
        LogPlus.sd(TAG , "绑定服务：" + downloadUrl );
        downloadApk(downloadUrl);
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.w(TAG , "结束下载服务" ) ;
        return super.onUnbind(intent);
    }

    /**
     * 下载最新APK
     */
    private void downloadApk(String url) {
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadObserver = new DownloadChangeObserver();

        registerContentObserver();

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "my.apk");
        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, APKFILENAME );
        request.setTitle("APK文件下载");
        //request.setDescription("下载中通知栏提示");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        //request.setMimeType("application/cn.trinea.download.file"); //用于响应点击的打开文件
        request.setVisibleInDownloadsUi(true);  //显示下载界面
        request.allowScanningByMediaScanner();  //准许被系统扫描到
        downloadId = downloadManager.enqueue(request);
        registerBroadcast(); //下载成功和点击通知栏动作监听
    }

    /**
     * 注册广播
     */
    private void registerBroadcast() {
        /**注册service 广播 1.任务完成时 2.进行中的任务被点击*/
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        registerReceiver(downLoadBroadcast = new DownLoadBroadcast(), intentFilter);
    }

    /**
     * 注销广播
     */
    private void unregisterBroadcast() {
        if (downLoadBroadcast != null) {
            unregisterReceiver(downLoadBroadcast);
            downLoadBroadcast = null;
        }
    }

    /**
     * 注册ContentObserver
     */
    private void registerContentObserver() {
        /** observer download change **/
        if (downloadObserver != null) {
            getContentResolver().registerContentObserver(
                    Uri.parse("content://downloads/my_downloads"), false, downloadObserver);
        }
    }

    /**
     * 注销ContentObserver
     */
    private void unregisterContentObserver() {
        if (downloadObserver != null) {
            getContentResolver().unregisterContentObserver(downloadObserver);
        }
    }

    /**
     * 发送Handler消息更新进度和状态
     * 将查询结果从子线程中发往主线程（handler方式），以防止ANR
     */
    private void updateProgress() {
        int[] bytesAndStatus = getBytesAndStatus(downloadId);
        LogPlus.sd(TAG , "读取下载文件状态 下载大小：" + bytesAndStatus[0] + " - 总大小：" + bytesAndStatus[1] + " - 状态：" + bytesAndStatus[2] );
        downLoadHandler.sendMessage(downLoadHandler.obtainMessage(HANDLE_DOWNLOAD, bytesAndStatus[0], bytesAndStatus[1], bytesAndStatus[2]));
    }

    /**
     * 通过query查询下载状态，包括已下载数据大小，总大小，下载状态
     *
     * @param downloadId
     * @return
     */
    private int[] getBytesAndStatus(long downloadId) {
        int[] bytesAndStatus = new int[]{
                -1, -1, 0
        };
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor cursor = null;
        try {
            cursor = downloadManager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                //已经下载文件大小
                bytesAndStatus[0] = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                //下载文件的总大小
                bytesAndStatus[1] = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                //下载状态
                bytesAndStatus[2] = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return bytesAndStatus;
    }

    /**
     * 绑定此DownloadService的Activity实例
     *
     * @param activity
     */
    public void setTargetActivity(Activity activity) {
        this.activity = activity;
    }

    /**
     * 接受下载完成广播
     */
    private class DownLoadBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            LogPlus.sd(TAG , "发送下载广播 DownLoadBroadcast：" +  intent.getAction() + " - 下载文件id:" + downId ) ;
            switch (intent.getAction()) {
                case DownloadManager.ACTION_DOWNLOAD_COMPLETE:
                    if (downloadId == downId && downId != -1 && downloadManager != null) {
                        Uri downIdUri = downloadManager.getUriForDownloadedFile(downloadId);

                        close();

                        if (downIdUri != null) {
//                            SPTools.put(Constant.SP_DOWNLOAD_PATH, downIdUri.getPath());
                            LogPlus.sd(TAG, "广播监听下载完成，APK存储路径为 ：" + downIdUri.getPath());
                            ApkInstallUtil apkIns =  new ApkInstallUtil() ;
                            apkIns.installApk(context);
                        } else {
                            onProgressListener.dismissProgress(true);
                            LogPlus.sd(TAG, "下载应用错误" );
                        }
                        if (onProgressListener != null) {
                            onProgressListener.onProgress(UNBIND_SERVICE);
                        }
                    }
                    break;

                case DownloadManager.ACTION_NOTIFICATION_CLICKED:
                    LogPlus.sd(TAG , "我被点击啦！");
                    break;
            }
        }
    }

    /**
     * 监听下载进度
     */
    private class DownloadChangeObserver extends ContentObserver {

        public DownloadChangeObserver() {
            super(downLoadHandler);
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        }

        /**
         * 当所监听的Uri发生改变时，就会回调此方法
         * @param selfChange 此值意义不大, 一般情况下该回调值false
         */
        @Override
        public void onChange(boolean selfChange) {
            LogPlus.sd(TAG , "onChange 开始读取");
            scheduledExecutorService.scheduleAtFixedRate(progressRunnable, 0, 100 , TimeUnit.MILLISECONDS); //在子线程中查询
        }
    }


    /**
     * 关闭定时器，线程等操作
     */
    private void close() {
        LogPlus.sd(TAG , "结束读取");
        if (scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.shutdown();
            scheduledExecutorService = null ;
        }

        if (downLoadHandler != null) {
            downLoadHandler.removeCallbacksAndMessages(null);
        }
    }


    public class DownloadBinder extends Binder {
        /**
         * 返回当前服务的实例
         * @return
         */
        public DownloadServise getService() {
            return DownloadServise.this;
        }

    }

    public interface OnProgressListener {
        /**
         * 下载进度
         * @param fraction 已下载/总大小
         */
        void onProgress(float fraction);

        /**
         * 取消下载进度框
         * @param dismiss
         */
        void dismissProgress(boolean dismiss);
    }

    /**
     * 对外开发的方法
     * @param onProgressListener
     */
    public void setOnProgressListener(OnProgressListener onProgressListener) {
        DownloadServise.onProgressListener = onProgressListener;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
        unregisterContentObserver();
    }

}
