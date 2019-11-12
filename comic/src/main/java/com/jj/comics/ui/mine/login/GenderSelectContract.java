package com.jj.comics.ui.mine.login;

import com.jj.base.mvp.IView;
import com.jj.comics.data.model.UserInfo;

/**
 * Author ：le
 * Date ：2019-11-08 10:56
 * Description ：
 */
public interface GenderSelectContract {

    interface Presenter {
        void updateUserInfo(String avatar, String nickname, int sex);
    }

    interface View extends IView{
        void onSuccess(UserInfo userInfo);
    }
}
