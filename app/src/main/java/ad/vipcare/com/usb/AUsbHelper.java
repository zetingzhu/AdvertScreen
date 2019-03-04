package ad.vipcare.com.usb;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileInputStream;
import com.github.mjdev.libaums.fs.UsbFileOutputStream;
import com.github.mjdev.libaums.partition.Partition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

import static ad.vipcare.com.usb.AUSBBroadCastReceiver.ACTION_USB_COPY_FILE;
import static ad.vipcare.com.usb.AUSBBroadCastReceiver.ACTION_USB_PERMISSION;

/**
 * Created by zeting
 * Date 19/1/9.
 */

public class AUsbHelper {

    //上下文对象
    private Context context;
    //USB 设备列表
    private UsbMassStorageDevice[] storageDevices;
    //USB 广播
    private AUSBBroadCastReceiver mUsbReceiver;
    //回调
    private AUSBBroadCastReceiver.UsbListener usbListener;
    //当前路径
    private UsbFile currentFolder = null;
    //TAG
    private static String TAG = "UsbHelper";

    public AUsbHelper(Context context, AUSBBroadCastReceiver.UsbListener usbListener) {
        this.context = context;
        this.usbListener = usbListener;
    }



    /**
     * 注册 USB 监听广播
     */
    public void registerReceiver() {
        mUsbReceiver = new AUSBBroadCastReceiver();
        mUsbReceiver.setUsbListener(usbListener);
        //监听otg插入 拔出
        IntentFilter usbDeviceStateFilter = new IntentFilter();
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        context.registerReceiver(mUsbReceiver, usbDeviceStateFilter);
        //注册监听自定义广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(ACTION_USB_COPY_FILE);
        context.registerReceiver(mUsbReceiver, filter);

    }

