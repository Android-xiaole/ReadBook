package com.jj.comics.jpush;

import android.content.Context;

import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.service.JPushMessageReceiver;

public class PushReceiver extends JPushMessageReceiver {

    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        super.onMessage(context, customMessage);
    }

}
