package com.jj.comics.ui.mine.notify;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.SharedPref;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.ui.web.WebViewActivity;
import com.jj.comics.util.SharedPreManger;


import butterknife.BindView;

@Route(path = RouterMap.COMIC_MINE_NOTIFICATION_DETAIL)
public class NotificationDetailActivity extends BaseActivity<NotificationDetailPresenter> implements NotificationDetailContract.INotificationDetailView {
    @BindView(R2.id.tv_ss_title)
    TextView mTvTitle;
    @BindView(R2.id.tv_content)
    TextView mTvContent;
    @BindView(R2.id.webview)
    WebView mWebView;
    @Override
    protected void initData(Bundle savedInstanceState) {
        long id = getIntent().getLongExtra(Constants.IntentKey.ID, -1);
//        if (longExtra >= 0) {
//            getP().getNotificationDetail(longExtra);
//        }else {
//            mTvTitle.setText("详情不存在!");
//        }

        String msg =
                "http://share.sou89.cn/notice.html?id="+id+"&token=" + SharedPreManger.getInstance().getToken();
        ARouter.getInstance().build(RouterMap.COMIC_WEBVIEW_ACTIVITY).withString(WebViewActivity.URL_KEY,msg).navigation();
        finish();

//        mWebView.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                return true;
//            }
//        });
//
//        initWebView(mWebView);
    }

    private void initWebView(WebView webView) {
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
    protected int getLayoutId() {
        return R.layout.comic_activity_notification_detail;
    }

    @Override
    public NotificationDetailPresenter setPresenter() {
        return new NotificationDetailPresenter();
    }

    @Override
    public void showText(String title,String text) {
        mTvTitle.setText(title);
        mTvContent.setText(text);
    }

    @Override
    public void showHtml(String url) {
        ARouter.getInstance().build(RouterMap.COMIC_WEBVIEW_ACTIVITY).withString(WebViewActivity.URL_KEY,url).navigation();
        finish();
    }

    @Override
    public void showErr(String err) {
        mTvTitle.setText("详情不存在! \n" + err);
    }

    @Override
    public void showHtmlLocal(String title,String html) {
        mTvTitle.setText(title);
        mWebView.setVisibility(View.VISIBLE);
        mWebView.loadData(html,"text/html","utf-8");

//        Document document = Jsoup.parse(html);
//        mWebView.loadData(document.html(),"text/html","utf-8");
    }
}
