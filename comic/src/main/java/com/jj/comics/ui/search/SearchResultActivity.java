package com.jj.comics.ui.search;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.immersionbar.ImmersionBar;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.detail.CommonRecommendAdapter;
import com.jj.comics.adapter.recommend.RecentlyAdapter;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.ui.detail.ComicDetailActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.COMIC_SEARCH_RESULT_ACTIVITY)
public class SearchResultActivity extends BaseActivity<SearchResultPresenter> implements SearchResultContract.ISearchResultView{
    @BindView(R2.id.search_result_edit)
    EditText et_search;
    @BindView(R2.id.iv_search)
    ImageView iv_search;
    @BindView(R2.id.rv_searchResult)
    RecyclerView rv_searchResult;

    private RecentlyAdapter adapter_recently;//最近更新adapter
    private CommonRecommendAdapter mRecommendAdapter;

    @Override
    public void initData(Bundle savedInstanceState) {
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                int i = v.getId();
                if (i == R.id.search_result_edit && actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //输入法点击搜索
                    String key = et_search.getText().toString().trim();
                    if (TextUtils.isEmpty(key)) {
                        showToastShort(getString(R.string.comic_search_remind));
                    } else {
                        showProgress();
                        //do net work
                        getP().getSearchComicListByKeywords(key);
                    }
                }
                return false;
            }
        });
        //点击搜索图标的点击事件
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = et_search.getText().toString().trim();
                if (TextUtils.isEmpty(key)) {
                    showToastShort(getString(R.string.comic_search_remind));
                } else{
                    showProgress();
                    //do net work
                    getP().getSearchComicListByKeywords(key);
                }
            }
        });

        adapter_recently = new RecentlyAdapter(R.layout.comic_item_recommend_recentlyupdate, 1);
        rv_searchResult.setNestedScrollingEnabled(false);
        rv_searchResult.setHasFixedSize(true);
        rv_searchResult.setLayoutManager(new LinearLayoutManager(this));
        adapter_recently.bindToRecyclerView(rv_searchResult, true, true);
        adapter_recently.setEmptyImgSrc(R.drawable.img_unsearch, false);
        adapter_recently.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ComicDetailActivity.toDetail(SearchResultActivity.this,
                        adapter_recently.getData().get(position).getId(), "搜索结果");
            }
        });

        showProgress();
        String key = getIntent().getStringExtra(Constants.IntentKey.KEY);
        et_search.setText(key);
        et_search.setSelection(key.length());

        getP().getSearchComicListByKeywords(key);
    }


    @OnClick({R2.id.search_result_back,R2.id.search_cancel})
    void dealClick(View view) {
        int id = view.getId();
        if (id == R.id.search_result_back) {
            finish();
        } else if (view.getId() == R.id.search_cancel) {
            finish();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_search_result;
    }

    @Override
    public SearchResultPresenter setPresenter() {
        return new SearchResultPresenter();
    }

    @Override
    public void fillSearchComicListByKeywords(List<BookModel> contentList) {
        adapter_recently.setNewData(contentList);
    }

    @Override
    public void fillWatchingComicData(List<BookModel> contentList) {
        mRecommendAdapter.setNewData(contentList);
    }

    @Override
    public void onComplete() {
        hideProgress();
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        ImmersionBar.with(this)
                .reset()
                .fitsSystemWindows(true)
                .keyboardEnable(true)
                .statusBarColor(R.color.base_color_ffffff)
                .statusBarDarkFont(true, 0.2f)
                .init();
    }
}
