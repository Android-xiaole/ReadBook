package com.jj.comics.ui.mine.userinfo;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.data.biz.content.ContentRepository;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.LoginResponse;
import com.jj.comics.data.model.OSSResponse;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.data.model.UserInfoResponse;
import com.jj.comics.util.SharedPreManger;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class EditInfoPresenter extends BasePresenter<BaseRepository, EditInfoContract.IEditInfoView> implements EditInfoContract.IEditInfoPresenter {

    @Override
    public void updateUserInfo(String avatar, String nickname, int sex) {
        getV().showProgress();
        UserRepository.getInstance().updateUserInfo(avatar, nickname, sex)
                .as(this.<UserInfo>bindLifecycle())
                .subscribe(new ApiSubscriber2<UserInfo>() {
                    @Override
                    public void onNext(UserInfo userInfo) {
                        getV().onSuccess(userInfo);
                    }

                    @Override
                    protected void onFail(NetError error) {
                        ToastUtil.showToastShort(error.getMessage());
                    }

                    @Override
                    protected void onEnd() {
                        getV().hideProgress();
                    }
                });
    }
}
