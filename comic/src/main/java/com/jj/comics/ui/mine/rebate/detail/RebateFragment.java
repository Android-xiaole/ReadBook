package com.jj.comics.ui.mine.rebate.detail;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseVPFragment;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.mine.RebateListAdapter;
import com.jj.comics.data.model.RebateListResponse;

import java.util.List;

import butterknife.BindView;

@Route(path = RouterMap.COMIC_REBATE_FRAGMENT)
public class RebateFragment extends BaseVPFragment<RebatePresenter> implements RebateContract.IRebateView,SwipeRefreshLayout.OnRefreshListener
,BaseQuickAdapter.RequestLoadMoreListener{
    @BindView(R2.id.rv)
    RecyclerView mRecyclerView;

    @BindView(R2.id.mRefreshLayout)
    SwipeRefreshLayout mRefreshLayout;

    private int currentPage = 1;
    private RebateListAdapter mNotificationListAdapter;


    @Override
    public void initData(Bundle savedInstanceState) {
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setRefreshing(true);
        mNotificationListAdapter = new RebateListAdapter(R.layout.comic_item_rebate_detail);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        mNotificationListAdapter.bindToRecyclerView(mRecyclerView,true);
        mNotificationListAdapter.disableLoadMoreIfNotFullPage(mRecyclerView);
        mNotificationListAdapter.setOnLoadMoreListener(this,mRecyclerView);
        getP().getRebateList(currentPage);
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_fragment_rebate;
    }

    @Override
    public RebatePresenter setPresenter() {
        return new RebatePresenter();
    }

    @Override
    public void onRefresh() {
        mRefreshLayout.setRefreshing(true);
        currentPage = 1;
        getP().getRebateList(currentPage);
    }

    @Override
    public void onGetRebateListSucc(List<RebateListResponse.DataBeanX.RebateModel> list) {
        mRefreshLayout.setRefreshing(false);
        if (currentPage == 1) {
            mNotificationListAdapter.setNewData(list);
        }else{
            mNotificationListAdapter.addData(list);
        }
        if(list.size() == 0) {
            mNotificationListAdapter.loadMoreEnd(true);
        }else {
            mNotificationListAdapter.loadMoreComplete();
        }
    }


    @Override
    public void onGetRebateListFail(NetError error) {
        mRefreshLayout.setRefreshing(false);
        currentPage --;
        mNotificationListAdapter.loadMoreFail();
    }

    @Override
    public void onLoadMoreRequested() {
        currentPage ++;
        getP().getRebateList(currentPage);
    }
}
