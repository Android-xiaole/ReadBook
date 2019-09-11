package com.jj.comics.ui.mine.coin;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.mine.ComsumeDetailAdapter;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.model.ConsumeDetailListResponse;
import com.jj.comics.data.model.ExpenseSumRecordModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


@Route(path = RouterMap.COMIC_CONSUME_DETAIL_ACTIVITY)
public class ConsumeDetailActivity extends BaseActivity<ConsumeDetailPreseneter> implements ConsumeDetailContract.IConsumeDetailView , SwipeRefreshLayout.OnRefreshListener {
    /**
     * 下拉刷新页面
     */
    @BindView(R2.id.swipeRefreshLayout)
    SwipeRefreshLayout mRefresh;
    /**
     * 列表
     */
    @BindView(R2.id.recyclerView)
    RecyclerView mRecyclerView;
    private ComsumeDetailAdapter mAdapter;

    private long mBookId = -1;

    @Override
    protected void initData(Bundle savedInstanceState) {
        mRefresh.setColorSchemeColors(getResources().getColor(R.color.comic_yellow_ffd850));
        mRefresh.setOnRefreshListener(this);

        mAdapter = new ComsumeDetailAdapter(R.layout.comic_item_consume_detail);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.bindToRecyclerView(mRecyclerView, true, false);
        mAdapter.setEmptyImgSrc(R.drawable.img_weixiaofei, true);
        mAdapter.isUseEmpty(true);

        mBookId = getIntent().getLongExtra(Constants.IntentKey.BOOK_ID, -1);


        //刷新
        mAdapter.setEmptyClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRefresh.setRefreshing(true);
                getP().getConsumeDetail(mBookId);
            }
        });
        getP().getConsumeDetail(mBookId);
    }

    /**
     * 填充数据
     * @param contentList
     */
    @Override
    public void fillData(List<ConsumeDetailListResponse.DataBeanX.ConsumeDetail> contentList) {
        mAdapter.setNewData(contentList);
        if (mRefresh.isRefreshing())
            mRefresh.setRefreshing(false);
    }


    /**
     * 处理异常数据情况
     * @param error 异常信息
     */
    @Override
    public void getDataFail(NetError error) {
        if (mRefresh.isRefreshing())
            mRefresh.setRefreshing(false);
        mAdapter.setEmptyText(error.getMessage());
        mAdapter.setNewData(null);
    }

    /**
     * 刷新列表
     */
    @Override
    public void onRefresh() {
        getP().getConsumeDetail(mBookId);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.comic_activity_consume_record_detail;
    }

    @Override
    public ConsumeDetailPreseneter setPresenter() {
        return new ConsumeDetailPreseneter();
    }

}
