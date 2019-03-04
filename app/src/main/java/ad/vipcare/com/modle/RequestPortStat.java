package ad.vipcare.com.modle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 网络请求接收数据
 * Created by zeting
 * Date 19/1/30.
 */

public class RequestPortStat  {
    private String code ;
    private String msg ;
    private RequestPortData data ;


    private List<RequestPort> mStats ;// 所有端口状态

    public RequestPortData getData() {
        return data;
    }

    public void setData(RequestPortData data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public List<RequestPort> getStats() {
        return mStats;
    }

    public void setStats(List<RequestPort> stats) {
        mStats = stats;
    }

    /**
     * 解析data获取端口状态
     */
    public void getStatsList(){
        try {
            mStats = new ArrayList<>();
            RequestPort mReqPort1 = new RequestPort(String.valueOf( 1 ) , data.getPortNum1().getEnable() );
            mStats.add(mReqPort1);
            RequestPort mReqPort2 = new RequestPort(String.valueOf( 2 ) , data.getPortNum2().getEnable() );
            mStats.add(mReqPort2);
            RequestPort mReqPort3 = new RequestPort(String.valueOf( 3 ) , data.getPortNum3().getEnable() );
            mStats.add(mReqPort3);
            RequestPort mReqPort4 = new RequestPort(String.valueOf( 4 ) , data.getPortNum4().getEnable() );
            mStats.add(mReqPort4);
            RequestPort mReqPort5 = new RequestPort(String.valueOf( 5 ) , data.getPortNum5().getEnable() );
            mStats.add(mReqPort5);
            RequestPort mReqPort6 = new RequestPort(String.valueOf( 6 ) , data.getPortNum6().getEnable() );
            mStats.add(mReqPort6);
            RequestPort mReqPort7 = new RequestPort(String.valueOf( 7 ) , data.getPortNum7().getEnable() );
            mStats.add(mReqPort7);
            RequestPort mReqPort8 = new RequestPort(String.valueOf( 8 ) , data.getPortNum8().getEnable() );
            mStats.add(mReqPort8);
            RequestPort mReqPort9 = new RequestPort(String.valueOf( 9 ) , data.getPortNum9().getEnable() );
            mStats.add(mReqPort9);
            RequestPort mReqPort10 = new RequestPort(String.valueOf( 10 ) , data.getPortNum10().getEnable() );
            mStats.add(mReqPort10);
            RequestPort mReqPort11 = new RequestPort(String.valueOf( 11 ) , data.getPortNum11().getEnable() );
            mStats.add(mReqPort11);
            RequestPort mReqPort12 = new RequestPort(String.valueOf( 12 ) , data.getPortNum12().getEnable() );
            mStats.add(mReqPort12);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
