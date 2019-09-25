package com.jj.comics.ui.web;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.Utils;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.widget.comic.toolbar.ComicToolBar;

import butterknife.BindView;

@Route(path = RouterMap.COMIC_WEBVIEW_ACTIVITY)
public class WebViewActivity extends BaseActivity {
    public static final String URL_KEY = "url";

    @BindView(R2.id.toolBar)
    ComicToolBar toolBar;
    @BindView(R2.id.webView)
    WebView webView;
    @BindView(R2.id.view_progress)
    View view_progress;

    @Override
    protected void initData(Bundle savedInstanceState) {
        initWebView();

        String url = getIntent().getStringExtra(URL_KEY);
        webView.loadUrl(url);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
//                LogUtils.e(newProgress);
                if (view_progress == null) {
                    return;
                }
                view_progress.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams layoutParams = view_progress.getLayoutParams();
                layoutParams.width = Utils.getScreenWidth(WebViewActivity.this) * newProgress / 100;
                view_progress.setLayoutParams(layoutParams);
//                LogUtils.e("宽度："+layoutParams.width);
                if (newProgress == 100) {
                    view_progress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (toolBar == null) {
                    return;
                }
                toolBar.setTitleText(title);
            }

        });
        webView.setWebViewClient(new WebViewClient() {
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("alipays://")) {
                    startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
                    finish();
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.comic_activity_webview;
    }

    @Override
    public BasePresenter setPresenter() {
        return null;
    }

    private void initWebView() {
        WebSettings webSettings = webView.getSettings();
        /**
         * 用WebView显示图片，可使用这个参数 设置网页布局类型：
         * 1、LayoutAlgorithm.NARROW_COLUMNS ：适应内容大小
         * 2、LayoutAlgorithm.SINGLE_COLUMN:适应屏幕，内容将自动缩放
         */
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBlockNetworkImage(false);//解决图片不显示

        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); //
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

        webSettings.setDomStorageEnabled(true);//打开DOM储存API

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//解决加载混合资源图片不显示的问题
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
