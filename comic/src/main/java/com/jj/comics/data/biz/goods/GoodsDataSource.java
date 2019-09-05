package com.jj.comics.data.biz.goods;

import com.jj.comics.data.model.PrePayOrderResponseAli;
import com.jj.comics.data.model.PrePayOrderResponseHuifubao;
import com.jj.comics.data.model.PrePayOrderResponseWx;
import com.jj.comics.data.model.ResponseModel;
import com.jj.comics.data.model.RewardGiftsResponse;
import com.jj.comics.data.model.VIPListResponse;

import io.reactivex.Observable;
import io.reactivex.Observable;

public interface GoodsDataSource {

    //获取全部会员类型和对应类型会员的信息,新版本使用0320
    Observable<VIPListResponse> getVIPList(String retryTag);

    //兑换码兑换金币
    Observable<ResponseModel> goldExchange(long userId, String code);

    //获取打上礼物列表
    Observable<RewardGiftsResponse> getRewardGoodsList();

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

    /**
     * 汇付宝下单
     *
     * @param goosId
     * @param book_id
     * @return
     */
    Observable<PrePayOrderResponseHuifubao> createPreOrderHuifubao(long goosId, long book_id);
}
