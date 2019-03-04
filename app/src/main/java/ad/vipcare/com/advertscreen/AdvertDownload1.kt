package ad.vipcare.com.advertscreen

import ad.vipcare.com.download.DownloadServise
import ad.vipcare.com.usb.CUsbReadService
import android.content.*
import android.os.IBinder
import android.widget.Toast
import com.github.mjdev.libaums.fs.UsbFile
import io.vov.vitamio.utils.Log
import android.app.ProgressDialog



/**
 * 下载类
 * Created by zeting
 * Date 19/1/23.
 */

//open class AdvertDownload : AdvertBasePersenter {
//    private val TAG = "Download"
//    private var downloadBroadcastReceiver: DownloadReceiver? = null
////    private val url = "https://downpack.baidu.com/appsearch_AndroidPhone_v8.0.3(1.0.65.172)_1012271b.apk"
//    private val url = "https://ali-fir-pro-binary.fir.im/3784f76dac9ed340b4e8f7bd0c54bf67bd5d5dd2.apk?auth_key=1548233508-0-0-60849ef51efe5f29f7a610f8c508e599"
//    private val title = "下载"
//    private val desc = "下载"
//
//
//    constructor(con: Context?) : super(con) {
//        this.mContext = con
//    }
//
//    fun initDownload(){
//
////        val dm = DownloadManagerUtil(mContext)
////        if (dm.checkDownloadManagerEnable()) {
//////            if (MyApplication.getInstance().downloadId != 0L) {
////                dm.clearCurrentTask(MyApplication.getInstance().downloadId) // 先清空之前的下载
//////            }
////            Log.d(TAG , "开始下载")
////            MyApplication.getInstance().downloadId = dm.download(url, title, desc)
////        } else {
////            Toast.makeText(mContext, "请开启下载管理器", Toast.LENGTH_SHORT).show()
////        }
//
//        bindDownloadService(mContext)
//
//    }
//
//    // 拷贝文件服务
//    private var mServiceConnection: ServiceConnection? = null
//    private var usbReadService: DownloadServise? = null
//
//    fun bindDownloadService(mCon: Context ) {
//        mServiceConnection = object : ServiceConnection {
//            override fun onServiceConnected(name: ComponentName, service: IBinder) {
//                android.util.Log.d(TAG, "绑定服务成功")
//                usbReadService = (service as DownloadServise.DownloadBinder).service
//            }
//
//            override fun onServiceDisconnected(name: ComponentName) {
//                android.util.Log.d(TAG, "绑定服务失败")
//            }
//        }
//        val gattServiceIntent = Intent(mCon, CUsbReadService::class.java)
//        gattServiceIntent.putExtra( DownloadServise.BUNDLE_KEY_DOWNLOAD_URL , url )
//        mCon.bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)
//    }
//
//    fun register(){
//        downloadBroadcastReceiver = DownloadReceiver()
//        val intentFilter = IntentFilter()
//        intentFilter.addAction("android.intent.action.DOWNLOAD_COMPLETE")
//        intentFilter.addAction("android.intent.action.DOWNLOAD_NOTIFICATION_CLICKED")
//        mContext.registerReceiver(downloadBroadcastReceiver, intentFilter)
//
//    }
//
//    fun unregister(){
//        mContext.unregisterReceiver(downloadBroadcastReceiver)
//    }
//
//
//    private val progress: ProgressDialog? = null
//    private val isBindService: Boolean = false
//
//
//
//
//}
