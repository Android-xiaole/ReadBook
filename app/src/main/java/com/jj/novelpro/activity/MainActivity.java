package com.jj.novelpro.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppInstallAdapter;
import com.fm.openinstall.listener.AppWakeUpAdapter;
import com.fm.openinstall.model.AppData;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gyf.barlibrary.ImmersionBar;
import com.jj.base.CusNavigationCallback;
import com.jj.base.log.LogUtil;
import com.jj.base.ui.BaseActivity;
import com.jj.base.ui.BaseFragment;
import com.jj.base.utils.PackageUtil;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.SharedPref;
import com.jj.base.utils.toast.ToastUtil;
import com.jj.comics.util.LoginHelper;
import com.jj.comics.util.SharedPreManger;
import com.jj.novelpro.R;
import com.jj.novelpro.R2;
import com.jj.novelpro.present.MainContract;
import com.jj.novelpro.present.MainPresenter;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.util.reporter.ActionReporter;
import com.jj.comics.common.constants.RequestCode;
import com.jj.comics.data.model.UpdateModelProxy;
import com.jj.comics.util.eventbus.events.ChangeTabBarEvent;
import com.jj.comics.util.eventbus.events.LogoutEvent;
import com.jj.comics.common.net.download.DownInfo;
import com.jj.comics.ui.detail.DetailActivityHelper;
import com.jj.comics.util.IntentUtils;
import com.jj.comics.util.RegularUtil;
import com.tencent.bugly.beta.Beta;
import com.umeng.analytics.MobclickAgent;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.OnClick;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

import static android.view.KeyEvent.KEYCODE_BACK;

