package com.jj.comics.ui.mine.userinfo;

import com.jj.base.mvp.IView;

public interface AppRaiseContract {

    interface IAppRaiseView extends IView {
    }

    interface IAppRaisePresenter {
        void getMyStarList(int pageNum, int pageSize);
    }
}
