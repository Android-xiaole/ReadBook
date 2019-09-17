package com.jj.comics.ui.mine.history;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.jj.base.net.NetError;
import com.jj.base.ui.BaseActivity;
import com.jj.base.utils.RouterMap;
import com.jj.comics.R;
import com.jj.comics.R2;
import com.jj.comics.adapter.bookshelf.BookShelfAdapter;
import com.jj.comics.data.model.BookModel;
import com.jj.comics.ui.detail.DetailActivityHelper;
import com.jj.comics.util.eventbus.EventBusManager;
import com.jj.comics.util.eventbus.events.ChangeTabBarEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

@Route(path = RouterMap.COMIC_HISTORY_ACTIVITY)
public class HistoryActivity extends BaseActivity<HistoryPresenter> implements HistoryContract.IHistoryView, View.OnClickListener {
    @BindView(R2.id.swipeRefreshLayout)
    SwipeRefreshLayout mRefresh;
    @BindView(R2.id.recyclerView)
    RecyclerView mRecycler;
    private BookShelfAdapter mAdapter;

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mAdapter = new BookShelfAdapter(R.layout.comic_bookshelf_item,null);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.bindToRecyclerView(mRecycler);
        mAdapter.setEmptyView(getEmptyView());

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
//                    getP().toRead(mAdapter.getData().get(position), mAdapter.getData().get(position).getChapterid());
                    DetailActivityHelper.toDetail(HistoryActivity.this,
                            bookModel.getId(),
                            "历史列表");
                }else if (view.getId() == R.id.right) {
                    showToastShort("删除");
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

    @Override
    public boolean useEventBus() {
        return false;
    }

    private View getEmptyView() {
        View view = LayoutInflater.from(this).inflate(R.layout.comic_empty_view_btn, mRecycler,
                false);
        ImageView img = view.findViewById(R.id.iv_empty_img);
        img.setBackgroundResource(R.drawable.img_empty_read);
        TextView tvDesc = (TextView) view.findViewById(R.id.tv_empty_desc);
        tvDesc.setText("哦呵~您还没有看过任何书籍\r\n赶快去看书吧~");
        TextView btnAction = (TextView) view.findViewById(R.id.btn_empty_action);
        btnAction.setText(R.string.comic_go_read);
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBusManager.sendChangeTabBarEvent(new ChangeTabBarEvent(1));
                finish();
            }
        });
        return view;
    }
}
