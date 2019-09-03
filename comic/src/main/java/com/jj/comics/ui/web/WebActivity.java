package com.jj.comics.ui.web;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.StatusBarUtil;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.ui.dialog.DialogUtilForComic;
import com.jj.comics.ui.dialog.NormalNotifyDialog;

import butterknife.BindView;

@Route(path = RouterMap.COMIC_WEB_ACTIVITY)
public class WebActivity extends BaseActivity<WebPresenter> implements WebContract.IWebView{
    @BindView(R2.id.webView)
    WebView mWebView;
    private NormalNotifyDialog mDialog;

    @Override
    public void initData(Bundle savedInstanceState) {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.comic_yellow_ffd850), 77);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // 开启 DOM storage API 功能
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setSupportZoom(false);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.setWebViewClient(getP().mClient);
        showProgress();
        mWebView.loadUrl(getIntent().getStringExtra("url"));
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_web;
    }

    @Override
    public WebPresenter setPresenter() {
        return new WebPresenter();
    }

    public void showDiaLog(final String url) {
        hideProgress();
        if (mDialog == null)
            mDialog = new NormalNotifyDialog();
        mDialog.show(getSupportFragmentManager(), getString(R.string.base_pay_confirm),getString(R.string.comic_pay_remind), new DialogUtilForComic.OnDialogClick() {
            @Override
            public void onConfirm() {
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
                setResult(RESULT_OK);
            }

            @Override
            public void onRefused() {
                setResult(RESULT_CANCELED);
                finish();
            }

        });
//            mDialog = DialogUtil.showPayConfirmDialog(this, getResources().getString(R.string.comic_pay_remind), new DialogUtil.OnDialogClick() {
//                @Override
//                public void onConfirm() {
//                    startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
//                    setResult(RESULT_OK);
//                }
//
//                @Override
//                public void onRefused() {
//                    setResult(RESULT_CANCELED);
//                    finish();
//                }
//            });
        startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
    }
}
