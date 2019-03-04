package ad.vipcare.com.advertscreen

import ad.vipcare.com.util.LogToFile
import android.app.Activity
import android.content.Context
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.view.View

import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.widget.Toast
import java.io.IOException

/**
 * Created by zeting
 * Date 19/1/10.
 */

open class BaseActivity : AppCompatActivity(){

    /**
     * 延迟加载控件id
     */
    fun <T : View> Activity.bindView(@IdRes res: Int): Lazy<T> {
        return lazy { findViewById(res) as T }
    }


    fun showToast( str: String){
        Toast.makeText(this@BaseActivity , str , Toast.LENGTH_SHORT ).show()
    }


    /**
     * 写入日志信息到文件中
     */
    fun writeError(context: Context, str : String) {
        writeLog(str)
    }

    /**
     * 写入日志信息到文件中去
     * @param str
     */
    fun writeLog(str: String) {
        LogToFile.writeLog(str)
    }

}
