package ad.vipcare.com.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;


/**
 * root 权限获取
 * Created by zeting
 * Date 19/1/29.
 */

public class RootUtil {
    private static final String TAG = "RootUtil" ;

    /**
     * 判断当前手机是否有ROOT权限
     * @return
     */
    public static boolean isPhoneRoot() {
        boolean bool = false;

        try {
            if ((!new File("/system/bin/su").exists()) && (!new File("/system/xbin/su").exists())) {
                bool = false;
            } else {
                bool = true;
            }
            Log.d(TAG, "bool = " + bool);
        } catch (Exception e) {

        }
        return bool;
    }


    /**
     *
     * 判断应用是否有root权限
     */
    public static boolean getAppRoot() {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();
            int exitValue = process.waitFor();
            if (exitValue == 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.d("*** DEBUG ***", "Unexpected error - Here is what I know: "  + e.getMessage());
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     *  获取root全是
     */
    public boolean getRoot(Context mContext ) {
        String apkRoot = "chmod 777 " + mContext.getPackageCodePath();
        boolean booRoot = RootCommand(apkRoot);
        Log.i(TAG , "获取到应用是否可以获取root权限：" + booRoot );

//        Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c", cmd});

        return booRoot ;
    }

    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     *
     * @param command 命令：String apkRoot="chmod 777 "+getPackageCodePath(); RootCommand(apkRoot);
     * @return 应用程序是/否获取Root权限
     */
    public boolean RootCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            Log.d("*** DEBUG ***", "ROOT REE" + e.getMessage());
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        Log.d("*** DEBUG ***", "Root SUC ");
        return true;
    }

    /**
     * 获取设备是否有root权限
     * @return
     */
    public static boolean isDeviceRooted() {
        boolean boo1 = checkRootMethod1() ;
        boolean boo2 = checkRootMethod2() ;
        boolean boo3 = checkRootMethod3() ;
        LogPlus.sd(TAG , "是否获取root权限-1:" + boo1 + " -2:" + boo2 + " -3:" +boo3  + " -4:" +boo3);
        return boo1 || boo2 || boo3 ;
    }

    private static boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private static boolean checkRootMethod2() {
        String[] paths = { "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }

    private static boolean checkRootMethod3() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) return true;
            return false;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }
}
