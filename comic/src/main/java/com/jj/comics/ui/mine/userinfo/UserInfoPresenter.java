package com.jj.comics.ui.mine.userinfo;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.comics.data.biz.content.ContentRepository;
import com.jj.comics.data.model.OSSResponse;
import com.jj.comics.data.model.UserInfo;

import java.io.File;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class UserInfoPresenter extends BasePresenter<BaseRepository, UserInfoContract.IUserInfoView> implements UserInfoContract.IUserInfoPresenter {
    @Override
    public void uploadImage(UserInfo userInfo, File file, String filePath) {

    }

    @Override
    public void updateUserInfo(UserInfo userInfo) {

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
