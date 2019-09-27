package com.jj.comics.data.biz.goods;

import com.jj.base.net.ComicApiImpl;
import com.jj.comics.common.net.ComicApi;
import com.jj.comics.common.net.RequestBodyBuilder;
import com.jj.comics.data.model.PrePayOrderResponseAli;
import com.jj.comics.data.model.PrePayOrderResponseWx;

import io.reactivex.Observable;
import okhttp3.RequestBody;

public class GoodsRepository implements GoodsDataSource {

    private static GoodsRepository INSTANCE;

    private GoodsRepository() {
    }

    public static GoodsRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GoodsRepository();
        }
        return INSTANCE;
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
    public Observable<PrePayOrderResponseAli> createPreOrderAli(long goosId, long bookId) {
        RequestBodyBuilder bodyBuilder = new RequestBodyBuilder()
                .addProperty("goods_id", goosId)
                .addProperty("book_id", bookId);
        RequestBody requestBody = bodyBuilder.build();
        return ComicApi.getApi().prePayAli(requestBody)
                .compose(ComicApiImpl.<PrePayOrderResponseAli>getApiTransformer2());
    }


}
