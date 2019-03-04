package ad.vipcare.com.usb;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.util.Log;

import com.github.mjdev.libaums.fs.UsbFile;

import java.io.IOException;

import ad.vipcare.com.advertscreen.Crashtest;
import ad.vipcare.com.util.LogToFile;

/**
 * Created by zeting
 * Date 19/1/9.
 */

public class AUSBBroadCastReceiver extends BroadcastReceiver {


    private static final String TAG = "USB/BR";

    private UsbListener usbListener;

    public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    // 拷贝文件广播
    public static final String ACTION_USB_COPY_FILE = "ad.vipcare.com.usb.USBBroadCastReceiver.copyFile";

    private ServiceConnection mServiceConnection ;
    CUsbReadService usbReadService ;

    private UsbFile readUsbFile ;


    public void  writeError( Context context , String str ) {
//        try {
//            Crashtest ct = new Crashtest();
//            ct.dumpExceptionToSDCard(context, new Exception( str));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        LogToFile.writeLog(str);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        writeError(context , "接收到的广播：" + action);

        switch (action) {
            case ACTION_USB_PERMISSION:
                //接受到自定义广播
                UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                //允许权限申请
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    if (usbDevice != null) {
                        //回调
                        if (usbListener != null) {
                            usbListener.getReadUsbPermission(usbDevice);
                        }
                    } else {
                        writeError(context , "没有插入U盘");
                    }
                } else {
                    if (usbListener != null) {
                        usbListener.failedReadUsb(usbDevice);
                    }
                }
                break;
            case UsbManager.ACTION_USB_DEVICE_ATTACHED://接收到存储设备插入广播
                writeError(context , "处理广播");
                UsbDevice device_add = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device_add != null) {
                    writeError(context , "UsbDevice 不为空");
                    if (usbListener != null) {
                        writeError(context , "usbListener 不为空");
                        usbListener.insertUsb(device_add);
                    } else {
                        writeError(context , "usbListener 为空");
                    }
                }else {
                    writeError(context , "UsbDevice 为空");
                }
                break;
            case UsbManager.ACTION_USB_DEVICE_DETACHED:
                //接收到存储设备拔出广播
                UsbDevice device_remove = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device_remove != null) {
                    if (usbListener != null) {
                        usbListener.removeUsb(device_remove);
                    }
                }
                break;
            case ACTION_USB_COPY_FILE :
                // 收到这个广播时候讲发送给服务中去，开始拷贝文件
                Log.d(TAG , "接收到了广播");
//                bindUsbService(context) ;
                break;
        }
    }



    public void setUsbListener(UsbListener usbListener) {
        this.usbListener = usbListener;
    }

    /**
     * USB 操作监听
     */
    public interface UsbListener {
        /**USB 插入
         *
         * @param device_add
         */
        void insertUsb(UsbDevice device_add);

        /**USB 移除
         *
         * @param device_remove
         */
        void removeUsb(UsbDevice device_remove);

        /**获取读取USB权限
         *
         * @param usbDevice
         */
        void getReadUsbPermission(UsbDevice usbDevice);

        /**读取USB信息失败
         *
         * @param usbDevice
         */
        void failedReadUsb(UsbDevice usbDevice);
    }
}