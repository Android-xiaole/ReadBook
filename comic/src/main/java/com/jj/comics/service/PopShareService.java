package com.jj.comics.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;

import androidx.annotation.Nullable;

import com.imuxuan.floatingview.FloatingView;
import com.jj.base.log.LogUtil;

import java.util.Random;

public class PopShareService extends Service {
    private  int i = 0;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    //全局随机每5秒~10分钟执行一次显示
                    int ms = new Random().nextInt(5*60*1000 - 5000) + 5000;
                    LogUtil.e("SERVICE " + ms);
                    SystemClock.sleep(5000);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            int a = new Random().nextInt(100);
                            float i = (float)a/ (float)100 + new Random().nextInt(999);
                            FloatingView floatingView = FloatingView.get();
//                            String url = "http://static-cdn.ceair.com/resource/images/public/logo_l.png?v=zh_CN_18180";
                            String url = "http://fanli.jjmh668.cn/prd/avater/";
                            url = url + new Random().nextInt(1000) + ".jpg";
                            floatingView.add(i + "",url);
                            LogUtil.e("SERVICE " + i);
                        }
                    });

                }
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

}
