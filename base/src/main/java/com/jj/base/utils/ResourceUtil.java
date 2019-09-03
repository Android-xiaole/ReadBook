package com.jj.base.utils;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class ResourceUtil {
    public static boolean getAppBooleanRes(Application app, String key, boolean defaultValue) {
        ApplicationInfo info = null;
        try {
            info = app.getPackageManager().getApplicationInfo(app.getPackageName(),
                    PackageManager.GET_META_DATA);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return info == null ? defaultValue : info.metaData.getBoolean(key, defaultValue);

    }

    public static Object getAppRes(Application app, String key, Object defaultValue) {
        ApplicationInfo info = null;
        try {
            info = app.getPackageManager().getApplicationInfo(app.getPackageName(),
                    PackageManager.GET_META_DATA);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return info == null ? defaultValue : info.metaData.get(key);

    }

}
