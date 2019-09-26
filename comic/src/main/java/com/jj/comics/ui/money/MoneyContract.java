package com.jj.comics.ui.money;

import com.jj.base.mvp.IView;
import com.jj.comics.data.model.PayInfo;
import com.jj.comics.data.model.ShareRecommendResponse;

import java.util.List;

public interface MoneyContract {
    interface IMoneyView extends IView {
        void onGetUserPayInfo(PayInfo payInfo);

        void onGetShareRecommend(List<ShareRecommendResponse.DataBean> list);

        //分享截图
        void shareImage(String content);
    }

    interface IMoneyPresenter {
        void getUserPayInfo();

        void getShareRecommend();
    }
}
