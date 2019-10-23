package com.jj.comics.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;

import androidx.annotation.Nullable;

import com.imuxuan.floatingview.FloatingView;
import com.jj.base.utils.toast.ToastUtil;

import java.util.Random;

public class PopShareService extends Service {
    private float money = 0;
    private boolean stopService;
    private int bound;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stopService = false;
        bound = 5 * 60 * 1000 - 5000;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!stopService) {
                    //全局随机每5秒~5分钟执行一次显示
                    int ms = new Random().nextInt(bound) + 5000;
                    ToastUtil.showToastLong(ms+"");

                    int a = new Random().nextInt(100);
                    money = (float) a / (float) 100 + new Random().nextInt(999);

                    SystemClock.sleep(ms);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (!stopService) {
                                FloatingView floatingView = FloatingView.get();
                                String url = "http://fanli.jjmh668.cn/prd/avater/";
                                url = url + new Random().nextInt(1000) + ".jpg";
                                floatingView.add(money + "", url);
                            }
                        }
                    });

                }
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        stopService = true;
        super.onDestroy();
    }
}
