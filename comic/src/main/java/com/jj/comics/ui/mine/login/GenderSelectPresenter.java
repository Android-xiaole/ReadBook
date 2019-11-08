package com.jj.comics.ui.mine.login;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.UserInfo;

/**
 * Author ：le
 * Date ：2019-11-08 10:56
 * Description ：
 */
public class GenderSelectPresenter extends BasePresenter<BaseRepository,GenderSelectContract.View> implements GenderSelectContract.Presenter {

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
