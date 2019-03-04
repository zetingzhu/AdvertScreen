package ad.vipcare.com.serialport;

import android.os.HandlerThread;
import android.serialport.SerialPort;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ad.vipcare.com.util.ByteUtil;
import ad.vipcare.com.util.LogPlus;
import ad.vipcare.com.util.LogToFile;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * 串口操作工具类
 * Created by Administrator on 2017/3/28 0028.
 */
public class SerialPortManager {

    private static final String TAG = "SerialPortManager";

    private SerialReadThread mReadThread;
    private OutputStream mOutputStream;
    private HandlerThread mWriteThread;
    private Scheduler mSendScheduler;

    private static class InstanceHolder {

        public static SerialPortManager sManager = new SerialPortManager();
    }

    public static SerialPortManager instance() {
        return InstanceHolder.sManager;
    }

    private SerialPort mSerialPort;

    private SerialPortManager() {
    }

    /**
     * 打开串口
     *
     * @param device
     * @return
     */
    public SerialPort open(Device device , SerialPortListener mSpl) {
        return open(device.getPath(), device.getBaudrate() , mSpl);
    }

    /**
     * 打开串口
     *
     * @param devicePath
     * @param baudrateString
     * @return
     */
    public SerialPort open(String devicePath, String baudrateString , SerialPortListener mSpl) {

        sendCmdCb300();

        if (mSerialPort != null) {
            close();
        }

        try {
            File device = new File(devicePath);
            // zzz 打开串口
//            File device = new File("/dev/rfcomm");
//            File device = new File("/dev/ttyGS");
//            File device = new File("/dev/ttyUSB");
//            File device = new File("/dev/ttyS");
//            File device = new File("/dev/ttyGS3");
//            File device = new File("/dev/ttyGS2");
//            File device = new File("/dev/ttyGS1");
//            File device = new File("/dev/ttyGS0");
//            File device = new File("/dev/ttyS3");
//            File device = new File("/dev/ttyS2");
//            File device = new File("/dev/ttyS1");
//            File device = new File("/dev/ttyS0");

            int baurate = Integer.parseInt(baudrateString);
            mSerialPort = new SerialPort(device, baurate, 0);

            mReadThread = new SerialReadThread(mSerialPort.getInputStream());
            mReadThread.setSerialPortListener(mSpl);
            mReadThread.start();

            mOutputStream = mSerialPort.getOutputStream();

            mWriteThread = new HandlerThread("write-thread");
            mWriteThread.start();
            mSendScheduler = AndroidSchedulers.from(mWriteThread.getLooper());

            return mSerialPort;
        } catch (Throwable tr) {
            LogPlus.e(TAG, "打开串口失败", tr);
            close();
            return null;
        }
    }

    /**
     * 关闭串口
     */
    public void close() {
        if (mReadThread != null) {
            mReadThread.close();
        }
        if (mOutputStream != null) {
            try {
                mOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (mWriteThread != null) {
            mWriteThread.quit();
        }

        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }

    /**
     * 发送数据
     *
     * @param datas
     * @return
     */
    private void sendData(byte[] datas) throws Exception {
        // 初始化一下，这样就开始来接收这一次的数据了
        mReadThread.getData(false , null , 0);
        mReadThread.getData(true , null , 0);
        mOutputStream.write(datas);
    }

    /**
     * (rx包裹)发送数据
     *
     * @param datas
     * @return
     */
    private Observable<Object> rxSendData(final byte[] datas) {

        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                try {
                    sendData(datas);
                    emitter.onNext(new Object());
                } catch (Exception e) {

                    LogPlus.e("发送：" + ByteUtil.bytes2HexStr(datas) + " 失败", e);

                    if (!emitter.isDisposed()) {
                        emitter.onError(e);
                        return;
                    }
                }
                emitter.onComplete();
            }
        });
    }


    /**
     *  cb300 读取
     * @return
     */
    public byte[] sendCmdCb300 () {
        // 从12开始读，读三位
        byte[] byteQueryCb = new byte[]{0x01, 0x03 , 0x00 , 0x0c ,  0x00 , 0x03 , (byte) 0xc5, (byte) 0xc8} ;
        return byteQueryCb ;
    }



    /**
     * 发送 FC3 块读取 各个串口状态
     */
    public void sendCommand() {
        LogPlus.i("发送命令：" );
        byte[] bytes = sendCmdCb300() ;
        Log.d(TAG, "sendSerialPort: 发送数据, len:" + bytes.length  + "  " + ByteUtil.bytes2HexStr(bytes));

        LogToFile.writeLog("sendSerialPort: 发送数据, len:" + bytes.length  + "  " + ByteUtil.bytes2HexStr(bytes) );

        rxSendData(bytes).subscribeOn(mSendScheduler).subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object o) {
//                LogManager.instance().post(new SendMessage("发送命令"));
                LogPlus.e("发送命令" );
            }

            @Override
            public void onError(Throwable e) {
                LogPlus.e("发送失败", e);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     *  读取串口获取设备id
     */
    public void ReadDeviceIdsendCommand() {
        LogPlus.i("发送设备id命令：" );
        // 从1开始读
//        byte[] bytes = new byte[]{0x01, 0x03 , 0x00 , 0x01 ,  0x00 , 0x0e , (byte) 0x95, (byte) 0xce} ;
        byte[] bytes = new byte[]{0x01, 0x03 , 0x00 , 0x01 ,  0x00 , 0x04 , 0x15, (byte) 0xc9 } ;
        Log.d(TAG, "sendSerialPort: 发送获取设备id数据, len:" + bytes.length  + "  " + ByteUtil.bytes2HexStr(bytes));
        LogToFile.writeLog( "sendSerialPort: 发送获取设备id数据, len:" + bytes.length  + "  " + ByteUtil.bytes2HexStr(bytes) );

        equalsCrc( bytes ) ;
        rxSendData(bytes).subscribeOn(mSendScheduler).subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object o) {
//                LogManager.instance().post(new SendMessage("发送命令"));
                LogPlus.e("发送命令" );
            }

            @Override
            public void onError(Throwable e) {
                LogPlus.e("发送失败", e);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 验证crc
     * @param byteQueryCb
     */
    public boolean equalsCrc(byte[] byteQueryCb){
        try {
            if (byteQueryCb.length >3) {
                byte[] byteData = new byte[byteQueryCb.length - 2];
                byte[] byteCrc = new byte[2];
                System.arraycopy(byteQueryCb, 0, byteData, 0, byteQueryCb.length - 2);
                System.arraycopy(byteQueryCb, byteQueryCb.length - 2 , byteCrc, 0, 2 );
                short ii = (short) CRC16Util.calcCrc16(byteData);
                byte[] byteCrcL = ByteUtil.shortToByteArrayLittel(ii);
                Log.d(TAG , "校验的crc:" + ByteUtil.bytesToHex2(byteCrcL) );
                if (Arrays.equals( byteCrc , byteCrcL)){
                    LogPlus.e( "crc 验证成功" );
                    return true ;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false ;
    }


}
