package com.jj.comics.data.biz.goods;

import com.jj.comics.data.model.PrePayOrderResponseAli;
import com.jj.comics.data.model.PrePayOrderResponseWx;

import io.reactivex.Observable;

public interface GoodsDataSource {

    /**
     * 微信支付下单
     *
     * @param goosId
     * @param book_id
     * @return
     */
    Observable<PrePayOrderResponseWx> createPreOrderWx(long goosId, long book_id);

    /**
     * 支付宝下单
     *
     * @param goosId
     * @param book_id
     * @return
     */
    Observable<PrePayOrderResponseAli> createPreOrderAli(long goosId, long book_id);

}
