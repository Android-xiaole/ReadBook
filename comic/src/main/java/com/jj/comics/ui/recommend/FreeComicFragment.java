package com.jj.comics.ui.recommend;


import android.os.Bundle;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.android.material.tabs.TabLayout;
import com.gyf.barlibrary.ImmersionBar;
import com.jj.base.ui.BaseCommonFragment;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.common.constants.Constants;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;

/**
 * 限免页面（现在是免费）
 */
@Route(path = RouterMap.COMIC_RECENTLY_FRAGMENT)
public class FreeComicFragment extends BaseCommonFragment{

//    @BindView(R2.id.swipeRefreshLayout)
//    SwipeRefreshLayout mRefresh;//限时免费列表
//    @BindView(R2.id.recyclerView)
//    RecyclerView mRecyclerView;//限时免费列表
//    @BindView(R2.id.recommend_float_btn)
//    ImageView mToTop;

    @BindView(R2.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R2.id.viewPager)
    ViewPager viewPager;

    private List<Fragment> fragments = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    private int currentIndex;//记录当前fragment索引

    @Override
    public void initData(Bundle savedInstanceState) {
        ImmersionBar.with(this).reset().statusBarDarkFont(true,0.2f).init();

        Bundle arguments = new Bundle();
        //本周限免
        FreeComicChildFragment freeComicChildFragment1 = new FreeComicChildFragment();
        arguments.putString("pageCode", Constants.RequestBodyKey.TYPE_THIS_WEEK);
        freeComicChildFragment1.setArguments(arguments);
        fragments.add(freeComicChildFragment1);

        Bundle nextArgs = new Bundle();
        //下周预告
        FreeComicChildFragment freeComicChildFragment2 = new FreeComicChildFragment();
        nextArgs.putString("pageCode",Constants.RequestBodyKey.TYPE_NEXT_WEEK);
        freeComicChildFragment2.setArguments(nextArgs);
        fragments.add(freeComicChildFragment2);

        titles.add(getString(R.string.comic_recommend_free_title1));
        titles.add(getString(R.string.comic_recommend_free_title2));
        viewPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return titles.get(position);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected boolean hasFragment() {
        return true;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
//        //这里需要主动调用当前加载的子fragment的生命周期，去处理友盟统计相关逻辑，系统不会自动调用
        fragments.get(currentIndex).setUserVisibleHint(!hidden);
        ImmersionBar.with(this).reset().statusBarDarkFont(true,0.2f).init();
    }


    @Override
    public int getLayoutId() {
        return R.layout.comic_fragment_recommend_free;
    }

    @Override
    public FreeListPresenter setPresenter() {
        return new FreeListPresenter();
    }
}
