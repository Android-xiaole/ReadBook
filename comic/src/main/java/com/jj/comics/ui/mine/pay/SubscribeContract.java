package com.jj.comics.ui.mine.pay;

import com.jj.base.mvp.IView;
import com.jj.base.net.NetError;
import com.jj.comics.data.model.PayInfo;
import com.jj.comics.data.model.ResponseModel;

public interface SubscribeContract {

    interface ISubscribeView extends IView {
        //展示dialog的回调
        void showDiaLog(PayInfo payInfo);

        //订阅的回调
        void onSubscribe(ResponseModel responseModel);

        //订阅失败的回调
        void onSubscribeFail(NetError netError);
    }

    interface ISubscribePresenter {
        //获取用户支付信息
        void getUserPayInfo();

        //订阅漫画
        void subscribeComic(long bookId,long chapterId);
    }
}
