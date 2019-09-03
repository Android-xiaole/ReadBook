package com.jj.comics.ui.mine;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.FeedbackListResponse;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MyFeedbackPresenter extends BasePresenter<BaseRepository,MyFeedbackContract.View> implements MyFeedbackContract.Presenter {

    @Override
    public void getFeedbackList(int pageNum) {
        UserRepository.getInstance().getFeedbackList(pageNum,getV().getClass().getSimpleName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .map(new Function<FeedbackListResponse, FeedbackListResponse>() {
//                    @Override
//                    public FeedbackListResponse apply(FeedbackListResponse feedbackModel) throws Exception {
//                        Iterator<FeedbackModel> iterator = feedbackModel.getData().iterator();
//                        while (iterator.hasNext()){
//                            FeedbackModel next = iterator.next();
//                            if (next.getContent() == null||next.getContent().length() == 0){
//                                iterator.remove();
//                            }
//                        }
//                        return feedbackModel;
//                    }
//                })
                .as(this.<FeedbackListResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<FeedbackListResponse>() {
                    @Override
                    public void onNext(FeedbackListResponse feedbackModel) {
                        getV().onLoadFeedbackList(feedbackModel);
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToastShort(error.getMessage());
                        getV().onLoadFeedbackListFail(error);
                    }
                });
    }
}
