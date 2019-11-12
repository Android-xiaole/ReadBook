package com.jj.comics.ui.mine.login;

import android.text.TextUtils;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.net.ApiSubscriber2;
import com.jj.base.net.NetError;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.common.constants.LoginTypeEnum;
import com.jj.comics.data.biz.user.UserRepository;
import com.jj.comics.data.model.LoginResponse;
import com.jj.comics.data.model.ResponseModel;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.util.RegularUtil;
import com.jj.comics.util.SharedPreManger;
import com.jj.comics.util.reporter.ActionReporter;
import com.umeng.analytics.MobclickAgent;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

/**
 * Author ：le
 * Date ：2019-11-07 11:40
 * Description ：
 */
public class GetCodePresenter extends BasePresenter<BaseRepository, GetCodeContract.View> implements GetCodeContract.Presenter {


    @Override
    public void getCode(String mobile) {
        UserRepository.getInstance().getSecurityCode(getV().getClass().getName(), mobile)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiSubscriber2<ResponseModel>() {
                    @Override
                    protected void onFail(NetError error) {
                        getV().showToastShort(error.getMessage());
                    }

                    @Override
                    public void onNext(ResponseModel responseModel) {

                    }
                });
    }

    /**
     * 手机号登录
     * @param phone
     * @param code
     * @param inviteCode
     */
    @Override
    public void phoneLogin(String phone, String code, String inviteCode) {
        if (TextUtils.isEmpty(phone)) {
            getV().showToastShort("请输入手机号");
            return;
        }
        if (!RegularUtil.isMobile(phone)) {
            getV().showToastShort("请输入正确的手机号");
            return;
        }
        getV().showProgress();
        UserRepository.getInstance().loginBySecurityCode(phone, code, inviteCode)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<LoginResponse, ObservableSource<UserInfo>>() {
                    @Override
                    public ObservableSource<UserInfo> apply(LoginResponse responseModel) throws Exception {
                        if (responseModel.getData() != null) {
                            UserInfo user_info = responseModel.getData().getUser_info();
                            if (user_info != null) {
                                user_info.setLogin_type(LoginTypeEnum.PHONE.name());
                                SharedPreManger.getInstance().saveToken(responseModel.getData().getBearer_token());
                                return UserRepository.getInstance().saveUser(user_info);
                            }
                        }
                        return Observable.error(NetError.noDataError());
                    }
                })
                .as(this.<UserInfo>bindLifecycle())
                .subscribe(new LoginApiSubscriber());
    }

    /**
     * 第三方登录绑定手机号
     */
    @Override
    public void bindPhone(String phoneNum, String code) {
        getV().showProgress();
        UserRepository.getInstance().bindPhone(phoneNum,code)
                .flatMap(new Function<LoginResponse, ObservableSource<UserInfo>>() {
                    @Override
                    public ObservableSource<UserInfo> apply(LoginResponse loginResponse) throws Exception {
                        if (loginResponse.getData()!=null&&loginResponse.getData().getUser_info()!=null){
                            SharedPreManger.getInstance().saveToken(loginResponse.getData().getBearer_token());
                            return UserRepository.getInstance().saveUser(loginResponse.getData().getUser_info());
                        }else{
                            return Observable.error(NetError.noDataError());
                        }
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .as(bindLifecycle())
                .subscribe(new LoginApiSubscriber());
    }

    private class LoginApiSubscriber extends ApiSubscriber2<UserInfo> {

        @Override
        protected void onFail(NetError error) {
            if (getV() != null) {
                getV().showToastShort(error.getMessage());
                getV().hideProgress();
            }
        }

        @Override
        public void onNext(UserInfo userInfo) {
            if (userInfo != null) {
                ActionReporter.reportAction(ActionReporter.Event.LOGIN, null, null, null);
                MobclickAgent.onProfileSignIn(userInfo.getLogin_type(), userInfo.getUid() + "");
            }
            if (getV() != null) {
                getV().hideProgress();
                getV().setResultAndFinish();
            }
        }
    }
}
