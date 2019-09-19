package com.jj.comics.util;

import android.app.Activity;
import android.text.TextUtils;

import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.common.constants.Constants;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.data.biz.goods.GoodsRepository;
import com.jj.comics.data.model.PrePayOrderResponseWx;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PayUtils {
    /**
     * 微信支付
     *
     * @param activity
     * @param goodsId
     * @param book_id
     */
    public static void wxPay(final Activity activity, long goodsId, long book_id) {
        GoodsRepository.getInstance()
                .createPreOrderWx(goodsId, book_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiSubscriber2<PrePayOrderResponseWx>() {
                    @Override
                    public void onNext(PrePayOrderResponseWx prePayOrderResponseWx) {
                        PrePayOrderResponseWx.DataBean data = prePayOrderResponseWx.getData();
                        IWXAPI wxApi = TencentHelper.getWxApi(Constants.WX_APP_ID_PAY());
                        PayReq req = new PayReq();
                        //给req对象赋值
                        req.appId = data.getAppid();
                        req.partnerId = data.getMch_id();//    商户号
                        req.prepayId = data.getPrepay_id();//  预付款ID
                        req.nonceStr = data.getNonce_str();//随机数
                        req.timeStamp = data.getTimeStamp() + "";//时间戳 需转换为s
                        req.packageValue = "Sign=WXPay";//固定值Sign=WXPay
                        req.sign = data.getSign().toUpperCase();//签名

                        wxApi.sendReq(req);//将订单信息对象发送给微信服务器，即发送支付请求
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToastLong(error.getMessage());
                        ((BaseActivity)activity).hideProgress();
                    }
                });
    }

    /**
     * 创建支付请求参数
     *
     * @return 请求体
     */
    public static String getAlipayResponse(String resultStatus) {
        if (TextUtils.equals(resultStatus, "9000")) {
            return "支付成功";
        } else if (TextUtils.equals(resultStatus, "8000")) {
            return "正在处理中，请稍后刷新查看结果";
        } else if (TextUtils.equals(resultStatus, "4000")) {
            return "支付失败";
        } else if (TextUtils.equals(resultStatus, "5000")) {
            return "太快啦，请稍后再支付~";
        } else if (TextUtils.equals(resultStatus, "6001")) {
            return "您取消了支付";
        } else if (TextUtils.equals(resultStatus, "6002")) {
            return "网络连接错误";
        } else if (TextUtils.equals(resultStatus, "6004")) {
            return "正在处理中，请稍后刷新查看结果";
        }
        return "支付失败";
    }
}
