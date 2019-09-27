package com.jj.comics.data.biz.pruduct;

import com.jj.base.net.ComicApiImpl;
import com.jj.base.net.RetryFunction2;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.net.ComicApi;
import com.jj.comics.common.net.RequestBodyBuilder;
import com.jj.comics.data.model.AppConfigResponse;
import com.jj.comics.data.model.NotificationListResponse;
import com.jj.comics.data.model.NotificationResponse;
import com.jj.comics.data.model.PayActionResponse;
import com.jj.comics.data.model.ProtocalModel;
import com.jj.comics.data.model.Push;
import com.jj.comics.data.model.ResponseModel;
import com.jj.comics.data.model.UpdateModelProxy;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

public class ProductRepository implements ProductDataSource {

    private static ProductRepository INSTANCE;

    private ProductRepository() {
    }

    public static ProductRepository getInstance() {
        if (INSTANCE == null) INSTANCE = new ProductRepository();
        return INSTANCE;
    }

    @Override
    public Observable<ProtocalModel> getProductProtocalByProtocolCode(String activityName, String agreementKey) {
        Observable<ProtocalModel> compose =
                ComicApi.getApi()
                        .getProductProtocalByProtocolCode(Constants.PRODUCT_CODE, agreementKey)
                        .compose(ComicApiImpl.<ProtocalModel>getApiTransformer2())
                        .subscribeOn(Schedulers.io());
        return compose;
    }


    @Override
    public Observable<UpdateModelProxy> getChannelUpdateInfo(String activityName) {
        String channel_id = Constants.CHANNEL_ID;

        Observable<UpdateModelProxy> compose = ComicApi.getApi().getChannelUpdateInfo(Constants.PRODUCT_CODE,
                channel_id)
                .compose(ComicApiImpl.<UpdateModelProxy>getApiTransformer2())
                .retryWhen(new RetryFunction2(activityName))
                .subscribeOn(Schedulers.io());
        return compose;
    }


    @Override
    public Observable<AppConfigResponse> getAppConfig() {
        return ComicApi.getApi()
                .getAppConfig()
                .compose(ComicApiImpl.<AppConfigResponse>getApiTransformer2())
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<AppConfigResponse> getAppConfigByIP() {
        return ComicApi.getApi()
                .getAppConfigByIP()
                .compose(ComicApiImpl.<AppConfigResponse>getApiTransformer2())
                .subscribeOn(Schedulers.io());
    }


    @Override
    public Observable<NotificationListResponse> getNotificationList(int pageNum) {
        return ComicApi.getApi().getNotificationList(pageNum)
                .compose(ComicApiImpl.getApiTransformer2())
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<NotificationResponse> getNotificationDetail(long id) {
        return ComicApi.getApi().getNotificationDetail(id)
                .compose(ComicApiImpl.getApiTransformer2())
                .subscribeOn(Schedulers.io());
    }
}
