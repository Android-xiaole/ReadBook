package com.jj.comics.ui.mine.help;

import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.widget.UserItemView;
import com.jj.comics.widget.comic.toolbar.ComicToolBar;

import butterknife.BindView;

@Route(path = RouterMap.COMIC_HELP_FISSION_ACTIVITY)
public class HelpFissionActivity extends BaseActivity {
    @BindView(R2.id.toolBar)
    ComicToolBar mToolBar;
    @Override
    protected void initData(Bundle savedInstanceState) {
        mToolBar.setTitleText("裂变返利");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.comic_activity_help_detail;
    }

    @Override
    public BasePresenter setPresenter() {
        return null;
    }
}
