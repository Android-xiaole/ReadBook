package com.jj.base.net;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.jj.base.utils.NetWorkUtil;

import org.json.JSONException;

import java.net.UnknownHostException;

import io.reactivex.subscribers.ResourceSubscriber;


/**
 * Created by wanglei on 2016/12/26.
 */

public abstract class ApiSubscriber<T> extends ResourceSubscriber<T> {

    @Override
    protected void onStart() {
        super.onStart();
        NetWorkUtil.NetworkType networkType = NetWorkUtil.getNetworkType();
        switch (networkType) {
            case NETWORK_NO:
            case NETWORK_UNKNOWN:
                // 一定好主动调用下面这一句,取消本次Subscriber订阅
                if (!isDisposed()) {
                    dispose();
                }
                onFail(NetError.noConnectError("netWork type :" + networkType.name()));
                break;
            default:
                break;
        }
    }

    @Override
    public void onError(Throwable e) {
        NetError error = null;
        if (e != null) {
            if (!(e instanceof NetError)) {
                if (e instanceof UnknownHostException) {
                    error = new NetError(e, NetError.NoConnectError);
                } else if (e instanceof JSONException
                        || e instanceof JsonParseException
                        || e instanceof JsonSyntaxException) {
                    error = new NetError(e, NetError.ParseError);
                } else {
                    error = new NetError(e, NetError.OtherError);
                }
            } else {
                error = (NetError) e;
            }
            onFail(error);
        }

    }

    protected abstract void onFail(NetError error);

    @Override
    public void onComplete() {

    }
}
