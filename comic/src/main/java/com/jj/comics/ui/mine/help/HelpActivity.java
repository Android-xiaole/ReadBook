package com.jj.comics.ui.mine.help;

import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.widget.UserItemView;

import butterknife.BindView;

@Route(path = RouterMap.COMIC_HELP_ACTIVITY)
public class HelpActivity extends BaseActivity {
    @BindView(R2.id.btn_recharge)
    UserItemView mBtnRecharge;
    @BindView(R2.id.btn_fission)
    UserItemView mBtnFission;
    @Override
    protected void initData(Bundle savedInstanceState) {
        mBtnRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(RouterMap.COMIC_HELP_RECHARGE_ACTIVITY).navigation();
            }
        });
        mBtnFission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(RouterMap.COMIC_HELP_FISSION_ACTIVITY).navigation();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.comic_activity_help;
    }

    @Override
    public BasePresenter setPresenter() {
        return null;
    }
}
