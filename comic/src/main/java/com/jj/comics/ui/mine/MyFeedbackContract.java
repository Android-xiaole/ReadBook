package com.jj.comics.ui.mine;

import com.jj.base.mvp.IView;
import com.jj.base.net.NetError;
import com.jj.comics.data.model.FeedbackListResponse;

public interface MyFeedbackContract {

    interface Presenter{
        void getFeedbackList(int pageNum);
    }

    interface View extends IView{
        void onLoadFeedbackList(FeedbackListResponse feedbackModel);
        void onLoadFeedbackListFail(NetError netError);
    }
}
