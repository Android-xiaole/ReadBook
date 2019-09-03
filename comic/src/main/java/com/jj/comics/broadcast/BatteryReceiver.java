package com.jj.comics.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jj.comics.util.eventbus.events.BatteryReceiverEvent;
import com.jj.comics.util.eventbus.EventBusManager;

public class BatteryReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        //发送电量变动消息
        EventBusManager.sendBatteryReceiverEvent(new BatteryReceiverEvent(intent));
//        EventBus.getDefault().post(intent);
//        final String action = intent.getAction();
//        if (TextUtils.equals(action, Intent.ACTION_BATTERY_CHANGED)) {
//            EventBus.getDefault().post(intent);
//        } else if (TextUtils.equals(action, Intent.ACTION_BATTERY_LOW)) {
//            // 表示当前电池电量低
//            sendBatteryMessage(context);
//        } else if (TextUtils.equals(action, Intent.ACTION_BATTERY_OKAY)) {
//            // 表示当前电池已经从电量低恢复为正常
//            sendBatteryMessage(context);
//        }
    }
}
