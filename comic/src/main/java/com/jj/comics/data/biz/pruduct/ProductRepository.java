package com.jj.comics.data.biz.pruduct;

import com.jj.base.net.ComicApiImpl;
import com.jj.base.net.RetryFunction2;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.net.ComicApi;
import com.jj.comics.common.net.RequestBodyBuilder;
import com.jj.comics.data.model.AppConfigResponse;
import com.jj.comics.data.model.PayActionResponse;
import com.jj.comics.data.model.ProtocalModel;
import com.jj.comics.data.model.Push;
import com.jj.comics.data.model.ResponseModel;
import com.jj.comics.data.model.ShareParamModel;
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
    public Observable<ResponseModel> uploadFeedback(String msg) {
        final RequestBody requestBody = new RequestBodyBuilder()
                .addProperty(Constants.RequestBodyKey.FEEDBACK_DESC, msg)
                .build();

        Observable<ResponseModel> compose = ComicApi.getApi()
                .uploadFeedback(requestBody)
                .compose(ComicApiImpl.<ResponseModel>getApiTransformer2())
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
    public Observable<ShareParamModel> getShareParam(String activityName) {

        RequestBody requestBody =
                new RequestBodyBuilder().addProperty(Constants.RequestBodyKey.PRODUCT_CODE,
                        Constants.PRODUCT_CODE)
                .build();

        Observable<ShareParamModel> compose = ComicApi.getApi()
                .getShareParam(requestBody)
                .compose(ComicApiImpl.<ShareParamModel>getApiTransformer2())
                .retryWhen(new RetryFunction2(activityName))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
        return compose;
    }

    @Override
    public Observable<Push> getAdsPush(String activityName,String typeId) {
        Observable<Push> compose =
                ComicApi.getApi().getAdsPush(Constants.CHANNEL_ID, typeId)
                        .compose(ComicApiImpl.<Push>getApiTransformer2())
                        .retryWhen(new RetryFunction2(activityName))
                        .subscribeOn(Schedulers.io());
        return compose;
    }

    @Override
    public Observable<AppConfigResponse> getAppConfig( String channelId) {
        RequestBody requestBody = new RequestBodyBuilder()
                .addProperty(Constants.RequestBodyKey.PACKAGE, channelId)
                .addProperty(Constants.RequestBodyKey.SOURCEID, Constants.SOURCE_ID)
                .build();
        return ComicApi.getApi()
                .getAppConfig(requestBody)
                .compose(ComicApiImpl.<AppConfigResponse>getApiTransformer2())
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<AppConfigResponse> getAppConfigByIP(String channelId) {
        RequestBody requestBody = new RequestBodyBuilder()
                .addProperty(Constants.RequestBodyKey.PACKAGE, channelId)
                .addProperty(Constants.RequestBodyKey.SOURCEID, Constants.SOURCE_ID)
                .build();
        return ComicApi.getApi()
                .getAppConfigByIP(requestBody)
                .compose(ComicApiImpl.<AppConfigResponse>getApiTransformer2())
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<PayActionResponse> getPayAction() {
        return ComicApi.getApi().getPayAction()
                .compose(ComicApiImpl.<PayActionResponse>getApiTransformer2())
                .subscribeOn(Schedulers.io());

    }
}
