package ad.vipcare.com.download;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;

import ad.vipcare.com.util.LogPlus;

/**
 * 检测系统是否禁用了下载权限
 * Created by zeting
 * Date 19/2/28.
 */

public class DownloadManagerResolver {
    private static final String DOWNLOAD_MANAGER_PACKAGE_NAME = "com.android.providers.downloads";

    /**
     * Resolve whether the DownloadManager is enable in current devices.
     *
     * @return true if DownloadManager is enable,false otherwise.
     */
    public static boolean resolve(Context context) {
        boolean enable = resolveEnable(context);
        LogPlus.sd("下载管理器状态：" + enable );
        if (!enable) {
            AlertDialog alertDialog = createDialog(context);
            alertDialog.show();
        }
        return enable;
    }

    /**
     * Resolve whether the DownloadManager is enable in current devices.
     *
     * @param context
     * @return true if DownloadManager is enable,false otherwise.
     */
    private static boolean resolveEnable(Context context) {
        try {
            int state = context.getPackageManager().getApplicationEnabledSetting(DOWNLOAD_MANAGER_PACKAGE_NAME);
            LogPlus.sd("下载管理器状态-state：" + state );
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                return !(state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
                        state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                        || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED);
            } else {
                return !(state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
                        state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false ;
        }
    }

    private static AlertDialog createDialog(final Context context) {
        AppCompatTextView messageTextView = new AppCompatTextView(context);
        messageTextView.setTextSize(16f);
        messageTextView.setText("DownloadManager is disabled. Please enable it.");
        return new AlertDialog.Builder(context)
                .setView(messageTextView, 50, 30, 50, 30)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        enableDownloadManager(context);
                    }
                })
                .setCancelable(false)
                .create();
    }

    /**
     * Start activity to Settings to enable DownloadManager.
     */
    private static void enableDownloadManager(Context context) {
        try {
            //Open the specific App Info page:
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + DOWNLOAD_MANAGER_PACKAGE_NAME));
            context.startActivity(intent);
            LogPlus.sd("启动下载管理器" );
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();

            //Open the generic Apps page:
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            context.startActivity(intent);
            LogPlus.sd("启动下载管理器错误-" , e );
        } catch (Exception e){
            e.printStackTrace();
            LogPlus.sd("启动下载管理器错误--" , e );
        }
    }
}
