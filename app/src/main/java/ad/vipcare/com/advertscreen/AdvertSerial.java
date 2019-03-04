package ad.vipcare.com.advertscreen;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ad.vipcare.com.bean.PortStat;
import ad.vipcare.com.modle.RequestPort;
import ad.vipcare.com.modle.RequestPortStat;
import ad.vipcare.com.serialport.Device;
import ad.vipcare.com.serialport.SerialPortListener;
import ad.vipcare.com.serialport.SerialPortManager;
import ad.vipcare.com.util.ByteUtil;
import ad.vipcare.com.util.LogPlus;
import ad.vipcare.com.util.SPStaticUtils;

/**
 * 串口操作
 * Created by zeting
 * Date 19/1/22.
 */
public class AdvertSerial extends  AdvertDownload {

    private static final String TAG = "AdvertPersenter/SP";


    // 串口是否已经打开
    private boolean mOpened = false;
    // 打开串口设备位置
    private Device mDevice;
    // 所有端口状态
    private List<PortStat> mListPort ;
    // 所有端口开通未开通状态
    private List<RequestPort> mListPortOpenStats ;
    // recycleview
    private RecyclerView recyclerViewPort ;
    // 界面适配器
    RecyclerViewGridAdapter recyclerViewGridAdapter ;

    public RecyclerView getRecyclerViewPort() {
        return recyclerViewPort;
    }

    public void setRecyclerViewPort(RecyclerView recyclerViewPort) {
        this.recyclerViewPort = recyclerViewPort;

        // 进入的时候初始化端口状态为未开通
        if (recyclerViewPort != null) {
            initPortDate(false , true );
        }
    }

    public List<PortStat> getListPort() {
        return mListPort;
    }

    public void setListPort(List<PortStat> listPort) {
        mListPort = listPort;
    }


    public AdvertSerial(Context con) {
        super(con);
    }

    /**
     *  定时器
     */
    ScheduledExecutorService mScheduledExecutorService ;

