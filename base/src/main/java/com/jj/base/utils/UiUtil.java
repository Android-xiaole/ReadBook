package com.jj.base.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class UiUtil {

    public static void fixNavigationBar(Activity activity) {
        //设置全屏模式不和底部虚拟按键重合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            //  | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    public static boolean hasNotchHW(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return ret;
        }
    }

    public static boolean hasNotchOPPO(Context context) {
        boolean ret = false;
        try {
            ret = context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return ret;
        }
    }

    public static boolean hasNotchMI(Activity activity) {
        return getInt("ro.miui.notch",activity) == 1;
    }


    public static int getInt(String key,Activity activity) {
        int result = 0;
        if (isXiaomi()){
            try {
                ClassLoader classLoader = activity.getClassLoader();
                @SuppressWarnings("rawtypes")
                Class SystemProperties = classLoader.loadClass("android.os.SystemProperties");
                //参数类型
                @SuppressWarnings("rawtypes")
                Class[] paramTypes = new Class[2];
                paramTypes[0] = String.class;
                paramTypes[1] = int.class;
                Method getInt = SystemProperties.getMethod("getInt", paramTypes);
                //参数
                Object[] params = new Object[2];
                params[0] = new String(key);
                params[1] = new Integer(0);
                result = (Integer) getInt.invoke(SystemProperties, params);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    public static boolean isXiaomi() {
        return "Xiaomi".equals(Build.MANUFACTURER);
    }

    public static final int NOTCH_IN_SCREEN_VOIO_MARK = 0x00000020;//是否有凹槽
    public static final int ROUNDED_IN_SCREEN_VOIO_MARK = 0x00000008;//是否有圆角

    public static boolean hasNotchVIVO(Context context, int mark) {

        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class ftFeature = cl.loadClass("android.util.FtFeature");
            Method get = ftFeature.getMethod("isFeatureSupport", int.class);
            ret = (boolean) get.invoke(ftFeature, mark);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return ret;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    public static void hasNotchGoogle(Activity context, final GetGoogleNotchListener listener) {
        try {
            View contentView = context.getWindow().getDecorView().findViewById(android.R.id.content).getRootView();
            contentView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @TargetApi(Build.VERSION_CODES.P)
                @Override
                public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                    DisplayCutout cutout = windowInsets.getDisplayCutout();
                    if (cutout == null) {
                        //通过cutout是否为null判断是否刘海屏手机
                        listener.onGet(false);
                    } else {
                        listener.onGet(true);
//                        List<Rect> rects = cutout.getBoundingRects();
//                    if (rects == null || rects.size() == 0) {
//                        Log.e(TAG, "rects==null || rects.size()==0, is not notch screen");
//                    } else {
//                        Log.e(TAG, "rect size:" + rects.size());//注意：刘海的数量可以是多个
//                        for (Rect rect : rects) {
//                            Log.e(TAG, "cutout.getSafeInsetTop():" + cutout.getSafeInsetTop()
//                                    + ", cutout.getSafeInsetBottom():" + cutout.getSafeInsetBottom()
//                                    + ", cutout.getSafeInsetLeft():" + cutout.getSafeInsetLeft()
//                                    + ", cutout.getSafeInsetRight():" + cutout.getSafeInsetRight()
//                                    + ", cutout.rects:" + rect
//                            );
//                        }
//                    }
                    }
                    return windowInsets;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            listener.onGet(false);
        }
    }

    public abstract static class GetGoogleNotchListener {
        public abstract void onGet(boolean hasNotch);
    }

}
