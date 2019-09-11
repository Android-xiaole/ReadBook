package com.jj.comics.ui.mine.settings;

import com.jj.base.mvp.IView;
import com.jj.base.ui.BaseActivity;
import com.jj.comics.data.model.UpdateModelProxy;

public interface SettingContract {

    interface ISettingView extends IView {
        //设置缓存大小的回调
        void setCacheSize(String cacheSize);
    }

    interface ISettingPresenter {
        //清楚缓存
        void clearCache();

        //获取缓存大小
        void getCacheSize();
    }
}
