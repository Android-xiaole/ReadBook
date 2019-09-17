package com.jj.comics.ui.mine.login;

import com.jj.base.mvp.IView;
import com.jj.comics.data.model.LoginResponse;
import com.jj.comics.data.model.UserInfo;

public interface BindPhoneContract {

    interface Presenter {
        //获取验证码
        void getCode(String phoneNum,String type);

        //绑定手机号码
        void bindPhone(String phoneNum,String code,String inviteCode,String openid);
    }

    interface View extends IView {

        void onBindPhone(UserInfo userInfo);

    }
}
