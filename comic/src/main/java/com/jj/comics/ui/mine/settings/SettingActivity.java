package com.jj.comics.ui.mine.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.gyf.barlibrary.ImmersionBar;
import com.jj.base.dialog.CustomFragmentDialog;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.SharedPref;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.common.constants.RequestCode;
import com.jj.comics.data.model.UpdateModelProxy;
import com.jj.comics.util.eventbus.events.LogoutEvent;
import com.jj.comics.common.net.download.DownInfo;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.IntentUtils;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.widget.SwitchButton;
import com.tencent.stat.StatMultiAccount;
import com.tencent.stat.StatService;

import java.io.File;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.COMIC_SETTING_ACTIVITY)
public class SettingActivity extends BaseActivity<SettingPresenter> implements SettingContract.ISettingView{
    @BindView(R2.id.setting_gprs_remind)
    SwitchButton remind;
    @BindView(R2.id.setting_gprs_comment)
    SwitchButton mDelComentRemind;
    @BindView(R2.id.setting_accept_notification)
    SwitchButton notification;
    @BindView(R2.id.setting_volume_page)
    SwitchButton volume_page;
    @BindView(R2.id.tv_bind_phone)
    TextView mTvBindPhone;
    @BindView(R2.id.setting_bind_phone)
    LinearLayout mBtnBindPhone;
    @BindView(R2.id.setting_exit)
    TextView mBtnExitLogin;

