package com.jj.comics.ui.mine.bought;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.jj.comics.adapter.mine.BoughtAdapter;
import com.jj.comics.data.model.BoughtResponse;
import com.jj.comics.ui.detail.ComicDetailActivity;
import com.jj.comics.widget.comic.toolbar.ComicToolBar;

import java.util.List;

import butterknife.BindView;


@Route(path = RouterMap.COMIC_BOUGHT_ACTIVITY)
public class BoughtActivity extends BaseActivity<BoughtPresenter> implements BoughtContract.IBoughtView {
    @BindView(R2.id.swipeRefreshLayout)
    SwipeRefreshLayout mRefresh;
    @BindView(R2.id.recyclerView)
    RecyclerView mRecycler;
    @BindView(R2.id.comic_tool_bar)
    ComicToolBar toolBar;
    private BoughtAdapter mAdapter;
    private int currentPage = 1;

    @Override
    protected void initData(Bundle savedInstanceState) {
        toolBar.setTitleText("我的购买");
        mAdapter = new BoughtAdapter(R.layout.comic_bought_item);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.bindToRecyclerView(mRecycler);
        mAdapter.setEmptyView(getEmptyView());

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                BoughtResponse.DataBeanX.BoughtModel bookModel = mAdapter.getData().get(position);
                if (bookModel == null) return;
                ComicDetailActivity.toDetail(BoughtActivity.this,
                        bookModel.getArticleid(),
                        "我的购买列表");
            }
        });

        mAdapter.disableLoadMoreIfNotFullPage();
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                currentPage++;
                getP().getBoughtList(currentPage);
            }
        }, mRecycler);

        mRefresh.setColorSchemeColors(getResources().getColor(R.color.base_yellow_ffd850));
        mRefresh.setRefreshing(true);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                getP().getBoughtList(currentPage);
            }
        });

        getP().getBoughtList(currentPage);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.comic_activity_history;
    }

    @Override
    public BoughtPresenter setPresenter() {
        return new BoughtPresenter();
    }

    @Override
    public void onLoadBoughtList(List<BoughtResponse.DataBeanX.BoughtModel> data) {
        if (currentPage == 1) {
            mAdapter.setNewData(data);
        }else {
            mAdapter.addData(data);
        }
        if (mRefresh.isRefreshing()) mRefresh.setRefreshing(false);
    }

    @Override
    public void onLoadBoughtListFail(NetError error) {
        mAdapter.loadMoreEnd(true);
        if (mRefresh.isRefreshing()) mRefresh.setRefreshing(false);
        hideProgress();
    }

    @Override
    public boolean useEventBus() {
        return false;
    }

    private View getEmptyView() {
        View view = LayoutInflater.from(this).inflate(R.layout.comic_empty_view, mRecycler,
                false);
        ImageView img = view.findViewById(R.id.iv_empty_img);
        img.setBackgroundResource(R.drawable.img_empty_buy);
        TextView tvDesc = (TextView) view.findViewById(R.id.tv_empty_desc);
        tvDesc.setText("哦呵~您还没有购买过任何书籍\r\n赶快去看书吧~");
        return view;
    }
}
