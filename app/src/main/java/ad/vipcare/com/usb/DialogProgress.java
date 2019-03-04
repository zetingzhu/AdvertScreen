package ad.vipcare.com.usb;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import ad.vipcare.com.advertscreen.R;

/**
 * 拷贝进去
 * Created by zeting
 * Date 19/1/10.
 */

public class DialogProgress extends ProgressDialog {


    public DialogProgress(Context context) {
//        super(context , R.style.kdialog);
        super(context);
    }

    public DialogProgress(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
//        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        this.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }


    private void applyCompat() {
        if (Build.VERSION.SDK_INT < 19) {
            return;
        }
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    /**
     * 显示下载进度条
     */
    public void setDialogShow(String title){
        setTitle(title );//设置一个标题
        setMax(100);//设置进度条的最大值
        setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);;
        setProgress(0);//设置进度条的当前进度
        setCancelable(true);//这是是否可撤销/也就是这个对话框是否可以关闭
        setIndeterminate(false);//设置是否是确定值
        show();//展示对话框
    }

}
