package com.jj.comics.ui.list.rank;

import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseVPFragment;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.rank.CommomRankAdapter;
import com.jj.comics.common.callback.GlideOnScrollListener;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.ui.detail.DetailActivityHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;

@Route(path = RouterMap.COMIC_POPULARITY_FRAGMENT)
public class CommonRankFragment extends BaseVPFragment<CommonRankPresenter> implements CommonRankContract.ICommonRankView,SwipeRefreshLayout.OnRefreshListener {
    @BindView(R2.id.common_refresh)
    SwipeRefreshLayout mRefresh;
    @BindView(R2.id.common_recycler)
    RecyclerView mRecyclerView;

    private int currentPage = 1;
    private CommomRankAdapter mAdapter;
    private String ACTION_ID = "popularity";

    @Override
    public void onRefresh() {
        currentPage = 1;
        getP().getRankListByAction(currentPage, ACTION_ID, false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mRefresh.setColorSchemeColors(getResources().getColor(R.color.comic_yellow_ffd850));
        mRefresh.setOnRefreshListener(this);
        mAdapter = new CommomRankAdapter(R.layout.comic_item_rank);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addOnScrollListener(new GlideOnScrollListener());
        mAdapter.bindToRecyclerView(mRecyclerView, true);

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //上传榜单点击事件到友盟
                BookModel bookModel = mAdapter.getData().get(position);
//                showToastShort("跳转路径未指定" + mainContent.getId());
                MobclickAgent.onEvent(getContext(), Constants.UMEventId.RANK, bookModel.getId() + " : " + bookModel.getTitle());
                DetailActivityHelper.toDetail(getActivity(),
                        mAdapter.getData().get(position).getId(),
                        "榜单");
            }
        });
        mAdapter.disableLoadMoreIfNotFullPage();
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                currentPage++;
                getP().getRankListByAction(currentPage, ACTION_ID, false);
            }
        }, mRecyclerView);

        mAdapter.setEmptyClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showProgress();
                mRefresh.setRefreshing(true);
                currentPage = 1;
                getP().getRankListByAction(currentPage, ACTION_ID, false);
            }
        });
        ACTION_ID = getArguments().getString(Constants.IntentKey.ACTION, "popularity");
        mRefresh.setRefreshing(true);
//        showProgress();
        getP().getRankListByAction(currentPage, ACTION_ID, false);
    }

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
//        hideProgress();
    }

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
//        hideProgress();
        currentPage--;
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_fragment_simple;
    }

    @Override
    public CommonRankPresenter setPresenter() {
        return new CommonRankPresenter();
    }

}
