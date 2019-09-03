package com.jj.comics.ui.mine;

import com.jj.base.mvp.IView;

public interface FeedBackContract {

    interface IFeedBackView extends IView {
        //反馈完成的回调
        void onComplete(boolean isComplete);
    }

    interface IFeedBackPresenter {
        //上传反馈信息
        void uploadMsg(String msg);
    }
}
