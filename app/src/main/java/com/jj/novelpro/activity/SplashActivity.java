package com.jj.novelpro.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.baidu.mobstat.StatService;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.jj.base.BaseApplication;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.DialogUtil;
import com.jj.base.utils.PackageUtil;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.SharedPref;
import com.jj.comics.adapter.ImagePagerAdapter;
import com.jj.comics.common.constants.Constants;
import com.jj.novelpro.R;
import com.jj.novelpro.R2;
import com.jj.novelpro.present.SplashContract;
import com.jj.novelpro.present.SplashPresenter;
import com.youth.banner.listener.OnBannerListener;

import net.frakbot.jumpingbeans.JumpingBeans;

import java.util.ArrayList;

import butterknife.BindView;

@Route(path = RouterMap.COMIC_SPLASH_ACTIVITY)
public class SplashActivity extends BaseActivity<SplashPresenter> implements SplashContract.ISplashView {
    @BindView(R2.id.splash_version)
    TextView mSplashVersion;
    @BindView(R2.id.splash_channel)
    TextView mSplashChannel;
    @BindView(R2.id.guide)
    ViewPager mGuide;
    @BindView(R2.id.tv_loading)
    TextView tv_loading;
    private ImagePagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //每次application非正常重启后只强制重启一次app
        BaseApplication.setNormalStart(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        JumpingBeans.with(tv_loading)
                .appendJumpingDots()
                .build();
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
            Intent intent = getIntent();
            boolean isJPush = intent.getBooleanExtra(Constants.IntentKey.IS_JPUSH,false);
            if (isJPush){
                ARouter.getInstance().build(RouterMap.COMIC_MAIN_ACTIVITY)
                        .withBoolean(Constants.IntentKey.IS_JPUSH,true)
                        .withString(Constants.IntentKey.ID,intent.getStringExtra(Constants.IntentKey.ID))
                        .withString("from","极光推送")
                        .navigation(this);
            }else{
                ARouter.getInstance().build(RouterMap.COMIC_MAIN_ACTIVITY).navigation(this);
            }
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