    /**
     * 需要开启电门等待一秒读取状态
     */
    public void setReBleState(){

        mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        mScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG , "定时服务" );
                try {

//                    ((Activity)mContext).runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            // 测试刷新端口数据
//                            RefreshPortData() ;
//                        }
//                    });

                    if (mOpened) {
                        if ("".equals( getDeviceId()) || "0".equals( getDeviceId())){
                            // 如果设备号为空就读取一次设备号
                            getPortDeviceId();
                        } else {
                            // 定时服务 获取串口数据
                            sendReadData();
                        }
                    }else {
                        Log.e(TAG , "如果串口没有打开就打开串口" );
                        switchSerialPort();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG , "定时服务 执行异常：" + e );
                }
            }
        } , 1 , 10 , TimeUnit.SECONDS);

    }



    /**
     *  停止扫描蓝牙信息
     */
    public void stopReadBleState (){
        if (mScheduledExecutorService != null){
            mScheduledExecutorService.shutdownNow() ;
            mScheduledExecutorService = null ;
        }
    }

    /**
     * 判断线程池当前执行线程状态
     * @return true  线程池已关闭 false 线程池没有关闭
     */
    private boolean scheduledIsTerminated(){
        if ( mScheduledExecutorService != null){
            return mScheduledExecutorService.isTerminated() ;
        }
        if (mScheduledExecutorService == null){
            return true ;
        }
        return false ;
    }

    /**
     * 打开串口获取设备ID
     */
    public void getPortDeviceId(){
        SerialPortManager.instance().ReadDeviceIdsendCommand();
    }


    /**
     * 打开或关闭串口
     */
    public void switchSerialPort() {
        if (mOpened) {
            SerialPortManager.instance().close();
            mOpened = false;
            stopReadBleState();
        } else {
//            mDevice = new Device( "/dev/ttyS2" , "9600" );
//            mDevice = new Device( "/dev/ttyS4" , "9600" );
            mDevice = new Device( "/dev/ttyS3" , "9600" );
            mOpened = SerialPortManager.instance().open(mDevice , mSpl) != null;
            if (mOpened) {
                LogPlus.d("成功打开串口");
                /** 启动定时线程池 */
                if ( scheduledIsTerminated() ){
                    setReBleState() ;
                }
                /** 串口打开成功获取设备号，只获取一次 */
            } else {
                LogPlus.d("打开串口失败");
            }
        }
    }



    SerialPortListener mSpl = new SerialPortListener() {
        @Override
        public void getDataByte(byte[] data) {
            writeLog("成功拿到了，数据：" + data.length );
            checkPortStat(data) ;
        }
    } ;


    /**
     * 校验各个串口的状态
     * @param data
     */
    public void checkPortStat(byte[] data){
        try {
            Log.i(TAG , "开始处理得到的数据:" + data.length );
//        Log.i(TAG , "处理得到的值："+ ByteUtil.getBit(data[2]) + " - " + ByteUtil.getBit(data[3])  );
            if (data.length == 11 ) {
                int[] intBy2 = ByteUtil.getBitToInt(data[3]) ;
                int[] intBy3 = ByteUtil.getBitToInt(data[4]) ;
                int[] intBy4 = ByteUtil.getBitToInt(data[5]) ;
                int[] intBy5 = ByteUtil.getBitToInt(data[6]) ;
                int[] intBy6 = ByteUtil.getBitToInt(data[7]) ;
                int[] intBy7 = ByteUtil.getBitToInt(data[8]) ;

                PortStat portStat1 = new PortStat(1 , intBy2[0] , intBy4[0] , intBy6[0]);
                PortStat portStat2 = new PortStat(2 , intBy2[1] , intBy4[1] , intBy6[1]);
                PortStat portStat3 = new PortStat(3 , intBy2[2] , intBy4[2] , intBy6[2]);
                PortStat portStat4 = new PortStat(4 , intBy2[3] , intBy4[3] , intBy6[3]);
                PortStat portStat5 = new PortStat(5 , intBy2[4] , intBy4[4] , intBy6[4]);
                PortStat portStat6 = new PortStat(6 , intBy2[5] , intBy4[5] , intBy6[5]);
                PortStat portStat7 = new PortStat(7 , intBy2[6] , intBy4[6] , intBy6[6]);
                PortStat portStat8 = new PortStat(8 , intBy2[7] , intBy4[7] , intBy6[7]);

                PortStat portStat9 = new PortStat(9 , intBy3[0] , intBy5[0] , intBy7[0]);
                PortStat portStat10 = new PortStat(10 , intBy3[1] , intBy5[1] , intBy7[1]);
                PortStat portStat11 = new PortStat(11 , intBy3[2] , intBy5[2] , intBy7[2]);
                PortStat portStat12 = new PortStat(12 , intBy3[3] , intBy5[3] , intBy7[3]);

                if (mListPort != null ) {
                    mListPort.clear();
                }
                mListPort = null ;
                mListPort = new ArrayList<>();
                mListPort.add(portStat1);
                mListPort.add(portStat2);
                mListPort.add(portStat3);
                mListPort.add(portStat4);
                mListPort.add(portStat5);
                mListPort.add(portStat6);
                mListPort.add(portStat7);
                mListPort.add(portStat8);
                mListPort.add(portStat9);
                mListPort.add(portStat10);
                mListPort.add(portStat11);
                mListPort.add(portStat12);

                /** 测试打印端口状态 */
                testShowPortStat(intBy4 , intBy5 , data);

                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /** 校验刷新端口数据 */
                        RefreshPortData(mListPort);
                    }
                });


            }else if (data.length == 13){
                // 说明获取的是设备id  01 03 08 EC 22 06 15 00 00 00 00 94 6D   000000102100002
                // 01 03 08 EC 21 06 15 00 00 00 00 A7 6D  102100001
                byte[] dvId = new byte[4];
                // 5,6 保存在1，2 位
                System.arraycopy(data, 5 , dvId, 0, 2 );
                // 3,4 保存在3，4 位
                System.arraycopy(data, 3 , dvId, 2, 2 );
                Log.e(TAG , "-----" +  ByteUtil.bytesToHex2(dvId) );
                int id = ByteUtil.bytesToInt2(dvId , 0 ) ;
                Log.e(TAG , "设备id: " +  id );
                // 设置设备id
                setDeviceId( String.valueOf( id ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            writeLog("解析数据串口错误：" + e.getMessage());
        }
    }

    /**
     * 处理买一个端口显示状态
     */
    public void testShowPortStat( int[] intBy4  , int[] intBy5 , byte[] data){
        StringBuffer sb = new StringBuffer() ;
        try {
            if (getListPort() != null ){
                for (PortStat ps : getListPort() ) {
                    int portId = ps.getPortId() ;
                    if (mListPortOpenStats != null) {
                        sb.append("第" + portId + "个状态:" + ps.getStat(Integer.parseInt(mListPortOpenStats.get(portId-1).getEnable())) + "-");
                    }else {
                        sb.append("第" + portId + "个状态:" + ps.getStat(0) + "-");
                    }
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            writeLog("展示串口状态错误：" + e.getMessage());
        }
        Log.e(TAG , "显示所有端口状态：" + sb.toString() );
        writeLog("显示所有端口状态：" + sb.toString());

//        StringBuffer sb1 = new StringBuffer() ;
//        int index = 0 ;
//        for (int i = 0; i < intBy4.length ; i++) {
//            index ++ ;
//            sb1.append( "第" + index + "个状态:" + intBy4[i] + "-" ) ;
//        }
//        for (int i = 0; i < intBy5.length ; i++) {
//            index ++ ;
//            sb1.append( "第" + index + "个状态:" + intBy5[i] + "-" ) ;
//        }
//        Log.e(TAG , "显示所有端口状态：" + sb1.toString() );
//        Log.e(TAG , "显示所有端口数据 5 :" +  ByteUtil.getBit(data[5]) + " - 6 :" +  ByteUtil.getBit(data[6]));
    }



    /**
     * 关闭串口
     */
    public void closeSerialPort() {
        SerialPortManager.instance().close() ;
        stopReadBleState();
    }

    /**
     * 发送读取数据的命令
     */
    private void sendReadData() {
        SerialPortManager.instance().sendCommand();
    }


    /**
     *  初始化端口状态
     * @param isversion true：反向显示  false：正常显示(默认)
     * @param orientation true 水平，false 垂直
     */
    private void initPortDate(Boolean isversion,Boolean orientation) {
        //创建一个集合，泛型是DetaBean
        ArrayList<PortStat> dates=new ArrayList<>();
        //给Bean类放数据，最后把装好数据的Bean类放到集合里面！！
        for(int x=1; x< 13; x++){
            //创建Bean类对象
            PortStat dateBean=new PortStat(); //DateBean是一个Bean类！！
            dateBean.setPortId( x );
            dateBean.setpStat(PortStat.PORT_CLOSE);
            //把Bean类放入集合
            dates.add(dateBean);
        }

        //创建适配器对象  参数一般有两个，上下文和数据加载集合
        recyclerViewGridAdapter = new RecyclerViewGridAdapter(mContext , dates);
        // 1 设置适配器
        recyclerViewPort.setAdapter(recyclerViewGridAdapter);

        //布局管理器所需参数，上下文
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext , 6 );

        //B 通过布局管理器，可以控制条目排列顺序  true：反向显示  false：正常显示(默认)
        gridLayoutManager.setReverseLayout(isversion);

        //C 设置RecyclerView显示的方向，是水平还是垂直！！ GridLayoutManager.VERTICAL(默认) false
        gridLayoutManager.setOrientation(orientation ? GridLayoutManager.VERTICAL: LinearLayoutManager.HORIZONTAL);

        //设置布局管理器 ， 参数 linearLayout
        recyclerViewPort.setLayoutManager(gridLayoutManager);

        // 获取本地是否保存了数据，如果保存了，就直接显示
        String spPort = SPStaticUtils.getString( SPPORTSTAT ) ;
        if (spPort != null && !"".equals(spPort)){
            try {
                RequestPortStat mPortStat =  mGson.fromJson(spPort , RequestPortStat.class ) ;
                // 处理数据
                mPortStat.getStatsList();
                // 将得到的端口状态传递给界面上
                refreshPortStat(mPortStat.getStats());
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *  刷新端口数据
     * @param mListPort
     */
    public void RefreshPortData(List<PortStat> mListPort){
        List<PortStat> oldList = recyclerViewGridAdapter.getDateBeen() ;
        for (int i = 0; i < mListPort.size(); i++) {
            PortStat newPs = mListPort.get(i);
            PortStat oldPs = oldList.get(i);
            if (newPs.getpStat() != oldPs.getpStat() ){
                // 如果接口数据和新的不一样，局部刷新
                Log.e(TAG , "更新第几条数据" + i + " - old: " + oldPs.getpStat() + " - new: " + newPs.getpStat() ) ;
                int finalI = i;
                // 测试刷新端口数据
                updateNearbyAndNewAnchorData(recyclerViewPort , newPs , finalI);
            }
        }
    }

    /**
     * 刷新断开数据
     */
    public void RefreshPortData(){
        Random random = new Random();
        int num = random.nextInt( 4 ) + 4;
        //创建一个集合，泛型是DetaBean
        ArrayList<PortStat> dates=new ArrayList<>();
        //给Bean类放数据，最后把装好数据的Bean类放到集合里面！！
        for(int x=0; x< 12; x++){
            //创建Bean类对象
            PortStat dateBean=new PortStat(); //DateBean是一个Bean类！！
            dateBean.setPortId( x );
            if (x % num == 0) {
                dateBean.setpStat(PortStat.PORT_ERROR);
            }else if (x % num == 1){
                dateBean.setpStat(PortStat.PORT_CLOSE);
            }else if (x % num == 2){
                dateBean.setpStat(PortStat.PORT_FREE);
            }else if (x % num == 3){
                dateBean.setpStat(PortStat.PORT_BUSY);
            }else {
                dateBean.setpStat(PortStat.PORT_ERROR);
            }
            //把Bean类放入集合
            dates.add(dateBean);
        }
        Log.i(TAG , "刷新数据 random:" + num  );
        List<PortStat> oldList = recyclerViewGridAdapter.getDateBeen() ;
        for (int i = 0; i < dates.size(); i++) {
            PortStat newPs = dates.get(i);
            PortStat oldPs = oldList.get(i);
            if (newPs.getpStat() != oldPs.getpStat() ){
                // 如果接口数据和新的不一样，局部刷新
//                notifyItemChanged(position, 1);
//                recyclerViewGridAdapter.refreshData(newPs , i ) ;
                Log.e(TAG , "更新第几条数据: " + i + " - old: " + oldPs.getpStat() + " - new: " + newPs.getpStat() ) ;
                updateNearbyAndNewAnchorData(recyclerViewPort , newPs , i);
            }
        }

//        recyclerViewGridAdapter.refreshData(dates);
    }

    /**
     * 更新某一个item数据
     * @param mRecyclerView
     * @param newPs 新的数据，
     * @param i 更新的第几天数据
     */
    public void updateNearbyAndNewAnchorData(RecyclerView mRecyclerView, PortStat newPs , int i ){
        recyclerViewGridAdapter.refreshData( newPs , i);
        RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForAdapterPosition(i);
        if (viewHolder != null && viewHolder instanceof RecyclerViewGridAdapter.GridViewHolder) {
            RecyclerViewGridAdapter.GridViewHolder itemHolder = (RecyclerViewGridAdapter.GridViewHolder) viewHolder ;
            itemHolder.setData(newPs);
        }

    }


    @Override
    public void refreshPortStat(List<RequestPort> mStats) {
        this.mListPortOpenStats = mStats ;
        Log.e(TAG , "网络刷新端口可用状态" ) ;
        List<PortStat> oldList = recyclerViewGridAdapter.getDateBeen() ;
        if (oldList.size() < 12 ){
            return ;
        }
        for (int i = 0; i < oldList.size(); i++) {
            PortStat oldPs = oldList.get(i);
            RequestPort newRp =  mStats.get(i) ;
            if ("1".equals( newRp.getEnable() )) {
                oldPs.setpStat(PortStat.PORT_FREE);
            } else {
                oldPs.setpStat(PortStat.PORT_CLOSE);
            }
            // 测试刷新端口数据
            updateNearbyAndNewAnchorData(recyclerViewPort, oldPs, i);
        }
    }
}
