package ad.vipcare.com.bean;

/**
 * Created by zeting
 * Date 19/1/22.
 */

public class PortStat {

    public final static int PORT_ERROR = -1 ; // 异常
    public final static int PORT_CLOSE = 0 ; // 未开通
    public final static int PORT_BUSY = 1 ; // 工作中
    public final static int PORT_FREE = 2 ; // 空闲

    private int portId ;
    private int int12 ;
    private int int13 ;
    private int int14 ;
    private int pStat ;// 端口状态

    public PortStat( ) {
    }

    public PortStat(int portId , int int12, int int13, int int14 ) {
        this.int12 = int12;
        this.int13 = int13;
        this.int14 = int14;
        this.portId = portId;
    }


    public int getpStat() {
        return pStat;
    }

    public void setpStat(int pStat) {
        this.pStat = pStat;
    }

    public int getPortId() {
        return portId;
    }

    public void setPortId(int portId) {
        this.portId = portId;
    }

    public int getInt12() {
        return int12;
    }

    public void setInt12(int int12) {
        this.int12 = int12;
    }

    public int getInt13() {
        return int13;
    }

    public void setInt13(int int13) {
        this.int13 = int13;
    }

    public int getInt14() {
        return int14;
    }

    public void setInt14(int int14) {
        this.int14 = int14;
    }

    /**
     * 获得端口状态
     * @return
     */
    public String getStat(int int0){
        return showPortStat(int0 , int13 , int14) ;
    }

    public int getStatInt(){
        showPortStat(int12 , int13 , int14) ;
        return pStat ;
    }

    /**
     * 判断显示的端口的状态
     * @param int12
     * @param int13
     * @param int14
     */
    private String showPortStat(int int12, int int13 , int int14){
        String stat = "未开通" ;
        setpStat( PORT_CLOSE );
        if ( int12 == 1 ){// 0: 开通  1:未开通 // 后面换成网络 1，开通
            if (int14 == 0){// 0:正常;1: 异常
                if (int13 == 1 ){//  0: 断开; 1: 闭合
                    stat = "充电中" ;
                    setpStat(PORT_BUSY);
                } else {
                    stat = "空闲" ;
                    setpStat(PORT_FREE);
                }
            } else {
                stat = "异常" ;
                setpStat(PORT_ERROR);
            }
        }
        return stat ;
    }
}
