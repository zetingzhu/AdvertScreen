package ad.vipcare.com.download;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by zeting
 * Date 19/2/28.
 */

public class ProgressHandler extends Handler {

    private WeakReference<Activity> mActivityWeakReference;

    public ProgressHandler(Activity activity){

        mActivityWeakReference=new WeakReference<Activity>(activity);

    }

    @Override
    public void handleMessage(Message msg) {

        if(mActivityWeakReference.get()!=null){

//            AmallLoadBean amallLoadBean= (AmallLoadBean) msg.obj;
//
//            long progress=amallLoadBean.getProgress();
//
//            long total=amallLoadBean.getTotal();
//
//            float cp=(float)progress/(float)total;

        }
    }
}