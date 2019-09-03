package com.jj.base.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.jj.base.BaseApplication;
import com.jj.base.log.LogUtil;

import java.util.List;

/**
 * FBI WARNING ! MAGIC ! DO NOT TOUGH !
 * Created by WangZQ on 2018/8/16 - 14:54.
 */
public class PackageUtil {
    private static String imsi = null;
    private static String imei = null;

    public static PackageInfo getPackageInfo() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = BaseApplication.getApplication().getPackageManager().getPackageInfo(BaseApplication.getApplication().getPackageName(), PackageManager.GET_CONFIGURATIONS);
        } catch (Exception ex) {
            LogUtil.e("Get package info error.");
        }

        return packageInfo;
    }

    /**
     * 获取图标 bitmap
     *
     * @param context
     */
    public static synchronized Drawable getIcon(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getApplicationContext()
                    .getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(
                    context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        Drawable d = packageManager.getApplicationIcon(applicationInfo); //xxx根据自己的情况获取drawable
        return d;
    }

    public static String getVersionName(Context context, String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }


    public static String getVersionCode(Context context, String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi.versionCode + "";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getAppName(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            int labelRes = pi.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getImei1() {
        if (imei == null) {
            List<SimUtil.SimInfo> simInfos = SimUtil.getSimInfo(BaseApplication.getApplication());
            if (simInfos != null && simInfos.size() > 0) {
                imei = simInfos.get(0).getImei();
            } else {
                imei = "";
            }
        }
        return imei;
    }

    public static String getImsi1() {
        if (imsi == null) {
            List<SimUtil.SimInfo> simInfos = SimUtil.getSimInfo(BaseApplication.getApplication());
            if (simInfos != null && simInfos.size() > 0) {
                imsi = simInfos.get(0).getImsi();
            } else {
                imsi = "";
            }
        }
        return imsi;
    }

    /**
     * 检测应用是否安装支付宝
     *
     * @param context
     * @return
     */
    public static boolean isAliPayInstalled(Context context) {
        Uri uri = Uri.parse("alipays://platformapi/startApp");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ComponentName componentName = intent.resolveActivity(context.getPackageManager());
        return componentName != null;
    }
}
