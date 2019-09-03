package com.jj.comics.ui.mine;

import android.app.Activity;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.jj.base.BaseApplication;
import com.jj.base.log.LogUtil;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.biz.goods.GoodsRepository;
import com.jj.comics.data.model.PrePayOrderResponseAli;
import com.jj.comics.data.model.VIPListResponse;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.PayUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class VIPPresenter extends BasePresenter<BaseRepository, VIPContract.IVIPView> implements VIPContract.IVIPPresenter {

    /**
     * 请求所有
     *
     * @param evict
     */
    @Override
    public void getNewVIPList(final boolean evict) {
        GoodsRepository.getInstance().getVIPList(getV().getClass().getName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<VIPListResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<VIPListResponse>() {
                    @Override
                    public void onNext(VIPListResponse vipListResponse) {
                        if (vipListResponse.getData() != null) {
                            getV().fillData(vipListResponse.getData());
                        } else {
                            getV().dataFail(new NetError(BaseApplication.getApplication().getString(com.jj.base.R.string.base_empty_data), NetError.NoDataError));
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().dataFail(error);
                        LogUtil.d("error", error.getMessage());
                        getV().dismissLoading();
                    }
                });
    }

    /**
     * 微信支付
     *
     * @param activity
     * @param goodsId
     * @param book_id
     */
    @Override
    public void payWx(Activity activity, long goodsId, long book_id) {
        PayUtils.wxPay(activity, goodsId, book_id);
    }

    /**
     * 支付支付
     *
     * @param activity
     * @param goodsId  商品id
     * @param book_id  书籍id
     */
    @Override
    public void payAli(final Activity activity, final long goodsId, final long book_id) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                emitter.onNext(LoginHelper.getOnLineUser() == null);
            }
        }).flatMap(new Function<Boolean, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Boolean loginState) throws Exception {
                return GoodsRepository.getInstance()
                        .createPreOrderAli(goodsId, book_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.newThread())
                        .flatMap(new Function<PrePayOrderResponseAli, ObservableSource<String>>() {
                            @Override
                            public ObservableSource<String> apply(PrePayOrderResponseAli prePayOrderResponseAli) throws Exception {
                                String result = "";
                                PrePayOrderResponseAli.DataBean data = prePayOrderResponseAli.getData();
                                if (data != null) {
                                    PayTask alipay = new PayTask(activity);
                                    Map<String, String> resultMap = alipay.payV2(data.getPay_content(), true);
                                    result = PayUtils.getAlipayResponse(resultMap.get("resultStatus"));
                                    MobclickAgent.onEvent(activity, Constants.UMEventId.PAY_TYPE, "AliPay");
                                    MobclickAgent.onEvent(activity, Constants.UMEventId.PAY_RESULT, result);
                                }
                                return Observable.just(result);
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiSubscriber2<String>() {
                    @Override
                    public void onNext(String response) {
                        if (!TextUtils.isEmpty(response)) {
                            //处理支付宝支付
                            if (TextUtils.equals(response, "支付失败")
                                    || TextUtils.equals(response, "网络连接错误")
                                    || TextUtils.equals(response, "您取消了支付")) {
                                //支付宝支付失败 弹窗提示
                                getV().payFail(response);
                            } else {
                                //支付宝支付失败  toast提示
                                getV().showToastShort(response);
                            }
                        }
                        if (TextUtils.equals("支付成功", response)) {
                            getV().onPaySuccess();
                        }
                        getV().hideProgress();
                    }

                    @Override
                    protected void onFail(NetError error) {
                        if (error != null)
                            getV().payFail(error.getMessage() + "");
                    }
                });
    }

    @Override
    public void payHuifubao(Activity activity, long goodsId, long book_id) {
        // tokenId 汇元订单号
        // agentId 商户号
        // agentBillId 商户订单号
        // pay_type 支付方式

//        GoodsRepository.getInstance()
//                .createPreOrderHuifubao()
//        String tokenId = "H19081209998381R_7594e95733325d162a653a7f249f41fa";
//        int agentId = 2121489;
//        String agentBillId = "NZ_201908121934277791";
//        int pay_type = 30;
//        HPlugin.pay(activity, tokenId + "," + agentId + "," + agentBillId + "," + pay_type);
    }
}
