package com.jj.comics.ui.mine.coin;

import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.jj.base.log.LogUtil;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.mine.RechargeRecordAdapter;
import com.jj.comics.data.model.RechargeRecordModel;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.LogoutEvent;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;

/**
 * 充值记录
 */

@Route(path = RouterMap.COMIC_RECHARGE_RECORD_ACTIVITY)
public class RechargeRecordActivity extends BaseActivity<RechargeRecordPresenter> implements RechargeRecordContract.IRechargeRecordView, SwipeRefreshLayout.OnRefreshListener {
    //下拉刷新
    @BindView(R2.id.swipeRefreshLayout)
    SwipeRefreshLayout mRefresh;
    //记录列表
    @BindView(R2.id.recyclerView)
    RecyclerView mRecyclerView;
    //列表适配器
    private RechargeRecordAdapter mAdapter;
    //当前页
    private int currentPage = 1;

    /**
     * 初始化
     *
     * @param savedInstanceState
     */
    @Override
    public void initData(Bundle savedInstanceState) {
        mRefresh.setColorSchemeColors(getResources().getColor(R.color.comic_yellow_ffd850));
        mRefresh.setOnRefreshListener(this);
        //充值记录
        mAdapter = new RechargeRecordAdapter(R.layout.comic_item_recharge_record);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mRecyclerView.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL,
//                Utils.dip2px(this, 1), getResources().getColor(R.color.comic_efefef)));
        mAdapter.bindToRecyclerView(mRecyclerView, true, false);
        mAdapter.setEmptyImgSrc(R.drawable.img_empty_buy, true);
        mAdapter.isUseEmpty(true);
        mAdapter.disableLoadMoreIfNotFullPage();
        //加载更多数据
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                currentPage++;
                getP().loadData(currentPage);
            }
        }, mRecyclerView);

        /**
         * 刷新数据
         */
        mAdapter.setEmptyClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showProgress();
                currentPage = 1;
                mRefresh.setRefreshing(true);
                getP().loadData(currentPage);
            }
        });
        mRefresh.setRefreshing(true);
        getP().loadData(currentPage);
    }

    /**
     * 填充数据
     *
     * @param contentList 充值数据
     */
    @Override
    public void fillData(List<RechargeRecordModel> contentList) {
        LogUtil.e(new Gson().toJson(contentList));
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

    /**
     * 错误数据展示
     *
     * @param error 异常信息
     */
    public void getDataFail(NetError error) {

        if (error.getType() == NetError.AuthError) {
            finish();
            EventBusManager.sendLogoutEvent(new LogoutEvent());
        } else {
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
                mAdapter.addData(new ArrayList<RechargeRecordModel>());
            }
            currentPage--;
        }
    }

    /**
     * 刷新数据
     */
    @Override
    public void onRefresh() {
        currentPage = 1;
        getP().loadData(currentPage);
    }

    /**
     * 加载布局
     *
     * @return
     */
    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_recharge_record;
    }

    /**
     * 实例化 P 层
     *
     * @return
     */
    @Override
    public RechargeRecordPresenter setPresenter() {
        return new RechargeRecordPresenter();
    }

}
