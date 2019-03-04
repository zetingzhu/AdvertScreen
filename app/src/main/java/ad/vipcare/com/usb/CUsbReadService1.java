package ad.vipcare.com.usb;

import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import ad.vipcare.com.eventbus.DownloadEvent;
import ad.vipcare.com.eventbus.HideMenuEvent;
import ad.vipcare.com.util.FileUtils;

/**
 * 拷贝文件的服务
 * Created by zeting
 * Date 19/1/8.
 */

public class CUsbReadService1 extends Service implements AUSBBroadCastReceiver.UsbListener{

    private static final String TAG = "USB/Sv";

    private IBinder iBinder = new CUsbReadService1.MyIBinder();
    private AUsbHelper usbHelper;

    // 本地文件根目录
    private String pathRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
    // 拷贝文件
    private String copyFile = pathRoot + File.separator + "Download" + File.separator + "anqifile.zip" ;
    private String pasteFile = pathRoot + File.separator + "anqiad" + File.separator + "anqifile.zip" ;
    // 拷贝文件解压格式
    private String copyJson = pathRoot + File.separator + "Download" + File.separator + "anqijson.txt" ;
    private String pasteJson = pathRoot + File.separator + "anqiad" + File.separator + "anqijson.txt" ;
    // 解压目录
    private String unzipFileDir = pathRoot + File.separator + "anqiadunzip" ;

    // 下载进度条
    private DialogProgress pd ;
    @Override
    public void onCreate() {
        Log.i(TAG , "服务 - onCreate") ;
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG , "服务 - onBind") ;
        usbHelper = new AUsbHelper(CUsbReadService1.this, this);
        return iBinder ;
    }

    /**
     * USB 插入
     * @param device_add
     */
    @Override
    public void insertUsb(UsbDevice device_add) {

    }

    /**
     * USB 移除
     * @param device_remove
     */
    @Override
    public void removeUsb(UsbDevice device_remove) {

    }

    /**
     * 获取读取USB权限
     * @param usbDevice
     */
    @Override
    public void getReadUsbPermission(UsbDevice usbDevice) {

    }

    /**
     * 读取USB信息失败
     * @param usbDevice
     */
    @Override
    public void failedReadUsb(UsbDevice usbDevice) {

    }


    /**
     * 返回绑定服务
     */
    public class MyIBinder extends Binder {
        public CUsbReadService1 getService() {
            return CUsbReadService1.this;
        }
    }


    /**
     * 拷贝usb文件到本地
     */
    public void copyUsbFile() {
        Log.i(TAG, "开始复制文件");
        //复制到本地的文件路径
        final File targetFile = new File(copyFile);
        final File targetJson = new File(copyJson);
        final File saveFile = new File(pasteFile);
        final File saveJson = new File(pasteJson);

        Log.w(TAG , "拷贝后文件是否存在：" + FileUtils.isFileExists(saveFile) );
        Log.w(TAG , "删除创建拷贝后的文件：" + FileUtils.createFileByDeleteOldFile(saveFile) );

        Log.w(TAG , "拷贝后Json是否存在：" + FileUtils.isFileExists(saveJson) );
        Log.w(TAG , "删除创建拷贝后的Json：" + FileUtils.createFileByDeleteOldFile(saveJson) );

        Log.w(TAG , "创建解压目录：" + FileUtils.createOrExistsDir(unzipFileDir) );
//        Log.w(TAG , "删除目录：" + FileUtils.deleteDir(unzipFileDir) );

//        showProgress() ;
//        MyApplication.getInstance().showProgress();
        if (!targetFile.exists()){
            Log.e(TAG , "需要拷贝的文件不存在");
            return;
        }
        EventBus.getDefault().post(new DownloadEvent(1));

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean resultFile = copyFile( targetFile , pasteFile ) ;
                if ( resultFile ){
                    boolean resultJson = copyFile( targetJson , pasteJson ) ;
                    if (resultJson){
                        EventBus.getDefault().post(new DownloadEvent(3));
                    }else {
                        EventBus.getDefault().post(new DownloadEvent(4));
                    }
                }else {
                    EventBus.getDefault().post(new DownloadEvent(4));
                }

            }
        }).start();
    }


    public boolean copyFile(final File targetFile, String savePath){
        //复制结果
        boolean result = usbHelper.saveCopyFilePaste(targetFile, savePath, new AUsbHelper.DownloadProgressListener() {
            @Override
            public void downloadProgress( int progress ) {
                String text = "To copy : " + targetFile.getName()
                        + " Progress : " + progress;
                Log.i(TAG, text);
                EventBus.getDefault().post(new DownloadEvent(2 , progress));
            }
        });

        if (result) {
            Log.i(TAG, "复制 成功");
        } else {
            Log.i(TAG, "复制 失败");
        }
        return result ;
    }


    /**
     * 展示下载进取框
     */
    public void showProgress(){
        pd = new DialogProgress(this);
        pd.setDialogShow("");
        pd.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.i(TAG, "下载进度条结束掉");
                EventBus.getDefault().post( new HideMenuEvent( 1 ) );
            }
        });
    }



    /**
     * 设置进度
     * @param progress
     */
    public void setProgress(int progress){
        if (progress >= 100 ){
            pd.dismiss();
            // 解压文件
//            try {
//                ZipUtils.unzipFile(pasteFileDir , unzipFileDir);
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.d(TAG , "解压文件错误：" + e ) ;
//            }
        }else {
            pd.setProgress(progress);//设置进度条的当前进度
        }
    }



}
