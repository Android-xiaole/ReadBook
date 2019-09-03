package com.jj.maozhua.present;

import com.jj.base.mvp.IView;

public interface SplashContract {

    interface ISplashView extends IView {
        void launch();
        void initConfigFail(String msg);
    }

    interface ISplashPresenter {

        void sendDelayedMessage(int second);
    }
}
