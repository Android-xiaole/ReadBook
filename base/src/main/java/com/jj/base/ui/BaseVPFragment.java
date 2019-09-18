package com.jj.base.ui;

import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.IPresenter;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseVPFragment<P extends BasePresenter> extends BaseFragment<P> {
    /**
     * 是否可见
     */
    private boolean mVisible;

    /**
     * 是否已经开始统计
     */
    private boolean hasStarted;
    protected final String TAG = getClass().getName();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mVisible = isVisibleToUser;
        if (isResumed()) {
            if (!isVisibleToUser) {
                MobclickAgent.onPageEnd(TAG);
                hasStarted = false;
            } else {
                hasStarted = true;
                MobclickAgent.onPageStart(TAG);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mVisible && !hasStarted) {
            hasStarted = true;
            MobclickAgent.onPageStart(TAG);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mVisible && hasStarted) {
            MobclickAgent.onPageEnd(TAG);
            hasStarted = false;
        }
    }

}
