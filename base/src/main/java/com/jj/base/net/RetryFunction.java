package com.jj.base.net;

import com.jj.base.log.LogUtil;

import org.reactivestreams.Publisher;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.functions.Function;

//
//            final Function<? super Flowable<Throwable>, ? extends Publisher<?>> handler
public class RetryFunction implements Function<Flowable<Throwable>, Publisher<?>> {
    private int maxConnectCount = 2;
    // 当前已重试次数
    private int currentRetryCount = 0;
    // 重试等待时间
    private int waitRetryTime = 0;

    private String activityName;

    public RetryFunction(String activityName) {
        this.activityName = activityName;
    }

    public RetryFunction() {
    }

    @Override
    public Publisher<Integer> apply(Flowable<Throwable> throwableObservable) throws Exception {
        return throwableObservable.flatMap(new Function<Throwable, Publisher<Integer>>() {
            @Override
            public Publisher<Integer> apply(Throwable throwable) throws Exception {
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
                    LogUtil.e(activityName + "属于IO异常，需重试");
                    if (currentRetryCount < maxConnectCount) {
                        currentRetryCount++;
                        LogUtil.e(activityName + "重试次数 = " + currentRetryCount);

                        waitRetryTime = 1000 + currentRetryCount * 1000;
                        LogUtil.d("等待时间 =" + waitRetryTime);
                        return Flowable.just(1).delay(waitRetryTime, TimeUnit.MILLISECONDS);
                    } else {
                        // 若重试次数已 > 设置重试次数，则不重试
                        // 通过发送error来停止重试（可在观察者的onError（）中获取信息）
//                        return Flowable.error(new NetError(/*"重试次数已超过设置次数 = " + currentRetryCount + "，即 不再重试"*/"网络不通畅,请检查后再试", NetError.NoConnectError));
                        return Flowable.error(new NetError(throwable.getMessage(),NetError.OtherError));
                    }
                } else if (throwable instanceof NetError) {
                    return Flowable.error(throwable);
                }
                return Flowable.error(new NetError(throwable.getMessage(),NetError.OtherError));
//                return Flowable.error(NetError.noConnectError(throwable.getMessage()));
//                return Flowable.error(new NetError(/*"重试次数已超过设置次数 = " + currentRetryCount + "，即 不再重试"*/"网络不通畅,请检查后再试", NetError.NoConnectError));
            }
        });
    }
}
