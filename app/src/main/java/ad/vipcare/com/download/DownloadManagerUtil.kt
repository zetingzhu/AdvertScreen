package ad.vipcare.com.download


import ad.vipcare.com.util.MatchUtil
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import javax.xml.datatype.DatatypeConstants.SECONDS
import android.database.ContentObserver
import android.database.Cursor
import android.os.Handler
import android.os.Message
import android.util.Log
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * 说明：下载管理器
 */
//class DownloadManagerUtil(private val mContext: Context) {
//    private val TAG = "Download/MU"
//
//    private var downloadObserver: DownloadChangeObserver? = null
//    private var dm : DownloadManager ?= null
//
//    private var scheduledExecutorService: ScheduledExecutorService? = null
//    private var downloadId: Long = 0
//    private val HANDLE_DOWNLOAD = 0x001
//    /**
//     * 可能会出错Cannot update URI: content://downloads/my_downloads/-1
//     * 检查下载管理器是否被禁用
//     */
//    fun checkDownloadManagerEnable():Boolean {
//        try {
//            val state = mContext.packageManager.getApplicationEnabledSetting("com.android.providers.downloads")
//            if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
//                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
//                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
//                val packageName = "com.android.providers.downloads"
//                try {
//                    val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                    intent.data = Uri.parse("package:$packageName")
//                    mContext.startActivity(intent)
//                } catch (e: ActivityNotFoundException) {
//                    e.printStackTrace()
//                    val intent = Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
//                    mContext.startActivity(intent)
//                }
//                return false
//            }
//        } catch (e:Exception) {
//            e.printStackTrace()
//            return false
//        }
//        return true
//    }
//
//    fun download(url: String, title: String, desc: String): Long {
//        val uri = Uri.parse(url)
//        val req = DownloadManager.Request(uri)
//
//        // 设置下载监听进度
//        downloadObserver = DownloadChangeObserver()
//        registerContentObserver()
//        //设置允许使用的网络类型，这里是移动网络和wifi都可以
//        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
//        //下载中和下载完后都显示通知栏
//        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//
//        //设置文件的保存的位置[三种方式]
//        // 第一种 file:///storage/emulated/0/Android/data/your-package/files/Download/update.apk
//        req.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS, "$title.apk")
//        //第二种 file:///storage/emulated/0/Download/update.apk
////        req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "update.apk")
//        //第三种 自定义文件路径
////        req.setDestinationUri()
//
//        //禁止发出通知，既后台下载
////        req.setShowRunningNotification(false);
//        //通知栏标题
//        req.setTitle(title)
//        //通知栏描述信息
//        req.setDescription(desc)
//        //设置类型为.apk
//        req.setMimeType("application/vnd.android.package-archive")
//        // 设置为可被媒体扫描器找到
//        req.allowScanningByMediaScanner()
//        // 设置为可见和可管理
//        req.setVisibleInDownloadsUi(true)
//        //获取下载任务ID
//        dm = mContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//        try {
//            downloadId =  dm!!.enqueue(req)
//        } catch (e: Exception) {
//            Toast.makeText(mContext, "找不到下载文件", Toast.LENGTH_SHORT).show()
//            return -1
//        }
//        return downloadId
//    }
//
//    /**
//     * 下载前先移除前一个任务，防止重复下载
//     *
//     * @param downloadId
//     */
//    fun clearCurrentTask(downloadId: Long) {
//        val dm = mContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//        try {
//            dm.remove(downloadId)
//        } catch (ex: IllegalArgumentException) {
//            ex.printStackTrace()
//        }
//    }
//
//
//    /**
//     * 通过query查询下载状态，包括已下载数据大小，总大小，下载状态
//
//     * @param downloadId
//     * *
//     * @return
//     */
//    private fun getBytesAndStatus(downloadId: Long): IntArray {
//        val bytesAndStatus = intArrayOf(-1, -1, 0)
//        val query = DownloadManager.Query().setFilterById(downloadId)
//        var cursor: Cursor? = null
//        try {
//            cursor = dm!!.query(query)
//            if (cursor != null && cursor!!.moveToFirst()) {
//                //已经下载文件大小
//                bytesAndStatus[0] = cursor!!.getInt(cursor!!.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
//                //下载文件的总大小
//                bytesAndStatus[1] = cursor!!.getInt(cursor!!.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
//                //下载状态
//                bytesAndStatus[2] = cursor!!.getInt(cursor!!.getColumnIndex(DownloadManager.COLUMN_STATUS))
//            }
//        } finally {
//            if (cursor != null) {
//                cursor!!.close()
//            }
//        }
//        return bytesAndStatus
//    }
//
//
//    /**
//     * 发送Handler消息更新进度和状态
//     * 将查询结果从子线程中发往主线程（handler方式），以防止ANR
//     */
//    private fun updateProgress() {
//        Log.w(TAG , "开始监听 " )
//        val bytesAndStatus = getBytesAndStatus(downloadId)
//        downLoadHandler.sendMessage(downLoadHandler.obtainMessage(HANDLE_DOWNLOAD, bytesAndStatus[0], bytesAndStatus[1], bytesAndStatus[2]))
//    }
//
//    var downLoadHandler: Handler = object : Handler() { //主线程的handler
//        override fun handleMessage(msg: Message) {
//            super.handleMessage(msg)
//            if ( HANDLE_DOWNLOAD === msg.what) {
//                Log.w(TAG , "下载状态：" +  msg.obj )
//                //被除数可以为0，除数必须大于0
//                if (msg.arg1 >= 0 && msg.arg2 > 0) {
////                    onProgressListener.onProgress(msg.arg1 / msg.arg2 as Float)
//                    var pro = MatchUtil.getProgress(msg.arg1.toDouble(), msg.arg2.toDouble(), 2) ;
//                    Log.w(TAG , "下载进度：${msg.arg1} - ${ msg.arg2 } - ${pro}"  )
//                    if (pro >= 100){
//                        unregisterContentObserver()
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * 监听下载进度
//     */
//    private inner class DownloadChangeObserver : ContentObserver(downLoadHandler) {
//        init {
//            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
//        }
//
//        /**
//         * 当所监听的Uri发生改变时，就会回调此方法
//         * @param selfChange 此值意义不大, 一般情况下该回调值false
//         */
//        override fun onChange(selfChange: Boolean) {
//            Log.w(TAG , "监听有没有改变：" + selfChange )
//            scheduledExecutorService!!.scheduleAtFixedRate(progressRunnable, 0, 1, TimeUnit.SECONDS) //在子线程中查询
//        }
//    }
//
//    private val progressRunnable = Runnable { updateProgress()
//    }
//
//    /**
//     * 注册ContentObserver
//     */
//    private fun registerContentObserver() {
//        /** observer download change  */
//        if (downloadObserver != null) {
//            mContext.getContentResolver().registerContentObserver(  Uri.parse("content://downloads/my_downloads"), false, downloadObserver)
//        }
//    }
//
//    /**
//     * 注销ContentObserver
//     */
//    private fun unregisterContentObserver() {
//        if (scheduledExecutorService != null) {
//            scheduledExecutorService!!.shutdownNow()
//            scheduledExecutorService = null
//        }
//        if (downloadObserver != null) {
//            mContext.getContentResolver().unregisterContentObserver(downloadObserver)
//        }
//    }
//
//}