    /**
     * 读取 USB设备列表
     *
     * @return USB设备列表
     */
    public UsbMassStorageDevice[] getDeviceList() {
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        //获取存储设备
        storageDevices = UsbMassStorageDevice.getMassStorageDevices(context);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
        //可能有几个 一般只有一个 因为大部分手机只有1个otg插口
        for (UsbMassStorageDevice device : storageDevices) {
//            Log.e(TAG, device.getUsbDevice().getDeviceName());
            //有就直接读取设备是否有权限
            if (!usbManager.hasPermission(device.getUsbDevice())) {
                //没有权限请求权限
                usbManager.requestPermission(device.getUsbDevice(), pendingIntent);
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
            //获取partition
            Partition partition = device.getPartitions().get(0);
            FileSystem currentFs = partition.getFileSystem();
            //获取根目录
            UsbFile root = currentFs.getRootDirectory();
            currentFolder = root;
            //将文件列表添加到ArrayList中
            Collections.addAll(usbFiles, root.listFiles());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usbFiles;
    }

    /**
     * 读取 USB 内文件夹下文件列表
     *
     * @param usbFolder usb文件夹
     * @return 文件列表
     */
    public ArrayList<UsbFile> getUsbFolderFileList(UsbFile usbFolder) {
        //更换当前目录
        currentFolder = usbFolder;
        ArrayList<UsbFile> usbFiles = new ArrayList<>();
        try {
            Collections.addAll(usbFiles, usbFolder.listFiles());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return usbFiles;
    }


    /**
     * 复制文件到 USB
     *
     * @param targetFile       需要复制的文件
     * @param saveFolder       复制的目标文件夹
     * @param progressListener 下载进度回调
     * @return 复制结果
     */
    public boolean saveSDFileToUsb(File targetFile, UsbFile saveFolder, DownloadProgressListener progressListener) {
        boolean result;
        try {
            //USB文件是否存在
            boolean isExist = false;
            UsbFile saveFile = null;
            for (UsbFile usbFile : saveFolder.listFiles()) {
                if (usbFile.getName().equals(targetFile.getName())) {
                    isExist = true;
                    saveFile = usbFile;
                }
            }
            if (isExist) {
                //文件已存在，删除文件
                saveFile.delete();
            }
            //创建新文件
            saveFile = saveFolder.createFile(targetFile.getName());
            //开始写入
            FileInputStream fis = new FileInputStream(targetFile);//读取选择的文件的
            int avi = fis.available();
            UsbFileOutputStream uos = new UsbFileOutputStream(saveFile);
            int bytesRead;
            byte[] buffer = new byte[1024 * 8];
            int writeCount = 0;
            while ((bytesRead = fis.read(buffer)) != -1) {
                uos.write(buffer, 0, bytesRead);
                writeCount += bytesRead;
//                Log.e(TAG, "Progress : " + (writeCount * 100 / avi));
                if (progressListener != null) {
                    //回调下载进度
                    progressListener.downloadProgress(writeCount * 100 / avi);
                }
            }
            uos.flush();
            fis.close();
            uos.close();
            result = true;
        } catch (final Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    /**
     * 复制 USB文件到本地
     *
     * @param targetFile       需要复制的文件
     * @param savePath         复制的目标文件路径
     * @param progressListener 下载进度回调
     * @return 复制结果
     */
    public boolean saveUSbFileToLocal(UsbFile targetFile, String savePath,
                                      DownloadProgressListener progressListener) {
        boolean result;
        try {
            //开始写入
            UsbFileInputStream uis = new UsbFileInputStream(targetFile);//读取选择的文件的
            FileOutputStream fos = new FileOutputStream(savePath);
            //这里uis.available一直为0
//            int avi = uis.available();
            long avi = targetFile.getLength();
            int writeCount = 0;
            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = uis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
                writeCount += bytesRead;
//                Log.e(TAG, "Progress : write : " + writeCount + " All : " + avi);
                if (progressListener != null) {
                    //回调下载进度
                    progressListener.downloadProgress((int) (writeCount * 100 / avi));
                }
            }
            fos.flush();
            uis.close();
            fos.close();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    /**
     * 复制文件到另外一个位置
     *
     * @param targetFile       需要复制的文件
     * @param savePath         复制的目标文件路径
     * @param progressListener 下载进度回调
     * @return 复制结果
     */
    public boolean saveCopyFilePaste(File targetFile, String savePath,  DownloadProgressListener progressListener) {
        boolean result;
        int downloadProgress = 0 ;
        try {
            //开始写入
            FileInputStream fis = new FileInputStream(targetFile);//读取选择的文件的
            File outFile  = new File(savePath) ;
            if (outFile.exists()){
                outFile.mkdirs();
            }
            // 写入输出流
            FileOutputStream fos = new FileOutputStream(savePath);
            //这里uis.available一直为0
            int avi = fis.available();
//            long avi = targetFile.getLength();
            int writeCount = 0;
            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
                writeCount += bytesRead;

                if (progressListener != null) {
                    //回调下载进度
                    int progress = getDivProgress(writeCount , avi , 2);
                    if ( progress > downloadProgress) {
                        downloadProgress =  progress;
                        progressListener.downloadProgress( downloadProgress );
                    }
                }
            }
            fos.flush();
            fis.close();
            fos.close();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }
    /**
     * 复制文件到另外一个位置
     *
     * @param targetFile       需要复制的文件
     * @param savePath         复制的目标文件路径
     * @param progressListener 下载进度回调
     * @return 复制结果
     */
    public boolean saveCopyFilePaste(UsbFile targetFile, String savePath,  DownloadProgressListener progressListener) {
        boolean result;
        int downloadProgress = 0 ;
        try {
            //开始写入
            UsbFileInputStream fis = new UsbFileInputStream(targetFile);//读取选择的文件的
            File outFile  = new File(savePath) ;
            if (outFile.exists()){
                outFile.mkdirs();
            }
            // 写入输出流
            FileOutputStream fos = new FileOutputStream(savePath);
            //这里uis.available一直为0
//            int avi = fis.available();
            long avi = targetFile.getLength();
            Log.i(TAG, "文件总长度：" + avi );
            int writeCount = 0;
            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
                writeCount += bytesRead;

                if (progressListener != null) {
                    //回调下载进度
                    int progress = getDivProgress(writeCount , avi , 2);
                    if ( progress > downloadProgress) {
                        downloadProgress =  progress;
                        progressListener.downloadProgress( downloadProgress );
                    }
                }
            }
            fos.flush();
            fis.close();
            fos.close();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }


    /**
     * 显示获取进度条进度
     * @param value1
     * @param value2
     * @param scale
     * @return
     * @throws IllegalAccessException
     */
    public int getDivProgress (double value1, double value2, int scale) throws IllegalAccessException {
        //如果精确范围小于0，抛出异常信息
        if (scale < 0) {
            throw new IllegalAccessException("精确度不能小于0");
        }
        if (value1 >= value2){
            return 100 ;
        } else {
            BigDecimal b1 = new BigDecimal(Double.toString(value1));
            BigDecimal b2 = new BigDecimal(Double.toString(value2));
            Double progress = b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).subtract(new BigDecimal(1)).doubleValue();
            if (progress <= 0) {
                progress = 0d;
            }
            return progress.intValue() ;
        }
    }


    /**
     * 获取上层目录文件夹
     *
     * @return usbFile : 父目录文件 / null ：无父目录
     */
    public UsbFile getParentFolder() {
        if (currentFolder != null && !currentFolder.isRoot()) {
            return currentFolder.getParent();
        } else {
            return null;
        }
    }


    /**
     * 获取当前 USBFolder
     */
    public UsbFile getCurrentFolder() {
        return currentFolder;
    }

    /**
     * 退出 UsbHelper
     */
    public void finishUsbHelper() {
        context.unregisterReceiver(mUsbReceiver);
    }

    /**
     * 下载进度回调
     */
    public interface DownloadProgressListener {
        void downloadProgress(int progress);
    }
}
