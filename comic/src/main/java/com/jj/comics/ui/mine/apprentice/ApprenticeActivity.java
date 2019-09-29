package com.jj.comics.ui.mine.apprentice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.android.material.tabs.TabLayout;
import com.jj.base.ui.BaseActivity;
import com.jj.base.ui.BaseFragment;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.ViewPagerAdapter;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.constants.RequestCode;
import com.jj.comics.ui.dialog.ShareDialog;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.widget.comic.toolbar.ComicToolBar;

import java.util.ArrayList;

import butterknife.BindView;

@Route(path = RouterMap.COMIC_MINE_APPRENTICE_ACTIVITY)
public class ApprenticeActivity extends BaseActivity <ApprenticePresenter> implements ApprenticeContract.IApprenticeView{
    @BindView(R2.id.tab_apprentice)
    TabLayout mTabLayout;
    @BindView(R2.id.vp_apprentice)
    ViewPager mViewPager;
    @BindView(R2.id.toolBar)
    ComicToolBar mToolBar;
    @BindView(R2.id.btn_shoutu)
    TextView mBtnST;
    @Override
    protected void initData(Bundle savedInstanceState) {
        ArrayList<BaseFragment> fragments = new ArrayList<>();
        fragments.add(new TuziFragment());
        fragments.add(new TusunFragment());
        String[] titles = {"徒子","徒孙"};
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
                fragments, titles);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOffscreenPageLimit(0);
        mTabLayout.setupWithViewPager(mViewPager);

        mBtnST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LoginHelper.interruptLogin(ApprenticeActivity.this, null)) {
                    share();
                }
            }
        });

        mToolBar.addChildClickListener(new ComicToolBar.OnComicToolBarListener() {
            @Override
            public void onComicToolBarLeftIconClick(View childView) {
                finish();
            }

            @Override
            public void onComicToolBarRightIconClick(View childView) {

            }

            @Override
            public void onComicToolBarRightTextClick(View childView) {
                if (LoginHelper.interruptLogin(ApprenticeActivity.this, null)) {
                    share();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.LOGIN_REQUEST_CODE && resultCode == RESULT_OK) {
            if (LoginHelper.interruptLogin(ApprenticeActivity.this, null)) {
                share();
            }
        }
    }

    private void share() {
        ShareDialog shareDialog = new ShareDialog(ApprenticeActivity.this,"我的徒弟","");
        shareDialog.show();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.comic_activity_appretice;
    }

    @Override
    public ApprenticePresenter setPresenter() {
        return new ApprenticePresenter();
    }
}
