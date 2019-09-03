package com.jj.comics.ui.recommend;

import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.recommend.RecommendLoadMoreAdapetr;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.ui.detail.DetailActivityHelper;
import com.jj.comics.widget.comic.toolbar.ComicToolBar;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;

/**
 * 推荐页各个专区的加载更多
 */
@Route(path = RouterMap.COMIC_RECOMMEND_LOADMORE)
public class RecommendLoadMoreActovity extends BaseActivity<RecommendLoadMorePresenter> implements SwipeRefreshLayout.OnRefreshListener, RecommendLoadMoreContract.View {

    @BindView(R2.id.toolBar)
    ComicToolBar toolBar;
    @BindView(R2.id.swipeRefreshLayout)
    SwipeRefreshLayout mRefresh;
    @BindView(R2.id.recyclerView)
    RecyclerView mRecyclerView;

    private int sectionId;
    private String title;
    private int currentPage = 1;
    private RecommendLoadMoreAdapetr mAdapter;

    @Override
    protected void initData(Bundle savedInstanceState) {
        sectionId = getIntent().getIntExtra(Constants.IntentKey.SECTION_ID, 1);
        title = getIntent().getStringExtra(Constants.IntentKey.TITLE);
        toolBar.setTitleText(title);

        mRefresh.setColorSchemeColors(getResources().getColor(R.color.comic_yellow_ffd850));
        mRefresh.setOnRefreshListener(this);
        mAdapter = new RecommendLoadMoreAdapetr(R.layout.comic_item_recommend_loadmore);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.bindToRecyclerView(mRecyclerView, true);

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //上传榜单点击事件到友盟
                BookModel bookModel = mAdapter.getData().get(position);
                MobclickAgent.onEvent(getApplication(), Constants.UMEventId.RANK,
                        bookModel.getId() + " : " + bookModel.getTitle());
//                showToastShort("跳转路径未设置" + bookModel.getId());
//                DetailActivityHelper.toDetail(RecommendLoadMoreActovity.this, mainContent, title);
                DetailActivityHelper.toDetail(RecommendLoadMoreActovity.this,
                        mAdapter.getData().get(position).getId(),
                        "专区更多");
            }
        });
        mAdapter.disableLoadMoreIfNotFullPage();
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                currentPage++;
                getP().getSectionDataListBySectionId(currentPage, sectionId, false);
            }
        }, mRecyclerView);

        mAdapter.setEmptyClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRefresh.setRefreshing(true);
                currentPage = 1;
                getP().getSectionDataListBySectionId(currentPage, sectionId, false);
            }
        });
        mRefresh.setRefreshing(true);
        getP().getSectionDataListBySectionId(currentPage, sectionId, false);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.comic_activity_recommend_loadmore;
    }

    @Override
    public RecommendLoadMorePresenter setPresenter() {
        return new RecommendLoadMorePresenter();
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        getP().getSectionDataListBySectionId(currentPage, sectionId, false);
    }

    @Override
    public void fillData(List<BookModel> contentList) {
        if (currentPage == 1) {
            mAdapter.setNewData(contentList);
        } else {
            mAdapter.addData(contentList);
        }
        mAdapter.loadMoreComplete();
        if (20 * currentPage > mAdapter.getData().size()) {
            mAdapter.loadMoreEnd(false);
        }
        if (mRefresh.isRefreshing())
            mRefresh.setRefreshing(false);
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
        mAdapter.setEmptyText(error.getMessage());
        if (currentPage == 1) {
            mAdapter.setNewData(null);
        } else {
            mAdapter.addData(new ArrayList<BookModel>());
        }
        currentPage--;
    }

}
