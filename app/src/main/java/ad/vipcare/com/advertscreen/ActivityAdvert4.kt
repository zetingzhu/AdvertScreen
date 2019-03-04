package ad.vipcare.com.advertscreen

import ad.vipcare.com.bean.AdvertInfoBean
import ad.vipcare.com.bean.AdvertsListBean
import ad.vipcare.com.eventbus.DownloadEvent
import ad.vipcare.com.eventbus.HideMenuEvent
import ad.vipcare.com.timeUtil.TimeService
import ad.vipcare.com.usb.AUSBBroadCastReceiver
import ad.vipcare.com.usb.DialogProgress
import ad.vipcare.com.util.PermissionUtil
import ad.vipcare.com.util.RootUtil
import ad.vipcare.com.widget.MyLocalImageHolderView
import ad.vipcare.com.widget.MyPorlView
import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.*
import com.bigkoo.convenientbanner.ConvenientBanner
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator
import com.bigkoo.convenientbanner.holder.Holder
import com.bigkoo.convenientbanner.listener.OnPageChangeListener
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 广告展示信息
 */
//class ActivityAdvert : BaseActivity() , View.OnClickListener , AdvertView {
//
//
//    private val TAG = "ADVERT"
////    private val IMAPATH = Environment.getExternalStorageDirectory().toString() + File.separator + "DCIM" + File.separator + "Camera"
////    private val IMAPATH = Environment.getExternalStorageDirectory().toString() + File.separator + "bluetooth"
//    //顶部广告栏控件
//    private var convenientBanner: ConvenientBanner<Any> ?= null
//    // 显示文件列表
////    private var localImagesFiles: List<File> ?= null
//    // 显示的文件列表
//    private var imgFiles: MutableList<AdvertInfoBean> = mutableListOf()
//    private var imgFilesCopyOnWrite: MutableList<AdvertInfoBean> = CopyOnWriteArrayList()
//    // 拷贝U盘文件
////    private val btnSdCard by bindView<Button>(R.id.btn_sdcard)
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
//    private var iv_video: ImageView? = null
//    private var ll_video: FrameLayout? = null
//    // 新闻播放列表对象
////    private var adList : MutableList<AdvertInfoBean> ?= null
//    //
////    private var textView2 : TextView ?= null
//    // 拷贝U盘文件
//    private val mPorlView by bindView<MyPorlView>(R.id.mPorlView)
//
//    private var advertListBean : AdvertsListBean = AdvertsListBean()
//    private var gson : Gson = Gson()
//
//    // 显示充电断开状态
//    private val recyclerViewPort by bindView<RecyclerView>(R.id.recyclerViewPort)
//    // 日期 天气
//    // 年月
//    private val tv_weather_date by bindView<TextView>(R.id.tv_weather_date)
//    // 星期
//    private val tv_weather_week by bindView<TextView>(R.id.tv_weather_week)
//    // 天气
//    private val  tv_weather_stat by bindView<TextView>(R.id.tv_weather_stat)
//    // 时间
//    private val  tv_weather_time by bindView<TextView>(R.id.tv_weather_time)
//    // 温度
//    private val  tv_weather_temp by bindView<TextView>(R.id.tv_weather_temp)
//    // 天气图片
//    private val  iv_weather_status by bindView<ImageView>(R.id.iv_weather_status)
//    // 天气状态，湿度，风度，风速
//    private val  tv_weather_stat1 by bindView<TextView>(R.id.tv_weather_stat1)
//    private val  tv_weather_stat2 by bindView<TextView>(R.id.tv_weather_stat2)
//    private val  tv_weather_stat3 by bindView<TextView>(R.id.tv_weather_stat3)
//    // logo 图标
//    private val  iv_logo by bindView<ImageView>(R.id.iv_logo)
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
//
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
//        advertPer!!.setAdview(this)
//        // 初始化Usb监听广播类
//        advertPer!!.registerReceiver( this@ActivityAdvert )
//        // 根据上次文件获取数据
////        advertPer!!.fistReadAd()
//        // 打开串口
//        advertPer!!.switchSerialPort()
//        // 设置串口显示适配器显示
//        advertPer!!.recyclerViewPort = recyclerViewPort
//        // 校验并且打开智能安装服务
////        advertPer!!.openAccessibility()
//        // 请求网络天气信息
//        advertPer!!.startRequestWeather()
//        // 获取屏幕分辨率
////        advertPer!!.getwidthheight()
//        // 应用启动清空一下glide 缓存图片
//        advertPer!!.clearGlide()
//        // 应用获取root权限
//        advertPer!!.getRoot()
//    }
//
//
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
//        iv_video = findViewById(R.id.iv_video) as ImageView?
//        ll_video = findViewById(R.id.ll_video) as FrameLayout?
//
////        localImagesFiles = FileUtils.listFilesInDir(  IMAPATH )
//
//        if (imgFiles !=null && imgFiles!!.any()  ){
////        if (localImagesFiles != null &&  localImagesFiles!!.any() ) {
////            showToast("有数据：" + IMAPATH )
////            localImagesFiles!!.forEach { Log.i(TAG, "------文件名称：${it.name}  ") }
////            localImagesFiles = localImagesFiles!!.filter { (it.name.contains(".png") || it.name.contains(".jpg") || it.name.contains(".mp4") ) && it.length() >0 }
////            localImagesFiles!!.forEach { Log.i(TAG, "------过滤之后的文件名称：${it.name} -大小：${it.length()} ") }
////            Log.w(TAG , "------展示页面数量" + localImagesFiles!!.size )
////
////            var i = 0
////            for(value in localImagesFiles!! ){
////                i ++
////                var aib : AdvertInfoBean = AdvertInfoBean()
////                aib.file = value
////                aib.fileName = value.name
////                aib.fileFormat = value.name.split(".")[1]
////                aib.startDay = "2019-1-1"
////                aib.endDay = "2019-4-1"
////                if (i%2 ==0) {
////                    aib.playType = 0
////                }else{
////                    aib.playType = 1
////                }
////                aib.showCount = 10
////                aib.startTime = "10:00"
////                aib.endTime = "18:00"
////
////                imgFiles.add(aib)
////            }
//
////            advertListBean.adverList = imgFiles
////            var str = gson.toJson(advertListBean)
////            Log.w(TAG , "--显示json" + str )
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
//                    .setPointViewVisible(false)//
//                    // 设置能否循环滚动
//                    .setCanLoop(true)
//                    //监听翻页事件
//                    .setOnPageChangeListener(onPageChange)
//            convenientBanner!!.startTurning(10000)
//        } else {
////            showToast("没有数据")
//            Log.w(TAG , "没有数据" )
//        }
//        // sdcard 点击事件
////        btnSdCard.setOnClickListener( this@ActivityAdvert )
////
////        var  pm: PackageManager = getPackageManager()
////        var  info: PackageInfo = pm.getPackageInfo(getPackageName(), 0)
////        var name = info.versionName
////        textView2 = findViewById(R.id.textView2) as TextView?
////        textView2!!.setText(name)
//
////        mPorlView.requestLayout()
////        mPorlView.invalidate()
//
//        // logo 点击事件
//        iv_logo.setOnClickListener( this@ActivityAdvert )
//
//    }
//
//    /**
//     * 切换的监听
//     */
//    var onPageChange : OnPageChangeListener = object : OnPageChangeListener{
//        override fun onPageSelected(index: Int) {
////            Log.w(TAG , "--切换------------------- onPageSelected ：${index}"  )
//            if( pageNewState == 0 ){
//                // 状态为0 说明当前已经切换完成
//                // 设置广告播放时间
//                var indexAd:AdvertInfoBean = imgFiles.get(index)
//                if (indexAd != null && indexAd.playTime != null ) {
//                    // 选设置翻页时间
//                    var pT : Int = indexAd.playTime.toInt()
//                    convenientBanner!!.startTurning((pT * 1000).toLong())
//                    /** 显示之后请求广告统计 */
//                    advertPer!!.requestAdvertCount(indexAd.adId , indexAd.playTime )
//
//                }
//                // 设置图片格式
//                if(indexAd.fileFormat != null && indexAd.fileFormat.contains("mp4")){
//                    pageIndex = index
//                    convenientBanner!!.stopTurning()
////                    vvVitamio!!.visibility = View.VISIBLE
//                    setVodeoFile(imgFiles.get(index).file)
//                }
//                // 设定广告的播放次数
//                var adInfo = imgFiles!!.get(index)
//                var count = adInfo.playCount
//                adInfo.playCount = count + 1
//                // 将播放次数保存的sp 中，方便异常的时候继续计数
//                advertPer!!.saveFileCountSp(adInfo.fileName , adInfo.playCount )
//
//                /** 不需要广告校验了
//                if (index == 0 ) {
//                    Log.w(TAG, "-- 显示校验文件个数：${imgFiles.size}")
//                    // 这个地方需要校验是不是有新的数据列表需要更新
//                    var boo = advertPer!!.checkAdListInfo(imgFiles)
//                    Log.w(TAG, "-- 显示 结果有没有需要删除的数据：${boo}")
//                    if (boo) {
//
//                        advertPer!!.refreshImgFileData(imgFiles);
//                    }
//                }
//                     */
//            }
//        }
//
//        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
////            Log.w(TAG , "--切换 onScrolled ：${dx}  - ${dy}" )
//        }
//
//        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
////            Log.d(TAG , "--切换 onScrollStateChanged ：${newState}"  )
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
//        ll_video!!.visibility = View.VISIBLE
//        iv_video!!.visibility = View.VISIBLE
//
//
//        //播放完成回调
//        mVideoView!!.setOnCompletionListener(mCompletionListener)
//        mVideoView!!.setOnPreparedListener(mPreparedListener)
//        mVideoView!!.setOnInfoListener( mInfoListener)
//        mVideoView!!.setOnErrorListener ( mOnErrorListener)
//
//        //设置视频路径
//        mVideoView!!.setVideoPath(data.path)
//
//        //开始播放视频
//        mVideoView!!.start()
//    }
//
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
//    internal var mOnErrorListener: MediaPlayer.OnErrorListener =  object : MediaPlayer.OnErrorListener {
//        override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
//            Log.i(TAG, "播放 错误 what: $what - extra:$extra")
//            return true
//        }
//    }
//
//    //播放结束
//    internal var mCompletionListener: MediaPlayer.OnCompletionListener = object : MediaPlayer.OnCompletionListener {
//        override fun onCompletion(mp: MediaPlayer?) {
//                    Log.i(TAG, "播放 结束")
//            ll_video!!.visibility = View.GONE
//            convenientBanner!!.startTurning()
//            var count: Int =  convenientBanner!!.getCurrentItem()
//            convenientBanner!!.setCurrentItem( count+1 , true )
//        }
//    }
//
//    // 播放缓存
//    internal var mInfoListener: MediaPlayer.OnInfoListener = object : MediaPlayer.OnInfoListener{
//        override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
//            Log.i(TAG, "视频信息 OnInfoListener " + what )
//            when(what){
//                MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
//                    Log.i(TAG, "视频信息 MEDIA_INFO_VIDEO_RENDERING_START")
//                    Handler().postDelayed({ iv_video!!.visibility = View.GONE  }, 100)
//                }
//                MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
//                    Log.i(TAG, "视频信息 MEDIA_INFO_BUFFERING_END")
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
//    fun refreshImagePages(adList : MutableList<AdvertInfoBean>){
//        this.imgFiles = adList
//        if(mVideoView != null && mVideoView!!.isPlaying){
//            mVideoView!!.stopPlayback()
//        }
//        ll_video!!.visibility = View.GONE
//
//        if(convenientBanner != null) {
//            Log.i(TAG, "重新刷新广告之后的个数：" + adList.toList().size)
//            convenientBanner!!.setListData(adList.toList())
//            convenientBanner!!.startTurning()
////            convenientBanner!!.setCanLoop(false)
////            convenientBanner!!.setPointViewVisible(false)
////            convenientBanner!!.stopTurning()
//        } else {
//            initView()
//        }
//    }
//
//    /**
//     * 处理点击事件
//     */
//    override fun onClick(v: View?) {
//        when (v!!.id){
//            R.id.btn_sdcard -> {
//// 发送广播来拷贝文件
////                sendBroadcastSdcard()
//                // 测试切换广告
////                refreshImagePages()
//            }
//            R.id.iv_logo -> {
//                // logo 点击事件
//                Log.w( TAG , "设备版本号：" + advertPer!!.getVersionName() )
//                // 打开检查更新
//                advertPer!!.initDownload()
//            }
//        }
//    }
//
//    /**
//     * 点击按钮发送广播拷贝sdcard中的文件
//     */
//    fun sendBroadcastSdcard(){
//        val intentReceiver = Intent()
//        intentReceiver.action = AUSBBroadCastReceiver.ACTION_USB_COPY_FILE
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
//        // 关闭串口
//        advertPer!!.closeSerialPort()
//        // 关闭广告网络请求接口
//        advertPer!!.stopADState()
//        // 关闭天气网络请求接口
//        advertPer!!.stopReadWeatherState()
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
//                Log.i(TAG , "2-> 时间到了1点通知更新" )
//                // 打开检查更新
//                advertPer!!.initDownload()
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
//                if(convenientBanner != null ) {
//                    convenientBanner!!.stopTurning()
//                }
//            }
//            2 -> {
//                setDownloadProgress(event.progress)
//            }
//            3 -> {
//                Log.i(TAG , "两个文件拷贝完成" )
//                dismissDownloadProgress()
//                /**文件拷贝成功了，开始解析转换广告对象*/
//                advertPer!!.successAdvertList()
//
//            }
//            4 -> {
//                Log.i(TAG , "拷贝文件出错" )
//                showToast("拷贝文件出错")
//                dismissDownloadProgress()
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
////        if (progress >= 100) {
////            pd!!.dismiss()
////        } else {
//            pd!!.setProgress(progress)//设置进度条的当前进度
////        }
//    }
//
//    /**
//     * 取消进度条
//     */
//    fun dismissDownloadProgress( ) {
//        pd!!.dismiss()
//    }
//
//    override fun refreshAdList(advertListBean: MutableList<AdvertInfoBean>) {
//        imgFiles = advertListBean
//        refreshImagePages(advertListBean)
//    }
//
//    override fun getWeather_stat(): TextView {
//        return tv_weather_stat
//    }
//
//    override fun getWeather_status(): ImageView {
//        return iv_weather_status
//    }
//
//    override fun getWeather_temp(): TextView {
//        return tv_weather_temp
//    }
//
//    override fun getWeather_time(): TextView {
//        return tv_weather_time
//    }
//
//    override fun getWeather_week(): TextView {
//        return tv_weather_week
//    }
//
//    override fun getWerther_date(): TextView {
//        return tv_weather_date
//    }
//    override fun getWeather_stat2(): TextView {
//        return tv_weather_stat2
//    }
//
//    override fun getWeather_stat3(): TextView {
//        return tv_weather_stat3
//    }
//
//    override fun getWeather_stat1(): TextView {
//        return tv_weather_stat1
//    }
//
//    override fun requestAdList(advertListBean: MutableList<AdvertInfoBean>) {
//        advertPer!!.requestSuccessAdvertList(advertListBean)
//    }
//
//
//}
