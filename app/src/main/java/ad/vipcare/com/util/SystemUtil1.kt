package ad.vipcare.com.util

import android.app.Activity
import android.os.Build
import android.view.View

/**
 * Created by zeting
 * Date 18/12/28.
 */

object SystemUtil1 {


    /**
     * 隐藏虚拟按键，并且全屏
     */
    fun hideBottomUIMenu(mContext: Activity) {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            val v = mContext.window.decorView
            v.systemUiVisibility = View.GONE
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            val decorView = mContext.window.decorView
            val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN
            decorView.systemUiVisibility = uiOptions
        }
    }

    fun hideNavigationBar(mContext: Activity) {
        var uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or // hide nav bar
                View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar

                if (Build.VERSION.SDK_INT >= 19) {
                    uiFlags = uiFlags or View.SYSTEM_UI_FLAG_IMMERSIVE//0x00001000; // SYSTEM_UI_FLAG_IMMERSIVE_STICKY: hide
                } else {
                    uiFlags = uiFlags or View.SYSTEM_UI_FLAG_LOW_PROFILE
                }

        try {
            mContext.window.decorView.systemUiVisibility = uiFlags
        } catch (e: Exception) {
            // TODO: handle exception
        }

    }

    fun hideUiMenu(mContext: Activity) {
        mContext.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    fun showUiMenu(mContext: Activity) {
        mContext.window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }

}