@Route(path = RouterMap.COMIC_MAIN_ACTIVITY)
public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.IMainView,
        BottomNavigationView.OnNavigationItemSelectedListener {
    //    @BindView(R2.id.radioGroup)
//    RadioGroup mRadioGroup;
//    @BindView(R2.id.bv_home_navigation)
//    BottomNavigationView mBottomNavigationView;

    @BindView(R2.id.iv_nav_featured)
    ImageView mIvFeatured;
    @BindView(R2.id.iv_nav_classify)
    ImageView mIvClassify;
    @BindView(R2.id.iv_nav_money)
    ImageView mIvMoney;
    @BindView(R2.id.iv_nav_shelf)
    ImageView mIvShelf;
    @BindView(R2.id.iv_nav_mine)
    ImageView mIvMine;

    @BindView(R2.id.tv_nav_featured)
    TextView mTvFeatured;
    @BindView(R2.id.tv_nav_classify)
    TextView mTvClassify;
    @BindView(R2.id.tv_nav_money)
    TextView mTvMoney;
    @BindView(R2.id.tv_nav_shelf)
    TextView mTvShelf;
    @BindView(R2.id.tv_nav_mine)
    TextView mTvMine;


    private int currentIndex = -1;
    private int preIndex = -1;
    private CusNavigationCallback mInterceptor;

    public static final int START_DOWNLOAD = 1;
    public static final int DOWNING = START_DOWNLOAD + 1;
    public static final int DONE = DOWNING + 1;
    public static final int DOWN_FAIL = DONE + 1;
    private ProgressDialog mDialog;
    private Badge badge;
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
    private BottomNavigationItemView mBottomMine;

    @Override
    protected void initImmersionBar() {
        ImmersionBar.with(MainActivity.this).init();
    }


    @Override
    public void initData(Bundle savedInstanceState) {
        //获取唤醒参数
        OpenInstall.getWakeUp(getIntent(), wakeUpAdapter);
        //获取OpenInstall安装数据
        OpenInstall.getInstall(new AppInstallAdapter() {
            @Override
            public void onInstall(AppData appData) {
                Log.d("OpenInstall", "getInstall : installData = " + appData.toString());
                //获取渠道数据
                String channelCode = appData.getChannel();
                //获取自定义数据
                String bindData = appData.getData();
                try {
                    JSONObject object = new JSONObject(bindData);
                    String invite_code = object.optString(Constants.SharedPrefKey.INVITE_CODE);
                    if (!TextUtils.isEmpty(invite_code)){
                        SharedPreManger.getInstance().saveInvitecode(invite_code);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    LogUtil.e("OpenInstall:appData转换json失败");
                }
            }
        });
        AndPermission.with(this)
                .runtime()
                .permission(Permission.WRITE_EXTERNAL_STORAGE,Permission.READ_EXTERNAL_STORAGE,Permission.READ_PHONE_STATE)
                .onGranted(permissions -> {
                    // Storage permission are allowed.
                    getP().checkUpdate();
                })
                .onDenied(permissions -> {
                    // Storage permission are not allowed.
                    List<String> strings = Permission.transformText(getApplicationContext(), permissions);
                    StringBuffer sb = new StringBuffer();
                    for (String permission : strings) {
                        sb.append(permission+"、");
                    }
                    sb.deleteCharAt(sb.lastIndexOf("、"));
                    ToastUtil.showToastShort("您已拒绝"+sb.toString()+"权限，可能导致App无法正常使用");
                })
                .start();

        // 不使用图标默认变色
//        mBottomNavigationView.setItemIconTintList(null);
//        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
//        mBottomMine = mBottomNavigationView.findViewById(R.id.menu_home_mine);
        badge = new QBadgeView(context);
        ActionReporter.reportAction(ActionReporter.Event.START_APP, null, null, null);
        if (LoginHelper.getOnLineUser() != null) {
            ActionReporter.reportAction(ActionReporter.Event.LOGIN_START_APP, null, null, null);
        }
        ActionReporter.reportAction(ActionReporter.Event.LOGIN_START_APP, null, null, null);
        if (PackageUtil.isAliPayInstalled(MainActivity.this)) {
            MobclickAgent.onEvent(MainActivity.this, Constants.UMEventId.USER_INSTALL_ALIPAY, "已安装支付宝");
        } else {
            MobclickAgent.onEvent(MainActivity.this, Constants.UMEventId.USER_INSTALL_ALIPAY, "未安装支付宝");
        }
//        getP().getUserData();
        //bugly自动检查更新
        Beta.checkUpgrade();
        mInterceptor = new CusNavigationCallback() {
            @Override
            public void onGetFragment(int index, BaseFragment fragment) {
                updateStatusBar(index);
            }

            @Override
            public void onArrival(Postcard postcard) {
                currentIndex = (int) postcard.getTag();
                preIndex = (int) postcard.getTag();
            }

            @Override
            public void onLost(Postcard postcard) {
                setCheck(currentIndex);
                preIndex = (int) postcard.getTag();
            }

            @Override
            public void onFound(Postcard postcard) {
                super.onFound(postcard);
                preIndex = (int) postcard.getTag();
            }

            @Override
            public void onInterrupt(Postcard postcard) {
                setCheck(currentIndex);
                preIndex = (int) postcard.getTag();
            }
        };
//        getP().showPopupWindow(mRadioGroup);
//        mRadioGroup.setOnCheckedChangeListener(this);

        int index = savedInstanceState == null ? 0 : savedInstanceState.getInt(Constants.IntentKey.INDEX, 0);

        switchBtns(index);
        getP().switchFragment(index, currentIndex, mInterceptor);
        getP().checkUpdate();
//        ARouter.getInstance().build(RouterMap.COMIC_ABOUT_ACTIVITY).navigation(this);
        if (getIntent() != null) {
            umengMessageClick(getIntent().getStringExtra(Constants.IntentKey.ID));
        }


    }

    private void updateStatusBar(int index) {
        switch (index) {
            case 0:
                ImmersionBar.with(MainActivity.this)
                        .reset()
                        .statusBarDarkFont(true, 0.2f)
                        .statusBarColor(R.color.base_color_ffffff)
                        .init();
                break;
            case 1:
                ImmersionBar.with(MainActivity.this)
                        .reset()
                        .statusBarDarkFont(true, 0.2f)
                        .statusBarColor(R.color.base_color_ffffff)
                        .init();
                break;
            case 2:
                ImmersionBar.with(MainActivity.this)
                        .reset()
                        .statusBarDarkFont(true, 0.2f)
                        .statusBarColor(R.color.base_color_ffffff)
                        .init();
                break;
            case 3:
                ImmersionBar.with(MainActivity.this)
                        .reset()
                        .statusBarDarkFont(true, 0.2f)
                        .statusBarColor(R.color.base_color_ffffff)
                        .init();
                break;
            case 4:
                ImmersionBar.with(MainActivity.this)
                        .reset()
                        .statusBarDarkFont(true, 0.2f)
                        .statusBarColor(R.color.base_color_ffffff)
                        .init();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (LoginHelper.getOnLineUser()!=null){
            getP().getMessageSum();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 此处要调用，否则App在后台运行时，会无法截获
        OpenInstall.getWakeUp(intent, wakeUpAdapter);
        if (intent != null) umengMessageClick(intent.getStringExtra(Constants.IntentKey.ID));
    }

    private void umengMessageClick(String id) {
        if (!TextUtils.isEmpty(id) && RegularUtil.isNumeric(id)) {
            DetailActivityHelper.toDetail(this, id, "推送");
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public MainPresenter setPresenter() {
        return new MainPresenter();
    }

//    @Override
//    public void onCheckedChanged(RadioGroup group, int checkedId) {
//        for (int i = 0; i < group.getChildCount(); i++) {
//            if (checkedId == group.getChildAt(i).getId()) {
//                getP().switchFragment(i, currentIndex, mInterceptor);
//                break;
//            }
//        }
//
//    }

    @OnClick({R2.id.btn_nav_featured,R2.id.btn_nav_classify,R2.id.btn_nav_money,
            R2.id.btn_nav_shelf,R2.id.btn_nav_mine})
    void onClick(View v) {
        if (v.getId() == R.id.btn_nav_featured) {
            switchBtns(0);
            getP().switchFragment(0, currentIndex, mInterceptor);
        }else if (v.getId() == R.id.btn_nav_classify) {
            switchBtns(1);
            getP().switchFragment(1, currentIndex, mInterceptor);
        }else if (v.getId() == R.id.btn_nav_money) {
            switchBtns(2);
            getP().switchFragment(2, currentIndex, mInterceptor);
        }else if (v.getId() == R.id.btn_nav_shelf) {
            switchBtns(3);
            getP().switchFragment(3, currentIndex, mInterceptor);
        }else if (v.getId() == R.id.btn_nav_mine) {
            switchBtns(4);
            getP().switchFragment(4, currentIndex, mInterceptor);
        }
    }

    private void switchBtns(int index) {
        switch (index) {
            case 0:
                mIvFeatured.setSelected(true);
                mIvClassify.setSelected(false);
                mIvMoney.setSelected(false);
                mIvShelf.setSelected(false);
                mIvMine.setSelected(false);

                mTvFeatured.setSelected(true);
                mTvClassify.setSelected(false);
                mTvMoney.setSelected(false);
                mTvShelf.setSelected(false);
                mTvMine.setSelected(false);
                break;
            case 1:
                mIvFeatured.setSelected(false);
                mIvClassify.setSelected(true);
                mIvMoney.setSelected(false);
                mIvShelf.setSelected(false);
                mIvMine.setSelected(false);

                mTvFeatured.setSelected(false);
                mTvClassify.setSelected(true);
                mTvMoney.setSelected(false);
                mTvShelf.setSelected(false);
                mTvMine.setSelected(false);
                break;
            case 2:
                mIvFeatured.setSelected(false);
                mIvClassify.setSelected(false);
                mIvMoney.setSelected(true);
                mIvShelf.setSelected(false);
                mIvMine.setSelected(false);

                mTvFeatured.setSelected(false);
                mTvClassify.setSelected(false);
                mTvMoney.setSelected(true);
                mTvShelf.setSelected(false);
                mTvMine.setSelected(false);
                break;
            case 3:
                mIvFeatured.setSelected(false);
                mIvClassify.setSelected(false);
                mIvMoney.setSelected(false);
                mIvShelf.setSelected(true);
                mIvMine.setSelected(false);

                mTvFeatured.setSelected(false);
                mTvClassify.setSelected(false);
                mTvMoney.setSelected(false);
                mTvShelf.setSelected(true);
                mTvMine.setSelected(false);
                break;
            case 4:
                mIvFeatured.setSelected(false);
                mIvClassify.setSelected(false);
                mIvMoney.setSelected(false);
                mIvShelf.setSelected(false);
                mIvMine.setSelected(true);

                mTvFeatured.setSelected(false);
                mTvClassify.setSelected(false);
                mTvMoney.setSelected(false);
                mTvShelf.setSelected(false);
                mTvMine.setSelected(true);
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_home_featured:
                getP().switchFragment(0, currentIndex, mInterceptor);
                return true;
            case R.id.menu_home_classify:
                getP().switchFragment(1, currentIndex, mInterceptor);
                return true;
            case R.id.menu_home_money:
                getP().switchFragment(2, currentIndex, mInterceptor);
                return true;
            case R.id.menu_home_bookshelf:
                getP().switchFragment(3, currentIndex, mInterceptor);
                return true;
            case R.id.menu_home_mine:
                getP().switchFragment(4, currentIndex, mInterceptor);
                return true;
        }
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(Constants.IntentKey.INDEX, currentIndex);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getP().onActivityResult(getSupportFragmentManager().getFragments(), requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RequestCode.LOGIN_REQUEST_CODE:
                    setCheck(preIndex);
                    break;
                case RequestCode.RICH_REQUEST_CODE:
                    setCheck(1);
                    break;
            }
        }
    }

    @Override
    public void updateAlert(final UpdateModelProxy.UpdateModel channelUpdate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,
                AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setCancelable(false);// 点击旁边不会消失
        builder.setTitle(R.string.update_dialog_title);
        builder.setMessage(channelUpdate.getProductUpdateDesc());
        builder.setPositiveButton(R.string.update_now, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 去下载APK
                getP().goDown(channelUpdate.getProductDownUrl());
            }
        });

        if (!TextUtils.equals("1", channelUpdate.getIsUpdate())) {
            builder.setNegativeButton(R.string.update_later, null);
        }
        builder.show();
    }

    @Override
    public BaseActivity getActivity() {
        return this;
    }

    @Override
    public void onGetTaskInfo(int count) {
        if (mBottomMine != null && count > 0) {
            badge.bindTarget(mBottomMine);
            badge.setShowShadow(false);
            badge.setBadgeNumber(count);
            SharedPref.getInstance().putInt(Constants.IntentKey.MESSAGE_SUM,count);
        } else {
            badge.hide(true);
        }

    }

    //    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setCheck(int index) {
        switch (index) {
//            case 0:
//                mBottomNavigationView.setSelectedItemId(R.id.menu_home_featured);
//                break;
//            case 1:
//                mBottomNavigationView.setSelectedItemId(R.id.menu_home_classify);
//                break;
//            case 2:
//                mBottomNavigationView.setSelectedItemId(R.id.menu_home_money);
//                break;
//            case 3:
//                mBottomNavigationView.setSelectedItemId(R.id.menu_home_bookshelf);
//                break;
//            case 4:
//                mBottomNavigationView.setSelectedItemId(R.id.menu_home_mine);
//                break;
        }
//        if (CommonUtil.checkValid(mRadioGroup.getChildCount(), index) && mRadioGroup.getChildAt(index) instanceof RadioButton) {
//            ((RadioButton) mRadioGroup.getChildAt(index)).setChecked(true);
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setCheck(ChangeTabBarEvent refresh) {
        setCheck(refresh.getIndex());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dealTokenUseless(LogoutEvent logoutEvent) {
        getP().dealTokenUseLess();
    }

    public void exitLogin() {
        preIndex = -1;
        setCheck(0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KEYCODE_BACK) {
//            DialogUtil.showExitDialog(this);
            doubleClickExit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private long mExitTime;

    private void doubleClickExit() {
        if (System.currentTimeMillis() - mExitTime > 2000) {
            showToastShort(getString(R.string.exit_app_remind));
            mExitTime = System.currentTimeMillis();
        } else {
            MobclickAgent.onKillProcess(context);
            //参数用作状态码；根据惯例，非 0 的状态码表示异常终止。
            System.exit(0);
        }
    }


    @Override
    public boolean hasFragment() {
        return true;
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    public void sendMessage(int what, Object info) {
        Message message = info == null ? mDownHandler.obtainMessage(what) : mDownHandler.obtainMessage(what, info);
        message.sendToTarget();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wakeUpAdapter = null;
    }

    AppWakeUpAdapter wakeUpAdapter = new AppWakeUpAdapter() {
        @Override
        public void onWakeUp(AppData appData) {
            //获取渠道数据
            String channelCode = appData.getChannel();
            //获取绑定数据
            String bindData = appData.getData();
            Log.d("OpenInstall", "getWakeUp : wakeupData = " + appData.toString());
        }
    };
}
