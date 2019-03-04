package com.vipcare.listenerservice;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * android判断是否在launcher界面
 * Created by zeting
 * Date 19/1/14.
 */
public class LauncherUtils {

    private static final String TAG = "ServiceListen";

    //获取所有launcher的包名：
    public static List getLauncherPackageName(Context context) {
        List packageNames = new ArrayList();
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
//        final ResolveInfo res = context.getPackageManager().resolveActivity(intent, 0);
        List<ResolveInfo> resolveInfo = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        for(ResolveInfo ri : resolveInfo){
            packageNames.add(ri.activityInfo.packageName);
            Log.i(TAG, "packageName =" + ri.activityInfo.packageName);
        }
        if(packageNames == null || packageNames.size() == 0){
            return null;
        }else{
            return packageNames;
        }
    }

//    intent.addCategory(Intent.CATEGORY_HOME)-----限制为launcher
//    List resolveInfo = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);------查询所有默认的activity的包名
//    如果改为getPackageManager().resolveActivity(intent, 0);就是查询包括第三方应用在内的所有app的包名


    //判断是否在launcher界面：
    public boolean isLauncher(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
        String topPackageName = rti.get(0).topActivity.getPackageName();
        List launcherName = getLauncherPackageName(context);
        Log.i(TAG, "topPackageName =" + topPackageName);
        Log.i(TAG, "launcherName =" + launcherName);
        if (launcherName != null && launcherName.size() != 0) {
            for (int i = 0; i < launcherName.size(); i ++) {
                if (launcherName.get(i) != null && launcherName.get(i).equals(topPackageName)) {
                    return true;
                }
            }
        }
        return false;
    }

//    mActivityManager.getRunningTasks(1)------得到当前正在运行的task列表，1表示最大任务数
//    rti.get(0).topActivity.getPackageName()-----topActivity为当前task最活跃的activity，就是栈最顶层的 如果返回true就是在launcher界面了

//    <uses-permission android:name="android.permission.GET_TASKS"/>
}
