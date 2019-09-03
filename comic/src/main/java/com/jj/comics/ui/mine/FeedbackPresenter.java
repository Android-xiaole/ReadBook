package com.jj.comics.ui.mine;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.data.biz.pruduct.ProductRepository;
import com.jj.comics.data.model.ResponseModel;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class FeedbackPresenter extends BasePresenter<BaseRepository, FeedBackContract.IFeedBackView> implements FeedBackContract.IFeedBackPresenter {

    @Override
    public void uploadMsg(String msg) {
        ProductRepository.getInstance().uploadFeedback( msg)
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<ResponseModel>bindLifecycle())
                .subscribe(new ApiSubscriber2<ResponseModel>() {
                    @Override
                    public void onNext(ResponseModel responseModel) {
                        getV().onComplete(true);
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().onComplete(false);
                        ToastUtil.showToastShort(error.getMessage());
                    }
                });
    }

    @Override
    public boolean useEventBus() {
        return false;
    }
}
