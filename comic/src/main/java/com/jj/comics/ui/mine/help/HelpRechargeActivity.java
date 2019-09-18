package com.jj.comics.ui.mine.help;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.request.RequestOptions;
import com.jj.base.imageloader.ILFactory;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.widget.UserItemView;
import com.jj.comics.widget.comic.toolbar.ComicToolBar;

import butterknife.BindView;

@Route(path = RouterMap.COMIC_HELP_RECHARGE_ACTIVITY)
public class HelpRechargeActivity extends BaseActivity {
    @BindView(R2.id.toolBar)
    ComicToolBar mToolBar;
    @BindView(R2.id.iv_help)
    ImageView mImageView;
    @Override
    protected void initData(Bundle savedInstanceState) {
        mToolBar.setTitleText("充值返利");

        RequestOptions options = new RequestOptions();
        options.error(R.drawable.img_base_empty)
                .encodeQuality(100);
        ILFactory.getLoader()
                .loadNet(mImageView,"http://fanli.jjmh668.cn/prd/help-recharge.png",
                        options);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.comic_activity_help_detail;
    }

    @Override
    public BasePresenter setPresenter() {
        return null;
    }
}
