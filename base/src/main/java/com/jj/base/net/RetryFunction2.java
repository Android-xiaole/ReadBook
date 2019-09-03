package com.jj.base.net;

import android.os.NetworkOnMainThreadException;

import com.jj.base.log.LogUtil;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.functions.Function;

/**
 * 为Observable定制的网络请求重试机制
 */
public class RetryFunction2 implements Function<Observable<Throwable>, ObservableSource<?>> {

    private int maxConnectCount = 2;
    // 当前已重试次数
    private int currentRetryCount = 0;
    // 重试等待时间
    private int waitRetryTime = 0;

    private String retryTag = "";//非必须，只是为了方便查看log

    public RetryFunction2(String retryTag) {
        this.retryTag = retryTag;
    }

    public RetryFunction2() {
    }


    @Override
    public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Throwable throwable) throws Exception {
                LogUtil.e("发生异常 = " + throwable.toString());
                boolean needRetry = throwable instanceof IOException;
                if (!needRetry && throwable instanceof CompositeException) {
                    List<Throwable> exceptions = ((CompositeException) throwable).getExceptions();
                    for (Throwable exception : exceptions) {
                        needRetry = exception instanceof IOException;
                        if (needRetry) break;
                    }
                }
                if (needRetry) {
                    LogUtil.e(retryTag + "属于IO异常，需重试");
                    if (currentRetryCount < maxConnectCount) {
                        currentRetryCount++;
                        LogUtil.e(retryTag + "重试次数 = " + currentRetryCount);

                        waitRetryTime = 1000 + currentRetryCount * 1000;
                        LogUtil.d("等待时间 =" + waitRetryTime);
                        return Observable.just(1).delay(waitRetryTime, TimeUnit.MILLISECONDS);
                    } else {
                        // 若重试次数已 > 设置重试次数，则不重试
                        // 通过发送error来停止重试（可在观察者的onError（）中获取信息）
                        return Observable.error(new NetError(throwable.getMessage(),NetError.OtherError));
                    }
                } else if (throwable instanceof NetError) {
                    return Observable.error(throwable);
                }else if (throwable instanceof NetworkOnMainThreadException){
                    return Observable.error(new NetError("NetworkOnMainThreadException",NetError.OtherError));
                }
                return Observable.error(new NetError(throwable.getMessage(),NetError.OtherError));
            }
        });
    }
}
