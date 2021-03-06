package com.jj.comics.ui.mine.history;

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
import com.jj.comics.adapter.bookshelf.CommonBookListAdapter;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.ui.detail.ComicDetailActivity;
import com.jj.comics.util.eventbus.events.UpdateReadHistoryEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

@Route(path = RouterMap.COMIC_HISTORY_ACTIVITY)
public class HistoryActivity extends BaseActivity<HistoryPresenter> implements HistoryContract.IHistoryView, View.OnClickListener {
    @BindView(R2.id.swipeRefreshLayout)
    SwipeRefreshLayout mRefresh;
    @BindView(R2.id.recyclerView)
    RecyclerView mRecycler;
    private CommonBookListAdapter mAdapter;

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mAdapter = new CommonBookListAdapter(R.layout.comic_bookshelf_item,CommonBookListAdapter.NAME_HISTORY);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.bindToRecyclerView(mRecycler);
        mAdapter.setEmptyView(getEmptyView());
        mAdapter.isUseEmpty(true);
//        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                DetailActivityHelper.toDetail(HistoryActivity.this,
//                        mAdapter.getData().get(position).getId(),
//                        "历史列表");
//            }
//        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                BookModel bookModel = mAdapter.getData().get(position);
                if (bookModel == null) return;

                if (view.getId() == R.id.content) {
                    ComicDetailActivity.toDetail(HistoryActivity.this,
                            bookModel.getId(),
                            "历史列表");
                }else if (view.getId() == R.id.right) {
                    ArrayList<BookModel> list = new ArrayList<>();
                    list.add(bookModel);
                    getP().deleteHistory(list,position);
                }
            }
        });

        mRefresh.setColorSchemeColors(getResources().getColor(R.color.base_yellow_ffd850));
        mRefresh.setRefreshing(true);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getP().getHistoryList();
            }
        });

        getP().getHistoryList();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.comic_activity_history;
    }

    @Override
    public HistoryPresenter setPresenter() {
        return new HistoryPresenter();
    }

    @Override
    public void onLoadHistoryList(List<BookModel> data) {
        mAdapter.setNewData(data);
        if (mRefresh.isRefreshing()) mRefresh.setRefreshing(false);
    }

    @Override
    public void onLoadHistoryListFail(NetError error) {
        mAdapter.setNewData(null);
        if (mRefresh.isRefreshing()) mRefresh.setRefreshing(false);
        hideProgress();
    }

    @Override
    public void onLoadRecommendList(List<BookModel> bookModelList) {

    }

    @Override
    public void getFooterDataFail(NetError error) {

    }

    @Override
    public void onDeleteComplete(int position) {
        mAdapter.remove(position);
    }

    private View getEmptyView() {
        View view = LayoutInflater.from(this).inflate(R.layout.comic_empty_view, mRecycler,
                false);
        ImageView img = view.findViewById(R.id.iv_empty_img);
        img.setBackgroundResource(R.drawable.img_empty_read);
        TextView tvDesc = (TextView) view.findViewById(R.id.tv_empty_desc);
        tvDesc.setText("哦呵~您还没有看过任何书籍\r\n赶快去看书吧~");
        return view;
    }

    /**
     * 来自阅读页面产生了历史记录刷新当前列表
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateReadHistory(UpdateReadHistoryEvent event) {
        getP().getHistoryList();
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

}
