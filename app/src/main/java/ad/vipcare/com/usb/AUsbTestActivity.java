package ad.vipcare.com.usb;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.UsbFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import ad.vipcare.com.advertscreen.R;
import ad.vipcare.com.util.FileAdvertMd5;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zeting
 * Date 19/1/9.
 */

public class AUsbTestActivity extends AppCompatActivity implements AUSBBroadCastReceiver.UsbListener {


    private static final String TAG = "USB/TA";

    @BindView(R.id.local_backspace_iv)
    ImageButton localBackspaceIv;
    @BindView(R.id.local_file_lv)
    ListView localFileLv;
    @BindView(R.id.usb_backspace_iv)
    ImageButton usbBackspaceIv;
    @BindView(R.id.usb_file_lv)
    ListView usbFileLv;
    @BindView(R.id.show_progress_tv)
    TextView showProgressTv;
    @BindView(R.id.btn_send)
    Button btnSend ;

    //本地文件列表相关
    private ArrayList<File> localList;
    private AFileListAdapter<File> localAdapter;
    private String localRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String localCurrentPath = "";
    //USB文件列表相关
    private ArrayList<UsbFile> usbList;
    private AFileListAdapter<UsbFile> usbAdapter;
    private AUsbHelper usbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb_test);
        ButterKnife.bind(this);
        initLocalFile();
        initUsbFile();

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide() ;
        md5FileTest();
        requestPermission() ;
    }

    /**
     * 初始化本地文件列表
     */
    private void initLocalFile() {
        localList = new ArrayList<>();
        Collections.addAll(localList, new File(localRootPath).listFiles());
        localCurrentPath = localRootPath;
        localAdapter = new AFileListAdapter<>(this, localList);
        localFileLv.setAdapter(localAdapter);
        localFileLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openLocalFile(localList.get(position));
            }
        });
    }

    /**
     * 初始化 USB文件列表
     */
    private void initUsbFile() {
        usbHelper = new AUsbHelper(this, this);
        usbHelper.registerReceiver();
        usbList = new ArrayList<>();
        usbAdapter = new AFileListAdapter<>(this, usbList);
        usbFileLv.setAdapter(usbAdapter);
        usbFileLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UsbFile file = usbList.get(position);
                openUsbFile(file);
            }
        });
        updateUsbFile(0);
    }

    /**
     * 打开本地 File
     *
     * @param file File
     */
    private void openLocalFile(File file) {
        if (file.isDirectory()) {
            //文件夹更新列表
            localList.clear();
            Collections.addAll(localList, file.listFiles());
            localAdapter.notifyDataSetChanged();
            localCurrentPath = file.getAbsolutePath();
        } else {
            //开启线程，将文件复制到本地
            copyLocalFile(file);
        }
    }

    /**
     * 更新 USB 文件列表
     */
    private void updateUsbFile(int position) {
        UsbMassStorageDevice[] usbMassStorageDevices = usbHelper.getDeviceList();
        if (usbMassStorageDevices.length > 0) {
            //存在USB
            usbList.clear();
            usbList.addAll(usbHelper.readDevice(usbMassStorageDevices[position]));
            usbAdapter.notifyDataSetChanged();
        } else {
            Log.e("UsbTestActivity", "No Usb Device");
            usbList.clear();
            usbAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 复制 USB 文件到本地
     *
     * @param file USB文件
     */
    private void copyLocalFile(final File file) {
        //复制到本地的文件路径
        new Thread(new Runnable() {
            @Override
            public void run() {
                //复制结果
                final boolean result = usbHelper.saveSDFileToUsb(file, usbHelper.getCurrentFolder(), new AUsbHelper.DownloadProgressListener() {
                    @Override
                    public void downloadProgress(final int progress) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String text = "From Local : " + localCurrentPath
                                        + "\nTo Usb : " + usbHelper.getCurrentFolder().getName()
                                        + "\nProgress : " + progress;
                                showProgressTv.setText(text);
                            }
                        });
                    }
                });
                //主线程更新UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result) {
                            openUsbFile(usbHelper.getCurrentFolder());
                        } else {
                            Toast.makeText(AUsbTestActivity.this, "复制失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 打开 USB File
     *
     * @param file USB File
     */
    private void openUsbFile(UsbFile file) {
        if (file.isDirectory()) {
            //文件夹更新列表
            usbList.clear();
            usbList.addAll(usbHelper.getUsbFolderFileList(file));
            usbAdapter.notifyDataSetChanged();
        } else {
            //开启线程，将文件复制到本地
            copyUSbFile(file);
        }
    }

    /**
     * 复制 USB 文件到本地
     *
     * @param file USB文件
     */
    private void copyUSbFile(final UsbFile file) {
        //复制到本地的文件路径
        final String filePath = localCurrentPath + File.separator + file.getName();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //复制结果
                final boolean result = usbHelper.saveUSbFileToLocal(file, filePath, new AUsbHelper.DownloadProgressListener() {
                    @Override
                    public void downloadProgress(final int progress) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String text = "From Usb " + usbHelper.getCurrentFolder().getName()
                                        + "\nTo Local " + localCurrentPath
                                        + "\n Progress : " + progress;
                                showProgressTv.setText(text);
                            }
                        });
                    }
                });
                //主线程更新UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result) {
                            openLocalFile(new File(localCurrentPath));
                        } else {
                            Toast.makeText(AUsbTestActivity.this, "复制失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.local_backspace_iv:
                if (!localCurrentPath.equals(localRootPath)) {
                    //不是根目录，返回上一目录
                    openLocalFile(new File(localCurrentPath).getParentFile());
                }
                break;
            case R.id.usb_backspace_iv:
                if (usbHelper.getParentFolder() != null) {
                    //不是根目录，返回上一目录
                    openUsbFile(usbHelper.getParentFolder()
                    );
                }
                break;
            case R.id.btn_send:
                // 发送广播
                Intent intentReceiver = new Intent();
                intentReceiver.setAction(AUSBBroadCastReceiver.ACTION_USB_COPY_FILE);
                sendBroadcast(intentReceiver);
//                bindUsbService(AUsbTestActivity.this);
                break;
        }
    }
    CUsbReadService usbReadService ;
