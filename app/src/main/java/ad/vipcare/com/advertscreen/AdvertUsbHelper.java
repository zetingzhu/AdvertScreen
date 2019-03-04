package ad.vipcare.com.advertscreen;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.partition.Partition;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ad.vipcare.com.usb.AUSBBroadCastReceiver;
import ad.vipcare.com.usb.AUsbTestActivity;
import ad.vipcare.com.usb.CUsbReadService;
import ad.vipcare.com.util.LogToFile;

import static ad.vipcare.com.usb.AUSBBroadCastReceiver.ACTION_USB_PERMISSION;

/**
 * 广告usb操作相关的类
 * Created by zeting
 * Date 19/1/15.
 */

public class AdvertUsbHelper extends AdvertSerial {


    private static final String TAG = "AdvertPersenter/usb";

    private Context mContext ;
    //USB 设备列表
    private UsbMassStorageDevice[] storageDevices;
    // 是否有访问的USB权限
    private boolean hasPermission = false ;
    //当前路径
    private UsbFile currentFolder = null;
    // 拷贝文件服务
    private ServiceConnection mServiceConnection ;
    private CUsbReadService usbReadService ;
    // 拷贝文件的usbFile
    private UsbFile readUsbFileZip ;
    private UsbFile readUsbFileJson ;

    public AdvertUsbHelper(Context con) {
        super(con);
       this.mContext = con;
    }

    /**
     * 请求窗口权限
     */
    public void requestPermission (){
        if (Build.VERSION.SDK_INT >= 23) {
            if (! Settings.canDrawOverlays(mContext)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + mContext.getPackageName()));
                ((Activity)mContext).startActivityForResult(intent,10);
            }
        }
    }

    /**
     * 读取 USB设备列表
     *
     * @return USB设备列表
     */
    public UsbMassStorageDevice[] getDeviceList() {
        UsbManager usbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        //获取存储设备
        storageDevices = UsbMassStorageDevice.getMassStorageDevices(mContext);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
        //可能有几个 一般只有一个 因为大部分手机只有1个otg插口
        for (UsbMassStorageDevice device : storageDevices) {
//            Log.e(TAG, device.getUsbDevice().getDeviceName());
            //有就直接读取设备是否有权限
            if (!usbManager.hasPermission(device.getUsbDevice())) {
                writeLog("U盘没有权限请求权限" );
                //没有权限请求权限
                usbManager.requestPermission(device.getUsbDevice(), pendingIntent);
                hasPermission = false ;
            } else {
                writeLog("U盘 有权限" );
                hasPermission = true ;
            }
        }
        return storageDevices;
    }

    /**
     * 获取device 根目录文件
     *
     * @param device USB 存储设备
     * @return 设备根目录下文件列表
     */
    public ArrayList<UsbFile> readDevice(UsbMassStorageDevice device) {
        ArrayList<UsbFile> usbFiles = new ArrayList<>();
        try {
            //初始化
            device.init();
            // 获取分区
            List<Partition> mListPar =  device.getPartitions() ;
            writeLog("U盘分区信息：" + mListPar.size() + "-如果值为0 请格式化U盘格式为 FAT32" );
            //获取partition
            Partition partition = mListPar.get(0);
            // 仅使用第一分区
            FileSystem currentFs = partition.getFileSystem();
            //获取根目录
            UsbFile root = currentFs.getRootDirectory();
//            showToast("usb 根目录信息：" + root.toString() );
            writeError(mContext , "usb 根目录信息：" + root.toString() + " - " + root.getName() );
            currentFolder = root;
            //将文件列表添加到ArrayList中
            Collections.addAll(usbFiles, root.listFiles());
        } catch (Exception e) {
            e.printStackTrace();
            writeLog("处理U盘信息错误：" + e.toString() );
            showToast("获取U盘信息错误，请重新插拔U盘" , Toast.LENGTH_LONG );
        }
        return usbFiles;
    }


    /**
     * 有权限的时候后去usb目录
     * @param position
     */
    public void updateUsbFile(int position) {
        UsbMassStorageDevice[] usbMassStorageDevices = getDeviceList();
        if (usbMassStorageDevices.length > 0) {
            if (hasPermission) {
                writeLog("USB有权限");
                try {
                    // 延时加载一下，不然有可能导致u盘初始化失败
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                List<UsbFile> mList = readDevice(usbMassStorageDevices[position]);
                writeLog("U盘目录文件有几个：" + mList.size());
                if (mList != null && mList.size() >0) {
                    //存在USB
//                    showToast("usb 所有目录：" + mList.get(0).getName() + "- 有几个U盘信息：" + usbMassStorageDevices.length);
                    writeError(mContext, "usb 所有目录：" + mList.toString() + "- 有几个U盘信息：" + usbMassStorageDevices.length);
                }
                readUsbFileZip = null ;
                readUsbFileJson = null ;
                for (UsbFile file : mList) {
                    writeLog("U盘文件目录: " + file.getName());
                    if (file.getName().contains("anqi")){
                        writeLog("在U盘中找到文件，拷贝文件 ：" + file.getParent().getName() );
                        try {
                            UsbFile[] usbFileAnqi = file.listFiles();

                            for (int i = 0; i < usbFileAnqi.length; i++) {
                                writeLog(" Anqi 文件目录: " + usbFileAnqi[i].getName());
                                if (usbFileAnqi[i].getName().equals("anqifile.zip")){
                                    readUsbFileZip = usbFileAnqi[i] ;
                                }else if (usbFileAnqi[i].getName().equals("anqijson.txt")){
                                    readUsbFileJson  = usbFileAnqi[i] ;
                                }
                            }
//                            sendBroadcastSdcard();
                            if (readUsbFileZip != null && readUsbFileJson != null ) {
                                bindUsbService(mContext, readUsbFileZip, readUsbFileJson);
                            }else {
                                showToast("文件不全，拷贝文件失败" );
                                writeLog(" 文件不全，这里就不能拷贝");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return ;
                    }
                }
            } else {
//                showToast("没有USB请求权限，没法读取U盘目录" );
                writeLog("没有USB请求权限，没法读取U盘目录");
            }
        } else {
//            showToast("没有读取到usb 目录信息");
            writeError(mContext , "没有读取到usb 目录信息" );
        }
    }

    /**
     * 绑定拷贝文件的服务
     * @param mCon
     */
    public void bindUsbService(Context mCon , final UsbFile zipFils , final UsbFile jsonFile ){
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG , "绑定服务成功");
                usbReadService = ((CUsbReadService.MyIBinder) service).getService();

                /** 调用拷贝文件 原来的老的直接U盘给数据的
                 使用新的不需要u盘给数据的
                 */
                usbReadService.copyUsbFile( zipFils , jsonFile );
//                usbReadService.copyUsbFile( zipFils );
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG , "绑定服务失败");
            }
        };
        Intent gattServiceIntent = new Intent(mCon , CUsbReadService.class);
        mCon.bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 发送广播拷贝文件
     */
