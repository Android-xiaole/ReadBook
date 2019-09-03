package com.jj.comics.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jj.comics.util.eventbus.EventBusManager;

import java.util.Date;

public class TimeChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_TIME_TICK:
                //每过一分钟 触发
            case Intent.ACTION_TIME_CHANGED:
                //设置了系统时间
            case Intent.ACTION_TIMEZONE_CHANGED:
                //设置了系统时区的action
                EventBusManager.sendTimeChangeReceiverEvent(new Date());
//                EventBus.getDefault().post(new Date());
                break;
        }
    }
}
