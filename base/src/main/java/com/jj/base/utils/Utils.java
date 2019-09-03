package com.jj.base.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.opengl.GLES10;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.gyf.barlibrary.BarHide;
import com.gyf.barlibrary.ImmersionBar;
import com.jj.base.R;

import java.io.File;
import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.UUID;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class Utils {
    public static int getScreenWidth(Context context) {
        return getMetrics(context).widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return getMetrics(context).heightPixels;
    }

    public static DisplayMetrics getMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = getMetrics(context).density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 将像素转换成dp
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = getMetrics(context).density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 适配刘海屏
     * @param context
     */
    public static void fixNotch(Activity context) {
        if (context == null)return;
        boolean hasNotch = UiUtil.hasNotchOPPO(context) || UiUtil.hasNotchHW(context)
                || UiUtil.hasNotchVIVO(context, UiUtil.NOTCH_IN_SCREEN_VOIO_MARK)
                || UiUtil.hasNotchMI(context);
        if (hasNotch) {
            ImmersionBar.with(context)
                    .reset()
                    .keyboardEnable(true)
                    .fitsSystemWindows(true)
                    .statusBarDarkFont(true, 0.2f)
                    .statusBarColor(R.color.base_color_ffffff)
                    .init();
        } else {
            ImmersionBar.with(context)
                    .hideBar(BarHide.FLAG_HIDE_STATUS_BAR)
                    .fitsSystemWindows(false)
                    .init();
        }
    }

    /**
     * 换算单位 超过10000的显示1.0万
     * @return
     */
    public static String convertUnit(int num){
        if (num<10000){
            return num+"";
        }else if(num == 10000){
            return "1万";
        }else if (num > 10000 && num < 100000){
            BigDecimal bigDecimal = new BigDecimal(num/10000.00);
            bigDecimal = bigDecimal.setScale(1,BigDecimal.ROUND_HALF_UP);
            if (bigDecimal.doubleValue()==10){
                return "10万";
            }
            return bigDecimal.doubleValue()+"万";
        }else if(num >= 100000){
            return (num/10000)+"万";
        }
        return num+"";
    }

    public static String encrypt(String s) {
        try {
            byte[] btInput = s.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < md.length; i++) {
                int val = ((int) md[i]) & 0xff;
                if (val < 16)
                    sb.append("0");
                sb.append(Integer.toHexString(val));
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getIp() {
        try {
            Enumeration en = NetworkInterface.getNetworkInterfaces();

            while (en.hasMoreElements()) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                Enumeration enumIpAddr = intf.getInetAddresses();

                while (enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
            return "";
        } catch (SocketException var4) {
            return "";
        }
    }

    @SuppressLint({"MissingPermission"})
    public static String getPhoneNumber(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String tel = "";
        if (tm != null) {
            try {
                tel = tm.getLine1Number();
            } catch (Exception var4) {
                tel = "";
            }
        }

        return tel;
    }

//    public static String getChannel(Context context) {
//        try {
//            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
//            return applicationInfo.metaData.get("SDK_SOURCE") + "";
////
//        } catch (PackageManager.NameNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return "";
//    }
//
//    public static boolean isHuaweiDevice() {
//        boolean result = false;
//        if (Build.BRAND.equalsIgnoreCase("huawei") || Build.BRAND.equalsIgnoreCase("honor")) {
//            result = true;
//        }
//
//        return result;
//    }
//
//    public static boolean isMiDevice(Context context) {
//        boolean result = false;
//
//        try {
//            PackageManager manager = context.getPackageManager();
//            PackageInfo pkgInfo = null;
//            if (TextUtils.equals(Build.BRAND.toLowerCase(), "Xiaomi".toLowerCase())) {
//                pkgInfo = manager.getPackageInfo("com.xiaomi.xmsf", 4);
//                if (pkgInfo != null && pkgInfo.versionCode >= 105) {
//                    result = true;
//                }
//            }
//        } catch (Throwable var4) {
//        }
//
//        return result;
//    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (DocumentsContract.isDocumentUri(context, uri)) {
                    if (isExternalStorageDocument(uri)) {
                        // ExternalStorageProvider
                        final String docId = DocumentsContract.getDocumentId(uri);
                        final String[] split = docId.split(":");
                        final String type = split[0];
                        if ("primary".equalsIgnoreCase(type)) {
                            data = Environment.getExternalStorageDirectory() + "/" + split[1];
                        }
                    } else if (isDownloadsDocument(uri)) {
                        // DownloadsProvider
                        final String id = DocumentsContract.getDocumentId(uri);
                        final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                                Long.valueOf(id));
                        data = getDataColumn(context, contentUri, null, null);
                    } else if (isMediaDocument(uri)) {
                        // MediaProvider
                        final String docId = DocumentsContract.getDocumentId(uri);
                        final String[] split = docId.split(":");
                        final String type = split[0];
                        Uri contentUri = null;
                        if ("image".equals(type)) {
                            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        } else if ("video".equals(type)) {
                            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        } else if ("audio".equals(type)) {
                            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }
                        final String selection = "_id=?";
                        final String[] selectionArgs = new String[]{split[1]};
                        data = getDataColumn(context, contentUri, selection, selectionArgs);
                    }
                } else {

                }
            } else {
                Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
                if (null != cursor) {
                    if (cursor.moveToFirst()) {
                        int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                        if (index > -1) {
                            data = cursor.getString(index);
                        }
                    }
                    cursor.close();
                }
            }
        }
        if (TextUtils.isEmpty(data))
            data = UUID.randomUUID().toString().trim().replaceAll("-", "") + ".png";
        return data;
    }

    public static File uriToFile(Context context, Uri uri) {
        String path = null;
        if ("file".equals(uri.getScheme())) {
            path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = context.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA}, buff.toString(), null, null);
                int index = 0;
                int dataIdx = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    index = cur.getInt(index);
                    dataIdx = cur.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    path = cur.getString(dataIdx);
                }
                cur.close();
                if (index == 0) {
                } else {
                    Uri u = Uri.parse("content://media/external/images/media/" + index);
                    System.out.println("temp uri is :" + u);
                }
            }
            if (path != null) {
                return new File(path);
            }
        } else if ("content".equals(uri.getScheme())) {
            // 4.2.2以后
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
            }
            cursor.close();
            if (TextUtils.isEmpty(path)) {
                return new File(getRealFilePath(context, uri));
            }
            return new File(path);
        } else {
            //Log.i(TAG, "Uri Scheme:" + uri.getScheme());
        }
        return new File(getRealFilePath(context, uri));
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static int getOpenglRenderLimit() {
        int maxsize;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            maxsize = getOpenglRenderLimitEqualAboveLollipop();
        } else {
            maxsize = getOpenglRenderLimitBelowLollipop();
        }
        return maxsize == 0 ? 4096 : maxsize;
    }

    private static int getOpenglRenderLimitBelowLollipop() {
        int[] maxSize = new int[1];
        GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0);
        return maxSize[0];
    }

    private static int getOpenglRenderLimitEqualAboveLollipop() {
        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay dpy = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        int[] vers = new int[2];
        egl.eglInitialize(dpy, vers);
        int[] configAttr = {
                EGL10.EGL_COLOR_BUFFER_TYPE, EGL10.EGL_RGB_BUFFER,
                EGL10.EGL_LEVEL, 0,
                EGL10.EGL_SURFACE_TYPE, EGL10.EGL_PBUFFER_BIT,
                EGL10.EGL_NONE
        };
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfig = new int[1];
        egl.eglChooseConfig(dpy, configAttr, configs, 1, numConfig);
        if (numConfig[0] == 0) {// TROUBLE! No config found.
        }
        EGLConfig config = configs[0];
        int[] surfAttr = {
                EGL10.EGL_WIDTH, 64,
                EGL10.EGL_HEIGHT, 64,
                EGL10.EGL_NONE
        };
        EGLSurface surf = egl.eglCreatePbufferSurface(dpy, config, surfAttr);
        final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;// missing in EGL10
        int[] ctxAttrib = {
                EGL_CONTEXT_CLIENT_VERSION, 1,
                EGL10.EGL_NONE
        };
        EGLContext ctx = egl.eglCreateContext(dpy, config, EGL10.EGL_NO_CONTEXT, ctxAttrib);
        egl.eglMakeCurrent(dpy, surf, surf, ctx);
        int[] maxSize = new int[1];
        GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0);
        egl.eglMakeCurrent(dpy, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_CONTEXT);
        egl.eglDestroySurface(dpy, surf);
        egl.eglDestroyContext(dpy, ctx);
        egl.eglTerminate(dpy);
        return maxSize[0];
    }

}
