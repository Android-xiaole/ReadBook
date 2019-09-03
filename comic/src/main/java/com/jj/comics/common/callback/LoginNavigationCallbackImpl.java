package com.jj.comics.common.callback;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavigationCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.comics.common.constants.RequestCode;

import java.lang.ref.WeakReference;

public class LoginNavigationCallbackImpl implements NavigationCallback {
    private WeakReference<BaseActivity> mActivity;

    public LoginNavigationCallbackImpl(BaseActivity activity) {
        mActivity = new WeakReference<>(activity);
    }

    @Override
    public void onFound(Postcard postcard) {

    }

    @Override
    public void onLost(Postcard postcard) {

    }

    @Override
    public void onArrival(Postcard postcard) {

    }

    @Override
    public void onInterrupt(Postcard postcard) {
        Postcard loginPostcard = ARouter.getInstance().build(RouterMap.COMIC_LOGIN_ACTIVITY);
        if (postcard.getExtras() != null) loginPostcard.with(postcard.getExtras());
        if (mActivity != null && mActivity.get() != null)
            loginPostcard.navigation(mActivity.get(), RequestCode.LOGIN_REQUEST_CODE);
        else {
            loginPostcard.navigation();
        }
    }

    public void destroy() {
        if (mActivity != null) mActivity.clear();
    }
}