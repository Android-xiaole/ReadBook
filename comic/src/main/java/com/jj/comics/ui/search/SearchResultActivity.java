package com.jj.comics.ui.search;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.mine.CommonRecommendAdapter;
import com.jj.comics.adapter.mine.SearchResultAdapter;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.ui.detail.DetailActivityHelper;

import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
    @BindView(R2.id.rv_watchingData)
    RecyclerView rv_watchingData;

    private SearchResultAdapter mResultAdapter;
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

        mResultAdapter = new SearchResultAdapter(R.layout.comic_item_searchresult_activity);
        rv_searchResult.setNestedScrollingEnabled(false);
        rv_searchResult.setHasFixedSize(true);
        rv_searchResult.setLayoutManager(new GridLayoutManager(this, 5));
        mResultAdapter.bindToRecyclerView(rv_searchResult, true, true);
        mResultAdapter.setEmptyImgSrc(R.drawable.img_unsearch, false);
        mResultAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DetailActivityHelper.toDetail(SearchResultActivity.this,
                        mResultAdapter.getData().get(position).getId()+"", "搜索结果");
            }
        });

        rv_watchingData.setNestedScrollingEnabled(false);
        rv_watchingData.setHasFixedSize(true);
        mRecommendAdapter = new CommonRecommendAdapter(R.layout.comic_item_search_watchingcomicdata);
        rv_watchingData.setLayoutManager(new GridLayoutManager(this, 3));
        mRecommendAdapter.bindToRecyclerView(rv_watchingData);
        mRecommendAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DetailActivityHelper.toDetail(SearchResultActivity.this,
                        mRecommendAdapter.getData().get(position).getId(), "搜索结果_热门搜索");

            }
        });
        rv_watchingData.setNestedScrollingEnabled(false);
        rv_watchingData.setHasFixedSize(true);

        showProgress();
        String key = getIntent().getStringExtra(Constants.IntentKey.KEY);
        et_search.setText(key);
        et_search.setSelection(key.length());

        getP().getSearchComicListByKeywords(key);
        getP().getWatchingComicData();
    }


    @OnClick({R2.id.search_result_back})
    void dealClick(View view) {
        int id = view.getId();
        if (id == R.id.search_result_back) {
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
        mResultAdapter.setNewData(contentList);
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
