package ad.vipcare.com.advertscreen

import ad.vipcare.com.util.SystemUtil1
import android.app.Activity
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var button1 :Button
    private val button2 by bindView<Button>(R.id.button2)
    private val button3:Button by bindView(R.id.button3)
    private val button4:Button by bindView(R.id.button4)
    private val button5:Button by bindView(R.id.button5)
    private val button6:Button by bindView(R.id.button6)
    private val button7:Button by bindView(R.id.button7)
    private val button8:Button by bindView(R.id.button8)

    private val textView:TextView by bindView(R.id.textView)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        setContentView(R.layout.activity_main)

        val actionBar = supportActionBar
        actionBar!!.hide()

        hideUiMenu( this@MainActivity )

        initView()

    }

    fun initView(){
        textView.text = "MainActivity"

        button1 = findViewById(R.id.button1) as Button
        button1.text = "隐藏状态栏和虚拟按键"
        button1.setOnClickListener {
//                  hideUiMenu( this@MainActivity )
            SystemUtil1.hideBottomUIMenu( this@MainActivity )
        }

        button2.setText("显示状态栏和虚拟按键")
        button2.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                showUiMenu( this@MainActivity )
            }
        })

        button3.setText("隐藏导航栏")
        button3.setOnClickListener {
            /**
             * View.SYSTEM_UI_FLAG_HIDE_NAVIGATION 隐藏导航栏，点击屏幕任意区域，导航栏将重新出现，并且不会自动消失。
             */
//            this@MainActivity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            /**
             * View.SYSTEM_UI_FLAG_IMMERSIVE 使状态栏和导航栏真正的进入沉浸模式,即全屏模式，
             * 如果没有设置这个标志，设置全屏时，我们点击屏幕的任意位置，就会恢复为正常模式。
             * 所以，View.SYSTEM_UI_FLAG_IMMERSIVE 都是配合View.SYSTEM_UI_FLAG_FULLSCREEN和View.SYSTEM_UI_FLAG_HIDE_NAVIGATION一起使用的。
             */
//            this@MainActivity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE
            /**
             * View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
             * 它的效果跟View.SYSTEM_UI_FLAG_IMMERSIVE一样。但是，它在全屏模式下，
             * 用户上下拉状态栏或者导航栏时，这些系统栏只是以半透明的状态显示出来，并且在一定时间后会自动消息。
             */
            this@MainActivity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        }

        button4.setText("隐藏状态栏")
        button4.setOnClickListener {
            /**
             * View.SYSTEM_UI_FLAG_FULLSCREEN 隐藏状态栏，点击屏幕区域不会出现，需要从状态栏位置下拉才会出现。
             */
            this@MainActivity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN  or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

//            this@MainActivity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE
        }

        button5.setText("将布局内容拓展到导航栏的后面")
        button5.setOnClickListener {
            // 将布局内容拓展到导航栏的后面 View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            this@MainActivity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }

        button6.setText("将布局内容拓展到状态栏的后面")
        button6.setOnClickListener {
            this@MainActivity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        button7.setText("显示正常")
        button7.setOnClickListener {
            this@MainActivity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
        button8.setText("全屏显示")
        button8.setOnClickListener {
            this@MainActivity.window.decorView.systemUiVisibility = View.INVISIBLE
        }

    }

    fun hideUiMenu(mContext: Activity) {
        /**
        SYSTEM_UI_FLAG_LOW_PROFILE	弱化状态栏和导航栏的图标
        SYSTEM_UI_FLAG_HIDE_NAVIGATION	隐藏导航栏，用户点击屏幕会显示导航栏
        SYSTEM_UI_FLAG_FULLSCREEN	隐藏状态栏
        SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION	拓展布局到导航栏后面
        SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN	拓展布局到状态栏后面
        SYSTEM_UI_FLAG_LAYOUT_STABLE	稳定的布局，不会随系统栏的隐藏、显示而变化
        SYSTEM_UI_FLAG_IMMERSIVE	沉浸模式，用户可以交互的界面
        SYSTEM_UI_FLAG_IMMERSIVE_STICKY	沉浸模式，用户可以交互的界面。同时，用户上下拉系统栏时，会自动隐藏系统栏
         */
        mContext.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    fun showUiMenu(mContext: Activity) {
        mContext.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        //or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

    }

    /**
     * 延迟加载控件id
     */
    fun <T : View> Activity.bindView(@IdRes res: Int): Lazy<T> {
        return lazy { findViewById(res) as T }
    }


}
