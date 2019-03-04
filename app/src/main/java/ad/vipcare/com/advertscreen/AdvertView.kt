package ad.vipcare.com.advertscreen

import ad.vipcare.com.bean.AdvertInfoBean
import ad.vipcare.com.bean.AdvertsListBean
import android.widget.ImageView
import android.widget.TextView

/**
 * Created by zeting
 * Date 19/1/16.
 */

interface AdvertView {
    /**
     * 刷新广告数据
     */
    fun refreshAdList( advertListBean : MutableList<AdvertInfoBean> )

    /**
     * 请求数据组成广告数据
     */
    fun requestAdList( advertListBean : MutableList<AdvertInfoBean> )

    // 日期
    fun getWerther_date() : TextView
    // 星期
    fun getWeather_week() : TextView
    // 天气
    fun getWeather_stat() : TextView
    // 时间
    fun getWeather_time() : TextView
    // 温度
    fun getWeather_temp() : TextView
    // 天气图片
    fun getWeather_status() : ImageView
    // 天气状态
    fun getWeather_stat1() : TextView
    fun getWeather_stat2() : TextView
    fun getWeather_stat3() : TextView

}
