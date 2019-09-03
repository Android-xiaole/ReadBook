package com.jj.comics.ui.mine.userinfo;

import com.jj.base.mvp.IView;
import com.jj.base.ui.BaseFragment;

import java.util.List;

public interface CheckUserInfoContract {

    interface ICheckUserInfoView extends IView {

    }

    interface ICheckUserInfoPresenter {
        String[] getTabNames();
        List<BaseFragment> getFragments();
    }
}
