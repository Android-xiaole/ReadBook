package com.jj.comics.ui.mine.pay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.android.material.tabs.TabLayout;
import com.jj.base.ui.BaseActivity;
import com.jj.base.ui.BaseCommonFragment;
import com.jj.base.ui.BaseFragment;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.ViewPagerAdapter;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.constants.RequestCode;
import com.jj.comics.ui.bookshelf.BookShelfContract;
import com.jj.comics.ui.bookshelf.BookShelfPresenter;
import com.jj.comics.ui.bookshelf.collection.CollectionFragment;
import com.jj.comics.util.eventbus.events.LogoutEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.ScreenUtils;

/**
 * 书架页面
 */
@Route(path = RouterMap.COMIC_PAY_ACTIVITY)
public class PayActivity extends BaseActivity<BookShelfPresenter> implements BookShelfContract.IShelfView {
    //    @BindView(R2.id.fake_statusbar_view_shelf)
//    View mFakeStatusBar;
    @BindView(R2.id.rl_root)
    RelativeLayout rl_root;
    @BindView(R2.id.bookshelf_tab)
    TabLayout mTabLayout;
    @BindView(R2.id.bookshelf_pager)
    ViewPager mPager;
    private ViewPagerAdapter mPagerAdapter;
    private List<BaseFragment> mList;
    private int currentIndex = 0;//记录当前显示的fragment索引
    private long mBookId = 0;
    private String payType;//充值类型
    PayFragment coinFragment;
    PayFragment vipFragment;
    @Override
    public void initData(Bundle savedInstanceState) {
        payType = getIntent().getStringExtra(Constants.IntentKey.PAY_TYPE);
        mBookId = getIntent().getLongExtra(Constants.IntentKey.BOOK_ID, 0);
        if (payType.equals("1")) {
            currentIndex = 0;
        } else {
            currentIndex = 1;
        }
        mList = new ArrayList<>();
        coinFragment = new PayFragment();
        Bundle coinBundle = new Bundle();
        coinBundle.putString(Constants.IntentKey.PAY_TYPE, "1");
        coinBundle.putLong(Constants.IntentKey.BOOK_ID, mBookId);
        coinFragment.setArguments(coinBundle);
        vipFragment = new PayFragment();
        Bundle vipBundle = new Bundle();
        vipBundle.putString(Constants.IntentKey.PAY_TYPE, "2");
        vipBundle.putLong(Constants.IntentKey.BOOK_ID, mBookId);
        vipFragment.setArguments(vipBundle);
        mList.add(coinFragment);
        mList.add(vipFragment);
        mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mList, new String[]{getString(R.string.comic_book_coin), getString(R.string.comic_book_vip)});
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
        mPager.setCurrentItem(currentIndex);
    }

    /**
     * @param activity 上下文
     * @param type     充值类型[书币充值:1；会员充值:2]
     * @param bookId   BookModel的id
     */
    public static void toPay(Activity activity, String type, long bookId) {
        ARouter.getInstance().build(RouterMap.COMIC_PAY_ACTIVITY)
                .withString(Constants.IntentKey.PAY_TYPE, type)
                .withLong(Constants.IntentKey.BOOK_ID, bookId)
                .navigation(activity, RequestCode.PAY_REQUEST_CODE);
    }


    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_pay;
    }

    @Override
    public BookShelfPresenter setPresenter() {
        return new BookShelfPresenter();
    }

    @OnClick({R2.id.iv_leftIcon})
    void OnClick(View view) {
        if (view.getId() == R.id.iv_leftIcon) {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (currentIndex == 0) {
            coinFragment.onActivityResult(requestCode, resultCode, data);
        }else {
            vipFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    //    @Override
//    public boolean useEventBus() {
//        return true;
//    }
}
