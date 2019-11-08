package com.jj.comics.ui.mine.login;

import com.jj.base.mvp.IView;
import com.jj.comics.data.model.LoginResponse;
import com.jj.comics.data.model.UserInfo;

public interface BindPhoneContract {

    interface Presenter {
        //获取验证码
        void getCode(String phoneNum);
    }

    interface View extends IView {
        void onGetCode();
    }
}
