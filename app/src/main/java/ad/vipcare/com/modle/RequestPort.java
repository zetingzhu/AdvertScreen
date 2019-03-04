package ad.vipcare.com.modle;

import android.net.wifi.WifiConfiguration;

/**
 * 网络请求的端口状态
 * Created by zeting
 * Date 19/1/30.
 */

public class RequestPort {

    private String portId ;// 端口号
    private String status ;// 端口状态
    private String enable ;// 代表是否启用 1,可以 , 0 ，不可用


    public RequestPort(String portId, String enable ) {
        this.enable = enable;
        this.portId = portId;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getPortId() {
        return portId;
    }

    public void setPortId(String portId) {
        this.portId = portId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
