package com.jj.comics.ui.mine.settings;

import com.jj.base.mvp.IView;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseActivity;
import com.jj.comics.data.model.RestResponse;
import com.jj.comics.data.model.UpdateModelProxy;

public interface SettingContract {

    interface ISettingView extends IView {
        //设置缓存大小的回调
        void setCacheSize(String cacheSize);
        //自动购买消息挂件
        void rest(RestResponse response);
        void showError(NetError netError);
    }

    interface ISettingPresenter {
        //清楚缓存
        void clearCache();

        //获取缓存大小
        void getCacheSize();
        void getRest();

        //设置自动购买和消息挂件
        void setAuto(int autoBuy, int receive);
    }
}