    public static final int START_DOWNLOAD = 1;
    public static final int DOWNING = START_DOWNLOAD + 1;
    public static final int DONE = DOWNING + 1;
    public static final int DOWN_FAIL = DONE + 1;
    private ProgressDialog mDialog;
    private Handler mDownHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_DOWNLOAD:
                    if (mDialog == null) {
                        mDialog = new ProgressDialog(context,
                                AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                        mDialog.setCancelable(false);
                        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    }
                    mDialog.show();
                    break;
                case DOWNING:
                    DownInfo info = (DownInfo) msg.obj;
                    int max = info.getContentLength() + info.getStartPos();
                    mDialog.setMax(max);
                    int progress = info.getBytesRead() + info.getStartPos();
                    mDialog.setProgress(progress);
                    float all = max / 1024f / 1024f;
                    float percent = progress / 1024f / 1024f;
                    mDialog.setProgressNumberFormat(String.format(getString(R.string.comic_download_progress_format),
                            percent, all));
                    break;
                case DONE:
                    if (mDialog.isShowing()) mDialog.dismiss();
                    IntentUtils.installApk(((File) msg.obj), context);
                    break;
                case DOWN_FAIL:
                    if (mDialog.isShowing()) mDialog.dismiss();
                    showToastShort(getString(R.string.comic_update_fail));
                    break;
            }
        }
    };

    @Override
    public void initData(Bundle savedInstanceState) {
//        StatusBarUtil.setColor(this, getResources().getColor(R.color.comic_yellow_ffd850), 0);
        initSwitchOnCheckedChangeListener();

        updateLoginStatus();
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

    private void initSwitchOnCheckedChangeListener() {
        remind.setChecked(SharedPref.getInstance(SettingActivity.this).getBoolean(Constants.SharedPrefKey.SWITCH_GPRS_READ_REMIND, true));
        notification.setChecked(SharedPref.getInstance(SettingActivity.this).getBoolean(Constants.SharedPrefKey.SWITCH_ACCEPT_NOTIFICATION, true));
        volume_page.setChecked(SharedPref.getInstance(SettingActivity.this).getBoolean(Constants.SharedPrefKey.SWITCH_VOLUME_PAGE, false));
        mDelComentRemind.setChecked(SharedPref.getInstance(SettingActivity.this).getBoolean(Constants.SharedPrefKey.SP_NO_DIALOG_FOR_DELETE_COMMENT, false));
        remind.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                SharedPref.getInstance(SettingActivity.this).putBoolean(Constants.SharedPrefKey.SWITCH_GPRS_READ_REMIND, isChecked);
            }
        });

        notification.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                SharedPref.getInstance(SettingActivity.this).putBoolean(Constants.SharedPrefKey.SWITCH_ACCEPT_NOTIFICATION, isChecked);
            }
        });

        volume_page.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                SharedPref.getInstance(SettingActivity.this).putBoolean(Constants.SharedPrefKey.SWITCH_VOLUME_PAGE, isChecked);
            }
        });
        mDelComentRemind.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                SharedPref.getInstance(SettingActivity.this).putBoolean(Constants.SharedPrefKey.SP_NO_DIALOG_FOR_DELETE_COMMENT, isChecked);
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_setting;
    }

    @Override
    public SettingPresenter setPresenter() {
        return new SettingPresenter();
    }

    public static void toPay(Activity activity) {
        ARouter.getInstance().build(RouterMap.COMIC_SETTING_ACTIVITY).navigation(activity, RequestCode.SETTING_REQUEST_CODE);
    }

    private CustomFragmentDialog customFragmentDialog;
    @OnClick({R2.id.setting_exit, R2.id.setting_redeem_code, R2.id.setting_about, R2.id.setting_check_update
            , R2.id.setting_bind_phone})
    void OnClick(View view) {
        int i = view.getId();
        if (i == R.id.setting_redeem_code) {
            if (LoginHelper.getOnLineUser() != null) {
                ARouter.getInstance().build(RouterMap.COMIC_COUPON_CODE_ACTIVITY).navigation(SettingActivity.this, RequestCode.Coupon_REQUEST_CODE);
            } else {
                ToastUtil.showToastLong(getString(R.string.comic_coupon_code_not_login_remind));
            }
        } else if (i == R.id.setting_about) {
            ARouter.getInstance().build(RouterMap.COMIC_ABOUT_ACTIVITY).navigation();
        } else if (i == R.id.setting_check_update) {
            getP().checkUpdate(this);
        } else if (i == R.id.setting_exit) {
            if (customFragmentDialog == null) customFragmentDialog = new CustomFragmentDialog();
            customFragmentDialog.show(this,getSupportFragmentManager(), R.layout.comic_dialog_exit);
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

//            final CustomDialog customDialog = new CustomDialog(SettingActivity.this, R.style.comic_CustomDialog, R.layout.comic_dialog_exit);
//            customDialog.show();
//            TextView cancel = customDialog.findViewById(R.id.dialog_exit_cancel);
//            cancel.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    customDialog.dismiss();
//                }
//            });
//
//            TextView confirm = customDialog.findViewById(R.id.dialog_exit_confirm);
//            confirm.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    customDialog.dismiss();
////                    EventBus.getDefault().post(new Refresh(0));
////                    EventBus.getDefault().post(SettingActivity.super.context);
//                    EventBusManager.sendLogoutEvent(new LogoutEvent(SettingActivity.super.context));
//                    finish();
//                }
//            });
        }else if (i == R.id.setting_bind_phone) {
            if (LoginHelper.getOnLineUser() != null) {
                ARouter.getInstance().build(RouterMap.COMIC_BIND_PHONE_ACTIVITY).navigation();
            } else {
                ToastUtil.showToastLong(getString(R.string.comic_bind_phone_not_login_remind));
            }
        }
    }

    @Override
    protected void initImmersionBar() {
        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.base_color_ffd850)
//                .navigationBarColor(R.color.comic_ffffff)
                .statusBarDarkFont(false, 0.2f)
                .init();
    }


//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void fillData(UserInfo user) {
//        updateLoginStatus();
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.Coupon_REQUEST_CODE && resultCode == RequestCode.Coupon_REQUEST_CODE) {
            finish();
        }
    }

    public void sendMessage(int what, Object info) {
        Message message = info == null ? mDownHandler.obtainMessage(what) : mDownHandler.obtainMessage(what, info);
        message.sendToTarget();
    }

    public void updateAlert(final UpdateModelProxy.UpdateModel channelUpdate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,
                AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setCancelable(false);// 点击旁边不会消失
        builder.setTitle("版本更新提醒");
        builder.setMessage(channelUpdate.getProductUpdateDesc());
        builder.setPositiveButton("立刻升级", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 去下载APK
                getP().goDown(channelUpdate.getProductDownUrl(),SettingActivity.this);
            }
        });

        if (!TextUtils.equals("1", channelUpdate.getIsUpdate())) {
            builder.setNegativeButton("稍后再说", null);
        }
        builder.show();
    }

    @Override
    public boolean useEventBus() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
