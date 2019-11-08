package com.jj.comics.ui.mine.login;

import com.jj.base.mvp.IView;

/**
 * Author ：le
 * Date ：2019-11-07 11:41
 * Description ：
 */
public interface GetCodeContract {

    interface Presenter{
        //获取验证码
        void getCode(String mobile);

        //手机号码登录注册
        void phoneLogin(String phone, String code, String inviteCode);

        //第三方登录绑定手机号
        void bindPhone(String phoneNum, String code);
    }

    interface View extends IView {
        void setResultAndFinish();
    }

}
