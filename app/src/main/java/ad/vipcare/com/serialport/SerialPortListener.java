package ad.vipcare.com.serialport;

/**
 * 接口返回成的时候处理值
 * Created by zeting
 * Date 19/1/22.
 */

public interface SerialPortListener {
    void getDataByte(byte[] data);
}
