package com.jj.comics.ui.detail;

import android.os.Bundle;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.jj.base.mvp.BasePresenter;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.common.constants.Constants;

import butterknife.BindView;

/**
 * 小说简介详情
 */
@Route(path = RouterMap.COMIC_DETAIL_BOOKINFO_ACTIVITY)
public class BookInfoActivity extends BaseActivity {

    @BindView(R2.id.tv_info)
    TextView tv_info;//小说简介

    @Override
    protected void initData(Bundle savedInstanceState) {
        String bookInfo = getIntent().getStringExtra(Constants.IntentKey.BOOK_INFO);
        tv_info.setText(bookInfo);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.comic_activity_detail_bookinfo;
    }

    @Override
    public BasePresenter setPresenter() {
        return null;
    }
}
