package com.jj.comics.ui.mine.login;

import com.jj.base.mvp.IView;
import com.jj.base.ui.BaseActivity;

public interface LoginContract {

    interface ILogoinView extends IView {
        //设置验证码显示倒计时的回调
        void setCuntDownText(String text, boolean enable);

        //跳转的回调
        void setResultAndFinish();

        void onTextChanged();
    }

    interface ILoginPresenter {

        //获取验证码
        void getVerifyCode(String mobile);

        //验证码登录
        void loginByVerifyCode(boolean isCheck, String phone, String psw);

        //qq登录
        void qqLogin(boolean isCheck, BaseActivity loginActivity);

        //微博登录
        void wbLogin(boolean isCheck, BaseActivity activity);

        //微信登录
        void wxLogin(boolean isCheck);

        //获取用户协议
        CharSequence getAgreementText();

        //用户信息保存到本地成功
//        void saveUserInfoSuccess();
    }
}
