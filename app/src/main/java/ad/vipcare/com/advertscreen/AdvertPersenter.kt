package ad.vipcare.com.advertscreen

import ad.vipcare.com.bean.AdvertInfoBean
import ad.vipcare.com.bean.AdvertsListBean
import ad.vipcare.com.bean.AdvertsMd5Anqi
import ad.vipcare.com.usb.AUSBBroadCastReceiver
import ad.vipcare.com.util.*
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log

import android.widget.Toast
import com.google.gson.Gson


import java.io.*
import java.text.SimpleDateFormat


/**
 * 广告界面操作类
 * Created by zeting
 * Date 19/1/14.
 */

class AdvertPersenter : AdvertUsbHelper {

    private val TAG = "AdvertPersenter/AD"

    private val pathRoot = Environment.getExternalStorageDirectory().absolutePath
    // 拷贝文件地址目录
    private val filePath = pathRoot + File.separator + "DCIM" + File.separator + "Camera"
    // 拷贝成功后的文件
    private val pasteFile = pathRoot + File.separator + "anqiad" + File.separator + "anqifile.zip"
    // 解压后的文件目录
    private val unZipfilePath = pathRoot + File.separator + "anqiadunzip"
    // 解析的json 文件为准
    private val pasteJson = pathRoot + File.separator + "anqiad" + File.separator + "anqijson.txt"
    // 解析文件对象
    private var advertListBean : AdvertsListBean = AdvertsListBean()
    private var gson : Gson = Gson()
    // 广播
    private var mUsbReceiver : AUSBBroadCastReceiver ?= null

    private var mContext: Context?=null
//     界面操作接口
//    private var adView : AdvertView ?= null


    constructor(con: Context?) : super(con) {
        this.mContext = con
    }

//    fun setAdview(advertView : AdvertView ){
//        this.adView = advertView
//    }

    /**
     * 根据时间获取需要展示的文件名称
     */
    fun getAdvertForTime() : MutableList<File>? {
        var fileName : MutableList<File> = mutableListOf()
        // 获取系统时间
        var sysTime = System.currentTimeMillis()

        var localImagesFiles: File = FileUtils.getFileByPath(filePath + File.separator + "testa.jpg")

        fileName!!.add(localImagesFiles)

        return fileName
    }


    /**
     * 第一次读取有没有广告信息
     */
    fun fistReadAd(): MutableList<AdvertInfoBean>? {
        val file = File(pasteJson)
        if (file.exists()){
            var txtJson = ReadTxtFile(pasteJson)
            if ("".equals( txtJson )){

                return null
            }
            advertListBean = gson.fromJson( txtJson , AdvertsListBean::class.java )
            // 从组广告对象，确定找到每一个file
            var adList : MutableList<AdvertInfoBean>  = changeAdInfo(advertListBean)
            // 经过筛选
//            adList =  countTimeChangeAdInfo(adList)
            // 将组成好的广告对象传进去
            adView!!.refreshAdList( adList )
            return adList
        }else{
            return null
        }
    }


    /**
     * 文件拷贝成功了，开始解析转换广告对象
     * 新的不需要解析，只需要解压文件就好了
     */
    fun successAdvertList(){
        // 解析json文件
        var txtJson = ReadTxtFile(pasteJson)
        Log.i(TAG , "读取拷贝过来的json内容：" + txtJson )
        var mAqMd5 : AdvertsMd5Anqi = gson.fromJson( txtJson , AdvertsMd5Anqi::class.java )
        // 比较文件md5值
        var md5Anqi = FileAdvertMd5.getMyFileMD5(pasteFile)
        Log.i(TAG , "安骑文件的MD5值是：" + md5Anqi + "传给我的md5值：" + mAqMd5.anqiMd5 )
        if (!md5Anqi.equals(mAqMd5.anqiMd5)){
            showToast("拷贝文件校验错误，请更正后重新拷贝" , Toast.LENGTH_LONG)
            return
        }
        // 解压文件
        ZipUtils.unzipFile(pasteFile , unZipfilePath)

        // 刷新界面布局
        writeLog( "拷贝文件完成，刷新界面" )
        setAdbertData(oldAd)

//        Log.i(TAG , "组成的adList ：" + advertListBean.toString() )
//        // 从组广告对象，确定找到每一个file
//        var adList : MutableList<AdvertInfoBean>  = changeAdInfo(advertListBean)
//        // 经过筛选
//        adList =  countTimeChangeAdInfo(adList)
//        // 将组成好的广告对象传进去
//        adView!!.refreshAdList( adList )
    }