//    public void sendBroadcastSdcard(){
//        Intent intentReceiver = new Intent() ;
//        intentReceiver.setAction( AUSBBroadCastReceiver.ACTION_USB_COPY_FILE );
//        mContext.sendBroadcast(intentReceiver);
//    }

    /**
     * 读取文件txt内容
     * @param strFilePath
     * @return
     */
    public String ReadTxtFile(String strFilePath)
    {
        String path = strFilePath;
//        List<String> newList=new ArrayList<String>();
        //打开文件
        File file = new File(path);
        StringBuffer sb = new StringBuffer("");
        //如果path是传递过来的参数，可以做一个非目录的判断

        if (file.isDirectory())  {
            Log.d("TestFile", "The File doesn't not exist.");
        }
        else {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);

                    String line;
                    //分行读取
                    while (( line = buffreader.readLine()) != null) {
//                        newList.add(line+"\n");
                        sb.append( line );
                    }
                    instream.close();
                }
            }
            catch (java.io.FileNotFoundException e) {
                e.printStackTrace();
                Log.d("TestFile", "The File doesn't not exist.");
            }
            catch (IOException e) {
                e.printStackTrace();
                Log.d("TestFile", e.getMessage());
            }
        }

        return sb.toString() ;
    }


    private String loadFromSDFile(String strFilePath) {
        String result=null;
        try {
            //打开文件
            File file = new File(strFilePath);
            int length=(int)file.length();
            byte[] buff=new byte[length];
            FileInputStream fin=new FileInputStream(file);
            fin.read(buff);
            fin.close();
            result=new String(buff,"UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }



    /**
     * 写入日志信息到文件中
     */
    public void writeError(Context context , String str){
//        try {
//            Crashtest ct = new Crashtest();
//            ct.dumpExceptionToSDCard(context , new Exception(str));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        writeLog(str);
    }





}
