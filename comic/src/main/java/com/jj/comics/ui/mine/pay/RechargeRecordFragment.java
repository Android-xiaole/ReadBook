package com.jj.comics.ui.mine.pay;

import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.jj.base.log.LogUtil;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseVPFragment;
import com.jj.base.utils.Utils;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.mine.NewRechargeRecordAdapter;
import com.jj.comics.data.model.RechargeRecordModel;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.LogoutEvent;
import com.jj.comics.widget.RecycleViewDivider;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;

/**
 * 充值记录
 */
public class RechargeRecordFragment extends BaseVPFragment<RechargeRecordPresenter> implements RechargeRecordContract.IRechargeRecordView, SwipeRefreshLayout.OnRefreshListener {
    //下拉刷新
    @BindView(R2.id.recommend_list_refresh)
    SwipeRefreshLayout mRefresh;
    //记录列表
    @BindView(R2.id.recommend_list_recycler)
    RecyclerView mRecyclerView;
    //顶部导航栏
    @BindView(R2.id.recommend_list_bar)
    AppBarLayout mBar;
    //列表适配器
    private NewRechargeRecordAdapter mAdapter;
    //当前页
    private int currentPage = 1;

    /**
     * 初始化
     *
     * @param savedInstanceState
     */
    @Override
    public void initData(Bundle savedInstanceState) {
        mBar.setVisibility(View.GONE);
        mRefresh.setColorSchemeColors(getResources().getColor(R.color.comic_yellow_ffd850));
        mRefresh.setOnRefreshListener(this);
        //充值记录
        mAdapter = new NewRechargeRecordAdapter(R.layout.comic_item_new_recharge_record);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.VERTICAL, Utils.dip2px(getContext(), 1), getResources().getColor(R.color.comic_efefef)));
        mAdapter.bindToRecyclerView(mRecyclerView, true, false);
        mAdapter.setEmptyImgSrc(R.drawable.img_weichongzhi, true);
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
            getActivity().finish();
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
        return R.layout.comic_activity_type_list;
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
