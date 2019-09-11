package com.jj.comics.ui.mine.settings;

import com.jj.base.mvp.IView;
import com.jj.comics.data.model.ResponseModel;
import com.jj.comics.data.model.UserInfo;

public interface BindPhoneContract {

    interface IBindPhoneView extends IView {
        //更新用户信息的回调
        void onUpdateUserInfo(UserInfo userInfo);

        void alterSuccess(ResponseModel responseModel);

        //设置获取验证码倒计时文字的回调
        void setCuntDownText(String text, boolean enable);
    }

    interface IBindPhonePresenter {
        //绑定手机号
        void alterPhone(String mobile, String verify);

        //获取用户信息
        void getUserData();

        //获取验证码
        void getCode(String mobile);
    }
}
