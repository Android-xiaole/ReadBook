package com.jj.comics.ui.mine;


import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.PayInfoResponse;
import com.jj.comics.data.model.UserInfoResponse;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.LogoutEvent;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class MinePresenter extends BasePresenter<BaseRepository, MineContract.IMineView> implements MineContract.IMinePresenter {

    @Override
    public void getUserInfo() {
        getV().showProgress();
        UserRepository.getInstance().getUserInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<UserInfoResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<UserInfoResponse>() {
                    @Override
                    public void onNext(UserInfoResponse responseModel) {
                        UserInfoResponse.DataBean data = responseModel.getData();
                        if (data!=null&&data.getBaseinfo()!=null){
                            LoginHelper.updateUser(data.getBaseinfo());
                            getV().onGetUserInfo(data.getBaseinfo());
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToastShort(error.getMessage());
                        if (error.getType() == NetError.AuthError) {
                            EventBusManager.sendLogoutEvent(new LogoutEvent());
                        }
                    }

                    @Override
                    protected void onEnd() {
                        super.onEnd();
                        getV().hideProgress();
                    }
                });
    }

    @Override
    public void getUserPayInfo(){
        UserRepository.getInstance().getUserPayInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<PayInfoResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<PayInfoResponse>() {

                    @Override
                    public void onNext(PayInfoResponse payInfoResponse) {
                        getV().onGetUserPayInfo(payInfoResponse.getData());
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToastShort(error.getMessage());
                        if (error.getType() == NetError.AuthError){
                            EventBusManager.sendLogoutEvent(new LogoutEvent());
                        }
                    }
                });
    }

}
