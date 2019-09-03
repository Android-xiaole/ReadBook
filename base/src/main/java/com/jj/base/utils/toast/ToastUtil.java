package com.jj.base.utils.toast;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.jj.base.BaseApplication;

import java.lang.reflect.Field;


/**
 * FBI WARNING ! MAGIC ! DO NOT TOUGH !
 * Created by WangZQ on 2018/8/6 - 11:51.
 */
public class ToastUtil {

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    private static Toast sToast;

    private static Context sContext;

    public static void showToastShort(CharSequence msg) {
        sContext = BaseApplication.getApplication();
        show(msg, Toast.LENGTH_SHORT);
    }

    public static void showToastLong(CharSequence msg) {
        sContext = BaseApplication.getApplication();
        show(msg, Toast.LENGTH_LONG);
    }

//    public static void showToastShort(Context context, CharSequence msg) {
//        if (context != null)
//            sContext = context;
//        show(msg, Toast.LENGTH_SHORT);
//    }
//
//    public static void showToastLong(Context context, CharSequence msg) {
//        if (context != null)
//            sContext = context;
//        show(msg, Toast.LENGTH_LONG);
//    }


    private static void show(final CharSequence msg, final int duration) {
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                cancel();

                sToast = Toast.makeText(sContext, msg, duration);

                showToast();
            }
        });
    }

    public static void cancel() {
        if (sToast != null) {
            sToast.cancel();
        }
    }

    private static void showToast() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            try {
                Field field = View.class.getDeclaredField("mContext");
                field.setAccessible(true);
                field.set(sToast.getView(), new ApplicationContextWrapperForApi25());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        sToast.show();
    }

    private static final class ApplicationContextWrapperForApi25 extends ContextWrapper {

        ApplicationContextWrapperForApi25() {
            super(sContext);
        }

        @Override
        public Context getApplicationContext() {
            return this;
        }

        @Override
        public Object getSystemService(String name) {
            if (Context.WINDOW_SERVICE.equals(name)) {
                // noinspection ConstantConditions
                return new WindowManagerWrapper(
                        (WindowManager) getBaseContext().getSystemService(name)
                );
            }
            return super.getSystemService(name);
        }

        private static final class WindowManagerWrapper implements WindowManager {

            private final WindowManager base;

            private WindowManagerWrapper(WindowManager base) {
                this.base = base;
            }

            @Override
            public Display getDefaultDisplay() {
                return base.getDefaultDisplay();
            }

            @Override
            public void removeViewImmediate(View view) {
                base.removeViewImmediate(view);
            }

            @Override
            public void addView(View view, ViewGroup.LayoutParams params) {
                try {
                    base.addView(view, params);
                } catch (BadTokenException e) {
                    Log.e("WindowManagerWrapper", e.getMessage());
                } catch (Throwable throwable) {
                    Log.e("WindowManagerWrapper", "[addView]", throwable);
                }
            }

            @Override
            public void updateViewLayout(View view, ViewGroup.LayoutParams params) {
                base.updateViewLayout(view, params);
            }

            @Override
            public void removeView(View view) {
                base.removeView(view);
            }
        }
    }

}
