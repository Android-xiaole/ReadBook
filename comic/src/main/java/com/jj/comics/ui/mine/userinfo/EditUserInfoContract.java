package com.jj.comics.ui.mine.userinfo;

import android.app.Activity;
import android.content.Context;

import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.jj.base.mvp.IView;
import com.jj.comics.data.model.UserInfo;

import java.io.File;

public interface EditUserInfoContract {

    interface IEditUserInfoView extends IView {
        //上传头像完成的回调
        void onImgUploadComplete(String headImgUrl);

        //更新用户信息完成的回调
        void onUpdateUserInfoComplete(UserInfo user, String msg);

        //获取上下文
        Activity getActivity();
    }

    interface IEditUserInfoPresenter {
        //展示地理位置选择弹窗
        void showAreaPickerView(EditUserInfoPresenter.OnAreaSelectLisenter listener);

        //上传头像
        void uploadImage(UserInfo userInfo, File file, String filePath);

        //展示性别选择弹窗
        void showSexPickerView(EditUserInfoPresenter.OnSexSelectLisenter listener);

        //展示
        void showDataPickerView(OnTimeSelectListener listener);

        //初始化地区json数据
        void initAreaJsonData(Context context);

        //更新用户信息
        void updateUserInfo(UserInfo userInfo);
    }
}