//    public void bindUsbService(Context mCon ){
//        ServiceConnection mServiceConnection = new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName name, IBinder service) {
//                Log.d(TAG , "绑定服务成功");
//                usbReadService = ((CUsbReadService.MyIBinder) service).getService();
//                // 调用拷贝文件
//                usbReadService.copyUsbFile();
//            }
//
//            @Override
//            public void onServiceDisconnected(ComponentName name) {
//                Log.d(TAG , "绑定服务失败");
//            }
//        };
//        Intent gattServiceIntent = new Intent(mCon , CUsbReadService.class);
//        boolean boo =  mCon.getApplicationContext().bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
//        Log.d(TAG , "绑定服务boo:" + boo );
//    }

    @Override
    public void insertUsb(UsbDevice device_add) {
        if (usbList.size() == 0) {
            updateUsbFile(0);
        }
    }

    @Override
    public void removeUsb(UsbDevice device_remove) {
        updateUsbFile(0);
    }

    @Override
    public void getReadUsbPermission(UsbDevice usbDevice) {

    }

    @Override
    public void failedReadUsb(UsbDevice usbDevice) {

    }

    @Override
    protected void onDestroy() {
        usbHelper.finishUsbHelper();
        super.onDestroy();
    }


    /**
     * 验证文件md5
     */
    public void md5FileTest(){
        String pathRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        String copyFile = pathRoot + File.separator + "Download" + File.separator + "md5FileTest.zip" ;
        long oldTime = System.currentTimeMillis() ;
        String fileMd5  = FileAdvertMd5.getMyFileMD5(copyFile) ;
        Log.d(TAG , "文件md5:" + fileMd5 + " -时间：" + (System.currentTimeMillis() - oldTime));
    }

    /**
     * 请求窗口权限
     */
    public void requestPermission (){
        if (Build.VERSION.SDK_INT >= 23) {
            if (! Settings.canDrawOverlays(AUsbTestActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent,10);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(this)) {
                    // SYSTEM_ALERT_WINDOW permission not granted...
                    Toast.makeText(AUsbTestActivity.this,"not granted",Toast.LENGTH_SHORT);
                }
            }
        }
    }
}