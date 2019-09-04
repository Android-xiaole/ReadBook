package com.jj.base.net;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.jj.base.utils.NetWorkUtil;

import org.json.JSONException;

import java.net.UnknownHostException;

import io.reactivex.observers.ResourceObserver;

public abstract class ApiSubscriber2<T> extends ResourceObserver<T> {

    /*
        网络请求结束的回调（不管成功还是失败都会回调，这里一般可以去做progress dismiss的操作）
        这和onComplete不同，onComplete只会在onNext走完之后回调,该方法不需要可以不调用
     */
    protected void onEnd(){}

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
                onEnd();
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
        onEnd();
    }

    //网络请求失败回调
    protected abstract void onFail(NetError error);

    @Override
    public void onComplete() {
        onEnd();
    }
}
