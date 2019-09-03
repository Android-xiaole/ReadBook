package com.jj.comics.ui.bookshelf;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.android.material.tabs.TabLayout;
import com.jj.base.ui.BaseCommonFragment;
import com.jj.base.ui.BaseFragment;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.ViewPagerAdapter;
import com.jj.comics.ui.bookshelf.collection.CollectionFragment;
import com.jj.comics.ui.bookshelf.history.HistoryFragment;
import com.jj.comics.util.eventbus.events.LogoutEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;

/**
 * 书架页面
 */
@Route(path = RouterMap.COMIC_BOOKSHELF_FRAGMENT)
public class BookShelfFragment extends BaseCommonFragment<BookShelfPresenter> implements BookShelfContract.IShelfView {
    //    @BindView(R2.id.fake_statusbar_view_shelf)
//    View mFakeStatusBar;
    @BindView(R2.id.rl_root)
    RelativeLayout rl_root;
    @BindView(R2.id.bookshelf_tab)
    TabLayout mTabLayout;
    @BindView(R2.id.bookshelf_pager)
    ViewPager mPager;
    @BindView(R2.id.item_bookShelf_delete)
    ImageView mDelete;
    @BindView(R2.id.item_bookShelf_done)
    TextView mDone;

    private ViewPagerAdapter mPagerAdapter;
    private List<BaseFragment> mList;
    private int currentIndex = 0;//记录当前显示的fragment索引

    @Override
    public void initData(Bundle savedInstanceState) {
        //设置toolbar距离上端的高度
        int statusBarHeight = ScreenUtils.getStatusBarHeight();
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) rl_root.getLayoutParams();
        lp.topMargin = statusBarHeight;
        rl_root.setLayoutParams(lp);

        mList = new ArrayList<>();
        mList.add(new CollectionFragment());
        HistoryFragment historyFragment = new HistoryFragment();
        mList.add(historyFragment);
        mPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), mList, new String[]{getString(R.string.comic_collect_text), getString(R.string.comic_history)});
        mPager.setOffscreenPageLimit(0);
        mPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mPager);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                currentIndex = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }


    @OnClick({R2.id.item_bookShelf_delete, R2.id.item_bookShelf_done})
    void dealClick(View view) {
        int id = view.getId();
        if (id == R.id.item_bookShelf_delete) {
            setEditMode(true);
        } else if (id == R.id.item_bookShelf_done) {
            setEditMode(false);
        }
    }

    public void setEditMode(boolean editMode) {
//        if (LoginHelper.getOnLineUser() == null) return;
        mDelete.setVisibility(editMode ? View.GONE : View.VISIBLE);
        mDone.setVisibility(editMode ? View.VISIBLE : View.GONE);
        for (BaseFragment baseFragment : mList) {
            if (baseFragment instanceof HistoryFragment)
                ((HistoryFragment) baseFragment).setEditMode(editMode);
            else if (baseFragment instanceof CollectionFragment) {
                ((CollectionFragment) baseFragment).setEditMode(editMode);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void logout(LogoutEvent activity) {
        mDelete.setVisibility(View.VISIBLE);
        mDone.setVisibility(View.GONE);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        //这里需要主动调用当前加载的子fragment的生命周期，去处理友盟统计相关逻辑，系统不会自动调用
        mList.get(currentIndex).setUserVisibleHint(!hidden);
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_fragment_new_bookshlef;
    }

    @Override
    public BookShelfPresenter setPresenter() {
        return new BookShelfPresenter();
    }


    @Override
    protected boolean hasFragment() {
        return true;
    }

    @Override
    public boolean useEventBus() {
        return true;
    }
}
