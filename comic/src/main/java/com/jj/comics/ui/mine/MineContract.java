package com.jj.comics.ui.mine;

import com.jj.base.mvp.IView;
import com.jj.comics.data.model.PayInfo;
import com.jj.comics.data.model.SignAutoResponse;
import com.jj.comics.data.model.UserInfo;

public interface MineContract {

    interface IMineView extends IView {

        void onGetUserInfo(UserInfo userInfo);

        void onGetUserPayInfo(PayInfo payInfo);

//        //设置缓存大小的回调
//        void setCacheSize(String cacheSize);
//
//        void onGetFeedbackStatus(int unReadCount);
//        void fillSignAuto(SignAutoResponse response);
//
//        void onGetTaskInfo(int count);
    }

    interface IMinePresenter  {
        //获取用户信息
        void getUserInfo();

        //获取用户支付相关信息
        void getUserPayInfo();

//        //设置自动购买状态
//        void autoBuy(boolean isChecked);
//
//        //获取自动购买状态
//        boolean hasAllSubscribe();
//
//        //清楚缓存
//        void clearCache();
//
//        void signAuto();
//
//        void getFeedbackStatus();
//
//        void getTaskStatus();
    }
}
