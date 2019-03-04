package ad.vipcare.com.advertscreen;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ad.vipcare.com.bean.AdvertInfoBean;
import ad.vipcare.com.modle.BaseObserver;
import ad.vipcare.com.modle.RequestPort;
import ad.vipcare.com.modle.RequestPortStat;
import ad.vipcare.com.retrofit.ApiRetrofit;
import ad.vipcare.com.retrofit.ApiRetrofitWeather;
import ad.vipcare.com.retrofit.ApiServer;
import ad.vipcare.com.util.LogPlus;
import ad.vipcare.com.util.LogToFile;
import ad.vipcare.com.util.LogToFile22;
import ad.vipcare.com.util.MD5Util;
import ad.vipcare.com.util.RootUtil;
import ad.vipcare.com.util.SPStaticUtils;
import ad.vipcare.com.util.ScreenUtils;
import ad.vipcare.com.util.TimeUtils;
import interfaces.heweather.com.interfacesmodule.bean.Lang;
import interfaces.heweather.com.interfacesmodule.bean.Unit;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.NowBase;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * 基础类 , 网络请求放在这个地方
 * Created by zeting
 * Date 19/1/23.
 */

public abstract class AdvertBasePersenter {
    private static final String TAG = "AdvertPersenter/ABP";

    // 界面操作接口
    public AdvertView adView = null ;
    public Context mContext ;
    /**
     * Disposable 管理容器
     如果在请求的过程中Activity已经退出了, 这个时候如果回到主线程去更新UI, 那么APP肯定就崩溃了, 怎么办呢,
     上一节我们说到了Disposable , 说它是个开关, 调用它的dispose()方法时就会切断水管, 使得下游收不到事件, 既然收不到事件,
     那么也就不会再去更新UI了. 因此我们可以在Activity中将这个Disposable 保存起来, 当Activity退出时, 切断它即可.

     那如果有多个Disposable 该怎么办呢, RxJava中已经内置了一个容器CompositeDisposable,
     每当我们得到一个Disposable时就调用CompositeDisposable.add()将它添加到容器中,
     在退出的时候, 调用CompositeDisposable.clear() 即可切断所有的水管.
     */
    private CompositeDisposable compositeDisposable;

    // 网络请求service
    protected ApiServer apiServer = ApiRetrofit.getInstance().getApiService();
    protected ApiServer apiWeatherServer =  ApiRetrofitWeather.getInstance().getApiService();

    // 设备号
    private String deviceId = "0" ;
    // 上一次请求的广告播放数据
    public String oldAd = "";

    public Gson mGson ;

    // 第一次是否请求成功了，端口状态，没有请求成功，就再次获取
    private boolean portStat = false ;

    //保存到本地的key
    public static String SPPORTSTAT = "SpPortStat" ;

    public AdvertBasePersenter(Context context) {
        mContext = context;
        mGson = new Gson() ;
    }

    /**
     * 刷新断开状态方法
     */
    public abstract void refreshPortStat(List<RequestPort> mStats);


    /**
     * 显示对话框
     * @param str
     */
    public void showToast(String str){
        showToast( str , Toast.LENGTH_SHORT);
    }

    public void showToast(String str , int duration){
        Toast.makeText( mContext , str , duration ).show();
    }

    public String getDeviceId() {
        return deviceId;
    }

    /**
     *  设置设备id
     * @param dId
     */
    public void setDeviceId(String dId) {
        try {
            String autoId  = String.format("%015d",Integer.valueOf(dId));
            this.deviceId = autoId;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            this.deviceId = deviceId;
        }
        Log.d( TAG , "前面补0的数字：" + deviceId + " - 读取的id:" + dId );
    }

    public void setAdview(AdvertView advertView ){
        this.adView = advertView ;
    }

