package ad.vipcare.com.retrofit;

import ad.vipcare.com.modle.AdvertShow;
import ad.vipcare.com.modle.MyWeather;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by allen on 18/8/13.
 */

public interface ApiServer {

//    @Headers({
//            "Accept: application/json",
//            "Content-Type: application/json; charset=utf-8"
//    })
//    @GET("TestToolforApp/Login/login")
//    Call<String> getLogin(@QueryMap Map<String, String> map);

//    @GET("TestToolforApp/Login/login")
//    Observable<LoginResponse> Login1(@Query("phone") String phone, @Query("password") String password);


//    @GET("TestToolforApp/Login/login?phone=13797745363&password=123456")
//    Call<LoginResponse> getLogin2();

//    @GET("TestToolforApp/login/{type}")
//    Call<LoginResponse> getLogin3(@Path("type") String type, @Query("phone") String phone, @Query("password") String password);

//    @FormUrlEncoded //POST请求必须添加
//    @POST("/login?")
//    Call<LoginResponse> postData(@Field("username") String phone, @Field("password") String passwrod);

//    @FormUrlEncoded
//    @POST("/login?")
//    Call<LoginResponse> postMapData(@FieldMap Map<String, String> map);


    // http://api.map.baidu.com/telematics/v3/weather?location=上海&output=json&ak=5slgyqGDENN7Sy7pw29IUvrZ

    /** 天气预报 */
    // https://search.heweather.com/find?location=北京&key=4b61a68895b149f1a5ea53fe43782e17
    @GET("find?key=4b61a68895b149f1a5ea53fe43782e17")
    Observable<MyWeather> getWheatherBeijin(@Query("location") String location);
    /** 天气预报 */
    //https://search.heweather.com/top?group=cn&key=4b61a68895b149f1a5ea53fe43782e17&number=20
//    @GET("top?key=4b61a68895b149f1a5ea53fe43782e17")
//    Observable<MyWeather> getWheatherGroup(@Query("group") String group, @Query("number") String number);


    /**下载文件  @Streaming注解可用于下载大文件 */
    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);


    /** 请求广告展示
     http://anxinchong.vipcare.com/api/ads/ad?d=xxx
     广告展示接口，d代表设备号。结果类似"1:20$2:30$1:10"，代表以广告订单1 20秒，广告订单2 30秒，广告订单1 10秒的顺序依次进行播放。
     */
    // http://anxinchong.vipcare.com/api/ads/ad?d=xxx
    @GET("ads/ad")
    Observable<String> getAdvertShow (@Query("d") String d );

    /** 请求广告展示
     http://anxinchong.vipcare.com/api/ads/pv?d=1&o=1&t=30&ts=1234225678&k=xxxxxxxxxxxxxxxxx
     广告统计，d=设备号，o=订单号，t=播放秒数，ts=时间戳，k=播放key,md5(设备号+订单号+时间戳+"CB300")
     */
    @GET("ads/pv")
    Observable<String> getAdvertCount (@Query("d") String d , @Query("o") String o  , @Query("t") String t ,  @Query("ts") String ts , @Query("k") String k );

    /**
     *  获取下载路径
     *  http://anxinchong.vipcare.com/api/ops/cb300Version
     */
    @GET("ops/cb300Version")
    Observable<String> getAdvertFile ();

    /**
     *  取到端口状态
     *  http://anxinchong.vipcare.com/api/ops/status?device_no=10001
     */
    @GET("ops/status")
    Observable<String> getPortStat (@Query("device_no") String device_no );



}