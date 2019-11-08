package com.jj.comics.ui.mine.login;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.gyf.immersionbar.ImmersionBar;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.data.model.UserInfo;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.FinishLoginActivityEvent;

import butterknife.OnClick;

/**
 * Author ：le
 * Date ：2019-11-08 10:55
 * Description ：
 */
@Route(path = RouterMap.COMIC_GENDERSELECT_ACTIVITY)
public class GenderSelectActivity extends BaseActivity<GenderSelectPresenter> implements GenderSelectContract.View {

    public static void toActivity(Context context) {
        ARouter.getInstance().build(RouterMap.COMIC_GENDERSELECT_ACTIVITY)
                .navigation(context);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.comic_activity_genderselect;
    }

    @Override
    public GenderSelectPresenter setPresenter() {
        return new GenderSelectPresenter();
    }

    @OnClick({R2.id.iv_girl, R2.id.iv_boy})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_girl) {
            getP().updateUserInfo(null,null,2);
        } else if (id == R.id.iv_boy) {
            getP().updateUserInfo(null,null,1);
        }
    }

    @Override
    public void onSuccess(UserInfo userInfo) {
        EventBusManager.sendFinishLoginActivityEvent(new FinishLoginActivityEvent());
        finish();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void initImmersionBar() {
        ImmersionBar.with(this)
                .reset()
                .fitsSystemWindows(false)
                .init();
    }
}
