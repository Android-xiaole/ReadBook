package com.jj.comics.util.eventbus.events;

import android.content.Intent;

/**
 * 刷新电池电量通知
 */
public class BatteryReceiverEvent {

    public Intent intent;

    public BatteryReceiverEvent(Intent intent) {
        this.intent = intent;
    }
}
