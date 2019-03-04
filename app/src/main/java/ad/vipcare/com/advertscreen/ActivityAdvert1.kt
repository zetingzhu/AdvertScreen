package ad.vipcare.com.advertscreen

import ad.vipcare.com.bean.AdvertInfoBean
import ad.vipcare.com.eventbus.DownloadEvent
import ad.vipcare.com.eventbus.HideMenuEvent
import ad.vipcare.com.timeUtil.TimeService
import ad.vipcare.com.usb.AUSBBroadCastReceiver
import ad.vipcare.com.usb.AUSBBroadCastReceiver.ACTION_USB_COPY_FILE
import ad.vipcare.com.usb.AUSBBroadCastReceiver.ACTION_USB_PERMISSION
import ad.vipcare.com.usb.DialogProgress
import ad.vipcare.com.util.FileUtils
import ad.vipcare.com.util.PermissionUtil
import ad.vipcare.com.widget.MyLocalImageHolderView
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.bigkoo.convenientbanner.ConvenientBanner
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator
import com.bigkoo.convenientbanner.holder.Holder
import com.bigkoo.convenientbanner.listener.OnPageChangeListener
import io.vov.vitamio.MediaPlayer
import io.vov.vitamio.Vitamio
import io.vov.vitamio.widget.VideoView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

/**
 * 广告展示信息
 */
