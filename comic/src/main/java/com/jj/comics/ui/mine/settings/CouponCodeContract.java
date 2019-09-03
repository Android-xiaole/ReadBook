package com.jj.comics.ui.mine.settings;

import com.jj.base.mvp.IView;

public interface CouponCodeContract {

    interface ICouponCodeView extends IView {
        //兑换金币成功的回调
        void onSuccess();
    }

    interface ICouponCodePresenter {
        //兑换金币
        void goldExchange(long userId, String code);
    }
}
