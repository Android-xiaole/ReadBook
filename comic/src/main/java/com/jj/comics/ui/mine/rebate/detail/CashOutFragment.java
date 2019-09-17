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
import com.jj.comics.adapter.mine.CashOutListAdapter;
import com.jj.comics.data.model.CashOutListResponse;

import java.util.List;

import butterknife.BindView;

@Route(path = RouterMap.COMIC_CASHOUT_FRAGMENT)
public class CashOutFragment extends BaseVPFragment<CashOutPresenter> implements CashOutContract.ICashOutView, SwipeRefreshLayout.OnRefreshListener
        , BaseQuickAdapter.RequestLoadMoreListener {

    @BindView(R2.id.rv)
    RecyclerView mRecyclerView;

    @BindView(R2.id.mRefreshLayout)
    SwipeRefreshLayout mRefreshLayout;

    private int currentPage = 1;
    private CashOutListAdapter mCashOutListAdapter;


    @Override
    public void initData(Bundle savedInstanceState) {
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setRefreshing(true);
        mCashOutListAdapter = new CashOutListAdapter(R.layout.comic_item_cash_out);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        mCashOutListAdapter.bindToRecyclerView(mRecyclerView, true);
        mCashOutListAdapter.disableLoadMoreIfNotFullPage(mRecyclerView);
        mCashOutListAdapter.setOnLoadMoreListener(this, mRecyclerView);
//        getP().getCashOutList(currentPage);
    }

    @Override
    public void onResume() {
        super.onResume();
        mRefreshLayout.setRefreshing(true);
        currentPage = 1;
        getP().getCashOutList(currentPage);
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_fragment_cash_out;
    }

    @Override
    public CashOutPresenter setPresenter() {
        return new CashOutPresenter();
    }

    @Override
    public void onRefresh() {
        mRefreshLayout.setRefreshing(true);
        currentPage = 1;
        getP().getCashOutList(currentPage);
    }


    @Override
    public void onGetDataSucc(List<CashOutListResponse.DataBeanX.CashOutModel> list) {
        mRefreshLayout.setRefreshing(false);
        if (currentPage == 1) {
            mCashOutListAdapter.setNewData(list);
        } else {
            mCashOutListAdapter.addData(list);
        }
        if (list.size() == 0) {
            mCashOutListAdapter.loadMoreEnd(true);
        } else {
            mCashOutListAdapter.loadMoreComplete();
        }
    }


    @Override
    public void onGetDataFail(NetError error) {
        mRefreshLayout.setRefreshing(false);
        currentPage--;
        mCashOutListAdapter.loadMoreFail();
    }

    @Override
    public void onLoadMoreRequested() {
        currentPage++;
        getP().getCashOutList(currentPage);
    }
}
