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

import java.io.File;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class EditInfoPresenter extends BasePresenter<BaseRepository, EditInfoContract.IEditInfoView> implements EditInfoContract.IEditInfoPresenter {

    @Override
    public void updateUserInfo(String avatar, String nickname, int sex) {
        UserRepository.getInstance().updateUserInfo(avatar, nickname, sex)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(this.<UserInfoResponse>bindLifecycle())
                .subscribe(new ApiSubscriber2<UserInfoResponse>() {
                    @Override
                    public void onNext(UserInfoResponse response) {
                        UserInfoResponse.DataBean data = response.getData();
                        if (data != null) {
//                            getV().onLoadConfig(data);
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
