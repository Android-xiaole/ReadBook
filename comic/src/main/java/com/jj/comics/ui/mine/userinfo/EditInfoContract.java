package com.jj.comics.ui.mine.userinfo;

import com.jj.base.mvp.IView;
import com.jj.base.net.NetError;
import com.jj.comics.data.model.UserInfo;

public class EditInfoContract {

    interface IEditInfoView extends IView {

        void onLoadFail(NetError error);
    }

    interface IEditInfoPresenter {

        //更新用户信息
        void updateUserInfo(String avatar, String nickname, int sex);
    }
}
