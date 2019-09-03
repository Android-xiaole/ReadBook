package com.jj.base.ui;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.IPresenter;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseCommonFragment<P extends BasePresenter> extends BaseFragment<P> {

    private String PAGE_NAME = getClass().getName();

    /**
     * 首次初始化
     */
    private boolean mFirstInit = true;

    /**
     * 是否可见
     */
    private boolean mVisible;

    @Override
    public void onResume() {
        super.onResume();
        //若首次初始化,默认可见并开启友盟统计
        if (mFirstInit) {
            mVisible = true;
            mFirstInit = false;
            onVisiable(true);
            if (!hasFragment()) {
                MobclickAgent.onPageStart(PAGE_NAME); //统计页面
            }
            return;
        }

        //若当前界面可见,调用友盟开启跳转统计
        if (mVisible) {onVisiable(true);
            if (!hasFragment()) {
                MobclickAgent.onPageStart(PAGE_NAME); //统计页面
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            mVisible = false;onVisiable(false);
            if (!hasFragment()) {
                MobclickAgent.onPageEnd(PAGE_NAME); //统计页面
            }
        }
        if (!hidden) {
            mVisible = true;onVisiable(true);
            if (!hasFragment()) {
                MobclickAgent.onPageStart(PAGE_NAME); //统计页面
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //若当前界面可见,调用友盟结束跳转统计
        if (mVisible) {onVisiable(false);
            if (!hasFragment()) {
                MobclickAgent.onPageEnd(PAGE_NAME); //统计页面
            }
        }
    }

    protected boolean hasFragment() {
        return false;
    }

    public void onVisiable(boolean visiable){};
}
