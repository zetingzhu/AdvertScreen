package ad.vipcare.com.advertscreen;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import ad.vipcare.com.download.DownloadFileListener;
import ad.vipcare.com.download.DownloadManagerResolver;
import ad.vipcare.com.download.DownloadRetrofitRxjava;
import ad.vipcare.com.download.DownloadServise;
import ad.vipcare.com.download.MyAccessibilityService;
import ad.vipcare.com.eventbus.DownloadEvent;
import ad.vipcare.com.eventbus.HideMenuEvent;
import ad.vipcare.com.util.LogPlus;
import ad.vipcare.com.util.MatchUtil;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * 文件下载操作类
 * Created by zeting
 * Date 19/1/23.
 */

public abstract class AdvertDownload extends AdvertBasePersenter {
    private static final String TAG = "AdvertPersenter/ADL" ;
//    private String url = "https://ali-fir-pro-binary.fir.im/3784f76dac9ed340b4e8f7bd0c54bf67bd5d5dd2.apk?auth_key=1548247228-0-0-9d8db761b510507d824100e15c3676d4" ;
    private String downloadUrl = "" ;
    // 下载的文件名称
    public String apkFileName = "downloadApk.apk" ;
    // 下载文件工具类
    private DownloadRetrofitRxjava dr ;

    public AdvertDownload(Context context) {
        super(context);
    }

    /**
     *  获取下载文件地址
     */
    public interface DownloadUrlListener {
        void getUrl(String url) ;
    }


    // 下载文件
    public void initDownload(){
        requestAdvertDownload(new DownloadUrlListener() {

            @Override
            public void getUrl(String url) {
                LogPlus.sd("获取到的下载文件地址：" + url);
                downloadUrl = url;
                if (!"".equals(downloadUrl)) {
                    LogPlus.sd("检查下载管理器是否被禁用" );
                    boolean isDownload = DownloadManagerResolver.resolve(mContext);
                    LogPlus.sd("检查下载管理器是否被禁用：" + isDownload);
                    if ( isDownload ) {
                        downLoadCs() ;
                    } else {
                        downLoadHttp() ;
                    }
                }
            }
        });
//        downLoadCs();
    }

    private ProgressDialog progress;
    private boolean isBindService;
    private ServiceConnection conn = new ServiceConnection() { //通过ServiceConnection间接可以拿到某项服务对象

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DownloadServise.DownloadBinder binder = (DownloadServise.DownloadBinder) service;
            final DownloadServise downloadServise = binder.getService();
            LogPlus.sd("下载服务启动成功");
            //接口回调，下载进度
            downloadServise.setOnProgressListener(new DownloadServise.OnProgressListener() {
                @Override
                public void onProgress(float fraction) {
//                    Log.i(TAG , "更新进度条：" + fraction);
                    progress.setProgress((int) fraction);

                    //判断是否真的下载完成进行安装了，以及是否注册绑定过服务
                    if (fraction == downloadServise.UNBIND_SERVICE && isBindService) {
                        LogPlus.sd( "是不是要结束服务：" + fraction);
                        progress.setProgress(100);
                        progress.dismiss();
                        mContext.unbindService(conn);
                        isBindService = false;
                        // 通知主界面全屏
                        EventBus.getDefault().post(new HideMenuEvent(1));
                    }
                }

                @Override
                public void dismissProgress(boolean dismiss) {

                    progress.dismiss();
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogPlus.sd("下载服务启动失败");
        }
    };

    /**
     * 初始化下载进度条
     */
    private void initProgressBar() {
        progress = new ProgressDialog(mContext);
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setProgress(1);
        progress.setCancelable(true);
        progress.setCanceledOnTouchOutside(true);
        progress.show();
        Log.i(TAG , "显示下载对话框");
    }

    /**
     *  通过系统下载管理器开始下载文件
     */
    private void downLoadCs() {

        try {
            LogPlus.sd(TAG ,  "downLoadCs 开始下载文件 " );
            initProgressBar();
            LogPlus.sd(TAG ,  "downLoadCs 删除旧文件" );
            removeOldApk();
            LogPlus.sd(TAG ,  "downLoadCs 启动下载服务 downloadUrl：" + downloadUrl );
            Intent intent = new Intent(mContext , DownloadServise.class);
            intent.putExtra(DownloadServise.BUNDLE_KEY_DOWNLOAD_URL, downloadUrl );
            isBindService = mContext.bindService(intent, conn, BIND_AUTO_CREATE); //绑定服务即开始下载 调用onBind()
        } catch (Exception e) {
            e.printStackTrace();
            LogPlus.sd("downLoadCs 启动服务报错  " , e );
        }
    }

    /**
     * 通过http下载文件
     */
    private void downLoadHttp() {
        dr = new DownloadRetrofitRxjava(mContext);
        String savaFile =  mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath() ;
        LogPlus.d(TAG , "保存apk 文件路径：" + savaFile );
        dr.downLoadApkFils(downloadUrl, savaFile);

    }

    /**
     * 删除上次更新存储在本地的apk
     */
    private void removeOldApk() {
        //获取老ＡＰＫ的存储路径
//        File fileName = new File(SPTools.getString(Constant.SP_DOWNLOAD_PATH, ""));
        File fileName = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS + "/" + apkFileName );

        if (fileName != null && fileName.exists() && (fileName.isFile() || fileName.isDirectory() )) {
            fileName.delete();
        }
    }


    /**
     * 校验检测辅助服务是否已经打开
     */
    public boolean isAccessibilitySettingsOn() {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + MyAccessibilityService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.v(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }

        return false;
    }


    /**
     *  进入设置打开应用智能安装服务
     */
    public void onForwardToAccessibility() {
        // 打开智能安装
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS) ;
        mContext .startActivity(intent) ;
    }

    /**
     * 校验并且打开智能服务
     */
    public void openAccessibility(){
        boolean booSet =  isAccessibilitySettingsOn();
        Log.w(TAG , "------辅助功能是否已经打开${booSet}-----");
        if (!booSet){
            // 打开智能安装
            onForwardToAccessibility() ;
        }
    }

}