    public void addDisposable(Observable<?> observable, BaseObserver observer) {
        if (compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
        }
        compositeDisposable.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer));

    }

    public void removeDisposable() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }

    /**
     * 天气 定时器
     */
    ScheduledExecutorService mSEServiceWeather ;
    /**
     *  广告请求定时器
     */
    ScheduledExecutorService mSEServiceAD ;
    /**
     * 端口状态定时请求
     */
    ScheduledExecutorService mPortStat ;

    /**
     * 获取天气信息读取状态
     */
    public void setReadAdState(){
        mSEServiceAD = Executors.newSingleThreadScheduledExecutor();
        mSEServiceAD.scheduleAtFixedRate( new Runnable() {
            @Override
            public void run() {
                Log.e(TAG , "定时服务 请求广告" );
                try {
                    // 设置系统时间
                    setSystemDate();
                    // 请求广告信息
                    requestAdvertShow() ;
                    // 如果温度为空的话，就再次执行天气请求
                    if ("".equals( adView.getWeather_temp().getText())){
                        try {
                            Log.e(TAG , "请求天气接口" );
                            requestWeather() ;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    // 端口请求失败了，就再次请求
                    if (!portStat){
                        try {
                            // 请求端口状态
                            requestPortStat() ;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } , 0 , 20 , TimeUnit.SECONDS ) ;

    }

    /**
     * 判断线程池当前执行线程状态
     * @return true  线程池已关闭 false 线程池没有关闭
     */
    private boolean mSESAdTerminated(){
        if ( mSEServiceAD != null){
            return mSEServiceAD.isTerminated() ;
        }
        if (mSEServiceAD == null){
            return true ;
        }
        return false ;
    }
    /**
     *  停止获取广告信息信息
     */
    public void stopADState (){
        if (mSEServiceAD != null){
            mSEServiceAD.shutdownNow() ;
            mSEServiceAD = null ;
        }
    }

    /**
     * 获取端口状态请求
     */
    public void setReadPortState(){
        mPortStat = Executors.newSingleThreadScheduledExecutor();
        mPortStat.scheduleAtFixedRate( new Runnable() {
            @Override
            public void run() {
                Log.e(TAG , "定时服务 请求端口状态" );
                try {
                    // 请求端口状态
                    requestPortStat() ;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } , 0 , 1 , TimeUnit.DAYS ) ;

    }
    private boolean mPortStatTerminated(){
        if ( mPortStat != null){
            return mPortStat.isTerminated() ;
        }
        if (mPortStat == null){
            return true ;
        }
        return false ;
    }
    public void stopPortState (){
        if (mPortStat != null){
            mPortStat.shutdownNow() ;
            mPortStat = null ;
        }
    }

    /**
     * 获取天气信息读取状态
     */
    public void setReadWeatherState(){

        mSEServiceWeather = Executors.newSingleThreadScheduledExecutor();
        mSEServiceWeather.scheduleAtFixedRate( new Runnable() {
            @Override
            public void run() {
                Log.e(TAG , "定时服务 请求天气" );
                try {
                    requestWeather() ;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } , 0 , 1 , TimeUnit.HOURS ) ;
//        } , 0 , 5 , TimeUnit.SECONDS ) ;
    }

    /**
     * 判断线程池当前执行线程状态
     * @return true  线程池已关闭 false 线程池没有关闭
     */
    private boolean scheduledIsTerminated(){
        if ( mSEServiceWeather != null){
            return mSEServiceWeather.isTerminated() ;
        }
        if (mSEServiceWeather == null){
            return true ;
        }
        return false ;
    }

    /**
     *  停止获取天气信息信息
     */
    public void stopReadWeatherState (){
        if (mSEServiceWeather != null){
            mSEServiceWeather.shutdownNow() ;
            mSEServiceWeather = null ;
        }
    }


    /**
     * 停止各种网络请求的定时器
     */
    public void stopScheduled(){
        // 停止天气网络请求
        stopReadWeatherState();
        // 停止广告网络请求
        stopADState();
        // 停止端口状态请求
        stopPortState();
    }

    /**
     * 请求成功开始获取天气信息
     */
    public void startRequestWeather(){
        // 打开天气定时器
        if ( scheduledIsTerminated() ){
            setReadWeatherState() ;
        }
        // 打开网络请求定时器
        if ( mSESAdTerminated() ){
            setReadAdState();
        }
        // 打开端口信息
        if ( mPortStatTerminated() ){
            setReadPortState();
        }

        Drawable drawable1 = mContext.getResources().getDrawable(R.mipmap.ad_weather_stat_sd);
        drawable1.setBounds(0,0,10,15);
        adView.getWeather_stat1().setCompoundDrawables(drawable1,null,null,null);

        Drawable drawable2 = mContext.getResources().getDrawable(R.mipmap.ad_weather_stat_fs);
        drawable2.setBounds(0,0,15,15);
        adView.getWeather_stat2().setCompoundDrawables(drawable2,null,null,null);

        Drawable drawable3 = mContext.getResources().getDrawable(R.mipmap.ad_weather_stat_fj);
        drawable3.setBounds(0,0,17,15);
        adView.getWeather_stat3().setCompoundDrawables(drawable3,null,null,null);


    }

    /**
     *  请求天气
     */
    public void requestWeather () {
        /**
         * 实况天气
         * 实况天气即为当前时间点的天气状况以及温湿风压等气象指数，具体包含的数据：体感温度、
         * 实测温度、天气状况、风力、风速、风向、相对湿度、大气压强、降水量、能见度等。
         *
         * @param context  上下文
         * @param location 地址详解
         * @param lang       多语言，默认为简体中文
         * @param unit        单位选择，公制（m）或英制（i），默认为公制单位
         * @param listener  网络访问回调接口
         * CN101020100 上海
         */
        writeLog("网络请求 requestWeather ， 获取天气状态 ");
        HeWeather.getWeatherNow( mContext, "CN101020100", Lang.CHINESE_SIMPLIFIED, Unit.METRIC,
                new HeWeather.OnResultWeatherNowBeanListener() {
                    @Override
                    public void onError(Throwable e) {
                        Log.i( TAG , "onError: ", e);
                    }

                    @Override
                    public void onSuccess(List<Now> dataObject) {
                        Log.i( TAG , "onSuccess: " + new Gson().toJson(dataObject));
                        setWeatherData(dataObject);
                    }
                });
    }

    /**
     * 设置天气数据
     * @param dataObject
     */
    public void setWeatherData(List<Now> dataObject){
        if (dataObject.size() > 0 ) {
            Now now = dataObject.get(0);
            // 天气信息
            NowBase nowBase = now.getNow();
            Log.i( TAG , "天气信息: " + nowBase.toString() ) ;
            // 温度
            adView.getWeather_temp().setText(nowBase.getTmp());
            // 天气状况
            adView.getWeather_stat().setText(nowBase.getCond_txt());
            setWeatherImage(Integer.parseInt(nowBase.getCond_code())  , adView.getWeather_status() ) ;
            // 风速
            adView.getWeather_stat1().setText(nowBase.getHum() + "%");
            adView.getWeather_stat2().setText(nowBase.getWind_spd() +"Km");
            adView.getWeather_stat3().setText(nowBase.getWind_sc() +"级");
        }
    }

    /**
     * 设置系统时间
     */
    public void setSystemDate(){
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                long sys = System.currentTimeMillis() ;
                // 星期
                adView.getWeather_week().setText( TimeUtils.getChineseWeek(sys) ) ;
                // 日期
                SimpleDateFormat dd = new SimpleDateFormat("MM月dd日") ;
                adView.getWerther_date().setText( TimeUtils.millis2String(sys , dd ) ) ;
                // 时间
                SimpleDateFormat tt = new SimpleDateFormat("HH:mm") ;
                adView.getWeather_time().setText( TimeUtils.millis2String(sys , tt) ) ;
            }
        });

    }

    /**
     * 设置天气图标
     *
     * @param image
     */
    public void setWeatherImage(int code,  ImageView image  ) {
        switch (code) {
            case 100: //晴
//                image.setImageResource(R.drawable.new_vehicle_weather_sunny_image);
                image.setImageResource(R.mipmap.img_weather_sunny);
                break;
            case 101: //多云
            case 102:
            case 103:
//                image.setImageResource(R.drawable.new_vehicle_weather_cloudy_image);
                image.setImageResource(R.mipmap.img_weather_cloudy );
                break;
            case 104: //阴天
//                image.setImageResource(R.drawable.new_vehicle_weather_overcast_image);
                image.setImageResource(R.mipmap.img_weather_overcast);
                break;
            case 200:   //有风
            case 201:   //平静
            case 202:   //微风
            case 203:   //和风
            case 204:   //清风
//                image.setImageResource(R.drawable.new_vehicle_weather_wind_image);
                break;
            case 205:   //强风/劲风
            case 206:   //疾风
            case 207:   //大风
            case 208:   //烈风
            case 209:   //风暴
            case 210:   //狂爆风
            case 211:   //飓风
//                image.setImageResource(R.drawable.new_vehicle_weather_send_image);
                break;
            case 212:   //龙卷风
            case 213:   //热带风暴
//                image.setImageResource(R.drawable.new_vehicle_weather_tornado_image);
                break;
            case 300:   //阵雨
            case 301:   //强阵雨
            case 310:   //暴雨
            case 311:   //大暴雨
            case 312:   //特大暴雨
            case 313:   //冻雨
//                image.setImageResource(R.drawable.new_vehicle_ice_rain_image);
                break;
            case 302:   //雷阵雨
            case 303:   //强雷阵雨
//                image.setImageResource(R.drawable.new_vehicle_thunder_rain_image);
                break;
            case 304:   //雷阵雨伴有冰雹
//                image.setImageResource(R.drawable.new_vehicle_hail_image);
                break;
            case 305:   //小雨
            case 308:   //极端降雨
            case 309:   //毛毛雨/细雨
//                image.setImageResource(R.drawable.new_vehicle_weather_light_rain_image);
                image.setImageResource(R.mipmap.img_weather_rain);
                break;
            case 306:   //中雨
//                image.setImageResource(R.drawable.new_vehicle_weather_moderate_rain_image);
                break;
            case 307:   //大雨
//                image.setImageResource(R.drawable.new_vehicle_weather_heavy_rain_image);
                break;
            case 400:   //小雪
            case 401:   //中雪
            case 402:   //大雪
//                image.setImageResource(R.drawable.new_vehicle_weather_light_snow_image);
                break;
            case 403:   //暴雪
//                image.setImageResource(R.drawable.new_vehicle_weather_snowstorm_image);
                break;
            case 404:   //雨夹雪
//                image.setImageResource(R.drawable.new_vehicle_weather_sleet_image);
                break;
            case 405:   //雨雪天气
            case 406:   //阵雨夹雪
            case 407:   //阵雪
//                image.setImageResource(R.drawable.new_vehicle_weather_snow_image);
                break;
            case 500:   //薄雾
            case 501:   //雾
            case 502:   //霾
//                image.setImageResource(R.drawable.new_vehicle_weather_foggy_image);
                break;
            case 503:   //扬沙
            case 504:   //浮尘
            case 507:   //沙尘暴
            case 508:   //强沙尘暴
//                image.setImageResource(R.drawable.new_vehicle_weather_sandstorm_image);
                break;
        }
    }


    /**
     *  请求其他的网络信息
     */
    public void request  () {
        addDisposable( apiWeatherServer.getWheatherBeijin("上海"), new BaseObserver() {
            @Override
            public void onSuccess(Object o) {
                Log.i(TAG, "请求成功：" + o.toString());
//                baseView.onLoginSucc();

            }

            @Override
            public void onError(String msg) {
                Log.i(TAG, "请求失败：" + msg);
//                baseView.showError(msg);

            }
        });
    }



    /**
     *  请求广告显示信息
     */
    public void requestAdvertShow(){
        addDisposable( apiServer.getAdvertShow(deviceId), new BaseObserver() {
//        addDisposable( apiWeatherServer.getAdvertShow("1"), new BaseObserver() {
            @Override
            public void onSuccess(Object o) {
                // 这个接口返回值，完全脑残，要么全是json,要么全是字符串，正确是字符串,错误是json,错误就不管了，返回太牛逼，没法解析
                Log.i(TAG, "广告显示请求成功：" + o.toString());
                if (o != null &&  !o.equals(oldAd)) {
                    setAdbertData(o.toString());
                    // 将数据保存到老的数据中
                    oldAd = o.toString() ;
                }
            }

            @Override
            public void onError(String msg) {
                Log.i(TAG, "广告显示请求失败：" + msg);
            }
        });
    }

    /**
     *  请求应用文件下载路径
     */
    public void requestAdvertDownload(final AdvertDownload.DownloadUrlListener dUrl ){
        addDisposable( apiServer.getAdvertFile(), new BaseObserver() {
            @Override
            public void onSuccess(Object o) {
                Log.i(TAG, "下载文件请求成功：" + o.toString());
                try {
                    JSONObject jsonUrl = new JSONObject(o.toString());
                    JSONObject jsonData =jsonUrl.optJSONObject("data");
                    String url =jsonData.optString("url");
                    dUrl.getUrl(url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(String msg) {
                Log.i(TAG, "下载文件请求失败：" + msg);
            }
        });
    }

    /**
     *  获取设备端口状态
     */
    public void requestPortStat( ){
        portStat = false ;
        writeLog("网络请求 requestPortStat ， 获取端口状态 ");
        addDisposable( apiServer.getPortStat(deviceId), new BaseObserver() {
            @Override
            public void onSuccess(Object o) {
                // 这个更脑残 ， jsonarray 就可以解决，非要弄一个数字当key,嵌套那么多层
                Log.i(TAG, "端口状态请求成功：" + o.toString());
                RequestPortStat mPortStat =  mGson.fromJson(o.toString() , RequestPortStat.class ) ;
                // 处理数据
                mPortStat.getStatsList();
                // 将得到的端口状态传递给界面上
                refreshPortStat(mPortStat.getStats());
                portStat = true ;
                // 将数据保存到本地
                SPStaticUtils.put( SPPORTSTAT , o.toString() ) ;
            }

            @Override
            public void onError(String msg) {
                Log.i(TAG, "端口状态请求失败：" + msg);
            }
        });
    }


    /**
     * 设置广告数据
     */
    public void setAdbertData(String obj){

        String[] adId = obj.split("[$]") ;
        List<AdvertInfoBean> mList = new ArrayList<AdvertInfoBean>() ;

        for (int i = 0; i < adId.length ; i++) {
            String adInfoData = adId[i] ;
            String[] adInfo = adInfoData.split(":") ;
            if (adInfo.length == 2) {
                AdvertInfoBean aib = new AdvertInfoBean();
                aib.setAdId(adInfo[0]);
                aib.setPlayTime(adInfo[1]);
                mList.add(aib);
            }
        }
        // 刷新播放时间
        adView.requestAdList(mList);
    }

    /**
     *  请求广告统计
     *  广告统计，d=设备号，o=订单号，t=播放秒数，ts=时间戳，k=播放key,md5(设备号+订单号+时间戳+"CB300")
     */
    public void requestAdvertCount(  String o  ,  String t  ){
        String ts = String.valueOf(System.currentTimeMillis());
//        md5(设备号+订单号+时间戳+"CB300")
        String k = MD5Util.getMD5String(deviceId + o + ts + "CB300" ) ;

        addDisposable( apiServer.getAdvertCount(deviceId , o , t , ts , k ), new BaseObserver() {
            //        addDisposable( apiWeatherServer.getAdvertShow("1"), new BaseObserver() {
            @Override
            public void onSuccess(Object o) {
                Log.i(TAG, "广告统计请求成功：" + o.toString());

            }

            @Override
            public void onError(String msg) {
                Log.i(TAG, "广告统计请求失败：" + msg);
            }
        });
    }


    /**
     * 获取屏幕分辨率
     */
    public void getwidthheight(){
        LogPlus.sd("屏幕宽：" + ScreenUtils.getScreenWidth());
        LogPlus.sd("屏幕高：" + ScreenUtils.getScreenHeight());
        LogPlus.sd("屏幕密度dp：" + ScreenUtils.getScreenDensity());
        LogPlus.sd("屏幕密度dpi：" + ScreenUtils.getScreenDensityDpi() );
        LogPlus.sd("屏幕宽(dp)：" + ScreenUtils.getScreenWidthDp());
        LogPlus.sd("屏幕高(dp)：" + ScreenUtils.getScreenHeightDp());

        showToast("屏幕宽：" + ScreenUtils.getScreenWidth()+"-屏幕高：" + ScreenUtils.getScreenHeight()+"-屏幕密度dp：" + ScreenUtils.getScreenDensity()+"-屏幕密度dpi：" + ScreenUtils.getScreenDensityDpi() , Toast.LENGTH_LONG );
        showToast("屏幕宽(dp)：" + ScreenUtils.getScreenWidthDp() + " -屏幕高(dp)：" + ScreenUtils.getScreenHeightDp() , Toast.LENGTH_LONG);

//        LogToFile22.init(mContext);
//        boolean boo  = LogToFile22.writeToFile();
//        showToast("创建文件：" + boo , Toast.LENGTH_LONG);
//        showToast("创建文件：" + LogToFile22.getFilePath(mContext) , Toast.LENGTH_LONG);

    }

    /**
     *  清空图片缓存
     */
    public void clearGlide(){
        //  清除内存缓存
        Glide.get(mContext).clearMemory();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //清除磁盘缓存：
                Glide.get(mContext).clearDiskCache() ;
            }
        }).start();
    }

    /**
     *  获取应用版本
     * @return
     */
    public String getVersionName()   {
        try {
            PackageManager pm  = mContext.getPackageManager();
            PackageInfo info = pm.getPackageInfo(mContext.getPackageName(), 0);
            String versionName = info.versionName ;
            return versionName ;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "" ;
        }
    }

    /**
     *  获取root
     */
    public void getRoot() {
        boolean boo = RootUtil.isDeviceRooted() ;
        LogPlus.sd(TAG , "第一种方式 判断应用是否有root：" + boo);

        LogPlus.sd(TAG, "第二种方式 获取root权限获取root权限：" + RootUtil.getAppRoot()) ;

    }

    /**
     * 写入日志信息到文件中去
     * @param str
     */
    public void writeLog(String str) {
        LogToFile.writeLog(str);
    }
}
