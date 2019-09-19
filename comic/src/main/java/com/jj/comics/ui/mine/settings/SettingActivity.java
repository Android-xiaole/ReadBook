package com.jj.comics.ui.mine.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jj.base.dialog.CustomFragmentDialog;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.SharedPref;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.SharedPreManger;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.LogoutEvent;
import com.jj.comics.widget.UserItemView;
import com.tencent.stat.StatMultiAccount;
import com.tencent.stat.StatService;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.COMIC_SETTING_ACTIVITY)
public class SettingActivity extends BaseActivity<SettingPresenter> implements SettingContract.ISettingView {
    @BindView(R2.id.setting_exit)
    TextView mBtnExitLogin;

    @BindView(R2.id.setting_clear_cache)
    UserItemView mCache;

    @BindView(R2.id.switch_autoBuy)
    Switch switchButton;

    @Override
    public void initData(Bundle savedInstanceState) {
        getP().getCacheSize();
        updateLoginStatus();
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreManger.getInstance().saveAutoBuyStatus(isChecked);
            }
        });
    }

    private void updateLoginStatus() {
        if (LoginHelper.getOnLineUser() == null) {
            mBtnExitLogin.setText(R.string.comic_not_login_state);
            mBtnExitLogin.setBackgroundColor(getResources().getColor(R.color.base_color_f0f0f0));
            mBtnExitLogin.setClickable(false);
        } else {
            mBtnExitLogin.setText(R.string.comic_logout);
            mBtnExitLogin.setBackgroundColor(getResources().getColor(R.color.comic_ffffff));
            mBtnExitLogin.setClickable(true);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_setting;
    }

    @Override
    public SettingPresenter setPresenter() {
        return new SettingPresenter();
    }

    private CustomFragmentDialog customFragmentDialog;

    @OnClick({R2.id.setting_exit, R2.id.setting_about, R2.id.setting_clear_cache})
    void OnClick(View view) {
        int i = view.getId();
        if (i == R.id.setting_about) {
            ARouter.getInstance().build(RouterMap.COMIC_ABOUT_ACTIVITY).navigation();
        } else if (i == R.id.setting_exit) {
            if (customFragmentDialog == null) customFragmentDialog = new CustomFragmentDialog();
            customFragmentDialog.show(this, getSupportFragmentManager(), R.layout.comic_dialog_exit);
            TextView cancel = customFragmentDialog.getDialog().findViewById(R.id.dialog_exit_cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customFragmentDialog.dismiss();
                }
            });

            TextView confirm = customFragmentDialog.getDialog().findViewById(R.id.dialog_exit_confirm);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customFragmentDialog.dismiss();
                    //登出所有用户
                    LoginHelper.logOffAllUser();
                    updateLoginStatus();
                    //最后发送退出登录的通知
                    EventBusManager.sendLogoutEvent(new LogoutEvent());

                    //腾讯账号统计
                    // 注销时调用
                    StatService.removeMultiAccount(context, StatMultiAccount.AccountType.PHONE_NO);

                    finish();
                }
            });
        } else {
            getP().clearCache();
        }
    }

    @Override
    public boolean useEventBus() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void setCacheSize(String cacheSize) {
        mCache.setRight_title(cacheSize);
    }
}
