package ad.vipcare.com.serialport;

import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import ad.vipcare.com.util.ByteUtil;
import ad.vipcare.com.util.LogPlus;
import ad.vipcare.com.util.LogToFile;

/**
 * 读串口线程
 */
public class SerialReadThread extends Thread {

    private static final String TAG = "SerialReadThread";

    private BufferedInputStream mInputStream;
    private SerialPortListener mSPListener ;


    public SerialReadThread(InputStream is) {
        mInputStream = new BufferedInputStream(is);
    }

    /**
     * 设置得到正确值得接口
     * @param mSPL
     */
    public void setSerialPortListener(SerialPortListener mSPL  ) {
        this.mSPListener =  mSPL ;
    }

    @Override
    public void run() {
        byte[] received = new byte[1024];
        int size;

        LogPlus.e("开始读线程");

        while (true) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            try {

                int available = mInputStream.available();

                if (available > 0) {
                    size = mInputStream.read(received);
                    if (size > 0) {
                        String hexStr = ByteUtil.bytes2HexStr(received, 0, size);
//                        LogPlus.e("读取到了数据 size:" + size + " - " + hexStr );
                        LogToFile.writeLog("读取到了数据 size:" + size + " - " + hexStr);
                        getData(true , received , size );
                    }
                } else {
                    // 暂停一点时间，免得一直循环造成CPU占用率过高
                    SystemClock.sleep(1);
                }
            } catch (IOException e) {
                LogPlus.e("读取数据失败", e);
            }
            //Thread.yield();
        }

        LogPlus.e("结束读进程");
    }

    boolean isReceive = false ;// 是否已经在接收返回值
    byte[] byData = null ;// 接收返回的数据数组
    int byIndex = 0 ;// 组成数组的长度
    /**
     * 处理获取到的数据
     */
    public void getData(boolean isRead , byte[] bt , int size){
//        LogPlus.i("传送的是什么数据：" + isRead + " - " + bt + " - " + size );
        if (isRead){
            if (!isReceive && bt == null ) {
                isReceive = true;
                byData = new byte[1024] ;/** 定义一个1024 的数字 */
                byIndex = 0 ;
            }
            if (byData != null && bt != null && size > 0 ){
                System.arraycopy(bt , 0 , byData , byIndex , size);
                byIndex += size ;

                byte length = byData[2] ;
//                LogPlus.e("得到数据长度:" + length );
                if (byIndex >= (length + 5)){
//                if (byIndex >=   5 ){
                    LogPlus.e("说明数据接收完了:" + length );
                    getData(false , null , 0) ;
                }
            }
        }else {
            if (isReceive){
                if (byIndex != 0 ) {
                    byte[] byReceive = new byte[byIndex];
                    System.arraycopy(byData, 0, byReceive, 0, byIndex);
                    String strReceive = ByteUtil.bytes2HexStr(byReceive);
                    LogPlus.e("拼接完成的数据 size:" + byIndex + " - " + strReceive);
                    LogToFile.writeLog("拼接完成的数据 size:" + byIndex + " - " + strReceive);
                    equalsCrc(byReceive);
                }
                isReceive = false ;
                byData = null ;
            }
        }

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
                LogToFile.writeLog("校验crc值：" + ByteUtil.bytes2HexStr(byteCrc)  + " -crcl " +  ByteUtil.bytes2HexStr(byteCrcL) );
                if (Arrays.equals( byteCrc , byteCrcL)){
                    LogPlus.e( "crc 验证成功" );
                    LogToFile.writeLog("校验crc值： - 成功 ");
                    if (mSPListener != null ){
                        LogToFile.writeLog("校验crc值： - 成功 1 ");
                        mSPListener.getDataByte(byteQueryCb);
                    }
                    return true ;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogToFile.writeLog("校验crc值 报错： " + e );
        }
        return false ;
    }





    /**
     * 停止读线程
     */
    public void close() {

        try {
            mInputStream.close();
        } catch (IOException e) {
            LogPlus.e("异常", e);
        } finally {
            super.interrupt();
        }
    }
}
