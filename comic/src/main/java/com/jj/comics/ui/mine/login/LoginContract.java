package com.jj.comics.ui.mine.login;

import com.jj.base.mvp.IView;
import com.jj.base.ui.BaseActivity;
import com.jj.comics.data.model.UserInfo;

import io.reactivex.Flowable;

public interface LoginContract {

    interface ILogoinView extends IView {
        //跳转的回调
        void setResultAndFinish();

        //获取验证码成功的回调
        void onGetCode(boolean is_first_login);
    }

    interface ILoginPresenter {

        //获取验证码
        void getVerifyCode(String mobile);

        //qq登录
        void qqLogin();

        //微博登录
        void wbLogin();

        //微信登录
        void wxLogin();

        //获取用户协议
        CharSequence getAgreementText();

        //用户信息保存到本地成功
//        void saveUserInfoSuccess();
    }
}
