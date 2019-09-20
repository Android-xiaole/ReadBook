package com.jj.comics.data.biz.goods;

import com.jj.base.net.ComicApiImpl;
import com.jj.base.net.RetryFunction2;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.net.ComicApi;
import com.jj.comics.common.net.RequestBodyBuilder;
import com.jj.comics.data.model.PrePayOrderResponseAli;
import com.jj.comics.data.model.PrePayOrderResponseHuifubao;
import com.jj.comics.data.model.PrePayOrderResponseWx;
import com.jj.comics.data.model.ResponseModel;
import com.jj.comics.data.model.RewardGiftsResponse;

import io.reactivex.Observable;
import okhttp3.RequestBody;

public class GoodsRepository implements GoodsDataSource {

    private static GoodsRepository INSTANCE;

    private GoodsRepository(){}

    public static GoodsRepository getInstance(){
        if (INSTANCE == null){
            INSTANCE = new GoodsRepository();
        }
        return INSTANCE;
    }


    @Override
    public Observable<ResponseModel> goldExchange(long userId,String code) {
        RequestBody requestBody = new RequestBodyBuilder()
                .addProperty(Constants.RequestBodyKey.EXCHANGE_CODE, code)
                .addProperty(Constants.RequestBodyKey.USER_ID, userId)
                .build();
        return ComicApi.getApi()
                .goldExchange(requestBody)
                .compose(ComicApiImpl.<ResponseModel>getApiTransformer2());
    }

    @Override
    public Observable<RewardGiftsResponse> getRewardGoodsList() {
        return ComicApi.getApi().getRewardGoodsList()
                .compose(ComicApiImpl.<RewardGiftsResponse>getApiTransformer2())
                .retryWhen(new RetryFunction2());
    }

    @Override
    public Observable<PrePayOrderResponseWx> createPreOrderWx(long goosId, long book_id) {

        RequestBodyBuilder bodyBuilder = new RequestBodyBuilder()
                .addProperty("goods_id", goosId)
                .addProperty("book_id", book_id);
        RequestBody requestBody = bodyBuilder.build();
        return ComicApi.getApi().prePayWx(requestBody)
                .compose(ComicApiImpl.<PrePayOrderResponseWx>getApiTransformer2());
    }

    @Override
    public Observable<PrePayOrderResponseAli> createPreOrderAli(long goosId,long bookId) {
        RequestBodyBuilder bodyBuilder = new RequestBodyBuilder()
                .addProperty("goods_id", goosId)
                .addProperty("book_id", bookId);
        RequestBody requestBody = bodyBuilder.build();
        return ComicApi.getApi().prePayAli(requestBody)
                .compose(ComicApiImpl.<PrePayOrderResponseAli>getApiTransformer2());
    }

    @Override
    public Observable<PrePayOrderResponseHuifubao> createPreOrderHuifubao(long goosId, long book_id) {
        RequestBodyBuilder bodyBuilder = new RequestBodyBuilder()
                .addProperty("goods_id", goosId)
                .addProperty("book_id", book_id);
        RequestBody requestBody = bodyBuilder.build();
        return ComicApi.getApi().prePayHuifubao(requestBody)
                .compose(ComicApiImpl.<PrePayOrderResponseHuifubao>getApiTransformer2());
    }

}
