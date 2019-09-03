package com.jj.maozhua.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.baidu.mobstat.StatService;
import com.gyf.barlibrary.BarHide;
import com.gyf.barlibrary.ImmersionBar;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.DialogUtil;
import com.jj.base.utils.PackageUtil;
import com.jj.base.utils.ResourceUtil;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.SharedPref;
import com.jj.maozhua.R;
import com.jj.maozhua.R2;
import com.jj.maozhua.present.SplashContract;
import com.jj.maozhua.present.SplashPresenter;
import com.jj.comics.adapter.ImagePagerAdapter;
import com.jj.comics.common.constants.Constants;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;

import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;

public class SplashActivity extends BaseActivity<SplashPresenter> implements SplashContract.ISplashView {
    @BindView(R2.id.splash_version)
    TextView mSplashVersion;
    @BindView(R2.id.splash_channel)
    TextView mSplashChannel;
    @BindView(R2.id.guide)
    ViewPager mGuide;
    private ImagePagerAdapter mPagerAdapter;

    @Override
    public void initData(Bundle savedInstanceState) {
        StatService.setAppChannel(getApplicationContext(), Constants.CHANNEL_ID, true);
        StatService.start(this);
        if (Constants.DEBUG) {
            mSplashVersion.setText("v:" + PackageUtil.getPackageInfo().versionName);
            mSplashChannel.setText("c:" + Constants.CHANNEL_ID);
        }
        getP().sendDelayedMessage(2);

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public SplashPresenter setPresenter() {
        return new SplashPresenter();
    }

    @Override
    public void launch() {
        if (SharedPref.getInstance(getApplicationContext()).getBoolean(Constants.SharedPrefKey.FIRST_OPEN, true)) {
            initViewPager();
        } else {
            ARouter.getInstance().build(RouterMap.COMIC_MAIN_ACTIVITY).navigation(this);
            finish();
        }
    }

    @Override
    public void initConfigFail(String msg) {
        DialogUtil.showExitDialog(SplashActivity.this,msg);
    }

    private void initViewPager() {
        mGuide.setVisibility(View.VISIBLE);
        ArrayList<View> layouts = new ArrayList<>();


        layouts.add(getView(R.layout.comic_guide_1, R.drawable.img_comic_splash_yindaoye1));
        layouts.add(getView(R.layout.comic_guide_2, R.drawable.img_comic_splash_yindaoye2));
        layouts.add(getView(R.layout.comic_guide_3, R.drawable.img_comic_splash_yindaoye3));

        mPagerAdapter = new ImagePagerAdapter(layouts);
        mPagerAdapter.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                if (mPagerAdapter.getCount() - 1 == position) {
                    SharedPref.getInstance(context).putBoolean(Constants.SharedPrefKey.FIRST_OPEN, false);
                    launch();
                }
            }
        });
        mGuide.setAdapter(mPagerAdapter);
    }

    private View getView(int layoutId, int drawableId) {
        View view = LayoutInflater.from(this).inflate(layoutId, mGuide, false);

        ILFactory.getLoader().loadResource((ImageView) view.findViewById(R.id.guide_iv), drawableId, null);
        return view;
    }

    @Override
    protected void initImmersionBar() {
        ImmersionBar.with(this)
                .fitsSystemWindows(false)
                .keyboardEnable(false)
                .statusBarDarkFont(true, 0.2f)
                .hideBar(BarHide.FLAG_HIDE_STATUS_BAR)
                .init();
    }

    @Override
    protected boolean isImmersionBarEnabled() {
        return true;
    }
}
