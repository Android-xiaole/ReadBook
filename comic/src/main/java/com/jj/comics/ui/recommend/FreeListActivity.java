package com.jj.comics.ui.recommend;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.CommonUtil;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.Utils;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.recommend.FreeListAdapter;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.ui.detail.DetailActivityHelper;
import com.jj.comics.widget.RecycleViewDivider;
import com.jj.comics.widget.comic.toolbar.ComicToolBar;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;

/**
 * 推荐页的免费专区
 */
@Route(path = RouterMap.COMIC_FREE_LIST_ACTIVITY)
public class FreeListActivity extends BaseActivity<FreeListPresenter> implements FreeListContract.IFreeListView,SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener, BaseQuickAdapter.OnItemClickListener {
    @BindView(R2.id.list_refresh)
    SwipeRefreshLayout mRefresh;//限时免费列表
    @BindView(R2.id.rv_rankList)
    RecyclerView mRecyclerView;//限时免费列表
    @BindView(R2.id.toolBar)
    ComicToolBar toolBar;
    private FreeListAdapter mAdapter;
    private int currentPage = 1;

    private boolean evict = false;

    @Override
    public void initData(Bundle savedInstanceState) {
        mRefresh.setColorSchemeColors(getResources().getColor(R.color.comic_yellow_ffd850));
        mRefresh.setOnRefreshListener(this);

        mAdapter = new FreeListAdapter(R.layout.comic_item_free_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL, Utils.dip2px(this, 15), Color.WHITE));
        mAdapter.bindToRecyclerView(mRecyclerView, true);
        mAdapter.disableLoadMoreIfNotFullPage();
        mAdapter.setOnLoadMoreListener(this, mRecyclerView);
        mAdapter.setOnItemClickListener(this);
        mRefresh.setRefreshing(true);
        getP().loadData(currentPage, evict);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        DetailActivityHelper.toDetail(FreeListActivity.this,
                mAdapter.getData().get(position).getId(),
                "免费专区");
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        evict = true;
        getP().loadData(currentPage, evict);
    }

    @Override
    public void onLoadMoreRequested() {
        currentPage++;
        getP().loadData(currentPage, evict);
    }

    @Override
    public void fillData(List<BookModel> bookModelList) {
//        toolBar.setTitleText(comicModel == null || comicModel.getPage() == null ? "" : comicModel.getPage().getName());
        toolBar.setTitleText("免费专区");
        if (!CommonUtil.checkEmpty(bookModelList)) {
            if (currentPage == 1) {
                mAdapter.setNewData(bookModelList);
            } else {
                mAdapter.addData(bookModelList);
            }
        }

        mAdapter.loadMoreComplete();
        if (FreeListPresenter.PAGE_SIZE * currentPage > mAdapter.getData().size()) {
            mAdapter.loadMoreEnd(false);
        }
        if (mRefresh.isRefreshing())
            mRefresh.setRefreshing(false);
//        hideProgress();
    }

    @Override
    public void getDataFail(NetError error) {
        if (error.getType() == NetError.NoDataError) {
            mAdapter.loadMoreEnd(false);
        } else {
            mAdapter.loadMoreFail();
        }
        if (mRefresh.isRefreshing())
            mRefresh.setRefreshing(false);
        if (currentPage == 1) {
            mAdapter.setNewData(null);
        } else {
            mAdapter.addData(new ArrayList<BookModel>());
        }
        currentPage--;
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_free_list;
    }

    @Override
    public FreeListPresenter setPresenter() {
        return new FreeListPresenter();
    }

    @Override
    protected void initImmersionBar() {
        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.comic_ffffff)
//                .navigationBarColor(R.color.comic_ffffff)
                .statusBarDarkFont(true, 0.2f)
                .init();
    }

}
