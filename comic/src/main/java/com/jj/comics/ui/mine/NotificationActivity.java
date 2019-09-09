package com.jj.comics.ui.mine;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.mine.NotificationListAdapter;
import com.jj.comics.data.model.NotificationListResponse;

import java.util.List;

import butterknife.BindView;


@Route(path=RouterMap.COMIC_NOTIFICATION_ACTIVITY)
public class NotificationActivity extends BaseActivity<NotificationPresenter> implements NotificationContract.INotionficatonView , SwipeRefreshLayout.OnRefreshListener
, BaseQuickAdapter.RequestLoadMoreListener {
    @BindView(R2.id.swipeRefreshLayout)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R2.id.recyclerView)
    RecyclerView mRecyclerView;

    private int currentPage = 1;
    private NotificationListAdapter mNotificationListAdapter;


    @Override
    protected void initData(Bundle savedInstanceState) {
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setRefreshing(true);
        mNotificationListAdapter = new NotificationListAdapter(R.layout.comic_item_notification);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mNotificationListAdapter.bindToRecyclerView(mRecyclerView,true);
        mNotificationListAdapter.disableLoadMoreIfNotFullPage(mRecyclerView);
        mNotificationListAdapter.setOnLoadMoreListener(this,mRecyclerView);
        getP().getNotificationList(currentPage);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.comic_activity_notification;
    }

    @Override
    public NotificationPresenter setPresenter() {
        return new NotificationPresenter();
    }

    @Override
    public void onRefresh() {
        mRefreshLayout.setRefreshing(true);
        currentPage = 1;
        getP().getNotificationList(currentPage);
    }

    @Override
    public void onGetNotificationSucc(List<NotificationListResponse.DataBeanX.SimpleNotificationDataBean> list) {
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
    public void onGetNotificationFail() {
        mRefreshLayout.setRefreshing(false);
        currentPage --;
        mNotificationListAdapter.loadMoreFail();
    }

    @Override
    public void onLoadMoreRequested() {
        currentPage ++;
        getP().getNotificationList(currentPage);
    }
}
