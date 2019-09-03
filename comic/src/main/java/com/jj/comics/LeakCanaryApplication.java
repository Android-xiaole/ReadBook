package com.jj.comics;

import android.content.Context;

import com.jj.base.BaseApplication;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * 用来监听内存泄漏的application
 * @link https://www.jianshu.com/p/70b8c87ea877
 */
public class LeakCanaryApplication extends BaseApplication {

    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        refWatcher= setupLeakCanary();
        super.getClass().getName();
    }

    @Override
    public void init() {

    }

    private RefWatcher setupLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return RefWatcher.DISABLED;
        }
        return LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        LeakCanaryApplication leakApplication = (LeakCanaryApplication) context.getApplicationContext();
        return leakApplication.refWatcher;
    }

}
