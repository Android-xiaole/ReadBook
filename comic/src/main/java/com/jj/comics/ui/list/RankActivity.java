package com.jj.comics.ui.list;

import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.android.material.tabs.TabLayout;
import com.gyf.barlibrary.ImmersionBar;
import com.jj.base.ui.BaseActivity;
import com.jj.base.ui.BaseFragment;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.ViewPagerAdapter;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.ui.list.rank.CommonRankFragment;
import com.jj.comics.widget.CustomTab;

import java.util.ArrayList;
import java.util.List;

import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;

@Route(path = RouterMap.COMIC_RANK_ACTIVITY)
public class RankActivity extends BaseActivity<RankPresenter>{
    @BindView(R2.id.tl_rank)
    TabLayout mTabLayout;
    @BindView(R2.id.vp_rank)
    ViewPager mViewPager;

    private ViewPagerAdapter mAdapter;

    @Override
    public void initData(Bundle savedInstanceState) {
        String[] tabNames = new String[]{"人气榜", "新作榜", "畅销榜"};
        String[] tabIds = new String[]{"popularity", "justzzu", "hot_sell"};
        mTabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                if (view != null) view.setSelected(true);
                mViewPager.setCurrentItem(mTabLayout.getSelectedTabPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                if (view != null) view.setSelected(false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        List<BaseFragment> pages = new ArrayList<>();
//        pages.add(new CommonRankFragment());
//        pages.add(new NewBookFragment());
//        pages.add(new BestSaleFragment());
//        pages.add(new OtakuFragment());

        mAdapter = new ViewPagerAdapter(getSupportFragmentManager(), pages, null);
        mViewPager.setAdapter(mAdapter);
//        mTabLayout.setupWithViewPager(mViewPager);
//        for (String tabName : tabNames) {
//            mTabLayout.addTab(mTabLayout.newTab().setCustomView(new CustomTab(context).setText(tabName)));
//        }
//        getP().loadData();

        ArrayList<BaseFragment> data = new ArrayList<>();
        for (int i=0;i<tabIds.length;i++) {
            mTabLayout.addTab(mTabLayout.newTab().setCustomView(new CustomTab(context).setText(tabNames[i])));
            CommonRankFragment commonRankFragment = new CommonRankFragment();
            Bundle arguments = new Bundle();
            arguments.putString(Constants.IntentKey.ACTION, tabIds[i]);
            commonRankFragment.setArguments(arguments);
            data.add(commonRankFragment);
        }
        mTabLayout.setTabMode(data.size() > 4 ? TabLayout.MODE_SCROLLABLE : TabLayout.MODE_FIXED);
        mAdapter.setNewData(data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_rank;
    }

    @Override
    public RankPresenter setPresenter() {
        return new RankPresenter();
    }

    @Override
    protected void initImmersionBar() {
        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.comic_ffffff)
//                .navigationBarColor(R.color.comic_ffffff)
                .statusBarDarkFont(true, 0.2f)
                .init();
    }

    @Override
    public boolean hasFragment() {
        return true;
    }
}
