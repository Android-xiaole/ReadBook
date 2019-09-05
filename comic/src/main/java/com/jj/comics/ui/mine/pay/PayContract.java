package com.jj.comics.ui.mine.pay;

import android.app.Activity;

import com.jj.base.mvp.IView;
import com.jj.comics.data.model.GoodsPriceModel;
import com.jj.comics.data.model.PayCenterInfoResponse;
import com.jj.comics.data.model.ProductPayTypeEnum;
import com.jj.comics.data.model.RechargeCoinResponse;
import com.jj.comics.data.model.UserInfo;

import java.util.List;

public interface PayContract {

    interface IPayView extends IView {
        //加载支付页面的数据的回调
        void fillData(List<PayCenterInfoResponse.PayCenterInfo> rechargeCoinList);

        //数据加载失败
        void loadFail(String msg);

        //支付失败的回调
        void payFail(String msg);

        //支付成功的回调
        void onPaySuccess();

        //获取用户信息的回调
        void onGetUserData(UserInfo userInfo);
    }

    interface IPayPresenter {
        //获取支付页面的数据
        void loadData();

        //温馨提示
        CharSequence getReminderText(Activity activity);

        void payWx(Activity activity, long goodsId, long book_id);

        void payAli(Activity activity, long goodsId, long book_id);

        void payHuifubao(Activity activity, long goodsId, long book_id);
    }
}
