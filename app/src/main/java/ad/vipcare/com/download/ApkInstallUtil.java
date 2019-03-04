package ad.vipcare.com.download;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import ad.vipcare.com.util.LogPlus;
import ad.vipcare.com.util.RootUtil;

import static ad.vipcare.com.download.DownloadServise.APKFILENAME;

/**
 * 安装apk工具
 * Created by zeting
 * Date 19/3/1.
 */

public class ApkInstallUtil {
    private static final String TAG = ApkInstallUtil.class.getSimpleName();

    public ApkInstallUtil() {
    }

    public void installApk(Context context ) {
        File file = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS + "/" + APKFILENAME );
        String path = file.getAbsolutePath();
        LogPlus.sd(TAG , "apk安装文件路径：" + path );
        if (isRoot()){
            LogPlus.sd(TAG , "有root 权限，静默安装");
            boolean boo = installRootApk(path);
            LogPlus.sd(TAG , "安装应用：" + boo );
        } else {
            LogPlus.sd(TAG , "没有root 权限，智能安装安装");
            installAutoApk(context , path) ;
        }
    }
    /**
     * 智能安装
     * @param context
     */
    public void installAutoApk(Context context, String apkPath) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        LogPlus.sd(TAG , "安装文件路径：" + apkPath);
        Uri downloadFileUri = Uri.parse("file://" + apkPath);
        intent.setDataAndType( downloadFileUri , "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 判断手机是否拥有Root权限。
     * @return 有root权限返回true，否则返回false。
     */
    public boolean isRoot() {
        boolean bool = false;
        bool = RootUtil.isPhoneRoot();
        // 如果有root目录就在确定一下
        if (bool){
            bool = RootUtil.getAppRoot();
        }
        return bool;
    }

    /**
     * 静默安装
     * @param apkPath apk文件路径
     */
    public boolean installRootApk(String apkPath) {
        boolean result = false;
        DataOutputStream dataOutputStream = null;
        BufferedReader errorStream = null;
        try {
            Process process = Runtime.getRuntime().exec("su");//申请root权限
            dataOutputStream = new DataOutputStream(process.getOutputStream());

            String command = "pm install -r " + apkPath + "\n";//拼接 pm install 命令，执行。-r表示若存在则覆盖安装
            LogPlus.sd(TAG , "执行root安装命令：" + command );
            dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
            dataOutputStream.flush();
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            process.waitFor();//安装过程是同步的，安装完成后再读取结果
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String message = "";
            String line;
            while ((line = errorStream.readLine()) != null) {
                message += line;
            }
            LogPlus.sd(TAG , "silentInstall" + message);
            if (!message.contains("Failure")) {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogPlus.sd(TAG , "静默安装应用错误" , e);
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            LogPlus.sd(TAG , "静默安装应用结束" );
        }

        return result;
    }
}
