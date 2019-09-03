package com.jj.comics.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import com.jj.base.utils.NetWorkUtil;
import com.jj.comics.util.eventbus.EventBusManager;

public class NetWorkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //监听到网络出现变化
        if (TextUtils.equals(intent.getAction(), ConnectivityManager.CONNECTIVITY_ACTION)) {
            NetWorkUtil.NetworkType networkType = NetWorkUtil.getNetworkType();
            EventBusManager.sendNetWorkReceiverEvent(networkType);
//            EventBus.getDefault().post(networkType);
        }
    }
}