    /**
     * 网络请求成功后重组广告对象
     */
    fun requestSuccessAdvertList(listAd : MutableList<AdvertInfoBean>){
        advertListBean = AdvertsListBean()
        writeLog( "广告信息：" + advertListBean + " - " + listAd.size )
        if (advertListBean != null &&  listAd.size > 0) {
            var fileDir: File = File(unZipfilePath )
            var files = fileDir.listFiles()
            writeLog( "文件路径：" + fileDir.path + " - " + listAd.size )
            if (files == null ){
                return
            }
            for (f in files ){
                for ( adinfo in listAd){

                    try {
                        var fileN = f.name.split(".")[0]
                        if (fileN.equals(adinfo.adId)){
                            adinfo.fileName = f.name
                        }
                    } catch(e: Exception) {
                        e.printStackTrace()
                        Log.e( TAG ,"解析文件错误：")
                    }

                }
            }
            advertListBean.adverList = listAd
            // 从组广告对象，确定找到每一个file
            var adList: MutableList<AdvertInfoBean> = changeAdInfo(advertListBean)
            // 经过筛选
//            adList = countTimeChangeAdInfo(adList)
            // 将组成好的广告对象传进去
            adView!!.refreshAdList(adList)
        }
    }

    /**
     * 重组广告对象
     * 将文件的绝对路径保存近文件中去
     */
    fun changeAdInfo(adInfo : AdvertsListBean?) : MutableList<AdvertInfoBean> {
        var listAd : MutableList<AdvertInfoBean> = adInfo!!.adverList
        synchronized (listAd) {
//            for (i in listAd.indices) {
//                var adInfo = listAd.get(i)
            val iterator = listAd.iterator()
            while (iterator.hasNext()) {
                var adInfo = iterator.next()

                var file: File = File(unZipfilePath + File.separator + adInfo.fileName)
                // 如果文件不存在就删掉这条数据
                if (!file.exists()) {
                    Log.i(TAG, "这个文件不存在，我需要删除这一条数据： ${ adInfo.fileName } ")
//                    listAd.remove(adInfo)
                    iterator.remove()
                }
                adInfo.file = file
            }
        }
        return listAd
    }


