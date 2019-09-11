package com.jj.comics.ui.mine.userinfo;

import com.jj.base.mvp.IView;
import com.jj.base.net.NetError;
import com.jj.comics.data.model.OSSResponse;
import com.jj.comics.data.model.UserInfo;

import java.io.File;

public class UserInfoContract {
    interface IUserInfoView extends IView {
        //上传头像完成的回调
        void onImgUploadComplete(String headImgUrl);

        void onLoadFail(NetError error);

        void onLoadConfig(OSSResponse.DataBean ossConfig);
    }

    interface IUserInfoPresenter {
        //上传头像
        void uploadImage(UserInfo userInfo, File file, String filePath);

        //更新用户信息
        void updateUserInfo(String avatar, String nickname, int sex);

        void getOSSConfig();
    }
}
