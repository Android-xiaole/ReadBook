package com.jj.comics.ui.mine.userinfo;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.data.biz.content.ContentRepository;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.OSSResponse;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.data.model.UserInfoResponse;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.UpdateUserInfoEvent;

import java.io.File;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class UserInfoPresenter extends BasePresenter<BaseRepository, UserInfoContract.IUserInfoView> implements UserInfoContract.IUserInfoPresenter {
    @Override
    public void uploadImage(UserInfo userInfo, File file, String filePath) {

    }

    @Override
    public void updateUserInfo(String avatar, String nickname, int sex) {
        UserRepository.getInstance().updateUserInfo(avatar, nickname, sex)
                .as(this.<UserInfo>bindLifecycle())
                .subscribe(new ApiSubscriber2<UserInfo>() {
                    @Override
                    public void onNext(UserInfo userInfo) {
                        if (userInfo != null) {
                            EventBusManager.sendUpdateUserInfoEvent(new UpdateUserInfoEvent(userInfo));
                            getV().onImgUploadComplete(userInfo.getAvatar());
                        } else {
                            getV().onLoadFail(NetError.noDataError());
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().onLoadFail(error);
                    }
                });
    }

    @Override
    public void getOSSConfig() {
        ContentRepository.getInstance()
                .getOSSConfig(getV().getClass().getName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<OSSResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<OSSResponse>() {
                    @Override
                    public void onNext(OSSResponse response) {
                        OSSResponse.DataBean data = response.getData();
                        if (data != null) {
                            getV().onLoadConfig(data);
                        } else {
                            getV().onLoadFail(NetError.noDataError());
                        }
                    }

                    @Override
                    protected void onFail(NetError error) {
                        getV().onLoadFail(error);
                    }
                });
    }
}