    /**
     * 根据时间，次数重新生成广告显示列表
     */
    fun countTimeChangeAdInfo(listAd : MutableList<AdvertInfoBean>) : MutableList<AdvertInfoBean> {
        // 获取系统时间
        var systime = System.currentTimeMillis()
//        var sysDate= TimeUtils.millis2String( systime )
        var dayFormat = SimpleDateFormat("yyyy-MM-dd")
        var timeFormat = SimpleDateFormat("HH:mm")
        var sysHour = timeFormat.format( System.currentTimeMillis() )
        synchronized (listAd) {
            var ifShowTime = checkSetTime(listAd , systime ,sysHour ,dayFormat , timeFormat) // 是否有时间段显示的广告，如果有就异常所有的次数显示的
            Log.i(TAG, "countTimeChangeAdInfo 有没有在当前时间段显示的广告：${ifShowTime}" )
            val iterator = listAd.iterator()
            while (iterator.hasNext()) {
                var value = iterator.next()

                var startDay = TimeUtils.string2Millis(value.startDay, dayFormat)
                var endDay = TimeUtils.string2Millis(value.endDay, dayFormat)
                if (systime < startDay || systime > endDay) {
                    Log.i(TAG, "countTimeChangeAdInfo 如果有时间已经超出当前 日期 的数据数据  日期数据文件文件: ${value.fileName} 日期：${value.startDay} - ${value.endDay} ")
                    // 经过筛选
//                listAd.remove(value)
                    iterator.remove()
                } else {
                    if (value.playType == 0) {
                        // 时间段
                        var startTime = TimeUtils.string2Millis(value.startTime, timeFormat)
                        var endTime = TimeUtils.string2Millis(value.endTime, timeFormat)
                        var sysh = TimeUtils.string2Millis( sysHour  , timeFormat)
                        if (sysh < startTime || sysh > endTime) {
                            Log.i(TAG, "countTimeChangeAdInfo 如果有时间已经超出当前 时间点 的数据数据文件: ${value.fileName} 时间：${value.startTime} - ${value.endTime} ")
//                        listAd.remove(value)
                            iterator.remove()
                        }
                    } else if (value.playType == 1) {
                        // 次数
                        if(ifShowTime){
                            // 如果有时间段显示的就，直接移除所有的次数显示的，只保留时间段显示的
                            iterator.remove()
                            continue
                        }
                        var playCount = value.playCount
                        var showCount = value.showCount
                        if (playCount > showCount) {
                            Log.i(TAG, "countTimeChangeAdInfo 有次数超出播放次数的数据数据 - type 1  次数超过文件: ${value.fileName} 次数：${value.playCount}  --》 总显示次数：${ value.showCount } ")
//                        listAd.remove(value)
                            iterator.remove()
                        }
                    }
                }
            }
        }
      return listAd
    }


    /**
     * 验证是否有根据时间显示的，如果有就只要需要根据时间显示的
     */
    fun checkSetTime(listAd : MutableList<AdvertInfoBean> , systime :Long , sysHour: String ,  dayFormat :SimpleDateFormat , timeFormat :SimpleDateFormat   ): Boolean {
        for (value in listAd){
            if (value.startDay == null ){
                continue
            }
            var startDay = TimeUtils.string2Millis(value.startDay, dayFormat)
            var endDay = TimeUtils.string2Millis(value.endDay, dayFormat)
            if (systime < startDay || systime > endDay) {
                continue
            } else {
                if (value.playType == 0) {
                    // 时间段
                    var startTime = TimeUtils.string2Millis(value.startTime, timeFormat)
                    var endTime = TimeUtils.string2Millis(value.endTime, timeFormat)
                    var sysh = TimeUtils.string2Millis(sysHour, timeFormat)
                    if (sysh < startTime || sysh > endTime) {
                        continue
                    }else {
                        return true
                    }
                }
            }
        }
        return false
    }

    /**
     * 校验是不是列表需要更新
     */
    fun checkAdListInfo( imgFiles: MutableList<AdvertInfoBean> ) : Boolean {
        var systime = System.currentTimeMillis()
        var dayFormat = SimpleDateFormat("yyyy-MM-dd")
        var timeFormat = SimpleDateFormat("HH:mm")
        var sysDay = dayFormat.format( System.currentTimeMillis() )
        var sysHour = timeFormat.format( System.currentTimeMillis() )
        for (value in imgFiles) {
            var startDay = TimeUtils.string2Millis(value.startDay, dayFormat)
            var endDay = TimeUtils.string2Millis(value.endDay, dayFormat)
            if (systime < startDay || systime > endDay) {
                Log.i(TAG, "如果有时间已经超出当前 日期 的数据数据  日期数据文件文件: ${value.fileName} 日期：${value.startDay} - ${value.endDay} ")
                // 经过筛选
                return true
            } else {
                if (value.playType == 0) {
                    // 时间段
                    var startTime = TimeUtils.string2Millis( value.startTime  , timeFormat)
                    var endTime = TimeUtils.string2Millis( value.endTime  , timeFormat)
                    var sysh = TimeUtils.string2Millis( sysHour  , timeFormat)
                    if (sysh < startTime || sysh > endTime) {
                        Log.i(TAG, "如果有时间已经超出当前 时间点 的数据数据文件: ${value.fileName} 时间：${value.startTime} - ${value.endTime} ")
                        // 经过筛选
                        return true
                    }
                } else if (value.playType == 1) {
                    // 次数
                    var playCount = value.playCount
                    var showCount = value.showCount
                    if (playCount > showCount) {
                        Log.i(TAG, "有次数超出播放次数的数据数据 - type 1  次数超过文件: ${value.fileName} 次数：${value.playCount}  --》 总显示次数：${ value.showCount } ")
                        return true
                    }
                }
            }
        }
        return false
    }

