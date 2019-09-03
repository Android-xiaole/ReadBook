package com.jj.comics.ui.mine.settings;

import com.jj.base.mvp.IView;
import com.jj.comics.data.model.ProtocalModel;

public interface UserAgreementContract {

    interface IUserAgreementView extends IView {
        //设置协议信息的回调
        void onFetchData(ProtocalModel.Protocal protocal);
    }

    interface IUserAgreementPresenter {
        //获取登录用户协议
        void getLoginUserAgreement(String agreementKey);
    }
}
