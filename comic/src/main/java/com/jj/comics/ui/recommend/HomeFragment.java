package com.jj.comics.ui.recommend;


import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gyf.immersionbar.ImmersionBar;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.ui.BaseCommonFragment;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.common.interceptor.LoginInterceptor;
import com.jj.comics.ui.dialog.ShareDialog;
import com.jj.comics.util.FRouterHelper;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.COMIC_HOME_FRAGMENT)
public class HomeFragment extends BaseCommonFragment implements HomeContract.IHomeView{

    @BindView(R2.id.fl_toolBar)
    FrameLayout fl_toolBar;
    @BindView(R2.id.view_toolbarBg)
    View view_toolbarBg;
    @BindView(R2.id.home_tab)
    FrameLayout mTab;
    @BindView(R2.id.home_share)
    FrameLayout home_share;
    private FRouterHelper mFRouterHelper;
    private int current = -1;
    private String fragmentPath[] = new String[]{RouterMap.COMIC_RECOMMEND_FRAGMENT};


    @Override
    public void initData(Bundle savedInstanceState) {
        //设置toolbar距离上端的高度
//        int statusBarHeight = ScreenUtils.getStatusBarHeight();

//        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) fl_toolBar.getLayoutParams();
//        lp.topMargin = statusBarHeight;
//        fl_toolBar.setLayoutParams(lp);

        mFRouterHelper = new FRouterHelper(fragmentPath, new LoginInterceptor());
        switchFragment(savedInstanceState == null ? 0 : savedInstanceState.getInt("index", 0));
//        home_share.setSelected(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("index", current);
        super.onSaveInstanceState(outState);
    }

    private ShareDialog shareDialog;

    @OnClick({R2.id.home_recommend, R2.id.home_recently, R2.id.home_share})
    void switchFragment(View view) {
        int id = view.getId();
        if (id == R.id.home_share) {
            if (shareDialog == null) {
                shareDialog = new ShareDialog(getBaseActivity(),"主页","");
            }
            shareDialog.show();
        } else{
            switchFragment(Integer.valueOf(view.getTag().toString()));
            if (id == R.id.home_recommend){
                home_share.setSelected(true);
                view_toolbarBg.setVisibility(View.VISIBLE);
            }else if (id == R.id.home_recently){
                home_share.setSelected(false);
                view_toolbarBg.setVisibility(View.GONE);
            }
        }
    }

    public void setToolBarBgAlpha(float alpha){
        view_toolbarBg.setAlpha(alpha);
        if (alpha == 0){
            ImmersionBar.with(this)
                    .reset()
                    .init();
            return;
        }
        ImmersionBar.with(this)
                .reset()
                .statusBarDarkFont(true, alpha)
                /*
                    这里用三个参数的方法很关键,实现了状态栏颜色渐变的效果
                    详见：https://github.com/gyf-dev/ImmersionBar/issues/217
                 */
                .statusBarColor("#00ffffff","#ffffff",alpha)
                .init();
    }

    private void switchFragment(int index) {
        if (current == index) return;
        if (mTab.getChildCount() > index) {
            for (int i = 0; i < mTab.getChildCount(); i++) {
                mTab.getChildAt(i).setSelected(i == index);
                if (i == index)
                    mFRouterHelper.switchFragment(this, index, current, R.id.fl_container, null);
            }
            current = index;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        //这里需要主动调用当前加载的子fragment的生命周期，去处理友盟统计相关逻辑，系统不会自动调用
        mFRouterHelper.getFragment(fragmentPath[current]).onHiddenChanged(hidden);
    }

    @Override
    protected boolean hasFragment() {
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_fragment_home;
    }

    @Override
    public BasePresenter setPresenter() {
        return null;
    }

}
