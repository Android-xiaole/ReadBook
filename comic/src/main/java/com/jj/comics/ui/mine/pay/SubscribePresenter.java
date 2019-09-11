package com.jj.comics.ui.mine.pay;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.common.net.ComicSubscriber;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.CommonStatusResponse;
import com.jj.comics.data.model.PayInfo;
import com.jj.comics.data.model.PayInfoResponse;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.LogoutEvent;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class SubscribePresenter extends BasePresenter<BaseRepository, SubscribeContract.ISubscribeView> implements SubscribeContract.ISubscribePresenter {

    @Override
    public void getUserPayInfo() {
        UserRepository.getInstance().getUserPayInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<PayInfoResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<PayInfoResponse>() {
                    @Override
                    public void onNext(PayInfoResponse response) {
                        PayInfo payInfo = response.getData();
                        if (payInfo!=null){
                            getV().showDiaLog(payInfo);
                        }else{
                            onFail(new NetError("用户余额信息为空",response.getCode()));
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        if (getV() instanceof SubscribeActivity){
                            ((SubscribeActivity) getV()).finish();
                        }
                        if (error.getType() == NetError.AuthError) {
                            EventBusManager.sendLogoutEvent(new LogoutEvent());
                        }else{
                            ToastUtil.showToastShort(error.getMessage());
                        }
                    }
                });
    }

    @Override
    public void subscribeComic(long bookId,long chapterId) {
        UserRepository.getInstance().subscribe(bookId,chapterId)
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<CommonStatusResponse>bindLifecycle())
                .subscribe(new ComicSubscriber<CommonStatusResponse>() {
                    @Override
                    public void onNext(CommonStatusResponse response) {
                        if (response.getData().getStatus()){
                            getV().onSubscribe(response);
                        }else{
                            ToastUtil.showToastShort(response.getMessage());
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().onSubscribeFail(error);
                    }
                });
    }

    @Override
    public boolean useEventBus() {
        return false;
    }
}
