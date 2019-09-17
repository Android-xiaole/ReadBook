package com.jj.comics.ui.mine.apprentice;

import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.android.material.tabs.TabLayout;
import com.jj.base.ui.BaseActivity;
import com.jj.base.ui.BaseFragment;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.ViewPagerAdapter;

import java.util.ArrayList;

import butterknife.BindView;

@Route(path = RouterMap.COMIC_MINE_APPRENTICE_ACTIVITY)
public class ApprenticeActivity extends BaseActivity <ApprenticePresenter> implements ApprenticeContract.IApprenticeView{
    @BindView(R2.id.tab_apprentice)
    TabLayout mTabLayout;
    @BindView(R2.id.vp_apprentice)
    ViewPager mViewPager;
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
