package com.vipcare.listenerservice;

import android.app.ActionBar;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 监听应用服务
 * Created by zeting
 * Date 19/1/10.
 */
public class ServiceListen extends Service {

    private static final String TAG = "ServiceListen";
    /**
     *  定时器
     */
    ScheduledExecutorService mScheduledExecutorService ;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG , "---启动监听服务 onStartCommand------" ) ;
        /** 启动定时线程池 */
        if ( scheduledIsTerminated() ){
            setReBleState() ;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean stopService(Intent name) {
        Log.i(TAG , "---启动监听服务 stopService------" ) ;
        return super.stopService(name);
    }

    @Override
    public ComponentName startService(Intent service) {
        Log.i(TAG , "---启动监听服务 startService------" ) ;
        return super.startService(service);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG , "---启动监听服务 onDestroy------" ) ;
        stopReadBleState() ;
    }


    /**
     * 监听线程
     */
    public void listenerThread(){
        String frontName = ProcessUtils.getForegroundProcessName();
        Log.i(TAG , "---前端运行线程：" + frontName) ;
        String nowname = ProcessUtils.getCurrentProcessName() ;
        Log.i(TAG , "---当前进程名称：" + nowname) ;

//        Set<String> mSet = ServiceUtils.getAllRunningServices();
//        for (String str : mSet ) {
//            Log.i(TAG , "---所有服务：" + str) ;
//        }

        // 如果在前端运行的界面不是广告界面就重启广告界面
        if ( !frontName.equals("ad.vipcare.com.advertscreen") ){
            startADActivity() ;
        }

    }
    /**
     * 启动广播app
     */
    public void startADActivity(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        ComponentName componentName = new ComponentName("ad.vipcare.com.advertscreen", "ad.vipcare.com.advertscreen.ActivityAdvert");
        intent.setComponent(componentName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
    }
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
                    // 定时服务
                    listenerThread();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG , "定时服务 执行异常：" + e );
                }
            }
        } , 1 , 20 , TimeUnit.SECONDS);

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

}