//class ActivityAdvert1 : BaseActivity() , View.OnClickListener {
//
//    private val TAG = "ADVERT"
//    private val IMAPATH = Environment.getExternalStorageDirectory().toString() + File.separator + "DCIM" + File.separator + "Camera"
////    private val IMAPATH = Environment.getExternalStorageDirectory().toString() + File.separator + "bluetooth"
//    //顶部广告栏控件
//    private var convenientBanner: ConvenientBanner<Any> ?= null
//    // 显示文件列表
//    private var localImagesFiles: List<File> ?= null
//    // 显示的文件列表
//    private var imgFiles: MutableList<AdvertInfoBean> = mutableListOf()
//    // 拷贝U盘文件
//    private val btnSdCard by bindView<Button>(R.id.btn_sdcard)
//    // 广告文件处理类
//    private var advertPer : AdvertPersenter ?= null
//    // 视频播放控件
////    private var vvVitamio: VideoView? = null
//    // 当前滚动状态 0: 静止没有滚动 1:正在被外部拖拽,一般为用户正在用手指滚动 2:自动滚动
//    private var pageNewState: Int ?= null
//    // 播放视频的位置
//    private var pageIndex: Int ?= null
//    //mVideoView
//    private var mVideoView: VideoView? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        Log.w(TAG , "------onCreate-----")
//        // 初始化
////        Vitamio.isInitialized(applicationContext)
//        setContentView(R.layout.activity_advert)
//        // 隐藏actionbar
//        val actionBar = supportActionBar
//        actionBar!!.hide()
//        // 初始化布局文件
//        initView()
//        // 初始化使用对象
//        initData()
//
//        // 请求窗口权限
////        PermissionUtil.requestPermission(this@ActivityAdvert)
//
//        /** 注册事件 */
//        EventBus.getDefault().register(this)
//
//        //启动后台时间监听服务服务
//        val service = Intent(this, TimeService::class.java)
//        startService(service)
//
//    }
//
//    fun initData(){
//        advertPer = AdvertPersenter(this@ActivityAdvert)
//        // 初始化广播类
//        advertPer!!.registerReceiver( this@ActivityAdvert )
//    }
//
//    private fun initView() {
//
////        vvVitamio = findViewById(R.id.vvVitamio) as VideoView?
////        //设置相关的监听
////        vvVitamio!!.setOnPreparedListener(mPreparedListener)
////        vvVitamio!!.setOnErrorListener(mOnErrorListener)
////        vvVitamio!!.setOnCompletionListener(mCompletionListener)
////        vvVitamio!!.setOnInfoListener (mInfoListener)
//
//        mVideoView = findViewById(R.id.mVideoView) as VideoView?
//
//        localImagesFiles = FileUtils.listFilesInDir(  IMAPATH )
//
//        if (localImagesFiles != null &&  localImagesFiles!!.any() ) {
//            showToast("有数据：" + IMAPATH )
//            localImagesFiles!!.forEach { Log.i(TAG, "------文件名称：${it.name}  ") }
//            localImagesFiles = localImagesFiles!!.filter { (it.name.contains(".png") || it.name.contains(".jpg") || it.name.contains(".mp4") ) && it.length() >0 }
//            localImagesFiles!!.forEach { Log.i(TAG, "------过滤之后的文件名称：${it.name} -大小：${it.length()} ") }
//            Log.w(TAG , "------展示页面数量" + localImagesFiles!!.size )
//
//            for(value in localImagesFiles!! ){
//                var aib : AdvertInfoBean = AdvertInfoBean()
//                aib.file = value
//                aib.fileName = value.name
//                aib.fileFormat = value.name.split(".")[1]
//                imgFiles.add(aib)
//            }
//
//            convenientBanner = findViewById(R.id.convenientBanner) as ConvenientBanner<Any>
//
//            convenientBanner!!.setPages(
//                    object : CBViewHolderCreator<Holder<String>> {
////                        override fun getVitamio(type: Boolean, obj: Any?) {
////                            Log.i(TAG , "获取得到视频对象数据 --${type } -- ${obj}"  )
////                        }
//
//                        override fun createHolder(itemView: View): MyLocalImageHolderView {
//                            Log.w(TAG , "--createHolder--这个会创建多少次来---"  )
//                            var myLihv = MyLocalImageHolderView(this@ActivityAdvert, itemView)
//                            return myLihv
//                        }
//
//                        override fun getLayoutId(): Int {
//                            return R.layout.item_localimage
//                        }
//                    }, imgFiles as List<Any>?)
//                    //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
//                    .setPageIndicator(intArrayOf(R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused))
//                    .setOnItemClickListener { position -> Toast.makeText(this@ActivityAdvert, "点击了第" + position + "个", Toast.LENGTH_SHORT).show() }
//                    //设置指示器的方向
//                    .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
//                    //设置指示器是否可见
//                    .setPointViewVisible(true)//
//                    // 设置能否循环滚动
//                    .setCanLoop(true)
//                    //监听翻页事件
//                    .setOnPageChangeListener(onPageChange)
//        } else {
//            showToast("没有数据")
//        }
//        // sdcard 点击事件
//        btnSdCard.setOnClickListener( this@ActivityAdvert )
//    }
//
//    /**
//     * 切换的监听
//     */
//    var onPageChange : OnPageChangeListener = object : OnPageChangeListener{
//        override fun onPageSelected(index: Int) {
//            Log.w(TAG , "--切换------------------- onPageSelected ：${index}"  )
//            if( pageNewState == 0 ){
//                // 状态为0 说明当前已经切换完成
//                if(imgFiles.get(index).fileFormat.contains("mp4")){
//                    pageIndex = index
//                    convenientBanner!!.stopTurning()
////                    vvVitamio!!.visibility = View.VISIBLE
//                    mVideoView!!.visibility = View.VISIBLE
//                    setVodeoFile(imgFiles.get(index).file)
//                }
//            }
//        }
//
//        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
////            Log.w(TAG , "--切换 onScrolled ：${dx}  - ${dy}" )
//        }
//
//        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
//            Log.d(TAG , "--切换 onScrollStateChanged ：${newState}"  )
//            pageNewState = newState
////            if( pageNewState == 0 ){
////                var ll = recyclerView!!.getChildAt(0) as LinearLayout
////                vvVitamio = ll.getChildAt(1) as VideoView
////            }
//        }
//
//    }
//
//
//    /**
//     * 如果有时视频文件配置视频属性
//     */
//    fun setVodeoFile(data: File) {
//        // 播放文件地址
////        vvVitamio!!.setVideoPath(data.path)
////                    开始播放
////        vvVitamio!!.start()
//
//    }
//
//
//    //播放准备
//    internal var mPreparedListener: MediaPlayer.OnPreparedListener = object : MediaPlayer.OnPreparedListener {
//        override fun onPrepared(mp: MediaPlayer?) {
//                    Log.i(TAG, "播放 准备")
//            //此处设置播放速度为正常速度1
////                        mediaPlayer.setPlaybackSpeed(1.0f);
//        }
//    }
//
//    //播放错误
//    internal var mOnErrorListener: MediaPlayer.OnErrorListener = MediaPlayer.OnErrorListener { mp, what, extra ->
//        Log.i(TAG, "播放 错误 what: $what - extra:$extra")
//        false
//    }
//
//    //播放结束
//    internal var mCompletionListener: MediaPlayer.OnCompletionListener = object : MediaPlayer.OnCompletionListener {
//        override fun onCompletion(mp: MediaPlayer?) {
//                    Log.i(TAG, "播放 结束")
//            //            mediaPlayer.seekTo(0);   //转到第一帧
//            //            mediaPlayer.start();     //开始播放
//            //pageIndex
////            vvVitamio!!.seekTo(0)
////            vvVitamio!!.stopPlayback()
////            vvVitamio!!.visibility = View.GONE
//            mVideoView!!.visibility = View.GONE
//            convenientBanner!!.startTurning()
//        }
//    }
//    // 播放缓存
//    internal var mInfoListener: MediaPlayer.OnInfoListener = object : MediaPlayer.OnInfoListener{
//        override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
//            when(what){
//                MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
//                    Log.i(TAG, "视频信息 MEDIA_INFO_BUFFERING_START")
//                }
//                MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
//                    Log.i(TAG, "视频信息 MEDIA_INFO_BUFFERING_END")
//                }
//                MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED -> {
//                    Log.i(TAG, "视频信息 MEDIA_INFO_DOWNLOAD_RATE_CHANGED")
//                }
//            }
//            return true
//        }
//    }
//
//
//    /**
//     * 刷新界面数据
//     */
//    fun refreshImagePages(){
//        localImagesFiles = advertPer!!.getAdvertForTime()
//        convenientBanner!!.setListData(localImagesFiles)
//        convenientBanner!!.setCanLoop(false)
//        convenientBanner!!.setPointViewVisible(false)
//        convenientBanner!!.stopTurning()
//    }
//
//    /**
//     * 开始播放视频文件
//     */
//    fun pleyVodeoStart(){
//
//    }
//
//    /**
//     * 结束播放视频文件
//     */
//    fun pleyVodeoStop(){
//
//    }
//
//
//
//    /**
//     * 处理点击事件
//     */
//    override fun onClick(v: View?) {
//        when (v!!.id){
//            R.id.btn_sdcard -> {
////                var str: String ?= null
////                str!!.length
//                sendBroadcastSdcard()
//                // 测试切换广告
////                refreshImagePages()
//            }
//        }
//    }
//
//    /**
//     * 点击按钮发送广播拷贝sdcard中的文件
//     */
//    fun sendBroadcastSdcard(){
//        val intentReceiver = Intent()
////        intentReceiver.action = AUSBBroadCastReceiver.ACTION_USB_COPY_FILE
//        intentReceiver.action = UsbManager.ACTION_USB_DEVICE_ATTACHED
//        sendBroadcast(intentReceiver)
//    }
//
//    override fun onStart() {
//        super.onStart()
//        Log.w(TAG , "------onStart-----")
//    }
//
//    override fun onStop() {
//        super.onStop()
//        Log.w(TAG , "------onStop-----")
//    }
//
//    override fun onResume() {
//        super.onResume()
//        Log.w(TAG , "------onResume-----")
//        if (convenientBanner != null) {
//            //开始自动翻页
//            convenientBanner!!.startTurning(5000)
//        }
//        hideUiMenu(this@ActivityAdvert)
//    }
//
//    override fun onPause() {
//        super.onPause()
//        Log.w(TAG , "------onPause-----")
//        if (convenientBanner != null) {
//            //停止翻页
//            convenientBanner!!.stopTurning()
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        /** 取消注册 */
//        EventBus.getDefault().unregister(this)
//        // 结束服务
//        val service = Intent(this, TimeService::class.java)
//        stopService(service)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        // 接收窗口权限结果
//        PermissionUtil.onActivityResult(this@ActivityAdvert ,requestCode , resultCode , data )
//    }
//
//    /**
//     * 接收消息来显示全屏
//     */
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onEvent(event: HideMenuEvent) {
//        Log.i(TAG , "EventBus event.type：" + event.type )
//        when(event.type){
//            1 -> {
//                Log.i(TAG , "1->通知隐藏状态栏" )
//                hideUiMenu(this@ActivityAdvert)
//            }
//            2 -> {
//                Log.i(TAG , "2->通知比较查看时间" )
//            }
//        }
//    }
//
//    /**
//     * 显示全屏
//     */
//    fun hideUiMenu(mContext: Activity) {
//        mContext.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//    }
//
//    /**
//     * 隐藏全屏显示
//     */
//    fun showUiMenu(mContext: Activity) {
//        mContext.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//    }
//
//
//    // 下载进度条
//    private var pd: DialogProgress? = null
//
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onDownloadEvent(event: DownloadEvent ) {
//        Log.i(TAG , "EventBus event.type：" + event.type )
//        when(event.type){
//            1 -> {
//                Log.i(TAG , "显示对话框" )
//                showDownloadProgress()
//            }
//            2 -> {
//                setDownloadProgress(event.progress)
//            }
//        }
//    }
//
//    /**
//     * 展示下载进取框
//     */
//    fun showDownloadProgress() {
//        pd = DialogProgress(this@ActivityAdvert)
//        pd!!.setDialogShow()
//        pd!!.setOnDismissListener{
//            Log.i(TAG, "下载进度条结束掉")
//            EventBus.getDefault().post(HideMenuEvent(1))
//        }
//    }
//
//    /**
//     * 设置进度
//     * @param progress
//     */
//    fun setDownloadProgress(progress: Int) {
//        if (progress >= 100) {
//            pd!!.dismiss()
//        } else {
//            pd!!.setProgress(progress)//设置进度条的当前进度
//        }
//    }
//
//
//}
