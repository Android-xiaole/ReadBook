package com.jj.comics.ui.web;

import android.net.http.SslError;
import android.text.TextUtils;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jj.base.mvp.BaseRepository;
import com.jj.base.mvp.BasePresenter;

public class WebPresenter extends BasePresenter<BaseRepository,WebActivity> implements WebContract.IWebPresenter{
    WebViewClient mClient;

    public WebPresenter() {
        mClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!TextUtils.isEmpty(url) && url.startsWith("weixin:")) {
                    getV().showDiaLog(url);
                }
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }
        };
    }

    @Override
    public boolean useEventBus() {
        return false;
    }
}
