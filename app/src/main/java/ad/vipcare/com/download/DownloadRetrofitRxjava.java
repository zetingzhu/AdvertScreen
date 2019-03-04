package ad.vipcare.com.download;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import ad.vipcare.com.eventbus.DownloadEvent;
import ad.vipcare.com.retrofit.ApiRetrofitDownload;
import ad.vipcare.com.retrofit.ApiServer;
import ad.vipcare.com.util.LogPlus;
import ad.vipcare.com.util.MatchUtil;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

import static ad.vipcare.com.download.DownloadServise.APKFILENAME;

/**
 * Created by zeting
 * Date 19/2/28.
 */

public class DownloadRetrofitRxjava {
    private static final String TAG = DownloadRetrofitRxjava.class.getSimpleName() ;

    protected ApiServer apiServer;

    private String saveFileDir;
    private DownloadFileListener listener;
    private Disposable mDisposable ;
    // 是否下载完成
    private boolean  isDownloadSucces = false ;

    private Context mContext ;
    public DownloadRetrofitRxjava( Context con) {
        this.mContext = con ;
        this.listener = new DownloadFileListener() {
            @Override
            public void onFail(Exception e) {
                LogPlus.d("apk 文件下载 失败：" + e.getLocalizedMessage());
                isDownloadSucces = false ;
            }
            @Override
            public void onSuccess() {
                if (isDownloadSucces){
                    LogPlus.d("apk 文件下载 成功");
                    installApk(mContext);
                }else {
                    LogPlus.d("apk 文件下载 失败");
                }
            }
            public void onProgress(long progress, long total, long speed, boolean done) {
//                LogPlus.i(TAG , "apk 文件下载进度 - progress：" + progress + " - total:" + total + " - speed:" + speed + " - done:" + done);
                isDownloadSucces = done ;
                installApkProgress(progress , total );
            }
        };
        apiServer = ApiRetrofitDownload.getInstance().getApiService(listener);
    }

    public DownloadRetrofitRxjava(DownloadFileListener downlistener ) {
        this.listener = downlistener;
        apiServer = ApiRetrofitDownload.getInstance().getApiService(listener);
    }

    public void saveFiles(ResponseBody responseBody) {
        InputStream inputStream = responseBody.byteStream();

        File fileDir = new File(saveFileDir);
        if(!fileDir.exists()){

            fileDir.mkdirs();
        }
        File file=new File(saveFileDir , APKFILENAME );

        writeFile(inputStream, file);
    }

    /**
     * 将输入流写入文件
     *
     * @param inputString
     * @param file
     */
    private void writeFile(InputStream inputString, File file) {
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);

            byte[] b = new byte[2048];

            int len;
            while ((len = inputString.read(b)) != -1) {
//                LogPlus.w("目前读取到的文件长度为多少：" + len );
                fos.write(b, 0, len);
            }
            inputString.close();
            fos.close();
//            LogPlus.d("什么时候才会把文件写完来");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            listener.onFail(e);
        } catch (IOException e) {
            e.printStackTrace();
            listener.onFail(e);
        } catch (Exception e) {
            e.printStackTrace();
            listener.onFail(e);
        }
    }

    /**
     * 保存文件
     */
    public void saveFiles(ResponseBody responseBody , int sign){

        InputStream inputStream = null;

        FileOutputStream fileOutputStream = null;

        byte[] buffer=new byte[2048];

        int len;

        File fileDir = new File(saveFileDir);
        if(!fileDir.exists()){

            fileDir.mkdirs();
        }
        File file=new File(saveFileDir , APKFILENAME );

        try {
            inputStream=responseBody.byteStream();

            fileOutputStream=new FileOutputStream(file);

            while ((len=inputStream.read(buffer))!=-1){

                fileOutputStream.write(buffer,0,len);
            }

            inputStream.close();

            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 下载文件
     * @param downloadUrl
     * @param saveFileName
     */
    public Disposable downLoadApkFils(@NonNull String downloadUrl, String saveFileName) {
        EventBus.getDefault().post(new DownloadEvent(1 , "下载文件"));
        this.saveFileDir = saveFileName;
        Observer<ResponseBody> observable =  apiServer.download(downloadUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io()) // 用于计算任务
                .doOnNext(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody body) throws Exception {
                        saveFiles(body);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        LogPlus.d(" -onSubscribe- ");
                        mDisposable = d ;
                    }

                    @Override
                    public void onNext(@NonNull ResponseBody body) {
                        LogPlus.d(" -onNext- ");
                        listener.onSuccess();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogPlus.d(" -onError- ");
                    }

                    @Override
                    public void onComplete() {
                        LogPlus.d(" -onComplete- ");
                    }
                });
        return mDisposable ;
    }

    /**
     * 安装以及下载完成的apk文件
     * @param context
     */
    public void installApk(Context context) {
        EventBus.getDefault().post(new DownloadEvent(5));
        ApkInstallUtil apkIns = new ApkInstallUtil();
        apkIns.installApk(context);
    }

    public void installApkProgress(long progress, long total) {
        try {
            int pro = MatchUtil.getProgress(progress , total , 2);
            EventBus.getDefault().post(new DownloadEvent(2 , pro));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
