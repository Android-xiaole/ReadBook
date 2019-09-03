package com.jj.comics.ui.mine.userinfo;

import com.jj.base.BaseApplication;
import com.jj.base.mvp.BaseRepository;
import com.jj.base.ui.BaseFragment;
import com.jj.base.mvp.BasePresenter;
import com.jj.comics.R;

import java.util.ArrayList;
import java.util.List;

public class CheckUserInfoPresenter extends BasePresenter<BaseRepository,CheckUserInfoContract.ICheckUserInfoView> implements CheckUserInfoContract.ICheckUserInfoPresenter{


    @Override
    public String[] getTabNames() {
        return new String[]{BaseApplication.getApplication().getString(R.string.comic_comments_text), BaseApplication.getApplication().getString(R.string.comic_app_raise_text)};
    }

    @Override
    public List<BaseFragment> getFragments() {
        List<BaseFragment> list = new ArrayList<>();
        list.add(new MyCommentsFragment());
        list.add(new AppRaiseFragment());
        return list;
    }

}
