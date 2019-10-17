package com.jj.base.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;
import com.imuxuan.floatingview.FloatingView;
import com.jj.base.R;
import com.jj.base.dialog.CustomProgressDialog;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.mvp.IView;
import com.jj.base.utils.toast.ToastUtil;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity implements IView {
    protected BaseActivity context;
    private P p;
    private Unbinder unbinder;
    private Dialog dialog;
    private InputMethodManager mInputMethodManager;
    private Bundle savedInstanceState;
    private boolean isStart;//标记当前activity已经走过一次onStart
    private NetStatusReceiver netReceiver;

    /**
     * activity可能由各种情景引发导致activity被回收 此时直接{@link #getIntent()}方法获取参数全部为null
     * 这里为了方便处理 把{@link #onSaveInstanceState}现场保护传的参数全部放入{@link #getIntent()} 以便回收后再次进入activity时getIntent()可获取到参数
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        if (savedInstanceState != null) {
            if (getIntent() != null)
                getIntent().putExtras(savedInstanceState);
        }
        super.onCreate(savedInstanceState);
        context = this;
        if (getLayoutId() > 0) {
            setContentView(getLayoutId());
            bindUI(null);
        }

        if (p == null) p = setPresenter();
        if (p != null) {
            p.attachV(this);
            getLifecycle().addObserver(p);
        }

        if (isImmersionBarEnabled()) {
            initImmersionBar();
        }

        if (useEventBus() && !EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        initRegistNetStatusReceiver();
    }

    private void initRegistNetStatusReceiver() {
        netReceiver = new NetStatusReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netReceiver, filter);
    }

    protected abstract void initData(Bundle savedInstanceState);

    protected abstract int getLayoutId();

    public abstract P setPresenter();

    public P getP() {
        return p;
    }


    public void bindUI(View rootView) {
        unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        /**
         * 初始化的操作方这里是因为init里面可能有网络请求，会操作到当前界面的lifecycle对象，
         * 但发送当前生命周期到BasePresenter里面的时候会有一定的延时，从而导致bindLifecycle操作空指针
         */
        if (!isStart) {
            initData(savedInstanceState);
        }

        //开启悬浮窗
        FloatingView.get().attach(this);
        isStart = true;
    }


    @Override
    protected void onStop() {
        super.onStop();

        FloatingView.get().detach(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode, resultCode, data, new IUiListener() {
            @Override
            public void onComplete(Object o) {

            }

            @Override
            public void onError(UiError uiError) {

            }

            @Override
            public void onCancel() {

            }
        });
        if (data != null) EventBus.getDefault().post(data);
    }

    @Override
    protected void onDestroy() {
        if (useEventBus() && EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        if (unbinder != null) unbinder.unbind();
        if (dialog != null) {
            if (dialog.isShowing()) dialog.dismiss();
            dialog = null;
        }
        super.onDestroy();
        if (netReceiver != null) {
            unregisterReceiver(netReceiver);
        }

        FloatingView.get().remove();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getOptionsMenuId() > 0) {
            getMenuInflater().inflate(getOptionsMenuId(), menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    public int getOptionsMenuId() {
        return 0;
    }

    @Override
    public void showProgress() {
        if (dialog == null) dialog = new CustomProgressDialog(this);
        if (!dialog.isShowing()) dialog.show();
    }

    /**
     * 当activity设置了状态栏沉浸式样式，首先使activity实现DialogInterface.OnDismissListener接口，并调用此方法显示
     * dialog，然后在avtivity回调的onDismiss方法中重新设置activity的沉浸式样式
     *
     * @param onDismissListener
     */
    public void showProgress(DialogInterface.OnDismissListener onDismissListener) {
        if (dialog == null) dialog = new CustomProgressDialog(this);
        if (!dialog.isShowing()) dialog.show();
        dialog.setOnDismissListener(onDismissListener);
    }

    @Override
    public void hideProgress() {
        if (dialog != null && dialog.isShowing()) dialog.dismiss();
    }

    @Override
    public void showToastShort(CharSequence msg) {
        ToastUtil.showToastShort(msg);
    }

    @Override
    public void showToastLong(CharSequence msg) {
        ToastUtil.showToastLong(msg);
    }

    public void onResume() {
        super.onResume();
        if (!hasFragment()) {
            MobclickAgent.onPageStart(getClass().getName()); //手动统计页面
        }
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        //关闭悬浮窗
        FloatingView.get().removeNow();

        if (!hasFragment()) {
            MobclickAgent.onPageEnd(getClass().getName()); //手动统计页面
        }
        MobclickAgent.onPause(this);
        hideProgress();
        super.onPause();
    }

    public boolean hasFragment() {
        return false;
    }

    public boolean useEventBus() {
        return false;
    }

    /**
     * 是否可以使用沉浸式
     * Is immersion bar enabled boolean.
     *
     * @return the boolean
     */
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        hideSoftKeyBoard();
    }

    public void hideSoftKeyBoard() {
        View localView = getCurrentFocus();
        if (this.mInputMethodManager == null) {
            this.mInputMethodManager = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
        }
        if ((localView != null) && (this.mInputMethodManager != null)) {
            this.mInputMethodManager.hideSoftInputFromWindow(localView.getWindowToken(), 2);
        }
    }

    /**
     * activity现场保护方法
     * 备注：activity可能会被回收 此时需要把相关参数存放到bundle里 防止再次打开activity时getIntent()获取参数为空
     * outState.putAll(getIntent().getExtras()) 是指把传进来的参数全部放到bundle里
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (getIntent() != null && getIntent().getExtras() != null) {
            outState.putAll(getIntent().getExtras());
        }
    }

    protected void initImmersionBar() {
//        在BaseActivity里初始化
        ImmersionBar.with(this)
                .reset()
                .fitsSystemWindows(true)
                .keyboardEnable(false)
                .statusBarDarkFont(true, 0.2f)
                .statusBarColor(R.color.base_color_ffffff)
//                .navigationBarColor(R.color.base_color_ffffff)
                .init();
    }

}
