package com.jj.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public abstract class BaseApplication extends Application {
    private static BaseApplication instance;
    private Activity topActivity;

    public static boolean isNormalStart() {
        return NORMAL_START;
    }

    public static void setNormalStart(boolean normalStart) {
        NORMAL_START = normalStart;
    }

    private static boolean NORMAL_START;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                topActivity = activity;
            }

            @Override
            public void onActivityResumed(Activity activity) {
                topActivity = activity;
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
        init();
    }


    public abstract void init();

    public static BaseApplication getApplication() {
        return instance;
    }

    public Activity getTopActivity(){
        return topActivity;
    }
}