    /**
     * 刷新文件数据，重新显示
     */
    fun refreshImgFileData( listAd : MutableList<AdvertInfoBean> ){
        // 经过筛选
        var adNewList : MutableList<AdvertInfoBean> =  countTimeChangeAdInfo(listAd)
        // 将组成好的广告对象传进去
        Log.i(TAG, " 条件成熟，需要手动刷新删除数据了 " + adNewList.size )
        adView!!.refreshAdList( adNewList )
    }

    /**
     * 保存文件的个数到sp中
     */
    fun saveFileCountSp(imgName : String , count : Int ){
        SPStaticUtils.put(imgName , count )
    }

    /**
     * 注册 USB 监听广播
     */
    fun registerReceiver(con: Context) {
        mUsbReceiver = AUSBBroadCastReceiver()
        mUsbReceiver!!.setUsbListener(usbListener)
        //监听otg插入 拔出
        val usbDeviceStateFilter = IntentFilter()
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        con.registerReceiver(mUsbReceiver, usbDeviceStateFilter)
        //注册监听自定义广播
        val filter = IntentFilter()
        filter.addAction(AUSBBroadCastReceiver.ACTION_USB_PERMISSION)
        filter.addAction(AUSBBroadCastReceiver.ACTION_USB_COPY_FILE)
        con.registerReceiver(mUsbReceiver, filter)

    }

    /**
     * 设置监听U盘权限
     */
    var usbListener : AUSBBroadCastReceiver.UsbListener = object: AUSBBroadCastReceiver.UsbListener {
        override fun failedReadUsb(usbDevice: UsbDevice?) {
            writeLog("读取USB信息失败")
        }

        override fun getReadUsbPermission(usbDevice: UsbDevice?) {
            writeLog("获取USB权限 成功 - 再次读取U盘信息")
            updateUsbFile(0)
        }

        override fun insertUsb(device_add: UsbDevice?) {
            writeError(mContext , "插入U盘")
//            Toast.makeText(this@ActivityAdvert , "插入U盘" , Toast.LENGTH_SHORT).show()
            updateUsbFile(0)
        }

        override fun removeUsb(device_remove: UsbDevice?) {
//            Toast.makeText(this@ActivityAdvert , "拔出U盘" , Toast.LENGTH_SHORT).show()
            writeError(mContext , "拔出U盘")
        }
    }


    /**
     * 申请磁盘权限
     */
    fun requestStoragePermiss() {
        // 申请权限的使用
        if (ContextCompat.checkSelfPermission( mContext!! , Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
            // 没有权限，申请权限。
            if (ActivityCompat.shouldShowRequestPermissionRationale( mContext as Activity, Manifest.permission.WRITE_EXTERNAL_STORAGE )) {
                // 用户拒绝过这个权限了，应该提示用户，为什么需要这个权限。
                Log.i(TAG, "用户拒绝过这个权限了，应该提示用户，为什么需要这个权限。")
            } else {
                Log.i(TAG, "申请权限")
                //  申请读取系统磁盘权限
                ActivityCompat.requestPermissions(mContext as Activity, arrayOf( Manifest.permission.WRITE_EXTERNAL_STORAGE ), 1101)
            }
        } else {
            // 有权限了，去放肆吧。
            Log.i(TAG, "应用已经有了读取磁盘权限")
        }
    }

    /**
     *  监听磁盘权限
     */
    fun onRequestSroragePermissions(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            1101 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限被用户同意，可以去放肆了。
                    Log.i(TAG, "申请权限 成功")
                } else {
                    // 权限被用户拒绝了，洗洗睡吧。
                    Log.i(TAG, "申请权限 失败")
                }
                return
            }
        }
    }


}
