package com.jj.comics.ui.mine.apprentice;

import android.os.Bundle;
import android.view.View;

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
import com.jj.comics.adapter.mine.ApprenticeAdapter;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.model.ApprenticeModel;

import java.util.List;

import butterknife.BindView;

@Route(path = RouterMap.COMIC_MINE_APPRENTICE_TUZI_FRAGMENT)
public class TuziFragment extends BaseVPFragment <TuziPresenter> implements TuziContract.ITuziView{
    @BindView(R2.id.common_refresh)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R2.id.common_recycler)
    RecyclerView mRecyclerView;
    private ApprenticeAdapter mAdapter;
    private int currentPage = 1;

    @Override
    public void initData(Bundle savedInstanceState) {
        mAdapter = new ApprenticeAdapter(R.layout.comic_item_apprentice);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);
        mAdapter.bindToRecyclerView(mRecyclerView,true);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                currentPage ++;
                getP().getData(currentPage, Constants.APPRENTICE.TUZI);
            }
        },mRecyclerView);

        mRefreshLayout.setRefreshing(true);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                getP().getData(currentPage, Constants.APPRENTICE.TUZI);
            }
        });
        getP().getData(currentPage, Constants.APPRENTICE.TUZI);
    }

    @Override
    public int getLayoutId() {
        return R.layout.comic_fragment_simple;
    }

    @Override
    public TuziPresenter setPresenter() {
        return new TuziPresenter();
    }

    @Override
    public void onGetData(List<ApprenticeModel> list) {
        mRefreshLayout.setRefreshing(false);
        if (currentPage == 1) {
            mAdapter.setNewData(list);
            mAdapter.disableLoadMoreIfNotFullPage();
        }else {
            mAdapter.addData(list);
        }
    }

    @Override
    public void onGetDataFail(NetError error) {
        mRefreshLayout.setRefreshing(false);
        mAdapter.loadMoreEnd(true);
    }
}
