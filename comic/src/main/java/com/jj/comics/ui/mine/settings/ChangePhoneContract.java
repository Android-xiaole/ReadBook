package com.jj.comics.ui.mine.settings;

import com.jj.base.mvp.IView;
import com.jj.comics.data.model.ResponseModel;
import com.jj.comics.data.model.UserInfo;

public interface ChangePhoneContract {

    interface IBindPhoneView extends IView {

        void alterSuccess(UserInfo userInfo);

        //设置获取验证码倒计时文字的回调
        void setCuntDownText(String text, boolean enable);
    }

    interface IBindPhonePresenter {
        //绑定手机号
        void alterPhone(String mobile, String verify);

        //获取验证码
        void getCode(String mobile);
    }
}
