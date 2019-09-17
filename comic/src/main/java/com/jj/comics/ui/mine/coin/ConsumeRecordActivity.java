package com.jj.comics.ui.mine.coin;

import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.base.utils.Utils;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.mine.ComsumeRecordAdapter;
import com.jj.comics.common.constants.Constants;
import com.jj.comics.data.model.ExpenseSumRecordModel;
import com.jj.comics.widget.RecycleViewDivider;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;

/**
 * 消费记录页面
 */

@Route(path = RouterMap.COMIC_CONSUME_RECORD_ACTIVITY)
public class ConsumeRecordActivity extends BaseActivity<ConsumeRecordPresenter> implements ConsumeRecordContract.IConsumeRecordView,SwipeRefreshLayout.OnRefreshListener {
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
    private ComsumeRecordAdapter mAdapter;
    //当前页
    private int currentPage = 1;

    /**
     * 初始化数据
     * @param savedInstanceState
     */
    @Override
    public void initData(Bundle savedInstanceState) {
        mRefresh.setColorSchemeColors(getResources().getColor(R.color.comic_yellow_ffd850));
        mRefresh.setOnRefreshListener(this);

        mAdapter = new ComsumeRecordAdapter(R.layout.comic_item_consume_record);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.bindToRecyclerView(mRecyclerView, true, false);
        mAdapter.setEmptyImgSrc(R.drawable.img_weixiaofei, true);
        mAdapter.disableLoadMoreIfNotFullPage();
        mAdapter.isUseEmpty(true);
        //加载更多
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                currentPage++;
                getP().loadData(currentPage);
            }
        }, mRecyclerView);

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ExpenseSumRecordModel model = (ExpenseSumRecordModel) adapter.getData().get(position);
                ARouter.getInstance().build(RouterMap.COMIC_CONSUME_DETAIL_ACTIVITY).withLong(Constants.IntentKey.BOOK_ID,model.getArticleid()).navigation();
            }
        });

        //刷新
        mAdapter.setEmptyClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showProgress();
                currentPage = 1;
                mRefresh.setRefreshing(true);
                getP().loadData(currentPage);
            }
        });
//        showProgress();
        getP().loadData(currentPage);
    }

    /**
     * 填充数据
     * @param contentList
     */
    @Override
    public void fillData(List<ExpenseSumRecordModel> contentList) {
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
     * 处理异常数据情况
     * @param error 异常信息
     */
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
            mAdapter.addData(new ArrayList<ExpenseSumRecordModel>());
        }
//        hideProgress();
        currentPage--;
    }

    /**
     * 刷新列表
     */
    @Override
    public void onRefresh() {
        currentPage = 1;
        getP().loadData(currentPage);
    }

    /**
     * 加载布局
     * @return
     */
    @Override
    public int getLayoutId() {
        return R.layout.comic_activity_consume_record;
    }

    /**
     * 实例化P层用于数据传递
     *
     * @return
     */
    @Override
    public ConsumeRecordPresenter setPresenter() {
        return new ConsumeRecordPresenter();
    }
}
