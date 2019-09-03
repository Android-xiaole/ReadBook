package com.jj.comics.ui.mine.settings;

import com.jj.base.mvp.IView;
import com.jj.base.ui.BaseActivity;
import com.jj.comics.data.model.UpdateModelProxy;

public interface SettingContract {

    interface ISettingView extends IView {
        //展示版本更新弹窗的回调
        void updateAlert(UpdateModelProxy.UpdateModel updateModel);

        //发送信息的回调
        void sendMessage(int what, Object info);
    }

    interface ISettingPresenter {
        //检查版本更新
        void checkUpdate(BaseActivity baseActivity);

        //去下载
        void goDown(String updateAppUrl, BaseActivity baseActivity);
    }
}
