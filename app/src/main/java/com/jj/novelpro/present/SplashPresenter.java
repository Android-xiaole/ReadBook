package com.jj.novelpro.present;

import android.text.TextUtils;

import com.jj.base.mvp.BaseRepository;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.utils.SharedPref;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.biz.pruduct.ProductRepository;
import com.jj.comics.data.model.AppConfigResponse;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;

public class SplashPresenter extends BasePresenter<BaseRepository,SplashContract.ISplashView> implements SplashContract.ISplashPresenter{


    @Override
    public void sendDelayedMessage(int second) {
        Observable<AppConfigResponse> appConfig = ProductRepository.getInstance()
                .getAppConfig()
                .timeout(5, TimeUnit.SECONDS, new ObservableSource<AppConfigResponse>() {
                    @Override
                    public void subscribe(Observer<? super AppConfigResponse> observer) {
                        ProductRepository.getInstance()
                                .getAppConfigByIP()
                                .subscribe(new ApiSubscriber2<AppConfigResponse>() {
                                    @Override
                                    public void onNext(AppConfigResponse appConfigResponse) {
                                        observer.onNext(appConfigResponse);
                                    }

                                    @Override
                                    protected void onFail(NetError error) {
                                        observer.onError(error);
                                    }
                                });
                    }
                });
        Observable<Long> as = Observable.timer(second, TimeUnit.SECONDS);

        Observable.zip(appConfig, as, new BiFunction<AppConfigResponse, Long, Boolean>() {
            @Override
            public Boolean apply(AppConfigResponse appConfigResponse, Long t2) throws Exception {
                if (checkAppConfig(appConfigResponse)) {
                    return true;
                }else {
                    return false;
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(bindLifecycle())
                .subscribe(new ApiSubscriber2<Boolean>() {
            @Override
            protected void onFail(NetError error) {
                getV().initConfigFail(error.getMessage());
            }

            @Override
            public void onNext(Boolean b) {
                if (b) {
                    getV().launch();
                }else {
                    getV().initConfigFail("");
                }

            }
        });

    }

    private boolean checkAppConfig(AppConfigResponse appConfigResponse) {
        boolean flag = false;
        if (appConfigResponse == null) {
            flag = false;
        }else {
            AppConfigResponse.DataBean data = appConfigResponse.getData();
            if (data == null) {
                flag = false;
            }else {
                long channelid = data.getChannelid();
                if (channelid < 0) {
                    flag = false;
                }else {
                    String app_api_url = data.getApp_api_url();
                    if (TextUtils.isEmpty(app_api_url)) {
                        flag = false;
                    }else {
                        AppConfigResponse.DataBean.LoginBean login = data.getLogin();
                        AppConfigResponse.DataBean.PayBean pay = data.getPay();
                        if (login == null || pay == null) {
                            flag = false;
                        }else {
                            AppConfigResponse.DataBean.LoginBean.QqBean qq = login.getQq();
                            AppConfigResponse.DataBean.LoginBean.WechatBean wechat = login.getWechat();
                            AppConfigResponse.DataBean.LoginBean.WeiboBean weibo = login.getWeibo();
                            if (qq == null || wechat == null || weibo == null
                            || TextUtils.isEmpty(pay.getApp_id()) || TextUtils.isEmpty(pay.getMch_id())) {
                                flag = false;
                            }else {
                                String qqApp_id = qq.getApp_id();
                                String wechatApp_id = wechat.getApp_id();
                                String weiboApp_id = weibo.getApp_id();
                                if (TextUtils.isEmpty(qqApp_id) || TextUtils.isEmpty(wechatApp_id)
                                || TextUtils.isEmpty(weiboApp_id)) {
                                    flag = false;
                                }else {
                                    SharedPref.getInstance().putString("pay_info", data.getPay_info());
                                    SharedPref.getInstance().putString("CHANNEL_ID_PHP",channelid + "");
                                    SharedPref.getInstance().putString("BASEURL",app_api_url);
                                    SharedPref.getInstance().putString("WX_APP_ID_PAY",qqApp_id);
                                    SharedPref.getInstance().putString("QQ_APPID",qqApp_id);
                                    SharedPref.getInstance().putString("WEIBO_APP_ID",weiboApp_id);
                                    SharedPref.getInstance().putString("WX_APP_ID_LOGIN",wechatApp_id);
                                    flag = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return flag;
    }


}
