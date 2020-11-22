package com.fred.trafficlightsfillin.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * User: chw
 * Date: 2017/8/15
 */

public class AppUtils {
    private static int VERSION_CODE = -1;
    private static String VERSION_NAME = "";

    /**
     * 得到系统的版本号
     *
     * @return
     */
    public static String getOSVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取当前应用的版本号
     *
     * @return
     */
    public static int getSystemVersionCode() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * 得到应用的版本号
     *
     * @return
     */
    public static int getAppVersionCode(Context context) {
        if (VERSION_CODE != -1) {
            return VERSION_CODE;
        }
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            VERSION_CODE = packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return VERSION_CODE;
    }

    /**
     * 得到应用的版本名称
     *
     * @return
     */
    public static String getAppVersionName(Context context) {
        if (!TextUtils.isEmpty(VERSION_NAME)) {
            return VERSION_NAME;
        }
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo;

        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            VERSION_NAME = packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return VERSION_NAME;
    }

    /**
     * 获取手机高度
     *
     * @param context
     * @return
     */
    public static int getHeightPixels(Activity context) {
        int heightPixels = 0;
        try {
            WindowManager manager = context.getWindowManager();
            DisplayMetrics dm = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(dm);
            heightPixels = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return heightPixels;
    }

    public static int getWidthPixels(Activity context) {
        int WidthPixels = 0;
        try {
            WindowManager manager = context.getWindowManager();
            DisplayMetrics dm = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(dm);
            WidthPixels = dm.widthPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return WidthPixels;
    }

    public static boolean isAppInstall(Context c, String packageName) {
        if (null == c || null == packageName) {
            return false;
        }

        boolean bHas = true;
        try {
            c.getPackageManager().getPackageInfo(packageName, PackageManager.GET_GIDS);
        } catch (/* NameNotFoundException */Exception e) {
            // 抛出找不到的异常，说明该程序已经被卸载
            bHas = false;
        }
        return bHas;
    }

    public static void openApp(Context context, String packageName) {
        Intent intent = getLaunchIntentForPackage(context, packageName);
        if (intent != null) {
            startActivity(context, intent);
        }
    }

    public static Intent getLaunchIntentForPackage(Context context, String pkgname) {
        PackageManager pm = context.getPackageManager();
        Intent intent = null;
        try {
            intent = pm.getLaunchIntentForPackage(pkgname);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intent;
    }

    public static void startActivity(Context context, Class<?> pClass) {
        Intent intent = new Intent(context, pClass);
        startActivity(context, intent);
    }

    public static boolean startActivity(Context context, Intent intent) {
        boolean bResult = true;
        try {
            if (!(context instanceof Activity)) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            bResult = false;
        }
        return bResult;
    }

    public static void startActivity(Activity activity, Class<?> pClass, Bundle pBundle) {
        if (!activity.isFinishing()) {
            Intent intent = new Intent(activity, pClass);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            if (pBundle != null) {
                intent.putExtras(pBundle);
            }

            startActivity(activity, intent);
        }
    }

}
